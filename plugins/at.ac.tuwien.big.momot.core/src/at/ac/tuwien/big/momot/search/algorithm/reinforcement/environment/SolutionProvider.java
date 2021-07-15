package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhood;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.AbstractSolutionProvider;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.local.neighborhood.AbstractMatchSolutionNeighborhood;
import at.ac.tuwien.big.momot.search.algorithm.local.neighborhood.AbstractTransformationSolutionStepper;
import at.ac.tuwien.big.momot.search.solution.executor.SearchHelper;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public class SolutionProvider<S extends Solution> extends AbstractSolutionProvider<TransformationSolution> {
   private static final int DEFAULT_MAX_NEIGHBORS = 50;
   private final SearchHelper searchHelper;
   private IProblemEncoder<TransformationSolution> encoder;

   public SolutionProvider(final SearchHelper searchHelper) {
      this(searchHelper, DEFAULT_MAX_NEIGHBORS);
   }

   public SolutionProvider(final SearchHelper searchHelper, final int maxNeighbors) {
      super(maxNeighbors);
      this.searchHelper = searchHelper;
   }

   public SolutionProvider(final SearchHelper searchHelper, final int maxNeighbors,
         final IProblemEncoder<TransformationSolution> encoder) {
      super(maxNeighbors);
      this.searchHelper = searchHelper;
      this.encoder = encoder;
   }

   @Override
   public Object[] generateExtendedSolution(final IProblemEncoder<TransformationSolution> encoder,
         final TransformationSolution solution, final INDArray distribution) {
      final TransformationSolution nonEmptySolution = TransformationSolution.removePlaceholders(solution);
      return getSearchHelper().appendParticularVariable(encoder, nonEmptySolution, distribution);
   }

   @Override
   public TransformationSolution generateExtendedSolution(final TransformationSolution solution,
         final List<Assignment> assignments) {
      return getSearchHelper().appendVariables(TransformationSolution.removePlaceholders(solution), assignments);
   }

   @Override
   public INeighborhood<TransformationSolution> generateNeighbors(final TransformationSolution solution,
         final int maxNeighbors) {
      final TransformationSolution nonEmptySolution = TransformationSolution.removePlaceholders(solution);
      return new AbstractMatchSolutionNeighborhood(nonEmptySolution, maxNeighbors, encoder) {

         @Override
         public Iterator<TransformationSolution> iterator() {
            return new AbstractTransformationSolutionStepper(getBaseSolution(), getMaxNeighbors()) {
               private TransformationSolution extendSolution(final TransformationSolution baseSolution) {
                  return getSearchHelper().appendRandomVariables(baseSolution, 1);

               }

               private TransformationSolution extendSolutionWithEncoder(
                     final IProblemEncoder<TransformationSolution> encoder, final TransformationSolution baseSolution) {
                  return getSearchHelper().appendRandomVariablesWithStrategy(encoder, baseSolution, 1);

               }

               @Override
               protected TransformationSolution getNext() {
                  if(encoder != null) {
                     return TransformationSolution
                           .removePlaceholders(extendSolutionWithEncoder(encoder, getBaseSolution()));
                  }
                  return TransformationSolution.removePlaceholders(extendSolution(getBaseSolution()));
               }
            };
         }
      };
   }

   public SearchHelper getSearchHelper() {
      return searchHelper;
   }

}
