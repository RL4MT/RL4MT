package at.ac.tuwien.big.momot.examples.stack;

import at.ac.tuwien.big.moea.SearchAnalysis;
import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.SearchResultManager;
import at.ac.tuwien.big.moea.experiment.analyzer.SearchAnalyzer;
import at.ac.tuwien.big.moea.experiment.executor.SearchExecutor;
import at.ac.tuwien.big.moea.experiment.executor.listener.CurrentNondominatedPopulationPrintListener;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.RLAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.provider.IRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.fitness.comparator.ObjectiveFitnessComparator;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.examples.stack.stack.StackPackage;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationParameterMutation;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationPlaceholderMutation;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment.Environment;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment.SolutionProvider;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

public class StackSearch {

   private static final int SOLUTION_LENGTH = 8;
   private static final String INPUT_MODEL = "model_five_stacks.xmi";

   protected static final boolean PRINT_POPULATIONS = true;
   protected static final String PRINT_DIRECTORY = "output/populations/five_stacks";

   private static final int NR_RUNS = 3;
   private static final int POPULATION_SIZE = 100;
   private static final int MAX_EVALUATIONS = 5000;

   protected static String baseName;

   protected static double significanceLevel = 0.01;

   protected static TransformationResultManager handleResults(
         final SearchExperiment<TransformationSolution> experiment) {

      final TransformationResultManager resultManager = new TransformationResultManager(experiment);

      System.out.println("REFERENCE SET:");
      System.out.println(SearchResultManager.printObjectives(SearchResultManager.getReferenceSet(experiment, null)));
      System.out.println(SearchResultManager.printObjectives(resultManager.createApproximationSet()));

      Population population;
      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save objectives of all algorithms to 'output/objectives/objective_values.txt'");
      SearchResultManager.saveObjectives("output/objectives/objective_values.txt", population);

      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save models of all algorithms to 'output/models/'");
      saveModels(TransformationResultManager.saveModels("output/models/", INPUT_MODEL, population));

      System.out.println("- Save objectives of algorithms seperately to 'output/objectives/<algorithm>.txt'");
      System.out.println("- Save models of algorithms seperately to 'output/solutions/<algorithm>.txt'´\n");

      for(final Entry<SearchExecutor, List<NondominatedPopulation>> entry : resultManager.getResults().entrySet()) {

         System.out.println(entry.getKey().getName());

         population = SearchResultManager.createApproximationSet(experiment, entry.getKey().getName());
         System.out.println(SearchResultManager.printObjectives(population) + "\n");
         saveModels(TransformationResultManager.saveModels("output/models/" + entry.getKey().getName(),
               entry.getKey().getName(), population));

         population = SearchResultManager.createApproximationSet(experiment, entry.getKey().getName());
         SearchResultManager.saveObjectives("output/objectives/" + entry.getKey().getName() + ".txt", population);
      }

      return resultManager;
   }

   public static void main(final String[] args) throws IOException {
      StackPackage.eINSTANCE.eClass();

      final StackOrchestration search = new StackOrchestration(INPUT_MODEL, SOLUTION_LENGTH);

      final IEnvironment<TransformationSolution> env = new Environment<>(
            new SolutionProvider<>(search.getSearchHelper()), new ObjectiveFitnessComparator<TransformationSolution>(
                  search.getFitnessFunction().getObjectiveIndex("Standard Deviation")));

      final RLAlgorithmFactory<TransformationSolution> rl = search.createRLAlgorithmFactory(env);

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = search
            .createEvolutionaryAlgorithmFactory(POPULATION_SIZE);

      // algorithm settings

      search.addAlgorithm("NSGAII",
            moea.createNSGAII(new TournamentSelection(2), new OnePointCrossover(1.0),
                  new TransformationParameterMutation(0.1, search.getModuleManager()),
                  new TransformationPlaceholderMutation(0.2)));

      search.addAlgorithm("QLearningExplore",
            rl.createSingleObjectiveExploreQLearner(10, 0.9, 0.9, true, 1e-3, 0.1, null, 0));

      search.addAlgorithm("QLearning", rl.createSingleObjectiveQLearner(0.9, 0.9, true, 1e-3, 0.1, null, 0));

      // experiment
      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(search, MAX_EVALUATIONS);

      experiment.setNumberOfRuns(NR_RUNS);
      experiment.addProgressListener(new SeedRuntimePrintListener());

      final int printInterval = 1000;

      if(PRINT_POPULATIONS) {
         final List<String> algoNames = new ArrayList<>();
         for(final IRegisteredAlgorithm<? extends Algorithm> a : search.getAlgorithms()) {
            algoNames.add(search.getAlgorithmName(a));
         }

         if(!Files.exists(Paths.get(PRINT_DIRECTORY))) {
            new File(PRINT_DIRECTORY).mkdir();
         }

         experiment.addProgressListener(
               new CurrentNondominatedPopulationPrintListener(PRINT_DIRECTORY, algoNames, NR_RUNS, printInterval));
      }

      experiment.run();

      System.out.println("-------------------------------------------------------");
      System.out.println("Analysis");
      System.out.println("-------------------------------------------------------");
      performAnalysis(experiment);
      System.out.println("-------------------------------------------------------");
      System.out.println("Results");
      System.out.println("-------------------------------------------------------");
      handleResults(experiment);
   }

   protected static SearchAnalyzer performAnalysis(final SearchExperiment<TransformationSolution> experiment) {
      final SearchAnalysis analysis = new SearchAnalysis(experiment);
      analysis.setHypervolume(true);
      analysis.setShowAggregate(true);
      analysis.setShowIndividualValues(true);
      analysis.setShowStatisticalSignificance(true);
      analysis.setSignificanceLevel(significanceLevel);
      final SearchAnalyzer searchAnalyzer = analysis.analyze();
      System.out.println("---------------------------");
      System.out.println("Analysis Results");
      System.out.println("---------------------------");
      searchAnalyzer.printAnalysis();
      System.out.println("---------------------------");
      try {
         System.out.println("- Save Analysis to 'output/analysis/analysis.txt'");
         searchAnalyzer.saveAnalysis(new File("output/analysis/analysis.txt"));
      } catch(final IOException e) {
         e.printStackTrace();
      }
      System.out.println("- Save Indicator BoxPlots to 'output/analysis/'");
      searchAnalyzer.saveIndicatorBoxPlots("output/analysis/", baseName);
      return searchAnalyzer;
   }

   public static void saveModels(final List<File> modelFiles) {
      for(final File file : modelFiles) {
         final EGraph graph = MomotUtil.loadGraph(file.getPath());
         MomotUtil.saveGraph(graph, file.getPath());
      }
   }
}
