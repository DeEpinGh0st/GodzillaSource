package com.jediterm.terminal;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;




public class TerminalColor
{
  public static final TerminalColor BLACK = index(0);
  public static final TerminalColor WHITE = index(15);
  
  private final int myColorIndex;
  private final Color myColor;
  private final Supplier<Color> myColorSupplier;
  
  public TerminalColor(int colorIndex) {
    this(colorIndex, (Color)null, (Supplier<Color>)null);
  }
  
  public TerminalColor(int r, int g, int b) {
    this(-1, new Color(r, g, b), (Supplier<Color>)null);
  }
  
  public TerminalColor(@NotNull Supplier<Color> colorSupplier) {
    this(-1, (Color)null, colorSupplier);
  }
  
  private TerminalColor(int colorIndex, @Nullable Color color, @Nullable Supplier<Color> colorSupplier) {
    if (colorIndex != -1) {
      assert color == null;
      assert colorSupplier == null;
    }
    else if (color != null) {
      assert colorSupplier == null;
    } else {
      
      assert colorSupplier != null;
    } 
    this.myColorIndex = colorIndex;
    this.myColor = color;
    this.myColorSupplier = colorSupplier;
  }
  @NotNull
  public static TerminalColor index(int colorIndex) {
    return new TerminalColor(colorIndex);
  }
  
  public static TerminalColor rgb(int r, int g, int b) {
    return new TerminalColor(r, g, b);
  }
  
  public boolean isIndexed() {
    return (this.myColorIndex != -1);
  }
  @NotNull
  public Color toAwtColor() {
    if (isIndexed()) {
      throw new IllegalArgumentException("Color is indexed color so a palette is needed");
    }
    
    if (((this.myColor != null) ? this.myColor : ((Supplier<Color>)Objects.requireNonNull(this.myColorSupplier)).get()) == null) $$$reportNull$$$0(1);  return (this.myColor != null) ? this.myColor : ((Supplier<Color>)Objects.requireNonNull(this.myColorSupplier)).get();
  }
  
  public int getColorIndex() {
    return this.myColorIndex;
  }

  
  public boolean equals(Object o) {
    if (this == o) return true; 
    if (o == null || getClass() != o.getClass()) return false; 
    TerminalColor that = (TerminalColor)o;
    return (this.myColorIndex == that.myColorIndex && Objects.equals(this.myColor, that.myColor));
  }

  
  public int hashCode() {
    return Objects.hash(new Object[] { Integer.valueOf(this.myColorIndex), this.myColor });
  }
  @Nullable
  public static TerminalColor awt(@Nullable Color color) {
    if (color == null) {
      return null;
    }
    return rgb(color.getRed(), color.getGreen(), color.getBlue());
  }
}
