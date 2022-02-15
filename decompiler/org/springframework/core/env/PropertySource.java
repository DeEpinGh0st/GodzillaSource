package org.springframework.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;




















































public abstract class PropertySource<T>
{
  protected final Log logger = LogFactory.getLog(getClass());


  
  protected final String name;


  
  protected final T source;


  
  public PropertySource(String name, T source) {
    Assert.hasText(name, "Property source name must contain at least one character");
    Assert.notNull(source, "Property source must not be null");
    this.name = name;
    this.source = source;
  }







  
  public PropertySource(String name) {
    this(name, (T)new Object());
  }




  
  public String getName() {
    return this.name;
  }



  
  public T getSource() {
    return this.source;
  }







  
  public boolean containsProperty(String name) {
    return (getProperty(name) != null);
  }








  
  @Nullable
  public abstract Object getProperty(String paramString);








  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof PropertySource && 
      ObjectUtils.nullSafeEquals(getName(), ((PropertySource)other).getName())));
  }





  
  public int hashCode() {
    return ObjectUtils.nullSafeHashCode(getName());
  }










  
  public String toString() {
    if (this.logger.isDebugEnabled()) {
      return getClass().getSimpleName() + "@" + System.identityHashCode(this) + " {name='" + 
        getName() + "', properties=" + getSource() + "}";
    }
    
    return getClass().getSimpleName() + " {name='" + getName() + "'}";
  }



















  
  public static PropertySource<?> named(String name) {
    return new ComparisonPropertySource(name);
  }












  
  public static class StubPropertySource
    extends PropertySource<Object>
  {
    public StubPropertySource(String name) {
      super(name, new Object());
    }




    
    @Nullable
    public String getProperty(String name) {
      return null;
    }
  }




  
  static class ComparisonPropertySource
    extends StubPropertySource
  {
    private static final String USAGE_ERROR = "ComparisonPropertySource instances are for use with collection comparison only";



    
    public ComparisonPropertySource(String name) {
      super(name);
    }

    
    public Object getSource() {
      throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
    }

    
    public boolean containsProperty(String name) {
      throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
    }

    
    @Nullable
    public String getProperty(String name) {
      throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
    }
  }
}
