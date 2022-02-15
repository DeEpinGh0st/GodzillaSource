package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

































final class SynthesizedMergedAnnotationInvocationHandler<A extends Annotation>
  implements InvocationHandler
{
  private final MergedAnnotation<?> annotation;
  private final Class<A> type;
  private final AttributeMethods attributes;
  private final Map<String, Object> valueCache = new ConcurrentHashMap<>(8);
  
  @Nullable
  private volatile Integer hashCode;
  
  @Nullable
  private volatile String string;

  
  private SynthesizedMergedAnnotationInvocationHandler(MergedAnnotation<A> annotation, Class<A> type) {
    Assert.notNull(annotation, "MergedAnnotation must not be null");
    Assert.notNull(type, "Type must not be null");
    Assert.isTrue(type.isAnnotation(), "Type must be an annotation");
    this.annotation = annotation;
    this.type = type;
    this.attributes = AttributeMethods.forAnnotationType(type);
  }


  
  public Object invoke(Object proxy, Method method, Object[] args) {
    if (ReflectionUtils.isEqualsMethod(method)) {
      return Boolean.valueOf(annotationEquals(args[0]));
    }
    if (ReflectionUtils.isHashCodeMethod(method)) {
      return Integer.valueOf(annotationHashCode());
    }
    if (ReflectionUtils.isToStringMethod(method)) {
      return annotationToString();
    }
    if (isAnnotationTypeMethod(method)) {
      return this.type;
    }
    if (this.attributes.indexOf(method.getName()) != -1) {
      return getAttributeValue(method);
    }
    throw new AnnotationConfigurationException(String.format("Method [%s] is unsupported for synthesized annotation type [%s]", new Object[] { method, this.type }));
  }

  
  private boolean isAnnotationTypeMethod(Method method) {
    return (method.getName().equals("annotationType") && method.getParameterCount() == 0);
  }




  
  private boolean annotationEquals(Object other) {
    if (this == other) {
      return true;
    }
    if (!this.type.isInstance(other)) {
      return false;
    }
    for (int i = 0; i < this.attributes.size(); i++) {
      Method attribute = this.attributes.get(i);
      Object thisValue = getAttributeValue(attribute);
      Object otherValue = ReflectionUtils.invokeMethod(attribute, other);
      if (!ObjectUtils.nullSafeEquals(thisValue, otherValue)) {
        return false;
      }
    } 
    return true;
  }



  
  private int annotationHashCode() {
    Integer hashCode = this.hashCode;
    if (hashCode == null) {
      hashCode = computeHashCode();
      this.hashCode = hashCode;
    } 
    return hashCode.intValue();
  }
  
  private Integer computeHashCode() {
    int hashCode = 0;
    for (int i = 0; i < this.attributes.size(); i++) {
      Method attribute = this.attributes.get(i);
      Object value = getAttributeValue(attribute);
      hashCode += 127 * attribute.getName().hashCode() ^ getValueHashCode(value);
    } 
    return Integer.valueOf(hashCode);
  }


  
  private int getValueHashCode(Object value) {
    if (value instanceof boolean[]) {
      return Arrays.hashCode((boolean[])value);
    }
    if (value instanceof byte[]) {
      return Arrays.hashCode((byte[])value);
    }
    if (value instanceof char[]) {
      return Arrays.hashCode((char[])value);
    }
    if (value instanceof double[]) {
      return Arrays.hashCode((double[])value);
    }
    if (value instanceof float[]) {
      return Arrays.hashCode((float[])value);
    }
    if (value instanceof int[]) {
      return Arrays.hashCode((int[])value);
    }
    if (value instanceof long[]) {
      return Arrays.hashCode((long[])value);
    }
    if (value instanceof short[]) {
      return Arrays.hashCode((short[])value);
    }
    if (value instanceof Object[]) {
      return Arrays.hashCode((Object[])value);
    }
    return value.hashCode();
  }
  
  private String annotationToString() {
    String string = this.string;
    if (string == null) {
      StringBuilder builder = (new StringBuilder("@")).append(this.type.getName()).append('(');
      for (int i = 0; i < this.attributes.size(); i++) {
        Method attribute = this.attributes.get(i);
        if (i > 0) {
          builder.append(", ");
        }
        builder.append(attribute.getName());
        builder.append('=');
        builder.append(toString(getAttributeValue(attribute)));
      } 
      builder.append(')');
      string = builder.toString();
      this.string = string;
    } 
    return string;
  }
  
  private String toString(Object value) {
    if (value instanceof Class) {
      return ((Class)value).getName();
    }
    if (value.getClass().isArray()) {
      StringBuilder builder = new StringBuilder("[");
      for (int i = 0; i < Array.getLength(value); i++) {
        if (i > 0) {
          builder.append(", ");
        }
        builder.append(toString(Array.get(value, i)));
      } 
      builder.append(']');
      return builder.toString();
    } 
    return String.valueOf(value);
  }
  
  private Object getAttributeValue(Method method) {
    Object value = this.valueCache.computeIfAbsent(method.getName(), attributeName -> {
          Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(method.getReturnType());

          
          return this.annotation.getValue(attributeName, type).orElseThrow(());
        });

    
    if (value.getClass().isArray() && Array.getLength(value) > 0) {
      value = cloneArray(value);
    }
    
    return value;
  }




  
  private Object cloneArray(Object array) {
    if (array instanceof boolean[]) {
      return ((boolean[])array).clone();
    }
    if (array instanceof byte[]) {
      return ((byte[])array).clone();
    }
    if (array instanceof char[]) {
      return ((char[])array).clone();
    }
    if (array instanceof double[]) {
      return ((double[])array).clone();
    }
    if (array instanceof float[]) {
      return ((float[])array).clone();
    }
    if (array instanceof int[]) {
      return ((int[])array).clone();
    }
    if (array instanceof long[]) {
      return ((long[])array).clone();
    }
    if (array instanceof short[]) {
      return ((short[])array).clone();
    }

    
    return ((Object[])array).clone();
  }

  
  static <A extends Annotation> A createProxy(MergedAnnotation<A> annotation, Class<A> type) {
    ClassLoader classLoader = type.getClassLoader();
    InvocationHandler<A> handler = new SynthesizedMergedAnnotationInvocationHandler<>(annotation, type);
    (new Class[2])[0] = type; (new Class[2])[1] = SynthesizedAnnotation.class; (new Class[1])[0] = type; Class<?>[] interfaces = isVisible(classLoader, SynthesizedAnnotation.class) ? new Class[2] : new Class[1];
    
    return (A)Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  
  private static boolean isVisible(ClassLoader classLoader, Class<?> interfaceClass) {
    if (classLoader == interfaceClass.getClassLoader()) {
      return true;
    }
    try {
      return (Class.forName(interfaceClass.getName(), false, classLoader) == interfaceClass);
    }
    catch (ClassNotFoundException ex) {
      return false;
    } 
  }
}
