package com.jgoodies.forms.util;

import com.jgoodies.common.bean.Bean;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Toolkit;




















































public abstract class AbstractUnitConverter
  extends Bean
  implements UnitConverter
{
  private static final int DTP_RESOLUTION = 72;
  
  public int inchAsPixel(double in, Component component) {
    return inchAsPixel(in, getScreenResolution(component));
  }










  
  public int millimeterAsPixel(double mm, Component component) {
    return millimeterAsPixel(mm, getScreenResolution(component));
  }










  
  public int centimeterAsPixel(double cm, Component component) {
    return centimeterAsPixel(cm, getScreenResolution(component));
  }










  
  public int pointAsPixel(int pt, Component component) {
    return pointAsPixel(pt, getScreenResolution(component));
  }









  
  public int dialogUnitXAsPixel(int dluX, Component c) {
    return dialogUnitXAsPixel(dluX, getDialogBaseUnitsX(c));
  }










  
  public int dialogUnitYAsPixel(int dluY, Component c) {
    return dialogUnitYAsPixel(dluY, getDialogBaseUnitsY(c));
  }










  
  protected abstract double getDialogBaseUnitsX(Component paramComponent);










  
  protected abstract double getDialogBaseUnitsY(Component paramComponent);










  
  protected static final int inchAsPixel(double in, int dpi) {
    return (int)Math.round(dpi * in);
  }








  
  protected static final int millimeterAsPixel(double mm, int dpi) {
    return (int)Math.round(dpi * mm * 10.0D / 254.0D);
  }








  
  protected static final int centimeterAsPixel(double cm, int dpi) {
    return (int)Math.round(dpi * cm * 100.0D / 254.0D);
  }








  
  protected static final int pointAsPixel(double pt, int dpi) {
    return (int)Math.round(dpi * pt / 72.0D);
  }








  
  protected int dialogUnitXAsPixel(int dluX, double dialogBaseUnitsX) {
    return (int)Math.round(dluX * dialogBaseUnitsX / 4.0D);
  }








  
  protected int dialogUnitYAsPixel(int dluY, double dialogBaseUnitsY) {
    return (int)Math.round(dluY * dialogBaseUnitsY / 8.0D);
  }














  
  protected double computeAverageCharWidth(FontMetrics metrics, String testString) {
    int width = metrics.stringWidth(testString);
    double average = width / testString.length();
    
    return average;
  }







  
  protected int getScreenResolution(Component c) {
    if (c == null) {
      return getDefaultScreenResolution();
    }
    
    Toolkit toolkit = c.getToolkit();
    return (toolkit != null) ? toolkit.getScreenResolution() : getDefaultScreenResolution();
  }



  
  private static int defaultScreenResolution = -1;






  
  protected int getDefaultScreenResolution() {
    if (defaultScreenResolution == -1) {
      defaultScreenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    return defaultScreenResolution;
  }
}
