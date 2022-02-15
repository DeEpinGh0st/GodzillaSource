package org.springframework.expression;

import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ConstructorResolver {
  @Nullable
  ConstructorExecutor resolve(EvaluationContext paramEvaluationContext, String paramString, List<TypeDescriptor> paramList) throws AccessException;
}
