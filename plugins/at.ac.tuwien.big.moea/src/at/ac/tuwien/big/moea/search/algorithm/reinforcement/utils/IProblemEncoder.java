package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import java.util.List;
import java.util.Map;

import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface IProblemEncoder<S extends Solution> {
   List<UnitParameter> createBaseRules();

   List<UnitParameter> createPostBaseRules();

   double determineAdditionalReward(S s);

   INDArray encodeSolution(final S s);

   int getActionSpace(final S initialSolution);

   List<String> getEpisodeEndingRules();

   FixedRuleApplicationStrategy getFixedUnitApplicationStrategy(final S s, final int action);

   Map<String, Double> getRewardMap();

   int getStateSpace(final S initialSolution);

   int[] integerToOnehot(final int integer, final int bits);
}
