package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

































public abstract class RepeatableContainers
{
  @Nullable
  private final RepeatableContainers parent;
  
  private RepeatableContainers(@Nullable RepeatableContainers parent) {
    this.parent = parent;
  }










  
  public RepeatableContainers and(Class<? extends Annotation> container, Class<? extends Annotation> repeatable) {
    return new ExplicitRepeatableContainer(this, repeatable, container);
  }
  
  @Nullable
  Annotation[] findRepeatedAnnotations(Annotation annotation) {
    if (this.parent == null) {
      return null;
    }
    return this.parent.findRepeatedAnnotations(annotation);
  }


  
  public boolean equals(@Nullable Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return ObjectUtils.nullSafeEquals(this.parent, ((RepeatableContainers)other).parent);
  }

  
  public int hashCode() {
    return ObjectUtils.nullSafeHashCode(this.parent);
  }






  
  public static RepeatableContainers standardRepeatables() {
    return StandardRepeatableContainers.INSTANCE;
  }













  
  public static RepeatableContainers of(Class<? extends Annotation> repeatable, @Nullable Class<? extends Annotation> container) {
    return new ExplicitRepeatableContainer(null, repeatable, container);
  }





  
  public static RepeatableContainers none() {
    return NoRepeatableContainers.INSTANCE;
  }




  
  private static class StandardRepeatableContainers
    extends RepeatableContainers
  {
    private static final Map<Class<? extends Annotation>, Object> cache = (Map<Class<? extends Annotation>, Object>)new ConcurrentReferenceHashMap();
    
    private static final Object NONE = new Object();
    
    private static StandardRepeatableContainers INSTANCE = new StandardRepeatableContainers();
    
    StandardRepeatableContainers() {
      super(null);
    }

    
    @Nullable
    Annotation[] findRepeatedAnnotations(Annotation annotation) {
      Method method = getRepeatedAnnotationsMethod(annotation.annotationType());
      if (method != null) {
        return (Annotation[])ReflectionUtils.invokeMethod(method, annotation);
      }
      return super.findRepeatedAnnotations(annotation);
    }
    
    @Nullable
    private static Method getRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
      Object result = cache.computeIfAbsent(annotationType, StandardRepeatableContainers::computeRepeatedAnnotationsMethod);
      
      return (result != NONE) ? (Method)result : null;
    }
    
    private static Object computeRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
      AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
      if (methods.hasOnlyValueAttribute()) {
        Method method = methods.get(0);
        Class<?> returnType = method.getReturnType();
        if (returnType.isArray()) {
          Class<?> componentType = returnType.getComponentType();
          if (Annotation.class.isAssignableFrom(componentType) && componentType
            .isAnnotationPresent((Class)Repeatable.class)) {
            return method;
          }
        } 
      } 
      return NONE;
    }
  }


  
  private static class ExplicitRepeatableContainer
    extends RepeatableContainers
  {
    private final Class<? extends Annotation> repeatable;

    
    private final Class<? extends Annotation> container;

    
    private final Method valueMethod;

    
    ExplicitRepeatableContainer(@Nullable RepeatableContainers parent, Class<? extends Annotation> repeatable, @Nullable Class<? extends Annotation> container) {
      super(parent);
      Assert.notNull(repeatable, "Repeatable must not be null");
      if (container == null) {
        container = deduceContainer(repeatable);
      }
      Method valueMethod = AttributeMethods.forAnnotationType(container).get("value");
      try {
        if (valueMethod == null) {
          throw new NoSuchMethodException("No value method found");
        }
        Class<?> returnType = valueMethod.getReturnType();
        if (!returnType.isArray() || returnType.getComponentType() != repeatable) {
          throw new AnnotationConfigurationException("Container type [" + container
              .getName() + "] must declare a 'value' attribute for an array of type [" + repeatable
              
              .getName() + "]");
        }
      }
      catch (AnnotationConfigurationException ex) {
        throw ex;
      }
      catch (Throwable ex) {
        throw new AnnotationConfigurationException("Invalid declaration of container type [" + container
            .getName() + "] for repeatable annotation [" + repeatable
            .getName() + "]", ex);
      } 
      
      this.repeatable = repeatable;
      this.container = container;
      this.valueMethod = valueMethod;
    }
    
    private Class<? extends Annotation> deduceContainer(Class<? extends Annotation> repeatable) {
      Repeatable annotation = repeatable.<Repeatable>getAnnotation(Repeatable.class);
      Assert.notNull(annotation, () -> "Annotation type must be a repeatable annotation: failed to resolve container type for " + repeatable.getName());
      
      return annotation.value();
    }

    
    @Nullable
    Annotation[] findRepeatedAnnotations(Annotation annotation) {
      if (this.container.isAssignableFrom(annotation.annotationType())) {
        return (Annotation[])ReflectionUtils.invokeMethod(this.valueMethod, annotation);
      }
      return super.findRepeatedAnnotations(annotation);
    }

    
    public boolean equals(@Nullable Object other) {
      if (!super.equals(other)) {
        return false;
      }
      ExplicitRepeatableContainer otherErc = (ExplicitRepeatableContainer)other;
      return (this.container.equals(otherErc.container) && this.repeatable.equals(otherErc.repeatable));
    }

    
    public int hashCode() {
      int hashCode = super.hashCode();
      hashCode = 31 * hashCode + this.container.hashCode();
      hashCode = 31 * hashCode + this.repeatable.hashCode();
      return hashCode;
    }
  }



  
  private static class NoRepeatableContainers
    extends RepeatableContainers
  {
    private static NoRepeatableContainers INSTANCE = new NoRepeatableContainers();
    
    NoRepeatableContainers() {
      super(null);
    }
  }
}
