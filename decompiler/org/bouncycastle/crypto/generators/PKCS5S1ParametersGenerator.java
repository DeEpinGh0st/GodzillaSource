package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PKCS5S1ParametersGenerator extends PBEParametersGenerator {
  private Digest digest;
  
  public PKCS5S1ParametersGenerator(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  private byte[] generateDerivedKey() {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.update(this.password, 0, this.password.length);
    this.digest.update(this.salt, 0, this.salt.length);
    this.digest.doFinal(arrayOfByte, 0);
    for (byte b = 1; b < this.iterationCount; b++) {
      this.digest.update(arrayOfByte, 0, arrayOfByte.length);
      this.digest.doFinal(arrayOfByte, 0);
    } 
    return arrayOfByte;
  }
  
  public CipherParameters generateDerivedParameters(int paramInt) {
    paramInt /= 8;
    if (paramInt > this.digest.getDigestSize())
      throw new IllegalArgumentException("Can't generate a derived key " + paramInt + " bytes long."); 
    byte[] arrayOfByte = generateDerivedKey();
    return (CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt);
  }
  
  public CipherParameters generateDerivedParameters(int paramInt1, int paramInt2) {
    paramInt1 /= 8;
    paramInt2 /= 8;
    if (paramInt1 + paramInt2 > this.digest.getDigestSize())
      throw new IllegalArgumentException("Can't generate a derived key " + (paramInt1 + paramInt2) + " bytes long."); 
    byte[] arrayOfByte = generateDerivedKey();
    return (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt1), arrayOfByte, paramInt1, paramInt2);
  }
  
  public CipherParameters generateDerivedMacParameters(int paramInt) {
    return generateDerivedParameters(paramInt);
  }
}
