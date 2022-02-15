package org.bouncycastle.crypto.macs;

import java.util.Hashtable;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

public class HMac implements Mac {
  private static final byte IPAD = 54;
  
  private static final byte OPAD = 92;
  
  private Digest digest;
  
  private int digestSize;
  
  private int blockLength;
  
  private Memoable ipadState;
  
  private Memoable opadState;
  
  private byte[] inputPad;
  
  private byte[] outputBuf;
  
  private static Hashtable blockLengths = new Hashtable<Object, Object>();
  
  private static int getByteLength(Digest paramDigest) {
    if (paramDigest instanceof ExtendedDigest)
      return ((ExtendedDigest)paramDigest).getByteLength(); 
    Integer integer = (Integer)blockLengths.get(paramDigest.getAlgorithmName());
    if (integer == null)
      throw new IllegalArgumentException("unknown digest passed: " + paramDigest.getAlgorithmName()); 
    return integer.intValue();
  }
  
  public HMac(Digest paramDigest) {
    this(paramDigest, getByteLength(paramDigest));
  }
  
  private HMac(Digest paramDigest, int paramInt) {
    this.digest = paramDigest;
    this.digestSize = paramDigest.getDigestSize();
    this.blockLength = paramInt;
    this.inputPad = new byte[this.blockLength];
    this.outputBuf = new byte[this.blockLength + this.digestSize];
  }
  
  public String getAlgorithmName() {
    return this.digest.getAlgorithmName() + "/HMAC";
  }
  
  public Digest getUnderlyingDigest() {
    return this.digest;
  }
  
  public void init(CipherParameters paramCipherParameters) {
    this.digest.reset();
    byte[] arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
    int i = arrayOfByte.length;
    if (i > this.blockLength) {
      this.digest.update(arrayOfByte, 0, i);
      this.digest.doFinal(this.inputPad, 0);
      i = this.digestSize;
    } else {
      System.arraycopy(arrayOfByte, 0, this.inputPad, 0, i);
    } 
    for (int j = i; j < this.inputPad.length; j++)
      this.inputPad[j] = 0; 
    System.arraycopy(this.inputPad, 0, this.outputBuf, 0, this.blockLength);
    xorPad(this.inputPad, this.blockLength, (byte)54);
    xorPad(this.outputBuf, this.blockLength, (byte)92);
    if (this.digest instanceof Memoable) {
      this.opadState = ((Memoable)this.digest).copy();
      ((Digest)this.opadState).update(this.outputBuf, 0, this.blockLength);
    } 
    this.digest.update(this.inputPad, 0, this.inputPad.length);
    if (this.digest instanceof Memoable)
      this.ipadState = ((Memoable)this.digest).copy(); 
  }
  
  public int getMacSize() {
    return this.digestSize;
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    this.digest.doFinal(this.outputBuf, this.blockLength);
    if (this.opadState != null) {
      ((Memoable)this.digest).reset(this.opadState);
      this.digest.update(this.outputBuf, this.blockLength, this.digest.getDigestSize());
    } else {
      this.digest.update(this.outputBuf, 0, this.outputBuf.length);
    } 
    int i = this.digest.doFinal(paramArrayOfbyte, paramInt);
    for (int j = this.blockLength; j < this.outputBuf.length; j++)
      this.outputBuf[j] = 0; 
    if (this.ipadState != null) {
      ((Memoable)this.digest).reset(this.ipadState);
    } else {
      this.digest.update(this.inputPad, 0, this.inputPad.length);
    } 
    return i;
  }
  
  public void reset() {
    this.digest.reset();
    this.digest.update(this.inputPad, 0, this.inputPad.length);
  }
  
  private static void xorPad(byte[] paramArrayOfbyte, int paramInt, byte paramByte) {
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfbyte[b] = (byte)(paramArrayOfbyte[b] ^ paramByte); 
  }
  
  static {
    blockLengths.put("GOST3411", Integers.valueOf(32));
    blockLengths.put("MD2", Integers.valueOf(16));
    blockLengths.put("MD4", Integers.valueOf(64));
    blockLengths.put("MD5", Integers.valueOf(64));
    blockLengths.put("RIPEMD128", Integers.valueOf(64));
    blockLengths.put("RIPEMD160", Integers.valueOf(64));
    blockLengths.put("SHA-1", Integers.valueOf(64));
    blockLengths.put("SHA-224", Integers.valueOf(64));
    blockLengths.put("SHA-256", Integers.valueOf(64));
    blockLengths.put("SHA-384", Integers.valueOf(128));
    blockLengths.put("SHA-512", Integers.valueOf(128));
    blockLengths.put("Tiger", Integers.valueOf(64));
    blockLengths.put("Whirlpool", Integers.valueOf(64));
  }
}
