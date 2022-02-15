package org.springframework.core.type;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;







































public class StandardAnnotationMetadata
  extends StandardClassMetadata
  implements AnnotationMetadata
{
  private final MergedAnnotations mergedAnnotations;
  private final boolean nestedAnnotationsAsMap;
  @Nullable
  private Set<String> annotationTypes;
  
  @Deprecated
  public StandardAnnotationMetadata(Class<?> introspectedClass) {
    this(introspectedClass, false);
  }















  
  @Deprecated
  public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
    super(introspectedClass);
    this.mergedAnnotations = MergedAnnotations.from(introspectedClass, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, 
        RepeatableContainers.none());
    this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
  }


  
  public MergedAnnotations getAnnotations() {
    return this.mergedAnnotations;
  }

  
  public Set<String> getAnnotationTypes() {
    Set<String> annotationTypes = this.annotationTypes;
    if (annotationTypes == null) {
      annotationTypes = Collections.unmodifiableSet(super.getAnnotationTypes());
      this.annotationTypes = annotationTypes;
    } 
    return annotationTypes;
  }

  
  @Nullable
  public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    if (this.nestedAnnotationsAsMap) {
      return super.getAnnotationAttributes(annotationName, classValuesAsString);
    }
    return (Map<String, Object>)AnnotatedElementUtils.getMergedAnnotationAttributes(
        getIntrospectedClass(), annotationName, classValuesAsString, false);
  }

  
  @Nullable
  public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    if (this.nestedAnnotationsAsMap) {
      return super.getAllAnnotationAttributes(annotationName, classValuesAsString);
    }
    return AnnotatedElementUtils.getAllAnnotationAttributes(
        getIntrospectedClass(), annotationName, classValuesAsString, false);
  }

  
  public boolean hasAnnotatedMethods(String annotationName) {
    if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
      try {
        Method[] methods = ReflectionUtils.getDeclaredMethods(getIntrospectedClass());
        for (Method method : methods) {
          if (isAnnotatedMethod(method, annotationName)) {
            return true;
          }
        }
      
      } catch (Throwable ex) {
        throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
      } 
    }
    return false;
  }


  
  public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
    Set<MethodMetadata> annotatedMethods = null;
    if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
      try {
        Method[] methods = ReflectionUtils.getDeclaredMethods(getIntrospectedClass());
        for (Method method : methods) {
          if (isAnnotatedMethod(method, annotationName)) {
            if (annotatedMethods == null) {
              annotatedMethods = new LinkedHashSet<>(4);
            }
            annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
          }
        
        } 
      } catch (Throwable ex) {
        throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
      } 
    }
    return (annotatedMethods != null) ? annotatedMethods : Collections.<MethodMetadata>emptySet();
  }

  
  private static boolean isAnnotatedMethod(Method method, String annotationName) {
    return (!method.isBridge() && (method.getAnnotations()).length > 0 && 
      AnnotatedElementUtils.isAnnotated(method, annotationName));
  }
  
  static AnnotationMetadata from(Class<?> introspectedClass) {
    return new StandardAnnotationMetadata(introspectedClass, true);
  }
}
