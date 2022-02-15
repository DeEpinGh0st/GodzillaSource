package com.formdev.flatlaf.util;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;


























public class DerivedColor
  extends ColorUIResource
{
  private final ColorFunctions.ColorFunction[] functions;
  private boolean hasBaseOfDefaultColor;
  private int baseOfDefaultColorRGB;
  
  public DerivedColor(Color defaultColor, ColorFunctions.ColorFunction... functions) {
    super((defaultColor != null) ? defaultColor : Color.red);
    this.functions = functions;
  }
  
  public Color derive(Color baseColor) {
    if ((this.hasBaseOfDefaultColor && this.baseOfDefaultColorRGB == baseColor.getRGB()) || baseColor == this) {
      return this;
    }
    Color result = ColorFunctions.applyFunctions(baseColor, this.functions);


    
    if (!this.hasBaseOfDefaultColor && result.getRGB() == getRGB()) {
      this.hasBaseOfDefaultColor = true;
      this.baseOfDefaultColorRGB = baseColor.getRGB();
    } 
    
    return result;
  }
  
  public ColorFunctions.ColorFunction[] getFunctions() {
    return this.functions;
  }
}
