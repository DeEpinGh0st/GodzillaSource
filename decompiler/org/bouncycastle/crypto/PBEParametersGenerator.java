package org.bouncycastle.crypto;

import org.bouncycastle.util.Strings;

public abstract class PBEParametersGenerator {
  protected byte[] password;
  
  protected byte[] salt;
  
  protected int iterationCount;
  
  public void init(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    this.password = paramArrayOfbyte1;
    this.salt = paramArrayOfbyte2;
    this.iterationCount = paramInt;
  }
  
  public byte[] getPassword() {
    return this.password;
  }
  
  public byte[] getSalt() {
    return this.salt;
  }
  
  public int getIterationCount() {
    return this.iterationCount;
  }
  
  public abstract CipherParameters generateDerivedParameters(int paramInt);
  
  public abstract CipherParameters generateDerivedParameters(int paramInt1, int paramInt2);
  
  public abstract CipherParameters generateDerivedMacParameters(int paramInt);
  
  public static byte[] PKCS5PasswordToBytes(char[] paramArrayOfchar) {
    if (paramArrayOfchar != null) {
      byte[] arrayOfByte = new byte[paramArrayOfchar.length];
      for (byte b = 0; b != arrayOfByte.length; b++)
        arrayOfByte[b] = (byte)paramArrayOfchar[b]; 
      return arrayOfByte;
    } 
    return new byte[0];
  }
  
  public static byte[] PKCS5PasswordToUTF8Bytes(char[] paramArrayOfchar) {
    return (paramArrayOfchar != null) ? Strings.toUTF8ByteArray(paramArrayOfchar) : new byte[0];
  }
  
  public static byte[] PKCS12PasswordToBytes(char[] paramArrayOfchar) {
    if (paramArrayOfchar != null && paramArrayOfchar.length > 0) {
      byte[] arrayOfByte = new byte[(paramArrayOfchar.length + 1) * 2];
      for (byte b = 0; b != paramArrayOfchar.length; b++) {
        arrayOfByte[b * 2] = (byte)(paramArrayOfchar[b] >>> 8);
        arrayOfByte[b * 2 + 1] = (byte)paramArrayOfchar[b];
      } 
      return arrayOfByte;
    } 
    return new byte[0];
  }
}
