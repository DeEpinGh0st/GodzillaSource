package org.springframework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

































public final class Property
{
  private static Map<Property, Annotation[]> annotationCache = (Map<Property, Annotation[]>)new ConcurrentReferenceHashMap();
  
  private final Class<?> objectType;
  
  @Nullable
  private final Method readMethod;
  
  @Nullable
  private final Method writeMethod;
  
  private final String name;
  
  private final MethodParameter methodParameter;
  
  @Nullable
  private Annotation[] annotations;

  
  public Property(Class<?> objectType, @Nullable Method readMethod, @Nullable Method writeMethod) {
    this(objectType, readMethod, writeMethod, null);
  }


  
  public Property(Class<?> objectType, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable String name) {
    this.objectType = objectType;
    this.readMethod = readMethod;
    this.writeMethod = writeMethod;
    this.methodParameter = resolveMethodParameter();
    this.name = (name != null) ? name : resolveName();
  }




  
  public Class<?> getObjectType() {
    return this.objectType;
  }



  
  public String getName() {
    return this.name;
  }



  
  public Class<?> getType() {
    return this.methodParameter.getParameterType();
  }



  
  @Nullable
  public Method getReadMethod() {
    return this.readMethod;
  }



  
  @Nullable
  public Method getWriteMethod() {
    return this.writeMethod;
  }



  
  MethodParameter getMethodParameter() {
    return this.methodParameter;
  }
  
  Annotation[] getAnnotations() {
    if (this.annotations == null) {
      this.annotations = resolveAnnotations();
    }
    return this.annotations;
  }



  
  private String resolveName() {
    if (this.readMethod != null) {
      int index = this.readMethod.getName().indexOf("get");
      if (index != -1) {
        index += 3;
      } else {
        
        index = this.readMethod.getName().indexOf("is");
        if (index != -1) {
          index += 2;
        }
        else {
          
          index = 0;
        } 
      } 
      return StringUtils.uncapitalize(this.readMethod.getName().substring(index));
    } 
    if (this.writeMethod != null) {
      int index = this.writeMethod.getName().indexOf("set");
      if (index == -1) {
        throw new IllegalArgumentException("Not a setter method");
      }
      index += 3;
      return StringUtils.uncapitalize(this.writeMethod.getName().substring(index));
    } 
    
    throw new IllegalStateException("Property is neither readable nor writeable");
  }

  
  private MethodParameter resolveMethodParameter() {
    MethodParameter read = resolveReadMethodParameter();
    MethodParameter write = resolveWriteMethodParameter();
    if (write == null) {
      if (read == null) {
        throw new IllegalStateException("Property is neither readable nor writeable");
      }
      return read;
    } 
    if (read != null) {
      Class<?> readType = read.getParameterType();
      Class<?> writeType = write.getParameterType();
      if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
        return read;
      }
    } 
    return write;
  }
  
  @Nullable
  private MethodParameter resolveReadMethodParameter() {
    if (getReadMethod() == null) {
      return null;
    }
    return (new MethodParameter(getReadMethod(), -1)).withContainingClass(getObjectType());
  }
  
  @Nullable
  private MethodParameter resolveWriteMethodParameter() {
    if (getWriteMethod() == null) {
      return null;
    }
    return (new MethodParameter(getWriteMethod(), 0)).withContainingClass(getObjectType());
  }
  
  private Annotation[] resolveAnnotations() {
    Annotation[] annotations = annotationCache.get(this);
    if (annotations == null) {
      Map<Class<? extends Annotation>, Annotation> annotationMap = new LinkedHashMap<>();
      addAnnotationsToMap(annotationMap, getReadMethod());
      addAnnotationsToMap(annotationMap, getWriteMethod());
      addAnnotationsToMap(annotationMap, getField());
      annotations = (Annotation[])annotationMap.values().toArray((Object[])new Annotation[0]);
      annotationCache.put(this, annotations);
    } 
    return annotations;
  }


  
  private void addAnnotationsToMap(Map<Class<? extends Annotation>, Annotation> annotationMap, @Nullable AnnotatedElement object) {
    if (object != null) {
      for (Annotation annotation : object.getAnnotations()) {
        annotationMap.put(annotation.annotationType(), annotation);
      }
    }
  }
  
  @Nullable
  private Field getField() {
    String name = getName();
    if (!StringUtils.hasLength(name)) {
      return null;
    }
    Field field = null;
    Class<?> declaringClass = declaringClass();
    if (declaringClass != null) {
      field = ReflectionUtils.findField(declaringClass, name);
      if (field == null) {
        
        field = ReflectionUtils.findField(declaringClass, StringUtils.uncapitalize(name));
        if (field == null) {
          field = ReflectionUtils.findField(declaringClass, StringUtils.capitalize(name));
        }
      } 
    } 
    return field;
  }
  
  @Nullable
  private Class<?> declaringClass() {
    if (getReadMethod() != null) {
      return getReadMethod().getDeclaringClass();
    }
    if (getWriteMethod() != null) {
      return getWriteMethod().getDeclaringClass();
    }
    
    return null;
  }



  
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Property)) {
      return false;
    }
    Property otherProperty = (Property)other;
    return (ObjectUtils.nullSafeEquals(this.objectType, otherProperty.objectType) && 
      ObjectUtils.nullSafeEquals(this.name, otherProperty.name) && 
      ObjectUtils.nullSafeEquals(this.readMethod, otherProperty.readMethod) && 
      ObjectUtils.nullSafeEquals(this.writeMethod, otherProperty.writeMethod));
  }

  
  public int hashCode() {
    return ObjectUtils.nullSafeHashCode(this.objectType) * 31 + ObjectUtils.nullSafeHashCode(this.name);
  }
}
