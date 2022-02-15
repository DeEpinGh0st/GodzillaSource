package com.kitfox.svg.animation.parser;


public class ASTEventTime
  extends SimpleNode
{
  public ASTEventTime(int id) {
    super(id);
  }
  
  public ASTEventTime(AnimTimeParser p, int id) {
    super(p, id);
  }
}
