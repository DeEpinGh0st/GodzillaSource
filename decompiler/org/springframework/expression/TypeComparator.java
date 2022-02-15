package org.springframework.expression;

import org.springframework.lang.Nullable;

public interface TypeComparator {
  boolean canCompare(@Nullable Object paramObject1, @Nullable Object paramObject2);
  
  int compare(@Nullable Object paramObject1, @Nullable Object paramObject2) throws EvaluationException;
}
