package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;

import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface ISolutionExtender<S extends Solution> {

   Object[] generateExtendedSolution(final IProblemEncoder<S> encoder, final S solution, final INDArray distribution);

   S generateExtendedSolution(S currentState, List<Assignment> action);

}
