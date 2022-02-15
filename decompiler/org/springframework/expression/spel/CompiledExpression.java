package org.springframework.expression.spel;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

public abstract class CompiledExpression {
  public abstract Object getValue(@Nullable Object paramObject, @Nullable EvaluationContext paramEvaluationContext) throws EvaluationException;
}
