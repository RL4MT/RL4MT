// package at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm;
//
// import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.INetworkIOModelEncoder;
// import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.ProblemType;
// import at.ac.tuwien.big.momot.examples.stack.stack.StackModel;
// import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
// import at.ac.tuwien.big.momot.util.MomotUtil;
//
// import java.awt.Point;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
//
// import org.moeaframework.core.Solution;
// import org.nd4j.linalg.api.ndarray.INDArray;
// import org.nd4j.linalg.factory.Nd4j;
//
// import PacmanGame.Game;
// import PacmanGame.GridNode;
// import PacmanGame.PositionableEntity;
// import PacmanGame.impl.FoodImpl;
// import PacmanGame.impl.GameImpl;
// import PacmanGame.impl.GhostImpl;
// import PacmanGame.impl.PacmanImpl;
// import icmt.tool.momot.demo.architecture.ClassModel;
//
// public class NetworkIOModelEncoder<S extends Solution> implements INetworkIOModelEncoder<S> {
//
// private static int[] integerToOnehot(final int integer, final int bits) {
// final int[] state = new int[bits];
// for(int i = 1; i <= bits; i++) {
// if(i == integer) {
// state[bits - i] = 1;
// } else {
// state[bits - i] = 0;
// }
// }
//
// return state;
// }
//
// private static int[] oneHotNearFoodVector(final List<Point> foodPos, final Point pacPos) {
// final int pacY = (int) pacPos.getY();
// final int pacX = (int) pacPos.getX();
//
// final int[] oneHotFoodVector = new int[4];
// if(foodPos.contains(new Point(pacX, pacY - 1))) {
// oneHotFoodVector[0] = 1;
// }
// if(foodPos.contains(new Point(pacX + 1, pacY))) {
// oneHotFoodVector[1] = 1;
// }
// if(foodPos.contains(new Point(pacX, pacY + 1))) {
// oneHotFoodVector[2] = 1;
// }
// if(foodPos.contains(new Point(pacX - 1, pacY))) {
// oneHotFoodVector[3] = 1;
// }
// return oneHotFoodVector;
// }
//
// @Override
// public INDArray encodeSolution(final ProblemType problemType, final Solution s) {
// final TransformationSolution solution = (TransformationSolution) s;
// INDArray encoding = null;
//
// switch(problemType) {
// case PACMAN_GRID:
// final GameImpl game = MomotUtil.getRoot(solution.getResultGraph(), GameImpl.class);
// // Per gridnode of game, assign integer for node state
// // 0 = nothing, 1 = pacman, 2 = food, 3 = ghost
// encoding = Nd4j.zeros(1, game.getGridnodes().size() * 3 + 9);
//
// final Map<String, Integer> nodeStates = new HashMap<>();
// for(final GridNode g : game.getGridnodes()) {
// nodeStates.put(g.getId(), 0);
// }
// final Point pacPos = new Point();
//
// final List<Point> foodPos = new ArrayList<>();
//
// double foodCnt = 0;
//
// double foodPiecesAtStart = 0;
//
// final GameImpl startGame = MomotUtil.getRoot(solution.getSourceGraph(), GameImpl.class);
//
// for(final PositionableEntity entity : startGame.getEntites()) {
// if(entity instanceof FoodImpl) {
// foodPiecesAtStart++;
// }
// }
//
// for(final PositionableEntity entity : game.getEntites()) {
// if(entity instanceof PacmanImpl) {
// final String pacId = ((PacmanImpl) entity).getOn().getId();
// nodeStates.put(pacId, 1);
//
// pacPos.setLocation(Integer.parseInt(pacId.substring(1, 2)), Integer.parseInt(pacId.substring(0, 1)));
//
// } else if(entity instanceof FoodImpl) {
// final String foodId = ((FoodImpl) entity).getOn().getId();
//
// nodeStates.put(foodId, 2);
//
// foodPos.add(
// new Point(Integer.parseInt(foodId.substring(1, 2)), Integer.parseInt(foodId.substring(0, 1))));
//
// foodCnt++;
// } else if(entity instanceof GhostImpl) {
// nodeStates.put(((GhostImpl) entity).getOn().getId(), 3);
// }
// }
//
// // for(final int[] element : foodPos) {
// // final double distance = Math.abs(element[0] - pacPos[0]) + Math.abs(element[1] - pacPos[1]);
// // if(distance < minDistance) {
// // minDistance = distance;
// // }
// // }
//
// final List<String> sortedNodeIds = new ArrayList<>(nodeStates.keySet());
//
// int column = 0;
// for(final String nodeId : sortedNodeIds) {
// final int[] oneHotState = integerToOnehot(nodeStates.get(nodeId), 3);
// for(final int i : oneHotState) {
// encoding.put(0, column++, i);
// }
// }
//
// final int[] pacmanNearFoodVector = oneHotNearFoodVector(foodPos, pacPos);
// for(final double i : pacmanNearFoodVector) {
// encoding.put(0, column++, i);
// }
//
// // if(minDistance == 1) {
// // encoding.putScalar(0, column, 1);
// //
// // } else {
// // encoding.putScalar(0, column, 0);
// //
// // }
// // System.out.println(Arrays.toString(pacmanNearFoodVector));
//
// final double[] pacmanFoodNearnessVector = foodDistanceVector(foodPos, pacPos, 7, 7);
// for(final double i : pacmanFoodNearnessVector) {
// encoding.put(0, column++, i);
// }
// final double foodProgress = (foodPiecesAtStart - foodPos.size()) / foodPiecesAtStart;
//
// encoding.putScalar(0, column, foodProgress);
//
// // System.out.println(Arrays.toString(pacmanFoodNearnessVector));
//
// return encoding;
// case CRA:
// final ClassModel cm = MomotUtil.getRoot(solution.execute(), ClassModel.class);
// encoding = Nd4j.zeros(1, cm.getClasses().size() * 2);
//
// int externalDependencies = 0;
// int internalDependencies = 0;
// int i = 0;
// for(final icmt.tool.momot.demo.architecture.Class c : cm.getClasses()) {
// externalDependencies = 0;
// internalDependencies = 0;
//
// for(final icmt.tool.momot.demo.architecture.Feature feature : c.getEncapsulates()) {
// if(feature instanceof Method) {
// final Method m = (Method) feature;
// for(final Method m2 : m.getFunctionalDependency()) {
// if(m2.getIsEncapsulatedBy().equals(c)) {
// internalDependencies++;
// } else {
// externalDependencies++;
// }
// }
// for(final Attribute a2 : m.getDataDependency()) {
// if(a2.getIsEncapsulatedBy().equals(c)) {
// internalDependencies++;
// } else {
// externalDependencies++;
// }
// }
// }
// }
// double normalize = externalDependencies + internalDependencies;
// if(normalize == 0) {
// normalize = 1;
// }
// encoding.putScalar(0, i++, externalDependencies / normalize);
// encoding.putScalar(0, i++, internalDependencies / normalize);
//
// }
//
// return encoding;
// case STACK:
// final StackModel sm = MomotUtil.getRoot(((TransformationSolution) s).execute(), StackModel.class);
//
// encoding = Nd4j.zeros(1, sm.getStacks().size());
//
// i = 0;
// for(final Stack stack : sm.getStacks()) {
// encoding.putScalar(0, i++, stack.getLoad());
// }
//
// return encoding;
// }
//
// return null;
//
// /*
// * Map<String,Serializable> entries= new HashMap<>();
// * for(Entry<K,V> entry : entries.entrySet()){
// * // you can get key by entry.getKey() and value by entry.getValue()
// * // or set new value by entry.setValue(V value)
// * }
// */
// }
//
// private double[] foodDistanceVector(final List<Point> foodPos, final Point pacPos, final int boardWidth,
// final int boardHeight) {
// final int pacY = (int) pacPos.getY();
// final int pacX = (int) pacPos.getX();
//
// double yUpNearness = 0;
// double yDownNearness = 0;
// double xRightNearness = 0;
// double xLeftNearness = 0;
//
// for(final Point food : foodPos) {
// if(food.y == pacY) {
// if(food.x < pacX) {
// final double curXLeftXNearness = 1 - Math.abs(pacX - food.x) / (double) boardWidth;
// if(curXLeftXNearness > xLeftNearness) {
// xLeftNearness = curXLeftXNearness;
// }
// } else if(food.x > pacX) {
// final double curXRightNearness = 1 - Math.abs(pacX - food.x) / (double) boardWidth;
// if(curXRightNearness > xRightNearness) {
// xRightNearness = curXRightNearness;
// }
// }
// } else if(food.x == pacX) {
// if(food.y < pacY) {
// final double curYUpNearness = 1 - Math.abs(pacY - food.y) / (double) boardHeight;
//
// if(curYUpNearness > yUpNearness) {
// yUpNearness = curYUpNearness;
// }
// } else if(food.y > pacY) {
// final double curYDownNearness = 1 - Math.abs(pacY - food.y) / (double) boardHeight;
// if(curYDownNearness > yDownNearness) {
// yDownNearness = curYDownNearness;
// }
// }
// }
//
// }
//
// final double[] nearnessVector = new double[4];
// nearnessVector[0] = yUpNearness;
// nearnessVector[1] = xRightNearness;
// nearnessVector[2] = yDownNearness;
// nearnessVector[3] = xLeftNearness;
//
// return nearnessVector;
// }
//
// @Override
// public int getActionSpace(final ProblemType problemType, final Solution initialSolution) {
// final TransformationSolution solution = (TransformationSolution) initialSolution;
// switch(problemType) {
// case PACMAN_GRID:
// // 4 actions; move up/left/right/down
// return 4;
//
// case CRA:
// // as many actions as features^2 => One class per feature exists,
// // so each feature can be assigned to nrOfFeatures classes => features^2 options
// // by including assignment of feature to same class as currently is
// final ClassModel cm = MomotUtil.getRoot(solution.execute(), ClassModel.class);
// return (int) Math.pow(cm.getFeatures().size(), 2);
// case STACK:
// final StackModel sm = MomotUtil.getRoot(solution.execute(), StackModel.class);
// // Per stack 10 possibilities, shiftLeft or shiftRight with shift amount in [1,5]
// return sm.getStacks().size() * 10;
//
// }
//
// return 0;
// }
//
// @Override
// public int getStateSpace(final ProblemType problemType, final Solution initialSolution) {
// final TransformationSolution solution = (TransformationSolution) initialSolution;
// switch(problemType) {
// case PACMAN_GRID:
// // final GameImpl sourceGame = (GameImpl) solution.getSourceGraph().getRoots().get(0);
// final Game game = MomotUtil.getRoot(solution.execute(), Game.class);
// return game.getGridnodes().size() * 3 + 9;
//
// case CRA:
// final ClassModel cm = MomotUtil.getRoot(solution.execute(), ClassModel.class);
// return cm.getClasses().size() * 2;
//
// case STACK:
// final StackModel sm = MomotUtil.getRoot(solution.execute(), StackModel.class);
// // Per stack one input with the current load on stack
// return sm.getStacks().size();
// }
//
// return 0;
// }
//
// }
