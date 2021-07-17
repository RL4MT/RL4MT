# Towards Reinforcement Learning for In-Place Model Transformations

This project demonstrates the use of Reinforcement Learning (RL) methods for rule-orchestrated model transformations. In that matter,
the MOMoT Framework is employed which represents problem domains by means of Ecore meta-models and problem instances as models to be optimized through executing graph transformation rules. The framework is extended by two different RL approaches, named value-based and policy-based, which
portray alternative optimization methods to so far implemented metaheuristics for finding valuable rule compositions. The project includes
case studies that primarily evaluate the RL agents against the Non-Dominated Sorting Algorithm (NSGA-II) and demonstrate the feasibility of RL methods for search-based model optimization.

The following sections describe how RL agents can be employed using the MOMoT Framework with RL-based extensions in general and how
the case studies presented in the paper are executed. Also, a short example for the Stack Load Balancing case is shown and the steps to reproduce results as presented in the paper are given.

* Overview
    * [Employ RL for problem cases](#how-the-rl-approaches-can-be-employed-for-problem-cases)
    * [Case Studies: How to run](#case-studies:-how-to-run)
        * [Example: Stack Load Balancing](#example:-stack-load-balancing)
    * [Reproduction of Results](#reproduction-of-results-presented-in-paper)
        * [Table 1: Stack Load Balancing, CRA, OO-Refactoring](#table-1:-hypervolume-avg./std.-and-best-objective-value-(stack-load-balancing,-oo-refactoring,-class-responsibility-assignment-problem))
        * [Pacman: Training & Reward plot](#reward-plot-of-q_Basic-and-policy-gradient-in-the-pacman-game)
    

## Where to get the project

The project with the extended MOMoT framework and implementation of four case studies is publicly
available at github.com/rl4mt/rl4mt. 

A list of the required programs, how to set them up and how to configure the environment in order to support our project, and other preliminaries
can be found in installation.pdf.

## How to use it - General use of the RL agents and result reproduction 

### How the RL approaches can be employed for problem cases

The new RL methods and required utilities are implemented in the MOMoT core plugins as provided in this project. The following examples show how one instantiates an agent for generic problem cases and for cases where the encoding for a neural network is provided. 

*Note*: 1.1 and 1.2 describe general implementation details and are not required to run the case studies as described in Section 2.

#### For generic learners 

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

#### For policy-based learners with domain-specific encodings (like the Pacman game)

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

### Case Studies: How to run

Generally, MOMoT requires a search configuration which defines the (problem) model instance, a set of transformation rules,
objectives, constraints, and a selection of algorithms to execute an experiment. As the RL agents are not yet part of the configuration language 
provided for MOMoT, the configurations are contained in single static .java files.

The metamodel, model instances, and rules for four different example cases are present in the repository:

* Stack Load Balancing -> *at.ac.tuwien.big.momot.examples.stack/src/at.ac.tuwien.big.momot.examples.stack* (**main: StackSearch.java**)
* Class Responsibility Assignment -> *at.ac.tuwien.big.momot.examples.cra/src/icmt.tool.momot.demo* (**main: ArchitectureSearch.java**)
* OO-Refactoring -> *at.ac.tuwien.big.momot.examples.refactoring/src/at.ac.tuwien.big.momot.examples.refactoring* (**main: RefactoringSearch.java**)
* The Pacman Game -> *org.pacman/src/PacmanGame.search* (**main: PacmanSearch.java**)

The "main"-files contain an executable search setup, respectively, such that would naturally be generated according to a search configuration using a configuration language specific to MOMoT. In these setups, one can change the initial model instance (*INITIAL_MODEL*), maximum transformation length (*SOLUTION_LENGTH*), and the population size (*POPULATION_SIZE*) used for NSGA-II. *MAX_EVALUATIONS* and *NR_RUNS* define the maximum number of solution evaluations per trial and the number of trials for the experiment. The search algorithms (including the contributed RL agents) and corresponding parameters are added in method *createOrchestration(...)*.

Upon running a "main"-file, the setup will be executed and the experiment settings printed to the Eclipse console. Furthermore,
the console shows runtime progress regarding the currently executed trial for all algorithms. After the last trial, summary statistics
will be printed to enable a performance comparison between tested algorithms. Printed metrics include the average and maximum hypervolume obtained in the runs and a combined pareto set of solutions where the best objective value can be observed.

In case of the Pacman model, the learning behaviour of the policy gradient was analyzed and compared to the value-based algorithm Q_Basic. Therefore, the agents are trained separately from the same file *PacmanSearch.java* and the average rewards recorded and saved as .csv file in the "output"-subfolder. Location of this file is defined when adding the algorithms to the orchestration in *createOrchestration(...)* method. To reproduce the plot, follow the steps [in this section](#reward-plot-of-q_Basic-and-policy-gradient-in-the-pacman-game).

#### Example: Stack Load Balancing

Resources for this example are to be found in the project *at.ac.tuwien.big.momot.examples.stack*. Hereby, a *StackModel* (*stack.ecore* in *model* folder) consists of up to multiple *Stack*s with a load each. The goal in this case is to apply shift-operations in form of two rules, *shiftLeft* and *shiftRight*, where a certain amount is 
transfered from the source stack to its left or right neighbour, i.e., the target stack. Note that the shift amount is chosen randomly by the Henshin Engine but set to a maximum of 5 units in one transformation step for this problem domain. (see *StackOrchestration.java* in *src\at\ac\tuwien\big\momot\examples\stack*).
<figure>
<img src="./stack.svg" alt="Stack Meta-Model">
<figcaption><b>Meta-Model: Stack Load Balancing Case Study</b></figcaption>
</figure>

There are four different problem instances provided in the *model*-folder with varying number of stacks. Each of them can be set for the *INPUT_MODEL* in *StackSearch.java* (*src\at\ac\tuwien\big\momot\examples\stack*). Additionally, other parameters concerning the experiment setting can be changed as requested. For example, using the model with five stacks as initial problem instance, allowing for up to 8 shift operations, and repeating the experiment 3 times with 5,000 iterations each, we set the variables in *StackSearch.java* as follows:

```java
private static final int SOLUTION_LENGTH = 8;
private static final String INPUT_MODEL = "model_five_stacks.xmi";
private static final int NR_RUNS = 3;
private static final int MAX_EVALUATIONS = 5000;
```

When we execute this, the output looks as follows:

<details>
  <summary>Click to show console output</summary>
  
  ```
  -------------------------------------------------------
Search
-------------------------------------------------------
Objectives:      [Standard Deviation, SolutionLength]
NrObjectives:    2
Constraints:     []
NrConstraints:   0
Transformations: [Stack]
Units:           [Rule shiftLeft(fromId, toId, amount, fromLoad, toLoad), Rule shiftRight(fromId, toId, amount, fromLoad, toLoad)]
SolutionLength:  8
PopulationSize:  100
Iterations:      50
MaxEvaluations:  5000
AlgorithmRuns:   3
---------------------------
Run 'NSGAII' 3 times...
[05:05:00.119] Run 1 of 3 started.
[05:05:03.081] Run 1 of 3 terminated after 00:00:02.955 (2955 ms).
[05:05:03.083] Run 2 of 3 started.
[05:05:03.842] Run 2 of 3 terminated after 00:00:00.758 (758 ms).
[05:05:03.843] Run 3 of 3 started.
[05:05:04.467] Run 3 of 3 terminated after 00:00:00.624 (624 ms).
[05:05:04.469] Total runtime for 3 seeds: 00:00:04.350 (4350 ms).
Run 'QLearningExplore' 3 times...
[05:05:04.485] Run 1 of 3 started.
[05:05:06.183] Run 1 of 3 terminated after 00:00:01.696 (1696 ms).
[05:05:06.189] Run 2 of 3 started.
[05:05:07.783] Run 2 of 3 terminated after 00:00:01.594 (1594 ms).
[05:05:07.784] Run 3 of 3 started.
[05:05:09.438] Run 3 of 3 terminated after 00:00:01.653 (1653 ms).
[05:05:09.439] Total runtime for 3 seeds: 00:00:04.954 (4954 ms).
Run 'QLearning' 3 times...
[05:05:09.442] Run 1 of 3 started.
[05:05:11.886] Run 1 of 3 terminated after 00:00:02.444 (2444 ms).
[05:05:11.888] Run 2 of 3 started.
[05:05:14.215] Run 2 of 3 terminated after 00:00:02.327 (2327 ms).
[05:05:14.217] Run 3 of 3 started.
[05:05:16.624] Run 3 of 3 terminated after 00:00:02.407 (2407 ms).
[05:05:16.626] Total runtime for 3 seeds: 00:00:07.183 (7183 ms).
-------------------------------------------------------
Analysis
-------------------------------------------------------
---------------------------
Analysis Results
---------------------------
NSGAII:
    Hypervolume: 
        Aggregate: 0.4989877579547276
        Min: 0.4804635643013558
        Median: 0.4989877579547276
        Max: 0.4989877579547276
        Mean: 0.492813026736937
        StandardDeviation: 0.01069494819229498
        Variance: 1.1438191683587365E-4
        Count: 3
        Indifferent: [QLearningExplore, QLearning]
        EffectSize: [
            - { measure: CohensD, algorithm: QLearningExplore, effectSize: 0.816496580927726, magnitude: large }
            - { measure: CliffsDelta, algorithm: QLearningExplore, effectSize: 0.3333333333333333, magnitude: medium }
            - { measure: VarghaDelaneyA, algorithm: QLearningExplore, effectSize: 0.3333333333333333 }
            - { measure: CohensD, algorithm: QLearning, effectSize: 1.779545233537748, magnitude: large }
            - { measure: CliffsDelta, algorithm: QLearning, effectSize: 0.7777777777777778, magnitude: large }
            - { measure: VarghaDelaneyA, algorithm: QLearning, effectSize: 0.8888888888888888 }
        ]
        Values: [0.4989877579547276, 0.4989877579547276, 0.4804635643013558]
QLearningExplore:
    Hypervolume: 
        Aggregate: 0.4989877579547276
        Min: 0.4989877579547276
        Median: 0.4989877579547276
        Max: 0.4989877579547276
        Mean: 0.4989877579547276
        StandardDeviation: 0.0
        Variance: 0.0
        Count: 3
        Indifferent: [NSGAII, QLearning]
        EffectSize: [
            - { measure: CohensD, algorithm: NSGAII, effectSize: 0.816496580927726, magnitude: large }
            - { measure: CliffsDelta, algorithm: NSGAII, effectSize: 0.3333333333333333, magnitude: medium }
            - { measure: VarghaDelaneyA, algorithm: NSGAII, effectSize: 0.6666666666666666 }
            - { measure: CohensD, algorithm: QLearning, effectSize: 4.538540534168039, magnitude: large }
            - { measure: CliffsDelta, algorithm: QLearning, effectSize: 1.0, magnitude: large }
            - { measure: VarghaDelaneyA, algorithm: QLearning, effectSize: 1.0 }
        ]
        Values: [0.4989877579547276, 0.4989877579547276, 0.4989877579547276]
QLearning:
    Hypervolume: 
        Aggregate: 0.48477364423394687
        Min: 0.4727905920581036
        Median: 0.4727905920581036
        Max: 0.48477364423394687
        Mean: 0.4767849427833847
        StandardDeviation: 0.006918418399436447
        Variance: 4.786451314966077E-5
        Count: 3
        Indifferent: [NSGAII, QLearningExplore]
        EffectSize: [
            - { measure: CohensD, algorithm: NSGAII, effectSize: 1.779545233537748, magnitude: large }
            - { measure: CliffsDelta, algorithm: NSGAII, effectSize: 0.7777777777777778, magnitude: large }
            - { measure: VarghaDelaneyA, algorithm: NSGAII, effectSize: 0.1111111111111111 }
            - { measure: CohensD, algorithm: QLearningExplore, effectSize: 4.538540534168039, magnitude: large }
            - { measure: CliffsDelta, algorithm: QLearningExplore, effectSize: 1.0, magnitude: large }
            - { measure: VarghaDelaneyA, algorithm: QLearningExplore, effectSize: 0.0 }
        ]
        Values: [0.48477364423394687, 0.4727905920581036, 0.4727905920581036]
---------------------------
- Save Analysis to 'output/analysis/analysis.txt'
- Save Indicator BoxPlots to 'output/analysis/'
-------------------------------------------------------
Results
-------------------------------------------------------
REFERENCE SET:

2.8284271247461903 0.0
2.0976176963403033 1.0
0.6324555320336759 3.0
0.8944271909999159 2.0
0.0 5.0

- Save objectives of all algorithms to 'output/objectives/objective_values.txt'
- Save models of all algorithms to 'output/models/'
- Save objectives of algorithms seperately to 'output/objectives/<algorithm>.txt'
- Save models of algorithms seperately to 'output/solutions/<algorithm>.txt'´

QLearning
2.8284271247461903 0.0
2.0976176963403033 1.0
1.0954451150103321 2.0
0.6324555320336759 3.0


NSGAII
0.6324555320336759 3.0
2.8284271247461903 0.0
2.0976176963403033 1.0
0.8944271909999159 2.0
0.0 5.0


QLearningExplore
2.8284271247461903 0.0
0.0 5.0
2.0976176963403033 1.0
0.6324555320336759 3.0
0.8944271909999159 2.0
  ```
</details>


## Reproduction of results presented in paper          

For results in the paper we used the Hypervolume and objective values to compare solution sets produced by our Q-learning implementations, Q_Basic and Q_Explore, against NSGA-II for
three case studies (Stack Load Balancing, OO-Refactoring, Class Responsibility Assignment Problem). For the Pacman game we compared a policy gradient based approach (PG) with
domain-specific inputs to the Q_Basic algorithm in terms of average returned rewards during training. The following two sections explain how the results were produced.

### Table 1: Hypervolume and best objective value (Stack Load Balancing, OO-Refactoring, Class Responsibility Assignment Problem)

By default, the average and standard deviation of the Hypervolume over the trials as well as the best objective value (as can be read from the printed pareto set) are included
in the console output after running any of these experiments. 

In certain circumstances an experiment might not finish without error. For example, a memory error might aborts further program execution preemptively due to insufficient heap space as a result of memory not beeing freed in between trials. This appears more likely with a larger number of trials and solution evaluations. In order to still run such experiments of larger scale, there is a Listener implemented to record the pareto solution sets obtained during a trial in regular intervals. Therefore, in each "main"-file (e.g. *StackSearch.java*) a variable *PRINT_POPULATIONS* can be set to ```true```. With that, the solution sets will be recorded and saved every 1000 evaluations in the path set with *PRINT_DIRECTORY* in a subfolder *runs*. A possible setting would be:

```java
protected static final boolean PRINT_POPULATIONS = true;
protected static final String PRINT_DIRECTORY = "output/populations/five_stacks";
```

With *PRINT_POPULATIONS* set to ```true```, solutions of the runs are persisted and the experiment can be rerun multiple times to accumulate more trials. The listener recognizes past runs
saved in *PRINT_DIRECTORY* and continues the enumerated folder names in the respective *runs*-folder. Having collected enough runs, *HVCalculator.java* has to be run after setting *CASE_PATH* equal
to the folder with the experiment runs, i.e., *PRINT_DIRECTORY*. This creates a file "summary.txt" in the same directory, containing the statistics computed over all recorded runs.

*Note*: The latest created solution folder, i.e., the one named with the highest number among all folders, in the respective *runs*-subfolder should be deleted when encontering *OutOfMemoryError* as the run
finished preemptively. E.g., if the 10th run of algorithm "XYZ" results in a memory-related error in the Stack example case, a folder *examples.stack\output\populations\five_stacks\XYZ\runs* will have a subfolder
named "10" and, being the latest run, should be deleted.

### Reward plot of Q_Basic and Policy Gradient in the Pacman game

In this example we trained two agents, Q_Basic and PG, and examined the learning behavior by means of the average returned reward during training. Therefore,
two configurations *PacmanSearchQLearning.java* and *PacmanSearchPolicyGradient.java* are executed.

#### Training the agents

When executing the configurations, rewards received by the respective agent will be recorded and stored at a path given along the algorithm-related settings in *createOrchestration(...)* method. By default, this is the "output" subfolder in the Pacman project. Note that both agents are trained in separate configurations, *PacmanSearchQLearning.java* and *PacmanSearchPolicyGradient.java*. Therefore, we set an arbitrary but very high number for *MAX_EVALUATIONS* and trained for a certain amount of time, e.g. 10 minutes, as is set with *TRAINING_TIME* (= number of seconds to train for).  

During training, console output informs on the progress in terms of performed training epochs and saving the progress. By default, recorded rewards over time are written to files *output/pg.csv* and *output/qlearn.csv*:

```java
     orchestration.addAlgorithm("Q-learning", rl.createSingleObjectiveQLearner(
         0.9, 0.9, true, 1e-3, 0.1, "output/qlearn", 200, TRAINING_TIME
    ));

      orchestration.addAlgorithm("PG", rl.createPolicyGradient(
          0.95, 1e-4, null, "output/nn", "output/pg", 200, false, TRAINING_TIME
    ));
```

#### Creating the plot 

After training both the Q_Basic agent and the policy gradient based network, the rewards can be plotted to compare the learned behavior. For producing the graph depicted in the paper, we trained both agents for at least 10 minutes of time as shown on the horizontal axis. Having reward files for both agents, *generate_plot.py* can be used to create the plot in *plot_util* directory. Note that Python needs to be installed as illustrated in the installation file in order for this to work. In a command line interface, the script is called with the paths to the reward files as follows:

```bash
    python generate_plot.py ../output/pg.csv ../output/qlearn.csv 600
```

In this sequence, the first two parameters after the script name are the paths to the reward files recorded from training the PG agent and Q_Basic agent, respectively. The third
argument is the max. value to use on the time axis, meaning the time range in seconds (600 in the code snippet) over which rewards are to be plotted. The plot will be saved in the same directory as *average_rewards.png*. Note that the figure can deviate from the reward plot presented in the paper due to randomness of random number generators. In essence, results show that the PG-based agent learns a policy that avoids large negative rewards a lot faster and more consistenly than value-based Q_Basic.

