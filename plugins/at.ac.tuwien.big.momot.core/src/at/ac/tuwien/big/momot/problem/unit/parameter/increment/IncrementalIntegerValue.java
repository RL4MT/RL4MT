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
package at.ac.tuwien.big.momot.problem.unit.parameter.increment;

import at.ac.tuwien.big.momot.problem.unit.parameter.IParameterValue;

public class IncrementalIntegerValue implements IParameterValue<Integer> {

   private final int initialValue;
   private int curValue;
   private final int step;

   public IncrementalIntegerValue() {
      this(0, 1);
   }

   public IncrementalIntegerValue(final int initialValue) {
      this(initialValue, 1);
   }

   public IncrementalIntegerValue(final int initialValue, final int step) {
      this.initialValue = initialValue;
      this.curValue = initialValue;
      this.step = step;
   }

   public int getCurrentValue() {
      return curValue;
   }

   @Override
   public Integer getInitialValue() {
      return initialValue;
   }

   public int getStartValue() {
      return initialValue;
   }

   public int getStep() {
      return step;
   }

   @Override
   public Integer nextValue() {
      final int returnValue = curValue;
      curValue += step;
      return returnValue;
   }

}
