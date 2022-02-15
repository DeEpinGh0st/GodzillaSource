package org.fife.ui.rsyntaxtextarea;

import javax.swing.text.Segment;

































public abstract class AbstractJFlexTokenMaker
  extends TokenMakerBase
{
  protected Segment s;
  protected int start;
  protected int offsetShift;
  
  public abstract void yybegin(int paramInt);
  
  protected void yybegin(int state, int languageIndex) {
    yybegin(state);
    setLanguageIndex(languageIndex);
  }
}
