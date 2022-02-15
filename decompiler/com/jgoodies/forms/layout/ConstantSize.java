package com.jgoodies.forms.layout;

import com.jgoodies.common.base.Preconditions;
import java.awt.Component;
import java.awt.Container;
import java.io.Serializable;
import java.util.List;





































































public final class ConstantSize
  implements Size, Serializable
{
  public static final Unit PIXEL = new Unit("Pixel", "px", null, true);
  public static final Unit POINT = new Unit("Point", "pt", null, true);
  public static final Unit DIALOG_UNITS_X = new Unit("Dialog units X", "dluX", "dlu", true);
  public static final Unit DIALOG_UNITS_Y = new Unit("Dialog units Y", "dluY", "dlu", true);
  public static final Unit MILLIMETER = new Unit("Millimeter", "mm", null, false);
  public static final Unit CENTIMETER = new Unit("Centimeter", "cm", null, false);
  public static final Unit INCH = new Unit("Inch", "in", null, false);
  
  public static final Unit PX = PIXEL;
  public static final Unit PT = POINT;
  public static final Unit DLUX = DIALOG_UNITS_X;
  public static final Unit DLUY = DIALOG_UNITS_Y;
  public static final Unit MM = MILLIMETER;
  public static final Unit CM = CENTIMETER;
  public static final Unit IN = INCH;




  
  private static final Unit[] VALUES = new Unit[] { PIXEL, POINT, DIALOG_UNITS_X, DIALOG_UNITS_Y, MILLIMETER, CENTIMETER, INCH };





  
  private final double value;




  
  private final Unit unit;





  
  public ConstantSize(int value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }









  
  public ConstantSize(double value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }













  
  static ConstantSize valueOf(String encodedValueAndUnit, boolean horizontal) {
    String[] split = splitValueAndUnit(encodedValueAndUnit);
    String encodedValue = split[0];
    String encodedUnit = split[1];
    Unit unit = Unit.valueOf(encodedUnit, horizontal);
    double value = Double.parseDouble(encodedValue);
    if (unit.requiresIntegers) {
      Preconditions.checkArgument((value == (int)value), "%s value %s must be an integer.", new Object[] { unit, encodedValue });
    }
    
    return new ConstantSize(value, unit);
  }








  
  static ConstantSize dluX(int value) {
    return new ConstantSize(value, DLUX);
  }








  
  static ConstantSize dluY(int value) {
    return new ConstantSize(value, DLUY);
  }










  
  public double getValue() {
    return this.value;
  }








  
  public Unit getUnit() {
    return this.unit;
  }









  
  public int getPixelSize(Component component) {
    if (this.unit == PIXEL)
      return intValue(); 
    if (this.unit == DIALOG_UNITS_X)
      return Sizes.dialogUnitXAsPixel(intValue(), component); 
    if (this.unit == DIALOG_UNITS_Y)
      return Sizes.dialogUnitYAsPixel(intValue(), component); 
    if (this.unit == POINT)
      return Sizes.pointAsPixel(intValue(), component); 
    if (this.unit == INCH)
      return Sizes.inchAsPixel(this.value, component); 
    if (this.unit == MILLIMETER)
      return Sizes.millimeterAsPixel(this.value, component); 
    if (this.unit == CENTIMETER) {
      return Sizes.centimeterAsPixel(this.value, component);
    }
    throw new IllegalStateException("Invalid unit " + this.unit);
  }























  
  public int maximumSize(Container container, List components, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure) {
    return getPixelSize(container);
  }













  
  public boolean compressible() {
    return false;
  }














  
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConstantSize)) {
      return false;
    }
    ConstantSize size = (ConstantSize)o;
    return (this.value == size.value && this.unit == size.unit);
  }













  
  public int hashCode() {
    return (new Double(this.value)).hashCode() + 37 * this.unit.hashCode();
  }











  
  public String toString() {
    return (this.value == intValue()) ? (Integer.toString(intValue()) + this.unit.abbreviation()) : (Double.toString(this.value) + this.unit.abbreviation());
  }











  
  public String encode() {
    return (this.value == intValue()) ? (Integer.toString(intValue()) + this.unit.encode()) : (Double.toString(this.value) + this.unit.encode());
  }





  
  private int intValue() {
    return (int)Math.round(this.value);
  }









  
  private static String[] splitValueAndUnit(String encodedValueAndUnit) {
    String[] result = new String[2];
    int len = encodedValueAndUnit.length();
    int firstLetterIndex = len;
    
    while (firstLetterIndex > 0 && Character.isLetter(encodedValueAndUnit.charAt(firstLetterIndex - 1))) {
      firstLetterIndex--;
    }
    result[0] = encodedValueAndUnit.substring(0, firstLetterIndex);
    result[1] = encodedValueAndUnit.substring(firstLetterIndex);
    return result;
  }



















  
  public static final class Unit
    implements Serializable
  {
    private final transient String name;


















    
    private final transient String abbreviation;

















    
    private final transient String parseAbbreviation;

















    
    final transient boolean requiresIntegers;


















    
    private Unit(String name, String abbreviation, String parseAbbreviation, boolean requiresIntegers) {
      this.ordinal = nextOrdinal++; this.name = name;
      this.abbreviation = abbreviation;
      this.parseAbbreviation = parseAbbreviation;
      this.requiresIntegers = requiresIntegers; } private Object readResolve() { return ConstantSize.VALUES[this.ordinal]; }

    
    static Unit valueOf(String name, boolean horizontal) {
      if (name.length() == 0) {
        Unit defaultUnit = Sizes.getDefaultUnit();
        if (defaultUnit != null)
          return defaultUnit; 
        return horizontal ? ConstantSize.DIALOG_UNITS_X : ConstantSize.DIALOG_UNITS_Y;
      } 
      if (name.equals("px"))
        return ConstantSize.PIXEL; 
      if (name.equals("dlu"))
        return horizontal ? ConstantSize.DIALOG_UNITS_X : ConstantSize.DIALOG_UNITS_Y; 
      if (name.equals("pt"))
        return ConstantSize.POINT; 
      if (name.equals("in"))
        return ConstantSize.INCH; 
      if (name.equals("mm"))
        return ConstantSize.MILLIMETER; 
      if (name.equals("cm"))
        return ConstantSize.CENTIMETER; 
      throw new IllegalArgumentException("Invalid unit name '" + name + "'. Must be one of: " + "px, dlu, pt, mm, cm, in");
    }
    
    public String toString() {
      return this.name;
    }
    
    public String encode() {
      return (this.parseAbbreviation != null) ? this.parseAbbreviation : this.abbreviation;
    }
    
    public String abbreviation() {
      return this.abbreviation;
    }
    
    private static int nextOrdinal = 0;
    private final int ordinal;
  }
}
