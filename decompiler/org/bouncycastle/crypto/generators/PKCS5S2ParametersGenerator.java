package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class PKCS5S2ParametersGenerator extends PBEParametersGenerator {
  private Mac hMac;
  
  private byte[] state;
  
  public PKCS5S2ParametersGenerator() {
    this(DigestFactory.createSHA1());
  }
  
  public PKCS5S2ParametersGenerator(Digest paramDigest) {
    this.hMac = (Mac)new HMac(paramDigest);
    this.state = new byte[this.hMac.getMacSize()];
  }
  
  private void F(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt2) {
    if (paramInt1 == 0)
      throw new IllegalArgumentException("iteration count must be at least 1."); 
    if (paramArrayOfbyte1 != null)
      this.hMac.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length); 
    this.hMac.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    this.hMac.doFinal(this.state, 0);
    System.arraycopy(this.state, 0, paramArrayOfbyte3, paramInt2, this.state.length);
    for (byte b = 1; b < paramInt1; b++) {
      this.hMac.update(this.state, 0, this.state.length);
      this.hMac.doFinal(this.state, 0);
      for (byte b1 = 0; b1 != this.state.length; b1++)
        paramArrayOfbyte3[paramInt2 + b1] = (byte)(paramArrayOfbyte3[paramInt2 + b1] ^ this.state[b1]); 
    } 
  }
  
  private byte[] generateDerivedKey(int paramInt) {
    int i = this.hMac.getMacSize();
    int j = (paramInt + i - 1) / i;
    byte[] arrayOfByte1 = new byte[4];
    byte[] arrayOfByte2 = new byte[j * i];
    int k = 0;
    KeyParameter keyParameter = new KeyParameter(this.password);
    this.hMac.init((CipherParameters)keyParameter);
    for (byte b = 1; b <= j; b++) {
      byte b1 = 3;
      arrayOfByte1[b1] = (byte)(arrayOfByte1[b1] + 1);
      while ((byte)(arrayOfByte1[b1] + 1) == 0)
        b1--; 
      F(this.salt, this.iterationCount, arrayOfByte1, arrayOfByte2, k);
      k += i;
    } 
    return arrayOfByte2;
  }
  
  public CipherParameters generateDerivedParameters(int paramInt) {
    paramInt /= 8;
    byte[] arrayOfByte = Arrays.copyOfRange(generateDerivedKey(paramInt), 0, paramInt);
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
