package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.ProblemType;

import java.util.HashMap;
import java.util.Map;

public class RewardFunction {

   protected static Map<String, Double> getRewardMap(final ProblemType problemType) {
      final Map<String, Double> rewardMap = new HashMap<>();

      switch(problemType) {
         case PACMAN_GRID:
            rewardMap.put("eat", 30.0);
            rewardMap.put("kill", -150.0);
            rewardMap.put("moveUp", -5.0);
            rewardMap.put("moveRight", -5.0);
            rewardMap.put("moveLeft", -5.0);
            rewardMap.put("moveDown", -5.0);
            break;
         default:
            return null;
      }
      return rewardMap;
   }
}
