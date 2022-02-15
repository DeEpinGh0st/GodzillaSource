package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public class OpenSSLPBEParametersGenerator extends PBEParametersGenerator {
  private Digest digest = DigestFactory.createMD5();
  
  public void init(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    init(paramArrayOfbyte1, paramArrayOfbyte2, 1);
  }
  
  private byte[] generateDerivedKey(int paramInt) {
    byte[] arrayOfByte1 = new byte[this.digest.getDigestSize()];
    byte[] arrayOfByte2 = new byte[paramInt];
    int i = 0;
    while (true) {
      this.digest.update(this.password, 0, this.password.length);
      this.digest.update(this.salt, 0, this.salt.length);
      this.digest.doFinal(arrayOfByte1, 0);
      int j = (paramInt > arrayOfByte1.length) ? arrayOfByte1.length : paramInt;
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, i, j);
      i += j;
      paramInt -= j;
      if (paramInt == 0)
        return arrayOfByte2; 
      this.digest.reset();
      this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
    } 
  }
  
  public CipherParameters generateDerivedParameters(int paramInt) {
    paramInt /= 8;
    byte[] arrayOfByte = generateDerivedKey(paramInt);
    return (CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt);
  }
  
  public CipherParameters generateDerivedParameters(int paramInt1, int paramInt2) {
    paramInt1 /= 8;
    paramInt2 /= 8;
    byte[] arrayOfByte = generateDerivedKey(paramInt1 + paramInt2);
    return (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt1), arrayOfByte, paramInt1, paramInt2);
  }
  
  public CipherParameters generateDerivedMacParameters(int paramInt) {
    return generateDerivedParameters(paramInt);
  }
}
