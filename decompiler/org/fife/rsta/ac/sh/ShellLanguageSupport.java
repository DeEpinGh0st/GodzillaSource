package org.fife.rsta.ac.sh;

import java.io.File;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;


























public class ShellLanguageSupport
  extends AbstractLanguageSupport
{
  private ShellCompletionProvider provider;
  private boolean useLocalManPages;
  
  public ShellLanguageSupport() {
    setParameterAssistanceEnabled(false);
    setShowDescWindow(true);
    setShowDescWindow(true);
    setAutoCompleteEnabled(true);
    setAutoActivationEnabled(true);
    setAutoActivationDelay(800);
    this.useLocalManPages = (File.separatorChar == '/');
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new CompletionCellRenderer();
  }






  
  private ShellCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new ShellCompletionProvider();
      ShellCompletionProvider.setUseLocalManPages(getUseLocalManPages());
    } 
    return this.provider;
  }










  
  public boolean getUseLocalManPages() {
    return this.useLocalManPages;
  }






  
  public void install(RSyntaxTextArea textArea) {
    ShellCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    
    textArea.setToolTipSupplier((ToolTipSupplier)provider);
  }











  
  public void setUseLocalManPages(boolean use) {
    if (use != this.useLocalManPages) {
      this.useLocalManPages = use;
      if (this.provider != null) {
        ShellCompletionProvider.setUseLocalManPages(this.useLocalManPages);
      }
    } 
  }






  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
  }
}
