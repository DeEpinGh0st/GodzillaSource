package com.jediterm.terminal.model;
import com.google.common.base.Preconditions;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

class ChangeWidthOperation {
  private static final Logger LOG = Logger.getLogger(TerminalTextBuffer.class);
  
  private final TerminalTextBuffer myTextBuffer;
  private final int myNewWidth;
  private final int myNewHeight;
  private final Map<Point, Point> myTrackingPoints = new HashMap<>();
  private final List<TerminalLine> myAllLines = new ArrayList<>();
  
  private TerminalLine myCurrentLine;
  private int myCurrentLineLength;
  
  ChangeWidthOperation(@NotNull TerminalTextBuffer textBuffer, int newWidth, int newHeight) {
    this.myTextBuffer = textBuffer;
    this.myNewWidth = newWidth;
    this.myNewHeight = newHeight;
  }
  
  void addPointToTrack(@NotNull Point original) {
    if (original == null) $$$reportNull$$$0(1);  this.myTrackingPoints.put(new Point(original), null);
  }
  
  @NotNull
  Point getTrackedPoint(@NotNull Point original) {
    if (original == null) $$$reportNull$$$0(2);  Point result = this.myTrackingPoints.get(new Point(original));
    if (result == null) {
      LOG.warn("Not tracked point: " + original);
      if (original == null) $$$reportNull$$$0(3);  return original;
    } 
    if (result == null) $$$reportNull$$$0(4);  return result;
  }
  
  void run() {
    LinesBuffer historyBuffer = this.myTextBuffer.getHistoryBufferOrBackup();
    for (int i = 0; i < historyBuffer.getLineCount(); i++) {
      TerminalLine line = historyBuffer.getLine(i);
      addLine(line);
    } 
    int screenStartInd = this.myAllLines.size() - 1;
    if (this.myCurrentLine == null || this.myCurrentLineLength == this.myNewWidth) {
      screenStartInd++;
    }
    Preconditions.checkState((screenStartInd >= 0), "screenStartInd < 0: %d", screenStartInd);
    LinesBuffer screenBuffer = this.myTextBuffer.getScreenBufferOrBackup();
    if (screenBuffer.getLineCount() > this.myTextBuffer.getHeight()) {
      LOG.warn("Terminal height < screen buffer line count: " + this.myTextBuffer.getHeight() + " < " + screenBuffer.getLineCount());
    }
    int oldScreenLineCount = Math.min(screenBuffer.getLineCount(), this.myTextBuffer.getHeight()); int j;
    for (j = 0; j < oldScreenLineCount; j++) {
      List<Point> points = findPointsAtY(j);
      for (Point point : points) {
        int newX = (this.myCurrentLineLength + point.x) % this.myNewWidth;
        int newY = this.myAllLines.size() + (this.myCurrentLineLength + point.x) / this.myNewWidth;
        if (this.myCurrentLine != null) {
          newY--;
        }
        this.myTrackingPoints.put(point, new Point(newX, newY));
      } 
      addLine(screenBuffer.getLine(j));
    } 
    for (j = oldScreenLineCount; j < this.myTextBuffer.getHeight(); j++) {
      List<Point> points = findPointsAtY(j);
      for (Point point : points) {
        int newX = point.x % this.myNewWidth;
        int newY = j - oldScreenLineCount + this.myAllLines.size() + point.x / this.myNewWidth;
        this.myTrackingPoints.put(point, new Point(newX, newY));
      } 
    } 
    
    int emptyBottomLineCount = getEmptyBottomLineCount();
    screenStartInd = Math.max(screenStartInd, this.myAllLines.size() - Math.min(this.myAllLines.size(), this.myNewHeight) - emptyBottomLineCount);
    screenStartInd = Math.min(screenStartInd, this.myAllLines.size() - Math.min(this.myAllLines.size(), this.myNewHeight));
    historyBuffer.clearAll();
    historyBuffer.addLines(this.myAllLines.subList(0, screenStartInd));
    screenBuffer.clearAll();
    screenBuffer.addLines(this.myAllLines.subList(screenStartInd, Math.min(screenStartInd + this.myNewHeight, this.myAllLines.size())));
    for (Map.Entry<Point, Point> entry : this.myTrackingPoints.entrySet()) {
      Point p = entry.getValue();
      if (p != null) {
        p.y -= screenStartInd;
      } else {
        p = new Point(entry.getKey());
        entry.setValue(p);
      } 
      p.x = Math.min(this.myNewWidth, Math.max(0, p.x));
      p.y = Math.min(this.myNewHeight, Math.max(0, p.y));
    } 
  }
  
  private int getEmptyBottomLineCount() {
    int result = 0;
    while (result < this.myAllLines.size() && ((TerminalLine)this.myAllLines.get(this.myAllLines.size() - result - 1)).isNul()) {
      result++;
    }
    return result;
  }
  
  @NotNull
  private List<Point> findPointsAtY(int y) {
    List<Point> result = Collections.emptyList();
    for (Point key : this.myTrackingPoints.keySet()) {
      if (key.y == y) {
        if (result.isEmpty()) {
          result = new ArrayList<>();
        }
        result.add(key);
      } 
    } 
    if (result == null) $$$reportNull$$$0(5);  return result;
  }
  
  private void addLine(@NotNull TerminalLine line) {
    if (line == null) $$$reportNull$$$0(6);  if (line.isNul()) {
      if (this.myCurrentLine != null) {
        this.myCurrentLine = null;
        this.myCurrentLineLength = 0;
      } 
      this.myAllLines.add(TerminalLine.createEmpty());
      return;
    } 
    line.forEachEntry(entry -> {
          if (entry.isNul()) {
            return;
          }
          
          for (int entryProcessedLength = 0; entryProcessedLength < entry.getLength(); entryProcessedLength += len) {
            if (this.myCurrentLine != null && this.myCurrentLineLength == this.myNewWidth) {
              this.myCurrentLine.setWrapped(true);
              
              this.myCurrentLine = null;
              this.myCurrentLineLength = 0;
            } 
            if (this.myCurrentLine == null) {
              this.myCurrentLine = new TerminalLine();
              this.myCurrentLineLength = 0;
              this.myAllLines.add(this.myCurrentLine);
            } 
            int len = Math.min(this.myNewWidth - this.myCurrentLineLength, entry.getLength() - entryProcessedLength);
            TerminalLine.TextEntry newEntry = subEntry(entry, entryProcessedLength, len);
            this.myCurrentLine.appendEntry(newEntry);
            this.myCurrentLineLength += len;
          } 
        });
    if (!line.isWrapped()) {
      this.myCurrentLine = null;
      this.myCurrentLineLength = 0;
    } 
  }
  
  @NotNull
  private static TerminalLine.TextEntry subEntry(@NotNull TerminalLine.TextEntry entry, int startInd, int count) {
    if (entry == null) $$$reportNull$$$0(7);  if (startInd == 0 && count == entry.getLength()) {
      if (entry == null) $$$reportNull$$$0(8);  return entry;
    } 
    return new TerminalLine.TextEntry(entry.getStyle(), entry.getText().subBuffer(startInd, count));
  }
}
