package org.springframework.core.type;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;




































public interface AnnotationMetadata
  extends ClassMetadata, AnnotatedTypeMetadata
{
  default Set<String> getAnnotationTypes() {
    return (Set<String>)getAnnotations().stream()
      .filter(MergedAnnotation::isDirectlyPresent)
      .map(annotation -> annotation.getType().getName())
      .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
  }







  
  default Set<String> getMetaAnnotationTypes(String annotationName) {
    MergedAnnotation<?> annotation = getAnnotations().get(annotationName, MergedAnnotation::isDirectlyPresent);
    if (!annotation.isPresent()) {
      return Collections.emptySet();
    }
    return (Set<String>)MergedAnnotations.from(annotation.getType(), MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS).stream()
      .map(mergedAnnotation -> mergedAnnotation.getType().getName())
      .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
  }







  
  default boolean hasAnnotation(String annotationName) {
    return getAnnotations().isDirectlyPresent(annotationName);
  }







  
  default boolean hasMetaAnnotation(String metaAnnotationName) {
    return getAnnotations().get(metaAnnotationName, MergedAnnotation::isMetaPresent)
      .isPresent();
  }






  
  default boolean hasAnnotatedMethods(String annotationName) {
    return !getAnnotatedMethods(annotationName).isEmpty();
  }





















  
  static AnnotationMetadata introspect(Class<?> type) {
    return StandardAnnotationMetadata.from(type);
  }
  
  Set<MethodMetadata> getAnnotatedMethods(String paramString);
}
