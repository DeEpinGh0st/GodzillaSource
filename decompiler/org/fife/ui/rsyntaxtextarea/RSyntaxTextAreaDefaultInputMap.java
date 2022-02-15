package org.fife.ui.rsyntaxtextarea;

import javax.swing.KeyStroke;
import org.fife.ui.rtextarea.RTADefaultInputMap;
import org.fife.ui.rtextarea.RTextArea;






























public class RSyntaxTextAreaDefaultInputMap
  extends RTADefaultInputMap
{
  public RSyntaxTextAreaDefaultInputMap() {
    int defaultMod = RTextArea.getDefaultModifier();
    int shift = 64;
    int defaultShift = defaultMod | shift;
    
    put(KeyStroke.getKeyStroke(9, shift), "RSTA.DecreaseIndentAction");
    put(KeyStroke.getKeyStroke('}'), "RSTA.CloseCurlyBraceAction");
    
    put(KeyStroke.getKeyStroke('/'), "RSTA.CloseMarkupTagAction");
    int os = RSyntaxUtilities.getOS();
    if (os == 1 || os == 2)
    {








      
      put(KeyStroke.getKeyStroke(47, defaultMod), "RSTA.ToggleCommentAction");
    }
    
    put(KeyStroke.getKeyStroke(91, defaultMod), "RSTA.GoToMatchingBracketAction");
    put(KeyStroke.getKeyStroke(109, defaultMod), "RSTA.CollapseFoldAction");
    put(KeyStroke.getKeyStroke(107, defaultMod), "RSTA.ExpandFoldAction");
    put(KeyStroke.getKeyStroke(111, defaultMod), "RSTA.CollapseAllFoldsAction");
    put(KeyStroke.getKeyStroke(106, defaultMod), "RSTA.ExpandAllFoldsAction");




    
    put(KeyStroke.getKeyStroke(32, defaultShift), "RSTA.TemplateAction");
  }
}
