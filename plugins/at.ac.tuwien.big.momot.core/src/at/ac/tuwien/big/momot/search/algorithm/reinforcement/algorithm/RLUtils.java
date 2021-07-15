package at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.solution.variable.ITransformationVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;

public class RLUtils<S extends Solution> implements IRLUtils<S> {

   @Override
   public List<Assignment> getAssignments(final S solution) {
      final TransformationSolution ts = (TransformationSolution) solution;

      final ITransformationVariable[] trafoVariables = ts.getVariables();
      final List<Assignment> assignments = new ArrayList<>();

      for(final ITransformationVariable trafoVariable : trafoVariables) {
         assignments.add(trafoVariable.getAssignment());
      }
      return assignments;

   }

   @Override
   public List<Assignment> getRuleAssignmentsDiff(final S cur, final S next) {
      final List<ITransformationVariable> trafoVars1 = new ArrayList<>();
      final List<ITransformationVariable> trafoVars2 = new ArrayList<>();
      for(int i = 0; i < cur.getNumberOfVariables(); i++) {
         trafoVars1.add((ITransformationVariable) cur.getVariable(i));

      }

      for(int i = 0; i < next.getNumberOfVariables(); i++) {
         trafoVars2.add((ITransformationVariable) next.getVariable(i));

      }

      final List<ITransformationVariable> diffList = trafoVars2.subList(trafoVars1.size(), trafoVars2.size());
      return diffList.stream().map(v -> v.getAssignment()).collect(Collectors.toList());
   }

}
