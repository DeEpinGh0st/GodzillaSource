package org.apache.log4j;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import org.apache.log4j.spi.ThrowableRenderer;


































public final class DefaultThrowableRenderer
  implements ThrowableRenderer
{
  public String[] doRender(Throwable throwable) {
    return render(throwable);
  }





  
  public static String[] render(Throwable throwable) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    try {
      throwable.printStackTrace(pw);
    } catch (RuntimeException ex) {}
    
    pw.flush();
    LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
    
    ArrayList lines = new ArrayList();
    try {
      String line = reader.readLine();
      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      } 
    } catch (IOException ex) {
      if (ex instanceof java.io.InterruptedIOException) {
        Thread.currentThread().interrupt();
      }
      lines.add(ex.toString());
    } 
    String[] tempRep = new String[lines.size()];
    lines.toArray(tempRep);
    return tempRep;
  }
}
