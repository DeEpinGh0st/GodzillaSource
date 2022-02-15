package org.fife.ui.rtextarea;

import javax.swing.InputMap;
import javax.swing.KeyStroke;











































public class RTADefaultInputMap
  extends InputMap
{
  public RTADefaultInputMap() {
    int defaultModifier = RTextArea.getDefaultModifier();
    
    int alt = 512;
    int shift = 64;
    boolean isOSX = RTextArea.isOSX();
    int moveByWordMod = isOSX ? alt : defaultModifier;
    
    put(KeyStroke.getKeyStroke(36, 0), isOSX ? "caret-begin" : "caret-begin-line");
    put(KeyStroke.getKeyStroke(36, shift), isOSX ? "selection-begin" : "selection-begin-line");
    put(KeyStroke.getKeyStroke(36, defaultModifier), "caret-begin");
    put(KeyStroke.getKeyStroke(36, defaultModifier | shift), "selection-begin");
    put(KeyStroke.getKeyStroke(35, 0), isOSX ? "caret-end" : "caret-end-line");
    put(KeyStroke.getKeyStroke(35, shift), isOSX ? "selection-end" : "selection-end-line");
    put(KeyStroke.getKeyStroke(35, defaultModifier), "caret-end");
    put(KeyStroke.getKeyStroke(35, defaultModifier | shift), "selection-end");
    
    put(KeyStroke.getKeyStroke(37, 0), "caret-backward");
    put(KeyStroke.getKeyStroke(37, shift), "selection-backward");
    put(KeyStroke.getKeyStroke(37, moveByWordMod), "caret-previous-word");
    put(KeyStroke.getKeyStroke(37, moveByWordMod | shift), "selection-previous-word");
    put(KeyStroke.getKeyStroke(40, 0), "caret-down");
    put(KeyStroke.getKeyStroke(40, shift), "selection-down");
    put(KeyStroke.getKeyStroke(40, defaultModifier), "RTA.ScrollDownAction");
    put(KeyStroke.getKeyStroke(40, alt), "RTA.LineDownAction");
    put(KeyStroke.getKeyStroke(39, 0), "caret-forward");
    put(KeyStroke.getKeyStroke(39, shift), "selection-forward");
    put(KeyStroke.getKeyStroke(39, moveByWordMod), "caret-next-word");
    put(KeyStroke.getKeyStroke(39, moveByWordMod | shift), "selection-next-word");
    put(KeyStroke.getKeyStroke(38, 0), "caret-up");
    put(KeyStroke.getKeyStroke(38, shift), "selection-up");
    put(KeyStroke.getKeyStroke(38, defaultModifier), "RTA.ScrollUpAction");
    put(KeyStroke.getKeyStroke(38, alt), "RTA.LineUpAction");
    
    put(KeyStroke.getKeyStroke(33, 0), "page-up");
    put(KeyStroke.getKeyStroke(33, shift), "RTA.SelectionPageUpAction");
    put(KeyStroke.getKeyStroke(33, defaultModifier | shift), "RTA.SelectionPageLeftAction");
    put(KeyStroke.getKeyStroke(34, 0), "page-down");
    put(KeyStroke.getKeyStroke(34, shift), "RTA.SelectionPageDownAction");
    put(KeyStroke.getKeyStroke(34, defaultModifier | shift), "RTA.SelectionPageRightAction");
    
    put(KeyStroke.getKeyStroke(65489, 0), "cut-to-clipboard");
    put(KeyStroke.getKeyStroke(65485, 0), "copy-to-clipboard");
    put(KeyStroke.getKeyStroke(65487, 0), "paste-from-clipboard");
    
    put(KeyStroke.getKeyStroke(88, defaultModifier), "cut-to-clipboard");
    put(KeyStroke.getKeyStroke(67, defaultModifier), "copy-to-clipboard");
    put(KeyStroke.getKeyStroke(86, defaultModifier), "paste-from-clipboard");
    put(KeyStroke.getKeyStroke(86, defaultModifier | shift), "RTA.PasteHistoryAction");
    put(KeyStroke.getKeyStroke(127, 0), "delete-next");
    put(KeyStroke.getKeyStroke(127, shift), "cut-to-clipboard");
    put(KeyStroke.getKeyStroke(127, defaultModifier), "RTA.DeleteRestOfLineAction");
    put(KeyStroke.getKeyStroke(155, 0), "RTA.ToggleTextModeAction");
    put(KeyStroke.getKeyStroke(155, shift), "paste-from-clipboard");
    put(KeyStroke.getKeyStroke(155, defaultModifier), "copy-to-clipboard");
    put(KeyStroke.getKeyStroke(65, defaultModifier), "select-all");
    
    put(KeyStroke.getKeyStroke(68, defaultModifier), "RTA.DeleteLineAction");
    put(KeyStroke.getKeyStroke(74, defaultModifier), "RTA.JoinLinesAction");
    
    put(KeyStroke.getKeyStroke(8, shift), "delete-previous");
    put(KeyStroke.getKeyStroke(8, defaultModifier), "RTA.DeletePrevWordAction");
    put(KeyStroke.getKeyStroke(9, 0), "insert-tab");
    put(KeyStroke.getKeyStroke(10, 0), "insert-break");
    put(KeyStroke.getKeyStroke(10, shift), "insert-break");
    put(KeyStroke.getKeyStroke(10, defaultModifier), "RTA.DumbCompleteWordAction");
    
    put(KeyStroke.getKeyStroke(90, defaultModifier), "RTA.UndoAction");
    put(KeyStroke.getKeyStroke(89, defaultModifier), "RTA.RedoAction");
    
    put(KeyStroke.getKeyStroke(113, 0), "RTA.NextBookmarkAction");
    put(KeyStroke.getKeyStroke(113, shift), "RTA.PrevBookmarkAction");
    put(KeyStroke.getKeyStroke(113, defaultModifier), "RTA.ToggleBookmarkAction");
    
    put(KeyStroke.getKeyStroke(75, defaultModifier | shift), "RTA.PrevOccurrenceAction");
    put(KeyStroke.getKeyStroke(75, defaultModifier), "RTA.NextOccurrenceAction");
    
    if (isOSX) {
      put(KeyStroke.getKeyStroke(37, defaultModifier), "caret-begin-line");
      put(KeyStroke.getKeyStroke(39, defaultModifier), "caret-end-line");
      put(KeyStroke.getKeyStroke(37, defaultModifier | shift), "selection-begin-line");
      put(KeyStroke.getKeyStroke(39, defaultModifier | shift), "selection-end-line");
    } 
  }
}
