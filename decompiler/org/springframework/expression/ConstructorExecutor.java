package org.springframework.expression;

public interface ConstructorExecutor {
  TypedValue execute(EvaluationContext paramEvaluationContext, Object... paramVarArgs) throws AccessException;
}
