package com.kitfox.svg.animation.parser;


public class ASTExpr
  extends SimpleNode
{
  public ASTExpr(int id) {
    super(id);
  }
  
  public ASTExpr(AnimTimeParser p, int id) {
    super(p, id);
  }
}
