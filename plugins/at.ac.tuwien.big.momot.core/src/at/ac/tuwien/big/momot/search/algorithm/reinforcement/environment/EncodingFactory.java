package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IProblemEncoder;

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.core.Solution;

public class EncodingFactory<S extends Solution, E extends IProblemEncoder<S>> {

   public static <S extends Solution, E extends IProblemEncoder<S>> IProblemEncoder<S> createEncoder(final Class<E> c) {
      try {
         return c.getDeclaredConstructor().newInstance();
      } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e) {
         e.printStackTrace();
      }
      return null;
   }

}
