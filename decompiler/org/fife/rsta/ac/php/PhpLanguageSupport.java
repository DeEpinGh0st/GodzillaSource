package org.fife.rsta.ac.php;

import java.util.HashSet;
import java.util.Set;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.html.HtmlLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;





























public class PhpLanguageSupport
  extends AbstractMarkupLanguageSupport
{
  private PhpCompletionProvider provider;
  private static Set<String> tagsToClose = new HashSet<>();




  
  public PhpLanguageSupport() {
    setAutoActivationEnabled(true);
    setParameterAssistanceEnabled(true);
    setShowDescWindow(true);
    setAutoActivationDelay(800);
    tagsToClose = HtmlLanguageSupport.getTagsToClose();
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new HtmlCellRenderer();
  }






  
  private PhpCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new PhpCompletionProvider();
    }
    return this.provider;
  }






  
  public void install(RSyntaxTextArea textArea) {
    PhpCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    installKeyboardShortcuts(textArea);
    
    textArea.setToolTipSupplier(null);
  }






  
  protected boolean shouldAutoCloseTag(String tag) {
    return tagsToClose.contains(tag.toLowerCase());
  }





  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
    uninstallKeyboardShortcuts(textArea);
  }
}
