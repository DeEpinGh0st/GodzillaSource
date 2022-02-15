package org.springframework.expression.spel.support;

import org.springframework.expression.TypedValue;

























public final class BooleanTypedValue
  extends TypedValue
{
  public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);



  
  public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);

  
  private BooleanTypedValue(boolean b) {
    super(Boolean.valueOf(b));
  }

  
  public static BooleanTypedValue forValue(boolean b) {
    return b ? TRUE : FALSE;
  }
}
