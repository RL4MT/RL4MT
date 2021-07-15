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
package at.ac.tuwien.big.momot.search.solution.executor;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FixedRuleApplicationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.UnitParameter;
import at.ac.tuwien.big.moea.util.CollectionUtil;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.solution.variable.ITransformationVariable;
import at.ac.tuwien.big.momot.problem.solution.variable.RuleApplicationVariable;
import at.ac.tuwien.big.momot.problem.solution.variable.TransformationPlaceholderVariable;
import at.ac.tuwien.big.momot.problem.solution.variable.UnitApplicationVariable;
import at.ac.tuwien.big.momot.problem.unit.parameter.fix.FixValue;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm.RLUtils;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.eclipse.emf.henshin.interpreter.ApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.Assignment;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.InterpreterFactory;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.model.Parameter;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class SearchHelper {
   public static final int UNLIMITED = -1;
   public static final int DEFAULT_NR_TRIES_PER_RULE = 5;

   protected TransformationSearchOrchestration searchOrchestration;
   protected Engine engine;
   protected int maxTriesPerUnit = DEFAULT_NR_TRIES_PER_RULE;
   protected ApplicationMonitor monitor = null;
   final Random rand = new Random();
   final IRLUtils<TransformationSolution> utils = new RLUtils<>();

   protected List<UnitParameter> baseRules;
   protected List<UnitParameter> postBaseRules;

   public SearchHelper() {
   }

   public SearchHelper(final Engine engine, final TransformationSearchOrchestration searchOrchestration) {
      this.engine = engine;
      this.searchOrchestration = searchOrchestration;
   }

   public SearchHelper(final TransformationSearchOrchestration searchOrchestration) {
      this(searchOrchestration.getEngine(), searchOrchestration);
   }

   // public SearchHelper(final TransformationSearchOrchestration searchOrchestration, final ProblemType problemType,
   // final List<UnitParameter> baseRules, final List<UnitParameter> postBaseRules) {
   // this(searchOrchestration.getEngine(), searchOrchestration);
   // this.baseRules = utils.createBaseRules(problemType);
   // this.postBaseRules = utils.createPostBaseRules(problemType);
   // }

   public Object[] appendParticularVariable(final IProblemEncoder<TransformationSolution> encoder,
         final TransformationSolution solution, INDArray actionProbs) {

      final Object[] rv = new Object[3];

      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());

      final EGraph searchGraph = solution.execute();

      final List<ITransformationVariable> variables = new ArrayList<>(Arrays.asList(solution.getVariables()));

      ITransformationVariable var = null;
      int action = 0;

      FixedRuleApplicationStrategy applicationStrategy = null;

      // double[] actionPropArr = actionProbs.dup().data().asDouble();
      final int[] actionSpace = Nd4j.arange(0, actionProbs.size(1), 1).toIntVector();

      do {
         final EnumeratedIntegerDistribution enumDist = new EnumeratedIntegerDistribution(actionSpace,
               actionProbs.toDoubleVector());

         action = enumDist.sample();
         applicationStrategy = encoder.getFixedUnitApplicationStrategy(solution, action);

         var = findFixedUnitApplication(searchGraph, applicationStrategy.getDistributionSampleRule().getUnitName(),
               applicationStrategy.getDistributionSampleRule().getParameterValues());
         if(var == null) {
            // move illegal, remove action and renormalize distribution for valid actions
            actionProbs.putScalar(action, 0.0);
            actionProbs = actionProbs.div(actionProbs.sum(1));
            continue;

         }

         var.execute(false);
         if(var.isExecuted()) {
            variables.add(var);
         }

      } while(var == null || !var.isExecuted());
      // If defined, perform Subsequent optinoal rules here (e.g., try to apply "eat"-rule after each move in pacman
      // game)
      for(final UnitParameter unitParameter : applicationStrategy.getOptionalSubsequentRules()) {
         var = findFixedUnitApplication(searchGraph, unitParameter.getUnitName(), unitParameter.getParameterValues());

         if(var == null) {
            continue;
         }
         var.execute(false);
         if(var.isExecuted()) {
            variables.add(var);
         }
      }

      final TransformationSolution extendedSolution = new TransformationSolution(solution.getSourceGraph(), variables,
            solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
      extendedSolution.execute(false);

      // extendedSolution.setTransformation(variables, searchGraph);

      rv[0] = action;
      rv[1] = extendedSolution;
      rv[2] = false;

      return rv;
   }

   public TransformationSolution appendRandomVariables(final TransformationSolution solution, final int nrVariables) {
      final int newSolutionLength = solution.getNumberOfVariables() + nrVariables;
      final TransformationSolution extendedSolution = new TransformationSolution(solution.getSourceGraph(),
            newSolutionLength, solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());

      final EGraph searchGraph = solution.execute();

      final List<ITransformationVariable> variables = new ArrayList<>(Arrays.asList(solution.getVariables()));
      if(nrVariables >= 1) {

         ITransformationVariable var = findUnitApplication(searchGraph);
         while(var != null) {
            var.execute(false);
            if(var.isExecuted()) {
               variables.add(var);

            }
            if(variables.size() >= nrVariables) {
               break;
            }
            var = findUnitApplication(searchGraph);
         }
      }

      extendedSolution.setTransformation(variables, searchGraph);
      return extendedSolution;
   }

   public TransformationSolution appendRandomVariablesWithStrategy(
         final IProblemEncoder<TransformationSolution> encoder, final TransformationSolution solution, int nrSteps) {

      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());

      final EGraph searchGraph = solution.execute();

      final List<UnitParameter> baseRules = encoder.createBaseRules();
      final List<UnitParameter> postBaseRules = encoder.createPostBaseRules();

      final List<ITransformationVariable> variables = new ArrayList<>(Arrays.asList(solution.getVariables()));

      while(nrSteps >= 1) {
         final UnitParameter unitParameter = CollectionUtil.getRandomElement(baseRules);
         ITransformationVariable var = findFixedUnitApplication(searchGraph, unitParameter.getUnitName(),
               unitParameter.getParameterValues());
         if(var != null) {
            var.execute(false);
            if(var.isExecuted()) {
               variables.add(var);

               for(final UnitParameter postBaseUnitParameter : postBaseRules) {
                  var = findFixedUnitApplication(searchGraph, postBaseUnitParameter.getUnitName(),
                        postBaseUnitParameter.getParameterValues());
                  if(var == null) {
                     continue;
                  }
                  var.execute(false);
                  if(var.isExecuted()) {
                     variables.add(var);
                     nrSteps--;

                  }
               }
               nrSteps--;
            }

            if(variables.get(variables.size() - 1).getUnit().getName().compareTo("kill") == 0) {
               break;
            }
         }
      }

      final TransformationSolution extendedSolution = new TransformationSolution(solution.getSourceGraph(), variables,
            solution.getNumberOfObjectives(), solution.getNumberOfConstraints());

      if(variables.get(variables.size() - 1).getUnit().getName().compareTo("kill") == 0) {
         extendedSolution.setDone(true);
      }

      return extendedSolution;

   }

   public TransformationSolution appendVariables(final TransformationSolution solution,
         final ITransformationVariable... variable) {

      final List<ITransformationVariable> variables = new ArrayList<>(Arrays.asList(solution.getVariables()));
      variables.addAll(Arrays.asList(variable));

      final TransformationSolution extendedSolution = new TransformationSolution(solution.getSourceGraph(), variables,
            solution.getNumberOfObjectives(), solution.getNumberOfConstraints());

      return extendedSolution;
   }

   public TransformationSolution appendVariables(final TransformationSolution solution,
         final List<Assignment> assignments) {

      final EGraph searchGraph = solution.execute();

      final List<ITransformationVariable> variables = new ArrayList<>(Arrays.asList(solution.getVariables()));

      for(final Assignment assignment : assignments) {

         final Map<String, Object> paramValues = new HashMap<>();
         for(final Parameter p : assignment.getUnit().getParameters()) {
            paramValues.put(p.getName(), assignment.getParameterValue(p));
         }

         final Match match = this.createFixedAssignment(assignment.getUnit(), paramValues);

         ITransformationVariable applicationVar = null;
         if(match != null) {
            final RuleApplicationVariable application = createApplication(searchGraph, match);

            if(application.execute(getMonitor())) {
               for(final Parameter param : assignment.getUnit().getParameters()) {
                  application.setParameterValue(param, application.getResultParameterValue(param));
               }

               applicationVar = clean(application);

               if(applicationVar != null) {
                  applicationVar.execute(false);
                  if(applicationVar.isExecuted()) {
                     variables.add(applicationVar);
                  }
               } else {
                  return null;
               }
            } else {
               application.undo(getMonitor());
            }
         } else {
            return null;
         }

      }
      final TransformationSolution extendedSolution = new TransformationSolution(solution.getSourceGraph(), variables,
            solution.getNumberOfObjectives(), solution.getNumberOfConstraints());
      return extendedSolution;

   }

   private ITransformationVariable clean(final ITransformationVariable variable) {
      getModuleManager().clearNonSolutionParameters(variable);
      return variable;
   }

   public UnitApplicationVariable createApplication(final EGraph graph, final Assignment assignment) {
      return new UnitApplicationVariable(getEngine(), graph, assignment.getUnit(), assignment);
   }

   public RuleApplicationVariable createApplication(final EGraph graph, final Match match) {
      return new RuleApplicationVariable(getEngine(), graph, match.getRule(), match);
   }

   public TransformationSolution createEmptyTransformationSolution() {
      final TransformationSolution solution = new TransformationSolution(getSearchOrchestration());
      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());
      return solution;
   }

   public TransformationSolution createEmptyTransformationSolution(final int solutionLength, final int nrObjectives,
         final int nrConstraints) {
      final TransformationSolution solution = new TransformationSolution(getSearchOrchestration().getProblemGraph(),
            solutionLength, nrObjectives, nrConstraints);
      for(int i = 0; i < solutionLength; i++) {
         solution.setVariable(i, new TransformationPlaceholderVariable());
      }
      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());
      return solution;
   }

   protected Match createFixedAssignment(final Unit rule, final Map<String, Object> valueMapping) {
      final Match assignment = InterpreterFactory.INSTANCE.createMatch((Rule) rule, false);

      if(assignment == null || assignment.getUnit() == null) {
         return assignment;
      }

      for(final Parameter parameter : assignment.getUnit().getParameters()) {
         final Object value = valueMapping.get(parameter.getName());
         if(value != null) {
            assignment.setParameterValue(parameter, value);
         }
      }

      return assignment;
   }

   protected Match createPartialAssignment(final Rule rule) {
      return getModuleManager().assignParameterValues(InterpreterFactory.INSTANCE.createMatch(rule, false));
   }

   protected Assignment createPartialAssignment(final Unit unit) {
      if(unit instanceof Rule) {
         return createPartialAssignment((Rule) unit);
      }
      return getModuleManager().assignParameterValues(InterpreterFactory.INSTANCE.createAssignment(unit, false));
   }

   public TransformationSolution createRandomTransformationSolution() {
      return createRandomTransformationSolution(getSearchOrchestration().getSolutionLength(),
            getSearchOrchestration().getNumberOfObjectives(), getSearchOrchestration().getNumberOfConstraints());
   }

   public TransformationSolution createRandomTransformationSolution(final int solutionLength, final int nrObjectives,
         final int nrConstraints) {

      final EGraph searchGraph = MomotUtil.copy(getSearchOrchestration().getProblemGraph());
      final TransformationSolution solution = createEmptyTransformationSolution(solutionLength, nrObjectives,
            nrConstraints);

      final List<ITransformationVariable> variables = new ArrayList<>();

      if(solutionLength >= 1) {
         ITransformationVariable variable = findUnitApplication(searchGraph);
         while(variable != null) {
            variable.execute(false);
            if(variable.isExecuted()) {
               variables.add(variable);
            }
            if(variables.size() >= solutionLength) {
               break;
            }
            variable = findUnitApplication(searchGraph);
         }
      }
      solution.setTransformation(variables, searchGraph);
      return solution;
   }

   // public TransformationSolution createRandomTransformationSolutionWithRuleStrategy(int solutionLength,
   // final int nrObjectives, final int nrConstraints) {
   // final EGraph searchGraph = MomotUtil.copy(getSearchOrchestration().getProblemGraph());
   // final TransformationSolution solution = createEmptyTransformationSolution(solutionLength, nrObjectives,
   // nrConstraints);
   //
   // final List<ITransformationVariable> variables = new ArrayList<>();
   //
   // while(solutionLength >= 1) {
   // final UnitParameter unitParameter = CollectionUtil.getRandomElement(this.getBaseRules());
   // ITransformationVariable variable = findFixedUnitApplication(searchGraph, unitParameter.getUnitName(),
   // unitParameter.getParameterValues());
   //
   // if(variable != null) {
   // variable.execute(false);
   //
   // if(variable.isExecuted()) {
   // variables.add(variable);
   // solutionLength--;
   // }
   // if(solutionLength < 1) {
   // break;
   // }
   // for(final UnitParameter postBaseUnitParameter : this.postBaseRules) {
   // variable = findFixedUnitApplication(searchGraph, postBaseUnitParameter.getUnitName(),
   // postBaseUnitParameter.getParameterValues());
   // if(variable == null) {
   // continue;
   // }
   // variable.execute(false);
   // if(variable.isExecuted()) {
   // variables.add(variable);
   // solutionLength--;
   //
   // }
   // }
   // if(variables.get(variables.size() - 1).getUnit().getName().compareTo("kill") == 0) {
   // break;
   // }
   //
   // }
   //
   // }
   //
   // solution.setTransformation(variables, searchGraph);
   // if(variables.get(variables.size() - 1).getUnit().getName().compareTo("kill") == 0) {
   // solution.setDone(true);
   // }
   //
   // return solution;
   // }

   public TransformationSolution createTransformationSolution(final EGraph sourceGraph,
         final List<? extends ITransformationVariable> variables, final int numberOfObjectives) {
      final TransformationSolution solution = new TransformationSolution(sourceGraph, variables, numberOfObjectives);
      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());
      return solution;
   }

   public TransformationSolution createTransformationSolution(final EGraph sourceGraph,
         final List<? extends ITransformationVariable> variables, final int numberOfObjectives,
         final int numberOfConstraints) {
      final TransformationSolution solution = new TransformationSolution(sourceGraph, variables, numberOfObjectives,
            numberOfConstraints);
      solution.setEqualityHelper(getSearchOrchestration().getEqualityHelper());
      return solution;
   }

   public ITransformationVariable findFixedUnitApplication(final EGraph sourceGraph, final String ruleName,
         final Map<String, Object> parameterValues) {

      final Unit chosenUnit = this.getModuleManager().getUnit(ruleName);

      for(final Map.Entry<String, Object> entry : parameterValues.entrySet()) {
         getModuleManager().setParameterValue(getModuleManager().getParameter(entry.getKey()),
               new FixValue<>(getModuleManager().getParameter(entry.getKey()), entry.getValue()));
      }

      final Assignment partialMatch = createPartialAssignment(chosenUnit);

      final Iterator<Match> foundMatches = getEngine().findMatches((Rule) chosenUnit, sourceGraph, (Match) partialMatch)
            .iterator();

      final Match match = foundMatches.next();

      if(chosenUnit instanceof Rule && match != null) {
         final RuleApplicationVariable application = createApplication(sourceGraph, match);
         if(application.execute(getMonitor())) {
            for(final Parameter param : chosenUnit.getParameters()) {
               application.setParameterValue(param, application.getResultParameterValue(param));
            }
            return clean(application);
         }
      }
      return null;
   }

   public ITransformationVariable findUnitApplication(final EGraph graph) {
      return findUnitApplication(graph, getMaxTriesPerUnit());
   }

   public ITransformationVariable findUnitApplication(final EGraph graph, final int maxTries) {
      // choose a unit randomly
      final List<? extends Unit> units = new ArrayList<>(getUnits());
      Unit chosenUnit = CollectionUtil.getRandomElement(units);
      final String n = chosenUnit.getName();

      // try to apply rule until match is found or maxRuleTries is reached
      int nrUnitTries = maxTries;

      while(chosenUnit != null) {
         // create assignment with user-defined parameter values
         final Assignment partialMatch = createPartialAssignment(chosenUnit);

         if(chosenUnit instanceof Rule) {
            // find matches

            final Iterator<Match> foundMatches = getEngine().findMatches((Rule) chosenUnit, graph, (Match) partialMatch)
                  .iterator();

            if(foundMatches != null && foundMatches.hasNext()) {
               // match found - break loop, return match
               final Match match = foundMatches.next();

               final RuleApplicationVariable application = createApplication(graph, match);

               if(application.execute(getMonitor())) {
                  for(final Parameter param : chosenUnit.getParameters()) {
                     application.setParameterValue(param, application.getResultParameterValue(param));
                  }
                  return clean(application);
               } else {
                  application.undo(getMonitor());
               }
            }
         } else {
            final UnitApplicationVariable application = createApplication(graph, partialMatch);
            if(application.execute(getMonitor())) {
               application.setAssignment(application.getResultAssignment());
               return clean(application);
            } else {
               application.undo(getMonitor());
            }
         }

         if(partialMatch.isEmpty()) {
            // no match found and no user-defined parameter values
            // -> further tries of this unit will yield same result
            nrUnitTries = 0; // skip further tries for this unit
         }

         if(--nrUnitTries <= 0) {
            // try other rule
            units.remove(chosenUnit); // don't try this rule again
            chosenUnit = CollectionUtil.getRandomElement(units);
            nrUnitTries = maxTries;
         }
      }
      return null; // no match found with the number of tries
   }

   public List<ITransformationVariable> findUnitApplications(final EGraph graph) {
      return findUnitApplications(graph, getMaxTriesPerUnit());
   }

   private List<ITransformationVariable> findUnitApplications(final EGraph graph, final int maxTries) {
      final List<ITransformationVariable> variables = new ArrayList<>();

      // choose a unit randomly
      final List<Unit> units = new ArrayList<>(getUnits());
      Unit chosenUnit = CollectionUtil.getRandomElement(units);

      // try to apply rule until match is found or maxRuleTries is reached
      int nrUnitTries = maxTries;

      while(chosenUnit != null) {
         // create assignment with user-defined parameter values

         final Assignment partialMatch = createPartialAssignment(chosenUnit);

         if(chosenUnit instanceof Rule) {
            // find matches
            final Iterator<Match> foundMatches = getEngine().findMatches((Rule) chosenUnit, graph, (Match) partialMatch)
                  .iterator();

            if(foundMatches != null && foundMatches.hasNext()) {
               // match found - break loop, return match
               final Match match = getModuleManager().clearNonSolutionParameters(foundMatches.next());
               variables.add(createApplication(graph, match));
            }
         } else {
            final UnitApplicationVariable application = createApplication(graph, partialMatch);
            if(application.execute(getMonitor())) {
               variables.add(application);
            }
         }

         if(partialMatch.isEmpty()) {
            // no match found and no user-defined parameter values
            // -> further tries of this unit will yield same result
            nrUnitTries = 0; // skip further tries for this unit
         }

         if(--nrUnitTries <= 0) {
            // try other rule
            units.remove(chosenUnit); // don't try this rule again
            chosenUnit = CollectionUtil.getRandomElement(units);
            nrUnitTries = maxTries;
         }
      }
      return variables;
   }

   public List<UnitParameter> getBaseRules() {
      return baseRules;
   }

   // public ITransformationVariable findUnitApplication2(final EGraph sourceGraph, final int ruleNr, final int stackNr,
   // final int attributeValue) {
   // // choose a unit randomly
   // final List<? extends Unit> units = new ArrayList<>(getUnits());
   // final Unit chosenUnit = units.get(ruleNr);
   //
   // final StackModel sm = MomotUtil.getRoot(sourceGraph, StackModel.class);
   //
   // // create assignment with user-defined parameter values
   // // final Assignment partialMatch = createPartialAssignment(chosenUnit);
   // final Assignment assignment = InterpreterFactory.INSTANCE.createAssignment(chosenUnit, false);
   //
   // final Stack fromStack = sm.getStacks().get(stackNr);
   // Stack toStack = null;
   // if(ruleNr == 0) {
   // toStack = sm.getStacks().get(stackNr).getLeft();
   // } else {
   // toStack = sm.getStacks().get(stackNr).getRight();
   //
   // }
   //
   // final Map<String, Object> test1 = new HashMap<>();
   //
   // test1.put("fromId", fromStack.getId());
   // test1.put("fromLoad", fromStack.getLoad());
   // test1.put("amount", attributeValue);
   // test1.put("toId", toStack.getId());
   // test1.put("toLoad", toStack.getLoad());
   //
   // for(final Parameter parameter : assignment.getUnit().getParameters()) {
   // assignment.setParameterValue(parameter, test1.get(parameter.getName()));
   // }
   //
   // if(chosenUnit instanceof Rule) {
   // final UnitApplicationVariable application = createApplication(sourceGraph, assignment);
   // if(application.execute(getMonitor())) {
   // for(final Parameter param : chosenUnit.getParameters()) {
   // application.setParameterValue(param, application.getResultParameterValue(param));
   // }
   // return clean(application);
   // }
   // }
   //
   // return null;
   // }

   public Engine getEngine() {

      return engine;
   }

   public int getMaxTriesPerUnit() {
      return maxTriesPerUnit;
   }

   public ModuleManager getModuleManager() {
      return getSearchOrchestration().getModuleManager();
   }

   public ApplicationMonitor getMonitor() {
      return monitor;
   }

   public List<UnitParameter> getPostBaseRules() {
      return postBaseRules;
   }

   protected List<Rule> getRules() {
      return getModuleManager().getRules();
   }

   public TransformationSearchOrchestration getSearchOrchestration() {
      return searchOrchestration;
   }

   protected List<? extends Unit> getUnits() {
      return getModuleManager().getUnits();
   }

   public void setBaseRules(final List<UnitParameter> baseRules) {
      this.baseRules = baseRules;
   }

   public void setEngine(final Engine engine) {
      this.engine = engine;
   }

   public void setMaxTriesPerUnit(final int maxTriesPerUnit) {
      this.maxTriesPerUnit = maxTriesPerUnit;
   }

   public void setMonitor(final ApplicationMonitor monitor) {
      this.monitor = monitor;
   }

   public void setPostBaseRules(final List<UnitParameter> postBaseRules) {
      this.postBaseRules = postBaseRules;
   }

   public void setSearchOrchestration(final TransformationSearchOrchestration searchOrchestration) {
      this.searchOrchestration = searchOrchestration;
   }
}
