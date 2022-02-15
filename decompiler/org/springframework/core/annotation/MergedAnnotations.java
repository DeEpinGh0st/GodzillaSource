package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;




































































































































































































































































public interface MergedAnnotations
  extends Iterable<MergedAnnotation<Annotation>>
{
  <A extends Annotation> boolean isPresent(Class<A> paramClass);
  
  boolean isPresent(String paramString);
  
  <A extends Annotation> boolean isDirectlyPresent(Class<A> paramClass);
  
  boolean isDirectlyPresent(String paramString);
  
  <A extends Annotation> MergedAnnotation<A> get(Class<A> paramClass);
  
  <A extends Annotation> MergedAnnotation<A> get(Class<A> paramClass, @Nullable Predicate<? super MergedAnnotation<A>> paramPredicate);
  
  <A extends Annotation> MergedAnnotation<A> get(Class<A> paramClass, @Nullable Predicate<? super MergedAnnotation<A>> paramPredicate, @Nullable MergedAnnotationSelector<A> paramMergedAnnotationSelector);
  
  <A extends Annotation> MergedAnnotation<A> get(String paramString);
  
  <A extends Annotation> MergedAnnotation<A> get(String paramString, @Nullable Predicate<? super MergedAnnotation<A>> paramPredicate);
  
  <A extends Annotation> MergedAnnotation<A> get(String paramString, @Nullable Predicate<? super MergedAnnotation<A>> paramPredicate, @Nullable MergedAnnotationSelector<A> paramMergedAnnotationSelector);
  
  <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> paramClass);
  
  <A extends Annotation> Stream<MergedAnnotation<A>> stream(String paramString);
  
  Stream<MergedAnnotation<Annotation>> stream();
  
  static MergedAnnotations from(AnnotatedElement element) {
    return from(element, SearchStrategy.DIRECT);
  }









  
  static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
    return from(element, searchStrategy, RepeatableContainers.standardRepeatables());
  }













  
  static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers) {
    return from(element, searchStrategy, repeatableContainers, AnnotationFilter.PLAIN);
  }















  
  static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
    Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
    Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
    return TypeMappedAnnotations.from(element, searchStrategy, repeatableContainers, annotationFilter);
  }







  
  static MergedAnnotations from(Annotation... annotations) {
    return from(annotations, annotations);
  }











  
  static MergedAnnotations from(Object source, Annotation... annotations) {
    return from(source, annotations, RepeatableContainers.standardRepeatables());
  }











  
  static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers) {
    return from(source, annotations, repeatableContainers, AnnotationFilter.PLAIN);
  }















  
  static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
    Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
    Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
    return TypeMappedAnnotations.from(source, annotations, repeatableContainers, annotationFilter);
  }















  
  static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
    return MergedAnnotationsCollection.of(annotations);
  }













  
  public enum SearchStrategy
  {
    DIRECT,








    
    INHERITED_ANNOTATIONS,






    
    SUPERCLASS,





    
    TYPE_HIERARCHY,









    
    TYPE_HIERARCHY_AND_ENCLOSING_CLASSES;
  }
}
