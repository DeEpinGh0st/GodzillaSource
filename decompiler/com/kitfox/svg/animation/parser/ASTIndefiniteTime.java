package com.kitfox.svg.animation.parser;


public class ASTIndefiniteTime
  extends SimpleNode
{
  public ASTIndefiniteTime(int id) {
    super(id);
  }
  
  public ASTIndefiniteTime(AnimTimeParser p, int id) {
    super(p, id);
  }
}
