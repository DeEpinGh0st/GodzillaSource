package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class SSL3Mac implements Mac {
  private static final byte IPAD_BYTE = 54;
  
  private static final byte OPAD_BYTE = 92;
  
  static final byte[] IPAD = genPad((byte)54, 48);
  
  static final byte[] OPAD = genPad((byte)92, 48);
  
  private Digest digest;
  
  private int padLength;
  
  private byte[] secret;
  
  public SSL3Mac(Digest paramDigest) {
    this.digest = paramDigest;
    if (paramDigest.getDigestSize() == 20) {
      this.padLength = 40;
    } else {
      this.padLength = 48;
    } 
  }
  
  public String getAlgorithmName() {
    return this.digest.getAlgorithmName() + "/SSL3MAC";
  }
  
  public Digest getUnderlyingDigest() {
    return this.digest;
  }
  
  public void init(CipherParameters paramCipherParameters) {
    this.secret = Arrays.clone(((KeyParameter)paramCipherParameters).getKey());
    reset();
  }
  
  public int getMacSize() {
    return this.digest.getDigestSize();
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    this.digest.update(this.secret, 0, this.secret.length);
    this.digest.update(OPAD, 0, this.padLength);
    this.digest.update(arrayOfByte, 0, arrayOfByte.length);
    int i = this.digest.doFinal(paramArrayOfbyte, paramInt);
    reset();
    return i;
  }
  
  public void reset() {
    this.digest.reset();
    this.digest.update(this.secret, 0, this.secret.length);
    this.digest.update(IPAD, 0, this.padLength);
  }
  
  private static byte[] genPad(byte paramByte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    Arrays.fill(arrayOfByte, paramByte);
    return arrayOfByte;
  }
}
