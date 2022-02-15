package org.fife.ui.rsyntaxtextarea;

import javax.swing.event.HyperlinkEvent;






















public class SelectRegionLinkGeneratorResult
  implements LinkGeneratorResult
{
  private RSyntaxTextArea textArea;
  private int sourceOffset;
  private int selStart;
  private int selEnd;
  
  public SelectRegionLinkGeneratorResult(RSyntaxTextArea textArea, int sourceOffset, int selStart, int selEnd) {
    this.textArea = textArea;
    this.sourceOffset = sourceOffset;
    this.selStart = selStart;
    this.selEnd = selEnd;
  }





  
  public HyperlinkEvent execute() {
    this.textArea.select(this.selStart, this.selEnd);
    return null;
  }


  
  public int getSourceOffset() {
    return this.sourceOffset;
  }
}
