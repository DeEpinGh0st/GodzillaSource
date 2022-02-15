package org.fife.rsta.ac.groovy;

import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;























public class GroovyLanguageSupport
  extends AbstractLanguageSupport
{
  private GroovyCompletionProvider provider;
  
  public GroovyLanguageSupport() {
    setParameterAssistanceEnabled(true);
    setShowDescWindow(true);
  }









  
  private GroovyCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new GroovyCompletionProvider();
    }
    return this.provider;
  }






  
  public void install(RSyntaxTextArea textArea) {
    GroovyCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    
    textArea.setToolTipSupplier((ToolTipSupplier)provider);
  }






  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
  }
}
