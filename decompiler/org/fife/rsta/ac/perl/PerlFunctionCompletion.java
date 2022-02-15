package org.fife.rsta.ac.perl;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;



























public class PerlFunctionCompletion
  extends FunctionCompletion
{
  public PerlFunctionCompletion(CompletionProvider provider, String name, String returnType) {
    super(provider, name, returnType);
  }






  
  public String getSummary() {
    String summary = null;
    File installLoc = PerlLanguageSupport.getPerlInstallLocation();
    if (installLoc != null && PerlLanguageSupport.getUseSystemPerldoc()) {
      summary = getSummaryFromPerldoc(installLoc);
    }
    
    if (summary == null) {
      summary = super.getSummary();
    }
    
    return summary;
  }










  
  private String getSummaryFromPerldoc(File installLoc) {
    Process p;
    String fileName = "bin/perldoc";
    if (File.separatorChar == '\\') {
      fileName = fileName + ".bat";
    }
    File perldoc = new File(installLoc, fileName);
    if (!perldoc.isFile()) {
      return null;
    }
    
    String[] cmd = { perldoc.getAbsolutePath(), "-f", getName() };
    try {
      p = Runtime.getRuntime().exec(cmd);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return null;
    } 

    
    OutputCollector oc = new OutputCollector(p.getInputStream());
    Thread t = new Thread((Runnable)oc);
    t.start();
    int rc = 0;
    try {
      rc = p.waitFor();
      t.join();
    }
    catch (InterruptedException ie) {
      ie.printStackTrace();
    } 
    
    CharSequence output = null;
    if (rc == 0) {
      output = oc.getOutput();
      if (output != null && output.length() > 0) {
        output = perldocToHtml(output);
      }
    } 
    
    return (output == null) ? null : output.toString();
  }




  
  private static StringBuilder perldocToHtml(CharSequence text) {
    StringBuilder sb;
    Font font = UIManager.getFont("Label.font");
    
    if (font != null) {
      
      sb = (new StringBuilder("<html><style>pre { font-family: ")).append(font.getFamily()).append("; }</style><pre>");
    } else {
      
      sb = new StringBuilder("<html><pre>");
    } 
    
    sb.append(text);
    return sb;
  }
}
