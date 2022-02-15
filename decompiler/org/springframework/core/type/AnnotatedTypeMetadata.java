package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationCollectors;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotationSelectors;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

















































public interface AnnotatedTypeMetadata
{
  MergedAnnotations getAnnotations();
  
  default boolean isAnnotated(String annotationName) {
    return getAnnotations().isPresent(annotationName);
  }










  
  @Nullable
  default Map<String, Object> getAnnotationAttributes(String annotationName) {
    return getAnnotationAttributes(annotationName, false);
  }















  
  @Nullable
  default Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName, null, 
        MergedAnnotationSelectors.firstDirectlyDeclared());
    if (!annotation.isPresent()) {
      return null;
    }
    return (Map<String, Object>)annotation.asAnnotationAttributes(MergedAnnotation.Adapt.values(classValuesAsString, true));
  }











  
  @Nullable
  default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
    return getAllAnnotationAttributes(annotationName, false);
  }














  
  @Nullable
  default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, true);
    return (MultiValueMap<String, Object>)getAnnotations().stream(annotationName)
      .filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
      .map(MergedAnnotation::withNonMergedAttributes)
      .collect(MergedAnnotationCollectors.toMultiValueMap(map -> map.isEmpty() ? null : map, adaptations));
  }
}
