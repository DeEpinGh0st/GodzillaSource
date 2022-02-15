package com.jgoodies.common.format;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;




























































public final class EmptyNumberFormat
  extends NumberFormat
{
  private final NumberFormat delegate;
  private final Number emptyValue;
  
  public EmptyNumberFormat(NumberFormat delegate) {
    this(delegate, (Number)null);
  }











  
  public EmptyNumberFormat(NumberFormat delegate, int emptyValue) {
    this(delegate, Integer.valueOf(emptyValue));
  }











  
  public EmptyNumberFormat(NumberFormat delegate, Number emptyValue) {
    this.delegate = (NumberFormat)Preconditions.checkNotNull(delegate, "The %1$s must not be null.", new Object[] { "delegate format" });
    this.emptyValue = emptyValue;
  }












  
  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    return Objects.equals(obj, this.emptyValue) ? toAppendTo : this.delegate.format(obj, toAppendTo, pos);
  }






  
  public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
    return this.delegate.format(number, toAppendTo, pos);
  }




  
  public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
    return this.delegate.format(number, toAppendTo, pos);
  }









  
  public Object parseObject(String source) throws ParseException {
    return Strings.isBlank(source) ? this.emptyValue : super.parseObject(source);
  }











  
  public Number parse(String source) throws ParseException {
    return Strings.isBlank(source) ? this.emptyValue : super.parse(source);
  }




  
  public Number parse(String source, ParsePosition pos) {
    return this.delegate.parse(source, pos);
  }
}
