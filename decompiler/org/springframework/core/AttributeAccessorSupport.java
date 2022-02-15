package org.springframework.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;






























public abstract class AttributeAccessorSupport
  implements AttributeAccessor, Serializable
{
  private final Map<String, Object> attributes = new LinkedHashMap<>();


  
  public void setAttribute(String name, @Nullable Object value) {
    Assert.notNull(name, "Name must not be null");
    if (value != null) {
      this.attributes.put(name, value);
    } else {
      
      removeAttribute(name);
    } 
  }

  
  @Nullable
  public Object getAttribute(String name) {
    Assert.notNull(name, "Name must not be null");
    return this.attributes.get(name);
  }


  
  public <T> T computeAttribute(String name, Function<String, T> computeFunction) {
    Assert.notNull(name, "Name must not be null");
    Assert.notNull(computeFunction, "Compute function must not be null");
    Object value = this.attributes.computeIfAbsent(name, computeFunction);
    Assert.state((value != null), () -> String.format("Compute function must not return null for attribute named '%s'", new Object[] { name }));
    
    return (T)value;
  }

  
  @Nullable
  public Object removeAttribute(String name) {
    Assert.notNull(name, "Name must not be null");
    return this.attributes.remove(name);
  }

  
  public boolean hasAttribute(String name) {
    Assert.notNull(name, "Name must not be null");
    return this.attributes.containsKey(name);
  }

  
  public String[] attributeNames() {
    return StringUtils.toStringArray(this.attributes.keySet());
  }





  
  protected void copyAttributesFrom(AttributeAccessor source) {
    Assert.notNull(source, "Source must not be null");
    String[] attributeNames = source.attributeNames();
    for (String attributeName : attributeNames) {
      setAttribute(attributeName, source.getAttribute(attributeName));
    }
  }


  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof AttributeAccessorSupport && this.attributes
      .equals(((AttributeAccessorSupport)other).attributes)));
  }

  
  public int hashCode() {
    return this.attributes.hashCode();
  }
}
