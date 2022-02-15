package com.formdev.flatlaf.util;

import java.awt.Color;






















public class ColorFunctions
{
  public static Color applyFunctions(Color color, ColorFunction... functions) {
    float[] hsl = HSLColor.fromRGB(color);
    float alpha = color.getAlpha() / 255.0F;
    float[] hsla = { hsl[0], hsl[1], hsl[2], alpha * 100.0F };
    
    for (ColorFunction function : functions) {
      function.apply(hsla);
    }
    return HSLColor.toRGB(hsla[0], hsla[1], hsla[2], hsla[3] / 100.0F);
  }
  
  public static float clamp(float value) {
    return (value < 0.0F) ? 0.0F : ((value > 100.0F) ? 100.0F : value);
  }













  
  public static Color mix(Color color1, Color color2, float weight) {
    if (weight >= 1.0F)
      return color1; 
    if (weight <= 0.0F) {
      return color2;
    }
    int r1 = color1.getRed();
    int g1 = color1.getGreen();
    int b1 = color1.getBlue();
    int a1 = color1.getAlpha();
    
    int r2 = color2.getRed();
    int g2 = color2.getGreen();
    int b2 = color2.getBlue();
    int a2 = color2.getAlpha();
    
    return new Color(
        Math.round(r2 + (r1 - r2) * weight), 
        Math.round(g2 + (g1 - g2) * weight), 
        Math.round(b2 + (b1 - b2) * weight), 
        Math.round(a2 + (a1 - a2) * weight));
  }


  
  public static interface ColorFunction
  {
    void apply(float[] param1ArrayOffloat);
  }


  
  public static class HSLIncreaseDecrease
    implements ColorFunction
  {
    public final int hslIndex;
    
    public final boolean increase;
    
    public final float amount;
    
    public final boolean relative;
    
    public final boolean autoInverse;

    
    public HSLIncreaseDecrease(int hslIndex, boolean increase, float amount, boolean relative, boolean autoInverse) {
      this.hslIndex = hslIndex;
      this.increase = increase;
      this.amount = amount;
      this.relative = relative;
      this.autoInverse = autoInverse;
    }

    
    public void apply(float[] hsla) {
      float amount2 = this.increase ? this.amount : -this.amount;
      
      if (this.hslIndex == 0) {
        
        hsla[0] = (hsla[0] + amount2) % 360.0F;
        
        return;
      } 
      amount2 = (this.autoInverse && shouldInverse(hsla)) ? -amount2 : amount2;
      hsla[this.hslIndex] = ColorFunctions.clamp(this.relative ? (hsla[this.hslIndex] * (100.0F + amount2) / 100.0F) : (hsla[this.hslIndex] + amount2));
    }


    
    protected boolean shouldInverse(float[] hsla) {
      return this.increase ? ((hsla[this.hslIndex] > 65.0F)) : ((hsla[this.hslIndex] < 35.0F));
    }
  }




  
  public static class Fade
    implements ColorFunction
  {
    public final float amount;



    
    public Fade(float amount) {
      this.amount = amount;
    }

    
    public void apply(float[] hsla) {
      hsla[3] = ColorFunctions.clamp(this.amount);
    }
  }
}
