package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public class HKDFParameters implements DerivationParameters {
  private final byte[] ikm;
  
  private final boolean skipExpand;
  
  private final byte[] salt;
  
  private final byte[] info;
  
  private HKDFParameters(byte[] paramArrayOfbyte1, boolean paramBoolean, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    if (paramArrayOfbyte1 == null)
      throw new IllegalArgumentException("IKM (input keying material) should not be null"); 
    this.ikm = Arrays.clone(paramArrayOfbyte1);
    this.skipExpand = paramBoolean;
    if (paramArrayOfbyte2 == null || paramArrayOfbyte2.length == 0) {
      this.salt = null;
    } else {
      this.salt = Arrays.clone(paramArrayOfbyte2);
    } 
    if (paramArrayOfbyte3 == null) {
      this.info = new byte[0];
    } else {
      this.info = Arrays.clone(paramArrayOfbyte3);
    } 
  }
  
  public HKDFParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    this(paramArrayOfbyte1, false, paramArrayOfbyte2, paramArrayOfbyte3);
  }
  
  public static HKDFParameters skipExtractParameters(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return new HKDFParameters(paramArrayOfbyte1, true, null, paramArrayOfbyte2);
  }
  
  public static HKDFParameters defaultParameters(byte[] paramArrayOfbyte) {
    return new HKDFParameters(paramArrayOfbyte, false, null, null);
  }
  
  public byte[] getIKM() {
    return Arrays.clone(this.ikm);
  }
  
  public boolean skipExtract() {
    return this.skipExpand;
  }
  
  public byte[] getSalt() {
    return Arrays.clone(this.salt);
  }
  
  public byte[] getInfo() {
    return Arrays.clone(this.info);
  }
}
