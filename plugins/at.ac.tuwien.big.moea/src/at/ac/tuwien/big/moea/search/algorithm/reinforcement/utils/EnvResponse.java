package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import org.moeaframework.core.Solution;

public class EnvResponse<S extends Solution> {
   private S state;
   private double reward;
   private boolean done;
   private int appliedActionId;

   public int getAppliedAction() {
      return appliedActionId;
   }

   public int getAppliedActionId() {
      return appliedActionId;
   }

   public double getReward() {
      return reward;
   }

   public S getState() {
      return state;
   }

   public boolean isDone() {
      return done;
   }

   public void setAppliedActionId(final int appliedActionId) {
      this.appliedActionId = appliedActionId;
   }

   public void setDone(final boolean done) {
      this.done = done;
   }

   public void setReward(final double reward) {
      this.reward = reward;
   }

   public void setState(final S state) {
      this.state = state;
   }

}
