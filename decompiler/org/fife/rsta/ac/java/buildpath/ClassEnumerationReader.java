package org.fife.rsta.ac.java.buildpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

























































public class ClassEnumerationReader
{
  public static List<String> getClassNames(InputStream in) throws IOException {
    String lastPkg = null;
    
    List<String> classNames = new ArrayList<>();
    
    try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
      String line;
      while ((line = r.readLine()) != null) {

        
        line = line.trim();
        if (line.length() == 0 || line.charAt(0) == '#') {
          continue;
        }

        
        if (line.charAt(0) == '-') {
          line = line.substring(1).trim();
          classNames.add(line);
          int lastDot = line.lastIndexOf('.');
          lastPkg = line.substring(0, lastDot + 1);
          
          continue;
        } 
        
        String className = line;
        if (lastPkg != null) {
          className = lastPkg + className;
        }
        classNames.add(className);
      } 
    } 



    
    return classNames;
  }
}
