package org.springframework.expression;

import java.util.List;
import org.springframework.lang.Nullable;

public interface EvaluationContext {
  TypedValue getRootObject();
  
  List<PropertyAccessor> getPropertyAccessors();
  
  List<ConstructorResolver> getConstructorResolvers();
  
  List<MethodResolver> getMethodResolvers();
  
  @Nullable
  BeanResolver getBeanResolver();
  
  TypeLocator getTypeLocator();
  
  TypeConverter getTypeConverter();
  
  TypeComparator getTypeComparator();
  
  OperatorOverloader getOperatorOverloader();
  
  void setVariable(String paramString, @Nullable Object paramObject);
  
  @Nullable
  Object lookupVariable(String paramString);
}
