package com.jgoodies.forms.layout;

import com.jgoodies.forms.util.DefaultUnitConverter;
import com.jgoodies.forms.util.UnitConverter;
import java.awt.Component;
import java.awt.Container;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;



















































public final class Sizes
{
  public static final ConstantSize ZERO = pixel(0);
  
  public static final ConstantSize DLUX1 = dluX(1);
  public static final ConstantSize DLUX2 = dluX(2);
  public static final ConstantSize DLUX3 = dluX(3);
  public static final ConstantSize DLUX4 = dluX(4);
  public static final ConstantSize DLUX5 = dluX(5);
  public static final ConstantSize DLUX6 = dluX(6);
  public static final ConstantSize DLUX7 = dluX(7);
  public static final ConstantSize DLUX8 = dluX(8);
  public static final ConstantSize DLUX9 = dluX(9);
  public static final ConstantSize DLUX11 = dluX(11);
  public static final ConstantSize DLUX14 = dluX(14);



  
  public static final ConstantSize DLUX21 = dluX(21);
  
  public static final ConstantSize DLUY1 = dluY(1);
  public static final ConstantSize DLUY2 = dluY(2);
  public static final ConstantSize DLUY3 = dluY(3);
  public static final ConstantSize DLUY4 = dluY(4);
  public static final ConstantSize DLUY5 = dluY(5);
  public static final ConstantSize DLUY6 = dluY(6);
  public static final ConstantSize DLUY7 = dluY(7);
  public static final ConstantSize DLUY8 = dluY(8);
  public static final ConstantSize DLUY9 = dluY(9);
  public static final ConstantSize DLUY11 = dluY(11);
  public static final ConstantSize DLUY14 = dluY(14);



  
  public static final ConstantSize DLUY21 = dluY(21);






  
  public static final ComponentSize MINIMUM = new ComponentSize("minimum");



  
  public static final ComponentSize PREFERRED = new ComponentSize("preferred");





  
  public static final ComponentSize DEFAULT = new ComponentSize("default");




  
  private static final ComponentSize[] VALUES = new ComponentSize[] { MINIMUM, PREFERRED, DEFAULT };








  
  private static UnitConverter unitConverter;








  
  private static ConstantSize.Unit defaultUnit = ConstantSize.PIXEL;



















  
  public static ConstantSize constant(String encodedValueAndUnit, boolean horizontal) {
    String lowerCase = encodedValueAndUnit.toLowerCase(Locale.ENGLISH);
    String trimmed = lowerCase.trim();
    return ConstantSize.valueOf(trimmed, horizontal);
  }







  
  public static ConstantSize dluX(int value) {
    return ConstantSize.dluX(value);
  }







  
  public static ConstantSize dluY(int value) {
    return ConstantSize.dluY(value);
  }







  
  public static ConstantSize pixel(int value) {
    return new ConstantSize(value, ConstantSize.PIXEL);
  }











  
  public static Size bounded(Size basis, Size lowerBound, Size upperBound) {
    return new BoundedSize(basis, lowerBound, upperBound);
  }










  
  public static int inchAsPixel(double in, Component component) {
    return (in == 0.0D) ? 0 : getUnitConverter().inchAsPixel(in, component);
  }










  
  public static int millimeterAsPixel(double mm, Component component) {
    return (mm == 0.0D) ? 0 : getUnitConverter().millimeterAsPixel(mm, component);
  }










  
  public static int centimeterAsPixel(double cm, Component component) {
    return (cm == 0.0D) ? 0 : getUnitConverter().centimeterAsPixel(cm, component);
  }










  
  public static int pointAsPixel(int pt, Component component) {
    return (pt == 0) ? 0 : getUnitConverter().pointAsPixel(pt, component);
  }










  
  public static int dialogUnitXAsPixel(int dluX, Component component) {
    return (dluX == 0) ? 0 : getUnitConverter().dialogUnitXAsPixel(dluX, component);
  }










  
  public static int dialogUnitYAsPixel(int dluY, Component component) {
    return (dluY == 0) ? 0 : getUnitConverter().dialogUnitYAsPixel(dluY, component);
  }











  
  public static UnitConverter getUnitConverter() {
    if (unitConverter == null) {
      unitConverter = (UnitConverter)DefaultUnitConverter.getInstance();
    }
    return unitConverter;
  }






  
  public static void setUnitConverter(UnitConverter newUnitConverter) {
    unitConverter = newUnitConverter;
  }











  
  public static ConstantSize.Unit getDefaultUnit() {
    return defaultUnit;
  }












  
  public static void setDefaultUnit(ConstantSize.Unit unit) {
    if (unit == ConstantSize.DLUX || unit == ConstantSize.DLUY) {
      throw new IllegalArgumentException("The unit must not be DLUX or DLUY. To use DLU as default unit, invoke this method with null.");
    }

    
    defaultUnit = unit;
  }




















































  
  static final class ComponentSize
    implements Size, Serializable
  {
    private final transient String name;



















































    
    private ComponentSize(String name) {
      this.ordinal = nextOrdinal++;
      this.name = name;
    } private Object readResolve() {
      return Sizes.VALUES[this.ordinal];
    }
    
    static ComponentSize valueOf(String str) {
      if (str.equals("m") || str.equals("min"))
        return Sizes.MINIMUM; 
      if (str.equals("p") || str.equals("pref"))
        return Sizes.PREFERRED; 
      if (str.equals("d") || str.equals("default"))
        return Sizes.DEFAULT; 
      return null;
    }
    
    public int maximumSize(Container container, List components, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure) {
      FormLayout.Measure measure = (this == Sizes.MINIMUM) ? minMeasure : ((this == Sizes.PREFERRED) ? prefMeasure : defaultMeasure);
      int maximum = 0;
      for (Iterator<Component> i = components.iterator(); i.hasNext(); ) {
        Component c = i.next();
        maximum = Math.max(maximum, measure.sizeOf(c));
      } 
      return maximum;
    }
    
    public boolean compressible() {
      return (this == Sizes.DEFAULT);
    }
    
    public String toString() {
      return encode();
    }
    
    public String encode() {
      return this.name.substring(0, 1);
    }
    
    private static int nextOrdinal = 0;
    private final int ordinal;
  }
}
