package com.jediterm.terminal.model;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import org.jetbrains.annotations.NotNull;

public abstract class TerminalLineIntervalHighlighting {
  private final TerminalLine myLine;
  private final int myStartOffset;
  private final int myEndOffset;
  private final TextStyle myStyle;
  private boolean myDisposed = false;
  
  TerminalLineIntervalHighlighting(@NotNull TerminalLine line, int startOffset, int length, @NotNull TextStyle style) {
    if (startOffset < 0) {
      throw new IllegalArgumentException("Negative startOffset: " + startOffset);
    }
    if (length < 0) {
      throw new IllegalArgumentException("Negative length: " + length);
    }
    this.myLine = line;
    this.myStartOffset = startOffset;
    this.myEndOffset = startOffset + length;
    this.myStyle = style;
  }
  @NotNull
  public TerminalLine getLine() {
    if (this.myLine == null) $$$reportNull$$$0(2);  return this.myLine;
  }
  
  public int getStartOffset() {
    return this.myStartOffset;
  }
  
  public int getEndOffset() {
    return this.myEndOffset;
  }
  
  public int getLength() {
    return this.myEndOffset - this.myStartOffset;
  }
  
  public boolean isDisposed() {
    return this.myDisposed;
  }
  
  public final void dispose() {
    doDispose();
    this.myDisposed = true;
  }
  
  protected abstract void doDispose();
  
  public boolean intersectsWith(int otherStartOffset, int otherEndOffset) {
    return (this.myEndOffset > otherStartOffset && otherEndOffset > this.myStartOffset);
  }
  @NotNull
  public TextStyle mergeWith(@NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(3);  TerminalColor foreground = this.myStyle.getForeground();
    if (foreground == null) {
      foreground = style.getForeground();
    }
    TerminalColor background = this.myStyle.getBackground();
    if (background == null) {
      background = style.getBackground();
    }
    return new TextStyle(foreground, background);
  }

  
  public String toString() {
    return "startOffset=" + this.myStartOffset + ", endOffset=" + this.myEndOffset + ", disposed=" + this.myDisposed;
  }
}
