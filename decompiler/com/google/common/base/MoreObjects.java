package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;













































@GwtCompatible
public final class MoreObjects
{
  public static <T> T firstNonNull(T first, T second) {
    if (first != null) {
      return first;
    }
    if (second != null) {
      return second;
    }
    throw new NullPointerException("Both parameters are null");
  }








































  
  public static ToStringHelper toStringHelper(Object self) {
    return new ToStringHelper(self.getClass().getSimpleName());
  }










  
  public static ToStringHelper toStringHelper(Class<?> clazz) {
    return new ToStringHelper(clazz.getSimpleName());
  }








  
  public static ToStringHelper toStringHelper(String className) {
    return new ToStringHelper(className);
  }



  
  public static final class ToStringHelper
  {
    private final String className;

    
    private final ValueHolder holderHead = new ValueHolder();
    private ValueHolder holderTail = this.holderHead;
    
    private boolean omitNullValues = false;
    
    private ToStringHelper(String className) {
      this.className = Preconditions.<String>checkNotNull(className);
    }







    
    @CanIgnoreReturnValue
    public ToStringHelper omitNullValues() {
      this.omitNullValues = true;
      return this;
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, Object value) {
      return addHolder(name, value);
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, boolean value) {
      return addHolder(name, String.valueOf(value));
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, char value) {
      return addHolder(name, String.valueOf(value));
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, double value) {
      return addHolder(name, String.valueOf(value));
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, float value) {
      return addHolder(name, String.valueOf(value));
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, int value) {
      return addHolder(name, String.valueOf(value));
    }





    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, long value) {
      return addHolder(name, String.valueOf(value));
    }






    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(Object value) {
      return addHolder(value);
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(boolean value) {
      return addHolder(String.valueOf(value));
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(char value) {
      return addHolder(String.valueOf(value));
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(double value) {
      return addHolder(String.valueOf(value));
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(float value) {
      return addHolder(String.valueOf(value));
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(int value) {
      return addHolder(String.valueOf(value));
    }








    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(long value) {
      return addHolder(String.valueOf(value));
    }










    
    public String toString() {
      boolean omitNullValuesSnapshot = this.omitNullValues;
      String nextSeparator = "";
      StringBuilder builder = (new StringBuilder(32)).append(this.className).append('{');
      ValueHolder valueHolder = this.holderHead.next;
      for (; valueHolder != null; 
        valueHolder = valueHolder.next) {
        Object value = valueHolder.value;
        if (!omitNullValuesSnapshot || value != null) {
          builder.append(nextSeparator);
          nextSeparator = ", ";
          
          if (valueHolder.name != null) {
            builder.append(valueHolder.name).append('=');
          }
          if (value != null && value.getClass().isArray()) {
            Object[] objectArray = { value };
            String arrayString = Arrays.deepToString(objectArray);
            builder.append(arrayString, 1, arrayString.length() - 1);
          } else {
            builder.append(value);
          } 
        } 
      } 
      return builder.append('}').toString();
    }
    
    private ValueHolder addHolder() {
      ValueHolder valueHolder = new ValueHolder();
      this.holderTail = this.holderTail.next = valueHolder;
      return valueHolder;
    }
    
    private ToStringHelper addHolder(Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      return this;
    }
    
    private ToStringHelper addHolder(String name, Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      valueHolder.name = Preconditions.<String>checkNotNull(name);
      return this;
    }
    
    private static final class ValueHolder {
      String name;
      Object value;
      ValueHolder next;
      
      private ValueHolder() {}
    }
  }
}
