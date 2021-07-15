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
package at.ac.tuwien.big.moea.experiment.instrumenter.collector;

import at.ac.tuwien.big.moea.search.algorithm.local.LocalSearchAlgorithm;
import at.ac.tuwien.big.moea.util.AccumulatorUtil;

import java.io.Serializable;

import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.AttachPoint;
import org.moeaframework.analysis.collector.Collector;

public class LocalBestFitnessCollector implements Collector {

   private final LocalSearchAlgorithm<?> algorithm;

   public LocalBestFitnessCollector() {
      this(null);
   }

   public LocalBestFitnessCollector(final LocalSearchAlgorithm<?> algorithm) {
      this.algorithm = algorithm;
   }

   @Override
   public Collector attach(final Object object) {
      return new LocalBestFitnessCollector((LocalSearchAlgorithm<?>) object);
   }

   @Override
   public void collect(final Accumulator accumulator) {
      final Serializable bestFitness = (Serializable) algorithm.getBestFitness();
      accumulator.add(AccumulatorUtil.Keys.LOCAL_BEST_FITNESS, bestFitness);
   }

   @Override
   public AttachPoint getAttachPoint() {
      return AttachPoint.isSubclass(LocalSearchAlgorithm.class)
            .and(AttachPoint.not(AttachPoint.isNestedIn(LocalSearchAlgorithm.class)));
   }

}
