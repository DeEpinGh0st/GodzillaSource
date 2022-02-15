package org.springframework.expression;

public interface MethodExecutor {
  TypedValue execute(EvaluationContext paramEvaluationContext, Object paramObject, Object... paramVarArgs) throws AccessException;
}
