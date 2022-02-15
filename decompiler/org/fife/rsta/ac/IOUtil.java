package org.fife.rsta.ac;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



































public class IOUtil
{
  private static Map<String, String> DEFAULT_ENV;
  
  private static Map<String, String> getDefaultEnvMap() {
    if (DEFAULT_ENV != null) {
      return DEFAULT_ENV;
    }

    
    try {
      DEFAULT_ENV = System.getenv();
    } catch (SecurityException e) {
      DEFAULT_ENV = Collections.emptyMap();
    } 
    
    return DEFAULT_ENV;
  }











  
  public static String getEnvSafely(String var) {
    String value = null;
    try {
      value = System.getenv(var);
    } catch (SecurityException securityException) {}

    
    return value;
  }














  
  public static String[] getEnvironmentSafely(String[] toAdd) {
    Map<String, String> env = getDefaultEnvMap();

    
    if (toAdd != null) {
      Map<String, String> temp = new HashMap<>(env);
      for (int j = 0; j < toAdd.length; j += 2) {
        temp.put(toAdd[j], toAdd[j + 1]);
      }
      env = temp;
    } 

    
    int count = env.size();
    String[] vars = new String[count];
    int i = 0;
    for (Map.Entry<String, String> entry : env.entrySet()) {
      vars[i++] = (String)entry.getKey() + "=" + (String)entry.getValue();
    }
    
    return vars;
  }
















  
  public static int waitForProcess(Process p, StringBuilder stdout, StringBuilder stderr) throws IOException {
    InputStream in = p.getInputStream();
    InputStream err = p.getErrorStream();
    Thread t1 = new Thread(new OutputCollector(in, stdout));
    Thread t2 = new Thread(new OutputCollector(err, stderr));
    t1.start();
    t2.start();
    int rc = -1;
    
    try {
      rc = p.waitFor();
      t1.join();
      t2.join();
    } catch (InterruptedException ie) {
      p.destroy();
    } finally {
      in.close();
      err.close();
    } 
    
    return rc;
  }







  
  public static void main(String[] args) {
    for (String arg : args) {
      String value = getEnvSafely(arg);
      System.out.println(arg + "=" + value);
    } 
  }
}
