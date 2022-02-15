package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;




public class JavaScriptShorthandCompletion
  extends ShorthandCompletion
  implements JSCompletionUI
{
  private static final String PREFIX = "<html><nobr>";
  
  public JavaScriptShorthandCompletion(CompletionProvider provider, String inputText, String replacementText) {
    super(provider, inputText, replacementText);
  }


  
  public JavaScriptShorthandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc) {
    super(provider, inputText, replacementText, shortDesc);
  }



  
  public JavaScriptShorthandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc, String summary) {
    super(provider, inputText, replacementText, shortDesc, summary);
  }


  
  public Icon getIcon() {
    return IconFactory.getIcon("template");
  }


  
  public int getRelevance() {
    return 0;
  }

  
  public String getShortDescriptionText() {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(getInputText());
    sb.append(" - ");
    sb.append(getShortDescription());
    return sb.toString();
  }
}
