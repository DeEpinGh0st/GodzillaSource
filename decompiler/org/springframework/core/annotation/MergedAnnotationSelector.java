package org.springframework.core.annotation;


































@FunctionalInterface
public interface MergedAnnotationSelector<A extends java.lang.annotation.Annotation>
{
  default boolean isBestCandidate(MergedAnnotation<A> annotation) {
    return false;
  }
  
  MergedAnnotation<A> select(MergedAnnotation<A> paramMergedAnnotation1, MergedAnnotation<A> paramMergedAnnotation2);
}
