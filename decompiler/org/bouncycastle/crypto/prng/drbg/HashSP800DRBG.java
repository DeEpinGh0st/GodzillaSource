package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public class HashSP800DRBG implements SP80090DRBG {
  private static final byte[] ONE = new byte[] { 1 };
  
  private static final long RESEED_MAX = 140737488355328L;
  
  private static final int MAX_BITS_REQUEST = 262144;
  
  private static final Hashtable seedlens = new Hashtable<Object, Object>();
  
  private Digest _digest;
  
  private byte[] _V;
  
  private byte[] _C;
  
  private long _reseedCounter;
  
  private EntropySource _entropySource;
  
  private int _securityStrength;
  
  private int _seedLength;
  
  public HashSP800DRBG(Digest paramDigest, int paramInt, EntropySource paramEntropySource, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramInt > Utils.getMaxSecurityStrength(paramDigest))
      throw new IllegalArgumentException("Requested security strength is not supported by the derivation function"); 
    if (paramEntropySource.entropySize() < paramInt)
      throw new IllegalArgumentException("Not enough entropy for security strength required"); 
    this._digest = paramDigest;
    this._entropySource = paramEntropySource;
    this._securityStrength = paramInt;
    this._seedLength = ((Integer)seedlens.get(paramDigest.getAlgorithmName())).intValue();
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, paramArrayOfbyte2, paramArrayOfbyte1);
    byte[] arrayOfByte3 = Utils.hash_df(this._digest, arrayOfByte2, this._seedLength);
    this._V = arrayOfByte3;
    byte[] arrayOfByte4 = new byte[this._V.length + 1];
    System.arraycopy(this._V, 0, arrayOfByte4, 1, this._V.length);
    this._C = Utils.hash_df(this._digest, arrayOfByte4, this._seedLength);
    this._reseedCounter = 1L;
  }
  
  public int getBlockSize() {
    return this._digest.getDigestSize() * 8;
  }
  
  public int generate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, boolean paramBoolean) {
    int i = paramArrayOfbyte1.length * 8;
    if (i > 262144)
      throw new IllegalArgumentException("Number of bits per request limited to 262144"); 
    if (this._reseedCounter > 140737488355328L)
      return -1; 
    if (paramBoolean) {
      reseed(paramArrayOfbyte2);
      paramArrayOfbyte2 = null;
    } 
    if (paramArrayOfbyte2 != null) {
      byte[] arrayOfByte5 = new byte[1 + this._V.length + paramArrayOfbyte2.length];
      arrayOfByte5[0] = 2;
      System.arraycopy(this._V, 0, arrayOfByte5, 1, this._V.length);
      System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte5, 1 + this._V.length, paramArrayOfbyte2.length);
      byte[] arrayOfByte6 = hash(arrayOfByte5);
      addTo(this._V, arrayOfByte6);
    } 
    byte[] arrayOfByte1 = hashgen(this._V, i);
    byte[] arrayOfByte2 = new byte[this._V.length + 1];
    System.arraycopy(this._V, 0, arrayOfByte2, 1, this._V.length);
    arrayOfByte2[0] = 3;
    byte[] arrayOfByte3 = hash(arrayOfByte2);
    addTo(this._V, arrayOfByte3);
    addTo(this._V, this._C);
    byte[] arrayOfByte4 = new byte[4];
    arrayOfByte4[0] = (byte)(int)(this._reseedCounter >> 24L);
    arrayOfByte4[1] = (byte)(int)(this._reseedCounter >> 16L);
    arrayOfByte4[2] = (byte)(int)(this._reseedCounter >> 8L);
    arrayOfByte4[3] = (byte)(int)this._reseedCounter;
    addTo(this._V, arrayOfByte4);
    this._reseedCounter++;
    System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    return i;
  }
  
  private byte[] getEntropy() {
    byte[] arrayOfByte = this._entropySource.getEntropy();
    if (arrayOfByte.length < (this._securityStrength + 7) / 8)
      throw new IllegalStateException("Insufficient entropy provided by entropy source"); 
    return arrayOfByte;
  }
  
  private void addTo(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte b = 0;
    int i;
    for (i = 1; i <= paramArrayOfbyte2.length; i++) {
      int j = (paramArrayOfbyte1[paramArrayOfbyte1.length - i] & 0xFF) + (paramArrayOfbyte2[paramArrayOfbyte2.length - i] & 0xFF) + b;
      b = (j > 255) ? 1 : 0;
      paramArrayOfbyte1[paramArrayOfbyte1.length - i] = (byte)j;
    } 
    for (i = paramArrayOfbyte2.length + 1; i <= paramArrayOfbyte1.length; i++) {
      int j = (paramArrayOfbyte1[paramArrayOfbyte1.length - i] & 0xFF) + b;
      b = (j > 255) ? 1 : 0;
      paramArrayOfbyte1[paramArrayOfbyte1.length - i] = (byte)j;
    } 
  }
  
  public void reseed(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = getEntropy();
    byte[] arrayOfByte2 = Arrays.concatenate(ONE, this._V, arrayOfByte1, paramArrayOfbyte);
    byte[] arrayOfByte3 = Utils.hash_df(this._digest, arrayOfByte2, this._seedLength);
    this._V = arrayOfByte3;
    byte[] arrayOfByte4 = new byte[this._V.length + 1];
    arrayOfByte4[0] = 0;
    System.arraycopy(this._V, 0, arrayOfByte4, 1, this._V.length);
    this._C = Utils.hash_df(this._digest, arrayOfByte4, this._seedLength);
    this._reseedCounter = 1L;
  }
  
  private byte[] hash(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[this._digest.getDigestSize()];
    doHash(paramArrayOfbyte, arrayOfByte);
    return arrayOfByte;
  }
  
  private void doHash(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this._digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    this._digest.doFinal(paramArrayOfbyte2, 0);
  }
  
  private byte[] hashgen(byte[] paramArrayOfbyte, int paramInt) {
    int i = this._digest.getDigestSize();
    int j = paramInt / 8 / i;
    byte[] arrayOfByte1 = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte2 = new byte[paramInt / 8];
    byte[] arrayOfByte3 = new byte[this._digest.getDigestSize()];
    for (byte b = 0; b <= j; b++) {
      doHash(arrayOfByte1, arrayOfByte3);
      int k = (arrayOfByte2.length - b * arrayOfByte3.length > arrayOfByte3.length) ? arrayOfByte3.length : (arrayOfByte2.length - b * arrayOfByte3.length);
      System.arraycopy(arrayOfByte3, 0, arrayOfByte2, b * arrayOfByte3.length, k);
      addTo(arrayOfByte1, ONE);
    } 
    return arrayOfByte2;
  }
  
  static {
    seedlens.put("SHA-1", Integers.valueOf(440));
    seedlens.put("SHA-224", Integers.valueOf(440));
    seedlens.put("SHA-256", Integers.valueOf(440));
    seedlens.put("SHA-512/256", Integers.valueOf(440));
    seedlens.put("SHA-512/224", Integers.valueOf(440));
    seedlens.put("SHA-384", Integers.valueOf(888));
    seedlens.put("SHA-512", Integers.valueOf(888));
  }
}
