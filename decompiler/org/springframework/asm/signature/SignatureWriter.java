package org.springframework.asm.signature;

public class SignatureWriter implements SignatureVisitor {
  private final StringBuffer a = new StringBuffer();
  
  private boolean b;
  
  private boolean c;
  
  private int d;
  
  public void visitFormalTypeParameter(String paramString) {
    if (!this.b) {
      this.b = true;
      this.a.append('<');
    } 
    this.a.append(paramString);
    this.a.append(':');
  }
  
  public SignatureVisitor visitClassBound() {
    return this;
  }
  
  public SignatureVisitor visitInterfaceBound() {
    this.a.append(':');
    return this;
  }
  
  public SignatureVisitor visitSuperclass() {
    a();
    return this;
  }
  
  public SignatureVisitor visitInterface() {
    return this;
  }
  
  public SignatureVisitor visitParameterType() {
    a();
    if (!this.c) {
      this.c = true;
      this.a.append('(');
    } 
    return this;
  }
  
  public SignatureVisitor visitReturnType() {
    a();
    if (!this.c)
      this.a.append('('); 
    this.a.append(')');
    return this;
  }
  
  public SignatureVisitor visitExceptionType() {
    this.a.append('^');
    return this;
  }
  
  public void visitBaseType(char paramChar) {
    this.a.append(paramChar);
  }
  
  public void visitTypeVariable(String paramString) {
    this.a.append('T');
    this.a.append(paramString);
    this.a.append(';');
  }
  
  public SignatureVisitor visitArrayType() {
    this.a.append('[');
    return this;
  }
  
  public void visitClassType(String paramString) {
    this.a.append('L');
    this.a.append(paramString);
    this.d *= 2;
  }
  
  public void visitInnerClassType(String paramString) {
    b();
    this.a.append('.');
    this.a.append(paramString);
    this.d *= 2;
  }
  
  public void visitTypeArgument() {
    if (this.d % 2 == 0) {
      this.d++;
      this.a.append('<');
    } 
    this.a.append('*');
  }
  
  public SignatureVisitor visitTypeArgument(char paramChar) {
    if (this.d % 2 == 0) {
      this.d++;
      this.a.append('<');
    } 
    if (paramChar != '=')
      this.a.append(paramChar); 
    return this;
  }
  
  public void visitEnd() {
    b();
    this.a.append(';');
  }
  
  public String toString() {
    return this.a.toString();
  }
  
  private void a() {
    if (this.b) {
      this.b = false;
      this.a.append('>');
    } 
  }
  
  private void b() {
    if (this.d % 2 == 1)
      this.a.append('>'); 
    this.d /= 2;
  }
}
