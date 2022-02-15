package org.fife.rsta.ac;

import java.awt.event.ActionEvent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.TextAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;































public abstract class AbstractMarkupLanguageSupport
  extends AbstractLanguageSupport
{
  protected static final String INSERT_CLOSING_TAG_ACTION = "HtmlLanguageSupport.InsertClosingTag";
  private boolean autoAddClosingTags;
  
  protected AbstractMarkupLanguageSupport() {
    setAutoAddClosingTags(true);
  }









  
  public boolean getAutoAddClosingTags() {
    return this.autoAddClosingTags;
  }














  
  protected void installKeyboardShortcuts(RSyntaxTextArea textArea) {
    InputMap im = textArea.getInputMap();
    ActionMap am = textArea.getActionMap();
    
    im.put(KeyStroke.getKeyStroke('>'), "HtmlLanguageSupport.InsertClosingTag");
    am.put("HtmlLanguageSupport.InsertClosingTag", new InsertClosingTagAction());
  }









  
  protected abstract boolean shouldAutoCloseTag(String paramString);









  
  public void setAutoAddClosingTags(boolean autoAdd) {
    this.autoAddClosingTags = autoAdd;
  }











  
  protected void uninstallKeyboardShortcuts(RSyntaxTextArea textArea) {
    InputMap im = textArea.getInputMap();
    ActionMap am = textArea.getActionMap();
    
    im.remove(KeyStroke.getKeyStroke('>'));
    am.remove("HtmlLanguageSupport.InsertClosingTag");
  }






  
  private class InsertClosingTagAction
    extends TextAction
  {
    InsertClosingTagAction() {
      super("HtmlLanguageSupport.InsertClosingTag");
    }


    
    public void actionPerformed(ActionEvent e) {
      RSyntaxTextArea textArea = (RSyntaxTextArea)getTextComponent(e);
      RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
      Caret c = textArea.getCaret();
      
      int dot = c.getDot();
      boolean selection = (dot != c.getMark());
      textArea.replaceSelection(">");

      
      if (!selection && AbstractMarkupLanguageSupport.this.getAutoAddClosingTags()) {
        
        Token t = doc.getTokenListForLine(textArea.getCaretLineNumber());
        t = RSyntaxUtilities.getTokenAtOffset(t, dot);
        if (t != null && t.isSingleChar(25, '>')) {
          String tagName = discoverTagName(doc, dot);
          if (tagName != null) {
            textArea.replaceSelection("</" + tagName + ">");
            textArea.setCaretPosition(dot + 1);
          } 
        } 
      } 
    }













    
    private String discoverTagName(RSyntaxDocument doc, int dot) {
      String candidate = null;
      
      Element root = doc.getDefaultRootElement();
      int curLine = root.getElementIndex(dot);


      
      Token t = doc.getTokenListForLine(curLine);
      while (t != null && t.isPaintable()) {
        if (t.getType() == 25) {
          if (t.isSingleChar('<')) {
            t = t.getNextToken();
            if (t != null && t.isPaintable()) {
              candidate = t.getLexeme();
            }
          }
          else if (t.isSingleChar('>')) {
            if (t.getOffset() == dot) {
              if (candidate == null || AbstractMarkupLanguageSupport.this
                .shouldAutoCloseTag(candidate)) {
                return candidate;
              }
              return null;
            }
          
          } else if (t.is(25, "</")) {
            candidate = null;
          } 
        }
        
        t = t.getNextToken();
      } 

      
      return null;
    }
  }
}
