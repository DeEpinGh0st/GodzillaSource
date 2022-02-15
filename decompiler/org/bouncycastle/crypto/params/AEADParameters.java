package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class AEADParameters implements CipherParameters {
  private byte[] associatedText;
  
  private byte[] nonce;
  
  private KeyParameter key;
  
  private int macSize;
  
  public AEADParameters(KeyParameter paramKeyParameter, int paramInt, byte[] paramArrayOfbyte) {
    this(paramKeyParameter, paramInt, paramArrayOfbyte, null);
  }
  
  public AEADParameters(KeyParameter paramKeyParameter, int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.key = paramKeyParameter;
    this.nonce = paramArrayOfbyte1;
    this.macSize = paramInt;
    this.associatedText = paramArrayOfbyte2;
  }
  
  public KeyParameter getKey() {
    return this.key;
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  public byte[] getAssociatedText() {
    return this.associatedText;
  }
  
  public byte[] getNonce() {
    return this.nonce;
  }
}
