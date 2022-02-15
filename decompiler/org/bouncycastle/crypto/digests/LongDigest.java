package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class LongDigest implements ExtendedDigest, Memoable, EncodableDigest {
  private static final int BYTE_LENGTH = 128;
  
  private byte[] xBuf = new byte[8];
  
  private int xBufOff;
  
  private long byteCount1;
  
  private long byteCount2;
  
  protected long H1;
  
  protected long H2;
  
  protected long H3;
  
  protected long H4;
  
  protected long H5;
  
  protected long H6;
  
  protected long H7;
  
  protected long H8;
  
  private long[] W = new long[80];
  
  private int wOff;
  
  static final long[] K = new long[] { 
      4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 
      2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 
      3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 
      489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, 
      -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 
      2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, 
      -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 
      1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L };
  
  protected LongDigest() {
    this.xBufOff = 0;
    reset();
  }
  
  protected LongDigest(LongDigest paramLongDigest) {
    copyIn(paramLongDigest);
  }
  
  protected void copyIn(LongDigest paramLongDigest) {
    System.arraycopy(paramLongDigest.xBuf, 0, this.xBuf, 0, paramLongDigest.xBuf.length);
    this.xBufOff = paramLongDigest.xBufOff;
    this.byteCount1 = paramLongDigest.byteCount1;
    this.byteCount2 = paramLongDigest.byteCount2;
    this.H1 = paramLongDigest.H1;
    this.H2 = paramLongDigest.H2;
    this.H3 = paramLongDigest.H3;
    this.H4 = paramLongDigest.H4;
    this.H5 = paramLongDigest.H5;
    this.H6 = paramLongDigest.H6;
    this.H7 = paramLongDigest.H7;
    this.H8 = paramLongDigest.H8;
    System.arraycopy(paramLongDigest.W, 0, this.W, 0, paramLongDigest.W.length);
    this.wOff = paramLongDigest.wOff;
  }
  
  protected void populateState(byte[] paramArrayOfbyte) {
    System.arraycopy(this.xBuf, 0, paramArrayOfbyte, 0, this.xBufOff);
    Pack.intToBigEndian(this.xBufOff, paramArrayOfbyte, 8);
    Pack.longToBigEndian(this.byteCount1, paramArrayOfbyte, 12);
    Pack.longToBigEndian(this.byteCount2, paramArrayOfbyte, 20);
    Pack.longToBigEndian(this.H1, paramArrayOfbyte, 28);
    Pack.longToBigEndian(this.H2, paramArrayOfbyte, 36);
    Pack.longToBigEndian(this.H3, paramArrayOfbyte, 44);
    Pack.longToBigEndian(this.H4, paramArrayOfbyte, 52);
    Pack.longToBigEndian(this.H5, paramArrayOfbyte, 60);
    Pack.longToBigEndian(this.H6, paramArrayOfbyte, 68);
    Pack.longToBigEndian(this.H7, paramArrayOfbyte, 76);
    Pack.longToBigEndian(this.H8, paramArrayOfbyte, 84);
    Pack.intToBigEndian(this.wOff, paramArrayOfbyte, 92);
    for (byte b = 0; b < this.wOff; b++)
      Pack.longToBigEndian(this.W[b], paramArrayOfbyte, 96 + b * 8); 
  }
  
  protected void restoreState(byte[] paramArrayOfbyte) {
    this.xBufOff = Pack.bigEndianToInt(paramArrayOfbyte, 8);
    System.arraycopy(paramArrayOfbyte, 0, this.xBuf, 0, this.xBufOff);
    this.byteCount1 = Pack.bigEndianToLong(paramArrayOfbyte, 12);
    this.byteCount2 = Pack.bigEndianToLong(paramArrayOfbyte, 20);
    this.H1 = Pack.bigEndianToLong(paramArrayOfbyte, 28);
    this.H2 = Pack.bigEndianToLong(paramArrayOfbyte, 36);
    this.H3 = Pack.bigEndianToLong(paramArrayOfbyte, 44);
    this.H4 = Pack.bigEndianToLong(paramArrayOfbyte, 52);
    this.H5 = Pack.bigEndianToLong(paramArrayOfbyte, 60);
    this.H6 = Pack.bigEndianToLong(paramArrayOfbyte, 68);
    this.H7 = Pack.bigEndianToLong(paramArrayOfbyte, 76);
    this.H8 = Pack.bigEndianToLong(paramArrayOfbyte, 84);
    this.wOff = Pack.bigEndianToInt(paramArrayOfbyte, 92);
    for (byte b = 0; b < this.wOff; b++)
      this.W[b] = Pack.bigEndianToLong(paramArrayOfbyte, 96 + b * 8); 
  }
  
  protected int getEncodedStateSize() {
    return 96 + this.wOff * 8;
  }
  
  public void update(byte paramByte) {
    this.xBuf[this.xBufOff++] = paramByte;
    if (this.xBufOff == this.xBuf.length) {
      processWord(this.xBuf, 0);
      this.xBufOff = 0;
    } 
    this.byteCount1++;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (this.xBufOff != 0 && paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
    while (paramInt2 > this.xBuf.length) {
      processWord(paramArrayOfbyte, paramInt1);
      paramInt1 += this.xBuf.length;
      paramInt2 -= this.xBuf.length;
      this.byteCount1 += this.xBuf.length;
    } 
    while (paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
  }
  
  public void finish() {
    adjustByteCounts();
    long l1 = this.byteCount1 << 3L;
    long l2 = this.byteCount2;
    update(-128);
    while (this.xBufOff != 0)
      update((byte)0); 
    processLength(l1, l2);
    processBlock();
  }
  
  public void reset() {
    this.byteCount1 = 0L;
    this.byteCount2 = 0L;
    this.xBufOff = 0;
    byte b;
    for (b = 0; b < this.xBuf.length; b++)
      this.xBuf[b] = 0; 
    this.wOff = 0;
    for (b = 0; b != this.W.length; b++)
      this.W[b] = 0L; 
  }
  
  public int getByteLength() {
    return 128;
  }
  
  protected void processWord(byte[] paramArrayOfbyte, int paramInt) {
    this.W[this.wOff] = Pack.bigEndianToLong(paramArrayOfbyte, paramInt);
    if (++this.wOff == 16)
      processBlock(); 
  }
  
  private void adjustByteCounts() {
    if (this.byteCount1 > 2305843009213693951L) {
      this.byteCount2 += this.byteCount1 >>> 61L;
      this.byteCount1 &= 0x1FFFFFFFFFFFFFFFL;
    } 
  }
  
  protected void processLength(long paramLong1, long paramLong2) {
    if (this.wOff > 14)
      processBlock(); 
    this.W[14] = paramLong2;
    this.W[15] = paramLong1;
  }
  
  protected void processBlock() {
    adjustByteCounts();
    for (byte b1 = 16; b1 <= 79; b1++)
      this.W[b1] = Sigma1(this.W[b1 - 2]) + this.W[b1 - 7] + Sigma0(this.W[b1 - 15]) + this.W[b1 - 16]; 
    long l1 = this.H1;
    long l2 = this.H2;
    long l3 = this.H3;
    long l4 = this.H4;
    long l5 = this.H5;
    long l6 = this.H6;
    long l7 = this.H7;
    long l8 = this.H8;
    byte b2 = 0;
    byte b3;
    for (b3 = 0; b3 < 10; b3++) {
      l8 += Sum1(l5) + Ch(l5, l6, l7) + K[b2] + this.W[b2++];
      l4 += l8;
      l8 += Sum0(l1) + Maj(l1, l2, l3);
      l7 += Sum1(l4) + Ch(l4, l5, l6) + K[b2] + this.W[b2++];
      l3 += l7;
      l7 += Sum0(l8) + Maj(l8, l1, l2);
      l6 += Sum1(l3) + Ch(l3, l4, l5) + K[b2] + this.W[b2++];
      l2 += l6;
      l6 += Sum0(l7) + Maj(l7, l8, l1);
      l5 += Sum1(l2) + Ch(l2, l3, l4) + K[b2] + this.W[b2++];
      l1 += l5;
      l5 += Sum0(l6) + Maj(l6, l7, l8);
      l4 += Sum1(l1) + Ch(l1, l2, l3) + K[b2] + this.W[b2++];
      l8 += l4;
      l4 += Sum0(l5) + Maj(l5, l6, l7);
      l3 += Sum1(l8) + Ch(l8, l1, l2) + K[b2] + this.W[b2++];
      l7 += l3;
      l3 += Sum0(l4) + Maj(l4, l5, l6);
      l2 += Sum1(l7) + Ch(l7, l8, l1) + K[b2] + this.W[b2++];
      l6 += l2;
      l2 += Sum0(l3) + Maj(l3, l4, l5);
      l1 += Sum1(l6) + Ch(l6, l7, l8) + K[b2] + this.W[b2++];
      l5 += l1;
      l1 += Sum0(l2) + Maj(l2, l3, l4);
    } 
    this.H1 += l1;
    this.H2 += l2;
    this.H3 += l3;
    this.H4 += l4;
    this.H5 += l5;
    this.H6 += l6;
    this.H7 += l7;
    this.H8 += l8;
    this.wOff = 0;
    for (b3 = 0; b3 < 16; b3++)
      this.W[b3] = 0L; 
  }
  
  private long Ch(long paramLong1, long paramLong2, long paramLong3) {
    return paramLong1 & paramLong2 ^ (paramLong1 ^ 0xFFFFFFFFFFFFFFFFL) & paramLong3;
  }
  
  private long Maj(long paramLong1, long paramLong2, long paramLong3) {
    return paramLong1 & paramLong2 ^ paramLong1 & paramLong3 ^ paramLong2 & paramLong3;
  }
  
  private long Sum0(long paramLong) {
    return (paramLong << 36L | paramLong >>> 28L) ^ (paramLong << 30L | paramLong >>> 34L) ^ (paramLong << 25L | paramLong >>> 39L);
  }
  
  private long Sum1(long paramLong) {
    return (paramLong << 50L | paramLong >>> 14L) ^ (paramLong << 46L | paramLong >>> 18L) ^ (paramLong << 23L | paramLong >>> 41L);
  }
  
  private long Sigma0(long paramLong) {
    return (paramLong << 63L | paramLong >>> 1L) ^ (paramLong << 56L | paramLong >>> 8L) ^ paramLong >>> 7L;
  }
  
  private long Sigma1(long paramLong) {
    return (paramLong << 45L | paramLong >>> 19L) ^ (paramLong << 3L | paramLong >>> 61L) ^ paramLong >>> 6L;
  }
}
