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
package at.ac.tuwien.big.momot.problem.unit.parameter.fix;

import org.eclipse.emf.henshin.model.Parameter;

public class NullValue extends FixValue<Object> {

   public NullValue() {
      this(null);
   }

   public NullValue(final Parameter parameter) {
      super(parameter, null);
   }

}
