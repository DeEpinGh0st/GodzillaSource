package org.springframework.core.style;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;































public class DefaultToStringStyler
  implements ToStringStyler
{
  private final ValueStyler valueStyler;
  
  public DefaultToStringStyler(ValueStyler valueStyler) {
    Assert.notNull(valueStyler, "ValueStyler must not be null");
    this.valueStyler = valueStyler;
  }



  
  protected final ValueStyler getValueStyler() {
    return this.valueStyler;
  }


  
  public void styleStart(StringBuilder buffer, Object obj) {
    if (!obj.getClass().isArray()) {
      buffer.append('[').append(ClassUtils.getShortName(obj.getClass()));
      styleIdentityHashCode(buffer, obj);
    } else {
      
      buffer.append('[');
      styleIdentityHashCode(buffer, obj);
      buffer.append(' ');
      styleValue(buffer, obj);
    } 
  }
  
  private void styleIdentityHashCode(StringBuilder buffer, Object obj) {
    buffer.append('@');
    buffer.append(ObjectUtils.getIdentityHexString(obj));
  }

  
  public void styleEnd(StringBuilder buffer, Object o) {
    buffer.append(']');
  }

  
  public void styleField(StringBuilder buffer, String fieldName, @Nullable Object value) {
    styleFieldStart(buffer, fieldName);
    styleValue(buffer, value);
    styleFieldEnd(buffer, fieldName);
  }
  
  protected void styleFieldStart(StringBuilder buffer, String fieldName) {
    buffer.append(' ').append(fieldName).append(" = ");
  }

  
  protected void styleFieldEnd(StringBuilder buffer, String fieldName) {}

  
  public void styleValue(StringBuilder buffer, @Nullable Object value) {
    buffer.append(this.valueStyler.style(value));
  }

  
  public void styleFieldSeparator(StringBuilder buffer) {
    buffer.append(',');
  }
}
