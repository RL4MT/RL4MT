package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.PolicyGradientNetwork;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class PolicyGradient<S extends Solution> extends AbstractAlgorithm {

   protected NondominatedPopulation population;
   private PolicyGradientNetwork<S> policyGradientNetwork = null;
   int iterations = 0;

   public PolicyGradient(final double gamma, final double learningRate, final Problem problem,
         final IEnvironment<S> environment, final File network, final String modelSavePath, final String scoreSavePath,
         final int epochsPerModelSave, final boolean enableProgressServer, final int terminateAfterSeconds) {
      super(problem);

      this.population = new NondominatedPopulation();

      try {
         policyGradientNetwork = new PolicyGradientNetwork<>(gamma, learningRate, problem, environment, network,
               modelSavePath, scoreSavePath, epochsPerModelSave, enableProgressServer, terminateAfterSeconds);
      } catch(final IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   public NondominatedPopulation getResult() {
      return this.population;
   }

   @Override
   protected void iterate() {
      final boolean terminateDueTime = policyGradientNetwork.trainEpoch();

      if(terminateDueTime) {
         this.terminate();
      }
      // for(final S s : epochSolutions) {
      // evaluate(s);
      // population.add(s);
      //
      // }

   }
}
