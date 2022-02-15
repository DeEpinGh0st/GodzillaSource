package com.kitfox.svg.animation.parser;


public class ASTInteger
  extends SimpleNode
{
  public ASTInteger(int id) {
    super(id);
  }
  
  public ASTInteger(AnimTimeParser p, int id) {
    super(p, id);
  }
}
