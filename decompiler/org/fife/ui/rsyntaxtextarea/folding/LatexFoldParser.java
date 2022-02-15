package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;

















public class LatexFoldParser
  implements FoldParser
{
  private static final char[] BEGIN = "\\begin".toCharArray();
  private static final char[] END = "\\end".toCharArray();



  
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    List<Fold> folds = new ArrayList<>();
    Stack<String> expectedStack = new Stack<>();
    
    Fold currentFold = null;
    int lineCount = textArea.getLineCount();

    
    try {
      for (int line = 0; line < lineCount; line++)
      {
        Token t = textArea.getTokenListForLine(line);
        while (t != null && t.isPaintable())
        {
          if (t.is(6, BEGIN)) {
            Token temp = t.getNextToken();
            if (temp != null && temp.isLeftCurly()) {
              temp = temp.getNextToken();
              if (temp != null && temp.getType() == 6) {
                if (currentFold == null) {
                  currentFold = new Fold(0, textArea, t.getOffset());
                  folds.add(currentFold);
                } else {
                  
                  currentFold = currentFold.createChild(0, t.getOffset());
                } 
                expectedStack.push(temp.getLexeme());
                t = temp;
              }
            
            }
          
          } else if (t.is(6, END) && currentFold != null && 
            !expectedStack.isEmpty()) {
            Token temp = t.getNextToken();
            if (temp != null && temp.isLeftCurly()) {
              temp = temp.getNextToken();
              if (temp != null && temp.getType() == 6) {
                String value = temp.getLexeme();
                if (((String)expectedStack.peek()).equals(value)) {
                  expectedStack.pop();
                  currentFold.setEndOffset(t.getOffset());
                  Fold parentFold = currentFold.getParent();
                  
                  if (currentFold.isOnSingleLine() && 
                    !currentFold.removeFromParent()) {
                    folds.remove(folds.size() - 1);
                  }
                  
                  t = temp;
                  currentFold = parentFold;
                } 
              } 
            } 
          } 
          
          t = t.getNextToken();
        }
      
      }
    
    }
    catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    
    return folds;
  }
}
