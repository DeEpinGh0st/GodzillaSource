package org.fife.ui.rsyntaxtextarea.parser;

import java.awt.Color;

















































































































public interface ParserNotice
  extends Comparable<ParserNotice>
{
  boolean containsPosition(int paramInt);
  
  Color getColor();
  
  int getLength();
  
  Level getLevel();
  
  int getLine();
  
  boolean getKnowsOffsetAndLength();
  
  String getMessage();
  
  int getOffset();
  
  Parser getParser();
  
  boolean getShowInEditor();
  
  String getToolTipText();
  
  public enum Level
  {
    INFO(2),



    
    WARNING(1),



    
    ERROR(0);
    
    private int value;
    
    Level(int value) {
      this.value = value;
    }





    
    public int getNumericValue() {
      return this.value;
    }







    
    public boolean isEqualToOrWorseThan(Level other) {
      return (this.value <= other.getNumericValue());
    }
  }
}
