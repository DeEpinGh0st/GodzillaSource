package org.fife.ui.rsyntaxtextarea.modes;























public class LessTokenMaker
  extends CSSTokenMaker
{
  public LessTokenMaker() {
    setHighlightingLess(true);
  }





  
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] { "//", null };
  }





  
  public boolean getMarkOccurrencesOfTokenType(int type) {
    return (type == 17 || super
      .getMarkOccurrencesOfTokenType(type));
  }
}
