package com.kitfox.svg.animation.parser;


public class ASTSum
  extends SimpleNode
{
  public ASTSum(int id) {
    super(id);
  }
  
  public ASTSum(AnimTimeParser p, int id) {
    super(p, id);
  }
}
