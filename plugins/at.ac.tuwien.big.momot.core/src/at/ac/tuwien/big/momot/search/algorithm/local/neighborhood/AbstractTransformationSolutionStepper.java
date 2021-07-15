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
package at.ac.tuwien.big.momot.search.algorithm.local.neighborhood;

import at.ac.tuwien.big.moea.search.algorithm.local.neighborhood.AbstractSolutionStepper;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;

public abstract class AbstractTransformationSolutionStepper extends AbstractSolutionStepper<TransformationSolution> {

   public AbstractTransformationSolutionStepper(final TransformationSolution baseSolution) {
      super(baseSolution);
   }

   public AbstractTransformationSolutionStepper(final TransformationSolution baseSolution, final int maxSolutions) {
      super(baseSolution, maxSolutions);
   }
}
