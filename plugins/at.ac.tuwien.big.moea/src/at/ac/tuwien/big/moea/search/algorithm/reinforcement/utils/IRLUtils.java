package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;

public interface IRLUtils<S extends Solution> {

   List<Assignment> getAssignments(final S solution);

   List<Assignment> getRuleAssignmentsDiff(final S cur, final S next);

}
