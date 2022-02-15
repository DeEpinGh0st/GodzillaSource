package org.fife.rsta.ac.java.rjc.lexer;

public interface Token extends TokenTypes {
  int getColumn();
  
  String getLexeme();
  
  int getLength();
  
  int getLine();
  
  int getOffset();
  
  int getType();
  
  boolean isBasicType();
  
  boolean isIdentifier();
  
  boolean isInvalid();
  
  boolean isOperator();
  
  boolean isType(int paramInt);
}
