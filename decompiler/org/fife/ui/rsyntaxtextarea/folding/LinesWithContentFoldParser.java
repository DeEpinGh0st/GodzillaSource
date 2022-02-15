package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenUtils;

















public class LinesWithContentFoldParser
  implements FoldParser
{
  public List<Fold> getFolds(RSyntaxTextArea textArea) {
    List<Fold> folds = new ArrayList<>();
    
    Fold fold = null;
    int lineCount = textArea.getLineCount();

    
    try {
      for (int line = 0; line < lineCount; line++) {
        
        Token t = textArea.getTokenListForLine(line);
        
        if (!TokenUtils.isBlankOrAllWhiteSpaceWithoutComments(t)) {

          
          if (fold == null) {
            fold = new Fold(0, textArea, t.getOffset());
            folds.add(fold);
          }
        
        } else if (fold != null) {
          
          fold.setEndOffset(textArea.getLineStartOffset(line) - 1);
          if (fold.isOnSingleLine()) {
            folds.remove(folds.size() - 1);
          }
          fold = null;
        } 
      } 
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    
    return folds;
  }
}
