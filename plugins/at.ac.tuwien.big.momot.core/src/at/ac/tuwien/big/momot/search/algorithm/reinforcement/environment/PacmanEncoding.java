package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FixedRuleApplicationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.UnitParameter;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import PacmanGame.Game;
import PacmanGame.GridNode;
import PacmanGame.PositionableEntity;
import PacmanGame.impl.FoodImpl;
import PacmanGame.impl.GameImpl;
import PacmanGame.impl.GhostImpl;
import PacmanGame.impl.PacmanImpl;

public class PacmanEncoding<S extends Solution> extends AbstractProblemEncoder<S> {

   private static int[] oneHotNearFoodVector(final List<Point> foodPos, final Point pacPos) {
      final int pacY = (int) pacPos.getY();
      final int pacX = (int) pacPos.getX();

      final int[] oneHotFoodVector = new int[4];
      if(foodPos.contains(new Point(pacX, pacY - 1))) {
         oneHotFoodVector[0] = 1;
      }
      if(foodPos.contains(new Point(pacX + 1, pacY))) {
         oneHotFoodVector[1] = 1;
      }
      if(foodPos.contains(new Point(pacX, pacY + 1))) {
         oneHotFoodVector[2] = 1;
      }
      if(foodPos.contains(new Point(pacX - 1, pacY))) {
         oneHotFoodVector[3] = 1;
      }
      return oneHotFoodVector;
   }

   @Override
   public List<UnitParameter> createBaseRules() {
      final List<UnitParameter> baseRules = new ArrayList<>();
      Map<String, Object> parameterValues = new HashMap<>();

      parameterValues.put("move_up::moveUp::mover", "p1");
      baseRules.add(new UnitParameter("move_up::moveUp", parameterValues));

      parameterValues = new HashMap<>();
      parameterValues.put("move_right::moveRight::mover", "p1");
      baseRules.add(new UnitParameter("move_right::moveRight", parameterValues));

      parameterValues = new HashMap<>();
      parameterValues.put("move_down::moveDown::mover", "p1");
      baseRules.add(new UnitParameter("move_down::moveDown", parameterValues));

      parameterValues = new HashMap<>();
      parameterValues.put("move_left::moveLeft::mover", "p1");
      baseRules.add(new UnitParameter("move_left::moveLeft", parameterValues));

      return baseRules;
   }

   @Override
   public List<UnitParameter> createPostBaseRules() {
      final List<UnitParameter> postBaseRules = new ArrayList<>();
      final Map<String, Object> parameterValues = new HashMap<>();

      postBaseRules.add(new UnitParameter("eat::eat", parameterValues));
      postBaseRules.add(new UnitParameter("kill::kill", parameterValues));

      return postBaseRules;
   }

   @Override
   public double determineAdditionalReward(final S s) {
      final GameImpl game = MomotUtil.getRoot(((TransformationSolution) s).getResultGraph(), GameImpl.class);

      boolean foodLeft = false;
      for(final PositionableEntity entity : game.getEntites()) {
         if(entity instanceof FoodImpl) {
            foodLeft = true;
            break;
         }
      }
      if(!foodLeft) {
         return 150;
      }
      return 0;
   }

   @Override
   public INDArray encodeSolution(final S s) {
      final TransformationSolution solution = (TransformationSolution) s;
      INDArray encoding = null;

      final GameImpl game = MomotUtil.getRoot(solution.getResultGraph(), GameImpl.class);
      // Per gridnode of game, assign integer for node state
      // 0 = nothing, 1 = pacman, 2 = food, 3 = ghost
      encoding = Nd4j.zeros(1, game.getGridnodes().size() * 3 + 9);

      final Map<String, Integer> nodeStates = new HashMap<>();
      for(final GridNode g : game.getGridnodes()) {
         nodeStates.put(g.getId(), 0);
      }
      final Point pacPos = new Point();

      final List<Point> foodPos = new ArrayList<>();

      double foodPiecesAtStart = 0;

      final GameImpl startGame = MomotUtil.getRoot(solution.getSourceGraph(), GameImpl.class);

      for(final PositionableEntity entity : startGame.getEntites()) {
         if(entity instanceof FoodImpl) {
            foodPiecesAtStart++;
         }
      }

      for(final PositionableEntity entity : game.getEntites()) {
         if(entity instanceof PacmanImpl) {
            final String pacId = ((PacmanImpl) entity).getOn().getId();
            nodeStates.put(pacId, 1);

            pacPos.setLocation(Integer.parseInt(pacId.substring(1, 2)), Integer.parseInt(pacId.substring(0, 1)));

         } else if(entity instanceof FoodImpl) {
            final String foodId = ((FoodImpl) entity).getOn().getId();

            nodeStates.put(foodId, 2);

            foodPos.add(new Point(Integer.parseInt(foodId.substring(1, 2)), Integer.parseInt(foodId.substring(0, 1))));

         } else if(entity instanceof GhostImpl) {
            nodeStates.put(((GhostImpl) entity).getOn().getId(), 3);
         }
      }

      final List<String> sortedNodeIds = new ArrayList<>(nodeStates.keySet());

      int column = 0;
      for(final String nodeId : sortedNodeIds) {
         final int[] oneHotState = integerToOnehot(nodeStates.get(nodeId), 3);
         for(final int i : oneHotState) {
            encoding.put(0, column++, i);
         }
      }

      final int[] pacmanNearFoodVector = oneHotNearFoodVector(foodPos, pacPos);
      for(final double i : pacmanNearFoodVector) {
         encoding.put(0, column++, i);
      }

      final double[] pacmanFoodNearnessVector = foodDistanceVector(foodPos, pacPos, 7, 7);
      for(final double i : pacmanFoodNearnessVector) {
         encoding.put(0, column++, i);
      }
      final double foodProgress = (foodPiecesAtStart - foodPos.size()) / foodPiecesAtStart;

      encoding.putScalar(0, column, foodProgress);

      // System.out.println(Arrays.toString(pacmanFoodNearnessVector));

      return encoding;
   }

   private double[] foodDistanceVector(final List<Point> foodPos, final Point pacPos, final int boardWidth,
         final int boardHeight) {
      final int pacY = (int) pacPos.getY();
      final int pacX = (int) pacPos.getX();

      double yUpNearness = 0;
      double yDownNearness = 0;
      double xRightNearness = 0;
      double xLeftNearness = 0;

      for(final Point food : foodPos) {
         if(food.y == pacY) {
            if(food.x < pacX) {
               final double curXLeftXNearness = 1 - Math.abs(pacX - food.x) / (double) boardWidth;
               if(curXLeftXNearness > xLeftNearness) {
                  xLeftNearness = curXLeftXNearness;
               }
            } else if(food.x > pacX) {
               final double curXRightNearness = 1 - Math.abs(pacX - food.x) / (double) boardWidth;
               if(curXRightNearness > xRightNearness) {
                  xRightNearness = curXRightNearness;
               }
            }
         } else if(food.x == pacX) {
            if(food.y < pacY) {
               final double curYUpNearness = 1 - Math.abs(pacY - food.y) / (double) boardHeight;

               if(curYUpNearness > yUpNearness) {
                  yUpNearness = curYUpNearness;
               }
            } else if(food.y > pacY) {
               final double curYDownNearness = 1 - Math.abs(pacY - food.y) / (double) boardHeight;
               if(curYDownNearness > yDownNearness) {
                  yDownNearness = curYDownNearness;
               }
            }
         }

      }

      final double[] nearnessVector = new double[4];
      nearnessVector[0] = yUpNearness;
      nearnessVector[1] = xRightNearness;
      nearnessVector[2] = yDownNearness;
      nearnessVector[3] = xLeftNearness;

      return nearnessVector;
   }

   @Override
   public int getActionSpace(final S initialSolution) {
      return 4;
   }

   @Override
   public List<String> getEpisodeEndingRules() {
      final List<String> endingRules = new ArrayList<>();
      endingRules.add(new String("kill"));
      return endingRules;
   }

   @Override
   public FixedRuleApplicationStrategy getFixedUnitApplicationStrategy(final S s, final int action) {
      final FixedRuleApplicationStrategy applicationStrategy = new FixedRuleApplicationStrategy();
      String moveUnitName = null;
      final Map<String, Object> parameterValues = new HashMap<>();
      UnitParameter moveAction = null;

      // 0 = up, 1 = right, 2 = down, 3 = left

      // 1. main role (move step in one direction)
      moveUnitName = null;

      switch(action) {
         case 0:
            moveUnitName = "move_up::moveUp";
            parameterValues.put("move_up::moveUp::mover", "p1"); // p1 = pacman
            break;
         case 1:
            moveUnitName = "move_right::moveRight";
            parameterValues.put("move_right::moveRight::mover", "p1"); // p1 = pacman
            break;
         case 2:
            moveUnitName = "move_down::moveDown";
            parameterValues.put("move_down::moveDown::mover", "p1"); // p1 = pacman
            break;
         case 3:
            moveUnitName = "move_left::moveLeft";
            parameterValues.put("move_left::moveLeft::mover", "p1"); // p1 = pacman
            break;

      }
      moveAction = new UnitParameter(moveUnitName, parameterValues);
      applicationStrategy.setDistributionSampleRule(moveAction);

      // Subsequent optional rules: Check if can apply rule "eat"
      final List<UnitParameter> optionalSubsequentRules = new ArrayList<>();
      final Map<String, Object> subseqRuleParameterValues = new HashMap<>();
      // subseqRuleParameterValues.put("score_value", "1");
      optionalSubsequentRules.add(new UnitParameter("eat::eat", subseqRuleParameterValues));
      optionalSubsequentRules.add(new UnitParameter("kill::kill", subseqRuleParameterValues));

      applicationStrategy.setOptionalSubsequentRules(optionalSubsequentRules);

      return applicationStrategy;

   }

   @Override
   public Map<String, Double> getRewardMap() {
      final Map<String, Double> rewardMap = new HashMap<>();
      rewardMap.put("eat", 30.0);
      rewardMap.put("kill", -150.0);
      rewardMap.put("moveUp", -5.0);
      rewardMap.put("moveRight", -5.0);
      rewardMap.put("moveLeft", -5.0);
      rewardMap.put("moveDown", -5.0);
      return rewardMap;
   }

   @Override
   public int getStateSpace(final S initialSolution) {
      final TransformationSolution solution = (TransformationSolution) initialSolution;
      final Game game = MomotUtil.getRoot(solution.execute(), Game.class);
      return game.getGridnodes().size() * 3 + 9;
   }

}
