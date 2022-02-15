package org.fife.ui.rsyntaxtextarea;

public interface TokenOrientedView {
  Token getTokenListForPhysicalLineAbove(int paramInt);
  
  Token getTokenListForPhysicalLineBelow(int paramInt);
}
