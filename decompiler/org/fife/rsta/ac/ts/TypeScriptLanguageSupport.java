package org.fife.rsta.ac.ts;

import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;














public class TypeScriptLanguageSupport
  extends AbstractLanguageSupport
{
  private TypeScriptCompletionProvider provider = new TypeScriptCompletionProvider(this);


  
  private AutoCompletion createAutoCompletion() {
    AutoCompletion ac = new AutoCompletion((CompletionProvider)this.provider);
    return ac;
  }



  
  public void install(RSyntaxTextArea textArea) {
    AutoCompletion ac = createAutoCompletion();
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
  }

















  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
  }
}
