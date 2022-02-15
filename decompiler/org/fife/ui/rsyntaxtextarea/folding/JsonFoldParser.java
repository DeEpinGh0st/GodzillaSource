package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;




















public class JsonFoldParser
  implements FoldParser
{
  private static final Object OBJECT_BLOCK = new Object();
  private static final Object ARRAY_BLOCK = new Object();



  
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    Stack<Object> blocks = new Stack();
    List<Fold> folds = new ArrayList<>();
    
    Fold currentFold = null;
    int lineCount = textArea.getLineCount();

    
    try {
      for (int line = 0; line < lineCount; line++)
      {
        Token t = textArea.getTokenListForLine(line);
        while (t != null && t.isPaintable())
        {
          if (t.isLeftCurly()) {
            if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            } 
            blocks.push(OBJECT_BLOCK);
          
          }
          else if (t.isRightCurly() && popOffTop(blocks, OBJECT_BLOCK)) {
            if (currentFold != null) {
              currentFold.setEndOffset(t.getOffset());
              Fold parentFold = currentFold.getParent();

              
              if (currentFold.isOnSingleLine() && 
                !currentFold.removeFromParent()) {
                folds.remove(folds.size() - 1);
              }
              
              currentFold = parentFold;
            }
          
          }
          else if (isLeftBracket(t)) {
            if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            } 
            blocks.push(ARRAY_BLOCK);
          
          }
          else if (isRightBracket(t) && popOffTop(blocks, ARRAY_BLOCK) && 
            currentFold != null) {
            currentFold.setEndOffset(t.getOffset());
            Fold parentFold = currentFold.getParent();

            
            if (currentFold.isOnSingleLine() && 
              !currentFold.removeFromParent()) {
              folds.remove(folds.size() - 1);
            }
            
            currentFold = parentFold;
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









  
  private static boolean isLeftBracket(Token t) {
    return (t.getType() == 22 && t.isSingleChar('['));
  }








  
  private static boolean isRightBracket(Token t) {
    return (t.getType() == 22 && t.isSingleChar(']'));
  }









  
  private static boolean popOffTop(Stack<Object> stack, Object value) {
    if (stack.size() > 0 && stack.peek() == value) {
      stack.pop();
      return true;
    } 
    return false;
  }
}
