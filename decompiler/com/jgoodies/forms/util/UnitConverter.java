package com.jgoodies.forms.util;

import java.awt.Component;

public interface UnitConverter {
  int inchAsPixel(double paramDouble, Component paramComponent);
  
  int millimeterAsPixel(double paramDouble, Component paramComponent);
  
  int centimeterAsPixel(double paramDouble, Component paramComponent);
  
  int pointAsPixel(int paramInt, Component paramComponent);
  
  int dialogUnitXAsPixel(int paramInt, Component paramComponent);
  
  int dialogUnitYAsPixel(int paramInt, Component paramComponent);
}
