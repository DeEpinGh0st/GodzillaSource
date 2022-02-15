package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.params.MGFParameters;

public class BcFixedLengthMGF1Padder implements EncryptedValuePadder {
  private int length;
  
  private SecureRandom random;
  
  private Digest dig = (Digest)new SHA1Digest();
  
  public BcFixedLengthMGF1Padder(int paramInt) {
    this(paramInt, null);
  }
  
  public BcFixedLengthMGF1Padder(int paramInt, SecureRandom paramSecureRandom) {
    this.length = paramInt;
    this.random = paramSecureRandom;
  }
  
  public byte[] getPaddedData(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = new byte[this.length];
    byte[] arrayOfByte2 = new byte[this.dig.getDigestSize()];
    byte[] arrayOfByte3 = new byte[this.length - this.dig.getDigestSize()];
    if (this.random == null)
      this.random = new SecureRandom(); 
    this.random.nextBytes(arrayOfByte2);
    MGF1BytesGenerator mGF1BytesGenerator = new MGF1BytesGenerator(this.dig);
    mGF1BytesGenerator.init((DerivationParameters)new MGFParameters(arrayOfByte2));
    mGF1BytesGenerator.generateBytes(arrayOfByte3, 0, arrayOfByte3.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, arrayOfByte2.length);
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, arrayOfByte2.length, paramArrayOfbyte.length);
    int i;
    for (i = arrayOfByte2.length + paramArrayOfbyte.length + 1; i != arrayOfByte1.length; i++)
      arrayOfByte1[i] = (byte)(1 + this.random.nextInt(255)); 
    for (i = 0; i != arrayOfByte3.length; i++)
      arrayOfByte1[i + arrayOfByte2.length] = (byte)(arrayOfByte1[i + arrayOfByte2.length] ^ arrayOfByte3[i]); 
    return arrayOfByte1;
  }
  
  public byte[] getUnpaddedData(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = new byte[this.dig.getDigestSize()];
    byte[] arrayOfByte2 = new byte[this.length - this.dig.getDigestSize()];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, arrayOfByte1.length);
    MGF1BytesGenerator mGF1BytesGenerator = new MGF1BytesGenerator(this.dig);
    mGF1BytesGenerator.init((DerivationParameters)new MGFParameters(arrayOfByte1));
    mGF1BytesGenerator.generateBytes(arrayOfByte2, 0, arrayOfByte2.length);
    int i;
    for (i = 0; i != arrayOfByte2.length; i++)
      paramArrayOfbyte[i + arrayOfByte1.length] = (byte)(paramArrayOfbyte[i + arrayOfByte1.length] ^ arrayOfByte2[i]); 
    i = 0;
    for (int j = paramArrayOfbyte.length - 1; j != arrayOfByte1.length; j--) {
      if (paramArrayOfbyte[j] == 0) {
        i = j;
        break;
      } 
    } 
    if (i == 0)
      throw new IllegalStateException("bad padding in encoding"); 
    byte[] arrayOfByte3 = new byte[i - arrayOfByte1.length];
    System.arraycopy(paramArrayOfbyte, arrayOfByte1.length, arrayOfByte3, 0, arrayOfByte3.length);
    return arrayOfByte3;
  }
}
