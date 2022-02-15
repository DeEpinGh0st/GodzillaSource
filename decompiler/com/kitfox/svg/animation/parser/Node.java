package com.kitfox.svg.animation.parser;

import java.io.Serializable;

public interface Node extends Serializable {
  void jjtOpen();
  
  void jjtClose();
  
  void jjtSetParent(Node paramNode);
  
  Node jjtGetParent();
  
  void jjtAddChild(Node paramNode, int paramInt);
  
  Node jjtGetChild(int paramInt);
  
  int jjtGetNumChildren();
  
  int getId();
}
