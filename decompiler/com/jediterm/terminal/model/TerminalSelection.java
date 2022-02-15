package com.jediterm.terminal.model;

import com.jediterm.terminal.util.Pair;
import java.awt.Point;
import org.jetbrains.annotations.Nullable;





public class TerminalSelection
{
  private final Point myStart;
  private Point myEnd;
  
  public TerminalSelection(Point start) {
    this.myStart = start;
  }
  
  public TerminalSelection(Point start, Point end) {
    this.myStart = start;
    this.myEnd = end;
  }
  
  public Point getStart() {
    return this.myStart;
  }
  
  public Point getEnd() {
    return this.myEnd;
  }
  
  public void updateEnd(Point end) {
    this.myEnd = end;
  }
  
  public Pair<Point, Point> pointsForRun(int width) {
    Pair<Point, Point> p = SelectionUtil.sortPoints(new Point(this.myStart), new Point(this.myEnd));
    ((Point)p.second).x = Math.min(((Point)p.second).x + 1, width);
    return p;
  }
  
  public boolean contains(Point toTest) {
    return intersects(toTest.x, toTest.y, 1);
  }
  
  public void shiftY(int dy) {
    this.myStart.y += dy;
    this.myEnd.y += dy;
  }
  
  public boolean intersects(int x, int row, int length) {
    return (null != intersect(x, row, length));
  }
  
  @Nullable
  public Pair<Integer, Integer> intersect(int x, int row, int length) {
    int newLength, newX = x;

    
    Pair<Point, Point> p = SelectionUtil.sortPoints(new Point(this.myStart), new Point(this.myEnd));
    
    if (((Point)p.first).y == row) {
      newX = Math.max(x, ((Point)p.first).x);
    }
    
    if (((Point)p.second).y == row) {
      newLength = Math.min(((Point)p.second).x, x + length - 1) - newX + 1;
    } else {
      newLength = length - newX + x;
    } 
    
    if (newLength <= 0 || row < ((Point)p.first).y || row > ((Point)p.second).y) {
      return null;
    }
    return Pair.create(Integer.valueOf(newX), Integer.valueOf(newLength));
  }

  
  public String toString() {
    return "[x=" + this.myStart.x + ",y=" + this.myStart.y + "] -> [x=" + this.myEnd.x + ",y=" + this.myEnd.y + "]";
  }
}
