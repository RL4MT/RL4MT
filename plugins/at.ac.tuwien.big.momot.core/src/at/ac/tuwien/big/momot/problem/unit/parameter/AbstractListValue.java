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
package at.ac.tuwien.big.momot.problem.unit.parameter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListValue<T extends Object> implements IParameterValue<T> {

   protected static <T extends Object> List<T> generateList(final IParameterValue<T> value, final int nrValues) {
      final List<T> values = new ArrayList<>();
      for(int i = 0; i < nrValues; i++) {
         values.add(value.nextValue());
      }
      return values;
   }

   private List<T> values;

   private T initialValue = null;

   public AbstractListValue(final IParameterValue<T> value, final int nrValues) {
      this(generateList(value, nrValues));
   }

   public AbstractListValue(final List<T> values) {
      if(values.size() == 0) {
         throw new IllegalArgumentException("At least one list value must be given.");
      }
      this.values = values;
   }

   @Override
   public T getInitialValue() {
      return initialValue;
   }

   protected List<T> getValues() {
      return values;
   }

   protected abstract int nextIndex();

   @Override
   public T nextValue() {
      if(values.isEmpty()) {
         return null;
      }
      final T value = values.get(nextIndex());
      if(initialValue == null) {
         initialValue = value;
      }
      return value;
   }
}
