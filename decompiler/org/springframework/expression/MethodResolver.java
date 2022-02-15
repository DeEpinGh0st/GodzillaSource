package org.springframework.expression;

import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public interface MethodResolver {
  @Nullable
  MethodExecutor resolve(EvaluationContext paramEvaluationContext, Object paramObject, String paramString, List<TypeDescriptor> paramList) throws AccessException;
}
