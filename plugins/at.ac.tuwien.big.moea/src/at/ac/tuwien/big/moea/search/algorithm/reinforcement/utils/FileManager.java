package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FileManager {

   private static boolean checkFileExists(final String path) {
      final File f = new File(path);
      if(!f.exists()) {
         return false;
      }

      return true;
   }

   public static List<Double> retreiveColumnFullCSV(final String path, final int column) throws Exception {

      final List<String> lines = IOUtils.readLines(new FileInputStream(path), StandardCharsets.UTF_8);
      final List<Double> columns = new ArrayList<>();

      for(int i = 1; i < lines.size(); i++) {

         final Double d = Double.valueOf(lines.get(i).split(",")[column]);

         columns.add(d);

      }

      return columns;
   }

   public static void rewriteFullCSV(final String path, final String vals) throws Exception {

      final FileWriter filewriter = new FileWriter(path);

      filewriter.append(vals);

      filewriter.flush();
      filewriter.close();

   }

   // framecoutn // reward
   public static void saveBenchMark(final String header, final ArrayList<ArrayList<Double>> data, final String path) {

      final StringBuilder sb = new StringBuilder(header + "\n");

      for(int j = 0; j < data.get(0).size(); j++) {
         for(final ArrayList<Double> element : data) {
            sb.append(element.get(j)).append(";");
         }

         sb.setLength(sb.length() - 1);
         sb.append("\n");

      }
      try {
         rewriteFullCSV(path, sb.toString());
      } catch(final Exception e) {
         e.printStackTrace();
      }

   }

}
