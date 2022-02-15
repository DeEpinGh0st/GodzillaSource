package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;



















public class XmlFoldParser
  implements FoldParser
{
  private static final char[] MARKUP_CLOSING_TAG_START = new char[] { '<', '/' };
  private static final char[] MARKUP_SHORT_TAG_END = new char[] { '/', '>' };
  private static final char[] MLC_END = new char[] { '-', '-', '>' };



  
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    List<Fold> folds = new ArrayList<>();
    
    Fold currentFold = null;
    int lineCount = textArea.getLineCount();
    boolean inMLC = false;
    int mlcStart = 0;

    
    try {
      for (int line = 0; line < lineCount; line++)
      {
        Token t = textArea.getTokenListForLine(line);
        while (t != null && t.isPaintable())
        {
          if (t.isComment()) {

            
            if (inMLC) {
              
              if (t.endsWith(MLC_END)) {
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
            else if (t.getType() == 2 && !t.endsWith(MLC_END)) {
              inMLC = true;
              mlcStart = t.getOffset();
            
            }

          
          }
          else if (t.isSingleChar(25, '<')) {
            if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            }
          
          }
          else if (t.is(25, MARKUP_SHORT_TAG_END)) {
            if (currentFold != null) {
              Fold parentFold = currentFold.getParent();
              removeFold(currentFold, folds);
              currentFold = parentFold;
            }
          
          }
          else if (t.is(25, MARKUP_CLOSING_TAG_START) && 
            currentFold != null) {
            currentFold.setEndOffset(t.getOffset());
            Fold parentFold = currentFold.getParent();
            
            if (currentFold.isOnSingleLine()) {
              removeFold(currentFold, folds);
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










  
  private static void removeFold(Fold fold, List<Fold> folds) {
    if (!fold.removeFromParent())
      folds.remove(folds.size() - 1); 
  }
}
