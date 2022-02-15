package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;



























final class MergedAnnotationsCollection
  implements MergedAnnotations
{
  private final MergedAnnotation<?>[] annotations;
  private final AnnotationTypeMappings[] mappings;
  
  private MergedAnnotationsCollection(Collection<MergedAnnotation<?>> annotations) {
    Assert.notNull(annotations, "Annotations must not be null");
    this.annotations = (MergedAnnotation<?>[])annotations.<MergedAnnotation>toArray(new MergedAnnotation[0]);
    this.mappings = new AnnotationTypeMappings[this.annotations.length];
    for (int i = 0; i < this.annotations.length; i++) {
      MergedAnnotation<?> annotation = this.annotations[i];
      Assert.notNull(annotation, "Annotation must not be null");
      Assert.isTrue(annotation.isDirectlyPresent(), "Annotation must be directly present");
      Assert.isTrue((annotation.getAggregateIndex() == 0), "Annotation must have aggregate index of zero");
      this.mappings[i] = AnnotationTypeMappings.forAnnotationType((Class)annotation.getType());
    } 
  }


  
  public Iterator<MergedAnnotation<Annotation>> iterator() {
    return Spliterators.iterator(spliterator());
  }

  
  public Spliterator<MergedAnnotation<Annotation>> spliterator() {
    return spliterator(null);
  }
  
  private <A extends Annotation> Spliterator<MergedAnnotation<A>> spliterator(@Nullable Object annotationType) {
    return new AnnotationsSpliterator<>(annotationType);
  }

  
  public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
    return isPresent(annotationType, false);
  }

  
  public boolean isPresent(String annotationType) {
    return isPresent(annotationType, false);
  }

  
  public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
    return isPresent(annotationType, true);
  }

  
  public boolean isDirectlyPresent(String annotationType) {
    return isPresent(annotationType, true);
  }
  
  private boolean isPresent(Object requiredType, boolean directOnly) {
    for (MergedAnnotation<?> annotation : this.annotations) {
      Class<? extends Annotation> type = (Class)annotation.getType();
      if (type == requiredType || type.getName().equals(requiredType)) {
        return true;
      }
    } 
    if (!directOnly) {
      for (AnnotationTypeMappings mappings : this.mappings) {
        for (int i = 1; i < mappings.size(); i++) {
          AnnotationTypeMapping mapping = mappings.get(i);
          if (isMappingForType(mapping, requiredType)) {
            return true;
          }
        } 
      } 
    }
    return false;
  }

  
  public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
    return get(annotationType, (Predicate<? super MergedAnnotation<A>>)null, (MergedAnnotationSelector<A>)null);
  }



  
  public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {
    return get(annotationType, predicate, (MergedAnnotationSelector<A>)null);
  }




  
  public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
    MergedAnnotation<A> result = find(annotationType, predicate, selector);
    return (result != null) ? result : MergedAnnotation.<A>missing();
  }

  
  public <A extends Annotation> MergedAnnotation<A> get(String annotationType) {
    return get(annotationType, (Predicate<? super MergedAnnotation<A>>)null, (MergedAnnotationSelector<A>)null);
  }



  
  public <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {
    return get(annotationType, predicate, (MergedAnnotationSelector<A>)null);
  }




  
  public <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
    MergedAnnotation<A> result = find(annotationType, predicate, selector);
    return (result != null) ? result : MergedAnnotation.<A>missing();
  }




  
  @Nullable
  private <A extends Annotation> MergedAnnotation<A> find(Object requiredType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
    if (selector == null) {
      selector = MergedAnnotationSelectors.nearest();
    }
    
    MergedAnnotation<A> result = null;
    for (int i = 0; i < this.annotations.length; i++) {
      MergedAnnotation<?> root = this.annotations[i];
      AnnotationTypeMappings mappings = this.mappings[i];
      for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
        AnnotationTypeMapping mapping = mappings.get(mappingIndex);
        if (isMappingForType(mapping, requiredType)) {


          
          MergedAnnotation<A> candidate = (mappingIndex == 0) ? (MergedAnnotation)root : TypeMappedAnnotation.<A>createIfPossible(mapping, root, IntrospectionFailureLogger.INFO);
          if (candidate != null && (predicate == null || predicate.test(candidate))) {
            if (selector.isBestCandidate(candidate)) {
              return candidate;
            }
            result = (result != null) ? selector.select(result, candidate) : candidate;
          } 
        } 
      } 
    }  return result;
  }

  
  public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
    return StreamSupport.stream(spliterator(annotationType), false);
  }

  
  public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
    return StreamSupport.stream(spliterator(annotationType), false);
  }

  
  public Stream<MergedAnnotation<Annotation>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }
  
  private static boolean isMappingForType(AnnotationTypeMapping mapping, @Nullable Object requiredType) {
    if (requiredType == null) {
      return true;
    }
    Class<? extends Annotation> actualType = mapping.getAnnotationType();
    return (actualType == requiredType || actualType.getName().equals(requiredType));
  }
  
  static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
    Assert.notNull(annotations, "Annotations must not be null");
    if (annotations.isEmpty()) {
      return TypeMappedAnnotations.NONE;
    }
    return new MergedAnnotationsCollection(annotations);
  }

  
  private class AnnotationsSpliterator<A extends Annotation>
    implements Spliterator<MergedAnnotation<A>>
  {
    @Nullable
    private Object requiredType;
    private final int[] mappingCursors;
    
    public AnnotationsSpliterator(Object requiredType) {
      this.mappingCursors = new int[MergedAnnotationsCollection.this.annotations.length];
      this.requiredType = requiredType;
    }

    
    public boolean tryAdvance(Consumer<? super MergedAnnotation<A>> action) {
      int lowestDistance = Integer.MAX_VALUE;
      int annotationResult = -1;
      for (int annotationIndex = 0; annotationIndex < MergedAnnotationsCollection.this.annotations.length; annotationIndex++) {
        AnnotationTypeMapping mapping = getNextSuitableMapping(annotationIndex);
        if (mapping != null && mapping.getDistance() < lowestDistance) {
          annotationResult = annotationIndex;
          lowestDistance = mapping.getDistance();
        } 
        if (lowestDistance == 0) {
          break;
        }
      } 
      if (annotationResult != -1) {
        MergedAnnotation<A> mergedAnnotation = createMergedAnnotationIfPossible(annotationResult, this.mappingCursors[annotationResult]);
        
        this.mappingCursors[annotationResult] = this.mappingCursors[annotationResult] + 1;
        if (mergedAnnotation == null) {
          return tryAdvance(action);
        }
        action.accept(mergedAnnotation);
        return true;
      } 
      return false;
    }

    
    @Nullable
    private AnnotationTypeMapping getNextSuitableMapping(int annotationIndex) {
      while (true) {
        AnnotationTypeMapping mapping = getMapping(annotationIndex, this.mappingCursors[annotationIndex]);
        if (mapping != null && MergedAnnotationsCollection.isMappingForType(mapping, this.requiredType)) {
          return mapping;
        }
        this.mappingCursors[annotationIndex] = this.mappingCursors[annotationIndex] + 1;
        
        if (mapping == null)
          return null; 
      } 
    }
    @Nullable
    private AnnotationTypeMapping getMapping(int annotationIndex, int mappingIndex) {
      AnnotationTypeMappings mappings = MergedAnnotationsCollection.this.mappings[annotationIndex];
      return (mappingIndex < mappings.size()) ? mappings.get(mappingIndex) : null;
    }

    
    @Nullable
    private MergedAnnotation<A> createMergedAnnotationIfPossible(int annotationIndex, int mappingIndex) {
      MergedAnnotation<?> root = MergedAnnotationsCollection.this.annotations[annotationIndex];
      if (mappingIndex == 0) {
        return (MergedAnnotation)root;
      }
      IntrospectionFailureLogger logger = (this.requiredType != null) ? IntrospectionFailureLogger.INFO : IntrospectionFailureLogger.DEBUG;
      
      return TypeMappedAnnotation.createIfPossible(MergedAnnotationsCollection.this
          .mappings[annotationIndex].get(mappingIndex), root, logger);
    }

    
    @Nullable
    public Spliterator<MergedAnnotation<A>> trySplit() {
      return null;
    }

    
    public long estimateSize() {
      int size = 0;
      for (int i = 0; i < MergedAnnotationsCollection.this.annotations.length; i++) {
        AnnotationTypeMappings mappings = MergedAnnotationsCollection.this.mappings[i];
        int numberOfMappings = mappings.size();
        numberOfMappings -= Math.min(this.mappingCursors[i], mappings.size());
        size += numberOfMappings;
      } 
      return size;
    }

    
    public int characteristics() {
      return 1280;
    }
  }
}
