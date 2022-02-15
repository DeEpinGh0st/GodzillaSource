package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;















































public class CurlyFoldParser
  implements FoldParser
{
  private boolean foldableMultiLineComments;
  private final boolean java;
  private static final char[] KEYWORD_IMPORT = "import".toCharArray();



  
  protected static final char[] C_MLC_END = "*/".toCharArray();





  
  public CurlyFoldParser() {
    this(true, false);
  }










  
  public CurlyFoldParser(boolean cStyleMultiLineComments, boolean java) {
    this.foldableMultiLineComments = cStyleMultiLineComments;
    this.java = java;
  }







  
  public boolean getFoldableMultiLineComments() {
    return this.foldableMultiLineComments;
  }



  
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    List<Fold> folds = new ArrayList<>();
    
    Fold currentFold = null;
    int lineCount = textArea.getLineCount();
    boolean inMLC = false;
    int mlcStart = 0;
    int importStartLine = -1;
    int lastSeenImportLine = -1;
    int importGroupStartOffs = -1;
    int importGroupEndOffs = -1;
    int lastRightCurlyLine = -1;
    Fold prevFold = null;

    
    try {
      for (int line = 0; line < lineCount; line++)
      {
        Token t = textArea.getTokenListForLine(line);
        while (t != null && t.isPaintable())
        {
          if (getFoldableMultiLineComments() && t.isComment()) {

            
            if (this.java)
            {
              if (importStartLine > -1) {
                if (lastSeenImportLine > importStartLine) {
                  Fold fold = null;



                  
                  if (currentFold == null) {
                    fold = new Fold(2, textArea, importGroupStartOffs);
                    
                    folds.add(fold);
                  } else {
                    
                    fold = currentFold.createChild(2, importGroupStartOffs);
                  } 
                  
                  fold.setEndOffset(importGroupEndOffs);
                } 
                importStartLine = lastSeenImportLine = importGroupStartOffs = importGroupEndOffs = -1;
              } 
            }


            
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
          else if (isLeftCurly(t)) {

            
            if (this.java)
            {
              if (importStartLine > -1) {
                if (lastSeenImportLine > importStartLine) {
                  Fold fold = null;



                  
                  if (currentFold == null) {
                    fold = new Fold(2, textArea, importGroupStartOffs);
                    
                    folds.add(fold);
                  } else {
                    
                    fold = currentFold.createChild(2, importGroupStartOffs);
                  } 
                  
                  fold.setEndOffset(importGroupEndOffs);
                } 
                importStartLine = lastSeenImportLine = importGroupStartOffs = importGroupEndOffs = -1;
              } 
            }





            
            if (prevFold != null && line == lastRightCurlyLine) {
              currentFold = prevFold;


              
              prevFold = null;
              lastRightCurlyLine = -1;
            }
            else if (currentFold == null) {
              currentFold = new Fold(0, textArea, t.getOffset());
              folds.add(currentFold);
            } else {
              
              currentFold = currentFold.createChild(0, t.getOffset());
            
            }
          
          }
          else if (isRightCurly(t)) {
            
            if (currentFold != null) {
              currentFold.setEndOffset(t.getOffset());
              Fold parentFold = currentFold.getParent();

              
              if (currentFold.isOnSingleLine()) {
                if (!currentFold.removeFromParent()) {
                  folds.remove(folds.size() - 1);
                
                }
              
              }
              else {
                
                lastRightCurlyLine = line;
                prevFold = currentFold;
              } 
              currentFold = parentFold;
            
            }

          
          }
          else if (this.java) {
            
            if (t.is(6, KEYWORD_IMPORT)) {
              if (importStartLine == -1) {
                importStartLine = line;
                importGroupStartOffs = t.getOffset();
                importGroupEndOffs = t.getOffset();
              } 
              lastSeenImportLine = line;
            
            }
            else if (importStartLine > -1 && t
              .isIdentifier() && t
              .isSingleChar(';')) {
              importGroupEndOffs = t.getOffset();
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










  
  public boolean isLeftCurly(Token t) {
    return t.isLeftCurly();
  }









  
  public boolean isRightCurly(Token t) {
    return t.isRightCurly();
  }







  
  public void setFoldableMultiLineComments(boolean foldable) {
    this.foldableMultiLineComments = foldable;
  }
}
