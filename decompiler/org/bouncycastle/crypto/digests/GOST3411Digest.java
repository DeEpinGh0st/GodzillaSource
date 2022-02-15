package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class GOST3411Digest implements ExtendedDigest, Memoable {
  private static final int DIGEST_LENGTH = 32;
  
  private byte[] H = new byte[32];
  
  private byte[] L = new byte[32];
  
  private byte[] M = new byte[32];
  
  private byte[] Sum = new byte[32];
  
  private byte[][] C = new byte[4][32];
  
  private byte[] xBuf = new byte[32];
  
  private int xBufOff;
  
  private long byteCount;
  
  private BlockCipher cipher = (BlockCipher)new GOST28147Engine();
  
  private byte[] sBox;
  
  private byte[] K = new byte[32];
  
  byte[] a = new byte[8];
  
  short[] wS = new short[16];
  
  short[] w_S = new short[16];
  
  byte[] S = new byte[32];
  
  byte[] U = new byte[32];
  
  byte[] V = new byte[32];
  
  byte[] W = new byte[32];
  
  private static final byte[] C2 = new byte[] { 
      0, -1, 0, -1, 0, -1, 0, -1, -1, 0, 
      -1, 0, -1, 0, -1, 0, 0, -1, -1, 0, 
      -1, 0, 0, -1, -1, 0, 0, 0, -1, -1, 
      0, -1 };
  
  public GOST3411Digest() {
    this.sBox = GOST28147Engine.getSBox("D-A");
    this.cipher.init(true, (CipherParameters)new ParametersWithSBox(null, this.sBox));
    reset();
  }
  
  public GOST3411Digest(byte[] paramArrayOfbyte) {
    this.sBox = Arrays.clone(paramArrayOfbyte);
    this.cipher.init(true, (CipherParameters)new ParametersWithSBox(null, this.sBox));
    reset();
  }
  
  public GOST3411Digest(GOST3411Digest paramGOST3411Digest) {
    reset(paramGOST3411Digest);
  }
  
  public String getAlgorithmName() {
    return "GOST3411";
  }
  
  public int getDigestSize() {
    return 32;
  }
  
  public void update(byte paramByte) {
    this.xBuf[this.xBufOff++] = paramByte;
    if (this.xBufOff == this.xBuf.length) {
      sumByteArray(this.xBuf);
      processBlock(this.xBuf, 0);
      this.xBufOff = 0;
    } 
    this.byteCount++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (this.xBufOff != 0 && paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
    while (paramInt2 > this.xBuf.length) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.xBuf, 0, this.xBuf.length);
      sumByteArray(this.xBuf);
      processBlock(this.xBuf, 0);
      paramInt1 += this.xBuf.length;
      paramInt2 -= this.xBuf.length;
      this.byteCount += this.xBuf.length;
    } 
    while (paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
  }
  
  private byte[] P(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < 8; b++) {
      this.K[4 * b] = paramArrayOfbyte[b];
      this.K[1 + 4 * b] = paramArrayOfbyte[8 + b];
      this.K[2 + 4 * b] = paramArrayOfbyte[16 + b];
      this.K[3 + 4 * b] = paramArrayOfbyte[24 + b];
    } 
    return this.K;
  }
  
  private byte[] A(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < 8; b++)
      this.a[b] = (byte)(paramArrayOfbyte[b] ^ paramArrayOfbyte[b + 8]); 
    System.arraycopy(paramArrayOfbyte, 8, paramArrayOfbyte, 0, 24);
    System.arraycopy(this.a, 0, paramArrayOfbyte, 24, 8);
    return paramArrayOfbyte;
  }
  
  private void E(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, byte[] paramArrayOfbyte3, int paramInt2) {
    this.cipher.init(true, (CipherParameters)new KeyParameter(paramArrayOfbyte1));
    this.cipher.processBlock(paramArrayOfbyte3, paramInt2, paramArrayOfbyte2, paramInt1);
  }
  
  private void fw(byte[] paramArrayOfbyte) {
    cpyBytesToShort(paramArrayOfbyte, this.wS);
    this.w_S[15] = (short)(this.wS[0] ^ this.wS[1] ^ this.wS[2] ^ this.wS[3] ^ this.wS[12] ^ this.wS[15]);
    System.arraycopy(this.wS, 1, this.w_S, 0, 15);
    cpyShortToBytes(this.w_S, paramArrayOfbyte);
  }
  
  protected void processBlock(byte[] paramArrayOfbyte, int paramInt) {
    System.arraycopy(paramArrayOfbyte, paramInt, this.M, 0, 32);
    System.arraycopy(this.H, 0, this.U, 0, 32);
    System.arraycopy(this.M, 0, this.V, 0, 32);
    byte b;
    for (b = 0; b < 32; b++)
      this.W[b] = (byte)(this.U[b] ^ this.V[b]); 
    E(P(this.W), this.S, 0, this.H, 0);
    for (b = 1; b < 4; b++) {
      byte[] arrayOfByte = A(this.U);
      byte b1;
      for (b1 = 0; b1 < 32; b1++)
        this.U[b1] = (byte)(arrayOfByte[b1] ^ this.C[b][b1]); 
      this.V = A(A(this.V));
      for (b1 = 0; b1 < 32; b1++)
        this.W[b1] = (byte)(this.U[b1] ^ this.V[b1]); 
      E(P(this.W), this.S, b * 8, this.H, b * 8);
    } 
    for (b = 0; b < 12; b++)
      fw(this.S); 
    for (b = 0; b < 32; b++)
      this.S[b] = (byte)(this.S[b] ^ this.M[b]); 
    fw(this.S);
    for (b = 0; b < 32; b++)
      this.S[b] = (byte)(this.H[b] ^ this.S[b]); 
    for (b = 0; b < 61; b++)
      fw(this.S); 
    System.arraycopy(this.S, 0, this.H, 0, this.H.length);
  }
  
  private void finish() {
    Pack.longToLittleEndian(this.byteCount * 8L, this.L, 0);
    while (this.xBufOff != 0)
      update((byte)0); 
    processBlock(this.L, 0);
    processBlock(this.Sum, 0);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    System.arraycopy(this.H, 0, paramArrayOfbyte, paramInt, this.H.length);
    reset();
    return 32;
  }
  
  public void reset() {
    this.byteCount = 0L;
    this.xBufOff = 0;
    byte b;
    for (b = 0; b < this.H.length; b++)
      this.H[b] = 0; 
    for (b = 0; b < this.L.length; b++)
      this.L[b] = 0; 
    for (b = 0; b < this.M.length; b++)
      this.M[b] = 0; 
    for (b = 0; b < (this.C[1]).length; b++)
      this.C[1][b] = 0; 
    for (b = 0; b < (this.C[3]).length; b++)
      this.C[3][b] = 0; 
    for (b = 0; b < this.Sum.length; b++)
      this.Sum[b] = 0; 
    for (b = 0; b < this.xBuf.length; b++)
      this.xBuf[b] = 0; 
    System.arraycopy(C2, 0, this.C[2], 0, C2.length);
  }
  
  private void sumByteArray(byte[] paramArrayOfbyte) {
    int i = 0;
    for (byte b = 0; b != this.Sum.length; b++) {
      int j = (this.Sum[b] & 0xFF) + (paramArrayOfbyte[b] & 0xFF) + i;
      this.Sum[b] = (byte)j;
      i = j >>> 8;
    } 
  }
  
  private void cpyBytesToShort(byte[] paramArrayOfbyte, short[] paramArrayOfshort) {
    for (byte b = 0; b < paramArrayOfbyte.length / 2; b++)
      paramArrayOfshort[b] = (short)(paramArrayOfbyte[b * 2 + 1] << 8 & 0xFF00 | paramArrayOfbyte[b * 2] & 0xFF); 
  }
  
  private void cpyShortToBytes(short[] paramArrayOfshort, byte[] paramArrayOfbyte) {
    for (byte b = 0; b < paramArrayOfbyte.length / 2; b++) {
      paramArrayOfbyte[b * 2 + 1] = (byte)(paramArrayOfshort[b] >> 8);
      paramArrayOfbyte[b * 2] = (byte)paramArrayOfshort[b];
    } 
  }
  
  public int getByteLength() {
    return 32;
  }
  
  public Memoable copy() {
    return new GOST3411Digest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    GOST3411Digest gOST3411Digest = (GOST3411Digest)paramMemoable;
    this.sBox = gOST3411Digest.sBox;
    this.cipher.init(true, (CipherParameters)new ParametersWithSBox(null, this.sBox));
    reset();
    System.arraycopy(gOST3411Digest.H, 0, this.H, 0, gOST3411Digest.H.length);
    System.arraycopy(gOST3411Digest.L, 0, this.L, 0, gOST3411Digest.L.length);
    System.arraycopy(gOST3411Digest.M, 0, this.M, 0, gOST3411Digest.M.length);
    System.arraycopy(gOST3411Digest.Sum, 0, this.Sum, 0, gOST3411Digest.Sum.length);
    System.arraycopy(gOST3411Digest.C[1], 0, this.C[1], 0, (gOST3411Digest.C[1]).length);
    System.arraycopy(gOST3411Digest.C[2], 0, this.C[2], 0, (gOST3411Digest.C[2]).length);
    System.arraycopy(gOST3411Digest.C[3], 0, this.C[3], 0, (gOST3411Digest.C[3]).length);
    System.arraycopy(gOST3411Digest.xBuf, 0, this.xBuf, 0, gOST3411Digest.xBuf.length);
    this.xBufOff = gOST3411Digest.xBufOff;
    this.byteCount = gOST3411Digest.byteCount;
  }
}
