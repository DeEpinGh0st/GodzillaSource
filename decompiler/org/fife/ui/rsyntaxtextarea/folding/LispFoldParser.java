package org.fife.ui.rsyntaxtextarea.folding;

import org.fife.ui.rsyntaxtextarea.Token;

















public class LispFoldParser
  extends CurlyFoldParser
{
  public boolean isLeftCurly(Token t) {
    return t.isSingleChar(22, '(');
  }


  
  public boolean isRightCurly(Token t) {
    return t.isSingleChar(22, ')');
  }
}
