package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

























































































public abstract class AnnotationUtils
{
  public static final String VALUE = "value";
  private static final AnnotationFilter JAVA_LANG_ANNOTATION_FILTER = AnnotationFilter.packages(new String[] { "java.lang.annotation" });
  
  private static final Map<Class<? extends Annotation>, Map<String, DefaultValueHolder>> defaultValuesCache = (Map<Class<? extends Annotation>, Map<String, DefaultValueHolder>>)new ConcurrentReferenceHashMap();














  
  public static boolean isCandidateClass(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes) {
    for (Class<? extends Annotation> annotationType : annotationTypes) {
      if (isCandidateClass(clazz, annotationType)) {
        return true;
      }
    } 
    return false;
  }











  
  public static boolean isCandidateClass(Class<?> clazz, Class<? extends Annotation> annotationType) {
    return isCandidateClass(clazz, annotationType.getName());
  }











  
  public static boolean isCandidateClass(Class<?> clazz, String annotationName) {
    if (annotationName.startsWith("java.")) {
      return true;
    }
    if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(clazz)) {
      return false;
    }
    return true;
  }














  
  @Nullable
  public static <A extends Annotation> A getAnnotation(Annotation annotation, Class<A> annotationType) {
    if (annotationType.isInstance(annotation)) {
      return synthesizeAnnotation((A)annotation, annotationType);
    }
    
    if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotation)) {
      return null;
    }
    
    return (A)MergedAnnotations.from(annotation, new Annotation[] { annotation }, RepeatableContainers.none())
      .<Annotation>get(annotationType).withNonMergedAttributes()
      .synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
  }













  
  @Nullable
  public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
      return annotatedElement.getAnnotation(annotationType);
    }
    
    return (A)MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none())
      .<Annotation>get(annotationType).withNonMergedAttributes()
      .synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
  }
  
  private static <A extends Annotation> boolean isSingleLevelPresent(MergedAnnotation<A> mergedAnnotation) {
    int distance = mergedAnnotation.getDistance();
    return (distance == 0 || distance == 1);
  }














  
  @Nullable
  public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
    Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
    return getAnnotation(resolvedMethod, annotationType);
  }












  
  @Deprecated
  @Nullable
  public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
    try {
      return synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
    }
    catch (Throwable ex) {
      handleIntrospectionFailure(annotatedElement, ex);
      return null;
    } 
  }













  
  @Deprecated
  @Nullable
  public static Annotation[] getAnnotations(Method method) {
    try {
      return synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
    }
    catch (Throwable ex) {
      handleIntrospectionFailure(method, ex);
      return null;
    } 
  }






























  
  @Deprecated
  public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
    return getRepeatableAnnotations(annotatedElement, annotationType, null);
  }



































  
  @Deprecated
  public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
    RepeatableContainers repeatableContainers = (containerAnnotationType != null) ? RepeatableContainers.of(annotationType, containerAnnotationType) : RepeatableContainers.standardRepeatables();
    
    return (Set<A>)MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.SUPERCLASS, repeatableContainers)
      .<A>stream(annotationType)
      .filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
      .map(MergedAnnotation::withNonMergedAttributes)
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }































  
  @Deprecated
  public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
    return getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
  }



































  
  @Deprecated
  public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
    RepeatableContainers repeatableContainers = (containerAnnotationType != null) ? RepeatableContainers.of(annotationType, containerAnnotationType) : RepeatableContainers.standardRepeatables();
    
    return (Set<A>)MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.DIRECT, repeatableContainers)
      .<A>stream(annotationType)
      .map(MergedAnnotation::withNonMergedAttributes)
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }


















  
  @Nullable
  public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, @Nullable Class<A> annotationType) {
    if (annotationType == null) {
      return null;
    }

    
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
      return annotatedElement.getDeclaredAnnotation(annotationType);
    }

    
    return (A)MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none())
      .<Annotation>get(annotationType).withNonMergedAttributes()
      .synthesize(MergedAnnotation::isPresent).orElse(null);
  }















  
  @Nullable
  public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> annotationType) {
    if (annotationType == null) {
      return null;
    }

    
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(method)) {
      return method.getDeclaredAnnotation(annotationType);
    }

    
    return (A)MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
      .<Annotation>get(annotationType).withNonMergedAttributes()
      .synthesize(MergedAnnotation::isPresent).orElse(null);
  }






















  
  @Nullable
  public static <A extends Annotation> A findAnnotation(Class<?> clazz, @Nullable Class<A> annotationType) {
    if (annotationType == null) {
      return null;
    }

    
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(clazz)) {
      A annotation = clazz.getDeclaredAnnotation(annotationType);
      if (annotation != null) {
        return annotation;
      }

      
      Class<?> superclass = clazz.getSuperclass();
      if (superclass == null || superclass == Object.class) {
        return null;
      }
      return findAnnotation(superclass, annotationType);
    } 

    
    return (A)MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
      .<Annotation>get(annotationType).withNonMergedAttributes()
      .synthesize(MergedAnnotation::isPresent).orElse(null);
  }























  
  @Deprecated
  @Nullable
  public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    
    return (Class)MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.SUPERCLASS)
      .<Annotation>get(annotationType, MergedAnnotation::isDirectlyPresent)
      .getSource();
  }

























  
  @Deprecated
  @Nullable
  public static Class<?> findAnnotationDeclaringClassForTypes(List<Class<? extends Annotation>> annotationTypes, @Nullable Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    
    return MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.SUPERCLASS)
      .stream()
      .filter(MergedAnnotationPredicates.<Annotation>typeIn(annotationTypes).and(MergedAnnotation::isDirectlyPresent))
      .map(MergedAnnotation::getSource)
      .findFirst().orElse(null);
  }















  
  public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
    return MergedAnnotations.from(clazz).<Annotation>get(annotationType).isDirectlyPresent();
  }




















  
  @Deprecated
  public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
    return 


      
      (((MergedAnnotation)MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS).<Annotation>stream(annotationType).filter(MergedAnnotation::isDirectlyPresent).findFirst().orElseGet(MergedAnnotation::missing)).getAggregateIndex() > 0);
  }











  
  @Deprecated
  public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType, @Nullable Class<? extends Annotation> metaAnnotationType) {
    if (metaAnnotationType == null) {
      return false;
    }
    
    if (AnnotationFilter.PLAIN.matches(metaAnnotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotationType)) {
      return annotationType.isAnnotationPresent(metaAnnotationType);
    }
    
    return MergedAnnotations.from(annotationType, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, 
        RepeatableContainers.none()).isPresent(metaAnnotationType);
  }






  
  public static boolean isInJavaLangAnnotationPackage(@Nullable Annotation annotation) {
    return (annotation != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotation));
  }







  
  public static boolean isInJavaLangAnnotationPackage(@Nullable String annotationType) {
    return (annotationType != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotationType));
  }












  
  public static void validateAnnotation(Annotation annotation) {
    AttributeMethods.forAnnotationType(annotation.annotationType()).validate(annotation);
  }















  
  public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
    return getAnnotationAttributes((AnnotatedElement)null, annotation);
  }
















  
  public static Map<String, Object> getAnnotationAttributes(Annotation annotation, boolean classValuesAsString) {
    return getAnnotationAttributes(annotation, classValuesAsString, false);
  }


















  
  public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    return getAnnotationAttributes(null, annotation, classValuesAsString, nestedAnnotationsAsMap);
  }















  
  public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation) {
    return getAnnotationAttributes(annotatedElement, annotation, false, false);
  }





















  
  public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap);
    return (AnnotationAttributes)MergedAnnotation.<Annotation>from(annotatedElement, annotation)
      .withNonMergedAttributes()
      .asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType(), true), adaptations);
  }







  
  public static void registerDefaultValues(AnnotationAttributes attributes) {
    Class<? extends Annotation> annotationType = attributes.annotationType();
    if (annotationType != null && Modifier.isPublic(annotationType.getModifiers()) && 
      !AnnotationFilter.PLAIN.matches(annotationType)) {
      Map<String, DefaultValueHolder> defaultValues = getDefaultValues(annotationType);
      defaultValues.forEach(attributes::putIfAbsent);
    } 
  }


  
  private static Map<String, DefaultValueHolder> getDefaultValues(Class<? extends Annotation> annotationType) {
    return defaultValuesCache.computeIfAbsent(annotationType, AnnotationUtils::computeDefaultValues);
  }



  
  private static Map<String, DefaultValueHolder> computeDefaultValues(Class<? extends Annotation> annotationType) {
    AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
    if (!methods.hasDefaultValueMethod()) {
      return Collections.emptyMap();
    }
    Map<String, DefaultValueHolder> result = CollectionUtils.newLinkedHashMap(methods.size());
    if (!methods.hasNestedAnnotation()) {
      
      for (int i = 0; i < methods.size(); i++) {
        Method method = methods.get(i);
        Object defaultValue = method.getDefaultValue();
        if (defaultValue != null) {
          result.put(method.getName(), new DefaultValueHolder(defaultValue));
        }
      }
    
    }
    else {
      
      AnnotationAttributes attributes = (AnnotationAttributes)MergedAnnotation.<Annotation>of(annotationType).asMap(annotation -> new AnnotationAttributes(annotation.getType(), true), new MergedAnnotation.Adapt[] { MergedAnnotation.Adapt.ANNOTATION_TO_MAP });
      
      for (Map.Entry<String, Object> element : attributes.entrySet()) {
        result.put(element.getKey(), new DefaultValueHolder(element.getValue()));
      }
    } 
    return result;
  }


















  
  public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, @Nullable AnnotationAttributes attributes, boolean classValuesAsString) {
    if (attributes == null) {
      return;
    }
    if (!attributes.validated) {
      Class<? extends Annotation> annotationType = attributes.annotationType();
      if (annotationType == null) {
        return;
      }
      AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(annotationType).get(0);
      for (int i = 0; i < mapping.getMirrorSets().size(); i++) {
        AnnotationTypeMapping.MirrorSets.MirrorSet mirrorSet = mapping.getMirrorSets().get(i);
        int resolved = mirrorSet.resolve(attributes.displayName, attributes, AnnotationUtils::getAttributeValueForMirrorResolution);
        
        if (resolved != -1) {
          Method attribute = mapping.getAttributes().get(resolved);
          Object value = attributes.get(attribute.getName());
          for (int j = 0; j < mirrorSet.size(); j++) {
            Method mirror = mirrorSet.get(j);
            if (mirror != attribute) {
              attributes.put(mirror.getName(), 
                  adaptValue(annotatedElement, value, classValuesAsString));
            }
          } 
        } 
      } 
    } 
    for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
      String attributeName = attributeEntry.getKey();
      Object value = attributeEntry.getValue();
      if (value instanceof DefaultValueHolder) {
        value = ((DefaultValueHolder)value).defaultValue;
        attributes.put(attributeName, 
            adaptValue(annotatedElement, value, classValuesAsString));
      } 
    } 
  }
  
  private static Object getAttributeValueForMirrorResolution(Method attribute, Object attributes) {
    Object result = ((AnnotationAttributes)attributes).get(attribute.getName());
    return (result instanceof DefaultValueHolder) ? ((DefaultValueHolder)result).defaultValue : result;
  }


  
  @Nullable
  private static Object adaptValue(@Nullable Object annotatedElement, @Nullable Object value, boolean classValuesAsString) {
    if (classValuesAsString) {
      if (value instanceof Class) {
        return ((Class)value).getName();
      }
      if (value instanceof Class[]) {
        Class<?>[] classes = (Class[])value;
        String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
          names[i] = classes[i].getName();
        }
        return names;
      } 
    } 
    if (value instanceof Annotation) {
      Annotation annotation = (Annotation)value;
      return MergedAnnotation.<Annotation>from(annotatedElement, annotation).synthesize();
    } 
    if (value instanceof Annotation[]) {
      Annotation[] annotations = (Annotation[])value;
      Annotation[] synthesized = (Annotation[])Array.newInstance(annotations
          .getClass().getComponentType(), annotations.length);
      for (int i = 0; i < annotations.length; i++) {
        synthesized[i] = MergedAnnotation.<Annotation>from(annotatedElement, annotations[i]).synthesize();
      }
      return synthesized;
    } 
    return value;
  }









  
  @Nullable
  public static Object getValue(Annotation annotation) {
    return getValue(annotation, "value");
  }









  
  @Nullable
  public static Object getValue(@Nullable Annotation annotation, @Nullable String attributeName) {
    if (annotation == null || !StringUtils.hasText(attributeName)) {
      return null;
    }
    try {
      Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
      ReflectionUtils.makeAccessible(method);
      return method.invoke(annotation, new Object[0]);
    }
    catch (NoSuchMethodException ex) {
      return null;
    }
    catch (InvocationTargetException ex) {
      rethrowAnnotationConfigurationException(ex.getTargetException());
      throw new IllegalStateException("Could not obtain value for annotation attribute '" + attributeName + "' in " + annotation, ex);
    
    }
    catch (Throwable ex) {
      handleIntrospectionFailure(annotation.getClass(), ex);
      return null;
    } 
  }







  
  static void rethrowAnnotationConfigurationException(Throwable ex) {
    if (ex instanceof AnnotationConfigurationException) {
      throw (AnnotationConfigurationException)ex;
    }
  }















  
  static void handleIntrospectionFailure(@Nullable AnnotatedElement element, Throwable ex) {
    rethrowAnnotationConfigurationException(ex);
    IntrospectionFailureLogger logger = IntrospectionFailureLogger.INFO;
    boolean meta = false;
    if (element instanceof Class && Annotation.class.isAssignableFrom((Class)element)) {
      
      logger = IntrospectionFailureLogger.DEBUG;
      meta = true;
    } 
    if (logger.isEnabled()) {
      String message = meta ? "Failed to meta-introspect annotation " : "Failed to introspect annotations on ";

      
      logger.log(message + element + ": " + ex);
    } 
  }







  
  @Nullable
  public static Object getDefaultValue(Annotation annotation) {
    return getDefaultValue(annotation, "value");
  }







  
  @Nullable
  public static Object getDefaultValue(@Nullable Annotation annotation, @Nullable String attributeName) {
    return (annotation != null) ? getDefaultValue(annotation.annotationType(), attributeName) : null;
  }







  
  @Nullable
  public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
    return getDefaultValue(annotationType, "value");
  }










  
  @Nullable
  public static Object getDefaultValue(@Nullable Class<? extends Annotation> annotationType, @Nullable String attributeName) {
    if (annotationType == null || !StringUtils.hasText(attributeName)) {
      return null;
    }
    return MergedAnnotation.<Annotation>of(annotationType).getDefaultValue(attributeName).orElse(null);
  }



















  
  public static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable AnnotatedElement annotatedElement) {
    if (annotation instanceof SynthesizedAnnotation || AnnotationFilter.PLAIN.matches((Annotation)annotation)) {
      return annotation;
    }
    return MergedAnnotation.<A>from(annotatedElement, annotation).synthesize();
  }















  
  public static <A extends Annotation> A synthesizeAnnotation(Class<A> annotationType) {
    return synthesizeAnnotation(Collections.emptyMap(), annotationType, null);
  }































  
  public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> attributes, Class<A> annotationType, @Nullable AnnotatedElement annotatedElement) {
    try {
      return MergedAnnotation.<A>of(annotatedElement, annotationType, attributes).synthesize();
    }
    catch (NoSuchElementException|IllegalStateException ex) {
      throw new IllegalArgumentException(ex);
    } 
  }

















  
  static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, AnnotatedElement annotatedElement) {
    if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
      return annotations;
    }
    Annotation[] synthesized = (Annotation[])Array.newInstance(annotations
        .getClass().getComponentType(), annotations.length);
    for (int i = 0; i < annotations.length; i++) {
      synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
    }
    return synthesized;
  }




  
  public static void clearCache() {
    AnnotationTypeMappings.clearCache();
    AnnotationsScanner.clearCache();
  }


  
  private static class DefaultValueHolder
  {
    final Object defaultValue;


    
    public DefaultValueHolder(Object defaultValue) {
      this.defaultValue = defaultValue;
    }

    
    public String toString() {
      return "*" + this.defaultValue;
    }
  }
}
