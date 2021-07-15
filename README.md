# Towards Reinforcement Learning for In-Place Model Transformations

This is the source repository for our submission at MODELS 2021: ACM/IEEE 24th International Conference on Model Driven Engineering Languages and Systems. It includes four case studies on which we tested our reinforcement learning (RL) approaches embedded in the MOMoT framework ([Website](http://martin-fleck.github.io/momot/), [GitHub](https://github.com/martin-fleck/momot)) and with which we conducted the experiments presented in the paper.

## Project Structure 
 
The case studies are divided over 4 subprojects in the "examples"-project with
separate main files to run each example case:

* Stack Load Balancing -> examples/examples.stack (**main: StackSearch.java**)
* Class Responsibility Assignment -> examples/examples.cra (**main: ArchitectureSearch.java**)
* OO-Refactoring -> examples/examples.refactoring (**main: RefactoringSearch.java**)
* The Pacman Game -> examples/examples.pacman (**main: PacmanSearch.java**)

All other projects/plugins are responsible for the MOMoT functionality with the core plugins (packages *at.ac.tuwien.big.moea* and *at.ac.tuwien.big.momot.core*) having been extended to support several RL methods.

## Results 

In ***examples/submission_results*** we deposited the Hypervolume statistics and non-dominated solution sets obtained in
our experiments.

## Reproducing results

MOMoT uses the *SearchAnalysis* class (derived from *Analyzer* of the MOEA framework) to compute evaluation metrics after
experiments are finished. The main classes listed above already set up the analyzer such that Hypervolume statistics and 
the final non-dominated solution sets are printed to the console. Additionally, statistics and result models will be
saved into the "output"-subfolder of the respective example project after the experiment is finished.

## How the RL approaches can be employed for other problem cases

The new RL methods and required utilities are implemented in the MOMoT core plugins as provided in the above mentioned repository. The following examples show how one instantiates an agent for generic problem cases and for cases where the encoding for a neural network is provided.

1. For generic learners 

Generally, algorithms are employed for optimization in MOMoT by creating them with a corresponding "Factory" object first
and then adding them to the orchestration: 

```java
// Create factory for evolutionary algorithms
final EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
            .createEvolutionaryAlgorithmFactory(populationSize);
// Add NSGA-II to orchestration      
orchestration.addAlgorithm("NSGA-II",
    moea.createNSGAII(new TournamentSelection(2), new OnePointCrossover(1.0),
        new TransformationParameterMutation(0.1, orchestration.getModuleManager()),
        new TransformationPlaceholderMutation(0.2))
);
```

For the RL agents, an Environment object is created first with information on the problem domain 
and further used to to initialize the *RLAlgorithmFactory*:

```java
// Create environment
final IEnvironment<TransformationSolution> env = new Environment<>(
    new SolutionProvider<>(orchestration.getSearchHelper()), 
    new ObjectiveFitnessComparator<TransformationSolution>(
                  orchestration.getFitnessFunction().getObjectiveIndex("Standard Deviation")
    )
);
// Create factory with environment for reinforcement learning algorithms
final RLAlgorithmFactory<TransformationSolution> rlFactory = orchestration.createRLAlgorithmFactory(env);
```

A *SolutionProvider* and a fitness function *ObjectiveFitnessComparator* with the objective name, here "Standard Deviation", are passed to the *Environment* constructor.

Then the algorithm can be 
created and added to the evaluation as usual. The basic Q-Learning agent, for example, is instantiated as follows:

```java
// Add basic Q-Learning algorithm with parameters gamma, epsilon, etc.
orchestration.addAlgorithm("QLearning", rlFactory.createSingleObjectiveQLearner(0.9, 0.9, true, 1e-4, 0.1, null);
```

2. For policy-based learners with domain-specific encodings (like the Pacman game)

In order to use the policy-based approach, a reward function and an encoding for the domain model 
have to be provided. This requires implementing a class deriving from class *AbstractProblemEncoder* (e.g., see *PacmanEncoding.java* in *at.ac.tuwien.big.momot.core* package). Such a class defines how the network inputs are derived from a model state, the rewards issued on certain rule applications, and one can further specify rule dependencies such that, for example, the rule *eat* and *kill* need to be applied after each step done by the Pacman if possible, i.e., if a field with food or a ghost was entered. Then a *DomainEnvironment* is created and used to initialize *RLAlgorithmFactory* as follows:

```java
// Create domain-specific environment
 final IEnvironment<TransformationSolution> env = new DomainEnvironment<TransformationSolution>(
    new SolutionProvider<>(orchestration.getSearchHelper()), 
    new ObjectiveFitnessComparator<TransformationSolution>(
        orchestration.getFitnessFunction().getObjectiveIndex("Score")
    ), 
    EncodingFactory.createEncoder(PacmanEncoding.class)
 );
// Create factory with environment
final RLAlgorithmFactory<TransformationSolution> rlFactory = orchestration.createRLAlgorithmFactory(env);
// Add policy gradient agent (or others from rlFactory) with parameters gamma, learning rate, logging paths ..
orchestration.addAlgorithm("PG", rl.createPolicyGradient(0.95, 1e-4, null,
            null, null, 0, false));
```

The only difference to the *Environment* for generic problems is that *DomainEnvironment* takes an additional parameter
*PacmanEncoding* with the domain-specific informations like the model encoding.