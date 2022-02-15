package org.fife.rsta.ac.sh;

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;







































public class ShellCompletionProvider
  extends CCompletionProvider
{
  private static boolean useLocalManPages;
  
  protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {}
  
  protected CompletionProvider createStringCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.setAutoActivationRules(true, null);
    return (CompletionProvider)cp;
  }





  
  public char getParameterListEnd() {
    return Character.MIN_VALUE;
  }





  
  public char getParameterListStart() {
    return Character.MIN_VALUE;
  }










  
  public static boolean getUseLocalManPages() {
    return useLocalManPages;
  }





  
  protected String getXmlResource() {
    return "data/sh.xml";
  }










  
  public static void setUseLocalManPages(boolean use) {
    useLocalManPages = use;
  }
}
