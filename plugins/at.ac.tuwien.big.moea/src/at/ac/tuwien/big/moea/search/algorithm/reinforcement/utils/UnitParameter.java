package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import java.util.Map;

public class UnitParameter {
   private String unitName;
   private Map<String, Object> parameterValues;

   public UnitParameter(final String unitName, final Map<String, Object> parameterValues) {
      this.unitName = unitName;
      this.parameterValues = parameterValues;
   }

   public Map<String, Object> getParameterValues() {
      return parameterValues;
   }

   public String getUnitName() {
      return unitName;
   }

   public void setParameterValues(final Map<String, Object> parameterValues) {
      this.parameterValues = parameterValues;
   }

   public void setUnit(final String unitName) {
      this.unitName = unitName;
   }

}
