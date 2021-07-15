package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import java.util.ArrayList;
import java.util.List;

public class FixedRuleApplicationStrategy {
   private UnitParameter distributionSampleRule;
   private List<UnitParameter> optionalSubsequentRules;

   public FixedRuleApplicationStrategy() {
      this.optionalSubsequentRules = new ArrayList<UnitParameter>();
   }

   public UnitParameter getDistributionSampleRule() {
      return distributionSampleRule;
   }

   public List<UnitParameter> getOptionalSubsequentRules() {
      return optionalSubsequentRules;
   }

   public void setDistributionSampleRule(final UnitParameter distributionSampleRule) {
      this.distributionSampleRule = distributionSampleRule;
   }

   public void setOptionalSubsequentRules(final List<UnitParameter> optionalSubsequentRules) {
      this.optionalSubsequentRules = optionalSubsequentRules;
   }

}
