package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;

public final class WhirlpoolDigest implements ExtendedDigest, Memoable {
  private static final int BYTE_LENGTH = 64;
  
  private static final int DIGEST_LENGTH_BYTES = 64;
  
  private static final int ROUNDS = 10;
  
  private static final int REDUCTION_POLYNOMIAL = 285;
  
  private static final int[] SBOX = new int[] { 
      24, 35, 198, 232, 135, 184, 1, 79, 54, 166, 
      210, 245, 121, 111, 145, 82, 96, 188, 155, 142, 
      163, 12, 123, 53, 29, 224, 215, 194, 46, 75, 
      254, 87, 21, 119, 55, 229, 159, 240, 74, 218, 
      88, 201, 41, 10, 177, 160, 107, 133, 189, 93, 
      16, 244, 203, 62, 5, 103, 228, 39, 65, 139, 
      167, 125, 149, 216, 251, 238, 124, 102, 221, 23, 
      71, 158, 202, 45, 191, 7, 173, 90, 131, 51, 
      99, 2, 170, 113, 200, 25, 73, 217, 242, 227, 
      91, 136, 154, 38, 50, 176, 233, 15, 213, 128, 
      190, 205, 52, 72, 255, 122, 144, 95, 32, 104, 
      26, 174, 180, 84, 147, 34, 100, 241, 115, 18, 
      64, 8, 195, 236, 219, 161, 141, 61, 151, 0, 
      207, 43, 118, 130, 214, 27, 181, 175, 106, 80, 
      69, 243, 48, 239, 63, 85, 162, 234, 101, 186, 
      47, 192, 222, 28, 253, 77, 146, 117, 6, 138, 
      178, 230, 14, 31, 98, 212, 168, 150, 249, 197, 
      37, 89, 132, 114, 57, 76, 94, 120, 56, 140, 
      209, 165, 226, 97, 179, 33, 156, 30, 67, 199, 
      252, 4, 81, 153, 109, 13, 250, 223, 126, 36, 
      59, 171, 206, 17, 143, 78, 183, 235, 60, 129, 
      148, 247, 185, 19, 44, 211, 231, 110, 196, 3, 
      86, 68, 127, 169, 42, 187, 193, 83, 220, 11, 
      157, 108, 49, 116, 246, 70, 172, 137, 20, 225, 
      22, 58, 105, 9, 112, 182, 208, 237, 204, 66, 
      152, 164, 40, 92, 248, 134 };
  
  private static final long[] C0 = new long[256];
  
  private static final long[] C1 = new long[256];
  
  private static final long[] C2 = new long[256];
  
  private static final long[] C3 = new long[256];
  
  private static final long[] C4 = new long[256];
  
  private static final long[] C5 = new long[256];
  
  private static final long[] C6 = new long[256];
  
  private static final long[] C7 = new long[256];
  
  private final long[] _rc = new long[11];
  
  private static final int BITCOUNT_ARRAY_SIZE = 32;
  
  private byte[] _buffer = new byte[64];
  
  private int _bufferPos = 0;
  
  private short[] _bitCount = new short[32];
  
  private long[] _hash = new long[8];
  
  private long[] _K = new long[8];
  
  private long[] _L = new long[8];
  
  private long[] _block = new long[8];
  
  private long[] _state = new long[8];
  
  private static final short[] EIGHT = new short[32];
  
  public WhirlpoolDigest() {
    byte b;
    for (b = 0; b < 'Ā'; b++) {
      int i = SBOX[b];
      int j = maskWithReductionPolynomial(i << 1);
      int k = maskWithReductionPolynomial(j << 1);
      int m = k ^ i;
      int n = maskWithReductionPolynomial(k << 1);
      int i1 = n ^ i;
      C0[b] = packIntoLong(i, i, k, i, n, m, j, i1);
      C1[b] = packIntoLong(i1, i, i, k, i, n, m, j);
      C2[b] = packIntoLong(j, i1, i, i, k, i, n, m);
      C3[b] = packIntoLong(m, j, i1, i, i, k, i, n);
      C4[b] = packIntoLong(n, m, j, i1, i, i, k, i);
      C5[b] = packIntoLong(i, n, m, j, i1, i, i, k);
      C6[b] = packIntoLong(k, i, n, m, j, i1, i, i);
      C7[b] = packIntoLong(i, k, i, n, m, j, i1, i);
    } 
    this._rc[0] = 0L;
    for (b = 1; b <= 10; b++) {
      int i = 8 * (b - 1);
      this._rc[b] = C0[i] & 0xFF00000000000000L ^ C1[i + 1] & 0xFF000000000000L ^ C2[i + 2] & 0xFF0000000000L ^ C3[i + 3] & 0xFF00000000L ^ C4[i + 4] & 0xFF000000L ^ C5[i + 5] & 0xFF0000L ^ C6[i + 6] & 0xFF00L ^ C7[i + 7] & 0xFFL;
    } 
  }
  
  private long packIntoLong(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    return paramInt1 << 56L ^ paramInt2 << 48L ^ paramInt3 << 40L ^ paramInt4 << 32L ^ paramInt5 << 24L ^ paramInt6 << 16L ^ paramInt7 << 8L ^ paramInt8;
  }
  
  private int maskWithReductionPolynomial(int paramInt) {
    int i = paramInt;
    if (i >= 256L)
      i ^= 0x11D; 
    return i;
  }
  
  public WhirlpoolDigest(WhirlpoolDigest paramWhirlpoolDigest) {
    reset(paramWhirlpoolDigest);
  }
  
  public String getAlgorithmName() {
    return "Whirlpool";
  }
  
  public int getDigestSize() {
    return 64;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    finish();
    for (byte b = 0; b < 8; b++)
      convertLongToByteArray(this._hash[b], paramArrayOfbyte, paramInt + b * 8); 
    reset();
    return getDigestSize();
  }
  
  public void reset() {
    this._bufferPos = 0;
    Arrays.fill(this._bitCount, (short)0);
    Arrays.fill(this._buffer, (byte)0);
    Arrays.fill(this._hash, 0L);
    Arrays.fill(this._K, 0L);
    Arrays.fill(this._L, 0L);
    Arrays.fill(this._block, 0L);
    Arrays.fill(this._state, 0L);
  }
  
  private void processFilledBuffer(byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < this._state.length; b++)
      this._block[b] = bytesToLongFromBuffer(this._buffer, b * 8); 
    processBlock();
    this._bufferPos = 0;
    Arrays.fill(this._buffer, (byte)0);
  }
  
  private long bytesToLongFromBuffer(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt + 0] & 0xFFL) << 56L | (paramArrayOfbyte[paramInt + 1] & 0xFFL) << 48L | (paramArrayOfbyte[paramInt + 2] & 0xFFL) << 40L | (paramArrayOfbyte[paramInt + 3] & 0xFFL) << 32L | (paramArrayOfbyte[paramInt + 4] & 0xFFL) << 24L | (paramArrayOfbyte[paramInt + 5] & 0xFFL) << 16L | (paramArrayOfbyte[paramInt + 6] & 0xFFL) << 8L | paramArrayOfbyte[paramInt + 7] & 0xFFL;
  }
  
  private void convertLongToByteArray(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < 8; b++)
      paramArrayOfbyte[paramInt + b] = (byte)(int)(paramLong >> 56 - b * 8 & 0xFFL); 
  }
  
  protected void processBlock() {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iload_1
    //   3: bipush #8
    //   5: if_icmpge -> 40
    //   8: aload_0
    //   9: getfield _state : [J
    //   12: iload_1
    //   13: aload_0
    //   14: getfield _block : [J
    //   17: iload_1
    //   18: laload
    //   19: aload_0
    //   20: getfield _K : [J
    //   23: iload_1
    //   24: aload_0
    //   25: getfield _hash : [J
    //   28: iload_1
    //   29: laload
    //   30: dup2_x2
    //   31: lastore
    //   32: lxor
    //   33: lastore
    //   34: iinc #1, 1
    //   37: goto -> 2
    //   40: iconst_1
    //   41: istore_1
    //   42: iload_1
    //   43: bipush #10
    //   45: if_icmpgt -> 662
    //   48: iconst_0
    //   49: istore_2
    //   50: iload_2
    //   51: bipush #8
    //   53: if_icmpge -> 324
    //   56: aload_0
    //   57: getfield _L : [J
    //   60: iload_2
    //   61: lconst_0
    //   62: lastore
    //   63: aload_0
    //   64: getfield _L : [J
    //   67: iload_2
    //   68: dup2
    //   69: laload
    //   70: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C0 : [J
    //   73: aload_0
    //   74: getfield _K : [J
    //   77: iload_2
    //   78: iconst_0
    //   79: isub
    //   80: bipush #7
    //   82: iand
    //   83: laload
    //   84: bipush #56
    //   86: lushr
    //   87: l2i
    //   88: sipush #255
    //   91: iand
    //   92: laload
    //   93: lxor
    //   94: lastore
    //   95: aload_0
    //   96: getfield _L : [J
    //   99: iload_2
    //   100: dup2
    //   101: laload
    //   102: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C1 : [J
    //   105: aload_0
    //   106: getfield _K : [J
    //   109: iload_2
    //   110: iconst_1
    //   111: isub
    //   112: bipush #7
    //   114: iand
    //   115: laload
    //   116: bipush #48
    //   118: lushr
    //   119: l2i
    //   120: sipush #255
    //   123: iand
    //   124: laload
    //   125: lxor
    //   126: lastore
    //   127: aload_0
    //   128: getfield _L : [J
    //   131: iload_2
    //   132: dup2
    //   133: laload
    //   134: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C2 : [J
    //   137: aload_0
    //   138: getfield _K : [J
    //   141: iload_2
    //   142: iconst_2
    //   143: isub
    //   144: bipush #7
    //   146: iand
    //   147: laload
    //   148: bipush #40
    //   150: lushr
    //   151: l2i
    //   152: sipush #255
    //   155: iand
    //   156: laload
    //   157: lxor
    //   158: lastore
    //   159: aload_0
    //   160: getfield _L : [J
    //   163: iload_2
    //   164: dup2
    //   165: laload
    //   166: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C3 : [J
    //   169: aload_0
    //   170: getfield _K : [J
    //   173: iload_2
    //   174: iconst_3
    //   175: isub
    //   176: bipush #7
    //   178: iand
    //   179: laload
    //   180: bipush #32
    //   182: lushr
    //   183: l2i
    //   184: sipush #255
    //   187: iand
    //   188: laload
    //   189: lxor
    //   190: lastore
    //   191: aload_0
    //   192: getfield _L : [J
    //   195: iload_2
    //   196: dup2
    //   197: laload
    //   198: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C4 : [J
    //   201: aload_0
    //   202: getfield _K : [J
    //   205: iload_2
    //   206: iconst_4
    //   207: isub
    //   208: bipush #7
    //   210: iand
    //   211: laload
    //   212: bipush #24
    //   214: lushr
    //   215: l2i
    //   216: sipush #255
    //   219: iand
    //   220: laload
    //   221: lxor
    //   222: lastore
    //   223: aload_0
    //   224: getfield _L : [J
    //   227: iload_2
    //   228: dup2
    //   229: laload
    //   230: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C5 : [J
    //   233: aload_0
    //   234: getfield _K : [J
    //   237: iload_2
    //   238: iconst_5
    //   239: isub
    //   240: bipush #7
    //   242: iand
    //   243: laload
    //   244: bipush #16
    //   246: lushr
    //   247: l2i
    //   248: sipush #255
    //   251: iand
    //   252: laload
    //   253: lxor
    //   254: lastore
    //   255: aload_0
    //   256: getfield _L : [J
    //   259: iload_2
    //   260: dup2
    //   261: laload
    //   262: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C6 : [J
    //   265: aload_0
    //   266: getfield _K : [J
    //   269: iload_2
    //   270: bipush #6
    //   272: isub
    //   273: bipush #7
    //   275: iand
    //   276: laload
    //   277: bipush #8
    //   279: lushr
    //   280: l2i
    //   281: sipush #255
    //   284: iand
    //   285: laload
    //   286: lxor
    //   287: lastore
    //   288: aload_0
    //   289: getfield _L : [J
    //   292: iload_2
    //   293: dup2
    //   294: laload
    //   295: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C7 : [J
    //   298: aload_0
    //   299: getfield _K : [J
    //   302: iload_2
    //   303: bipush #7
    //   305: isub
    //   306: bipush #7
    //   308: iand
    //   309: laload
    //   310: l2i
    //   311: sipush #255
    //   314: iand
    //   315: laload
    //   316: lxor
    //   317: lastore
    //   318: iinc #2, 1
    //   321: goto -> 50
    //   324: aload_0
    //   325: getfield _L : [J
    //   328: iconst_0
    //   329: aload_0
    //   330: getfield _K : [J
    //   333: iconst_0
    //   334: aload_0
    //   335: getfield _K : [J
    //   338: arraylength
    //   339: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   342: aload_0
    //   343: getfield _K : [J
    //   346: iconst_0
    //   347: dup2
    //   348: laload
    //   349: aload_0
    //   350: getfield _rc : [J
    //   353: iload_1
    //   354: laload
    //   355: lxor
    //   356: lastore
    //   357: iconst_0
    //   358: istore_2
    //   359: iload_2
    //   360: bipush #8
    //   362: if_icmpge -> 638
    //   365: aload_0
    //   366: getfield _L : [J
    //   369: iload_2
    //   370: aload_0
    //   371: getfield _K : [J
    //   374: iload_2
    //   375: laload
    //   376: lastore
    //   377: aload_0
    //   378: getfield _L : [J
    //   381: iload_2
    //   382: dup2
    //   383: laload
    //   384: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C0 : [J
    //   387: aload_0
    //   388: getfield _state : [J
    //   391: iload_2
    //   392: iconst_0
    //   393: isub
    //   394: bipush #7
    //   396: iand
    //   397: laload
    //   398: bipush #56
    //   400: lushr
    //   401: l2i
    //   402: sipush #255
    //   405: iand
    //   406: laload
    //   407: lxor
    //   408: lastore
    //   409: aload_0
    //   410: getfield _L : [J
    //   413: iload_2
    //   414: dup2
    //   415: laload
    //   416: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C1 : [J
    //   419: aload_0
    //   420: getfield _state : [J
    //   423: iload_2
    //   424: iconst_1
    //   425: isub
    //   426: bipush #7
    //   428: iand
    //   429: laload
    //   430: bipush #48
    //   432: lushr
    //   433: l2i
    //   434: sipush #255
    //   437: iand
    //   438: laload
    //   439: lxor
    //   440: lastore
    //   441: aload_0
    //   442: getfield _L : [J
    //   445: iload_2
    //   446: dup2
    //   447: laload
    //   448: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C2 : [J
    //   451: aload_0
    //   452: getfield _state : [J
    //   455: iload_2
    //   456: iconst_2
    //   457: isub
    //   458: bipush #7
    //   460: iand
    //   461: laload
    //   462: bipush #40
    //   464: lushr
    //   465: l2i
    //   466: sipush #255
    //   469: iand
    //   470: laload
    //   471: lxor
    //   472: lastore
    //   473: aload_0
    //   474: getfield _L : [J
    //   477: iload_2
    //   478: dup2
    //   479: laload
    //   480: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C3 : [J
    //   483: aload_0
    //   484: getfield _state : [J
    //   487: iload_2
    //   488: iconst_3
    //   489: isub
    //   490: bipush #7
    //   492: iand
    //   493: laload
    //   494: bipush #32
    //   496: lushr
    //   497: l2i
    //   498: sipush #255
    //   501: iand
    //   502: laload
    //   503: lxor
    //   504: lastore
    //   505: aload_0
    //   506: getfield _L : [J
    //   509: iload_2
    //   510: dup2
    //   511: laload
    //   512: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C4 : [J
    //   515: aload_0
    //   516: getfield _state : [J
    //   519: iload_2
    //   520: iconst_4
    //   521: isub
    //   522: bipush #7
    //   524: iand
    //   525: laload
    //   526: bipush #24
    //   528: lushr
    //   529: l2i
    //   530: sipush #255
    //   533: iand
    //   534: laload
    //   535: lxor
    //   536: lastore
    //   537: aload_0
    //   538: getfield _L : [J
    //   541: iload_2
    //   542: dup2
    //   543: laload
    //   544: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C5 : [J
    //   547: aload_0
    //   548: getfield _state : [J
    //   551: iload_2
    //   552: iconst_5
    //   553: isub
    //   554: bipush #7
    //   556: iand
    //   557: laload
    //   558: bipush #16
    //   560: lushr
    //   561: l2i
    //   562: sipush #255
    //   565: iand
    //   566: laload
    //   567: lxor
    //   568: lastore
    //   569: aload_0
    //   570: getfield _L : [J
    //   573: iload_2
    //   574: dup2
    //   575: laload
    //   576: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C6 : [J
    //   579: aload_0
    //   580: getfield _state : [J
    //   583: iload_2
    //   584: bipush #6
    //   586: isub
    //   587: bipush #7
    //   589: iand
    //   590: laload
    //   591: bipush #8
    //   593: lushr
    //   594: l2i
    //   595: sipush #255
    //   598: iand
    //   599: laload
    //   600: lxor
    //   601: lastore
    //   602: aload_0
    //   603: getfield _L : [J
    //   606: iload_2
    //   607: dup2
    //   608: laload
    //   609: getstatic org/bouncycastle/crypto/digests/WhirlpoolDigest.C7 : [J
    //   612: aload_0
    //   613: getfield _state : [J
    //   616: iload_2
    //   617: bipush #7
    //   619: isub
    //   620: bipush #7
    //   622: iand
    //   623: laload
    //   624: l2i
    //   625: sipush #255
    //   628: iand
    //   629: laload
    //   630: lxor
    //   631: lastore
    //   632: iinc #2, 1
    //   635: goto -> 359
    //   638: aload_0
    //   639: getfield _L : [J
    //   642: iconst_0
    //   643: aload_0
    //   644: getfield _state : [J
    //   647: iconst_0
    //   648: aload_0
    //   649: getfield _state : [J
    //   652: arraylength
    //   653: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   656: iinc #1, 1
    //   659: goto -> 42
    //   662: iconst_0
    //   663: istore_1
    //   664: iload_1
    //   665: bipush #8
    //   667: if_icmpge -> 698
    //   670: aload_0
    //   671: getfield _hash : [J
    //   674: iload_1
    //   675: dup2
    //   676: laload
    //   677: aload_0
    //   678: getfield _state : [J
    //   681: iload_1
    //   682: laload
    //   683: aload_0
    //   684: getfield _block : [J
    //   687: iload_1
    //   688: laload
    //   689: lxor
    //   690: lxor
    //   691: lastore
    //   692: iinc #1, 1
    //   695: goto -> 664
    //   698: return
  }
  
  public void update(byte paramByte) {
    this._buffer[this._bufferPos] = paramByte;
    this._bufferPos++;
    if (this._bufferPos == this._buffer.length)
      processFilledBuffer(this._buffer, 0); 
    increment();
  }
  
  private void increment() {
    int i = 0;
    for (int j = this._bitCount.length - 1; j >= 0; j--) {
      int k = (this._bitCount[j] & 0xFF) + EIGHT[j] + i;
      i = k >>> 8;
      this._bitCount[j] = (short)(k & 0xFF);
    } 
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (paramInt2 > 0) {
      update(paramArrayOfbyte[paramInt1]);
      paramInt1++;
      paramInt2--;
    } 
  }
  
  private void finish() {
    byte[] arrayOfByte = copyBitLength();
    this._buffer[this._bufferPos++] = (byte)(this._buffer[this._bufferPos++] | 0x80);
    if (this._bufferPos == this._buffer.length)
      processFilledBuffer(this._buffer, 0); 
    if (this._bufferPos > 32)
      while (this._bufferPos != 0)
        update((byte)0);  
    while (this._bufferPos <= 32)
      update((byte)0); 
    System.arraycopy(arrayOfByte, 0, this._buffer, 32, arrayOfByte.length);
    processFilledBuffer(this._buffer, 0);
  }
  
  private byte[] copyBitLength() {
    byte[] arrayOfByte = new byte[32];
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = (byte)(this._bitCount[b] & 0xFF); 
    return arrayOfByte;
  }
  
  public int getByteLength() {
    return 64;
  }
  
  public Memoable copy() {
    return new WhirlpoolDigest(this);
  }
  
  public void reset(Memoable paramMemoable) {
    WhirlpoolDigest whirlpoolDigest = (WhirlpoolDigest)paramMemoable;
    System.arraycopy(whirlpoolDigest._rc, 0, this._rc, 0, this._rc.length);
    System.arraycopy(whirlpoolDigest._buffer, 0, this._buffer, 0, this._buffer.length);
    this._bufferPos = whirlpoolDigest._bufferPos;
    System.arraycopy(whirlpoolDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
    System.arraycopy(whirlpoolDigest._hash, 0, this._hash, 0, this._hash.length);
    System.arraycopy(whirlpoolDigest._K, 0, this._K, 0, this._K.length);
    System.arraycopy(whirlpoolDigest._L, 0, this._L, 0, this._L.length);
    System.arraycopy(whirlpoolDigest._block, 0, this._block, 0, this._block.length);
    System.arraycopy(whirlpoolDigest._state, 0, this._state, 0, this._state.length);
  }
  
  static {
    EIGHT[31] = 8;
  }
}
