package org.springframework.expression.common;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;




























public class LiteralExpression
  implements Expression
{
  private final String literalValue;
  
  public LiteralExpression(String literalValue) {
    this.literalValue = literalValue;
  }


  
  public final String getExpressionString() {
    return this.literalValue;
  }

  
  public Class<?> getValueType(EvaluationContext context) {
    return String.class;
  }

  
  public String getValue() {
    return this.literalValue;
  }

  
  @Nullable
  public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
    Object value = getValue();
    return ExpressionUtils.convertTypedValue(null, new TypedValue(value), expectedResultType);
  }

  
  public String getValue(@Nullable Object rootObject) {
    return this.literalValue;
  }

  
  @Nullable
  public <T> T getValue(@Nullable Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
    Object value = getValue(rootObject);
    return ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
  }

  
  public String getValue(EvaluationContext context) {
    return this.literalValue;
  }



  
  @Nullable
  public <T> T getValue(EvaluationContext context, @Nullable Class<T> expectedResultType) throws EvaluationException {
    Object value = getValue(context);
    return ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
  }

  
  public String getValue(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
    return this.literalValue;
  }



  
  @Nullable
  public <T> T getValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
    Object value = getValue(context, rootObject);
    return ExpressionUtils.convertTypedValue(context, new TypedValue(value), desiredResultType);
  }

  
  public Class<?> getValueType() {
    return String.class;
  }

  
  public Class<?> getValueType(@Nullable Object rootObject) throws EvaluationException {
    return String.class;
  }

  
  public Class<?> getValueType(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
    return String.class;
  }

  
  public TypeDescriptor getValueTypeDescriptor() {
    return TypeDescriptor.valueOf(String.class);
  }

  
  public TypeDescriptor getValueTypeDescriptor(@Nullable Object rootObject) throws EvaluationException {
    return TypeDescriptor.valueOf(String.class);
  }

  
  public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
    return TypeDescriptor.valueOf(String.class);
  }

  
  public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
    return TypeDescriptor.valueOf(String.class);
  }

  
  public boolean isWritable(@Nullable Object rootObject) throws EvaluationException {
    return false;
  }

  
  public boolean isWritable(EvaluationContext context) {
    return false;
  }

  
  public boolean isWritable(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
    return false;
  }

  
  public void setValue(@Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
    throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
  }

  
  public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
    throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
  }

  
  public void setValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
    throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
  }
}
