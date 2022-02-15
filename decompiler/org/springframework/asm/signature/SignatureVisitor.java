package org.springframework.asm.signature;

public interface SignatureVisitor {
  public static final char EXTENDS = '+';
  
  public static final char SUPER = '-';
  
  public static final char INSTANCEOF = '=';
  
  void visitFormalTypeParameter(String paramString);
  
  SignatureVisitor visitClassBound();
  
  SignatureVisitor visitInterfaceBound();
  
  SignatureVisitor visitSuperclass();
  
  SignatureVisitor visitInterface();
  
  SignatureVisitor visitParameterType();
  
  SignatureVisitor visitReturnType();
  
  SignatureVisitor visitExceptionType();
  
  void visitBaseType(char paramChar);
  
  void visitTypeVariable(String paramString);
  
  SignatureVisitor visitArrayType();
  
  void visitClassType(String paramString);
  
  void visitInnerClassType(String paramString);
  
  void visitTypeArgument();
  
  SignatureVisitor visitTypeArgument(char paramChar);
  
  void visitEnd();
}
