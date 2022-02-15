package com.jediterm.terminal.model.hyperlinks;

import org.jetbrains.annotations.NotNull;




public class LinkResultItem
{
  private int myStartOffset;
  private int myEndOffset;
  private LinkInfo myLinkInfo;
  
  public LinkResultItem(int startOffset, int endOffset, @NotNull LinkInfo linkInfo) {
    this.myStartOffset = startOffset;
    this.myEndOffset = endOffset;
    this.myLinkInfo = linkInfo;
  }
  
  public int getStartOffset() {
    return this.myStartOffset;
  }
  
  public int getEndOffset() {
    return this.myEndOffset;
  }
  
  public LinkInfo getLinkInfo() {
    return this.myLinkInfo;
  }
}
