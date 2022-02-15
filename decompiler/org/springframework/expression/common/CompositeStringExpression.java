package org.springframework.expression.common;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;




































public class CompositeStringExpression
  implements Expression
{
  private final String expressionString;
  private final Expression[] expressions;
  
  public CompositeStringExpression(String expressionString, Expression[] expressions) {
    this.expressionString = expressionString;
    this.expressions = expressions;
  }


  
  public final String getExpressionString() {
    return this.expressionString;
  }
  
  public final Expression[] getExpressions() {
    return this.expressions;
  }

  
  public String getValue() throws EvaluationException {
    StringBuilder sb = new StringBuilder();
    for (Expression expression : this.expressions) {
      String value = (String)expression.getValue(String.class);
      if (value != null) {
        sb.append(value);
      }
    } 
    return sb.toString();
  }

  
  @Nullable
  public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
    Object value = getValue();
    return ExpressionUtils.convertTypedValue(null, new TypedValue(value), expectedResultType);
  }

  
  public String getValue(@Nullable Object rootObject) throws EvaluationException {
    StringBuilder sb = new StringBuilder();
    for (Expression expression : this.expressions) {
      String value = (String)expression.getValue(rootObject, String.class);
      if (value != null) {
        sb.append(value);
      }
    } 
    return sb.toString();
  }

  
  @Nullable
  public <T> T getValue(@Nullable Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
    Object value = getValue(rootObject);
    return ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
  }

  
  public String getValue(EvaluationContext context) throws EvaluationException {
    StringBuilder sb = new StringBuilder();
    for (Expression expression : this.expressions) {
      String value = (String)expression.getValue(context, String.class);
      if (value != null) {
        sb.append(value);
      }
    } 
    return sb.toString();
  }



  
  @Nullable
  public <T> T getValue(EvaluationContext context, @Nullable Class<T> expectedResultType) throws EvaluationException {
    Object value = getValue(context);
    return ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
  }

  
  public String getValue(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
    StringBuilder sb = new StringBuilder();
    for (Expression expression : this.expressions) {
      String value = (String)expression.getValue(context, rootObject, String.class);
      if (value != null) {
        sb.append(value);
      }
    } 
    return sb.toString();
  }



  
  @Nullable
  public <T> T getValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
    Object value = getValue(context, rootObject);
    return ExpressionUtils.convertTypedValue(context, new TypedValue(value), desiredResultType);
  }

  
  public Class<?> getValueType() {
    return String.class;
  }

  
  public Class<?> getValueType(EvaluationContext context) {
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
    throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
  }

  
  public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
    throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
  }

  
  public void setValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
    throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
  }
}
