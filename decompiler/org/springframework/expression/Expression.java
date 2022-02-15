package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public interface Expression {
  String getExpressionString();
  
  @Nullable
  Object getValue() throws EvaluationException;
  
  @Nullable
  <T> T getValue(@Nullable Class<T> paramClass) throws EvaluationException;
  
  @Nullable
  Object getValue(@Nullable Object paramObject) throws EvaluationException;
  
  @Nullable
  <T> T getValue(@Nullable Object paramObject, @Nullable Class<T> paramClass) throws EvaluationException;
  
  @Nullable
  Object getValue(EvaluationContext paramEvaluationContext) throws EvaluationException;
  
  @Nullable
  Object getValue(EvaluationContext paramEvaluationContext, @Nullable Object paramObject) throws EvaluationException;
  
  @Nullable
  <T> T getValue(EvaluationContext paramEvaluationContext, @Nullable Class<T> paramClass) throws EvaluationException;
  
  @Nullable
  <T> T getValue(EvaluationContext paramEvaluationContext, @Nullable Object paramObject, @Nullable Class<T> paramClass) throws EvaluationException;
  
  @Nullable
  Class<?> getValueType() throws EvaluationException;
  
  @Nullable
  Class<?> getValueType(@Nullable Object paramObject) throws EvaluationException;
  
  @Nullable
  Class<?> getValueType(EvaluationContext paramEvaluationContext) throws EvaluationException;
  
  @Nullable
  Class<?> getValueType(EvaluationContext paramEvaluationContext, @Nullable Object paramObject) throws EvaluationException;
  
  @Nullable
  TypeDescriptor getValueTypeDescriptor() throws EvaluationException;
  
  @Nullable
  TypeDescriptor getValueTypeDescriptor(@Nullable Object paramObject) throws EvaluationException;
  
  @Nullable
  TypeDescriptor getValueTypeDescriptor(EvaluationContext paramEvaluationContext) throws EvaluationException;
  
  @Nullable
  TypeDescriptor getValueTypeDescriptor(EvaluationContext paramEvaluationContext, @Nullable Object paramObject) throws EvaluationException;
  
  boolean isWritable(@Nullable Object paramObject) throws EvaluationException;
  
  boolean isWritable(EvaluationContext paramEvaluationContext) throws EvaluationException;
  
  boolean isWritable(EvaluationContext paramEvaluationContext, @Nullable Object paramObject) throws EvaluationException;
  
  void setValue(@Nullable Object paramObject1, @Nullable Object paramObject2) throws EvaluationException;
  
  void setValue(EvaluationContext paramEvaluationContext, @Nullable Object paramObject) throws EvaluationException;
  
  void setValue(EvaluationContext paramEvaluationContext, @Nullable Object paramObject1, @Nullable Object paramObject2) throws EvaluationException;
}
