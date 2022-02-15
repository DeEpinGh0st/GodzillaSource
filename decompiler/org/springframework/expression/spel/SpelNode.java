package org.springframework.expression.spel;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

public interface SpelNode {
  @Nullable
  Object getValue(ExpressionState paramExpressionState) throws EvaluationException;
  
  TypedValue getTypedValue(ExpressionState paramExpressionState) throws EvaluationException;
  
  boolean isWritable(ExpressionState paramExpressionState) throws EvaluationException;
  
  void setValue(ExpressionState paramExpressionState, @Nullable Object paramObject) throws EvaluationException;
  
  String toStringAST();
  
  int getChildCount();
  
  SpelNode getChild(int paramInt);
  
  @Nullable
  Class<?> getObjectClass(@Nullable Object paramObject);
  
  int getStartPosition();
  
  int getEndPosition();
}
