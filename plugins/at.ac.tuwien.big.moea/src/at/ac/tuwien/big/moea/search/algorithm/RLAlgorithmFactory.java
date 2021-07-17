package at.ac.tuwien.big.moea.search.algorithm;

import at.ac.tuwien.big.moea.ISearchOrchestration;
import at.ac.tuwien.big.moea.search.algorithm.provider.AbstractRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.PolicyGradient;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.SingleObjectiveExploreQLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.SingleObjectiveQLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;

import java.io.File;

import org.moeaframework.core.Solution;

public class RLAlgorithmFactory<S extends Solution> extends AbstractAlgorithmFactory<S> {

   protected S initialSolution;
   protected IEnvironment<S> environment;

   // public RLAlgorithmFactory(final ISearchOrchestration<S> searchOrchestration, final List<String>
   // episodeEndingRules,
   // final Map<String, Double> rewardMap, final INeighborhoodFunction<S> neighborhoodFunction,
   // final IFitnessComparator<?, S> fitnessComparator) {
   // setSearchOrchestration(searchOrchestration);
   // this.environment = new Environment<>(this.getInitialSolution(), neighborhoodFunction, fitnessComparator,
   // episodeEndingRules, rewardMap, searchOrchestration.getProblem().getNumberOfVariables());
   //
   // }

   public RLAlgorithmFactory(final ISearchOrchestration<S> searchOrchestration, final IEnvironment<S> environment) {

      setSearchOrchestration(searchOrchestration);
      environment.setInitialSolution(this.getInitialSolution());
      environment.setSolutionLength(searchOrchestration.getProblem().getNumberOfVariables());
      this.environment = environment;

   }

   /**
    * Policy gradient network architecture
    *
    * @param gamma
    *           .. discount factor
    * @param learningRate
    *           .. learning rate
    * @param problemType
    *           .. problem type for encodings
    * @param network
    *           .. can be given to continue training a saved model state
    * @param modelSavePath
    *           .. path to save model state every n epochs
    * @param scoreSavePath
    *           .. path to save score over time every n epochs
    * @param epochsPerModelSave
    *           .. n (number of epochs to save a model and score stats)
    * @param enableProgressServer
    *           .. should the server be enabled for training stats, i.e., gradient updates, loss, ..
    *
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<PolicyGradient<S>> createPolicyGradient(final double gamma,
         final double learningRate, final File network, final String modelSavePath, final String scoreSavePath,
         final int epochsPerModelSave, final boolean enableProgressServer, final int terminateAfterSeconds) {
      return new AbstractRegisteredAlgorithm<PolicyGradient<S>>() {
         @Override
         public PolicyGradient<S> createAlgorithm() {
            return new PolicyGradient<>(gamma, learningRate, createProblem(), getEnvironment(), network, modelSavePath,
                  scoreSavePath, epochsPerModelSave, enableProgressServer, terminateAfterSeconds);
         }
      };
   }

   /**
    * Exploring-focused Q-Learning
    *
    * @param explorationSteps
    *           .. sampling steps in exploration phase
    * @param gamma
    *           .. discount factor
    * @param eps
    *           .. epsilon / probability of entering exploration phase
    * @param withEpsDecay
    *           .. use epsilon decay
    * @param epsDecay
    *           .. epsilon decay value (subtracted from eps when entering exploration phase), if withEpsDecay is used
    * @param epsMinimum
    *           .. minimum epsilon to decay to, if withEpsDecay is used
    * @param savePath
    *           .. storage path
    * @param recordInterval
    *           .. Recording interval / number of epochs
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<SingleObjectiveExploreQLearning<S>> createSingleObjectiveExploreQLearner(
         final int explorationSteps, final double gamma, final double eps, final boolean withEpsDecay,
         final double epsDecay, final double epsMinimum, final String savePath, final int recordInterval,
         final int terminateAfterSeconds) {

      return new AbstractRegisteredAlgorithm<SingleObjectiveExploreQLearning<S>>() {
         @Override
         public SingleObjectiveExploreQLearning<S> createAlgorithm() {
            return new SingleObjectiveExploreQLearning<>(explorationSteps, gamma, eps, withEpsDecay, epsDecay,
                  epsMinimum, createProblem(), getEnvironment(), savePath, recordInterval, terminateAfterSeconds);
         }
      };
   }

   /**
    * Basic Q-Learning
    *
    * @param gamma
    *           .. discount factor
    * @param eps
    *           .. epsilon / probability of entering exploration phase
    * @param withEpsDecay
    *           .. use epsilon decay
    * @param epsDecay
    *           .. epsilon decay value (subtracted from eps when entering exploration phase), if withEpsDecay is used
    * @param epsMinimum
    *           .. minimum epsilon to decay to, if withEpsDecay is used
    * @param savePath
    *           .. storage path
    * @param recordInterval
    *           .. Recording interval / number of epochs
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>> createSingleObjectiveQLearner(final double gamma,
         final double eps, final boolean withEpsDecay, final double epsDecay, final double epsMinimum,
         final String savePath, final int recordInterval, final int terminateAfterSeconds) {

      return new AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>>() {
         @Override
         public SingleObjectiveQLearning<S> createAlgorithm() {
            return new SingleObjectiveQLearning<>(gamma, eps, withEpsDecay, epsDecay, epsMinimum, createProblem(),
                  getEnvironment(), savePath, recordInterval, terminateAfterSeconds);
         }
      };
   }

   public IEnvironment<S> getEnvironment() {
      return this.environment;
   }

   public S getInitialSolution() {
      if(initialSolution == null) {
         return getSearchOrchestration().createNewSolution(0);
      }
      return initialSolution;
   }

   public void setInitialSolution(final S initialSolution) {
      this.initialSolution = initialSolution;
   }
}
