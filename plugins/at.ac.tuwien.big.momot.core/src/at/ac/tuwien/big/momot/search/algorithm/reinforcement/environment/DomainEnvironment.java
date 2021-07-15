package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.AbstractSolutionProvider;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;
import at.ac.tuwien.big.momot.problem.solution.variable.ITransformationVariable;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.nd4j.linalg.api.ndarray.INDArray;

@SuppressWarnings("restriction")
public class DomainEnvironment<S extends Solution> extends AbstractEnvironment<S> {

   private final List<String> episodeEndingRules;
   private final Map<String, Double> rewardMap;
   private final IProblemEncoder<S> encoder;

   public DomainEnvironment(final AbstractSolutionProvider<S> solutionProvider,
         final IFitnessComparator<?, S> fitnessComparator, final IProblemEncoder<S> encoder) {
      super(solutionProvider, fitnessComparator);

      this.encoder = encoder;
      this.rewardMap = encoder.getRewardMap();
      this.episodeEndingRules = encoder.getEpisodeEndingRules();
   }

   @Override
   public boolean determineIsEpisodeDone(final S nextState) {
      for(int i = 0; i < nextState.getNumberOfVariables(); i++) {
         final Variable var = nextState.getVariable(i);
         if(episodeEndingRules.contains(((ITransformationVariable) var).getUnit().getName())) {
            return true;
         }
      }

      if(nextState.getNumberOfVariables() >= this.maxSolutionLength) {
         return true;
      }

      return false;
   }

   private double determineReward(final List<Assignment> ruleAssignments) {
      double reward = 0;
      for(final Assignment actionRule : ruleAssignments) {
         final String ruleName = actionRule.getUnit().getName();
         if(rewardMap.containsKey(ruleName)) {
            reward += rewardMap.get(ruleName);
         }
      }

      return reward;
   }

   @Override
   public IProblemEncoder<S> getProblemEncoder() {
      return this.encoder;
   }

   @Override
   public EnvResponse<S> sampledStep(final S solution, final INDArray actionProbs) {
      final EnvResponse<S> response = new EnvResponse<>();

      /*
       * final TransformationSolution nonEmptySolution = TransformationSolution
       * .removePlaceholders((TransformationSolution) solution);
       */
      final Object[] actionSolution = this.solutionProvider.generateExtendedSolution(this.encoder, solution,
            actionProbs);
      final int action = (int) actionSolution[0];
      final S nextState = (S) actionSolution[1];

      response.setState(nextState);
      response.setAppliedActionId(action);

      final double reward = determineReward(utils.getRuleAssignmentsDiff(currentState, nextState));

      response.setReward(reward);

      response.setDone(determineIsEpisodeDone(nextState));

      currentState = nextState;

      return response;
   }

   @Override
   public EnvResponse<S> step(final List<Assignment> action) {
      final EnvResponse<S> response = new EnvResponse<>();

      S nextState = null;

      if(action == null) {
         for(final S solution : this.solutionProvider.generateNeighbors(this.currentState)) {
            nextState = solution;
            break;
         }
      } else {
         nextState = this.solutionProvider.generateExtendedSolution(this.currentState, action);
      }

      evaluteSolution(nextState);

      final double reward = determineReward(utils.getRuleAssignmentsDiff(currentState, nextState));

      final double stateBasedReward = encoder.determineAdditionalReward(nextState);

      response.setReward(reward + stateBasedReward);
      response.setDone(determineIsEpisodeDone(nextState));
      response.setState(nextState);

      currentState = nextState;

      return response;
   }

   @Override
   public EnvResponse<S> stepSampling(final List<Assignment> nextAction, final int explorationSteps) {
      // TODO Auto-generated method stub
      return null;
   }

}
