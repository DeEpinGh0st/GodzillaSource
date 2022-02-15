package org.springframework.expression;

public interface BeanResolver {
  Object resolve(EvaluationContext paramEvaluationContext, String paramString) throws AccessException;
}
