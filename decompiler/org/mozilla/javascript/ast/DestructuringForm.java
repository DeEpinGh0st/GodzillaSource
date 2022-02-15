package org.mozilla.javascript.ast;

public interface DestructuringForm {
  void setIsDestructuring(boolean paramBoolean);
  
  boolean isDestructuring();
}
