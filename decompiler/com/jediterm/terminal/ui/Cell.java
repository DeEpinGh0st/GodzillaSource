package com.jediterm.terminal.ui;

final class Cell {
  private final int myLine;
  private final int myColumn;
  
  public Cell(int line, int column) {
    this.myLine = line;
    this.myColumn = column;
  }
  
  public int getLine() {
    return this.myLine;
  }
  
  public int getColumn() {
    return this.myColumn;
  }
}
