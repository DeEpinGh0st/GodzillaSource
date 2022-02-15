package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

























final class AttributeMethods
{
  static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);

  
  private static final Map<Class<? extends Annotation>, AttributeMethods> cache = (Map<Class<? extends Annotation>, AttributeMethods>)new ConcurrentReferenceHashMap();
  
  static {
    methodComparator = ((m1, m2) -> 
      (m1 != null && m2 != null) ? m1.getName().compareTo(m2.getName()) : ((m1 != null) ? -1 : 1));
  }


  
  private static final Comparator<Method> methodComparator;
  
  @Nullable
  private final Class<? extends Annotation> annotationType;
  
  private final Method[] attributeMethods;
  
  private final boolean[] canThrowTypeNotPresentException;
  
  private final boolean hasDefaultValueMethod;
  
  private final boolean hasNestedAnnotation;

  
  private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
    this.annotationType = annotationType;
    this.attributeMethods = attributeMethods;
    this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
    boolean foundDefaultValueMethod = false;
    boolean foundNestedAnnotation = false;
    for (int i = 0; i < attributeMethods.length; i++) {
      Method method = this.attributeMethods[i];
      Class<?> type = method.getReturnType();
      if (method.getDefaultValue() != null) {
        foundDefaultValueMethod = true;
      }
      if (type.isAnnotation() || (type.isArray() && type.getComponentType().isAnnotation())) {
        foundNestedAnnotation = true;
      }
      ReflectionUtils.makeAccessible(method);
      this.canThrowTypeNotPresentException[i] = (type == Class.class || type == Class[].class || type.isEnum());
    } 
    this.hasDefaultValueMethod = foundDefaultValueMethod;
    this.hasNestedAnnotation = foundNestedAnnotation;
  }






  
  boolean hasOnlyValueAttribute() {
    return (this.attributeMethods.length == 1 && "value"
      .equals(this.attributeMethods[0].getName()));
  }








  
  boolean isValid(Annotation annotation) {
    assertAnnotation(annotation);
    for (int i = 0; i < size(); i++) {
      if (canThrowTypeNotPresentException(i)) {
        try {
          get(i).invoke(annotation, new Object[0]);
        }
        catch (Throwable ex) {
          return false;
        } 
      }
    } 
    return true;
  }










  
  void validate(Annotation annotation) {
    assertAnnotation(annotation);
    for (int i = 0; i < size(); i++) {
      if (canThrowTypeNotPresentException(i)) {
        try {
          get(i).invoke(annotation, new Object[0]);
        }
        catch (Throwable ex) {
          throw new IllegalStateException("Could not obtain annotation attribute value for " + 
              get(i).getName() + " declared on " + annotation.annotationType(), ex);
        } 
      }
    } 
  }
  
  private void assertAnnotation(Annotation annotation) {
    Assert.notNull(annotation, "Annotation must not be null");
    if (this.annotationType != null) {
      Assert.isInstanceOf(this.annotationType, annotation);
    }
  }






  
  @Nullable
  Method get(String name) {
    int index = indexOf(name);
    return (index != -1) ? this.attributeMethods[index] : null;
  }







  
  Method get(int index) {
    return this.attributeMethods[index];
  }







  
  boolean canThrowTypeNotPresentException(int index) {
    return this.canThrowTypeNotPresentException[index];
  }






  
  int indexOf(String name) {
    for (int i = 0; i < this.attributeMethods.length; i++) {
      if (this.attributeMethods[i].getName().equals(name)) {
        return i;
      }
    } 
    return -1;
  }






  
  int indexOf(Method attribute) {
    for (int i = 0; i < this.attributeMethods.length; i++) {
      if (this.attributeMethods[i].equals(attribute)) {
        return i;
      }
    } 
    return -1;
  }




  
  int size() {
    return this.attributeMethods.length;
  }




  
  boolean hasDefaultValueMethod() {
    return this.hasDefaultValueMethod;
  }





  
  boolean hasNestedAnnotation() {
    return this.hasNestedAnnotation;
  }






  
  static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
    if (annotationType == null) {
      return NONE;
    }
    return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
  }
  
  private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
    Method[] methods = annotationType.getDeclaredMethods();
    int size = methods.length;
    for (int i = 0; i < methods.length; i++) {
      if (!isAttributeMethod(methods[i])) {
        methods[i] = null;
        size--;
      } 
    } 
    if (size == 0) {
      return NONE;
    }
    Arrays.sort(methods, methodComparator);
    Method[] attributeMethods = Arrays.<Method>copyOf(methods, size);
    return new AttributeMethods(annotationType, attributeMethods);
  }
  
  private static boolean isAttributeMethod(Method method) {
    return (method.getParameterCount() == 0 && method.getReturnType() != void.class);
  }






  
  static String describe(@Nullable Method attribute) {
    if (attribute == null) {
      return "(none)";
    }
    return describe(attribute.getDeclaringClass(), attribute.getName());
  }







  
  static String describe(@Nullable Class<?> annotationType, @Nullable String attributeName) {
    if (attributeName == null) {
      return "(none)";
    }
    String in = (annotationType != null) ? (" in annotation [" + annotationType.getName() + "]") : "";
    return "attribute '" + attributeName + "'" + in;
  }
}
