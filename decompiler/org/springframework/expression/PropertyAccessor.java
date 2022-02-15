package org.springframework.expression;

import org.springframework.lang.Nullable;

public interface PropertyAccessor {
  @Nullable
  Class<?>[] getSpecificTargetClasses();
  
  boolean canRead(EvaluationContext paramEvaluationContext, @Nullable Object paramObject, String paramString) throws AccessException;
  
  TypedValue read(EvaluationContext paramEvaluationContext, @Nullable Object paramObject, String paramString) throws AccessException;
  
  boolean canWrite(EvaluationContext paramEvaluationContext, @Nullable Object paramObject, String paramString) throws AccessException;
  
  void write(EvaluationContext paramEvaluationContext, @Nullable Object paramObject1, String paramString, @Nullable Object paramObject2) throws AccessException;
}
