package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.problem.solution.variable.IPlaceholderVariable;
import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.AbstractSolutionProvider;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Environment<S extends Solution> extends AbstractEnvironment<S> {

   public Environment(final AbstractSolutionProvider<S> solutionProvider,
         final IFitnessComparator<?, S> fitnessComparator) {
      super(solutionProvider, fitnessComparator);
   }

   @Override
   public boolean determineIsEpisodeDone(final S nextState) {

      if(nextState.getNumberOfVariables() >= this.maxSolutionLength) {
         return true;
      }
      return false;
   }

   private double determineReward(final S state) {

      return (Double) fitnessComparator.getValue(state) * -1;

   }

   @Override
   public IProblemEncoder<S> getProblemEncoder() {
      return null;
   }

   @Override
   public EnvResponse<S> sampledStep(final S solution, final INDArray actionProbs) {
      return null;
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

      final double reward = determineReward(nextState);

      response.setReward(reward);
      response.setDone(determineIsEpisodeDone(nextState));
      response.setState(nextState);

      currentState = nextState;

      return response;
   }

   @Override
   public EnvResponse<S> stepSampling(final List<Assignment> action, final int explorationSteps) {
      final EnvResponse<S> response = new EnvResponse<>();

      S nextState = null;
      double reward = 0;

      if(action == null) {
         double maxReward = Double.NEGATIVE_INFINITY;
         final List<S> solutions = new ArrayList<>();
         for(final S solution : this.solutionProvider.generateNeighbors(currentState, explorationSteps)) {
            solutions.add(solution);
         }

         for(final S solution : solutions) {
            evaluteSolution(solution);

            if(solution.getNumberOfVariables() > 0
                  && solution.getVariable(solution.getNumberOfVariables() - 1) instanceof IPlaceholderVariable) {
               continue;
            }

            final double nextStateReward = determineReward(solution);

            if(nextStateReward > maxReward) {
               maxReward = nextStateReward;
               nextState = solution;
            }
         }

         if(maxReward > Double.NEGATIVE_INFINITY) {
            reward = maxReward;
         }

      } else {
         nextState = this.solutionProvider.generateExtendedSolution(this.currentState, action);
         evaluteSolution(nextState);

         reward = determineReward(nextState);

      }

      response.setReward(reward);
      response.setDone(determineIsEpisodeDone(nextState));
      response.setState(nextState);

      currentState = nextState;

      return response;
   }

}
