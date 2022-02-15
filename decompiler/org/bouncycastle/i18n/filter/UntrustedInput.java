package org.bouncycastle.i18n.filter;

public class UntrustedInput {
  protected Object input;
  
  public UntrustedInput(Object paramObject) {
    this.input = paramObject;
  }
  
  public Object getInput() {
    return this.input;
  }
  
  public String getString() {
    return this.input.toString();
  }
  
  public String toString() {
    return this.input.toString();
  }
}
