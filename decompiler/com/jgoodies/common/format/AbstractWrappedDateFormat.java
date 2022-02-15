package com.jgoodies.common.format;

import com.jgoodies.common.base.Preconditions;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;






























































public abstract class AbstractWrappedDateFormat
  extends DateFormat
{
  protected final DateFormat delegate;
  
  public AbstractWrappedDateFormat(DateFormat delegate) {
    this.delegate = (DateFormat)Preconditions.checkNotNull(delegate, "The %1$s must not be null.", new Object[] { "delegate format" });
  }




  
  public abstract StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);




  
  public abstract Date parse(String paramString, ParsePosition paramParsePosition);



  
  public Calendar getCalendar() {
    return this.delegate.getCalendar();
  }


  
  public void setCalendar(Calendar newCalendar) {
    this.delegate.setCalendar(newCalendar);
  }


  
  public NumberFormat getNumberFormat() {
    return this.delegate.getNumberFormat();
  }


  
  public void setNumberFormat(NumberFormat newNumberFormat) {
    this.delegate.setNumberFormat(newNumberFormat);
  }


  
  public TimeZone getTimeZone() {
    return this.delegate.getTimeZone();
  }


  
  public void setTimeZone(TimeZone zone) {
    this.delegate.setTimeZone(zone);
  }


  
  public boolean isLenient() {
    return this.delegate.isLenient();
  }


  
  public void setLenient(boolean lenient) {
    this.delegate.setLenient(lenient);
  }


  
  public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
    return this.delegate.formatToCharacterIterator(obj);
  }
}
