package com.kitfox.svg.animation.parser;


public class ASTParamList
  extends SimpleNode
{
  public ASTParamList(int id) {
    super(id);
  }
  
  public ASTParamList(AnimTimeParser p, int id) {
    super(p, id);
  }
}
