package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.indicator.Hypervolume;

public abstract class AbstractValueBasedReinforcementAlgorithm<S extends Solution> extends AbstractAlgorithm
      implements IValueBasedReinforcementAlgorithm<S> {

   protected IEnvironment<S> environment;

   protected final String savePath;
   protected Map<List<Assignment>, Map<List<Assignment>, Double>> qTable;

   protected S currentSolution;
   protected final ArrayList<Double> framesList;
   protected final ArrayList<Double> rewardEarned;
   protected final ArrayList<Double> timePassedList;
   protected final ArrayList<Double> meanRewardEarned;

   protected Hypervolume hv = null;
   final long ts = Instant.now().getEpochSecond();

   protected int iterations = 0;
   protected int epochCount = 0;
   protected final IRLUtils<S> utils;
   protected int recordInterval;
   protected int epochSteps;

   FileOutputStream fOut;
   ObjectOutputStream oos;
   long startTS;

   protected int stopOnChangesWithoutImprovement;

   protected double cumReward = 0;
   protected Random rng = null;

   protected Hypervolume hypervolume;
   protected NondominatedPopulation population;

   public AbstractValueBasedReinforcementAlgorithm(final Problem problem, final IEnvironment<S> environment,
         final String savePath, final int recordInterval) {
      super(problem);

      this.rng = new Random();

      this.environment = environment;
      this.savePath = savePath;
      this.qTable = new HashMap<>();
      this.recordInterval = recordInterval;

      this.currentSolution = environment.reset();
      this.utils = environment.getRLUtils();
      this.epochSteps = 0;

      evaluate(this.currentSolution);

      this.population = new NondominatedPopulation();
      this.startTS = Instant.now().getEpochSecond();

      new File(savePath + "/" + this.startTS).mkdir();

      this.framesList = new ArrayList<>();
      this.rewardEarned = new ArrayList<>();
      this.timePassedList = new ArrayList<>();
      this.meanRewardEarned = new ArrayList<>();

      this.population.add(this.currentSolution);

      this.addStateToTableIfNotExists(new ArrayList<>());
   }

   @Override
   public void addSolutionIfImprovement(final Solution s) {
      if(!isDominatedByAnySolutionInParetoFront(s)) {
         this.population.add(s);
      }
   }

   public void addStateToTableIfNotExists(final List<Assignment> state) {
      if(!qTable.containsKey(state)) {
         this.qTable.put(state, new HashMap<>());
      }

   }

   @Override
   public abstract List<Assignment> epsGreedyDecision();

   @Override
   public List<Assignment> getMaxRewardAction(final List<Assignment> state) {

      if(!qTable.containsKey(state)) {
         return null;
      }

      List<Assignment> action = null;
      double maxReward = Double.NEGATIVE_INFINITY;
      final Map<List<Assignment>, Double> actionTable = qTable.get(state);

      for(final List<Assignment> choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction) > maxReward) {
            action = choosableAction;
            maxReward = actionTable.get(choosableAction);
         }
      }
      return action;
   }

   @Override
   public double getMaxRewardValue(final List<Assignment> state) {

      if(qTable.get(state).isEmpty()) {
         return 0;
      }

      double maxReward = Double.NEGATIVE_INFINITY;

      final Map<List<Assignment>, Double> actionTable = qTable.get(state);
      for(final List<Assignment> choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction) > maxReward) {
            maxReward = actionTable.get(choosableAction);
         }
      }

      return maxReward;
   }

   @Override
   public NondominatedPopulation getResult() {
      return this.population;
   }

   @Override
   public double getTransitionReward(final List<Assignment> state, final List<Assignment> action) {

      if(!qTable.get(state).containsKey(action)) {
         return 0;
      }

      return qTable.get(state).get(action);
   }

   public boolean isDominatedBy(final Solution s1, final Solution s2) {
      if(s1.getObjective(0) < s2.getObjective(0)) {
         return false;
      }
      return true;
   }

   @Override
   public boolean isDominatedByAnySolutionInParetoFront(final Solution s) {
      final DominanceComparator comparator = this.population.getComparator();
      for(int i = 0; i < this.population.size(); i++) {
         // if solution in pareto front dominates solution s, return true
         if(comparator.compare(s, this.population.get(i)) > 0) {
            return true;
         }
      }
      return false;
   }

   public void saveRewards(final String scoreSavePath, final List<Double> framesList, final List<Double> rewardList,
         final List<Double> secondsPassed, final List<Double> meanReward, final long ts) {
      final ArrayList<ArrayList<Double>> lll = new ArrayList<>();
      lll.add((ArrayList<Double>) framesList);
      lll.add((ArrayList<Double>) rewardList);
      lll.add((ArrayList<Double>) meanReward);
      lll.add((ArrayList<Double>) secondsPassed);
      FileManager.saveBenchMark("evaluations;reward;averageReward;runtime in ms;", lll, scoreSavePath + ".csv");
   }

}
