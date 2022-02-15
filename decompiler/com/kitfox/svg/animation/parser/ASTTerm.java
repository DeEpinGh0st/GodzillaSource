package com.kitfox.svg.animation.parser;


public class ASTTerm
  extends SimpleNode
{
  public ASTTerm(int id) {
    super(id);
  }
  
  public ASTTerm(AnimTimeParser p, int id) {
    super(p, id);
  }
}
