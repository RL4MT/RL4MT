package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.neighborhood.AbstractNeighborhoodFunction;

import org.moeaframework.core.Solution;

public abstract class AbstractSolutionProvider<S extends Solution> extends AbstractNeighborhoodFunction<S>
      implements ISolutionExtender<S> {

   public AbstractSolutionProvider() {
      super();
   }

   public AbstractSolutionProvider(final int maxNeighbors) {
      super(maxNeighbors);
   }

}
