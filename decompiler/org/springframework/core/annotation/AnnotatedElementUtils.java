package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;






















































































public abstract class AnnotatedElementUtils
{
  public static AnnotatedElement forAnnotations(Annotation... annotations) {
    return new AnnotatedElementForAnnotations(annotations);
  }
















  
  public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
    return getMetaAnnotationTypes(element, element.getAnnotation((Class)annotationType));
  }














  
  public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
    for (Annotation annotation : element.getAnnotations()) {
      if (annotation.annotationType().getName().equals(annotationName)) {
        return getMetaAnnotationTypes(element, annotation);
      }
    } 
    return Collections.emptySet();
  }
  
  private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, @Nullable Annotation annotation) {
    if (annotation == null) {
      return Collections.emptySet();
    }
    return (Set<String>)getAnnotations(annotation.annotationType()).stream()
      .map(mergedAnnotation -> mergedAnnotation.getType().getName())
      .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
  }












  
  public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
    return getAnnotations(element).<Annotation>stream(annotationType).anyMatch(MergedAnnotation::isMetaPresent);
  }












  
  public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
    return getAnnotations(element).<Annotation>stream(annotationName).anyMatch(MergedAnnotation::isMetaPresent);
  }















  
  public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
      return element.isAnnotationPresent(annotationType);
    }
    
    return getAnnotations(element).isPresent(annotationType);
  }












  
  public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
    return getAnnotations(element).isPresent(annotationName);
  }




















  
  @Nullable
  public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
    MergedAnnotation<?> mergedAnnotation = getAnnotations(element).get(annotationType, (Predicate<? super MergedAnnotation<?>>)null, MergedAnnotationSelectors.firstDirectlyDeclared());
    return getAnnotationAttributes(mergedAnnotation, false, false);
  }




















  
  @Nullable
  public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
    return getMergedAnnotationAttributes(element, annotationName, false, false);
  }





























  
  @Nullable
  public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    MergedAnnotation<?> mergedAnnotation = getAnnotations(element).get(annotationName, (Predicate<? super MergedAnnotation<?>>)null, MergedAnnotationSelectors.firstDirectlyDeclared());
    return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
  }















  
  @Nullable
  public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
      return element.getDeclaredAnnotation(annotationType);
    }
    
    return (A)getAnnotations(element)
      .<Annotation>get(annotationType, (Predicate<? super MergedAnnotation<Annotation>>)null, MergedAnnotationSelectors.firstDirectlyDeclared())
      .synthesize(MergedAnnotation::isPresent).orElse(null);
  }






















  
  public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
    return (Set<A>)getAnnotations(element).<A>stream(annotationType)
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }




















  
  public static Set<Annotation> getAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
    return (Set<Annotation>)getAnnotations(element).stream()
      .filter((Predicate)MergedAnnotationPredicates.typeIn(annotationTypes))
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }


























  
  public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
    return getMergedRepeatableAnnotations(element, annotationType, null);
  }





























  
  public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
    return (Set<A>)getRepeatableAnnotations(element, containerType, annotationType)
      .<A>stream(annotationType)
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }
















  
  @Nullable
  public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName) {
    return getAllAnnotationAttributes(element, annotationName, false, false);
  }




















  
  @Nullable
  public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap);
    return (MultiValueMap<String, Object>)getAnnotations(element).<Annotation>stream(annotationName)
      .filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
      .map(MergedAnnotation::withNonMergedAttributes)
      .collect(MergedAnnotationCollectors.toMultiValueMap(AnnotatedElementUtils::nullIfEmpty, adaptations));
  }















  
  public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
      return element.isAnnotationPresent(annotationType);
    }
    
    return findAnnotations(element).isPresent(annotationType);
  }






























  
  @Nullable
  public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    MergedAnnotation<?> mergedAnnotation = findAnnotations(element).get(annotationType, (Predicate<? super MergedAnnotation<?>>)null, MergedAnnotationSelectors.firstDirectlyDeclared());
    return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
  }






























  
  @Nullable
  public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    MergedAnnotation<?> mergedAnnotation = findAnnotations(element).get(annotationName, (Predicate<? super MergedAnnotation<?>>)null, MergedAnnotationSelectors.firstDirectlyDeclared());
    return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
  }



















  
  @Nullable
  public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
    if (AnnotationFilter.PLAIN.matches(annotationType) || 
      AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
      return element.getDeclaredAnnotation(annotationType);
    }
    
    return (A)findAnnotations(element)
      .<Annotation>get(annotationType, (Predicate<? super MergedAnnotation<Annotation>>)null, MergedAnnotationSelectors.firstDirectlyDeclared())
      .synthesize(MergedAnnotation::isPresent).orElse(null);
  }



















  
  public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
    return (Set<A>)findAnnotations(element).<A>stream(annotationType)
      .sorted(highAggregateIndexesFirst())
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }


















  
  public static Set<Annotation> findAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
    return (Set<Annotation>)findAnnotations(element).stream()
      .filter((Predicate)MergedAnnotationPredicates.typeIn(annotationTypes))
      .sorted(highAggregateIndexesFirst())
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }


























  
  public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
    return findMergedRepeatableAnnotations(element, annotationType, null);
  }




























  
  public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
    return (Set<A>)findRepeatableAnnotations(element, containerType, annotationType)
      .<A>stream(annotationType)
      .sorted(highAggregateIndexesFirst())
      .collect(MergedAnnotationCollectors.toAnnotationSet());
  }
  
  private static MergedAnnotations getAnnotations(AnnotatedElement element) {
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none());
  }


  
  private static MergedAnnotations getRepeatableAnnotations(AnnotatedElement element, @Nullable Class<? extends Annotation> containerType, Class<? extends Annotation> annotationType) {
    RepeatableContainers repeatableContainers = RepeatableContainers.of(annotationType, containerType);
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, repeatableContainers);
  }
  
  private static MergedAnnotations findAnnotations(AnnotatedElement element) {
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
  }


  
  private static MergedAnnotations findRepeatableAnnotations(AnnotatedElement element, @Nullable Class<? extends Annotation> containerType, Class<? extends Annotation> annotationType) {
    RepeatableContainers repeatableContainers = RepeatableContainers.of(annotationType, containerType);
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, repeatableContainers);
  }
  
  @Nullable
  private static MultiValueMap<String, Object> nullIfEmpty(MultiValueMap<String, Object> map) {
    return map.isEmpty() ? null : map;
  }
  
  private static <A extends Annotation> Comparator<MergedAnnotation<A>> highAggregateIndexesFirst() {
    return Comparator.<MergedAnnotation<A>>comparingInt(MergedAnnotation::getAggregateIndex)
      .reversed();
  }


  
  @Nullable
  private static AnnotationAttributes getAnnotationAttributes(MergedAnnotation<?> annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
    if (!annotation.isPresent()) {
      return null;
    }
    return annotation.asAnnotationAttributes(
        MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap));
  }


  
  private static class AnnotatedElementForAnnotations
    implements AnnotatedElement
  {
    private final Annotation[] annotations;

    
    AnnotatedElementForAnnotations(Annotation... annotations) {
      this.annotations = annotations;
    }


    
    @Nullable
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      for (Annotation annotation : this.annotations) {
        if (annotation.annotationType() == annotationClass) {
          return (T)annotation;
        }
      } 
      return null;
    }

    
    public Annotation[] getAnnotations() {
      return (Annotation[])this.annotations.clone();
    }

    
    public Annotation[] getDeclaredAnnotations() {
      return (Annotation[])this.annotations.clone();
    }
  }
}
