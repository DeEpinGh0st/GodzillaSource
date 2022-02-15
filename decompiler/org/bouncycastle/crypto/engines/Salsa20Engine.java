package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class Salsa20Engine implements SkippingStreamCipher {
  public static final int DEFAULT_ROUNDS = 20;
  
  private static final int STATE_SIZE = 16;
  
  private static final int[] TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
  
  protected static final byte[] sigma = Strings.toByteArray("expand 32-byte k");
  
  protected static final byte[] tau = Strings.toByteArray("expand 16-byte k");
  
  protected int rounds;
  
  private int index = 0;
  
  protected int[] engineState = new int[16];
  
  protected int[] x = new int[16];
  
  private byte[] keyStream = new byte[64];
  
  private boolean initialised = false;
  
  private int cW0;
  
  private int cW1;
  
  private int cW2;
  
  protected void packTauOrSigma(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    int i = (paramInt1 - 16) / 4;
    paramArrayOfint[paramInt2] = TAU_SIGMA[i];
    paramArrayOfint[paramInt2 + 1] = TAU_SIGMA[i + 1];
    paramArrayOfint[paramInt2 + 2] = TAU_SIGMA[i + 2];
    paramArrayOfint[paramInt2 + 3] = TAU_SIGMA[i + 3];
  }
  
  public Salsa20Engine() {
    this(20);
  }
  
  public Salsa20Engine(int paramInt) {
    if (paramInt <= 0 || (paramInt & 0x1) != 0)
      throw new IllegalArgumentException("'rounds' must be a positive, even number"); 
    this.rounds = paramInt;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (!(paramCipherParameters instanceof ParametersWithIV))
      throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must include an IV"); 
    ParametersWithIV parametersWithIV = (ParametersWithIV)paramCipherParameters;
    byte[] arrayOfByte = parametersWithIV.getIV();
    if (arrayOfByte == null || arrayOfByte.length != getNonceSize())
      throw new IllegalArgumentException(getAlgorithmName() + " requires exactly " + getNonceSize() + " bytes of IV"); 
    CipherParameters cipherParameters = parametersWithIV.getParameters();
    if (cipherParameters == null) {
      if (!this.initialised)
        throw new IllegalStateException(getAlgorithmName() + " KeyParameter can not be null for first initialisation"); 
      setKey(null, arrayOfByte);
    } else if (cipherParameters instanceof KeyParameter) {
      setKey(((KeyParameter)cipherParameters).getKey(), arrayOfByte);
    } else {
      throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
    } 
    reset();
    this.initialised = true;
  }
  
  protected int getNonceSize() {
    return 8;
  }
  
  public String getAlgorithmName() {
    String str = "Salsa20";
    if (this.rounds != 20)
      str = str + "/" + this.rounds; 
    return str;
  }
  
  public byte returnByte(byte paramByte) {
    if (limitExceeded())
      throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV"); 
    byte b = (byte)(this.keyStream[this.index] ^ paramByte);
    this.index = this.index + 1 & 0x3F;
    if (this.index == 0) {
      advanceCounter();
      generateKeyStream(this.keyStream);
    } 
    return b;
  }
  
  protected void advanceCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i > 0)
      this.engineState[9] = this.engineState[9] + i; 
    int k = this.engineState[8];
    this.engineState[8] = this.engineState[8] + j;
    if (k != 0 && this.engineState[8] < k)
      this.engineState[9] = this.engineState[9] + 1; 
  }
  
  protected void advanceCounter() {
    this.engineState[8] = this.engineState[8] + 1;
    if (this.engineState[8] + 1 == 0)
      this.engineState[9] = this.engineState[9] + 1; 
  }
  
  protected void retreatCounter(long paramLong) {
    int i = (int)(paramLong >>> 32L);
    int j = (int)paramLong;
    if (i != 0)
      if ((this.engineState[9] & 0xFFFFFFFFL) >= (i & 0xFFFFFFFFL)) {
        this.engineState[9] = this.engineState[9] - i;
      } else {
        throw new IllegalStateException("attempt to reduce counter past zero.");
      }  
    if ((this.engineState[8] & 0xFFFFFFFFL) >= (j & 0xFFFFFFFFL)) {
      this.engineState[8] = this.engineState[8] - j;
    } else if (this.engineState[9] != 0) {
      this.engineState[9] = this.engineState[9] - 1;
      this.engineState[8] = this.engineState[8] - j;
    } else {
      throw new IllegalStateException("attempt to reduce counter past zero.");
    } 
  }
  
  protected void retreatCounter() {
    if (this.engineState[8] == 0 && this.engineState[9] == 0)
      throw new IllegalStateException("attempt to reduce counter past zero."); 
    this.engineState[8] = this.engineState[8] - 1;
    if (this.engineState[8] - 1 == -1)
      this.engineState[9] = this.engineState[9] - 1; 
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (!this.initialised)
      throw new IllegalStateException(getAlgorithmName() + " not initialised"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt3 + paramInt2 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    if (limitExceeded(paramInt2))
      throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV"); 
    for (byte b = 0; b < paramInt2; b++) {
      paramArrayOfbyte2[b + paramInt3] = (byte)(this.keyStream[this.index] ^ paramArrayOfbyte1[b + paramInt1]);
      this.index = this.index + 1 & 0x3F;
      if (this.index == 0) {
        advanceCounter();
        generateKeyStream(this.keyStream);
      } 
    } 
    return paramInt2;
  }
  
  public long skip(long paramLong) {
    if (paramLong >= 0L) {
      long l = paramLong;
      if (l >= 64L) {
        long l1 = l / 64L;
        advanceCounter(l1);
        l -= l1 * 64L;
      } 
      int i = this.index;
      this.index = this.index + (int)l & 0x3F;
      if (this.index < i)
        advanceCounter(); 
    } else {
      long l1 = -paramLong;
      if (l1 >= 64L) {
        long l = l1 / 64L;
        retreatCounter(l);
        l1 -= l * 64L;
      } 
      long l2;
      for (l2 = 0L; l2 < l1; l2++) {
        if (this.index == 0)
          retreatCounter(); 
        this.index = this.index - 1 & 0x3F;
      } 
    } 
    generateKeyStream(this.keyStream);
    return paramLong;
  }
  
  public long seekTo(long paramLong) {
    reset();
    return skip(paramLong);
  }
  
  public long getPosition() {
    return getCounter() * 64L + this.index;
  }
  
  public void reset() {
    this.index = 0;
    resetLimitCounter();
    resetCounter();
    generateKeyStream(this.keyStream);
  }
  
  protected long getCounter() {
    return this.engineState[9] << 32L | this.engineState[8] & 0xFFFFFFFFL;
  }
  
  protected void resetCounter() {
    this.engineState[9] = 0;
    this.engineState[8] = 0;
  }
  
  protected void setKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 != null) {
      if (paramArrayOfbyte1.length != 16 && paramArrayOfbyte1.length != 32)
        throw new IllegalArgumentException(getAlgorithmName() + " requires 128 bit or 256 bit key"); 
      int i = (paramArrayOfbyte1.length - 16) / 4;
      this.engineState[0] = TAU_SIGMA[i];
      this.engineState[5] = TAU_SIGMA[i + 1];
      this.engineState[10] = TAU_SIGMA[i + 2];
      this.engineState[15] = TAU_SIGMA[i + 3];
      Pack.littleEndianToInt(paramArrayOfbyte1, 0, this.engineState, 1, 4);
      Pack.littleEndianToInt(paramArrayOfbyte1, paramArrayOfbyte1.length - 16, this.engineState, 11, 4);
    } 
    Pack.littleEndianToInt(paramArrayOfbyte2, 0, this.engineState, 6, 2);
  }
  
  protected void generateKeyStream(byte[] paramArrayOfbyte) {
    salsaCore(this.rounds, this.engineState, this.x);
    Pack.intToLittleEndian(this.x, paramArrayOfbyte, 0);
  }
  
  public static void salsaCore(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    if (paramArrayOfint1.length != 16)
      throw new IllegalArgumentException(); 
    if (paramArrayOfint2.length != 16)
      throw new IllegalArgumentException(); 
    if (paramInt % 2 != 0)
      throw new IllegalArgumentException("Number of rounds must be even"); 
    int i = paramArrayOfint1[0];
    int j = paramArrayOfint1[1];
    int k = paramArrayOfint1[2];
    int m = paramArrayOfint1[3];
    int n = paramArrayOfint1[4];
    int i1 = paramArrayOfint1[5];
    int i2 = paramArrayOfint1[6];
    int i3 = paramArrayOfint1[7];
    int i4 = paramArrayOfint1[8];
    int i5 = paramArrayOfint1[9];
    int i6 = paramArrayOfint1[10];
    int i7 = paramArrayOfint1[11];
    int i8 = paramArrayOfint1[12];
    int i9 = paramArrayOfint1[13];
    int i10 = paramArrayOfint1[14];
    int i11 = paramArrayOfint1[15];
    for (int i12 = paramInt; i12 > 0; i12 -= 2) {
      n ^= rotl(i + i8, 7);
      i4 ^= rotl(n + i, 9);
      i8 ^= rotl(i4 + n, 13);
      i ^= rotl(i8 + i4, 18);
      i5 ^= rotl(i1 + j, 7);
      i9 ^= rotl(i5 + i1, 9);
      j ^= rotl(i9 + i5, 13);
      i1 ^= rotl(j + i9, 18);
      i10 ^= rotl(i6 + i2, 7);
      k ^= rotl(i10 + i6, 9);
      i2 ^= rotl(k + i10, 13);
      i6 ^= rotl(i2 + k, 18);
      m ^= rotl(i11 + i7, 7);
      i3 ^= rotl(m + i11, 9);
      i7 ^= rotl(i3 + m, 13);
      i11 ^= rotl(i7 + i3, 18);
      j ^= rotl(i + m, 7);
      k ^= rotl(j + i, 9);
      m ^= rotl(k + j, 13);
      i ^= rotl(m + k, 18);
      i2 ^= rotl(i1 + n, 7);
      i3 ^= rotl(i2 + i1, 9);
      n ^= rotl(i3 + i2, 13);
      i1 ^= rotl(n + i3, 18);
      i7 ^= rotl(i6 + i5, 7);
      i4 ^= rotl(i7 + i6, 9);
      i5 ^= rotl(i4 + i7, 13);
      i6 ^= rotl(i5 + i4, 18);
      i8 ^= rotl(i11 + i10, 7);
      i9 ^= rotl(i8 + i11, 9);
      i10 ^= rotl(i9 + i8, 13);
      i11 ^= rotl(i10 + i9, 18);
    } 
    paramArrayOfint2[0] = i + paramArrayOfint1[0];
    paramArrayOfint2[1] = j + paramArrayOfint1[1];
    paramArrayOfint2[2] = k + paramArrayOfint1[2];
    paramArrayOfint2[3] = m + paramArrayOfint1[3];
    paramArrayOfint2[4] = n + paramArrayOfint1[4];
    paramArrayOfint2[5] = i1 + paramArrayOfint1[5];
    paramArrayOfint2[6] = i2 + paramArrayOfint1[6];
    paramArrayOfint2[7] = i3 + paramArrayOfint1[7];
    paramArrayOfint2[8] = i4 + paramArrayOfint1[8];
    paramArrayOfint2[9] = i5 + paramArrayOfint1[9];
    paramArrayOfint2[10] = i6 + paramArrayOfint1[10];
    paramArrayOfint2[11] = i7 + paramArrayOfint1[11];
    paramArrayOfint2[12] = i8 + paramArrayOfint1[12];
    paramArrayOfint2[13] = i9 + paramArrayOfint1[13];
    paramArrayOfint2[14] = i10 + paramArrayOfint1[14];
    paramArrayOfint2[15] = i11 + paramArrayOfint1[15];
  }
  
  protected static int rotl(int paramInt1, int paramInt2) {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  private void resetLimitCounter() {
    this.cW0 = 0;
    this.cW1 = 0;
    this.cW2 = 0;
  }
  
  private boolean limitExceeded() {
    return (++this.cW0 == 0 && ++this.cW1 == 0) ? (((++this.cW2 & 0x20) != 0)) : false;
  }
  
  private boolean limitExceeded(int paramInt) {
    this.cW0 += paramInt;
    return (this.cW0 < paramInt && this.cW0 >= 0 && ++this.cW1 == 0) ? (((++this.cW2 & 0x20) != 0)) : false;
  }
}
