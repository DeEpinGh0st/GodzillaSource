package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.PBEParametersGenerator;

public class PKCS12Key implements PBKDFKey {
  private final char[] password;
  
  private final boolean useWrongZeroLengthConversion;
  
  public PKCS12Key(char[] paramArrayOfchar) {
    this(paramArrayOfchar, false);
  }
  
  public PKCS12Key(char[] paramArrayOfchar, boolean paramBoolean) {
    if (paramArrayOfchar == null)
      paramArrayOfchar = new char[0]; 
    this.password = new char[paramArrayOfchar.length];
    this.useWrongZeroLengthConversion = paramBoolean;
    System.arraycopy(paramArrayOfchar, 0, this.password, 0, paramArrayOfchar.length);
  }
  
  public char[] getPassword() {
    return this.password;
  }
  
  public String getAlgorithm() {
    return "PKCS12";
  }
  
  public String getFormat() {
    return "PKCS12";
  }
  
  public byte[] getEncoded() {
    return (this.useWrongZeroLengthConversion && this.password.length == 0) ? new byte[2] : PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
  }
}
