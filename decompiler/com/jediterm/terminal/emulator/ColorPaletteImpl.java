package com.jediterm.terminal.emulator;

import java.awt.Color;
import org.jetbrains.annotations.NotNull;

public class ColorPaletteImpl
  extends ColorPalette
{
  private static final Color[] XTERM_COLORS = new Color[] { new Color(0), new Color(13434880), new Color(52480), new Color(13487360), new Color(2003199), new Color(13435085), new Color(52685), new Color(15066597), new Color(5000268), new Color(16711680), new Color(65280), new Color(16776960), new Color(4620980), new Color(16711935), new Color(65535), new Color(16777215) };


















  
  public static final ColorPalette XTERM_PALETTE = new ColorPaletteImpl(XTERM_COLORS);
  
  private static final Color[] WINDOWS_COLORS = new Color[] { new Color(0), new Color(8388608), new Color(32768), new Color(8421376), new Color(128), new Color(8388736), new Color(32896), new Color(12632256), new Color(8421504), new Color(16711680), new Color(65280), new Color(16776960), new Color(4620980), new Color(16711935), new Color(65535), new Color(16777215) };


















  
  public static final ColorPalette WINDOWS_PALETTE = new ColorPaletteImpl(WINDOWS_COLORS);
  
  private final Color[] myColors;
  
  private ColorPaletteImpl(@NotNull Color[] colors) {
    this.myColors = colors;
  }

  
  @NotNull
  public Color getForegroundByColorIndex(int colorIndex) {
    if (this.myColors[colorIndex] == null) $$$reportNull$$$0(1);  return this.myColors[colorIndex];
  }

  
  @NotNull
  protected Color getBackgroundByColorIndex(int colorIndex) {
    if (this.myColors[colorIndex] == null) $$$reportNull$$$0(2);  return this.myColors[colorIndex];
  }
}
