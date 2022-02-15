package com.jediterm.terminal.model;

import org.jetbrains.annotations.NotNull;

public class SubCharBuffer extends CharBuffer {
  private final CharBuffer myParent;
  private final int myOffset;
  
  public SubCharBuffer(@NotNull CharBuffer parent, int offset, int length) {
    super(parent.getBuf(), parent.getStart() + offset, length);
    this.myParent = parent;
    this.myOffset = offset;
  }
  @NotNull
  public CharBuffer getParent() {
    if (this.myParent == null) $$$reportNull$$$0(1);  return this.myParent;
  }
  
  public int getOffset() {
    return this.myOffset;
  }
}
