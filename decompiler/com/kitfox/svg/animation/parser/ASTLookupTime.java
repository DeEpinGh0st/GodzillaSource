package com.kitfox.svg.animation.parser;


public class ASTLookupTime
  extends SimpleNode
{
  public ASTLookupTime(int id) {
    super(id);
  }
  
  public ASTLookupTime(AnimTimeParser p, int id) {
    super(p, id);
  }
}
