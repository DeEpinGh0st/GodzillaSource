package org.fife.rsta.ac.jsp;

import java.util.HashSet;
import java.util.Set;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.rsta.ac.html.HtmlLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;





























public class JspLanguageSupport
  extends AbstractMarkupLanguageSupport
{
  private JspCompletionProvider provider;
  private static Set<String> tagsToClose = new HashSet<>();




  
  public JspLanguageSupport() {
    setAutoActivationEnabled(true);
    setParameterAssistanceEnabled(false);
    setShowDescWindow(true);
    tagsToClose = HtmlLanguageSupport.getTagsToClose();
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new HtmlCellRenderer();
  }

  
  private JspCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new JspCompletionProvider();
    }
    return this.provider;
  }






  
  public void install(RSyntaxTextArea textArea) {
    HtmlCompletionProvider provider = getProvider();
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
