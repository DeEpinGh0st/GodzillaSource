package org.springframework.expression.spel.ast;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.support.BooleanTypedValue;





















public class OpNE
  extends Operator
{
  public OpNE(int startPos, int endPos, SpelNodeImpl... operands) {
    super("!=", startPos, endPos, operands);
    this.exitTypeDescriptor = "Z";
  }


  
  public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    Object leftValue = getLeftOperand().getValueInternal(state).getValue();
    Object rightValue = getRightOperand().getValueInternal(state).getValue();
    this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(leftValue);
    this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(rightValue);
    return BooleanTypedValue.forValue(!equalityCheck(state.getEvaluationContext(), leftValue, rightValue));
  }



  
  public boolean isCompilable() {
    SpelNodeImpl left = getLeftOperand();
    SpelNodeImpl right = getRightOperand();
    if (!left.isCompilable() || !right.isCompilable()) {
      return false;
    }
    
    String leftDesc = left.exitTypeDescriptor;
    String rightDesc = right.exitTypeDescriptor;
    Operator.DescriptorComparison dc = Operator.DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc, this.leftActualDescriptor, this.rightActualDescriptor);
    
    return (!dc.areNumbers || dc.areCompatible);
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    cf.loadEvaluationContext(mv);
    String leftDesc = (getLeftOperand()).exitTypeDescriptor;
    String rightDesc = (getRightOperand()).exitTypeDescriptor;
    boolean leftPrim = CodeFlow.isPrimitive(leftDesc);
    boolean rightPrim = CodeFlow.isPrimitive(rightDesc);
    
    cf.enterCompilationScope();
    getLeftOperand().generateCode(mv, cf);
    cf.exitCompilationScope();
    if (leftPrim) {
      CodeFlow.insertBoxIfNecessary(mv, leftDesc.charAt(0));
    }
    cf.enterCompilationScope();
    getRightOperand().generateCode(mv, cf);
    cf.exitCompilationScope();
    if (rightPrim) {
      CodeFlow.insertBoxIfNecessary(mv, rightDesc.charAt(0));
    }
    
    String operatorClassName = Operator.class.getName().replace('.', '/');
    String evaluationContextClassName = EvaluationContext.class.getName().replace('.', '/');
    mv.visitMethodInsn(184, operatorClassName, "equalityCheck", "(L" + evaluationContextClassName + ";Ljava/lang/Object;Ljava/lang/Object;)Z", false);


    
    Label notZero = new Label();
    Label end = new Label();
    mv.visitJumpInsn(154, notZero);
    mv.visitInsn(4);
    mv.visitJumpInsn(167, end);
    mv.visitLabel(notZero);
    mv.visitInsn(3);
    mv.visitLabel(end);
    
    cf.pushDescriptor("Z");
  }
}
