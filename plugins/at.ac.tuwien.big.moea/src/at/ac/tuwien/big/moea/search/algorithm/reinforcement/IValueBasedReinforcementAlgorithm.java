package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Solution;

public interface IValueBasedReinforcementAlgorithm<S extends Solution> extends Algorithm {
   void addSolutionIfImprovement(Solution s);

   List<Assignment> epsGreedyDecision();

   List<Assignment> getMaxRewardAction(final List<Assignment> state);

   double getMaxRewardValue(final List<Assignment> state);

   double getTransitionReward(final List<Assignment> state, final List<Assignment> action);

   boolean isDominatedByAnySolutionInParetoFront(final Solution s);

}
