package org.bouncycastle.crypto.params;

public class DHKeyParameters extends AsymmetricKeyParameter {
  private DHParameters params;
  
  protected DHKeyParameters(boolean paramBoolean, DHParameters paramDHParameters) {
    super(paramBoolean);
    this.params = paramDHParameters;
  }
  
  public DHParameters getParameters() {
    return this.params;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHKeyParameters))
      return false; 
    DHKeyParameters dHKeyParameters = (DHKeyParameters)paramObject;
    return (this.params == null) ? ((dHKeyParameters.getParameters() == null)) : this.params.equals(dHKeyParameters.getParameters());
  }
  
  public int hashCode() {
    int i = isPrivate() ? 0 : 1;
    if (this.params != null)
      i ^= this.params.hashCode(); 
    return i;
  }
}
