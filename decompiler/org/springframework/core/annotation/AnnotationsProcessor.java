package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import org.springframework.lang.Nullable;



































@FunctionalInterface
interface AnnotationsProcessor<C, R>
{
  @Nullable
  default R doWithAggregate(C context, int aggregateIndex) {
    return null;
  }








  
  @Nullable
  R doWithAnnotations(C paramC, int paramInt, @Nullable Object paramObject, Annotation[] paramArrayOfAnnotation);








  
  @Nullable
  default R finish(@Nullable R result) {
    return result;
  }
}
