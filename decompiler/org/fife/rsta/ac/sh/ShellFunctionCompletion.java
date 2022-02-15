package org.fife.rsta.ac.sh;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;



























public class ShellFunctionCompletion
  extends FunctionCompletion
{
  public ShellFunctionCompletion(CompletionProvider provider, String name, String returnType) {
    super(provider, name, returnType);
  }






  
  public String getSummary() {
    String summary = null;
    if (ShellCompletionProvider.getUseLocalManPages()) {
      summary = getSummaryFromManPage();
    }
    
    if (summary == null) {
      summary = super.getSummary();
    }
    
    return summary;
  }









  
  private String getSummaryFromManPage() {
    Process p;
    String[] cmd = { "/usr/bin/man", getName() };
    try {
      p = Runtime.getRuntime().exec(cmd);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return null;
    } 

    
    OutputCollector stdout = new OutputCollector(p.getInputStream());
    Thread t = new Thread((Runnable)stdout);
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
      output = stdout.getOutput();
      if (output != null && output.length() > 0) {
        output = manToHtml(output);
      }
    } 
    
    return (output == null) ? null : output.toString();
  }



  
  private static final StringBuffer manToHtml(CharSequence text) {
    Pattern p = Pattern.compile("(?:_\\010.)+|(?:(.)\\010\\1)+");
    Matcher m = p.matcher(text);
    StringBuffer sb = new StringBuffer("<html><pre>");
    while (m.find()) {
      System.out.println("... found '" + m.group() + "'");
      String group = m.group();
      if (group.startsWith("_")) {
        sb.append("<u>");
        String str = group.replaceAll("_\\010", "");
        str = quoteReplacement(str);
        m.appendReplacement(sb, str);
        System.out.println("--- '" + str);
        sb.append("</u>");
        continue;
      } 
      String replacement = group.replaceAll(".\\010.", "");
      replacement = quoteReplacement(replacement);
      m.appendReplacement(sb, replacement);
      System.out.println("--- '" + replacement);
    } 
    
    m.appendTail(sb);
    
    return sb;
  }

  
  private static String quoteReplacement(String text) {
    if (text.indexOf('$') > -1 || text.indexOf('\\') > -1) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
        char ch = text.charAt(i);
        if (ch == '$' || ch == '\\') {
          sb.append('\\');
        }
        sb.append(ch);
      } 
      text = sb.toString();
    } 
    return text;
  }
}
