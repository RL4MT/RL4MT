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
package at.ac.tuwien.big.momot.print;

import at.ac.tuwien.big.moea.print.SolutionWriter;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;

import java.io.Serializable;

import org.eclipse.emf.henshin.interpreter.EGraph;

public class GenericTransformationSolutionPrinter extends SolutionWriter<TransformationSolution>
      implements ITransformationSolutionPrinter {

   @Override
   protected String printAttribute(final String key, final Serializable value) {
      if(key == TransformationSolution.ATTRIBUTE_EXECUTION_RESULT) {
         return "";
      }
      return super.printAttribute(key, value);
   }

   @Override
   protected String printAttributeSummary(final TransformationSolution solution) {
      if(!solution.hasAttribute(TransformationSolution.ATTRIBUTE_EXECUTION_RESULT)) {
         return super.printAttributeSummary(solution);
      }

      // skipping execution result
      return getIndent() + "Number of attributes: " + (solution.getAttributes().size() - 1) + "\n";
   }

   @Override
   public String write(final EGraph graph) {
      return "";
   }
}
