package org.fife.ui.rsyntaxtextarea.modes;

import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
























public abstract class AbstractMarkupTokenMaker
  extends AbstractJFlexTokenMaker
{
  public abstract boolean getCompleteCloseTags();
  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] { "<!--", "-->" };
  }







  
  public final boolean isMarkupLanguage() {
    return true;
  }
}
