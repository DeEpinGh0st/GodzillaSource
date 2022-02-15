package org.fife.ui.rsyntaxtextarea;

import javax.swing.text.Segment;

interface TokenFactory {
  TokenImpl createToken();
  
  TokenImpl createToken(Segment paramSegment, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  TokenImpl createToken(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void resetAllTokens();
}
