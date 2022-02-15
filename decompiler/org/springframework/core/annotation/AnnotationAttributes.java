package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;










































public class AnnotationAttributes
  extends LinkedHashMap<String, Object>
{
  private static final String UNKNOWN = "unknown";
  @Nullable
  private final Class<? extends Annotation> annotationType;
  final String displayName;
  boolean validated = false;
  
  public AnnotationAttributes() {
    this.annotationType = null;
    this.displayName = "unknown";
  }





  
  public AnnotationAttributes(int initialCapacity) {
    super(initialCapacity);
    this.annotationType = null;
    this.displayName = "unknown";
  }






  
  public AnnotationAttributes(Map<String, Object> map) {
    super(map);
    this.annotationType = null;
    this.displayName = "unknown";
  }






  
  public AnnotationAttributes(AnnotationAttributes other) {
    super(other);
    this.annotationType = other.annotationType;
    this.displayName = other.displayName;
    this.validated = other.validated;
  }







  
  public AnnotationAttributes(Class<? extends Annotation> annotationType) {
    Assert.notNull(annotationType, "'annotationType' must not be null");
    this.annotationType = annotationType;
    this.displayName = annotationType.getName();
  }









  
  AnnotationAttributes(Class<? extends Annotation> annotationType, boolean validated) {
    Assert.notNull(annotationType, "'annotationType' must not be null");
    this.annotationType = annotationType;
    this.displayName = annotationType.getName();
    this.validated = validated;
  }









  
  public AnnotationAttributes(String annotationType, @Nullable ClassLoader classLoader) {
    Assert.notNull(annotationType, "'annotationType' must not be null");
    this.annotationType = getAnnotationType(annotationType, classLoader);
    this.displayName = annotationType;
  }

  
  @Nullable
  private static Class<? extends Annotation> getAnnotationType(String annotationType, @Nullable ClassLoader classLoader) {
    if (classLoader != null) {
      try {
        return (Class)classLoader.loadClass(annotationType);
      }
      catch (ClassNotFoundException classNotFoundException) {}
    }

    
    return null;
  }






  
  @Nullable
  public Class<? extends Annotation> annotationType() {
    return this.annotationType;
  }








  
  public String getString(String attributeName) {
    return getRequiredAttribute(attributeName, String.class);
  }












  
  public String[] getStringArray(String attributeName) {
    return getRequiredAttribute(attributeName, (Class)String[].class);
  }








  
  public boolean getBoolean(String attributeName) {
    return ((Boolean)getRequiredAttribute(attributeName, Boolean.class)).booleanValue();
  }









  
  public <N extends Number> N getNumber(String attributeName) {
    return (N)getRequiredAttribute(attributeName, Number.class);
  }









  
  public <E extends Enum<?>> E getEnum(String attributeName) {
    return (E)getRequiredAttribute(attributeName, Enum.class);
  }









  
  public <T> Class<? extends T> getClass(String attributeName) {
    return getRequiredAttribute(attributeName, (Class)Class.class);
  }











  
  public Class<?>[] getClassArray(String attributeName) {
    return getRequiredAttribute(attributeName, (Class)Class[].class);
  }











  
  public AnnotationAttributes getAnnotation(String attributeName) {
    return getRequiredAttribute(attributeName, AnnotationAttributes.class);
  }











  
  public <A extends Annotation> A getAnnotation(String attributeName, Class<A> annotationType) {
    return (A)getRequiredAttribute(attributeName, annotationType);
  }














  
  public AnnotationAttributes[] getAnnotationArray(String attributeName) {
    return getRequiredAttribute(attributeName, (Class)AnnotationAttributes[].class);
  }















  
  public <A extends Annotation> A[] getAnnotationArray(String attributeName, Class<A> annotationType) {
    Object array = Array.newInstance(annotationType, 0);
    return (A[])getRequiredAttribute(attributeName, array.getClass());
  }
















  
  private <T> T getRequiredAttribute(String attributeName, Class<T> expectedType) {
    Assert.hasText(attributeName, "'attributeName' must not be null or empty");
    Object value = get(attributeName);
    assertAttributePresence(attributeName, value);
    assertNotException(attributeName, value);
    if (!expectedType.isInstance(value) && expectedType.isArray() && expectedType
      .getComponentType().isInstance(value)) {
      Object array = Array.newInstance(expectedType.getComponentType(), 1);
      Array.set(array, 0, value);
      value = array;
    } 
    assertAttributeType(attributeName, value, expectedType);
    return (T)value;
  }
  
  private void assertAttributePresence(String attributeName, Object attributeValue) {
    Assert.notNull(attributeValue, () -> String.format("Attribute '%s' not found in attributes for annotation [%s]", new Object[] { attributeName, this.displayName }));
  }


  
  private void assertNotException(String attributeName, Object attributeValue) {
    if (attributeValue instanceof Throwable) {
      throw new IllegalArgumentException(String.format("Attribute '%s' for annotation [%s] was not resolvable due to exception [%s]", new Object[] { attributeName, this.displayName, attributeValue }), (Throwable)attributeValue);
    }
  }


  
  private void assertAttributeType(String attributeName, Object attributeValue, Class<?> expectedType) {
    if (!expectedType.isInstance(attributeValue)) {
      throw new IllegalArgumentException(String.format("Attribute '%s' is of type %s, but %s was expected in attributes for annotation [%s]", new Object[] { attributeName, attributeValue
              
              .getClass().getSimpleName(), expectedType.getSimpleName(), this.displayName }));
    }
  }


  
  public String toString() {
    Iterator<Map.Entry<String, Object>> entries = entrySet().iterator();
    StringBuilder sb = new StringBuilder("{");
    while (entries.hasNext()) {
      Map.Entry<String, Object> entry = entries.next();
      sb.append(entry.getKey());
      sb.append('=');
      sb.append(valueToString(entry.getValue()));
      if (entries.hasNext()) {
        sb.append(", ");
      }
    } 
    sb.append('}');
    return sb.toString();
  }
  
  private String valueToString(Object value) {
    if (value == this) {
      return "(this Map)";
    }
    if (value instanceof Object[]) {
      return "[" + StringUtils.arrayToDelimitedString((Object[])value, ", ") + "]";
    }
    return String.valueOf(value);
  }









  
  @Nullable
  public static AnnotationAttributes fromMap(@Nullable Map<String, Object> map) {
    if (map == null) {
      return null;
    }
    if (map instanceof AnnotationAttributes) {
      return (AnnotationAttributes)map;
    }
    return new AnnotationAttributes(map);
  }
}
