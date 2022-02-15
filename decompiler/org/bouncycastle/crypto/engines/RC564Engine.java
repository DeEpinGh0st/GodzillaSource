package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC564Engine implements BlockCipher {
  private static final int wordSize = 64;
  
  private static final int bytesPerWord = 8;
  
  private int _noRounds = 12;
  
  private long[] _S = null;
  
  private static final long P64 = -5196783011329398165L;
  
  private static final long Q64 = -7046029254386353131L;
  
  private boolean forEncryption;
  
  public String getAlgorithmName() {
    return "RC5-64";
  }
  
  public int getBlockSize() {
    return 16;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof RC5Parameters))
      throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + paramCipherParameters.getClass().getName()); 
    RC5Parameters rC5Parameters = (RC5Parameters)paramCipherParameters;
    this.forEncryption = paramBoolean;
    this._noRounds = rC5Parameters.getRounds();
    setKey(rC5Parameters.getKey());
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    return this.forEncryption ? encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2) : decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
  }
  
  public void reset() {}
  
  private void setKey(byte[] paramArrayOfbyte) {
    // Byte code:
    //   0: aload_1
    //   1: arraylength
    //   2: bipush #7
    //   4: iadd
    //   5: bipush #8
    //   7: idiv
    //   8: newarray long
    //   10: astore_2
    //   11: iconst_0
    //   12: istore_3
    //   13: iload_3
    //   14: aload_1
    //   15: arraylength
    //   16: if_icmpeq -> 50
    //   19: aload_2
    //   20: iload_3
    //   21: bipush #8
    //   23: idiv
    //   24: dup2
    //   25: laload
    //   26: aload_1
    //   27: iload_3
    //   28: baload
    //   29: sipush #255
    //   32: iand
    //   33: i2l
    //   34: bipush #8
    //   36: iload_3
    //   37: bipush #8
    //   39: irem
    //   40: imul
    //   41: lshl
    //   42: ladd
    //   43: lastore
    //   44: iinc #3, 1
    //   47: goto -> 13
    //   50: aload_0
    //   51: iconst_2
    //   52: aload_0
    //   53: getfield _noRounds : I
    //   56: iconst_1
    //   57: iadd
    //   58: imul
    //   59: newarray long
    //   61: putfield _S : [J
    //   64: aload_0
    //   65: getfield _S : [J
    //   68: iconst_0
    //   69: ldc2_w -5196783011329398165
    //   72: lastore
    //   73: iconst_1
    //   74: istore_3
    //   75: iload_3
    //   76: aload_0
    //   77: getfield _S : [J
    //   80: arraylength
    //   81: if_icmpge -> 108
    //   84: aload_0
    //   85: getfield _S : [J
    //   88: iload_3
    //   89: aload_0
    //   90: getfield _S : [J
    //   93: iload_3
    //   94: iconst_1
    //   95: isub
    //   96: laload
    //   97: ldc2_w -7046029254386353131
    //   100: ladd
    //   101: lastore
    //   102: iinc #3, 1
    //   105: goto -> 75
    //   108: aload_2
    //   109: arraylength
    //   110: aload_0
    //   111: getfield _S : [J
    //   114: arraylength
    //   115: if_icmple -> 126
    //   118: iconst_3
    //   119: aload_2
    //   120: arraylength
    //   121: imul
    //   122: istore_3
    //   123: goto -> 134
    //   126: iconst_3
    //   127: aload_0
    //   128: getfield _S : [J
    //   131: arraylength
    //   132: imul
    //   133: istore_3
    //   134: lconst_0
    //   135: lstore #4
    //   137: lconst_0
    //   138: lstore #6
    //   140: iconst_0
    //   141: istore #8
    //   143: iconst_0
    //   144: istore #9
    //   146: iconst_0
    //   147: istore #10
    //   149: iload #10
    //   151: iload_3
    //   152: if_icmpge -> 238
    //   155: aload_0
    //   156: getfield _S : [J
    //   159: iload #8
    //   161: aload_0
    //   162: aload_0
    //   163: getfield _S : [J
    //   166: iload #8
    //   168: laload
    //   169: lload #4
    //   171: ladd
    //   172: lload #6
    //   174: ladd
    //   175: ldc2_w 3
    //   178: invokespecial rotateLeft : (JJ)J
    //   181: dup2_x2
    //   182: lastore
    //   183: lstore #4
    //   185: aload_2
    //   186: iload #9
    //   188: aload_0
    //   189: aload_2
    //   190: iload #9
    //   192: laload
    //   193: lload #4
    //   195: ladd
    //   196: lload #6
    //   198: ladd
    //   199: lload #4
    //   201: lload #6
    //   203: ladd
    //   204: invokespecial rotateLeft : (JJ)J
    //   207: dup2_x2
    //   208: lastore
    //   209: lstore #6
    //   211: iload #8
    //   213: iconst_1
    //   214: iadd
    //   215: aload_0
    //   216: getfield _S : [J
    //   219: arraylength
    //   220: irem
    //   221: istore #8
    //   223: iload #9
    //   225: iconst_1
    //   226: iadd
    //   227: aload_2
    //   228: arraylength
    //   229: irem
    //   230: istore #9
    //   232: iinc #10, 1
    //   235: goto -> 149
    //   238: return
  }
  
  private int encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    long l1 = bytesToWord(paramArrayOfbyte1, paramInt1) + this._S[0];
    long l2 = bytesToWord(paramArrayOfbyte1, paramInt1 + 8) + this._S[1];
    for (byte b = 1; b <= this._noRounds; b++) {
      l1 = rotateLeft(l1 ^ l2, l2) + this._S[2 * b];
      l2 = rotateLeft(l2 ^ l1, l1) + this._S[2 * b + 1];
    } 
    wordToBytes(l1, paramArrayOfbyte2, paramInt2);
    wordToBytes(l2, paramArrayOfbyte2, paramInt2 + 8);
    return 16;
  }
  
  private int decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    long l1 = bytesToWord(paramArrayOfbyte1, paramInt1);
    long l2 = bytesToWord(paramArrayOfbyte1, paramInt1 + 8);
    for (int i = this._noRounds; i >= 1; i--) {
      l2 = rotateRight(l2 - this._S[2 * i + 1], l1) ^ l1;
      l1 = rotateRight(l1 - this._S[2 * i], l2) ^ l2;
    } 
    wordToBytes(l1 - this._S[0], paramArrayOfbyte2, paramInt2);
    wordToBytes(l2 - this._S[1], paramArrayOfbyte2, paramInt2 + 8);
    return 16;
  }
  
  private long rotateLeft(long paramLong1, long paramLong2) {
    return paramLong1 << (int)(paramLong2 & 0x3FL) | paramLong1 >>> (int)(64L - (paramLong2 & 0x3FL));
  }
  
  private long rotateRight(long paramLong1, long paramLong2) {
    return paramLong1 >>> (int)(paramLong2 & 0x3FL) | paramLong1 << (int)(64L - (paramLong2 & 0x3FL));
  }
  
  private long bytesToWord(byte[] paramArrayOfbyte, int paramInt) {
    long l = 0L;
    for (byte b = 7; b >= 0; b--)
      l = (l << 8L) + (paramArrayOfbyte[b + paramInt] & 0xFF); 
    return l;
  }
  
  private void wordToBytes(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b < 8; b++) {
      paramArrayOfbyte[b + paramInt] = (byte)(int)paramLong;
      paramLong >>>= 8L;
    } 
  }
}
