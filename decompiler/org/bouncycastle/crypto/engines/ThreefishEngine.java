package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.TweakableBlockCipherParameters;

public class ThreefishEngine implements BlockCipher {
  public static final int BLOCKSIZE_256 = 256;
  
  public static final int BLOCKSIZE_512 = 512;
  
  public static final int BLOCKSIZE_1024 = 1024;
  
  private static final int TWEAK_SIZE_BYTES = 16;
  
  private static final int TWEAK_SIZE_WORDS = 2;
  
  private static final int ROUNDS_256 = 72;
  
  private static final int ROUNDS_512 = 72;
  
  private static final int ROUNDS_1024 = 80;
  
  private static final int MAX_ROUNDS = 80;
  
  private static final long C_240 = 2004413935125273122L;
  
  private static int[] MOD9 = new int[80];
  
  private static int[] MOD17 = new int[MOD9.length];
  
  private static int[] MOD5 = new int[MOD9.length];
  
  private static int[] MOD3 = new int[MOD9.length];
  
  private int blocksizeBytes;
  
  private int blocksizeWords;
  
  private long[] currentBlock;
  
  private long[] t = new long[5];
  
  private long[] kw;
  
  private ThreefishCipher cipher;
  
  private boolean forEncryption;
  
  public ThreefishEngine(int paramInt) {
    this.blocksizeBytes = paramInt / 8;
    this.blocksizeWords = this.blocksizeBytes / 8;
    this.currentBlock = new long[this.blocksizeWords];
    this.kw = new long[2 * this.blocksizeWords + 1];
    switch (paramInt) {
      case 256:
        this.cipher = new Threefish256Cipher(this.kw, this.t);
        return;
      case 512:
        this.cipher = new Threefish512Cipher(this.kw, this.t);
        return;
      case 1024:
        this.cipher = new Threefish1024Cipher(this.kw, this.t);
        return;
    } 
    throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) throws IllegalArgumentException {
    byte[] arrayOfByte;
    Object object;
    if (paramCipherParameters instanceof TweakableBlockCipherParameters) {
      TweakableBlockCipherParameters tweakableBlockCipherParameters = (TweakableBlockCipherParameters)paramCipherParameters;
      arrayOfByte = tweakableBlockCipherParameters.getKey().getKey();
      object = tweakableBlockCipherParameters.getTweak();
    } else if (paramCipherParameters instanceof KeyParameter) {
      arrayOfByte = ((KeyParameter)paramCipherParameters).getKey();
      object = null;
    } else {
      throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + paramCipherParameters.getClass().getName());
    } 
    long[] arrayOfLong1 = null;
    long[] arrayOfLong2 = null;
    if (arrayOfByte != null) {
      if (arrayOfByte.length != this.blocksizeBytes)
        throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)"); 
      arrayOfLong1 = new long[this.blocksizeWords];
      for (byte b = 0; b < arrayOfLong1.length; b++)
        arrayOfLong1[b] = bytesToWord(arrayOfByte, b * 8); 
    } 
    if (object != null) {
      if (object.length != 16)
        throw new IllegalArgumentException("Threefish tweak must be 16 bytes"); 
      arrayOfLong2 = new long[] { bytesToWord((byte[])object, 0), bytesToWord((byte[])object, 8) };
    } 
    init(paramBoolean, arrayOfLong1, arrayOfLong2);
  }
  
  public void init(boolean paramBoolean, long[] paramArrayOflong1, long[] paramArrayOflong2) {
    this.forEncryption = paramBoolean;
    if (paramArrayOflong1 != null)
      setKey(paramArrayOflong1); 
    if (paramArrayOflong2 != null)
      setTweak(paramArrayOflong2); 
  }
  
  private void setKey(long[] paramArrayOflong) {
    if (paramArrayOflong.length != this.blocksizeWords)
      throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)"); 
    long l = 2004413935125273122L;
    for (byte b = 0; b < this.blocksizeWords; b++) {
      this.kw[b] = paramArrayOflong[b];
      l ^= this.kw[b];
    } 
    this.kw[this.blocksizeWords] = l;
    System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
  }
  
  private void setTweak(long[] paramArrayOflong) {
    if (paramArrayOflong.length != 2)
      throw new IllegalArgumentException("Tweak must be 2 words."); 
    this.t[0] = paramArrayOflong[0];
    this.t[1] = paramArrayOflong[1];
    this.t[2] = this.t[0] ^ this.t[1];
    this.t[3] = this.t[0];
    this.t[4] = this.t[1];
  }
  
  public String getAlgorithmName() {
    return "Threefish-" + (this.blocksizeBytes * 8);
  }
  
  public int getBlockSize() {
    return this.blocksizeBytes;
  }
  
  public void reset() {}
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws DataLengthException, IllegalStateException {
    if (paramInt1 + this.blocksizeBytes > paramArrayOfbyte1.length)
      throw new DataLengthException("Input buffer too short"); 
    if (paramInt2 + this.blocksizeBytes > paramArrayOfbyte2.length)
      throw new OutputLengthException("Output buffer too short"); 
    byte b;
    for (b = 0; b < this.blocksizeBytes; b += 8)
      this.currentBlock[b >> 3] = bytesToWord(paramArrayOfbyte1, paramInt1 + b); 
    processBlock(this.currentBlock, this.currentBlock);
    for (b = 0; b < this.blocksizeBytes; b += 8)
      wordToBytes(this.currentBlock[b >> 3], paramArrayOfbyte2, paramInt2 + b); 
    return this.blocksizeBytes;
  }
  
  public int processBlock(long[] paramArrayOflong1, long[] paramArrayOflong2) throws DataLengthException, IllegalStateException {
    if (this.kw[this.blocksizeWords] == 0L)
      throw new IllegalStateException("Threefish engine not initialised"); 
    if (paramArrayOflong1.length != this.blocksizeWords)
      throw new DataLengthException("Input buffer too short"); 
    if (paramArrayOflong2.length != this.blocksizeWords)
      throw new OutputLengthException("Output buffer too short"); 
    if (this.forEncryption) {
      this.cipher.encryptBlock(paramArrayOflong1, paramArrayOflong2);
    } else {
      this.cipher.decryptBlock(paramArrayOflong1, paramArrayOflong2);
    } 
    return this.blocksizeWords;
  }
  
  public static long bytesToWord(byte[] paramArrayOfbyte, int paramInt) {
    if (paramInt + 8 > paramArrayOfbyte.length)
      throw new IllegalArgumentException(); 
    long l = 0L;
    int i = paramInt;
    l = paramArrayOfbyte[i++] & 0xFFL;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 8L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 16L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 24L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 32L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 40L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 48L;
    l |= (paramArrayOfbyte[i++] & 0xFFL) << 56L;
    return l;
  }
  
  public static void wordToBytes(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    if (paramInt + 8 > paramArrayOfbyte.length)
      throw new IllegalArgumentException(); 
    int i = paramInt;
    paramArrayOfbyte[i++] = (byte)(int)paramLong;
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 8L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 16L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 24L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 32L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 40L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 48L);
    paramArrayOfbyte[i++] = (byte)(int)(paramLong >> 56L);
  }
  
  static long rotlXor(long paramLong1, int paramInt, long paramLong2) {
    return (paramLong1 << paramInt | paramLong1 >>> -paramInt) ^ paramLong2;
  }
  
  static long xorRotr(long paramLong1, int paramInt, long paramLong2) {
    long l = paramLong1 ^ paramLong2;
    return l >>> paramInt | l << -paramInt;
  }
  
  static {
    for (byte b = 0; b < MOD9.length; b++) {
      MOD17[b] = b % 17;
      MOD9[b] = b % 9;
      MOD5[b] = b % 5;
      MOD3[b] = b % 3;
    } 
  }
  
  private static final class Threefish1024Cipher extends ThreefishCipher {
    private static final int ROTATION_0_0 = 24;
    
    private static final int ROTATION_0_1 = 13;
    
    private static final int ROTATION_0_2 = 8;
    
    private static final int ROTATION_0_3 = 47;
    
    private static final int ROTATION_0_4 = 8;
    
    private static final int ROTATION_0_5 = 17;
    
    private static final int ROTATION_0_6 = 22;
    
    private static final int ROTATION_0_7 = 37;
    
    private static final int ROTATION_1_0 = 38;
    
    private static final int ROTATION_1_1 = 19;
    
    private static final int ROTATION_1_2 = 10;
    
    private static final int ROTATION_1_3 = 55;
    
    private static final int ROTATION_1_4 = 49;
    
    private static final int ROTATION_1_5 = 18;
    
    private static final int ROTATION_1_6 = 23;
    
    private static final int ROTATION_1_7 = 52;
    
    private static final int ROTATION_2_0 = 33;
    
    private static final int ROTATION_2_1 = 4;
    
    private static final int ROTATION_2_2 = 51;
    
    private static final int ROTATION_2_3 = 13;
    
    private static final int ROTATION_2_4 = 34;
    
    private static final int ROTATION_2_5 = 41;
    
    private static final int ROTATION_2_6 = 59;
    
    private static final int ROTATION_2_7 = 17;
    
    private static final int ROTATION_3_0 = 5;
    
    private static final int ROTATION_3_1 = 20;
    
    private static final int ROTATION_3_2 = 48;
    
    private static final int ROTATION_3_3 = 41;
    
    private static final int ROTATION_3_4 = 47;
    
    private static final int ROTATION_3_5 = 28;
    
    private static final int ROTATION_3_6 = 16;
    
    private static final int ROTATION_3_7 = 25;
    
    private static final int ROTATION_4_0 = 41;
    
    private static final int ROTATION_4_1 = 9;
    
    private static final int ROTATION_4_2 = 37;
    
    private static final int ROTATION_4_3 = 31;
    
    private static final int ROTATION_4_4 = 12;
    
    private static final int ROTATION_4_5 = 47;
    
    private static final int ROTATION_4_6 = 44;
    
    private static final int ROTATION_4_7 = 30;
    
    private static final int ROTATION_5_0 = 16;
    
    private static final int ROTATION_5_1 = 34;
    
    private static final int ROTATION_5_2 = 56;
    
    private static final int ROTATION_5_3 = 51;
    
    private static final int ROTATION_5_4 = 4;
    
    private static final int ROTATION_5_5 = 53;
    
    private static final int ROTATION_5_6 = 42;
    
    private static final int ROTATION_5_7 = 41;
    
    private static final int ROTATION_6_0 = 31;
    
    private static final int ROTATION_6_1 = 44;
    
    private static final int ROTATION_6_2 = 47;
    
    private static final int ROTATION_6_3 = 46;
    
    private static final int ROTATION_6_4 = 19;
    
    private static final int ROTATION_6_5 = 42;
    
    private static final int ROTATION_6_6 = 44;
    
    private static final int ROTATION_6_7 = 25;
    
    private static final int ROTATION_7_0 = 9;
    
    private static final int ROTATION_7_1 = 48;
    
    private static final int ROTATION_7_2 = 35;
    
    private static final int ROTATION_7_3 = 52;
    
    private static final int ROTATION_7_4 = 23;
    
    private static final int ROTATION_7_5 = 31;
    
    private static final int ROTATION_7_6 = 37;
    
    private static final int ROTATION_7_7 = 20;
    
    public Threefish1024Cipher(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      super(param1ArrayOflong1, param1ArrayOflong2);
    }
    
    void encryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      // Byte code:
      //   0: aload_0
      //   1: getfield kw : [J
      //   4: astore_3
      //   5: aload_0
      //   6: getfield t : [J
      //   9: astore #4
      //   11: invokestatic access$300 : ()[I
      //   14: astore #5
      //   16: invokestatic access$100 : ()[I
      //   19: astore #6
      //   21: aload_3
      //   22: arraylength
      //   23: bipush #33
      //   25: if_icmpeq -> 36
      //   28: new java/lang/IllegalArgumentException
      //   31: dup
      //   32: invokespecial <init> : ()V
      //   35: athrow
      //   36: aload #4
      //   38: arraylength
      //   39: iconst_5
      //   40: if_icmpeq -> 51
      //   43: new java/lang/IllegalArgumentException
      //   46: dup
      //   47: invokespecial <init> : ()V
      //   50: athrow
      //   51: aload_1
      //   52: iconst_0
      //   53: laload
      //   54: lstore #7
      //   56: aload_1
      //   57: iconst_1
      //   58: laload
      //   59: lstore #9
      //   61: aload_1
      //   62: iconst_2
      //   63: laload
      //   64: lstore #11
      //   66: aload_1
      //   67: iconst_3
      //   68: laload
      //   69: lstore #13
      //   71: aload_1
      //   72: iconst_4
      //   73: laload
      //   74: lstore #15
      //   76: aload_1
      //   77: iconst_5
      //   78: laload
      //   79: lstore #17
      //   81: aload_1
      //   82: bipush #6
      //   84: laload
      //   85: lstore #19
      //   87: aload_1
      //   88: bipush #7
      //   90: laload
      //   91: lstore #21
      //   93: aload_1
      //   94: bipush #8
      //   96: laload
      //   97: lstore #23
      //   99: aload_1
      //   100: bipush #9
      //   102: laload
      //   103: lstore #25
      //   105: aload_1
      //   106: bipush #10
      //   108: laload
      //   109: lstore #27
      //   111: aload_1
      //   112: bipush #11
      //   114: laload
      //   115: lstore #29
      //   117: aload_1
      //   118: bipush #12
      //   120: laload
      //   121: lstore #31
      //   123: aload_1
      //   124: bipush #13
      //   126: laload
      //   127: lstore #33
      //   129: aload_1
      //   130: bipush #14
      //   132: laload
      //   133: lstore #35
      //   135: aload_1
      //   136: bipush #15
      //   138: laload
      //   139: lstore #37
      //   141: lload #7
      //   143: aload_3
      //   144: iconst_0
      //   145: laload
      //   146: ladd
      //   147: lstore #7
      //   149: lload #9
      //   151: aload_3
      //   152: iconst_1
      //   153: laload
      //   154: ladd
      //   155: lstore #9
      //   157: lload #11
      //   159: aload_3
      //   160: iconst_2
      //   161: laload
      //   162: ladd
      //   163: lstore #11
      //   165: lload #13
      //   167: aload_3
      //   168: iconst_3
      //   169: laload
      //   170: ladd
      //   171: lstore #13
      //   173: lload #15
      //   175: aload_3
      //   176: iconst_4
      //   177: laload
      //   178: ladd
      //   179: lstore #15
      //   181: lload #17
      //   183: aload_3
      //   184: iconst_5
      //   185: laload
      //   186: ladd
      //   187: lstore #17
      //   189: lload #19
      //   191: aload_3
      //   192: bipush #6
      //   194: laload
      //   195: ladd
      //   196: lstore #19
      //   198: lload #21
      //   200: aload_3
      //   201: bipush #7
      //   203: laload
      //   204: ladd
      //   205: lstore #21
      //   207: lload #23
      //   209: aload_3
      //   210: bipush #8
      //   212: laload
      //   213: ladd
      //   214: lstore #23
      //   216: lload #25
      //   218: aload_3
      //   219: bipush #9
      //   221: laload
      //   222: ladd
      //   223: lstore #25
      //   225: lload #27
      //   227: aload_3
      //   228: bipush #10
      //   230: laload
      //   231: ladd
      //   232: lstore #27
      //   234: lload #29
      //   236: aload_3
      //   237: bipush #11
      //   239: laload
      //   240: ladd
      //   241: lstore #29
      //   243: lload #31
      //   245: aload_3
      //   246: bipush #12
      //   248: laload
      //   249: ladd
      //   250: lstore #31
      //   252: lload #33
      //   254: aload_3
      //   255: bipush #13
      //   257: laload
      //   258: aload #4
      //   260: iconst_0
      //   261: laload
      //   262: ladd
      //   263: ladd
      //   264: lstore #33
      //   266: lload #35
      //   268: aload_3
      //   269: bipush #14
      //   271: laload
      //   272: aload #4
      //   274: iconst_1
      //   275: laload
      //   276: ladd
      //   277: ladd
      //   278: lstore #35
      //   280: lload #37
      //   282: aload_3
      //   283: bipush #15
      //   285: laload
      //   286: ladd
      //   287: lstore #37
      //   289: iconst_1
      //   290: istore #39
      //   292: iload #39
      //   294: bipush #20
      //   296: if_icmpge -> 1815
      //   299: aload #5
      //   301: iload #39
      //   303: iaload
      //   304: istore #40
      //   306: aload #6
      //   308: iload #39
      //   310: iaload
      //   311: istore #41
      //   313: lload #9
      //   315: bipush #24
      //   317: lload #7
      //   319: lload #9
      //   321: ladd
      //   322: dup2
      //   323: lstore #7
      //   325: invokestatic rotlXor : (JIJ)J
      //   328: lstore #9
      //   330: lload #13
      //   332: bipush #13
      //   334: lload #11
      //   336: lload #13
      //   338: ladd
      //   339: dup2
      //   340: lstore #11
      //   342: invokestatic rotlXor : (JIJ)J
      //   345: lstore #13
      //   347: lload #17
      //   349: bipush #8
      //   351: lload #15
      //   353: lload #17
      //   355: ladd
      //   356: dup2
      //   357: lstore #15
      //   359: invokestatic rotlXor : (JIJ)J
      //   362: lstore #17
      //   364: lload #21
      //   366: bipush #47
      //   368: lload #19
      //   370: lload #21
      //   372: ladd
      //   373: dup2
      //   374: lstore #19
      //   376: invokestatic rotlXor : (JIJ)J
      //   379: lstore #21
      //   381: lload #25
      //   383: bipush #8
      //   385: lload #23
      //   387: lload #25
      //   389: ladd
      //   390: dup2
      //   391: lstore #23
      //   393: invokestatic rotlXor : (JIJ)J
      //   396: lstore #25
      //   398: lload #29
      //   400: bipush #17
      //   402: lload #27
      //   404: lload #29
      //   406: ladd
      //   407: dup2
      //   408: lstore #27
      //   410: invokestatic rotlXor : (JIJ)J
      //   413: lstore #29
      //   415: lload #33
      //   417: bipush #22
      //   419: lload #31
      //   421: lload #33
      //   423: ladd
      //   424: dup2
      //   425: lstore #31
      //   427: invokestatic rotlXor : (JIJ)J
      //   430: lstore #33
      //   432: lload #37
      //   434: bipush #37
      //   436: lload #35
      //   438: lload #37
      //   440: ladd
      //   441: dup2
      //   442: lstore #35
      //   444: invokestatic rotlXor : (JIJ)J
      //   447: lstore #37
      //   449: lload #25
      //   451: bipush #38
      //   453: lload #7
      //   455: lload #25
      //   457: ladd
      //   458: dup2
      //   459: lstore #7
      //   461: invokestatic rotlXor : (JIJ)J
      //   464: lstore #25
      //   466: lload #33
      //   468: bipush #19
      //   470: lload #11
      //   472: lload #33
      //   474: ladd
      //   475: dup2
      //   476: lstore #11
      //   478: invokestatic rotlXor : (JIJ)J
      //   481: lstore #33
      //   483: lload #29
      //   485: bipush #10
      //   487: lload #19
      //   489: lload #29
      //   491: ladd
      //   492: dup2
      //   493: lstore #19
      //   495: invokestatic rotlXor : (JIJ)J
      //   498: lstore #29
      //   500: lload #37
      //   502: bipush #55
      //   504: lload #15
      //   506: lload #37
      //   508: ladd
      //   509: dup2
      //   510: lstore #15
      //   512: invokestatic rotlXor : (JIJ)J
      //   515: lstore #37
      //   517: lload #21
      //   519: bipush #49
      //   521: lload #27
      //   523: lload #21
      //   525: ladd
      //   526: dup2
      //   527: lstore #27
      //   529: invokestatic rotlXor : (JIJ)J
      //   532: lstore #21
      //   534: lload #13
      //   536: bipush #18
      //   538: lload #31
      //   540: lload #13
      //   542: ladd
      //   543: dup2
      //   544: lstore #31
      //   546: invokestatic rotlXor : (JIJ)J
      //   549: lstore #13
      //   551: lload #17
      //   553: bipush #23
      //   555: lload #35
      //   557: lload #17
      //   559: ladd
      //   560: dup2
      //   561: lstore #35
      //   563: invokestatic rotlXor : (JIJ)J
      //   566: lstore #17
      //   568: lload #9
      //   570: bipush #52
      //   572: lload #23
      //   574: lload #9
      //   576: ladd
      //   577: dup2
      //   578: lstore #23
      //   580: invokestatic rotlXor : (JIJ)J
      //   583: lstore #9
      //   585: lload #21
      //   587: bipush #33
      //   589: lload #7
      //   591: lload #21
      //   593: ladd
      //   594: dup2
      //   595: lstore #7
      //   597: invokestatic rotlXor : (JIJ)J
      //   600: lstore #21
      //   602: lload #17
      //   604: iconst_4
      //   605: lload #11
      //   607: lload #17
      //   609: ladd
      //   610: dup2
      //   611: lstore #11
      //   613: invokestatic rotlXor : (JIJ)J
      //   616: lstore #17
      //   618: lload #13
      //   620: bipush #51
      //   622: lload #15
      //   624: lload #13
      //   626: ladd
      //   627: dup2
      //   628: lstore #15
      //   630: invokestatic rotlXor : (JIJ)J
      //   633: lstore #13
      //   635: lload #9
      //   637: bipush #13
      //   639: lload #19
      //   641: lload #9
      //   643: ladd
      //   644: dup2
      //   645: lstore #19
      //   647: invokestatic rotlXor : (JIJ)J
      //   650: lstore #9
      //   652: lload #37
      //   654: bipush #34
      //   656: lload #31
      //   658: lload #37
      //   660: ladd
      //   661: dup2
      //   662: lstore #31
      //   664: invokestatic rotlXor : (JIJ)J
      //   667: lstore #37
      //   669: lload #33
      //   671: bipush #41
      //   673: lload #35
      //   675: lload #33
      //   677: ladd
      //   678: dup2
      //   679: lstore #35
      //   681: invokestatic rotlXor : (JIJ)J
      //   684: lstore #33
      //   686: lload #29
      //   688: bipush #59
      //   690: lload #23
      //   692: lload #29
      //   694: ladd
      //   695: dup2
      //   696: lstore #23
      //   698: invokestatic rotlXor : (JIJ)J
      //   701: lstore #29
      //   703: lload #25
      //   705: bipush #17
      //   707: lload #27
      //   709: lload #25
      //   711: ladd
      //   712: dup2
      //   713: lstore #27
      //   715: invokestatic rotlXor : (JIJ)J
      //   718: lstore #25
      //   720: lload #37
      //   722: iconst_5
      //   723: lload #7
      //   725: lload #37
      //   727: ladd
      //   728: dup2
      //   729: lstore #7
      //   731: invokestatic rotlXor : (JIJ)J
      //   734: lstore #37
      //   736: lload #29
      //   738: bipush #20
      //   740: lload #11
      //   742: lload #29
      //   744: ladd
      //   745: dup2
      //   746: lstore #11
      //   748: invokestatic rotlXor : (JIJ)J
      //   751: lstore #29
      //   753: lload #33
      //   755: bipush #48
      //   757: lload #19
      //   759: lload #33
      //   761: ladd
      //   762: dup2
      //   763: lstore #19
      //   765: invokestatic rotlXor : (JIJ)J
      //   768: lstore #33
      //   770: lload #25
      //   772: bipush #41
      //   774: lload #15
      //   776: lload #25
      //   778: ladd
      //   779: dup2
      //   780: lstore #15
      //   782: invokestatic rotlXor : (JIJ)J
      //   785: lstore #25
      //   787: lload #9
      //   789: bipush #47
      //   791: lload #35
      //   793: lload #9
      //   795: ladd
      //   796: dup2
      //   797: lstore #35
      //   799: invokestatic rotlXor : (JIJ)J
      //   802: lstore #9
      //   804: lload #17
      //   806: bipush #28
      //   808: lload #23
      //   810: lload #17
      //   812: ladd
      //   813: dup2
      //   814: lstore #23
      //   816: invokestatic rotlXor : (JIJ)J
      //   819: lstore #17
      //   821: lload #13
      //   823: bipush #16
      //   825: lload #27
      //   827: lload #13
      //   829: ladd
      //   830: dup2
      //   831: lstore #27
      //   833: invokestatic rotlXor : (JIJ)J
      //   836: lstore #13
      //   838: lload #21
      //   840: bipush #25
      //   842: lload #31
      //   844: lload #21
      //   846: ladd
      //   847: dup2
      //   848: lstore #31
      //   850: invokestatic rotlXor : (JIJ)J
      //   853: lstore #21
      //   855: lload #7
      //   857: aload_3
      //   858: iload #40
      //   860: laload
      //   861: ladd
      //   862: lstore #7
      //   864: lload #9
      //   866: aload_3
      //   867: iload #40
      //   869: iconst_1
      //   870: iadd
      //   871: laload
      //   872: ladd
      //   873: lstore #9
      //   875: lload #11
      //   877: aload_3
      //   878: iload #40
      //   880: iconst_2
      //   881: iadd
      //   882: laload
      //   883: ladd
      //   884: lstore #11
      //   886: lload #13
      //   888: aload_3
      //   889: iload #40
      //   891: iconst_3
      //   892: iadd
      //   893: laload
      //   894: ladd
      //   895: lstore #13
      //   897: lload #15
      //   899: aload_3
      //   900: iload #40
      //   902: iconst_4
      //   903: iadd
      //   904: laload
      //   905: ladd
      //   906: lstore #15
      //   908: lload #17
      //   910: aload_3
      //   911: iload #40
      //   913: iconst_5
      //   914: iadd
      //   915: laload
      //   916: ladd
      //   917: lstore #17
      //   919: lload #19
      //   921: aload_3
      //   922: iload #40
      //   924: bipush #6
      //   926: iadd
      //   927: laload
      //   928: ladd
      //   929: lstore #19
      //   931: lload #21
      //   933: aload_3
      //   934: iload #40
      //   936: bipush #7
      //   938: iadd
      //   939: laload
      //   940: ladd
      //   941: lstore #21
      //   943: lload #23
      //   945: aload_3
      //   946: iload #40
      //   948: bipush #8
      //   950: iadd
      //   951: laload
      //   952: ladd
      //   953: lstore #23
      //   955: lload #25
      //   957: aload_3
      //   958: iload #40
      //   960: bipush #9
      //   962: iadd
      //   963: laload
      //   964: ladd
      //   965: lstore #25
      //   967: lload #27
      //   969: aload_3
      //   970: iload #40
      //   972: bipush #10
      //   974: iadd
      //   975: laload
      //   976: ladd
      //   977: lstore #27
      //   979: lload #29
      //   981: aload_3
      //   982: iload #40
      //   984: bipush #11
      //   986: iadd
      //   987: laload
      //   988: ladd
      //   989: lstore #29
      //   991: lload #31
      //   993: aload_3
      //   994: iload #40
      //   996: bipush #12
      //   998: iadd
      //   999: laload
      //   1000: ladd
      //   1001: lstore #31
      //   1003: lload #33
      //   1005: aload_3
      //   1006: iload #40
      //   1008: bipush #13
      //   1010: iadd
      //   1011: laload
      //   1012: aload #4
      //   1014: iload #41
      //   1016: laload
      //   1017: ladd
      //   1018: ladd
      //   1019: lstore #33
      //   1021: lload #35
      //   1023: aload_3
      //   1024: iload #40
      //   1026: bipush #14
      //   1028: iadd
      //   1029: laload
      //   1030: aload #4
      //   1032: iload #41
      //   1034: iconst_1
      //   1035: iadd
      //   1036: laload
      //   1037: ladd
      //   1038: ladd
      //   1039: lstore #35
      //   1041: lload #37
      //   1043: aload_3
      //   1044: iload #40
      //   1046: bipush #15
      //   1048: iadd
      //   1049: laload
      //   1050: iload #39
      //   1052: i2l
      //   1053: ladd
      //   1054: ladd
      //   1055: lstore #37
      //   1057: lload #9
      //   1059: bipush #41
      //   1061: lload #7
      //   1063: lload #9
      //   1065: ladd
      //   1066: dup2
      //   1067: lstore #7
      //   1069: invokestatic rotlXor : (JIJ)J
      //   1072: lstore #9
      //   1074: lload #13
      //   1076: bipush #9
      //   1078: lload #11
      //   1080: lload #13
      //   1082: ladd
      //   1083: dup2
      //   1084: lstore #11
      //   1086: invokestatic rotlXor : (JIJ)J
      //   1089: lstore #13
      //   1091: lload #17
      //   1093: bipush #37
      //   1095: lload #15
      //   1097: lload #17
      //   1099: ladd
      //   1100: dup2
      //   1101: lstore #15
      //   1103: invokestatic rotlXor : (JIJ)J
      //   1106: lstore #17
      //   1108: lload #21
      //   1110: bipush #31
      //   1112: lload #19
      //   1114: lload #21
      //   1116: ladd
      //   1117: dup2
      //   1118: lstore #19
      //   1120: invokestatic rotlXor : (JIJ)J
      //   1123: lstore #21
      //   1125: lload #25
      //   1127: bipush #12
      //   1129: lload #23
      //   1131: lload #25
      //   1133: ladd
      //   1134: dup2
      //   1135: lstore #23
      //   1137: invokestatic rotlXor : (JIJ)J
      //   1140: lstore #25
      //   1142: lload #29
      //   1144: bipush #47
      //   1146: lload #27
      //   1148: lload #29
      //   1150: ladd
      //   1151: dup2
      //   1152: lstore #27
      //   1154: invokestatic rotlXor : (JIJ)J
      //   1157: lstore #29
      //   1159: lload #33
      //   1161: bipush #44
      //   1163: lload #31
      //   1165: lload #33
      //   1167: ladd
      //   1168: dup2
      //   1169: lstore #31
      //   1171: invokestatic rotlXor : (JIJ)J
      //   1174: lstore #33
      //   1176: lload #37
      //   1178: bipush #30
      //   1180: lload #35
      //   1182: lload #37
      //   1184: ladd
      //   1185: dup2
      //   1186: lstore #35
      //   1188: invokestatic rotlXor : (JIJ)J
      //   1191: lstore #37
      //   1193: lload #25
      //   1195: bipush #16
      //   1197: lload #7
      //   1199: lload #25
      //   1201: ladd
      //   1202: dup2
      //   1203: lstore #7
      //   1205: invokestatic rotlXor : (JIJ)J
      //   1208: lstore #25
      //   1210: lload #33
      //   1212: bipush #34
      //   1214: lload #11
      //   1216: lload #33
      //   1218: ladd
      //   1219: dup2
      //   1220: lstore #11
      //   1222: invokestatic rotlXor : (JIJ)J
      //   1225: lstore #33
      //   1227: lload #29
      //   1229: bipush #56
      //   1231: lload #19
      //   1233: lload #29
      //   1235: ladd
      //   1236: dup2
      //   1237: lstore #19
      //   1239: invokestatic rotlXor : (JIJ)J
      //   1242: lstore #29
      //   1244: lload #37
      //   1246: bipush #51
      //   1248: lload #15
      //   1250: lload #37
      //   1252: ladd
      //   1253: dup2
      //   1254: lstore #15
      //   1256: invokestatic rotlXor : (JIJ)J
      //   1259: lstore #37
      //   1261: lload #21
      //   1263: iconst_4
      //   1264: lload #27
      //   1266: lload #21
      //   1268: ladd
      //   1269: dup2
      //   1270: lstore #27
      //   1272: invokestatic rotlXor : (JIJ)J
      //   1275: lstore #21
      //   1277: lload #13
      //   1279: bipush #53
      //   1281: lload #31
      //   1283: lload #13
      //   1285: ladd
      //   1286: dup2
      //   1287: lstore #31
      //   1289: invokestatic rotlXor : (JIJ)J
      //   1292: lstore #13
      //   1294: lload #17
      //   1296: bipush #42
      //   1298: lload #35
      //   1300: lload #17
      //   1302: ladd
      //   1303: dup2
      //   1304: lstore #35
      //   1306: invokestatic rotlXor : (JIJ)J
      //   1309: lstore #17
      //   1311: lload #9
      //   1313: bipush #41
      //   1315: lload #23
      //   1317: lload #9
      //   1319: ladd
      //   1320: dup2
      //   1321: lstore #23
      //   1323: invokestatic rotlXor : (JIJ)J
      //   1326: lstore #9
      //   1328: lload #21
      //   1330: bipush #31
      //   1332: lload #7
      //   1334: lload #21
      //   1336: ladd
      //   1337: dup2
      //   1338: lstore #7
      //   1340: invokestatic rotlXor : (JIJ)J
      //   1343: lstore #21
      //   1345: lload #17
      //   1347: bipush #44
      //   1349: lload #11
      //   1351: lload #17
      //   1353: ladd
      //   1354: dup2
      //   1355: lstore #11
      //   1357: invokestatic rotlXor : (JIJ)J
      //   1360: lstore #17
      //   1362: lload #13
      //   1364: bipush #47
      //   1366: lload #15
      //   1368: lload #13
      //   1370: ladd
      //   1371: dup2
      //   1372: lstore #15
      //   1374: invokestatic rotlXor : (JIJ)J
      //   1377: lstore #13
      //   1379: lload #9
      //   1381: bipush #46
      //   1383: lload #19
      //   1385: lload #9
      //   1387: ladd
      //   1388: dup2
      //   1389: lstore #19
      //   1391: invokestatic rotlXor : (JIJ)J
      //   1394: lstore #9
      //   1396: lload #37
      //   1398: bipush #19
      //   1400: lload #31
      //   1402: lload #37
      //   1404: ladd
      //   1405: dup2
      //   1406: lstore #31
      //   1408: invokestatic rotlXor : (JIJ)J
      //   1411: lstore #37
      //   1413: lload #33
      //   1415: bipush #42
      //   1417: lload #35
      //   1419: lload #33
      //   1421: ladd
      //   1422: dup2
      //   1423: lstore #35
      //   1425: invokestatic rotlXor : (JIJ)J
      //   1428: lstore #33
      //   1430: lload #29
      //   1432: bipush #44
      //   1434: lload #23
      //   1436: lload #29
      //   1438: ladd
      //   1439: dup2
      //   1440: lstore #23
      //   1442: invokestatic rotlXor : (JIJ)J
      //   1445: lstore #29
      //   1447: lload #25
      //   1449: bipush #25
      //   1451: lload #27
      //   1453: lload #25
      //   1455: ladd
      //   1456: dup2
      //   1457: lstore #27
      //   1459: invokestatic rotlXor : (JIJ)J
      //   1462: lstore #25
      //   1464: lload #37
      //   1466: bipush #9
      //   1468: lload #7
      //   1470: lload #37
      //   1472: ladd
      //   1473: dup2
      //   1474: lstore #7
      //   1476: invokestatic rotlXor : (JIJ)J
      //   1479: lstore #37
      //   1481: lload #29
      //   1483: bipush #48
      //   1485: lload #11
      //   1487: lload #29
      //   1489: ladd
      //   1490: dup2
      //   1491: lstore #11
      //   1493: invokestatic rotlXor : (JIJ)J
      //   1496: lstore #29
      //   1498: lload #33
      //   1500: bipush #35
      //   1502: lload #19
      //   1504: lload #33
      //   1506: ladd
      //   1507: dup2
      //   1508: lstore #19
      //   1510: invokestatic rotlXor : (JIJ)J
      //   1513: lstore #33
      //   1515: lload #25
      //   1517: bipush #52
      //   1519: lload #15
      //   1521: lload #25
      //   1523: ladd
      //   1524: dup2
      //   1525: lstore #15
      //   1527: invokestatic rotlXor : (JIJ)J
      //   1530: lstore #25
      //   1532: lload #9
      //   1534: bipush #23
      //   1536: lload #35
      //   1538: lload #9
      //   1540: ladd
      //   1541: dup2
      //   1542: lstore #35
      //   1544: invokestatic rotlXor : (JIJ)J
      //   1547: lstore #9
      //   1549: lload #17
      //   1551: bipush #31
      //   1553: lload #23
      //   1555: lload #17
      //   1557: ladd
      //   1558: dup2
      //   1559: lstore #23
      //   1561: invokestatic rotlXor : (JIJ)J
      //   1564: lstore #17
      //   1566: lload #13
      //   1568: bipush #37
      //   1570: lload #27
      //   1572: lload #13
      //   1574: ladd
      //   1575: dup2
      //   1576: lstore #27
      //   1578: invokestatic rotlXor : (JIJ)J
      //   1581: lstore #13
      //   1583: lload #21
      //   1585: bipush #20
      //   1587: lload #31
      //   1589: lload #21
      //   1591: ladd
      //   1592: dup2
      //   1593: lstore #31
      //   1595: invokestatic rotlXor : (JIJ)J
      //   1598: lstore #21
      //   1600: lload #7
      //   1602: aload_3
      //   1603: iload #40
      //   1605: iconst_1
      //   1606: iadd
      //   1607: laload
      //   1608: ladd
      //   1609: lstore #7
      //   1611: lload #9
      //   1613: aload_3
      //   1614: iload #40
      //   1616: iconst_2
      //   1617: iadd
      //   1618: laload
      //   1619: ladd
      //   1620: lstore #9
      //   1622: lload #11
      //   1624: aload_3
      //   1625: iload #40
      //   1627: iconst_3
      //   1628: iadd
      //   1629: laload
      //   1630: ladd
      //   1631: lstore #11
      //   1633: lload #13
      //   1635: aload_3
      //   1636: iload #40
      //   1638: iconst_4
      //   1639: iadd
      //   1640: laload
      //   1641: ladd
      //   1642: lstore #13
      //   1644: lload #15
      //   1646: aload_3
      //   1647: iload #40
      //   1649: iconst_5
      //   1650: iadd
      //   1651: laload
      //   1652: ladd
      //   1653: lstore #15
      //   1655: lload #17
      //   1657: aload_3
      //   1658: iload #40
      //   1660: bipush #6
      //   1662: iadd
      //   1663: laload
      //   1664: ladd
      //   1665: lstore #17
      //   1667: lload #19
      //   1669: aload_3
      //   1670: iload #40
      //   1672: bipush #7
      //   1674: iadd
      //   1675: laload
      //   1676: ladd
      //   1677: lstore #19
      //   1679: lload #21
      //   1681: aload_3
      //   1682: iload #40
      //   1684: bipush #8
      //   1686: iadd
      //   1687: laload
      //   1688: ladd
      //   1689: lstore #21
      //   1691: lload #23
      //   1693: aload_3
      //   1694: iload #40
      //   1696: bipush #9
      //   1698: iadd
      //   1699: laload
      //   1700: ladd
      //   1701: lstore #23
      //   1703: lload #25
      //   1705: aload_3
      //   1706: iload #40
      //   1708: bipush #10
      //   1710: iadd
      //   1711: laload
      //   1712: ladd
      //   1713: lstore #25
      //   1715: lload #27
      //   1717: aload_3
      //   1718: iload #40
      //   1720: bipush #11
      //   1722: iadd
      //   1723: laload
      //   1724: ladd
      //   1725: lstore #27
      //   1727: lload #29
      //   1729: aload_3
      //   1730: iload #40
      //   1732: bipush #12
      //   1734: iadd
      //   1735: laload
      //   1736: ladd
      //   1737: lstore #29
      //   1739: lload #31
      //   1741: aload_3
      //   1742: iload #40
      //   1744: bipush #13
      //   1746: iadd
      //   1747: laload
      //   1748: ladd
      //   1749: lstore #31
      //   1751: lload #33
      //   1753: aload_3
      //   1754: iload #40
      //   1756: bipush #14
      //   1758: iadd
      //   1759: laload
      //   1760: aload #4
      //   1762: iload #41
      //   1764: iconst_1
      //   1765: iadd
      //   1766: laload
      //   1767: ladd
      //   1768: ladd
      //   1769: lstore #33
      //   1771: lload #35
      //   1773: aload_3
      //   1774: iload #40
      //   1776: bipush #15
      //   1778: iadd
      //   1779: laload
      //   1780: aload #4
      //   1782: iload #41
      //   1784: iconst_2
      //   1785: iadd
      //   1786: laload
      //   1787: ladd
      //   1788: ladd
      //   1789: lstore #35
      //   1791: lload #37
      //   1793: aload_3
      //   1794: iload #40
      //   1796: bipush #16
      //   1798: iadd
      //   1799: laload
      //   1800: iload #39
      //   1802: i2l
      //   1803: ladd
      //   1804: lconst_1
      //   1805: ladd
      //   1806: ladd
      //   1807: lstore #37
      //   1809: iinc #39, 2
      //   1812: goto -> 292
      //   1815: aload_2
      //   1816: iconst_0
      //   1817: lload #7
      //   1819: lastore
      //   1820: aload_2
      //   1821: iconst_1
      //   1822: lload #9
      //   1824: lastore
      //   1825: aload_2
      //   1826: iconst_2
      //   1827: lload #11
      //   1829: lastore
      //   1830: aload_2
      //   1831: iconst_3
      //   1832: lload #13
      //   1834: lastore
      //   1835: aload_2
      //   1836: iconst_4
      //   1837: lload #15
      //   1839: lastore
      //   1840: aload_2
      //   1841: iconst_5
      //   1842: lload #17
      //   1844: lastore
      //   1845: aload_2
      //   1846: bipush #6
      //   1848: lload #19
      //   1850: lastore
      //   1851: aload_2
      //   1852: bipush #7
      //   1854: lload #21
      //   1856: lastore
      //   1857: aload_2
      //   1858: bipush #8
      //   1860: lload #23
      //   1862: lastore
      //   1863: aload_2
      //   1864: bipush #9
      //   1866: lload #25
      //   1868: lastore
      //   1869: aload_2
      //   1870: bipush #10
      //   1872: lload #27
      //   1874: lastore
      //   1875: aload_2
      //   1876: bipush #11
      //   1878: lload #29
      //   1880: lastore
      //   1881: aload_2
      //   1882: bipush #12
      //   1884: lload #31
      //   1886: lastore
      //   1887: aload_2
      //   1888: bipush #13
      //   1890: lload #33
      //   1892: lastore
      //   1893: aload_2
      //   1894: bipush #14
      //   1896: lload #35
      //   1898: lastore
      //   1899: aload_2
      //   1900: bipush #15
      //   1902: lload #37
      //   1904: lastore
      //   1905: return
    }
    
    void decryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      long[] arrayOfLong1 = this.kw;
      long[] arrayOfLong2 = this.t;
      int[] arrayOfInt1 = ThreefishEngine.MOD17;
      int[] arrayOfInt2 = ThreefishEngine.MOD3;
      if (arrayOfLong1.length != 33)
        throw new IllegalArgumentException(); 
      if (arrayOfLong2.length != 5)
        throw new IllegalArgumentException(); 
      long l1 = param1ArrayOflong1[0];
      long l2 = param1ArrayOflong1[1];
      long l3 = param1ArrayOflong1[2];
      long l4 = param1ArrayOflong1[3];
      long l5 = param1ArrayOflong1[4];
      long l6 = param1ArrayOflong1[5];
      long l7 = param1ArrayOflong1[6];
      long l8 = param1ArrayOflong1[7];
      long l9 = param1ArrayOflong1[8];
      long l10 = param1ArrayOflong1[9];
      long l11 = param1ArrayOflong1[10];
      long l12 = param1ArrayOflong1[11];
      long l13 = param1ArrayOflong1[12];
      long l14 = param1ArrayOflong1[13];
      long l15 = param1ArrayOflong1[14];
      long l16 = param1ArrayOflong1[15];
      for (byte b = 19; b >= 1; b -= 2) {
        int i = arrayOfInt1[b];
        int j = arrayOfInt2[b];
        l1 -= arrayOfLong1[i + 1];
        l2 -= arrayOfLong1[i + 2];
        l3 -= arrayOfLong1[i + 3];
        l4 -= arrayOfLong1[i + 4];
        l5 -= arrayOfLong1[i + 5];
        l6 -= arrayOfLong1[i + 6];
        l7 -= arrayOfLong1[i + 7];
        l8 -= arrayOfLong1[i + 8];
        l9 -= arrayOfLong1[i + 9];
        l10 -= arrayOfLong1[i + 10];
        l11 -= arrayOfLong1[i + 11];
        l12 -= arrayOfLong1[i + 12];
        l13 -= arrayOfLong1[i + 13];
        l14 -= arrayOfLong1[i + 14] + arrayOfLong2[j + 1];
        l15 -= arrayOfLong1[i + 15] + arrayOfLong2[j + 2];
        l16 -= arrayOfLong1[i + 16] + b + 1L;
        l16 = ThreefishEngine.xorRotr(l16, 9, l1);
        l1 -= l16;
        l12 = ThreefishEngine.xorRotr(l12, 48, l3);
        l3 -= l12;
        l14 = ThreefishEngine.xorRotr(l14, 35, l7);
        l7 -= l14;
        l10 = ThreefishEngine.xorRotr(l10, 52, l5);
        l5 -= l10;
        l2 = ThreefishEngine.xorRotr(l2, 23, l15);
        l15 -= l2;
        l6 = ThreefishEngine.xorRotr(l6, 31, l9);
        l9 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 37, l11);
        l11 -= l4;
        l8 = ThreefishEngine.xorRotr(l8, 20, l13);
        l13 -= l8;
        l8 = ThreefishEngine.xorRotr(l8, 31, l1);
        l1 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 44, l3);
        l3 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 47, l5);
        l5 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 46, l7);
        l7 -= l2;
        l16 = ThreefishEngine.xorRotr(l16, 19, l13);
        l13 -= l16;
        l14 = ThreefishEngine.xorRotr(l14, 42, l15);
        l15 -= l14;
        l12 = ThreefishEngine.xorRotr(l12, 44, l9);
        l9 -= l12;
        l10 = ThreefishEngine.xorRotr(l10, 25, l11);
        l11 -= l10;
        l10 = ThreefishEngine.xorRotr(l10, 16, l1);
        l1 -= l10;
        l14 = ThreefishEngine.xorRotr(l14, 34, l3);
        l3 -= l14;
        l12 = ThreefishEngine.xorRotr(l12, 56, l7);
        l7 -= l12;
        l16 = ThreefishEngine.xorRotr(l16, 51, l5);
        l5 -= l16;
        l8 = ThreefishEngine.xorRotr(l8, 4, l11);
        l11 -= l8;
        l4 = ThreefishEngine.xorRotr(l4, 53, l13);
        l13 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 42, l15);
        l15 -= l6;
        l2 = ThreefishEngine.xorRotr(l2, 41, l9);
        l9 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 41, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 9, l3);
        l3 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 37, l5);
        l5 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 31, l7);
        l7 -= l8;
        l10 = ThreefishEngine.xorRotr(l10, 12, l9);
        l9 -= l10;
        l12 = ThreefishEngine.xorRotr(l12, 47, l11);
        l11 -= l12;
        l14 = ThreefishEngine.xorRotr(l14, 44, l13);
        l13 -= l14;
        l16 = ThreefishEngine.xorRotr(l16, 30, l15);
        l15 -= l16;
        l1 -= arrayOfLong1[i];
        l2 -= arrayOfLong1[i + 1];
        l3 -= arrayOfLong1[i + 2];
        l4 -= arrayOfLong1[i + 3];
        l5 -= arrayOfLong1[i + 4];
        l6 -= arrayOfLong1[i + 5];
        l7 -= arrayOfLong1[i + 6];
        l8 -= arrayOfLong1[i + 7];
        l9 -= arrayOfLong1[i + 8];
        l10 -= arrayOfLong1[i + 9];
        l11 -= arrayOfLong1[i + 10];
        l12 -= arrayOfLong1[i + 11];
        l13 -= arrayOfLong1[i + 12];
        l14 -= arrayOfLong1[i + 13] + arrayOfLong2[j];
        l15 -= arrayOfLong1[i + 14] + arrayOfLong2[j + 1];
        l16 -= arrayOfLong1[i + 15] + b;
        l16 = ThreefishEngine.xorRotr(l16, 5, l1);
        l1 -= l16;
        l12 = ThreefishEngine.xorRotr(l12, 20, l3);
        l3 -= l12;
        l14 = ThreefishEngine.xorRotr(l14, 48, l7);
        l7 -= l14;
        l10 = ThreefishEngine.xorRotr(l10, 41, l5);
        l5 -= l10;
        l2 = ThreefishEngine.xorRotr(l2, 47, l15);
        l15 -= l2;
        l6 = ThreefishEngine.xorRotr(l6, 28, l9);
        l9 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 16, l11);
        l11 -= l4;
        l8 = ThreefishEngine.xorRotr(l8, 25, l13);
        l13 -= l8;
        l8 = ThreefishEngine.xorRotr(l8, 33, l1);
        l1 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 4, l3);
        l3 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 51, l5);
        l5 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 13, l7);
        l7 -= l2;
        l16 = ThreefishEngine.xorRotr(l16, 34, l13);
        l13 -= l16;
        l14 = ThreefishEngine.xorRotr(l14, 41, l15);
        l15 -= l14;
        l12 = ThreefishEngine.xorRotr(l12, 59, l9);
        l9 -= l12;
        l10 = ThreefishEngine.xorRotr(l10, 17, l11);
        l11 -= l10;
        l10 = ThreefishEngine.xorRotr(l10, 38, l1);
        l1 -= l10;
        l14 = ThreefishEngine.xorRotr(l14, 19, l3);
        l3 -= l14;
        l12 = ThreefishEngine.xorRotr(l12, 10, l7);
        l7 -= l12;
        l16 = ThreefishEngine.xorRotr(l16, 55, l5);
        l5 -= l16;
        l8 = ThreefishEngine.xorRotr(l8, 49, l11);
        l11 -= l8;
        l4 = ThreefishEngine.xorRotr(l4, 18, l13);
        l13 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 23, l15);
        l15 -= l6;
        l2 = ThreefishEngine.xorRotr(l2, 52, l9);
        l9 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 24, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 13, l3);
        l3 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 8, l5);
        l5 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 47, l7);
        l7 -= l8;
        l10 = ThreefishEngine.xorRotr(l10, 8, l9);
        l9 -= l10;
        l12 = ThreefishEngine.xorRotr(l12, 17, l11);
        l11 -= l12;
        l14 = ThreefishEngine.xorRotr(l14, 22, l13);
        l13 -= l14;
        l16 = ThreefishEngine.xorRotr(l16, 37, l15);
        l15 -= l16;
      } 
      l1 -= arrayOfLong1[0];
      l2 -= arrayOfLong1[1];
      l3 -= arrayOfLong1[2];
      l4 -= arrayOfLong1[3];
      l5 -= arrayOfLong1[4];
      l6 -= arrayOfLong1[5];
      l7 -= arrayOfLong1[6];
      l8 -= arrayOfLong1[7];
      l9 -= arrayOfLong1[8];
      l10 -= arrayOfLong1[9];
      l11 -= arrayOfLong1[10];
      l12 -= arrayOfLong1[11];
      l13 -= arrayOfLong1[12];
      l14 -= arrayOfLong1[13] + arrayOfLong2[0];
      l15 -= arrayOfLong1[14] + arrayOfLong2[1];
      l16 -= arrayOfLong1[15];
      param1ArrayOflong2[0] = l1;
      param1ArrayOflong2[1] = l2;
      param1ArrayOflong2[2] = l3;
      param1ArrayOflong2[3] = l4;
      param1ArrayOflong2[4] = l5;
      param1ArrayOflong2[5] = l6;
      param1ArrayOflong2[6] = l7;
      param1ArrayOflong2[7] = l8;
      param1ArrayOflong2[8] = l9;
      param1ArrayOflong2[9] = l10;
      param1ArrayOflong2[10] = l11;
      param1ArrayOflong2[11] = l12;
      param1ArrayOflong2[12] = l13;
      param1ArrayOflong2[13] = l14;
      param1ArrayOflong2[14] = l15;
      param1ArrayOflong2[15] = l16;
    }
  }
  
  private static final class Threefish256Cipher extends ThreefishCipher {
    private static final int ROTATION_0_0 = 14;
    
    private static final int ROTATION_0_1 = 16;
    
    private static final int ROTATION_1_0 = 52;
    
    private static final int ROTATION_1_1 = 57;
    
    private static final int ROTATION_2_0 = 23;
    
    private static final int ROTATION_2_1 = 40;
    
    private static final int ROTATION_3_0 = 5;
    
    private static final int ROTATION_3_1 = 37;
    
    private static final int ROTATION_4_0 = 25;
    
    private static final int ROTATION_4_1 = 33;
    
    private static final int ROTATION_5_0 = 46;
    
    private static final int ROTATION_5_1 = 12;
    
    private static final int ROTATION_6_0 = 58;
    
    private static final int ROTATION_6_1 = 22;
    
    private static final int ROTATION_7_0 = 32;
    
    private static final int ROTATION_7_1 = 32;
    
    public Threefish256Cipher(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      super(param1ArrayOflong1, param1ArrayOflong2);
    }
    
    void encryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      // Byte code:
      //   0: aload_0
      //   1: getfield kw : [J
      //   4: astore_3
      //   5: aload_0
      //   6: getfield t : [J
      //   9: astore #4
      //   11: invokestatic access$000 : ()[I
      //   14: astore #5
      //   16: invokestatic access$100 : ()[I
      //   19: astore #6
      //   21: aload_3
      //   22: arraylength
      //   23: bipush #9
      //   25: if_icmpeq -> 36
      //   28: new java/lang/IllegalArgumentException
      //   31: dup
      //   32: invokespecial <init> : ()V
      //   35: athrow
      //   36: aload #4
      //   38: arraylength
      //   39: iconst_5
      //   40: if_icmpeq -> 51
      //   43: new java/lang/IllegalArgumentException
      //   46: dup
      //   47: invokespecial <init> : ()V
      //   50: athrow
      //   51: aload_1
      //   52: iconst_0
      //   53: laload
      //   54: lstore #7
      //   56: aload_1
      //   57: iconst_1
      //   58: laload
      //   59: lstore #9
      //   61: aload_1
      //   62: iconst_2
      //   63: laload
      //   64: lstore #11
      //   66: aload_1
      //   67: iconst_3
      //   68: laload
      //   69: lstore #13
      //   71: lload #7
      //   73: aload_3
      //   74: iconst_0
      //   75: laload
      //   76: ladd
      //   77: lstore #7
      //   79: lload #9
      //   81: aload_3
      //   82: iconst_1
      //   83: laload
      //   84: aload #4
      //   86: iconst_0
      //   87: laload
      //   88: ladd
      //   89: ladd
      //   90: lstore #9
      //   92: lload #11
      //   94: aload_3
      //   95: iconst_2
      //   96: laload
      //   97: aload #4
      //   99: iconst_1
      //   100: laload
      //   101: ladd
      //   102: ladd
      //   103: lstore #11
      //   105: lload #13
      //   107: aload_3
      //   108: iconst_3
      //   109: laload
      //   110: ladd
      //   111: lstore #13
      //   113: iconst_1
      //   114: istore #15
      //   116: iload #15
      //   118: bipush #18
      //   120: if_icmpge -> 540
      //   123: aload #5
      //   125: iload #15
      //   127: iaload
      //   128: istore #16
      //   130: aload #6
      //   132: iload #15
      //   134: iaload
      //   135: istore #17
      //   137: lload #9
      //   139: bipush #14
      //   141: lload #7
      //   143: lload #9
      //   145: ladd
      //   146: dup2
      //   147: lstore #7
      //   149: invokestatic rotlXor : (JIJ)J
      //   152: lstore #9
      //   154: lload #13
      //   156: bipush #16
      //   158: lload #11
      //   160: lload #13
      //   162: ladd
      //   163: dup2
      //   164: lstore #11
      //   166: invokestatic rotlXor : (JIJ)J
      //   169: lstore #13
      //   171: lload #13
      //   173: bipush #52
      //   175: lload #7
      //   177: lload #13
      //   179: ladd
      //   180: dup2
      //   181: lstore #7
      //   183: invokestatic rotlXor : (JIJ)J
      //   186: lstore #13
      //   188: lload #9
      //   190: bipush #57
      //   192: lload #11
      //   194: lload #9
      //   196: ladd
      //   197: dup2
      //   198: lstore #11
      //   200: invokestatic rotlXor : (JIJ)J
      //   203: lstore #9
      //   205: lload #9
      //   207: bipush #23
      //   209: lload #7
      //   211: lload #9
      //   213: ladd
      //   214: dup2
      //   215: lstore #7
      //   217: invokestatic rotlXor : (JIJ)J
      //   220: lstore #9
      //   222: lload #13
      //   224: bipush #40
      //   226: lload #11
      //   228: lload #13
      //   230: ladd
      //   231: dup2
      //   232: lstore #11
      //   234: invokestatic rotlXor : (JIJ)J
      //   237: lstore #13
      //   239: lload #13
      //   241: iconst_5
      //   242: lload #7
      //   244: lload #13
      //   246: ladd
      //   247: dup2
      //   248: lstore #7
      //   250: invokestatic rotlXor : (JIJ)J
      //   253: lstore #13
      //   255: lload #9
      //   257: bipush #37
      //   259: lload #11
      //   261: lload #9
      //   263: ladd
      //   264: dup2
      //   265: lstore #11
      //   267: invokestatic rotlXor : (JIJ)J
      //   270: lstore #9
      //   272: lload #7
      //   274: aload_3
      //   275: iload #16
      //   277: laload
      //   278: ladd
      //   279: lstore #7
      //   281: lload #9
      //   283: aload_3
      //   284: iload #16
      //   286: iconst_1
      //   287: iadd
      //   288: laload
      //   289: aload #4
      //   291: iload #17
      //   293: laload
      //   294: ladd
      //   295: ladd
      //   296: lstore #9
      //   298: lload #11
      //   300: aload_3
      //   301: iload #16
      //   303: iconst_2
      //   304: iadd
      //   305: laload
      //   306: aload #4
      //   308: iload #17
      //   310: iconst_1
      //   311: iadd
      //   312: laload
      //   313: ladd
      //   314: ladd
      //   315: lstore #11
      //   317: lload #13
      //   319: aload_3
      //   320: iload #16
      //   322: iconst_3
      //   323: iadd
      //   324: laload
      //   325: iload #15
      //   327: i2l
      //   328: ladd
      //   329: ladd
      //   330: lstore #13
      //   332: lload #9
      //   334: bipush #25
      //   336: lload #7
      //   338: lload #9
      //   340: ladd
      //   341: dup2
      //   342: lstore #7
      //   344: invokestatic rotlXor : (JIJ)J
      //   347: lstore #9
      //   349: lload #13
      //   351: bipush #33
      //   353: lload #11
      //   355: lload #13
      //   357: ladd
      //   358: dup2
      //   359: lstore #11
      //   361: invokestatic rotlXor : (JIJ)J
      //   364: lstore #13
      //   366: lload #13
      //   368: bipush #46
      //   370: lload #7
      //   372: lload #13
      //   374: ladd
      //   375: dup2
      //   376: lstore #7
      //   378: invokestatic rotlXor : (JIJ)J
      //   381: lstore #13
      //   383: lload #9
      //   385: bipush #12
      //   387: lload #11
      //   389: lload #9
      //   391: ladd
      //   392: dup2
      //   393: lstore #11
      //   395: invokestatic rotlXor : (JIJ)J
      //   398: lstore #9
      //   400: lload #9
      //   402: bipush #58
      //   404: lload #7
      //   406: lload #9
      //   408: ladd
      //   409: dup2
      //   410: lstore #7
      //   412: invokestatic rotlXor : (JIJ)J
      //   415: lstore #9
      //   417: lload #13
      //   419: bipush #22
      //   421: lload #11
      //   423: lload #13
      //   425: ladd
      //   426: dup2
      //   427: lstore #11
      //   429: invokestatic rotlXor : (JIJ)J
      //   432: lstore #13
      //   434: lload #13
      //   436: bipush #32
      //   438: lload #7
      //   440: lload #13
      //   442: ladd
      //   443: dup2
      //   444: lstore #7
      //   446: invokestatic rotlXor : (JIJ)J
      //   449: lstore #13
      //   451: lload #9
      //   453: bipush #32
      //   455: lload #11
      //   457: lload #9
      //   459: ladd
      //   460: dup2
      //   461: lstore #11
      //   463: invokestatic rotlXor : (JIJ)J
      //   466: lstore #9
      //   468: lload #7
      //   470: aload_3
      //   471: iload #16
      //   473: iconst_1
      //   474: iadd
      //   475: laload
      //   476: ladd
      //   477: lstore #7
      //   479: lload #9
      //   481: aload_3
      //   482: iload #16
      //   484: iconst_2
      //   485: iadd
      //   486: laload
      //   487: aload #4
      //   489: iload #17
      //   491: iconst_1
      //   492: iadd
      //   493: laload
      //   494: ladd
      //   495: ladd
      //   496: lstore #9
      //   498: lload #11
      //   500: aload_3
      //   501: iload #16
      //   503: iconst_3
      //   504: iadd
      //   505: laload
      //   506: aload #4
      //   508: iload #17
      //   510: iconst_2
      //   511: iadd
      //   512: laload
      //   513: ladd
      //   514: ladd
      //   515: lstore #11
      //   517: lload #13
      //   519: aload_3
      //   520: iload #16
      //   522: iconst_4
      //   523: iadd
      //   524: laload
      //   525: iload #15
      //   527: i2l
      //   528: ladd
      //   529: lconst_1
      //   530: ladd
      //   531: ladd
      //   532: lstore #13
      //   534: iinc #15, 2
      //   537: goto -> 116
      //   540: aload_2
      //   541: iconst_0
      //   542: lload #7
      //   544: lastore
      //   545: aload_2
      //   546: iconst_1
      //   547: lload #9
      //   549: lastore
      //   550: aload_2
      //   551: iconst_2
      //   552: lload #11
      //   554: lastore
      //   555: aload_2
      //   556: iconst_3
      //   557: lload #13
      //   559: lastore
      //   560: return
    }
    
    void decryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      long[] arrayOfLong1 = this.kw;
      long[] arrayOfLong2 = this.t;
      int[] arrayOfInt1 = ThreefishEngine.MOD5;
      int[] arrayOfInt2 = ThreefishEngine.MOD3;
      if (arrayOfLong1.length != 9)
        throw new IllegalArgumentException(); 
      if (arrayOfLong2.length != 5)
        throw new IllegalArgumentException(); 
      long l1 = param1ArrayOflong1[0];
      long l2 = param1ArrayOflong1[1];
      long l3 = param1ArrayOflong1[2];
      long l4 = param1ArrayOflong1[3];
      for (byte b = 17; b >= 1; b -= 2) {
        int i = arrayOfInt1[b];
        int j = arrayOfInt2[b];
        l1 -= arrayOfLong1[i + 1];
        l2 -= arrayOfLong1[i + 2] + arrayOfLong2[j + 1];
        l3 -= arrayOfLong1[i + 3] + arrayOfLong2[j + 2];
        l4 -= arrayOfLong1[i + 4] + b + 1L;
        l4 = ThreefishEngine.xorRotr(l4, 32, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 32, l3);
        l3 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 58, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 22, l3);
        l3 -= l4;
        l4 = ThreefishEngine.xorRotr(l4, 46, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 12, l3);
        l3 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 25, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 33, l3);
        l3 -= l4;
        l1 -= arrayOfLong1[i];
        l2 -= arrayOfLong1[i + 1] + arrayOfLong2[j];
        l3 -= arrayOfLong1[i + 2] + arrayOfLong2[j + 1];
        l4 -= arrayOfLong1[i + 3] + b;
        l4 = ThreefishEngine.xorRotr(l4, 5, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 37, l3);
        l3 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 23, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 40, l3);
        l3 -= l4;
        l4 = ThreefishEngine.xorRotr(l4, 52, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 57, l3);
        l3 -= l2;
        l2 = ThreefishEngine.xorRotr(l2, 14, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 16, l3);
        l3 -= l4;
      } 
      l1 -= arrayOfLong1[0];
      l2 -= arrayOfLong1[1] + arrayOfLong2[0];
      l3 -= arrayOfLong1[2] + arrayOfLong2[1];
      l4 -= arrayOfLong1[3];
      param1ArrayOflong2[0] = l1;
      param1ArrayOflong2[1] = l2;
      param1ArrayOflong2[2] = l3;
      param1ArrayOflong2[3] = l4;
    }
  }
  
  private static final class Threefish512Cipher extends ThreefishCipher {
    private static final int ROTATION_0_0 = 46;
    
    private static final int ROTATION_0_1 = 36;
    
    private static final int ROTATION_0_2 = 19;
    
    private static final int ROTATION_0_3 = 37;
    
    private static final int ROTATION_1_0 = 33;
    
    private static final int ROTATION_1_1 = 27;
    
    private static final int ROTATION_1_2 = 14;
    
    private static final int ROTATION_1_3 = 42;
    
    private static final int ROTATION_2_0 = 17;
    
    private static final int ROTATION_2_1 = 49;
    
    private static final int ROTATION_2_2 = 36;
    
    private static final int ROTATION_2_3 = 39;
    
    private static final int ROTATION_3_0 = 44;
    
    private static final int ROTATION_3_1 = 9;
    
    private static final int ROTATION_3_2 = 54;
    
    private static final int ROTATION_3_3 = 56;
    
    private static final int ROTATION_4_0 = 39;
    
    private static final int ROTATION_4_1 = 30;
    
    private static final int ROTATION_4_2 = 34;
    
    private static final int ROTATION_4_3 = 24;
    
    private static final int ROTATION_5_0 = 13;
    
    private static final int ROTATION_5_1 = 50;
    
    private static final int ROTATION_5_2 = 10;
    
    private static final int ROTATION_5_3 = 17;
    
    private static final int ROTATION_6_0 = 25;
    
    private static final int ROTATION_6_1 = 29;
    
    private static final int ROTATION_6_2 = 39;
    
    private static final int ROTATION_6_3 = 43;
    
    private static final int ROTATION_7_0 = 8;
    
    private static final int ROTATION_7_1 = 35;
    
    private static final int ROTATION_7_2 = 56;
    
    private static final int ROTATION_7_3 = 22;
    
    protected Threefish512Cipher(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      super(param1ArrayOflong1, param1ArrayOflong2);
    }
    
    public void encryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      // Byte code:
      //   0: aload_0
      //   1: getfield kw : [J
      //   4: astore_3
      //   5: aload_0
      //   6: getfield t : [J
      //   9: astore #4
      //   11: invokestatic access$200 : ()[I
      //   14: astore #5
      //   16: invokestatic access$100 : ()[I
      //   19: astore #6
      //   21: aload_3
      //   22: arraylength
      //   23: bipush #17
      //   25: if_icmpeq -> 36
      //   28: new java/lang/IllegalArgumentException
      //   31: dup
      //   32: invokespecial <init> : ()V
      //   35: athrow
      //   36: aload #4
      //   38: arraylength
      //   39: iconst_5
      //   40: if_icmpeq -> 51
      //   43: new java/lang/IllegalArgumentException
      //   46: dup
      //   47: invokespecial <init> : ()V
      //   50: athrow
      //   51: aload_1
      //   52: iconst_0
      //   53: laload
      //   54: lstore #7
      //   56: aload_1
      //   57: iconst_1
      //   58: laload
      //   59: lstore #9
      //   61: aload_1
      //   62: iconst_2
      //   63: laload
      //   64: lstore #11
      //   66: aload_1
      //   67: iconst_3
      //   68: laload
      //   69: lstore #13
      //   71: aload_1
      //   72: iconst_4
      //   73: laload
      //   74: lstore #15
      //   76: aload_1
      //   77: iconst_5
      //   78: laload
      //   79: lstore #17
      //   81: aload_1
      //   82: bipush #6
      //   84: laload
      //   85: lstore #19
      //   87: aload_1
      //   88: bipush #7
      //   90: laload
      //   91: lstore #21
      //   93: lload #7
      //   95: aload_3
      //   96: iconst_0
      //   97: laload
      //   98: ladd
      //   99: lstore #7
      //   101: lload #9
      //   103: aload_3
      //   104: iconst_1
      //   105: laload
      //   106: ladd
      //   107: lstore #9
      //   109: lload #11
      //   111: aload_3
      //   112: iconst_2
      //   113: laload
      //   114: ladd
      //   115: lstore #11
      //   117: lload #13
      //   119: aload_3
      //   120: iconst_3
      //   121: laload
      //   122: ladd
      //   123: lstore #13
      //   125: lload #15
      //   127: aload_3
      //   128: iconst_4
      //   129: laload
      //   130: ladd
      //   131: lstore #15
      //   133: lload #17
      //   135: aload_3
      //   136: iconst_5
      //   137: laload
      //   138: aload #4
      //   140: iconst_0
      //   141: laload
      //   142: ladd
      //   143: ladd
      //   144: lstore #17
      //   146: lload #19
      //   148: aload_3
      //   149: bipush #6
      //   151: laload
      //   152: aload #4
      //   154: iconst_1
      //   155: laload
      //   156: ladd
      //   157: ladd
      //   158: lstore #19
      //   160: lload #21
      //   162: aload_3
      //   163: bipush #7
      //   165: laload
      //   166: ladd
      //   167: lstore #21
      //   169: iconst_1
      //   170: istore #23
      //   172: iload #23
      //   174: bipush #18
      //   176: if_icmpge -> 962
      //   179: aload #5
      //   181: iload #23
      //   183: iaload
      //   184: istore #24
      //   186: aload #6
      //   188: iload #23
      //   190: iaload
      //   191: istore #25
      //   193: lload #9
      //   195: bipush #46
      //   197: lload #7
      //   199: lload #9
      //   201: ladd
      //   202: dup2
      //   203: lstore #7
      //   205: invokestatic rotlXor : (JIJ)J
      //   208: lstore #9
      //   210: lload #13
      //   212: bipush #36
      //   214: lload #11
      //   216: lload #13
      //   218: ladd
      //   219: dup2
      //   220: lstore #11
      //   222: invokestatic rotlXor : (JIJ)J
      //   225: lstore #13
      //   227: lload #17
      //   229: bipush #19
      //   231: lload #15
      //   233: lload #17
      //   235: ladd
      //   236: dup2
      //   237: lstore #15
      //   239: invokestatic rotlXor : (JIJ)J
      //   242: lstore #17
      //   244: lload #21
      //   246: bipush #37
      //   248: lload #19
      //   250: lload #21
      //   252: ladd
      //   253: dup2
      //   254: lstore #19
      //   256: invokestatic rotlXor : (JIJ)J
      //   259: lstore #21
      //   261: lload #9
      //   263: bipush #33
      //   265: lload #11
      //   267: lload #9
      //   269: ladd
      //   270: dup2
      //   271: lstore #11
      //   273: invokestatic rotlXor : (JIJ)J
      //   276: lstore #9
      //   278: lload #21
      //   280: bipush #27
      //   282: lload #15
      //   284: lload #21
      //   286: ladd
      //   287: dup2
      //   288: lstore #15
      //   290: invokestatic rotlXor : (JIJ)J
      //   293: lstore #21
      //   295: lload #17
      //   297: bipush #14
      //   299: lload #19
      //   301: lload #17
      //   303: ladd
      //   304: dup2
      //   305: lstore #19
      //   307: invokestatic rotlXor : (JIJ)J
      //   310: lstore #17
      //   312: lload #13
      //   314: bipush #42
      //   316: lload #7
      //   318: lload #13
      //   320: ladd
      //   321: dup2
      //   322: lstore #7
      //   324: invokestatic rotlXor : (JIJ)J
      //   327: lstore #13
      //   329: lload #9
      //   331: bipush #17
      //   333: lload #15
      //   335: lload #9
      //   337: ladd
      //   338: dup2
      //   339: lstore #15
      //   341: invokestatic rotlXor : (JIJ)J
      //   344: lstore #9
      //   346: lload #13
      //   348: bipush #49
      //   350: lload #19
      //   352: lload #13
      //   354: ladd
      //   355: dup2
      //   356: lstore #19
      //   358: invokestatic rotlXor : (JIJ)J
      //   361: lstore #13
      //   363: lload #17
      //   365: bipush #36
      //   367: lload #7
      //   369: lload #17
      //   371: ladd
      //   372: dup2
      //   373: lstore #7
      //   375: invokestatic rotlXor : (JIJ)J
      //   378: lstore #17
      //   380: lload #21
      //   382: bipush #39
      //   384: lload #11
      //   386: lload #21
      //   388: ladd
      //   389: dup2
      //   390: lstore #11
      //   392: invokestatic rotlXor : (JIJ)J
      //   395: lstore #21
      //   397: lload #9
      //   399: bipush #44
      //   401: lload #19
      //   403: lload #9
      //   405: ladd
      //   406: dup2
      //   407: lstore #19
      //   409: invokestatic rotlXor : (JIJ)J
      //   412: lstore #9
      //   414: lload #21
      //   416: bipush #9
      //   418: lload #7
      //   420: lload #21
      //   422: ladd
      //   423: dup2
      //   424: lstore #7
      //   426: invokestatic rotlXor : (JIJ)J
      //   429: lstore #21
      //   431: lload #17
      //   433: bipush #54
      //   435: lload #11
      //   437: lload #17
      //   439: ladd
      //   440: dup2
      //   441: lstore #11
      //   443: invokestatic rotlXor : (JIJ)J
      //   446: lstore #17
      //   448: lload #13
      //   450: bipush #56
      //   452: lload #15
      //   454: lload #13
      //   456: ladd
      //   457: dup2
      //   458: lstore #15
      //   460: invokestatic rotlXor : (JIJ)J
      //   463: lstore #13
      //   465: lload #7
      //   467: aload_3
      //   468: iload #24
      //   470: laload
      //   471: ladd
      //   472: lstore #7
      //   474: lload #9
      //   476: aload_3
      //   477: iload #24
      //   479: iconst_1
      //   480: iadd
      //   481: laload
      //   482: ladd
      //   483: lstore #9
      //   485: lload #11
      //   487: aload_3
      //   488: iload #24
      //   490: iconst_2
      //   491: iadd
      //   492: laload
      //   493: ladd
      //   494: lstore #11
      //   496: lload #13
      //   498: aload_3
      //   499: iload #24
      //   501: iconst_3
      //   502: iadd
      //   503: laload
      //   504: ladd
      //   505: lstore #13
      //   507: lload #15
      //   509: aload_3
      //   510: iload #24
      //   512: iconst_4
      //   513: iadd
      //   514: laload
      //   515: ladd
      //   516: lstore #15
      //   518: lload #17
      //   520: aload_3
      //   521: iload #24
      //   523: iconst_5
      //   524: iadd
      //   525: laload
      //   526: aload #4
      //   528: iload #25
      //   530: laload
      //   531: ladd
      //   532: ladd
      //   533: lstore #17
      //   535: lload #19
      //   537: aload_3
      //   538: iload #24
      //   540: bipush #6
      //   542: iadd
      //   543: laload
      //   544: aload #4
      //   546: iload #25
      //   548: iconst_1
      //   549: iadd
      //   550: laload
      //   551: ladd
      //   552: ladd
      //   553: lstore #19
      //   555: lload #21
      //   557: aload_3
      //   558: iload #24
      //   560: bipush #7
      //   562: iadd
      //   563: laload
      //   564: iload #23
      //   566: i2l
      //   567: ladd
      //   568: ladd
      //   569: lstore #21
      //   571: lload #9
      //   573: bipush #39
      //   575: lload #7
      //   577: lload #9
      //   579: ladd
      //   580: dup2
      //   581: lstore #7
      //   583: invokestatic rotlXor : (JIJ)J
      //   586: lstore #9
      //   588: lload #13
      //   590: bipush #30
      //   592: lload #11
      //   594: lload #13
      //   596: ladd
      //   597: dup2
      //   598: lstore #11
      //   600: invokestatic rotlXor : (JIJ)J
      //   603: lstore #13
      //   605: lload #17
      //   607: bipush #34
      //   609: lload #15
      //   611: lload #17
      //   613: ladd
      //   614: dup2
      //   615: lstore #15
      //   617: invokestatic rotlXor : (JIJ)J
      //   620: lstore #17
      //   622: lload #21
      //   624: bipush #24
      //   626: lload #19
      //   628: lload #21
      //   630: ladd
      //   631: dup2
      //   632: lstore #19
      //   634: invokestatic rotlXor : (JIJ)J
      //   637: lstore #21
      //   639: lload #9
      //   641: bipush #13
      //   643: lload #11
      //   645: lload #9
      //   647: ladd
      //   648: dup2
      //   649: lstore #11
      //   651: invokestatic rotlXor : (JIJ)J
      //   654: lstore #9
      //   656: lload #21
      //   658: bipush #50
      //   660: lload #15
      //   662: lload #21
      //   664: ladd
      //   665: dup2
      //   666: lstore #15
      //   668: invokestatic rotlXor : (JIJ)J
      //   671: lstore #21
      //   673: lload #17
      //   675: bipush #10
      //   677: lload #19
      //   679: lload #17
      //   681: ladd
      //   682: dup2
      //   683: lstore #19
      //   685: invokestatic rotlXor : (JIJ)J
      //   688: lstore #17
      //   690: lload #13
      //   692: bipush #17
      //   694: lload #7
      //   696: lload #13
      //   698: ladd
      //   699: dup2
      //   700: lstore #7
      //   702: invokestatic rotlXor : (JIJ)J
      //   705: lstore #13
      //   707: lload #9
      //   709: bipush #25
      //   711: lload #15
      //   713: lload #9
      //   715: ladd
      //   716: dup2
      //   717: lstore #15
      //   719: invokestatic rotlXor : (JIJ)J
      //   722: lstore #9
      //   724: lload #13
      //   726: bipush #29
      //   728: lload #19
      //   730: lload #13
      //   732: ladd
      //   733: dup2
      //   734: lstore #19
      //   736: invokestatic rotlXor : (JIJ)J
      //   739: lstore #13
      //   741: lload #17
      //   743: bipush #39
      //   745: lload #7
      //   747: lload #17
      //   749: ladd
      //   750: dup2
      //   751: lstore #7
      //   753: invokestatic rotlXor : (JIJ)J
      //   756: lstore #17
      //   758: lload #21
      //   760: bipush #43
      //   762: lload #11
      //   764: lload #21
      //   766: ladd
      //   767: dup2
      //   768: lstore #11
      //   770: invokestatic rotlXor : (JIJ)J
      //   773: lstore #21
      //   775: lload #9
      //   777: bipush #8
      //   779: lload #19
      //   781: lload #9
      //   783: ladd
      //   784: dup2
      //   785: lstore #19
      //   787: invokestatic rotlXor : (JIJ)J
      //   790: lstore #9
      //   792: lload #21
      //   794: bipush #35
      //   796: lload #7
      //   798: lload #21
      //   800: ladd
      //   801: dup2
      //   802: lstore #7
      //   804: invokestatic rotlXor : (JIJ)J
      //   807: lstore #21
      //   809: lload #17
      //   811: bipush #56
      //   813: lload #11
      //   815: lload #17
      //   817: ladd
      //   818: dup2
      //   819: lstore #11
      //   821: invokestatic rotlXor : (JIJ)J
      //   824: lstore #17
      //   826: lload #13
      //   828: bipush #22
      //   830: lload #15
      //   832: lload #13
      //   834: ladd
      //   835: dup2
      //   836: lstore #15
      //   838: invokestatic rotlXor : (JIJ)J
      //   841: lstore #13
      //   843: lload #7
      //   845: aload_3
      //   846: iload #24
      //   848: iconst_1
      //   849: iadd
      //   850: laload
      //   851: ladd
      //   852: lstore #7
      //   854: lload #9
      //   856: aload_3
      //   857: iload #24
      //   859: iconst_2
      //   860: iadd
      //   861: laload
      //   862: ladd
      //   863: lstore #9
      //   865: lload #11
      //   867: aload_3
      //   868: iload #24
      //   870: iconst_3
      //   871: iadd
      //   872: laload
      //   873: ladd
      //   874: lstore #11
      //   876: lload #13
      //   878: aload_3
      //   879: iload #24
      //   881: iconst_4
      //   882: iadd
      //   883: laload
      //   884: ladd
      //   885: lstore #13
      //   887: lload #15
      //   889: aload_3
      //   890: iload #24
      //   892: iconst_5
      //   893: iadd
      //   894: laload
      //   895: ladd
      //   896: lstore #15
      //   898: lload #17
      //   900: aload_3
      //   901: iload #24
      //   903: bipush #6
      //   905: iadd
      //   906: laload
      //   907: aload #4
      //   909: iload #25
      //   911: iconst_1
      //   912: iadd
      //   913: laload
      //   914: ladd
      //   915: ladd
      //   916: lstore #17
      //   918: lload #19
      //   920: aload_3
      //   921: iload #24
      //   923: bipush #7
      //   925: iadd
      //   926: laload
      //   927: aload #4
      //   929: iload #25
      //   931: iconst_2
      //   932: iadd
      //   933: laload
      //   934: ladd
      //   935: ladd
      //   936: lstore #19
      //   938: lload #21
      //   940: aload_3
      //   941: iload #24
      //   943: bipush #8
      //   945: iadd
      //   946: laload
      //   947: iload #23
      //   949: i2l
      //   950: ladd
      //   951: lconst_1
      //   952: ladd
      //   953: ladd
      //   954: lstore #21
      //   956: iinc #23, 2
      //   959: goto -> 172
      //   962: aload_2
      //   963: iconst_0
      //   964: lload #7
      //   966: lastore
      //   967: aload_2
      //   968: iconst_1
      //   969: lload #9
      //   971: lastore
      //   972: aload_2
      //   973: iconst_2
      //   974: lload #11
      //   976: lastore
      //   977: aload_2
      //   978: iconst_3
      //   979: lload #13
      //   981: lastore
      //   982: aload_2
      //   983: iconst_4
      //   984: lload #15
      //   986: lastore
      //   987: aload_2
      //   988: iconst_5
      //   989: lload #17
      //   991: lastore
      //   992: aload_2
      //   993: bipush #6
      //   995: lload #19
      //   997: lastore
      //   998: aload_2
      //   999: bipush #7
      //   1001: lload #21
      //   1003: lastore
      //   1004: return
    }
    
    public void decryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      long[] arrayOfLong1 = this.kw;
      long[] arrayOfLong2 = this.t;
      int[] arrayOfInt1 = ThreefishEngine.MOD9;
      int[] arrayOfInt2 = ThreefishEngine.MOD3;
      if (arrayOfLong1.length != 17)
        throw new IllegalArgumentException(); 
      if (arrayOfLong2.length != 5)
        throw new IllegalArgumentException(); 
      long l1 = param1ArrayOflong1[0];
      long l2 = param1ArrayOflong1[1];
      long l3 = param1ArrayOflong1[2];
      long l4 = param1ArrayOflong1[3];
      long l5 = param1ArrayOflong1[4];
      long l6 = param1ArrayOflong1[5];
      long l7 = param1ArrayOflong1[6];
      long l8 = param1ArrayOflong1[7];
      for (byte b = 17; b >= 1; b -= 2) {
        int i = arrayOfInt1[b];
        int j = arrayOfInt2[b];
        l1 -= arrayOfLong1[i + 1];
        l2 -= arrayOfLong1[i + 2];
        l3 -= arrayOfLong1[i + 3];
        l4 -= arrayOfLong1[i + 4];
        l5 -= arrayOfLong1[i + 5];
        l6 -= arrayOfLong1[i + 6] + arrayOfLong2[j + 1];
        l7 -= arrayOfLong1[i + 7] + arrayOfLong2[j + 2];
        l8 -= arrayOfLong1[i + 8] + b + 1L;
        l2 = ThreefishEngine.xorRotr(l2, 8, l7);
        l7 -= l2;
        l8 = ThreefishEngine.xorRotr(l8, 35, l1);
        l1 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 56, l3);
        l3 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 22, l5);
        l5 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 25, l5);
        l5 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 29, l7);
        l7 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 39, l1);
        l1 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 43, l3);
        l3 -= l8;
        l2 = ThreefishEngine.xorRotr(l2, 13, l3);
        l3 -= l2;
        l8 = ThreefishEngine.xorRotr(l8, 50, l5);
        l5 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 10, l7);
        l7 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 17, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 39, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 30, l3);
        l3 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 34, l5);
        l5 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 24, l7);
        l7 -= l8;
        l1 -= arrayOfLong1[i];
        l2 -= arrayOfLong1[i + 1];
        l3 -= arrayOfLong1[i + 2];
        l4 -= arrayOfLong1[i + 3];
        l5 -= arrayOfLong1[i + 4];
        l6 -= arrayOfLong1[i + 5] + arrayOfLong2[j];
        l7 -= arrayOfLong1[i + 6] + arrayOfLong2[j + 1];
        l8 -= arrayOfLong1[i + 7] + b;
        l2 = ThreefishEngine.xorRotr(l2, 44, l7);
        l7 -= l2;
        l8 = ThreefishEngine.xorRotr(l8, 9, l1);
        l1 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 54, l3);
        l3 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 56, l5);
        l5 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 17, l5);
        l5 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 49, l7);
        l7 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 36, l1);
        l1 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 39, l3);
        l3 -= l8;
        l2 = ThreefishEngine.xorRotr(l2, 33, l3);
        l3 -= l2;
        l8 = ThreefishEngine.xorRotr(l8, 27, l5);
        l5 -= l8;
        l6 = ThreefishEngine.xorRotr(l6, 14, l7);
        l7 -= l6;
        l4 = ThreefishEngine.xorRotr(l4, 42, l1);
        l1 -= l4;
        l2 = ThreefishEngine.xorRotr(l2, 46, l1);
        l1 -= l2;
        l4 = ThreefishEngine.xorRotr(l4, 36, l3);
        l3 -= l4;
        l6 = ThreefishEngine.xorRotr(l6, 19, l5);
        l5 -= l6;
        l8 = ThreefishEngine.xorRotr(l8, 37, l7);
        l7 -= l8;
      } 
      l1 -= arrayOfLong1[0];
      l2 -= arrayOfLong1[1];
      l3 -= arrayOfLong1[2];
      l4 -= arrayOfLong1[3];
      l5 -= arrayOfLong1[4];
      l6 -= arrayOfLong1[5] + arrayOfLong2[0];
      l7 -= arrayOfLong1[6] + arrayOfLong2[1];
      l8 -= arrayOfLong1[7];
      param1ArrayOflong2[0] = l1;
      param1ArrayOflong2[1] = l2;
      param1ArrayOflong2[2] = l3;
      param1ArrayOflong2[3] = l4;
      param1ArrayOflong2[4] = l5;
      param1ArrayOflong2[5] = l6;
      param1ArrayOflong2[6] = l7;
      param1ArrayOflong2[7] = l8;
    }
  }
  
  private static abstract class ThreefishCipher {
    protected final long[] t;
    
    protected final long[] kw;
    
    protected ThreefishCipher(long[] param1ArrayOflong1, long[] param1ArrayOflong2) {
      this.kw = param1ArrayOflong1;
      this.t = param1ArrayOflong2;
    }
    
    abstract void encryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2);
    
    abstract void decryptBlock(long[] param1ArrayOflong1, long[] param1ArrayOflong2);
  }
}
