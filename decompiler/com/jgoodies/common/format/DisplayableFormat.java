package com.jgoodies.common.format;

import com.jgoodies.common.display.Displayable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;








































public final class DisplayableFormat
  extends Format
{
  public static final DisplayableFormat INSTANCE = new DisplayableFormat();



















  
  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    if (obj == null) {
      return toAppendTo;
    }
    if (!(obj instanceof Displayable)) {
      throw new ClassCastException("The object to format must implement the Displayable interface.");
    }
    toAppendTo.append(((Displayable)obj).getDisplayString());
    return toAppendTo;
  }







  
  public Object parseObject(String source, ParsePosition pos) {
    throw new UnsupportedOperationException("The DisplayableFormat cannot parse.");
  }
}
