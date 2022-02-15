package com.kitfox.svg.animation.parser;


public class ASTLiteralTime
  extends SimpleNode
{
  public ASTLiteralTime(int id) {
    super(id);
  }
  
  public ASTLiteralTime(AnimTimeParser p, int id) {
    super(p, id);
  }
}
