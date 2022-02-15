package org.fife.ui.rsyntaxtextarea;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import org.fife.ui.rtextarea.SmartHighlightPainter;






















class DefaultOccurrenceMarker
  implements OccurrenceMarker
{
  public Token getTokenToMark(RSyntaxTextArea textArea) {
    int line = textArea.getCaretLineNumber();
    Token tokenList = textArea.getTokenListForLine(line);
    Caret c = textArea.getCaret();
    int dot = c.getDot();
    
    Token t = RSyntaxUtilities.getTokenAtOffset(tokenList, dot);
    if (t == null || !isValidType(textArea, t) || 
      RSyntaxUtilities.isNonWordChar(t)) {
      
      dot--;
      try {
        if (dot >= textArea.getLineStartOffset(line)) {
          t = RSyntaxUtilities.getTokenAtOffset(tokenList, dot);
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 
    
    return t;
  }



  
  public boolean isValidType(RSyntaxTextArea textArea, Token t) {
    return textArea.getMarkOccurrencesOfTokenType(t.getType());
  }



  
  public void markOccurrences(RSyntaxDocument doc, Token t, RSyntaxTextAreaHighlighter h, SmartHighlightPainter p) {
    markOccurrencesOfToken(doc, t, h, p);
  }












  
  public static void markOccurrencesOfToken(RSyntaxDocument doc, Token t, RSyntaxTextAreaHighlighter h, SmartHighlightPainter p) {
    char[] lexeme = t.getLexeme().toCharArray();
    int type = t.getType();
    int lineCount = doc.getDefaultRootElement().getElementCount();
    
    for (int i = 0; i < lineCount; i++) {
      Token temp = doc.getTokenListForLine(i);
      while (temp != null && temp.isPaintable()) {
        if (temp.is(type, lexeme)) {
          try {
            int end = temp.getEndOffset();
            h.addMarkedOccurrenceHighlight(temp.getOffset(), end, p);
          } catch (BadLocationException ble) {
            ble.printStackTrace();
          } 
        }
        temp = temp.getNextToken();
      } 
    } 
  }
}
