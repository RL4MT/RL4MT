package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface IEnvironment<S extends Solution> {
   boolean determineIsEpisodeDone(final S nextState);

   void evaluteSolution(final S state);

   S getInitialSolution();

   IProblemEncoder<S> getProblemEncoder();

   IRLUtils<S> getRLUtils();

   S reset();

   EnvResponse<S> sampledStep(final S solution, final INDArray actionProbs);

   void setEvaluationMethod(final Object agentInstance, final Method evaluateFunction);

   void setInitialSolution(S s);

   void setSolutionLength(int length);

   EnvResponse<S> step(List<Assignment> nextAction);

   EnvResponse<S> stepSampling(List<Assignment> nextAction, int explorationSteps);
}
