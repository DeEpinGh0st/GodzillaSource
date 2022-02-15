package org.fife.ui.rsyntaxtextarea;

import javax.swing.Action;
import javax.swing.text.Segment;

public interface TokenMaker {
  void addNullToken();
  
  void addToken(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  int getClosestStandardTokenTypeForInternalType(int paramInt);
  
  boolean getCurlyBracesDenoteCodeBlocks(int paramInt);
  
  int getLastTokenTypeOnLine(Segment paramSegment, int paramInt);
  
  String[] getLineCommentStartAndEnd(int paramInt);
  
  Action getInsertBreakAction();
  
  boolean getMarkOccurrencesOfTokenType(int paramInt);
  
  OccurrenceMarker getOccurrenceMarker();
  
  boolean getShouldIndentNextLineAfter(Token paramToken);
  
  Token getTokenList(Segment paramSegment, int paramInt1, int paramInt2);
  
  boolean isIdentifierChar(int paramInt, char paramChar);
  
  boolean isMarkupLanguage();
}
