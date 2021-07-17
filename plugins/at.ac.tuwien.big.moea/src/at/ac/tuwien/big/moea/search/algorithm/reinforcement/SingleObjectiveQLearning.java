package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EnvResponse;

import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class SingleObjectiveQLearning<S extends Solution> extends AbstractValueBasedReinforcementAlgorithm<S> {

   private final double gamma; // Eagerness - 0 looks in the near future, 1 looks in the distant future
   private double eps;
   private final double epsDecay;
   private final double epsMinimum;
   private final boolean withEpsDecay;
   private final long startTime;

   public SingleObjectiveQLearning(final double gamma, final double eps, final boolean withEpsDecay,
         final double epsDecay, final double epsMinimum, final Problem problem, final IEnvironment<S> environment,
         final String savePath, final int recordInterval, final int terminateAfterSeconds) {
      super(problem, environment, savePath, recordInterval, terminateAfterSeconds);

      this.gamma = gamma;
      this.eps = eps;
      this.epsDecay = epsDecay;
      this.epsMinimum = epsMinimum;
      this.withEpsDecay = withEpsDecay;

      startTime = System.currentTimeMillis();

      java.lang.reflect.Method evaluateFunc = null;
      try {
         evaluateFunc = this.getClass().getMethod("evaluate", Solution.class);
      } catch(NoSuchMethodException | SecurityException e) {
         e.printStackTrace();
      }

      this.environment.setEvaluationMethod(this, evaluateFunc);
   }

   @Override
   public List<Assignment> epsGreedyDecision() {

      List<Assignment> nextAction = null;

      if(rng.nextDouble() >= this.eps) {

         // Pick best transformation (max. benefit) for current state
         nextAction = getMaxRewardAction(utils.getAssignments(currentSolution));
      } else if(withEpsDecay && eps >= epsMinimum) {
         eps -= epsDecay;
      }

      return nextAction;
   }

   @Override
   protected void iterate() {
      epochSteps++;
      final List<Assignment> nextAction = epsGreedyDecision();

      final EnvResponse<S> response = environment.step(nextAction);

      final boolean isDone = response.isDone();
      final double reward = response.getReward();
      final S nextState = response.getState();

      cumReward += reward;

      updateQ(utils.getAssignments(currentSolution), utils.getRuleAssignmentsDiff(currentSolution, nextState),
            utils.getAssignments(nextState), reward);

      addSolutionIfImprovement(nextState);

      iterations++;

      if(isDone) {

         if(this.recordInterval > 0) {
            rewardEarned.add(cumReward);
            framesList.add((double) iterations);
            timePassedList.add((double) (System.currentTimeMillis() - startTime));
            meanRewardEarned.add(cumReward / epochSteps);

            if(terminateAfterSeconds > 0 && terminateAfterSeconds < (System.currentTimeMillis() - startTime) / 1000.0) {
               System.out.println("Terminated after " + terminateAfterSeconds + " seconds");
               this.terminate();
            }

            if(epochCount > 0 && epochCount % this.recordInterval == 0) {
               System.out.println("Saving rewards at epoch " + epochCount + " after "
                     + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
               saveRewards(savePath, framesList, rewardEarned, timePassedList, meanRewardEarned, epochCount);
            }
         }

         epochCount++;
         cumReward = 0;
         epochSteps = 0;
         currentSolution = environment.reset();
      } else {
         currentSolution = nextState;
      }

   }

   private void updateQ(final List<Assignment> state, final List<Assignment> action, final List<Assignment> nextState,
         final double reward) {

      final double transitionReward = getTransitionReward(state, action);

      this.addStateToTableIfNotExists(nextState);

      final double qUpdateValue = transitionReward + (reward + gamma * getMaxRewardValue(nextState) - transitionReward);

      qTable.get(state).put(action, qUpdateValue);
   }

}
