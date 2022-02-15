package com.formdev.flatlaf.util;

import java.awt.image.RGBImageFilter;









public class GrayFilter
  extends RGBImageFilter
{
  private final float brightness;
  private final float contrast;
  private final int alpha;
  private final int origContrast;
  private final int origBrightness;
  
  public static GrayFilter createDisabledIconFilter(boolean dark) {
    return dark ? new GrayFilter(-20, -70, 100) : new GrayFilter(25, -25, 100);
  }







  
  public GrayFilter(int brightness, int contrast, int alpha) {
    this.origBrightness = Math.max(-100, Math.min(100, brightness));
    this.origContrast = Math.max(-100, Math.min(100, contrast));
    this.alpha = Math.max(0, Math.min(100, alpha));
    
    this.brightness = (float)(Math.pow(this.origBrightness, 3.0D) / 10000.0D);
    this.contrast = this.origContrast / 100.0F;
    
    this.canFilterIndexColorModel = true;
  }
  
  public GrayFilter() {
    this(0, 0, 100);
  }
  
  public int getBrightness() {
    return this.origBrightness;
  }
  
  public int getContrast() {
    return this.origContrast;
  }
  
  public int getAlpha() {
    return this.alpha;
  }


  
  public int filterRGB(int x, int y, int rgb) {
    int gray = (int)(0.3D * (rgb >> 16 & 0xFF) + 0.59D * (rgb >> 8 & 0xFF) + 0.11D * (rgb & 0xFF));



    
    if (this.brightness >= 0.0F) {
      gray = (int)((gray + this.brightness * 255.0F) / (1.0F + this.brightness));
    } else {
      gray = (int)(gray / (1.0F - this.brightness));
    } 
    if (this.contrast >= 0.0F)
    { if (gray >= 127) {
        gray = (int)(gray + (255 - gray) * this.contrast);
      } else {
        gray = (int)(gray - gray * this.contrast);
      }  }
    else { gray = (int)(127.0F + (gray - 127) * (this.contrast + 1.0F)); }
    
    int a = (this.alpha != 100) ? ((rgb >> 24 & 0xFF) * this.alpha / 100 << 24) : (rgb & 0xFF000000);


    
    return a | gray << 16 | gray << 8 | gray;
  }
}
