package org.springframework.expression;

import org.springframework.lang.Nullable;

public interface OperatorOverloader {
  boolean overridesOperation(Operation paramOperation, @Nullable Object paramObject1, @Nullable Object paramObject2) throws EvaluationException;
  
  Object operate(Operation paramOperation, @Nullable Object paramObject1, @Nullable Object paramObject2) throws EvaluationException;
}
