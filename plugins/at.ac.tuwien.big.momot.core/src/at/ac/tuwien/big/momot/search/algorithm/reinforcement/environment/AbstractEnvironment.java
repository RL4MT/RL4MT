package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.AbstractSolutionProvider;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm.RLUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.moeaframework.core.Solution;

public abstract class AbstractEnvironment<S extends Solution> implements IEnvironment<S> {

   protected S initialState = null;
   protected S currentState = null;
   protected final AbstractSolutionProvider<S> solutionProvider;
   protected final IFitnessComparator<?, S> fitnessComparator;
   protected int maxSolutionLength;

   protected final IRLUtils<S> utils;

   private Object agentClass = null;
   private Method evaluateFunction = null;

   public AbstractEnvironment(final AbstractSolutionProvider<S> solutionProvider,
         final IFitnessComparator<?, S> fitnessComparator) {
      this.solutionProvider = solutionProvider;
      this.fitnessComparator = fitnessComparator;
      this.utils = new RLUtils<>();
   }

   @Override
   public void evaluteSolution(final S state) {
      try {
         evaluateFunction.invoke(agentClass, state);
      } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
         e.printStackTrace();
      }
   }

   @Override
   public S getInitialSolution() {
      return this.initialState;
   }

   @Override
   public IRLUtils<S> getRLUtils() {
      return this.utils;
   }

   @Override
   public S reset() {
      currentState = initialState;
      return initialState;
   }

   @Override
   public void setEvaluationMethod(final Object agentInstance, final Method evaluateFunction) {
      this.agentClass = agentInstance;
      this.evaluateFunction = evaluateFunction;
   }

   @Override
   public void setInitialSolution(final S solution) {
      this.initialState = solution;
   }

   @Override
   public void setSolutionLength(final int length) {
      this.maxSolutionLength = length;
   }
}
