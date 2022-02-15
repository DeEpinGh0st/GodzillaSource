package org.springframework.core;

import java.util.function.Function;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;




























































public interface AttributeAccessor
{
  void setAttribute(String paramString, @Nullable Object paramObject);
  
  @Nullable
  Object getAttribute(String paramString);
  
  default <T> T computeAttribute(String name, Function<String, T> computeFunction) {
    Assert.notNull(name, "Name must not be null");
    Assert.notNull(computeFunction, "Compute function must not be null");
    Object value = getAttribute(name);
    if (value == null) {
      value = computeFunction.apply(name);
      Assert.state((value != null), () -> String.format("Compute function must not return null for attribute named '%s'", new Object[] { name }));
      
      setAttribute(name, value);
    } 
    return (T)value;
  }
  
  @Nullable
  Object removeAttribute(String paramString);
  
  boolean hasAttribute(String paramString);
  
  String[] attributeNames();
}
