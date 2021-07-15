package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface INetworkIOModelEncoder<S extends Solution> {

   INDArray encodeSolution(final ProblemType problemType, final Solution s);

   int getActionSpace(final ProblemType problemType, final Solution initialSolution);

   int getStateSpace(final ProblemType problemType, final Solution initialSolution);
}
