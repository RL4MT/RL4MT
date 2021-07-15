/*******************************************************************************
 * Copyright (c) 2015 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Fleck (Vienna University of Technology) - initial API and implementation
 *
 * Initially developed in the context of ARTIST EU project www.artist-project.eu
 *******************************************************************************/
package at.ac.tuwien.big.moea.search.algorithm.local;

import java.util.ArrayList;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class RandomDescent<S extends Solution> extends AbstractLocalSearchAlgorithm<S> {

   public RandomDescent(final Problem problem, final S initialSolution,
         final INeighborhoodFunction<S> neighborhoodFunction, final IFitnessComparator<?, S> fitnessComparator) {
      super(problem, initialSolution, neighborhoodFunction, fitnessComparator);
      if(neighborhoodFunction.getMaxNeighbors() == INeighborhoodFunction.UNLIMITED) {
         System.err.println(
               "Warning: Neighborhood-Function may produce infinite neighbors, Random-Descent may get stuck in infinite loop.");
      }
   }

   @Override
   protected void iterate() {
      final ArrayList<S> neighbors = new ArrayList<>();
      for(final S neighbor : generateCurrentNeighbors()) {
         evaluate(neighbor);
         neighbors.add(neighbor);
         if(update(neighbor)) {
            break;
         }
      }
      System.out.println(-getCurrentSolution().getObjective(0));
      if(neighbors.isEmpty()) {
         terminate();
         return;
      }
   }
}
