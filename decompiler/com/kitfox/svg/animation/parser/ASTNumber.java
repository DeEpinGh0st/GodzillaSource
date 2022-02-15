package com.kitfox.svg.animation.parser;


public class ASTNumber
  extends SimpleNode
{
  public ASTNumber(int id) {
    super(id);
  }
  
  public ASTNumber(AnimTimeParser p, int id) {
    super(p, id);
  }
}
