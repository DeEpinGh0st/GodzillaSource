package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

























public class Assign
  extends SpelNodeImpl
{
  public Assign(int startPos, int endPos, SpelNodeImpl... operands) {
    super(startPos, endPos, operands);
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    TypedValue newValue = this.children[1].getValueInternal(state);
    getChild(0).setValue(state, newValue.getValue());
    return newValue;
  }

  
  public String toStringAST() {
    return getChild(0).toStringAST() + "=" + getChild(1).toStringAST();
  }
}
