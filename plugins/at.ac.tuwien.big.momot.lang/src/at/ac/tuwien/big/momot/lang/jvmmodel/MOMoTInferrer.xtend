package at.ac.tuwien.big.momot.lang.jvmmodel

class MOMoTInferrer {
   interface Name {
      public static val FIELD_INITIAL_MODEL = "INITIAL_MODEL"
      public static val FIELD_SOLUTION_LENGTH = "SOLUTION_LENGTH"

      public static val FIELD_MODULES = "modules"
      public static val FIELD_UNITS_TO_REMOVE = "unitsToRemove"
      public static val FIELD_NON_SOLUTION_PARAMETERS = "nonSolutionParameters"
      public static val FIELD_SOLUTION_REPAIRER = "solutionRepairer"
      public static val FIELD_FITNESS_FUNCTION = "fitnessFunction"
      public static val FIELD_POPULATION_SIZE = "populationSize"
      public static val FIELD_MAX_EVALUATIONS = "maxEvaluations"
      public static val FIELD_NR_RUNS = "nrRuns"
      public static val FIELD_REFERENCE_SET = "referenceSet"
      public static val FIELD_BASE_NAME = "baseName"
      public static val FIELD_SIGNIFICANCE_LEVEL = "significanceLevel"
      public static val FIELD_CREATE_PARAMETER_VALUE_KEY = "_parameterValueKey_"

      public static val METHOD_CREATE_MODULE_MANAGER = "createModuleManager"
      public static val METHOD_CREATE_FITNESS_FUNCTION = "createFitnessFunction"
      public static val METHOD_CREATE_OBJECTIVE_PREFIX = "_createObjective_"
      public static val METHOD_CREATE_OBJECTIVE_HELPER_PREFIX = "_createObjectiveHelper_"
      public static val METHOD_CREATE_CONSTRAINT_PREFIX = "_createConstraint_"
      public static val METHOD_CREATE_CONSTRAINT_HELPER_PREFIX = "_createConstraintHelper_"
      public static val METHOD_CREATE_LISTENER_PREFIX = "_createListener_"
      public static val METHOD_CREATE_COLLECTOR_PREFIX = "_createCollector_"
      public static val METHOD_CREATE_EQUALITY_HELPER = "_createEqualityHelper_"
      public static val METHOD_CREATE_EQUALITY_HELPER_HELPER = "_createEqualityHelperHelper_"
      public static val METHOD_CREATE_PARAMETER_VALUE_PREFIX = "_createParameterValue_"
      public static val METHOD_CREATE_ALGORITHM_PREFIX = "_createRegisteredAlgorithm_"
      public static val METHOD_CREATE_ALGORITHM_HELPER_PREFIX = "_createAlgorithm_"
      public static val METHOD_MAIN = "main"
      public static val METHOD_INIT = "initialization"
      public static val METHOD_FINAL = "finalization"
      public static val METHOD_PERFORM_SEARCH = "performSearch"
      public static val METHOD_PRINT_SEARCH_INFO = "printSearchInfo"
      public static val METHOD_CREATE_ORCHESTRATION = "createOrchestration"
      public static val METHOD_CREATE_INPUT_GRAPH = "createInputGraph"
      public static val METHOD_ADAPT_INPUT_GRAPH = "adaptInputGraph"
      public static val METHOD_ADAPT_INPUT_MODEL = "adaptInputModel"
      public static val METHOD_CREATE_EXPERIMENT = "createExperiment"
      public static val METHOD_PERFORM_ANALYSIS = "performAnalysis"
      public static val METHOD_HANDLE_RESULTS = "handleResults"
      public static val METHOD_ADAPT_MODELS = "adaptResultModels"
      public static val METHOD_ADAPT_MODEL = "adaptResultModel"
      public static val METHOD_DERIVE_BASE_NAME = "deriveBaseName"
      public static val METHOD_FITNESS_PREPROCESS = "preprocessEvaluation"
      public static val METHOD_FITNESS_POSTPROCESS = "postprocessEvaluation"

      public static val PARAM_INITIAL_GRAPH = "initialGraph"
      public static val PARAM_SOLUTION_LENGTH = "solutionLength"
      public static val PARAM_ORCHESTRATION = "orchestration"
      public static val PARAM_MODULE_MANAGER = "moduleManager"
      public static val PARAM_MOEA_FACTORY = "moea"
      public static val PARAM_LOCAL_FACTORY = "local"
      public static val PARAM_EXPERIMENT = "experiment"
      public static val PARAM_SEARCH_ANALYZER = "searchAnalyzer"
      public static val PARAM_RESULT_MANAGER = "resultManager"
      public static val PARAM_SOLUTION = "solution"
      public static val PARAM_AGGREGATED_FITNESS = "aggregatedFitness"
      public static val PARAM_GRAPH = "graph"
      public static val PARAM_ROOT = "root"
      public static val PARAM_EQUALITY_LEFT = "left"
      public static val PARAM_EQUALITY_RIGHT = "right"
      public static val PARAM_SOLUTION_WRITER = "solutionWriter"
      public static val PARAM_POPULATION_WRITER = "populationWriter"
      public static val PARAM_POPULATION = "population"
      public static val PARAM_MODEL_FILES = "modelFiles"
   }
}
