package org.fife.rsta.ac.js.completion;

import org.fife.rsta.ac.java.JavaTemplateCompletion;
import org.fife.ui.autocomplete.CompletionProvider;



















public class JavaScriptTemplateCompletion
  extends JavaTemplateCompletion
{
  public JavaScriptTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template) {
    this(provider, inputText, definitionString, template, null);
  }



  
  public JavaScriptTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String shortDesc) {
    this(provider, inputText, definitionString, template, shortDesc, null);
  }


  
  public JavaScriptTemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String shortDesc, String summary) {
    super(provider, inputText, definitionString, template, shortDesc, summary);
  }
}
