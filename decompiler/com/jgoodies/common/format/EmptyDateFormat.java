package com.jgoodies.common.format;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Strings;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;




































































public final class EmptyDateFormat
  extends AbstractWrappedDateFormat
{
  private final Date emptyValue;
  
  public EmptyDateFormat(DateFormat delegate) {
    this(delegate, null);
  }











  
  public EmptyDateFormat(DateFormat delegate, Date emptyValue) {
    super(delegate);
    this.emptyValue = emptyValue;
  }





  
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
    return Objects.equals(date, this.emptyValue) ? toAppendTo : this.delegate.format(date, toAppendTo, pos);
  }




  
  public Date parse(String source, ParsePosition pos) {
    if (Strings.isBlank(source)) {

      
      pos.setIndex(1);
      return this.emptyValue;
    } 
    return this.delegate.parse(source, pos);
  }
}
