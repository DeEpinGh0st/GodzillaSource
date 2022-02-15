package org.fife.rsta.ac.css;

import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;






















public class CssLanguageSupport
  extends AbstractLanguageSupport
{
  private CssCompletionProvider provider;
  
  public CssLanguageSupport() {
    setAutoActivationEnabled(true);
    setAutoActivationDelay(500);
    setParameterAssistanceEnabled(true);
  }



  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new CssCellRenderer();
  }







  
  protected CssCompletionProvider createProvider() {
    return new CssCompletionProvider();
  }

  
  private CssCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = createProvider();
    }
    return this.provider;
  }






  
  public void install(RSyntaxTextArea textArea) {
    CssCompletionProvider provider = getProvider();
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
