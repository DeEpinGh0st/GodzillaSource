package org.fife.rsta.ac.c;

import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;






















public class CLanguageSupport
  extends AbstractLanguageSupport
{
  private CCompletionProvider provider;
  
  public CLanguageSupport() {
    setShowDescWindow(true);
    setAutoCompleteEnabled(true);
    setAutoActivationEnabled(true);
    setAutoActivationDelay(800);
    setParameterAssistanceEnabled(true);
    setShowDescWindow(true);
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new CCellRenderer();
  }

  
  private CCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new CCompletionProvider();
    }
    return this.provider;
  }






  
  public void install(RSyntaxTextArea textArea) {
    CCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    
    textArea.setToolTipSupplier((ToolTipSupplier)provider);
  }






  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
    textArea.setToolTipSupplier(null);
  }
}
