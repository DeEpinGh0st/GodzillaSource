package org.fife.rsta.ac.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

































public class HtmlLanguageSupport
  extends AbstractMarkupLanguageSupport
{
  private HtmlCompletionProvider provider;
  
  public HtmlLanguageSupport() {
    setAutoActivationEnabled(true);
    setParameterAssistanceEnabled(false);
    setShowDescWindow(true);
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new HtmlCellRenderer();
  }

  
  private HtmlCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new HtmlCompletionProvider();
    }
    return this.provider;
  }







  
  public static Set<String> getTagsToClose() {
    return tagsToClose;
  }








  
  private static Set<String> getTagsToClose(String res) {
    Set<String> tags = new HashSet<>();
    InputStream in = HtmlLanguageSupport.class.getResourceAsStream(res);
    if (in != null) {
      
      try {
        BufferedReader r = new BufferedReader(new InputStreamReader(in)); String line;
        while ((line = r.readLine()) != null) {
          if (line.length() > 0 && line.charAt(0) != '#') {
            tags.add(line.trim());
          }
        } 
        r.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } 
    }
    return tags;
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


  
  private static Set<String> tagsToClose = getTagsToClose("html5_close_tags.txt");
}
