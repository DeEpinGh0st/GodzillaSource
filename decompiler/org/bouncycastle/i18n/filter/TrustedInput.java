package org.bouncycastle.i18n.filter;

public class TrustedInput {
  protected Object input;
  
  public TrustedInput(Object paramObject) {
    this.input = paramObject;
  }
  
  public Object getInput() {
    return this.input;
  }
  
  public String toString() {
    return this.input.toString();
  }
}
