package com.jediterm.terminal.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jediterm.terminal.RequestOrigin;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.StyledTextConsumerAdapter;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.hyperlinks.TextProcessing;
import com.jediterm.terminal.util.Pair;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;










public class TerminalTextBuffer
{
  private static final Logger LOG = Logger.getLogger(TerminalTextBuffer.class);
  
  @NotNull
  private final StyleState myStyleState;
  
  private LinesBuffer myHistoryBuffer;
  
  private LinesBuffer myScreenBuffer;
  
  private int myWidth;
  
  private int myHeight;
  
  private final int myHistoryLinesCount;
  private final Lock myLock = new ReentrantLock();
  
  private LinesBuffer myHistoryBufferBackup;
  
  private LinesBuffer myScreenBufferBackup;
  
  private boolean myAlternateBuffer = false;
  
  private boolean myUsingAlternateBuffer = false;
  private final List<TerminalModelListener> myListeners = new CopyOnWriteArrayList<>();
  
  @Nullable
  private final TextProcessing myTextProcessing;
  
  public TerminalTextBuffer(int width, int height, @NotNull StyleState styleState) {
    this(width, height, styleState, null);
  }
  
  public TerminalTextBuffer(int width, int height, @NotNull StyleState styleState, @Nullable TextProcessing textProcessing) {
    this(width, height, styleState, 5000, textProcessing);
  }
  
  public TerminalTextBuffer(int width, int height, @NotNull StyleState styleState, int historyLinesCount, @Nullable TextProcessing textProcessing) {
    this.myStyleState = styleState;
    this.myWidth = width;
    this.myHeight = height;
    this.myHistoryLinesCount = historyLinesCount;
    this.myTextProcessing = textProcessing;
    
    this.myScreenBuffer = createScreenBuffer();
    this.myHistoryBuffer = createHistoryBuffer();
  }
  
  @NotNull
  private LinesBuffer createScreenBuffer() {
    return new LinesBuffer(-1, this.myTextProcessing);
  }
  
  @NotNull
  private LinesBuffer createHistoryBuffer() {
    return new LinesBuffer(this.myHistoryLinesCount, this.myTextProcessing);
  }





  
  public Dimension resize(@NotNull Dimension pendingResize, @NotNull RequestOrigin origin, int cursorX, int cursorY, @NotNull JediTerminal.ResizeHandler resizeHandler, @Nullable TerminalSelection mySelection) {
    if (pendingResize == null) $$$reportNull$$$0(3);  if (origin == null) $$$reportNull$$$0(4);  if (resizeHandler == null) $$$reportNull$$$0(5);  lock();
    try {
      return doResize(pendingResize, origin, cursorX, cursorY, resizeHandler, mySelection);
    } finally {
      unlock();
    } 
  }





  
  private Dimension doResize(@NotNull Dimension pendingResize, @NotNull RequestOrigin origin, int cursorX, int cursorY, @NotNull JediTerminal.ResizeHandler resizeHandler, @Nullable TerminalSelection mySelection) {
    if (pendingResize == null) $$$reportNull$$$0(6);  if (origin == null) $$$reportNull$$$0(7);  if (resizeHandler == null) $$$reportNull$$$0(8);  int newWidth = pendingResize.width;
    int newHeight = pendingResize.height;
    int newCursorX = cursorX;
    int newCursorY = cursorY;
    
    if (this.myWidth != newWidth) {
      ChangeWidthOperation changeWidthOperation = new ChangeWidthOperation(this, newWidth, newHeight);
      Point cursor = new Point(cursorX, cursorY - 1);
      changeWidthOperation.addPointToTrack(cursor);
      if (mySelection != null) {
        changeWidthOperation.addPointToTrack(mySelection.getStart());
        changeWidthOperation.addPointToTrack(mySelection.getEnd());
      } 
      changeWidthOperation.run();
      this.myWidth = newWidth;
      this.myHeight = newHeight;
      Point newCursor = changeWidthOperation.getTrackedPoint(cursor);
      newCursorX = newCursor.x;
      newCursorY = newCursor.y + 1;
      if (mySelection != null) {
        mySelection.getStart().setLocation(changeWidthOperation.getTrackedPoint(mySelection.getStart()));
        mySelection.getEnd().setLocation(changeWidthOperation.getTrackedPoint(mySelection.getEnd()));
      } 
    } 
    
    int oldHeight = this.myHeight;
    if (newHeight < oldHeight) {
      int count = oldHeight - newHeight;
      if (!this.myAlternateBuffer) {

        
        int emptyLinesDeleted = this.myScreenBuffer.removeBottomEmptyLines(oldHeight - 1, count);
        this.myScreenBuffer.moveTopLinesTo(count - emptyLinesDeleted, this.myHistoryBuffer);
        newCursorY = cursorY - count - emptyLinesDeleted;
      } else {
        newCursorY = cursorY;
      } 
      if (mySelection != null) {
        mySelection.shiftY(-count);
      }
    } else if (newHeight > oldHeight) {
      if (!this.myAlternateBuffer) {
        
        int historyLinesCount = Math.min(newHeight - oldHeight, this.myHistoryBuffer.getLineCount());
        this.myHistoryBuffer.moveBottomLinesTo(historyLinesCount, this.myScreenBuffer);
        newCursorY = cursorY + historyLinesCount;
      } else {
        newCursorY = cursorY;
      } 
      if (mySelection != null) {
        mySelection.shiftY(newHeight - cursorY);
      }
    } 
    
    this.myWidth = newWidth;
    this.myHeight = newHeight;

    
    resizeHandler.sizeUpdated(this.myWidth, this.myHeight, newCursorX, newCursorY);

    
    fireModelChangeEvent();
    
    return pendingResize;
  }
  
  public void addModelListener(TerminalModelListener listener) {
    this.myListeners.add(listener);
  }
  
  public void removeModelListener(TerminalModelListener listener) {
    this.myListeners.remove(listener);
  }
  
  private void fireModelChangeEvent() {
    for (TerminalModelListener modelListener : this.myListeners) {
      modelListener.modelChanged();
    }
  }
  
  private TextStyle createEmptyStyleWithCurrentColor() {
    return this.myStyleState.getCurrent().createEmptyWithColors();
  }
  
  private TerminalLine.TextEntry createFillerEntry() {
    return new TerminalLine.TextEntry(createEmptyStyleWithCurrentColor(), new CharBuffer(false, this.myWidth));
  }
  
  public void deleteCharacters(int x, int y, int count) {
    if (y > this.myHeight - 1 || y < 0) {
      LOG.error("attempt to delete in line " + y + "\nargs were x:" + x + " count:" + count);
    }
    else if (count < 0) {
      LOG.error("Attempt to delete negative chars number: count:" + count);
    } else if (count > 0) {
      this.myScreenBuffer.deleteCharacters(x, y, count, createEmptyStyleWithCurrentColor());
      
      fireModelChangeEvent();
    } 
  }
  
  public void insertBlankCharacters(int x, int y, int count) {
    if (y > this.myHeight - 1 || y < 0) {
      LOG.error("attempt to insert blank chars in line " + y + "\nargs were x:" + x + " count:" + count);
    }
    else if (count < 0) {
      LOG.error("Attempt to insert negative blank chars number: count:" + count);
    } else if (count > 0) {
      this.myScreenBuffer.insertBlankCharacters(x, y, count, this.myWidth, createEmptyStyleWithCurrentColor());
      
      fireModelChangeEvent();
    } 
  }
  
  public void writeString(int x, int y, @NotNull CharBuffer str) {
    if (str == null) $$$reportNull$$$0(9);  writeString(x, y, str, this.myStyleState.getCurrent());
  }
  
  public void addLine(@NotNull TerminalLine line) {
    if (line == null) $$$reportNull$$$0(10);  this.myScreenBuffer.addLines(Lists.newArrayList((Object[])new TerminalLine[] { line }));
    
    fireModelChangeEvent();
  }
  
  private void writeString(int x, int y, @NotNull CharBuffer str, @NotNull TextStyle style) {
    if (str == null) $$$reportNull$$$0(11);  if (style == null) $$$reportNull$$$0(12);  this.myScreenBuffer.writeString(x, y - 1, str, style);
    
    fireModelChangeEvent();
  }
  
  public void scrollArea(int scrollRegionTop, int dy, int scrollRegionBottom) {
    if (dy == 0) {
      return;
    }
    if (dy > 0) {
      insertLines(scrollRegionTop - 1, dy, scrollRegionBottom);
    } else {
      LinesBuffer removed = deleteLines(scrollRegionTop - 1, -dy, scrollRegionBottom);
      if (scrollRegionTop == 1) {
        removed.moveTopLinesTo(removed.getLineCount(), this.myHistoryBuffer);
      }
      
      fireModelChangeEvent();
    } 
  }
  
  public String getStyleLines() {
    final Map<Integer, Integer> hashMap = Maps.newHashMap();
    this.myLock.lock();
    try {
      final StringBuilder sb = new StringBuilder();
      this.myScreenBuffer.processLines(0, this.myHeight, (StyledTextConsumer)new StyledTextConsumerAdapter() {
            int count = 0;

            
            public void consume(int x, int y, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
              if (style == null) $$$reportNull$$$0(0);  if (characters == null) $$$reportNull$$$0(1);  if (x == 0) {
                sb.append("\n");
              }
              int styleNum = style.getId();
              if (!hashMap.containsKey(Integer.valueOf(styleNum))) {
                hashMap.put(Integer.valueOf(styleNum), Integer.valueOf(this.count++));
              }
              sb.append(String.format("%02d ", new Object[] { this.val$hashMap.get(Integer.valueOf(styleNum)) }));
            }
          });
      return sb.toString();
    } finally {
      this.myLock.unlock();
    } 
  }






  
  public TerminalLine getLine(int index) {
    if (index >= 0) {
      if (index >= getHeight()) {
        LOG.error("Attempt to get line out of bounds: " + index + " >= " + getHeight());
        return TerminalLine.createEmpty();
      } 
      return this.myScreenBuffer.getLine(index);
    } 
    if (index < -getHistoryLinesCount()) {
      LOG.error("Attempt to get line out of bounds: " + index + " < " + -getHistoryLinesCount());
      return TerminalLine.createEmpty();
    } 
    return this.myHistoryBuffer.getLine(getHistoryLinesCount() + index);
  }

  
  public String getScreenLines() {
    this.myLock.lock();
    try {
      StringBuilder sb = new StringBuilder();
      for (int row = 0; row < this.myHeight; row++) {
        StringBuilder line = new StringBuilder(this.myScreenBuffer.getLine(row).getText());
        
        for (int i = line.length(); i < this.myWidth; i++) {
          line.append(' ');
        }
        if (line.length() > this.myWidth) {
          line.setLength(this.myWidth);
        }
        
        sb.append(line);
        sb.append('\n');
      } 
      return sb.toString();
    } finally {
      this.myLock.unlock();
    } 
  }
  
  public void processScreenLines(int yStart, int yCount, @NotNull StyledTextConsumer consumer) {
    if (consumer == null) $$$reportNull$$$0(13);  this.myScreenBuffer.processLines(yStart, yCount, consumer);
  }
  
  public void lock() {
    this.myLock.lock();
  }
  
  public void unlock() {
    this.myLock.unlock();
  }
  
  public boolean tryLock() {
    return this.myLock.tryLock();
  }
  
  public int getWidth() {
    return this.myWidth;
  }
  
  public int getHeight() {
    return this.myHeight;
  }
  
  public int getHistoryLinesCount() {
    return this.myHistoryBuffer.getLineCount();
  }
  
  public int getScreenLinesCount() {
    return this.myScreenBuffer.getLineCount();
  }
  
  public char getBuffersCharAt(int x, int y) {
    return getLine(y).charAt(x);
  }
  
  public TextStyle getStyleAt(int x, int y) {
    return getLine(y).getStyleAt(x);
  }
  
  public Pair<Character, TextStyle> getStyledCharAt(int x, int y) {
    synchronized (this.myScreenBuffer) {
      TerminalLine line = getLine(y);
      return new Pair(Character.valueOf(line.charAt(x)), line.getStyleAt(x));
    } 
  }
  
  public char getCharAt(int x, int y) {
    synchronized (this.myScreenBuffer) {
      TerminalLine line = getLine(y);
      return line.charAt(x);
    } 
  }
  
  public boolean isUsingAlternateBuffer() {
    return this.myUsingAlternateBuffer;
  }
  
  public void useAlternateBuffer(boolean enabled) {
    this.myAlternateBuffer = enabled;
    if (enabled) {
      if (!this.myUsingAlternateBuffer) {
        this.myScreenBufferBackup = this.myScreenBuffer;
        this.myHistoryBufferBackup = this.myHistoryBuffer;
        this.myScreenBuffer = createScreenBuffer();
        this.myHistoryBuffer = createHistoryBuffer();
        this.myUsingAlternateBuffer = true;
      }
    
    } else if (this.myUsingAlternateBuffer) {
      this.myScreenBuffer = this.myScreenBufferBackup;
      this.myHistoryBuffer = this.myHistoryBufferBackup;
      this.myScreenBufferBackup = createScreenBuffer();
      this.myHistoryBufferBackup = createHistoryBuffer();
      this.myUsingAlternateBuffer = false;
    } 
    
    fireModelChangeEvent();
  }
  
  public LinesBuffer getHistoryBuffer() {
    return this.myHistoryBuffer;
  }
  
  public void insertLines(int y, int count, int scrollRegionBottom) {
    this.myScreenBuffer.insertLines(y, count, scrollRegionBottom - 1, createFillerEntry());
    
    fireModelChangeEvent();
  }

  
  public LinesBuffer deleteLines(int y, int count, int scrollRegionBottom) {
    LinesBuffer linesBuffer = this.myScreenBuffer.deleteLines(y, count, scrollRegionBottom - 1, createFillerEntry());
    fireModelChangeEvent();
    return linesBuffer;
  }
  
  public void clearLines(int startRow, int endRow) {
    this.myScreenBuffer.clearLines(startRow, endRow, createFillerEntry());
    fireModelChangeEvent();
  }
  
  public void eraseCharacters(int leftX, int rightX, int y) {
    TextStyle style = createEmptyStyleWithCurrentColor();
    if (y >= 0) {
      this.myScreenBuffer.clearArea(leftX, y, rightX, y + 1, style);
      fireModelChangeEvent();
      if (this.myTextProcessing != null && y < getHeight()) {
        this.myTextProcessing.processHyperlinks(this.myScreenBuffer, getLine(y));
      }
    } else {
      LOG.error("Attempt to erase characters in line: " + y);
    } 
  }
  
  public void clearAll() {
    this.myScreenBuffer.clearAll();
    fireModelChangeEvent();
  }



  
  public void processHistoryAndScreenLines(int scrollOrigin, int maximalLinesToProcess, StyledTextConsumer consumer) {
    if (maximalLinesToProcess < 0)
    {
      
      maximalLinesToProcess = this.myHistoryBuffer.getLineCount() + this.myScreenBuffer.getLineCount();
    }
    
    int linesFromHistory = Math.min(-scrollOrigin, maximalLinesToProcess);
    
    int y = this.myHistoryBuffer.getLineCount() + scrollOrigin;
    if (y < 0) {
      y = 0;
    }
    this.myHistoryBuffer.processLines(y, linesFromHistory, consumer, y);
    
    if (linesFromHistory < maximalLinesToProcess)
    {
      this.myScreenBuffer.processLines(0, maximalLinesToProcess - linesFromHistory, consumer, -linesFromHistory);
    }
  }
  
  public void clearHistory() {
    this.myHistoryBuffer.clearAll();
    fireModelChangeEvent();
  }
  
  void moveScreenLinesToHistory() {
    this.myLock.lock();
    try {
      this.myScreenBuffer.removeBottomEmptyLines(this.myScreenBuffer.getLineCount() - 1, this.myScreenBuffer.getLineCount());
      this.myScreenBuffer.moveTopLinesTo(this.myScreenBuffer.getLineCount(), this.myHistoryBuffer);
      if (this.myHistoryBuffer.getLineCount() > 0) {
        this.myHistoryBuffer.getLine(this.myHistoryBuffer.getLineCount() - 1).setWrapped(false);
      }
    } finally {
      
      this.myLock.unlock();
    } 
  }
  
  @NotNull
  LinesBuffer getHistoryBufferOrBackup() {
    if ((this.myUsingAlternateBuffer ? this.myHistoryBufferBackup : this.myHistoryBuffer) == null) $$$reportNull$$$0(14);  return this.myUsingAlternateBuffer ? this.myHistoryBufferBackup : this.myHistoryBuffer;
  }
  
  @NotNull
  LinesBuffer getScreenBufferOrBackup() {
    if ((this.myUsingAlternateBuffer ? this.myScreenBufferBackup : this.myScreenBuffer) == null) $$$reportNull$$$0(15);  return this.myUsingAlternateBuffer ? this.myScreenBufferBackup : this.myScreenBuffer;
  }
  
  public int findScreenLineIndex(@NotNull TerminalLine line) {
    if (line == null) $$$reportNull$$$0(16);  return this.myScreenBuffer.findLineIndex(line);
  }
}
