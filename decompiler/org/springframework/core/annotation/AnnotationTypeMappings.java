package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;



































final class AnnotationTypeMappings
{
  private static final IntrospectionFailureLogger failureLogger = IntrospectionFailureLogger.DEBUG;
  
  private static final Map<AnnotationFilter, Cache> standardRepeatablesCache = (Map<AnnotationFilter, Cache>)new ConcurrentReferenceHashMap();
  
  private static final Map<AnnotationFilter, Cache> noRepeatablesCache = (Map<AnnotationFilter, Cache>)new ConcurrentReferenceHashMap();

  
  private final RepeatableContainers repeatableContainers;

  
  private final AnnotationFilter filter;

  
  private final List<AnnotationTypeMapping> mappings;

  
  private AnnotationTypeMappings(RepeatableContainers repeatableContainers, AnnotationFilter filter, Class<? extends Annotation> annotationType) {
    this.repeatableContainers = repeatableContainers;
    this.filter = filter;
    this.mappings = new ArrayList<>();
    addAllMappings(annotationType);
    this.mappings.forEach(AnnotationTypeMapping::afterAllMappingsSet);
  }

  
  private void addAllMappings(Class<? extends Annotation> annotationType) {
    Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
    addIfPossible(queue, null, annotationType, null);
    while (!queue.isEmpty()) {
      AnnotationTypeMapping mapping = queue.removeFirst();
      this.mappings.add(mapping);
      addMetaAnnotationsToQueue(queue, mapping);
    } 
  }
  
  private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source) {
    Annotation[] metaAnnotations = AnnotationsScanner.getDeclaredAnnotations(source.getAnnotationType(), false);
    for (Annotation metaAnnotation : metaAnnotations) {
      if (isMappable(source, metaAnnotation)) {

        
        Annotation[] repeatedAnnotations = this.repeatableContainers.findRepeatedAnnotations(metaAnnotation);
        if (repeatedAnnotations != null) {
          for (Annotation repeatedAnnotation : repeatedAnnotations) {
            if (isMappable(source, repeatedAnnotation))
            {
              
              addIfPossible(queue, source, repeatedAnnotation);
            }
          } 
        } else {
          addIfPossible(queue, source, metaAnnotation);
        } 
      } 
    } 
  }
  private void addIfPossible(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source, Annotation ann) {
    addIfPossible(queue, source, ann.annotationType(), ann);
  }


  
  private void addIfPossible(Deque<AnnotationTypeMapping> queue, @Nullable AnnotationTypeMapping source, Class<? extends Annotation> annotationType, @Nullable Annotation ann) {
    try {
      queue.addLast(new AnnotationTypeMapping(source, annotationType, ann));
    }
    catch (Exception ex) {
      AnnotationUtils.rethrowAnnotationConfigurationException(ex);
      if (failureLogger.isEnabled()) {
        failureLogger.log("Failed to introspect meta-annotation " + annotationType.getName(), (source != null) ? source
            .getAnnotationType() : null, ex);
      }
    } 
  }
  
  private boolean isMappable(AnnotationTypeMapping source, @Nullable Annotation metaAnnotation) {
    return (metaAnnotation != null && !this.filter.matches(metaAnnotation) && 
      !AnnotationFilter.PLAIN.matches(source.getAnnotationType()) && 
      !isAlreadyMapped(source, metaAnnotation));
  }
  
  private boolean isAlreadyMapped(AnnotationTypeMapping source, Annotation metaAnnotation) {
    Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
    AnnotationTypeMapping mapping = source;
    while (mapping != null) {
      if (mapping.getAnnotationType() == annotationType) {
        return true;
      }
      mapping = mapping.getSource();
    } 
    return false;
  }




  
  int size() {
    return this.mappings.size();
  }









  
  AnnotationTypeMapping get(int index) {
    return this.mappings.get(index);
  }






  
  static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType) {
    return forAnnotationType(annotationType, AnnotationFilter.PLAIN);
  }









  
  static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, AnnotationFilter annotationFilter) {
    return forAnnotationType(annotationType, RepeatableContainers.standardRepeatables(), annotationFilter);
  }











  
  static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
    if (repeatableContainers == RepeatableContainers.standardRepeatables()) {
      return ((Cache)standardRepeatablesCache.computeIfAbsent(annotationFilter, key -> new Cache(repeatableContainers, key)))
        .get(annotationType);
    }
    if (repeatableContainers == RepeatableContainers.none()) {
      return ((Cache)noRepeatablesCache.computeIfAbsent(annotationFilter, key -> new Cache(repeatableContainers, key)))
        .get(annotationType);
    }
    return new AnnotationTypeMappings(repeatableContainers, annotationFilter, annotationType);
  }
  
  static void clearCache() {
    standardRepeatablesCache.clear();
    noRepeatablesCache.clear();
  }



  
  private static class Cache
  {
    private final RepeatableContainers repeatableContainers;


    
    private final AnnotationFilter filter;

    
    private final Map<Class<? extends Annotation>, AnnotationTypeMappings> mappings;


    
    Cache(RepeatableContainers repeatableContainers, AnnotationFilter filter) {
      this.repeatableContainers = repeatableContainers;
      this.filter = filter;
      this.mappings = (Map<Class<? extends Annotation>, AnnotationTypeMappings>)new ConcurrentReferenceHashMap();
    }





    
    AnnotationTypeMappings get(Class<? extends Annotation> annotationType) {
      return this.mappings.computeIfAbsent(annotationType, this::createMappings);
    }
    
    AnnotationTypeMappings createMappings(Class<? extends Annotation> annotationType) {
      return new AnnotationTypeMappings(this.repeatableContainers, this.filter, annotationType);
    }
  }
}
