package org.fife.rsta.ac.xml;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.Parser;













































public class XmlLanguageSupport
  extends AbstractMarkupLanguageSupport
{
  private XmlCompletionProvider provider;
  private boolean showSyntaxErrors;
  
  public XmlLanguageSupport() {
    setAutoActivationEnabled(true);
    setParameterAssistanceEnabled(false);
    setShowDescWindow(false);
    setShowSyntaxErrors(true);
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return (ListCellRenderer<Object>)new HtmlCellRenderer();
  }






  
  private XmlCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new XmlCompletionProvider();
    }
    return this.provider;
  }










  
  public XmlParser getParser(RSyntaxTextArea textArea) {
    Object parser = textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport.LanguageParser");
    if (parser instanceof XmlParser) {
      return (XmlParser)parser;
    }
    return null;
  }







  
  public boolean getShowSyntaxErrors() {
    return this.showSyntaxErrors;
  }








  
  public void install(RSyntaxTextArea textArea) {
    XmlCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    
    XmlParser parser = new XmlParser(this);













    
    textArea.addParser((Parser)parser);
    textArea.putClientProperty("org.fife.rsta.ac.LanguageSupport.LanguageParser", parser);
    
    installKeyboardShortcuts(textArea);
  }







  
  protected void installKeyboardShortcuts(RSyntaxTextArea textArea) {
    super.installKeyboardShortcuts(textArea);
    
    InputMap im = textArea.getInputMap();
    ActionMap am = textArea.getActionMap();
    int c = textArea.getToolkit().getMenuShortcutKeyMask();
    int shift = 64;
    
    im.put(KeyStroke.getKeyStroke(79, c | shift), "GoToType");
    am.put("GoToType", (Action)new GoToMemberAction(XmlOutlineTree.class));
  }








  
  public void setShowSyntaxErrors(boolean show) {
    this.showSyntaxErrors = show;
  }





  
  protected boolean shouldAutoCloseTag(String tag) {
    return true;
  }






  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
    
    XmlParser parser = getParser(textArea);
    if (parser != null) {
      textArea.removeParser((Parser)parser);
    }
    
    uninstallKeyboardShortcuts(textArea);
  }







  
  protected void uninstallKeyboardShortcuts(RSyntaxTextArea textArea) {
    super.uninstallKeyboardShortcuts(textArea);
    
    InputMap im = textArea.getInputMap();
    ActionMap am = textArea.getActionMap();
    int c = textArea.getToolkit().getMenuShortcutKeyMask();
    int shift = 64;
    
    im.remove(KeyStroke.getKeyStroke(79, c | shift));
    am.remove("GoToType");
  }
}
