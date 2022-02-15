package com.jediterm.terminal.model.hyperlinks;

import com.google.common.collect.Lists;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.CharBuffer;
import com.jediterm.terminal.model.LinesBuffer;
import com.jediterm.terminal.model.TerminalLine;
import com.jediterm.terminal.model.TerminalTextBuffer;
import java.util.List;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;







public class TextProcessing
{
  private static final Logger LOG = Logger.getLogger(TextProcessing.class);
  
  private final List<HyperlinkFilter> myHyperlinkFilter;
  
  private TextStyle myHyperlinkColor;
  private HyperlinkStyle.HighlightMode myHighlightMode;
  private TerminalTextBuffer myTerminalTextBuffer;
  
  public TextProcessing(@NotNull TextStyle hyperlinkColor, @NotNull HyperlinkStyle.HighlightMode highlightMode) {
    this.myHyperlinkColor = hyperlinkColor;
    this.myHighlightMode = highlightMode;
    this.myHyperlinkFilter = Lists.newArrayList();
  }
  
  public void setTerminalTextBuffer(@NotNull TerminalTextBuffer terminalTextBuffer) {
    if (terminalTextBuffer == null) $$$reportNull$$$0(2);  this.myTerminalTextBuffer = terminalTextBuffer;
  }
  
  public void processHyperlinks(@NotNull LinesBuffer buffer, @NotNull TerminalLine updatedLine) {
    if (buffer == null) $$$reportNull$$$0(3);  if (updatedLine == null) $$$reportNull$$$0(4);  if (this.myHyperlinkFilter.isEmpty())
      return;  doProcessHyperlinks(buffer, updatedLine);
  }
  
  private void doProcessHyperlinks(@NotNull LinesBuffer buffer, @NotNull TerminalLine updatedLine) {
    if (buffer == null) $$$reportNull$$$0(5);  if (updatedLine == null) $$$reportNull$$$0(6);  this.myTerminalTextBuffer.lock();
    try {
      int updatedLineInd = findLineInd(buffer, updatedLine);
      if (updatedLineInd == -1) {
        
        updatedLineInd = findHistoryLineInd(this.myTerminalTextBuffer.getHistoryBuffer(), updatedLine);
        if (updatedLineInd == -1) {
          LOG.debug("Cannot find line for links processing");
          return;
        } 
        buffer = this.myTerminalTextBuffer.getHistoryBuffer();
      } 
      int startLineInd = updatedLineInd;
      while (startLineInd > 0 && buffer.getLine(startLineInd - 1).isWrapped()) {
        startLineInd--;
      }
      String lineStr = joinLines(buffer, startLineInd, updatedLineInd);
      for (HyperlinkFilter filter : this.myHyperlinkFilter) {
        LinkResult result = filter.apply(lineStr);
        if (result != null) {
          for (LinkResultItem item : result.getItems()) {
            
            HyperlinkStyle hyperlinkStyle = new HyperlinkStyle(this.myHyperlinkColor.getForeground(), this.myHyperlinkColor.getBackground(), item.getLinkInfo(), this.myHighlightMode, null);
            if (item.getStartOffset() < 0 || item.getEndOffset() > lineStr.length())
              continue; 
            int prevLinesLength = 0;
            for (int lineInd = startLineInd; lineInd <= updatedLineInd; lineInd++) {
              int startLineOffset = Math.max(prevLinesLength, item.getStartOffset());
              int endLineOffset = Math.min(prevLinesLength + this.myTerminalTextBuffer.getWidth(), item.getEndOffset());
              if (startLineOffset < endLineOffset) {
                buffer.getLine(lineInd).writeString(startLineOffset - prevLinesLength, new CharBuffer(lineStr.substring(startLineOffset, endLineOffset)), (TextStyle)hyperlinkStyle);
              }
              prevLinesLength += this.myTerminalTextBuffer.getWidth();
            } 
          } 
        }
      } 
    } finally {
      
      this.myTerminalTextBuffer.unlock();
    } 
  }
  
  private int findHistoryLineInd(@NotNull LinesBuffer historyBuffer, @NotNull TerminalLine line) {
    if (historyBuffer == null) $$$reportNull$$$0(7);  if (line == null) $$$reportNull$$$0(8);  int lastLineInd = Math.max(0, historyBuffer.getLineCount() - 200);
    for (int i = historyBuffer.getLineCount() - 1; i >= lastLineInd; i--) {
      if (historyBuffer.getLine(i) == line) {
        return i;
      }
    } 
    return -1;
  }
  
  private static int findLineInd(@NotNull LinesBuffer buffer, @NotNull TerminalLine line) {
    if (buffer == null) $$$reportNull$$$0(9);  if (line == null) $$$reportNull$$$0(10);  for (int i = 0; i < buffer.getLineCount(); i++) {
      TerminalLine l = buffer.getLine(i);
      if (l == line) {
        return i;
      }
    } 
    return -1;
  }
  
  @NotNull
  private String joinLines(@NotNull LinesBuffer buffer, int startLineInd, int updatedLineInd) {
    if (buffer == null) $$$reportNull$$$0(11);  StringBuilder result = new StringBuilder();
    for (int i = startLineInd; i <= updatedLineInd; i++) {
      String text = buffer.getLine(i).getText();
      if (i < updatedLineInd && text.length() < this.myTerminalTextBuffer.getWidth()) {
        text = text + new CharBuffer(false, this.myTerminalTextBuffer.getWidth() - text.length());
      }
      result.append(text);
    } 
    if (result.toString() == null) $$$reportNull$$$0(12);  return result.toString();
  }
  
  public void addHyperlinkFilter(@NotNull HyperlinkFilter filter) {
    if (filter == null) $$$reportNull$$$0(13);  this.myHyperlinkFilter.add(filter);
  }
}
