package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;





















public class NsisFoldParser
  implements FoldParser
{
  private static final char[] KEYWORD_FUNCTION = "Function".toCharArray();
  private static final char[] KEYWORD_FUNCTION_END = "FunctionEnd".toCharArray();
  private static final char[] KEYWORD_SECTION = "Section".toCharArray();
  private static final char[] KEYWORD_SECTION_END = "SectionEnd".toCharArray();
  
  protected static final char[] C_MLC_END = "*/".toCharArray();


  
  private static boolean foundEndKeyword(char[] keyword, Token t, Stack<char[]> endWordStack) {
    return (t.is(6, keyword) && !endWordStack.isEmpty() && keyword == endWordStack
      .peek());
  }



  
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    List<Fold> folds = new ArrayList<>();
    
    Fold currentFold = null;
    int lineCount = textArea.getLineCount();
    boolean inMLC = false;
    int mlcStart = 0;
    Stack<char[]> endWordStack = (Stack)new Stack<>();

    
    try {
      for (int line = 0; line < lineCount; line++)
      {
        Token t = textArea.getTokenListForLine(line);
        while (t != null && t.isPaintable())
        {
          if (t.isComment()) {
            
            if (inMLC)
            {
              
              if (t.endsWith(C_MLC_END)) {
                int mlcEnd = t.getEndOffset() - 1;
                if (currentFold == null) {
                  currentFold = new Fold(1, textArea, mlcStart);
                  currentFold.setEndOffset(mlcEnd);
                  folds.add(currentFold);
                  currentFold = null;
                } else {
                  
                  currentFold = currentFold.createChild(1, mlcStart);
                  currentFold.setEndOffset(mlcEnd);
                  currentFold = currentFold.getParent();
                } 
                
                inMLC = false;
                mlcStart = 0;

              
              }

            
            }
            else if (t.getType() != 1 && !t.endsWith(C_MLC_END))
            {
              inMLC = true;
              mlcStart = t.getOffset();
            
            }

          
          }
          else if (t.is(6, KEYWORD_SECTION)) {
            if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            } 
            endWordStack.push(KEYWORD_SECTION_END);
          
          }
          else if (t.is(6, KEYWORD_FUNCTION)) {
            if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            } 
            endWordStack.push(KEYWORD_FUNCTION_END);
          
          }
          else if ((foundEndKeyword(KEYWORD_SECTION_END, t, endWordStack) || 
            foundEndKeyword(KEYWORD_FUNCTION_END, t, endWordStack)) && 
            currentFold != null) {
            currentFold.setEndOffset(t.getOffset());
            Fold parentFold = currentFold.getParent();
            endWordStack.pop();
            
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
}
