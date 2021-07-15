package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EnvResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

public class PolicyGradientNetwork<S extends Solution> extends AbstractReinforcementNetwork<S> {

   private ComputationGraph nn = null;
   private final String modelSavePath;
   private final int epochsPerSave;
   private final long startTime;

   public PolicyGradientNetwork(final double gamma, final double lr, final Problem problem,
         final IEnvironment<S> environment, final File network, final String modelSavePath, final String scoreSavePath,
         final int epochsPerModelSave, final boolean enableProgressServer) throws IOException {
      super(gamma, lr, problem, environment, scoreSavePath, enableProgressServer);

      this.modelSavePath = modelSavePath;
      this.epochsPerSave = epochsPerModelSave;
      this.startTime = System.currentTimeMillis();

      if(network != null) {
         System.out.println("Loading computation graph for retraining...");
         this.nn = ComputationGraph.load(network, true);
      } else {
         this.nn = getVPGNetwork(stateSpace, totalActions);
      }

      if(this.enableProgressServer) {
         enableServerForTrainingVisualization(nn);
      }
   }

   public INDArray calcValueOfState(final ArrayList<INDArray> reward) {
      final ArrayList<INDArray> calcd = new ArrayList<>();
      final INDArray out = Nd4j.zeros(reward.size(), 1);

      calcd.add(reward.get(reward.size() - 1));

      for(int i = reward.size() - 2; i >= 0; i--) {

         calcd.add(0, reward.get(i).add(calcd.get(0).mul(gamma)));

      }

      for(int i = 0; i < calcd.size(); i++) {
         out.put(i, calcd.get(i));
      }
      out.subi(Nd4j.mean(out));

      if(Nd4j.std(out).getDouble(0) != 0.0) {
         out.divi(Nd4j.std(out));
      }

      return out;
   }

   public void fit(final ComputationGraph graph, final ArrayList<INDArray> state, final ArrayList<Integer> action,
         final ArrayList<INDArray> reward) {

      INDArray states = state.get(0);

      final INDArray advantage = calcValueOfState(reward);

      final INDArray actions = Nd4j.zeros(action.size(), totalActions);
      for(int i = 0; i < action.size(); i++) {
         actions.putScalar(i, action.get(i), 1);
         if(i != 0) {
            states = Nd4j.concat(0, states, state.get(i));
         }
      }

      final DataSet ds = new DataSet();

      final INDArray temp = actions.mul(advantage);

      ds.setLabels(temp);
      ds.setFeatures(states);

      graph.fit(ds);

   }

   public ComputationGraph getVPGNetwork(final int discObserv, final int actions) {

      final ComputationGraphConfiguration.GraphBuilder confBuild = new NeuralNetConfiguration.Builder().seed(123)
            .updater(new Adam(this.learningRate)).weightInit(WeightInit.NORMAL).graphBuilder().addInputs("disc_in");

      confBuild.addLayer("policy_1", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(1024).build(),
            "disc_in");
      confBuild.addLayer("policy_2", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(512).build(),
            "policy_1");

      confBuild.addLayer("action",
            new OutputLayer.Builder(new CustomLoss()).nOut(actions).activation(Activation.SOFTMAX).build(), "policy_2");

      confBuild.setOutputs("action").setInputTypes(InputType.feedForward(discObserv)).build();

      final ComputationGraph net = new ComputationGraph(confBuild.build());
      net.init();

      return net;

   }

   public void saveModel(final ComputationGraph nn, final long ts, final String savePath) {
      try {
         nn.save(new File(savePath + "_params_" + ts + ".mod"));
      } catch(final IOException e) {
         e.printStackTrace();
      }

   }

   public List<S> trainEpoch() {

      int cumReward = 0;
      int epochSteps = 1;
      final boolean done = false;
      final List<S> returnSolutions = new ArrayList<>();

      INDArray dist;
      INDArray oldObs = this.encoder.encodeSolution(environment.reset());
      S oldSolution = environment.getInitialSolution();

      final ArrayList<INDArray> stateLs = new ArrayList<>();
      final ArrayList<Integer> actionLs = new ArrayList<>();
      final ArrayList<INDArray> rewardLs = new ArrayList<>();

      System.out.println("Training epoch " + nrOfEpochs++ + "..");
      while(!done) {
         dist = nn.outputSingle(oldObs);

         final EnvResponse<S> response = environment.sampledStep(oldSolution, dist);

         final int action = response.getAppliedActionId();
         final S newSolution = response.getState();

         returnSolutions.add(newSolution);

         stateLs.add(oldObs);
         actionLs.add(action);

         final double[] curReward = new double[1];
         curReward[0] = response.getReward();

         rewardLs.add(Nd4j.create(curReward));

         cumReward += curReward[0];

         framecount++;

         if(response.isDone()) {

            environment.reset();
            // System.out.println("Cumulative reward: " + cumReward);
            break;
         }

         oldObs = this.encoder.encodeSolution(newSolution);
         oldSolution = newSolution;
         epochSteps++;
      }

      fit(nn, stateLs, actionLs, rewardLs);

      rewardEarned.add((double) cumReward);
      framesList.add((double) framecount);
      timePassedList.add((double) (System.currentTimeMillis() - startTime));
      meanRewardEarned.add((double) (cumReward / epochSteps));

      if(this.modelSavePath != null && epochsPerSave > 0 && nrOfEpochs % epochsPerSave == 0) {
         saveModel(nn, nrOfEpochs, modelSavePath);
      }

      if(this.scoreSavePath != null && epochsPerSave > 0 && nrOfEpochs % epochsPerSave == 0) {
         System.out.println("Saving rewards at epoch " + nrOfEpochs + " after "
               + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
         saveRewards(framesList, rewardEarned, meanRewardEarned, timePassedList, nrOfEpochs);
      }

      return returnSolutions;
   }

}
