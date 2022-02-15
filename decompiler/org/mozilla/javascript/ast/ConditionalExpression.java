package org.mozilla.javascript.ast;






















public class ConditionalExpression
  extends AstNode
{
  private AstNode testExpression;
  private AstNode trueExpression;
  private AstNode falseExpression;
  private int questionMarkPosition = -1;
  private int colonPosition = -1;



  
  public ConditionalExpression() {}


  
  public ConditionalExpression(int pos) {
    super(pos);
  }
  
  public ConditionalExpression(int pos, int len) {
    super(pos, len);
  }



  
  public AstNode getTestExpression() {
    return this.testExpression;
  }





  
  public void setTestExpression(AstNode testExpression) {
    assertNotNull(testExpression);
    this.testExpression = testExpression;
    testExpression.setParent(this);
  }



  
  public AstNode getTrueExpression() {
    return this.trueExpression;
  }






  
  public void setTrueExpression(AstNode trueExpression) {
    assertNotNull(trueExpression);
    this.trueExpression = trueExpression;
    trueExpression.setParent(this);
  }



  
  public AstNode getFalseExpression() {
    return this.falseExpression;
  }







  
  public void setFalseExpression(AstNode falseExpression) {
    assertNotNull(falseExpression);
    this.falseExpression = falseExpression;
    falseExpression.setParent(this);
  }



  
  public int getQuestionMarkPosition() {
    return this.questionMarkPosition;
  }




  
  public void setQuestionMarkPosition(int questionMarkPosition) {
    this.questionMarkPosition = questionMarkPosition;
  }



  
  public int getColonPosition() {
    return this.colonPosition;
  }




  
  public void setColonPosition(int colonPosition) {
    this.colonPosition = colonPosition;
  }

  
  public boolean hasSideEffects() {
    if (this.testExpression == null || this.trueExpression == null || this.falseExpression == null)
    {
      codeBug(); } 
    return (this.trueExpression.hasSideEffects() && this.falseExpression.hasSideEffects());
  }


  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append(this.testExpression.toSource(depth));
    sb.append(" ? ");
    sb.append(this.trueExpression.toSource(0));
    sb.append(" : ");
    sb.append(this.falseExpression.toSource(0));
    return sb.toString();
  }





  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.testExpression.visit(v);
      this.trueExpression.visit(v);
      this.falseExpression.visit(v);
    } 
  }
}
