package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class IESParameterSpec implements AlgorithmParameterSpec {
  private byte[] derivation;
  
  private byte[] encoding;
  
  private int macKeySize;
  
  private int cipherKeySize;
  
  private byte[] nonce;
  
  private boolean usePointCompression;
  
  public IESParameterSpec(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    this(paramArrayOfbyte1, paramArrayOfbyte2, paramInt, -1, null, false);
  }
  
  public IESParameterSpec(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2, byte[] paramArrayOfbyte3) {
    this(paramArrayOfbyte1, paramArrayOfbyte2, paramInt1, paramInt2, paramArrayOfbyte3, false);
  }
  
  public IESParameterSpec(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2, byte[] paramArrayOfbyte3, boolean paramBoolean) {
    if (paramArrayOfbyte1 != null) {
      this.derivation = new byte[paramArrayOfbyte1.length];
      System.arraycopy(paramArrayOfbyte1, 0, this.derivation, 0, paramArrayOfbyte1.length);
    } else {
      this.derivation = null;
    } 
    if (paramArrayOfbyte2 != null) {
      this.encoding = new byte[paramArrayOfbyte2.length];
      System.arraycopy(paramArrayOfbyte2, 0, this.encoding, 0, paramArrayOfbyte2.length);
    } else {
      this.encoding = null;
    } 
    this.macKeySize = paramInt1;
    this.cipherKeySize = paramInt2;
    this.nonce = Arrays.clone(paramArrayOfbyte3);
    this.usePointCompression = paramBoolean;
  }
  
  public byte[] getDerivationV() {
    return Arrays.clone(this.derivation);
  }
  
  public byte[] getEncodingV() {
    return Arrays.clone(this.encoding);
  }
  
  public int getMacKeySize() {
    return this.macKeySize;
  }
  
  public int getCipherKeySize() {
    return this.cipherKeySize;
  }
  
  public byte[] getNonce() {
    return Arrays.clone(this.nonce);
  }
  
  public void setPointCompression(boolean paramBoolean) {
    this.usePointCompression = paramBoolean;
  }
  
  public boolean getPointCompression() {
    return this.usePointCompression;
  }
}
