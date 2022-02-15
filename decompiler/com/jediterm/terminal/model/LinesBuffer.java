package com.jediterm.terminal.model;

import com.google.common.collect.Lists;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.hyperlinks.TextProcessing;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;





public class LinesBuffer
{
  private static final Logger LOG = Logger.getLogger(LinesBuffer.class);

  
  public static final int DEFAULT_MAX_LINES_COUNT = 5000;
  
  private int myBufferMaxLinesCount = 5000;
  
  private ArrayList<TerminalLine> myLines = Lists.newArrayList();
  
  @Nullable
  private final TextProcessing myTextProcessing;
  
  public LinesBuffer(@Nullable TextProcessing textProcessing) {
    this.myTextProcessing = textProcessing;
  }
  
  public LinesBuffer(int bufferMaxLinesCount, @Nullable TextProcessing textProcessing) {
    this.myBufferMaxLinesCount = bufferMaxLinesCount;
    this.myTextProcessing = textProcessing;
  }
  
  public synchronized String getLines() {
    StringBuilder sb = new StringBuilder();
    
    boolean first = true;
    
    for (TerminalLine line : this.myLines) {
      if (!first) {
        sb.append("\n");
      }
      
      sb.append(line.getText());
      first = false;
    } 
    
    return sb.toString();
  }

  
  public synchronized void addNewLine(@NotNull TextStyle style, @NotNull CharBuffer characters) {
    if (style == null) $$$reportNull$$$0(0);  if (characters == null) $$$reportNull$$$0(1);  addNewLine(new TerminalLine.TextEntry(style, characters));
  }

  
  private synchronized void addNewLine(@NotNull TerminalLine.TextEntry entry) {
    if (entry == null) $$$reportNull$$$0(2);  addLine(new TerminalLine(entry));
  }
  
  private synchronized void addLine(@NotNull TerminalLine line) {
    if (line == null) $$$reportNull$$$0(3);  if (this.myBufferMaxLinesCount > 0 && this.myLines.size() >= this.myBufferMaxLinesCount) {
      removeTopLines(1);
    }
    
    this.myLines.add(line);
  }
  
  public synchronized int getLineCount() {
    return this.myLines.size();
  }
  
  public synchronized void removeTopLines(int count) {
    if (count >= this.myLines.size()) {
      this.myLines = Lists.newArrayList();
    } else {
      this.myLines = Lists.newArrayList(this.myLines.subList(count, this.myLines.size()));
    } 
  }
  
  public String getLineText(int row) {
    TerminalLine line = getLine(row);
    
    return line.getText();
  }
  
  public synchronized void insertLines(int y, int count, int lastLine, @NotNull TerminalLine.TextEntry filler) {
    if (filler == null) $$$reportNull$$$0(4);  LinesBuffer tail = new LinesBuffer(this.myTextProcessing);
    
    if (lastLine < getLineCount() - 1) {
      moveBottomLinesTo(getLineCount() - lastLine - 1, tail);
    }
    
    LinesBuffer head = new LinesBuffer(this.myTextProcessing);
    if (y > 0) {
      moveTopLinesTo(y, head);
    }
    
    for (int i = 0; i < count; i++) {
      head.addNewLine(filler);
    }
    
    head.moveBottomLinesTo(head.getLineCount(), this);
    
    removeBottomLines(count);
    
    tail.moveTopLinesTo(tail.getLineCount(), this);
  }
  
  public synchronized LinesBuffer deleteLines(int y, int count, int lastLine, @NotNull TerminalLine.TextEntry filler) {
    if (filler == null) $$$reportNull$$$0(5);  LinesBuffer tail = new LinesBuffer(this.myTextProcessing);
    
    if (lastLine < getLineCount() - 1) {
      moveBottomLinesTo(getLineCount() - lastLine - 1, tail);
    }
    
    LinesBuffer head = new LinesBuffer(this.myTextProcessing);
    if (y > 0) {
      moveTopLinesTo(y, head);
    }
    
    int toRemove = Math.min(count, getLineCount());
    
    LinesBuffer removed = new LinesBuffer(this.myTextProcessing);
    moveTopLinesTo(toRemove, removed);
    
    head.moveBottomLinesTo(head.getLineCount(), this);
    
    for (int i = 0; i < toRemove; i++) {
      addNewLine(filler);
    }
    
    tail.moveTopLinesTo(tail.getLineCount(), this);
    
    return removed;
  }
  
  public synchronized void writeString(int x, int y, CharBuffer str, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(6);  TerminalLine line = getLine(y);
    
    line.writeString(x, str, style);
    
    if (this.myTextProcessing != null) {
      this.myTextProcessing.processHyperlinks(this, line);
    }
  }
  
  public synchronized void clearLines(int startRow, int endRow, @NotNull TerminalLine.TextEntry filler) {
    if (filler == null) $$$reportNull$$$0(7);  for (int i = startRow; i <= endRow; i++) {
      getLine(i).clear(filler);
    }
  }

  
  public synchronized void clearAll() {
    this.myLines.clear();
  }
  
  public synchronized void deleteCharacters(int x, int y, int count, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(8);  TerminalLine line = getLine(y);
    line.deleteCharacters(x, count, style);
  }
  
  public synchronized void insertBlankCharacters(int x, int y, int count, int maxLen, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(9);  TerminalLine line = getLine(y);
    line.insertBlankCharacters(x, count, maxLen, style);
  }
  
  public synchronized void clearArea(int leftX, int topY, int rightX, int bottomY, @NotNull TextStyle style) {
    if (style == null) $$$reportNull$$$0(10);  for (int y = topY; y < bottomY; y++) {
      TerminalLine line = getLine(y);
      line.clearArea(leftX, rightX, style);
    } 
  }
  
  public synchronized void processLines(int yStart, int yCount, @NotNull StyledTextConsumer consumer) {
    if (consumer == null) $$$reportNull$$$0(11);  processLines(yStart, yCount, consumer, -getLineCount());
  }



  
  public synchronized void processLines(int firstLine, int count, @NotNull StyledTextConsumer consumer, int startRow) {
    if (consumer == null) $$$reportNull$$$0(12);  if (firstLine < 0) {
      throw new IllegalArgumentException("firstLine=" + firstLine + ", should be >0");
    }
    for (int y = firstLine; y < Math.min(firstLine + count, this.myLines.size()); y++) {
      ((TerminalLine)this.myLines.get(y)).process(y, consumer, startRow);
    }
  }
  
  public synchronized void moveTopLinesTo(int count, @NotNull LinesBuffer buffer) {
    if (buffer == null) $$$reportNull$$$0(13);  count = Math.min(count, getLineCount());
    buffer.addLines(this.myLines.subList(0, count));
    removeTopLines(count);
  }
  
  public synchronized void addLines(@NotNull List<TerminalLine> lines) {
    if (lines == null) $$$reportNull$$$0(14);  if (this.myBufferMaxLinesCount > 0) {
      
      if (lines.size() >= this.myBufferMaxLinesCount) {
        int index = lines.size() - this.myBufferMaxLinesCount;
        this.myLines = Lists.newArrayList(lines.subList(index, lines.size()));
        
        return;
      } 
      int count = this.myLines.size() + lines.size();
      if (count >= this.myBufferMaxLinesCount) {
        removeTopLines(count - this.myBufferMaxLinesCount);
      }
    } 
    
    this.myLines.addAll(lines);
  }
  
  @NotNull
  public synchronized TerminalLine getLine(int row) {
    if (row < 0) {
      LOG.error("Negative line number: " + row);
      if (TerminalLine.createEmpty() == null) $$$reportNull$$$0(15);  return TerminalLine.createEmpty();
    } 
    
    for (int i = getLineCount(); i <= row; i++) {
      addLine(TerminalLine.createEmpty());
    }
    
    if ((TerminalLine)this.myLines.get(row) == null) $$$reportNull$$$0(16);  return this.myLines.get(row);
  }
  
  public synchronized void moveBottomLinesTo(int count, @NotNull LinesBuffer buffer) {
    if (buffer == null) $$$reportNull$$$0(17);  count = Math.min(count, getLineCount());
    buffer.addLinesFirst(this.myLines.subList(getLineCount() - count, getLineCount()));
    
    removeBottomLines(count);
  }
  
  private synchronized void addLinesFirst(@NotNull List<TerminalLine> lines) {
    if (lines == null) $$$reportNull$$$0(18);  List<TerminalLine> list = Lists.newArrayList(lines);
    list.addAll(this.myLines);
    this.myLines = Lists.newArrayList(list);
  }
  
  private synchronized void removeBottomLines(int count) {
    this.myLines = Lists.newArrayList(this.myLines.subList(0, getLineCount() - count));
  }
  
  public int removeBottomEmptyLines(int ind, int maxCount) {
    int i = 0;
    while (maxCount - i > 0 && (ind >= this.myLines.size() || ((TerminalLine)this.myLines.get(ind)).isNul())) {
      if (ind < this.myLines.size()) {
        this.myLines.remove(ind);
      }
      ind--;
      i++;
    } 
    
    return i;
  }
  
  synchronized int findLineIndex(@NotNull TerminalLine line) {
    if (line == null) $$$reportNull$$$0(19);  return this.myLines.indexOf(line);
  }
}
