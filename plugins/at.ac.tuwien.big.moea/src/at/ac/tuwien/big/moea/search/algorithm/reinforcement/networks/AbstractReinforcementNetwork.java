package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractReinforcementNetwork<S extends Solution> {

   protected final double gamma;
   protected final double learningRate;
   protected final int totalActions;

   protected int nrOfEpochs = 0;
   protected final int stateSpace;

   int framecount;
   protected final ArrayList<Double> rewardEarned;
   protected final ArrayList<Double> framesList;
   protected final ArrayList<Double> timePassedList;
   protected final ArrayList<Double> meanRewardEarned;

   protected IEnvironment<S> environment;

   protected IProblemEncoder<S> encoder;

   int[] actionSpace;

   protected final int maxSolutionLength;

   protected final boolean enableProgressServer;
   protected final String scoreSavePath;

   public AbstractReinforcementNetwork(final double gamma, final double learningRate, final Problem problem,
         final IEnvironment<S> environment, final String scoreSavePath, final boolean enableProgressServer) {

      this.learningRate = learningRate;
      this.gamma = gamma;

      this.encoder = environment.getProblemEncoder();

      this.environment = environment;
      this.rewardEarned = new ArrayList<>();
      this.framesList = new ArrayList<>();
      this.timePassedList = new ArrayList<>();
      this.meanRewardEarned = new ArrayList<>();

      this.scoreSavePath = scoreSavePath;
      this.enableProgressServer = enableProgressServer;
      this.framecount = 0;

      this.maxSolutionLength = problem.getNumberOfVariables();

      this.totalActions = this.encoder.getActionSpace(environment.getInitialSolution());
      this.stateSpace = this.encoder.getStateSpace(environment.getInitialSolution());

      this.actionSpace = new int[totalActions];
      for(int i = 0; i < actionSpace.length; i++) {
         actionSpace[i] = i;
      }
   }

   public void enableServerForTrainingVisualization(final ComputationGraph nn) {
      // Initialize the user interface backend
      System.out.println("Start UI Server ...");
      final UIServer uiServer = UIServer.getInstance();

      // Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
      final StatsStorage statsStorage = new InMemoryStatsStorage(); // Alternative: new FileStatsStorage(File), for
                                                                    // saving and loading later

      uiServer.attach(statsStorage);

      nn.setListeners(new StatsListener(statsStorage));

      System.out.println("Training progress board active!");
   }

   public void saveRewards(final List<Double> framesList, final List<Double> rewardList,
         final List<Double> meanRewardList, final List<Double> timePassedList, final long ts) {
      final ArrayList<ArrayList<Double>> lll = new ArrayList<>();
      lll.add((ArrayList<Double>) framesList);
      lll.add((ArrayList<Double>) rewardList);
      lll.add((ArrayList<Double>) meanRewardList);
      lll.add((ArrayList<Double>) timePassedList);

      FileManager.saveBenchMark("evaluations;reward;averageReward;runtime in ms;", lll,
            scoreSavePath + "_" + ts + ".csv");
   }
}
