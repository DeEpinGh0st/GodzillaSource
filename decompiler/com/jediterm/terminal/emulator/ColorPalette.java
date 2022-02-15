package com.jediterm.terminal.emulator;

import com.jediterm.terminal.TerminalColor;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;




public abstract class ColorPalette
{
  @NotNull
  public Color getForeground(@NotNull TerminalColor color) {
    if (color == null) $$$reportNull$$$0(0);  if (color.isIndexed()) {
      int colorIndex = color.getColorIndex();
      assertColorIndexIsLessThan16(colorIndex);
      if (getForegroundByColorIndex(colorIndex) == null) $$$reportNull$$$0(1);  return getForegroundByColorIndex(colorIndex);
    } 
    if (color.toAwtColor() == null) $$$reportNull$$$0(2);  return color.toAwtColor();
  }

  
  @NotNull
  public Color getBackground(@NotNull TerminalColor color) {
    if (color == null) $$$reportNull$$$0(3);  if (color.isIndexed()) {
      int colorIndex = color.getColorIndex();
      assertColorIndexIsLessThan16(colorIndex);
      if (getBackgroundByColorIndex(colorIndex) == null) $$$reportNull$$$0(4);  return getBackgroundByColorIndex(colorIndex);
    } 
    if (color.toAwtColor() == null) $$$reportNull$$$0(5);  return color.toAwtColor();
  }


  
  private void assertColorIndexIsLessThan16(int colorIndex) {
    if (colorIndex < 0 || colorIndex >= 16)
      throw new AssertionError("Color index is out of bounds [0,15]: " + colorIndex); 
  }
  
  @Nullable
  public static TerminalColor getIndexedTerminalColor(int colorIndex) {
    return (colorIndex < 16) ? TerminalColor.index(colorIndex) : getXTerm256(colorIndex);
  }
  @Nullable
  private static TerminalColor getXTerm256(int colorIndex) {
    return (colorIndex < 256) ? COL_RES_256[colorIndex - 16] : null;
  }

  
  private static final TerminalColor[] COL_RES_256 = new TerminalColor[240];

  
  static {
    for (int red = 0; red < 6; red++) {
      for (int green = 0; green < 6; green++) {
        for (int blue = 0; blue < 6; blue++) {
          COL_RES_256[36 * red + 6 * green + blue] = new TerminalColor(getCubeColorValue(red), 
              getCubeColorValue(green), 
              getCubeColorValue(blue));
        }
      } 
    } 


    
    for (int gray = 0; gray < 24; gray++) {
      int level = 10 * gray + 8;
      COL_RES_256[216 + gray] = new TerminalColor(level, level, level);
    } 
  }
  
  private static int getCubeColorValue(int value) {
    return (value == 0) ? 0 : (40 * value + 55);
  }
  
  @NotNull
  protected abstract Color getForegroundByColorIndex(int paramInt);
  
  @NotNull
  protected abstract Color getBackgroundByColorIndex(int paramInt);
}
