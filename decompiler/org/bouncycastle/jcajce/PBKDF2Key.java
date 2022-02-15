package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.util.Arrays;

public class PBKDF2Key implements PBKDFKey {
  private final char[] password;
  
  private final CharToByteConverter converter;
  
  public PBKDF2Key(char[] paramArrayOfchar, CharToByteConverter paramCharToByteConverter) {
    this.password = Arrays.clone(paramArrayOfchar);
    this.converter = paramCharToByteConverter;
  }
  
  public char[] getPassword() {
    return this.password;
  }
  
  public String getAlgorithm() {
    return "PBKDF2";
  }
  
  public String getFormat() {
    return this.converter.getType();
  }
  
  public byte[] getEncoded() {
    return this.converter.convert(this.password);
  }
}
