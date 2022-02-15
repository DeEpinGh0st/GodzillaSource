package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KeccakDigest implements ExtendedDigest {
  private static long[] KeccakRoundConstants = keccakInitializeRoundConstants();
  
  private static int[] KeccakRhoOffsets = keccakInitializeRhoOffsets();
  
  protected long[] state = new long[25];
  
  protected byte[] dataQueue = new byte[192];
  
  protected int rate;
  
  protected int bitsInQueue;
  
  protected int fixedOutputLength;
  
  protected boolean squeezing;
  
  private static long[] keccakInitializeRoundConstants() {
    long[] arrayOfLong = new long[24];
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = 1;
    for (byte b = 0; b < 24; b++) {
      arrayOfLong[b] = 0L;
      for (byte b1 = 0; b1 < 7; b1++) {
        int i = (1 << b1) - 1;
        if (LFSR86540(arrayOfByte))
          arrayOfLong[b] = arrayOfLong[b] ^ 1L << i; 
      } 
    } 
    return arrayOfLong;
  }
  
  private static boolean LFSR86540(byte[] paramArrayOfbyte) {
    boolean bool = ((paramArrayOfbyte[0] & 0x1) != 0) ? true : false;
    if ((paramArrayOfbyte[0] & 0x80) != 0) {
      paramArrayOfbyte[0] = (byte)(paramArrayOfbyte[0] << 1 ^ 0x71);
    } else {
      paramArrayOfbyte[0] = (byte)(paramArrayOfbyte[0] << 1);
    } 
    return bool;
  }
  
  private static int[] keccakInitializeRhoOffsets() {
    int[] arrayOfInt = new int[25];
    arrayOfInt[0] = 0;
    int i = 1;
    int j = 0;
    for (byte b = 0; b < 24; b++) {
      arrayOfInt[i % 5 + 5 * j % 5] = (b + 1) * (b + 2) / 2 % 64;
      int k = (0 * i + 1 * j) % 5;
      int m = (2 * i + 3 * j) % 5;
      i = k;
      j = m;
    } 
    return arrayOfInt;
  }
  
  public KeccakDigest() {
    this(288);
  }
  
  public KeccakDigest(int paramInt) {
    init(paramInt);
  }
  
  public KeccakDigest(KeccakDigest paramKeccakDigest) {
    System.arraycopy(paramKeccakDigest.state, 0, this.state, 0, paramKeccakDigest.state.length);
    System.arraycopy(paramKeccakDigest.dataQueue, 0, this.dataQueue, 0, paramKeccakDigest.dataQueue.length);
    this.rate = paramKeccakDigest.rate;
    this.bitsInQueue = paramKeccakDigest.bitsInQueue;
    this.fixedOutputLength = paramKeccakDigest.fixedOutputLength;
    this.squeezing = paramKeccakDigest.squeezing;
  }
  
  public String getAlgorithmName() {
    return "Keccak-" + this.fixedOutputLength;
  }
  
  public int getDigestSize() {
    return this.fixedOutputLength / 8;
  }
  
  public void update(byte paramByte) {
    absorb(new byte[] { paramByte }, 0, 1);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    absorb(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    squeeze(paramArrayOfbyte, paramInt, this.fixedOutputLength);
    reset();
    return getDigestSize();
  }
  
  protected int doFinal(byte[] paramArrayOfbyte, int paramInt1, byte paramByte, int paramInt2) {
    if (paramInt2 > 0)
      absorbBits(paramByte, paramInt2); 
    squeeze(paramArrayOfbyte, paramInt1, this.fixedOutputLength);
    reset();
    return getDigestSize();
  }
  
  public void reset() {
    init(this.fixedOutputLength);
  }
  
  public int getByteLength() {
    return this.rate / 8;
  }
  
  private void init(int paramInt) {
    switch (paramInt) {
      case 128:
      case 224:
      case 256:
      case 288:
      case 384:
      case 512:
        initSponge(1600 - (paramInt << 1));
        return;
    } 
    throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
  }
  
  private void initSponge(int paramInt) {
    if (paramInt <= 0 || paramInt >= 1600 || paramInt % 64 != 0)
      throw new IllegalStateException("invalid rate value"); 
    this.rate = paramInt;
    for (byte b = 0; b < this.state.length; b++)
      this.state[b] = 0L; 
    Arrays.fill(this.dataQueue, (byte)0);
    this.bitsInQueue = 0;
    this.squeezing = false;
    this.fixedOutputLength = (1600 - paramInt) / 2;
  }
  
  protected void absorb(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.bitsInQueue % 8 != 0)
      throw new IllegalStateException("attempt to absorb with odd length queue"); 
    if (this.squeezing)
      throw new IllegalStateException("attempt to absorb while squeezing"); 
    int i = this.bitsInQueue >> 3;
    int j = this.rate >> 3;
    int k = 0;
    label20: while (k < paramInt2) {
      if (i == 0 && k <= paramInt2 - j)
        while (true) {
          KeccakAbsorb(paramArrayOfbyte, paramInt1 + k);
          k += j;
          if (k > paramInt2 - j)
            continue label20; 
        }  
      int m = Math.min(j - i, paramInt2 - k);
      System.arraycopy(paramArrayOfbyte, paramInt1 + k, this.dataQueue, i, m);
      i += m;
      k += m;
      if (i == j) {
        KeccakAbsorb(this.dataQueue, 0);
        i = 0;
      } 
    } 
    this.bitsInQueue = i << 3;
  }
  
  protected void absorbBits(int paramInt1, int paramInt2) {
    if (paramInt2 < 1 || paramInt2 > 7)
      throw new IllegalArgumentException("'bits' must be in the range 1 to 7"); 
    if (this.bitsInQueue % 8 != 0)
      throw new IllegalStateException("attempt to absorb with odd length queue"); 
    if (this.squeezing)
      throw new IllegalStateException("attempt to absorb while squeezing"); 
    int i = (1 << paramInt2) - 1;
    this.dataQueue[this.bitsInQueue >> 3] = (byte)(paramInt1 & i);
    this.bitsInQueue += paramInt2;
  }
  
  private void padAndSwitchToSqueezingPhase() {
    this.dataQueue[this.bitsInQueue >> 3] = (byte)(this.dataQueue[this.bitsInQueue >> 3] | (byte)(int)(1L << (this.bitsInQueue & 0x7)));
    if (++this.bitsInQueue == this.rate) {
      KeccakAbsorb(this.dataQueue, 0);
      this.bitsInQueue = 0;
    } 
    int i = this.bitsInQueue >> 6;
    int j = this.bitsInQueue & 0x3F;
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      this.state[b] = this.state[b] ^ Pack.littleEndianToLong(this.dataQueue, bool);
      bool += true;
    } 
    if (j > 0) {
      long l = (1L << j) - 1L;
      this.state[i] = this.state[i] ^ Pack.littleEndianToLong(this.dataQueue, bool) & l;
    } 
    this.state[this.rate - 1 >> 6] = this.state[this.rate - 1 >> 6] ^ Long.MIN_VALUE;
    KeccakPermutation();
    KeccakExtract();
    this.bitsInQueue = this.rate;
    this.squeezing = true;
  }
  
  protected void squeeze(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
    if (!this.squeezing)
      padAndSwitchToSqueezingPhase(); 
    if (paramLong % 8L != 0L)
      throw new IllegalStateException("outputLength not a multiple of 8"); 
    long l;
    for (l = 0L; l < paramLong; l += i) {
      if (this.bitsInQueue == 0) {
        KeccakPermutation();
        KeccakExtract();
        this.bitsInQueue = this.rate;
      } 
      int i = (int)Math.min(this.bitsInQueue, paramLong - l);
      System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, paramArrayOfbyte, paramInt + (int)(l / 8L), i / 8);
      this.bitsInQueue -= i;
    } 
  }
  
  private void KeccakAbsorb(byte[] paramArrayOfbyte, int paramInt) {
    int i = this.rate >> 6;
    for (byte b = 0; b < i; b++) {
      this.state[b] = this.state[b] ^ Pack.littleEndianToLong(paramArrayOfbyte, paramInt);
      paramInt += 8;
    } 
    KeccakPermutation();
  }
  
  private void KeccakExtract() {
    Pack.longToLittleEndian(this.state, 0, this.rate >> 6, this.dataQueue, 0);
  }
  
  private void KeccakPermutation() {
    for (byte b = 0; b < 24; b++) {
      theta(this.state);
      rho(this.state);
      pi(this.state);
      chi(this.state);
      iota(this.state, b);
    } 
  }
  
  private static long leftRotate(long paramLong, int paramInt) {
    return paramLong << paramInt | paramLong >>> -paramInt;
  }
  
  private static void theta(long[] paramArrayOflong) {
    long l1 = paramArrayOflong[0] ^ paramArrayOflong[5] ^ paramArrayOflong[10] ^ paramArrayOflong[15] ^ paramArrayOflong[20];
    long l2 = paramArrayOflong[1] ^ paramArrayOflong[6] ^ paramArrayOflong[11] ^ paramArrayOflong[16] ^ paramArrayOflong[21];
    long l3 = paramArrayOflong[2] ^ paramArrayOflong[7] ^ paramArrayOflong[12] ^ paramArrayOflong[17] ^ paramArrayOflong[22];
    long l4 = paramArrayOflong[3] ^ paramArrayOflong[8] ^ paramArrayOflong[13] ^ paramArrayOflong[18] ^ paramArrayOflong[23];
    long l5 = paramArrayOflong[4] ^ paramArrayOflong[9] ^ paramArrayOflong[14] ^ paramArrayOflong[19] ^ paramArrayOflong[24];
    long l6 = leftRotate(l2, 1) ^ l5;
    paramArrayOflong[0] = paramArrayOflong[0] ^ l6;
    paramArrayOflong[5] = paramArrayOflong[5] ^ l6;
    paramArrayOflong[10] = paramArrayOflong[10] ^ l6;
    paramArrayOflong[15] = paramArrayOflong[15] ^ l6;
    paramArrayOflong[20] = paramArrayOflong[20] ^ l6;
    l6 = leftRotate(l3, 1) ^ l1;
    paramArrayOflong[1] = paramArrayOflong[1] ^ l6;
    paramArrayOflong[6] = paramArrayOflong[6] ^ l6;
    paramArrayOflong[11] = paramArrayOflong[11] ^ l6;
    paramArrayOflong[16] = paramArrayOflong[16] ^ l6;
    paramArrayOflong[21] = paramArrayOflong[21] ^ l6;
    l6 = leftRotate(l4, 1) ^ l2;
    paramArrayOflong[2] = paramArrayOflong[2] ^ l6;
    paramArrayOflong[7] = paramArrayOflong[7] ^ l6;
    paramArrayOflong[12] = paramArrayOflong[12] ^ l6;
    paramArrayOflong[17] = paramArrayOflong[17] ^ l6;
    paramArrayOflong[22] = paramArrayOflong[22] ^ l6;
    l6 = leftRotate(l5, 1) ^ l3;
    paramArrayOflong[3] = paramArrayOflong[3] ^ l6;
    paramArrayOflong[8] = paramArrayOflong[8] ^ l6;
    paramArrayOflong[13] = paramArrayOflong[13] ^ l6;
    paramArrayOflong[18] = paramArrayOflong[18] ^ l6;
    paramArrayOflong[23] = paramArrayOflong[23] ^ l6;
    l6 = leftRotate(l1, 1) ^ l4;
    paramArrayOflong[4] = paramArrayOflong[4] ^ l6;
    paramArrayOflong[9] = paramArrayOflong[9] ^ l6;
    paramArrayOflong[14] = paramArrayOflong[14] ^ l6;
    paramArrayOflong[19] = paramArrayOflong[19] ^ l6;
    paramArrayOflong[24] = paramArrayOflong[24] ^ l6;
  }
  
  private static void rho(long[] paramArrayOflong) {
    for (byte b = 1; b < 25; b++)
      paramArrayOflong[b] = leftRotate(paramArrayOflong[b], KeccakRhoOffsets[b]); 
  }
  
  private static void pi(long[] paramArrayOflong) {
    long l = paramArrayOflong[1];
    paramArrayOflong[1] = paramArrayOflong[6];
    paramArrayOflong[6] = paramArrayOflong[9];
    paramArrayOflong[9] = paramArrayOflong[22];
    paramArrayOflong[22] = paramArrayOflong[14];
    paramArrayOflong[14] = paramArrayOflong[20];
    paramArrayOflong[20] = paramArrayOflong[2];
    paramArrayOflong[2] = paramArrayOflong[12];
    paramArrayOflong[12] = paramArrayOflong[13];
    paramArrayOflong[13] = paramArrayOflong[19];
    paramArrayOflong[19] = paramArrayOflong[23];
    paramArrayOflong[23] = paramArrayOflong[15];
    paramArrayOflong[15] = paramArrayOflong[4];
    paramArrayOflong[4] = paramArrayOflong[24];
    paramArrayOflong[24] = paramArrayOflong[21];
    paramArrayOflong[21] = paramArrayOflong[8];
    paramArrayOflong[8] = paramArrayOflong[16];
    paramArrayOflong[16] = paramArrayOflong[5];
    paramArrayOflong[5] = paramArrayOflong[3];
    paramArrayOflong[3] = paramArrayOflong[18];
    paramArrayOflong[18] = paramArrayOflong[17];
    paramArrayOflong[17] = paramArrayOflong[11];
    paramArrayOflong[11] = paramArrayOflong[7];
    paramArrayOflong[7] = paramArrayOflong[10];
    paramArrayOflong[10] = l;
  }
  
  private static void chi(long[] paramArrayOflong) {
    for (byte b = 0; b < 25; b += 5) {
      long l1 = paramArrayOflong[0 + b] ^ (paramArrayOflong[1 + b] ^ 0xFFFFFFFFFFFFFFFFL) & paramArrayOflong[2 + b];
      long l2 = paramArrayOflong[1 + b] ^ (paramArrayOflong[2 + b] ^ 0xFFFFFFFFFFFFFFFFL) & paramArrayOflong[3 + b];
      long l3 = paramArrayOflong[2 + b] ^ (paramArrayOflong[3 + b] ^ 0xFFFFFFFFFFFFFFFFL) & paramArrayOflong[4 + b];
      long l4 = paramArrayOflong[3 + b] ^ (paramArrayOflong[4 + b] ^ 0xFFFFFFFFFFFFFFFFL) & paramArrayOflong[0 + b];
      long l5 = paramArrayOflong[4 + b] ^ (paramArrayOflong[0 + b] ^ 0xFFFFFFFFFFFFFFFFL) & paramArrayOflong[1 + b];
      paramArrayOflong[0 + b] = l1;
      paramArrayOflong[1 + b] = l2;
      paramArrayOflong[2 + b] = l3;
      paramArrayOflong[3 + b] = l4;
      paramArrayOflong[4 + b] = l5;
    } 
  }
  
  private static void iota(long[] paramArrayOflong, int paramInt) {
    paramArrayOflong[0] = paramArrayOflong[0] ^ KeccakRoundConstants[paramInt];
  }
}
