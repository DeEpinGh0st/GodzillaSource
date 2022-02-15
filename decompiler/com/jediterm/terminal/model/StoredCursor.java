package com.jediterm.terminal.model;

import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.charset.CharacterSet;
import com.jediterm.terminal.emulator.charset.GraphicSetState;
import org.jetbrains.annotations.NotNull;




















public class StoredCursor
{
  private final int myCursorX;
  private final int myCursorY;
  private final TextStyle myTextStyle;
  private final int myGLMapping;
  private final int myGRMapping;
  private final boolean myAutoWrap;
  private final boolean myOriginMode;
  private final int myGLOverride;
  private final CharacterSet[] myDesignations = new CharacterSet[4];





  
  public StoredCursor(int cursorX, int cursorY, @NotNull TextStyle textStyle, boolean autoWrap, boolean originMode, GraphicSetState graphicSetState) {
    this.myCursorX = cursorX;
    this.myCursorY = cursorY;
    this.myTextStyle = textStyle;
    this.myAutoWrap = autoWrap;
    this.myOriginMode = originMode;
    this.myGLMapping = graphicSetState.getGL().getIndex();
    this.myGRMapping = graphicSetState.getGR().getIndex();
    this.myGLOverride = graphicSetState.getGLOverrideIndex();
    for (int i = 0; i < 4; i++) {
      this.myDesignations[i] = graphicSetState.getGraphicSet(i).getDesignation();
    }
  }
  
  public int getCursorX() {
    return this.myCursorX;
  }
  
  public int getCursorY() {
    return this.myCursorY;
  }
  
  public TextStyle getTextStyle() {
    return this.myTextStyle;
  }
  
  public int getGLMapping() {
    return this.myGLMapping;
  }
  
  public int getGRMapping() {
    return this.myGRMapping;
  }
  
  public boolean isAutoWrap() {
    return this.myAutoWrap;
  }
  
  public boolean isOriginMode() {
    return this.myOriginMode;
  }
  
  public int getGLOverride() {
    return this.myGLOverride;
  }
  
  public CharacterSet[] getDesignations() {
    return this.myDesignations;
  }
}
