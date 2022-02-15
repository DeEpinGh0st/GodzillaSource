package com.jediterm.terminal.model;

import com.jediterm.terminal.util.Pair;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;





public class SelectionUtil
{
  private static final Logger LOG = Logger.getLogger(SelectionUtil.class);
  
  private static final List<Character> SEPARATORS = new ArrayList<>();
  static {
    SEPARATORS.add(Character.valueOf(' '));
    SEPARATORS.add(Character.valueOf(' '));
    SEPARATORS.add(Character.valueOf('\t'));
    SEPARATORS.add(Character.valueOf('\''));
    SEPARATORS.add(Character.valueOf('"'));
    SEPARATORS.add(Character.valueOf('$'));
    SEPARATORS.add(Character.valueOf('('));
    SEPARATORS.add(Character.valueOf(')'));
    SEPARATORS.add(Character.valueOf('['));
    SEPARATORS.add(Character.valueOf(']'));
    SEPARATORS.add(Character.valueOf('{'));
    SEPARATORS.add(Character.valueOf('}'));
    SEPARATORS.add(Character.valueOf('<'));
    SEPARATORS.add(Character.valueOf('>'));
  }
  
  public static List<Character> getDefaultSeparators() {
    return new ArrayList<>(SEPARATORS);
  }
  
  public static Pair<Point, Point> sortPoints(Point a, Point b) {
    if (a.y == b.y) {
      return Pair.create((a.x <= b.x) ? a : b, (a.x > b.x) ? a : b);
    }
    
    return Pair.create((a.y < b.y) ? a : b, (a.y > b.y) ? a : b);
  }

  
  public static String getSelectionText(TerminalSelection selection, TerminalTextBuffer terminalTextBuffer) {
    return getSelectionText(selection.getStart(), selection.getEnd(), terminalTextBuffer);
  }



  
  @NotNull
  public static String getSelectionText(@NotNull Point selectionStart, @NotNull Point selectionEnd, @NotNull TerminalTextBuffer terminalTextBuffer) {
    if (selectionStart == null) $$$reportNull$$$0(0);  if (selectionEnd == null) $$$reportNull$$$0(1);  if (terminalTextBuffer == null) $$$reportNull$$$0(2);  Pair<Point, Point> pair = sortPoints(selectionStart, selectionEnd);
    ((Point)pair.first).y = Math.max(((Point)pair.first).y, -terminalTextBuffer.getHistoryLinesCount());
    pair = sortPoints((Point)pair.first, (Point)pair.second);
    
    Point top = (Point)pair.first;
    Point bottom = (Point)pair.second;
    
    StringBuilder selectionText = new StringBuilder();
    
    for (int i = top.y; i <= bottom.y; i++) {
      TerminalLine line = terminalTextBuffer.getLine(i);
      String text = line.getText();
      if (i == top.y) {
        if (i == bottom.y) {
          selectionText.append(processForSelection(text.substring(Math.min(text.length(), top.x), Math.min(text.length(), bottom.x))));
        } else {
          selectionText.append(processForSelection(text.substring(Math.min(text.length(), top.x))));
        }
      
      } else if (i == bottom.y) {
        selectionText.append(processForSelection(text.substring(0, Math.min(text.length(), bottom.x))));
      } else {
        
        selectionText.append(processForSelection(line.getText()));
      } 
      if ((!line.isWrapped() && i < bottom.y) || bottom.x > text.length()) {
        selectionText.append("\n");
      }
    } 
    
    if (selectionText.toString() == null) $$$reportNull$$$0(3);  return selectionText.toString();
  }
  
  private static String processForSelection(String text) {
    if (text.indexOf('') != 0) {
      
      StringBuilder sb = new StringBuilder();
      for (char c : text.toCharArray()) {
        if (c != '') {
          sb.append(c);
        }
      } 
      return sb.toString();
    } 
    return text;
  }

  
  public static Point getPreviousSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer) {
    return getPreviousSeparator(charCoords, terminalTextBuffer, SEPARATORS);
  }
  
  public static Point getPreviousSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer, @NotNull List<Character> separators) {
    if (separators == null) $$$reportNull$$$0(4);  int x = charCoords.x;
    int y = charCoords.y;
    int terminalWidth = terminalTextBuffer.getWidth();
    
    if (separators.contains(Character.valueOf(terminalTextBuffer.getBuffersCharAt(x, y)))) {
      return new Point(x, y);
    }
    
    String line = terminalTextBuffer.getLine(y).getText();
    while (x < line.length() && !separators.contains(Character.valueOf(line.charAt(x)))) {
      x--;
      if (x < 0) {
        if (y <= -terminalTextBuffer.getHistoryLinesCount()) {
          return new Point(0, y);
        }
        y--;
        x = terminalWidth - 1;
        
        line = terminalTextBuffer.getLine(y).getText();
      } 
    } 
    
    x++;
    if (x >= terminalWidth) {
      y++;
      x = 0;
    } 
    
    return new Point(x, y);
  }
  
  public static Point getNextSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer) {
    return getNextSeparator(charCoords, terminalTextBuffer, SEPARATORS);
  }
  
  public static Point getNextSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer, @NotNull List<Character> separators) {
    if (separators == null) $$$reportNull$$$0(5);  int x = charCoords.x;
    int y = charCoords.y;
    int terminalWidth = terminalTextBuffer.getWidth();
    int terminalHeight = terminalTextBuffer.getHeight();
    
    if (separators.contains(Character.valueOf(terminalTextBuffer.getBuffersCharAt(x, y)))) {
      return new Point(x, y);
    }
    
    String line = terminalTextBuffer.getLine(y).getText();
    while (x < line.length() && !separators.contains(Character.valueOf(line.charAt(x)))) {
      x++;
      if (x >= terminalWidth) {
        if (y >= terminalHeight - 1) {
          return new Point(terminalWidth - 1, terminalHeight - 1);
        }
        y++;
        x = 0;
        
        line = terminalTextBuffer.getLine(y).getText();
      } 
    } 
    
    x--;
    if (x < 0) {
      y--;
      x = terminalWidth - 1;
    } 
    
    return new Point(x, y);
  }
}
