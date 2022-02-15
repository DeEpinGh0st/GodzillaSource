package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class TlsBlockCipher implements TlsCipher {
  protected TlsContext context;
  
  protected byte[] randomData;
  
  protected boolean useExplicitIV;
  
  protected boolean encryptThenMAC;
  
  protected BlockCipher encryptCipher;
  
  protected BlockCipher decryptCipher;
  
  protected TlsMac writeMac;
  
  protected TlsMac readMac;
  
  public TlsMac getWriteMac() {
    return this.writeMac;
  }
  
  public TlsMac getReadMac() {
    return this.readMac;
  }
  
  public TlsBlockCipher(TlsContext paramTlsContext, BlockCipher paramBlockCipher1, BlockCipher paramBlockCipher2, Digest paramDigest1, Digest paramDigest2, int paramInt) throws IOException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte3;
    ParametersWithIV parametersWithIV1;
    ParametersWithIV parametersWithIV2;
    this.context = paramTlsContext;
    this.randomData = new byte[256];
    paramTlsContext.getNonceRandomGenerator().nextBytes(this.randomData);
    this.useExplicitIV = TlsUtils.isTLSv11(paramTlsContext);
    this.encryptThenMAC = (paramTlsContext.getSecurityParameters()).encryptThenMAC;
    int i = 2 * paramInt + paramDigest1.getDigestSize() + paramDigest2.getDigestSize();
    if (!this.useExplicitIV)
      i += paramBlockCipher1.getBlockSize() + paramBlockCipher2.getBlockSize(); 
    byte[] arrayOfByte1 = TlsUtils.calculateKeyBlock(paramTlsContext, i);
    int j = 0;
    TlsMac tlsMac1 = new TlsMac(paramTlsContext, paramDigest1, arrayOfByte1, j, paramDigest1.getDigestSize());
    j += paramDigest1.getDigestSize();
    TlsMac tlsMac2 = new TlsMac(paramTlsContext, paramDigest2, arrayOfByte1, j, paramDigest2.getDigestSize());
    j += paramDigest2.getDigestSize();
    KeyParameter keyParameter1 = new KeyParameter(arrayOfByte1, j, paramInt);
    j += paramInt;
    KeyParameter keyParameter2 = new KeyParameter(arrayOfByte1, j, paramInt);
    j += paramInt;
    if (this.useExplicitIV) {
      arrayOfByte2 = new byte[paramBlockCipher1.getBlockSize()];
      arrayOfByte3 = new byte[paramBlockCipher2.getBlockSize()];
    } else {
      arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, j, j + paramBlockCipher1.getBlockSize());
      j += paramBlockCipher1.getBlockSize();
      arrayOfByte3 = Arrays.copyOfRange(arrayOfByte1, j, j + paramBlockCipher2.getBlockSize());
      j += paramBlockCipher2.getBlockSize();
    } 
    if (j != i)
      throw new TlsFatalAlert((short)80); 
    if (paramTlsContext.isServer()) {
      this.writeMac = tlsMac2;
      this.readMac = tlsMac1;
      this.encryptCipher = paramBlockCipher2;
      this.decryptCipher = paramBlockCipher1;
      parametersWithIV1 = new ParametersWithIV((CipherParameters)keyParameter2, arrayOfByte3);
      parametersWithIV2 = new ParametersWithIV((CipherParameters)keyParameter1, arrayOfByte2);
    } else {
      this.writeMac = tlsMac1;
      this.readMac = tlsMac2;
      this.encryptCipher = paramBlockCipher1;
      this.decryptCipher = paramBlockCipher2;
      parametersWithIV1 = new ParametersWithIV((CipherParameters)keyParameter1, arrayOfByte2);
      parametersWithIV2 = new ParametersWithIV((CipherParameters)keyParameter2, arrayOfByte3);
    } 
    this.encryptCipher.init(true, (CipherParameters)parametersWithIV1);
    this.decryptCipher.init(false, (CipherParameters)parametersWithIV2);
  }
  
  public int getPlaintextLimit(int paramInt) {
    int i = this.encryptCipher.getBlockSize();
    int j = this.writeMac.getSize();
    int k = paramInt;
    if (this.useExplicitIV)
      k -= i; 
    if (this.encryptThenMAC) {
      k -= j;
      k -= k % i;
    } else {
      k -= k % i;
      k -= j;
    } 
    return --k;
  }
  
  public byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = this.encryptCipher.getBlockSize();
    int j = this.writeMac.getSize();
    ProtocolVersion protocolVersion = this.context.getServerVersion();
    int k = paramInt2;
    if (!this.encryptThenMAC)
      k += j; 
    int m = i - 1 - k % i;
    if ((this.encryptThenMAC || !(this.context.getSecurityParameters()).truncatedHMac) && !protocolVersion.isDTLS() && !protocolVersion.isSSL()) {
      int i4 = (255 - m) / i;
      int i5 = chooseExtraPadBlocks(this.context.getSecureRandom(), i4);
      m += i5 * i;
    } 
    int n = paramInt2 + j + m + 1;
    if (this.useExplicitIV)
      n += i; 
    byte[] arrayOfByte = new byte[n];
    int i1 = 0;
    if (this.useExplicitIV) {
      byte[] arrayOfByte1 = new byte[i];
      this.context.getNonceRandomGenerator().nextBytes(arrayOfByte1);
      this.encryptCipher.init(true, (CipherParameters)new ParametersWithIV(null, arrayOfByte1));
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, i1, i);
      i1 += i;
    } 
    int i2 = i1;
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, i1, paramInt2);
    i1 += paramInt2;
    if (!this.encryptThenMAC) {
      byte[] arrayOfByte1 = this.writeMac.calculateMac(paramLong, paramShort, paramArrayOfbyte, paramInt1, paramInt2);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, i1, arrayOfByte1.length);
      i1 += arrayOfByte1.length;
    } 
    int i3;
    for (i3 = 0; i3 <= m; i3++)
      arrayOfByte[i1++] = (byte)m; 
    for (i3 = i2; i3 < i1; i3 += i)
      this.encryptCipher.processBlock(arrayOfByte, i3, arrayOfByte, i3); 
    if (this.encryptThenMAC) {
      byte[] arrayOfByte1 = this.writeMac.calculateMac(paramLong, paramShort, arrayOfByte, 0, i1);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, i1, arrayOfByte1.length);
      i1 += arrayOfByte1.length;
    } 
    return arrayOfByte;
  }
  
  public byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = this.decryptCipher.getBlockSize();
    int j = this.readMac.getSize();
    int k = i;
    if (this.encryptThenMAC) {
      k += j;
    } else {
      k = Math.max(k, j + 1);
    } 
    if (this.useExplicitIV)
      k += i; 
    if (paramInt2 < k)
      throw new TlsFatalAlert((short)50); 
    int m = paramInt2;
    if (this.encryptThenMAC)
      m -= j; 
    if (m % i != 0)
      throw new TlsFatalAlert((short)21); 
    if (this.encryptThenMAC) {
      int i3 = paramInt1 + paramInt2;
      byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfbyte, i3 - j, i3);
      byte[] arrayOfByte2 = this.readMac.calculateMac(paramLong, paramShort, paramArrayOfbyte, paramInt1, paramInt2 - j);
      boolean bool = !Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte1) ? true : false;
      if (bool)
        throw new TlsFatalAlert((short)20); 
    } 
    if (this.useExplicitIV) {
      this.decryptCipher.init(false, (CipherParameters)new ParametersWithIV(null, paramArrayOfbyte, paramInt1, i));
      paramInt1 += i;
      m -= i;
    } 
    int n;
    for (n = 0; n < m; n += i)
      this.decryptCipher.processBlock(paramArrayOfbyte, paramInt1 + n, paramArrayOfbyte, paramInt1 + n); 
    n = checkPaddingConstantTime(paramArrayOfbyte, paramInt1, m, i, this.encryptThenMAC ? 0 : j);
    int i1 = (n == 0) ? 1 : 0;
    int i2 = m - n;
    if (!this.encryptThenMAC) {
      i2 -= j;
      int i3 = i2;
      int i4 = paramInt1 + i3;
      byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfbyte, i4, i4 + j);
      byte[] arrayOfByte2 = this.readMac.calculateMacConstantTime(paramLong, paramShort, paramArrayOfbyte, paramInt1, i3, m - j, this.randomData);
      i1 |= !Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte1) ? 1 : 0;
    } 
    if (i1 != 0)
      throw new TlsFatalAlert((short)20); 
    return Arrays.copyOfRange(paramArrayOfbyte, paramInt1, paramInt1 + i2);
  }
  
  protected int checkPaddingConstantTime(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    // Byte code:
    //   0: iload_2
    //   1: iload_3
    //   2: iadd
    //   3: istore #6
    //   5: aload_1
    //   6: iload #6
    //   8: iconst_1
    //   9: isub
    //   10: baload
    //   11: istore #7
    //   13: iload #7
    //   15: sipush #255
    //   18: iand
    //   19: istore #8
    //   21: iload #8
    //   23: iconst_1
    //   24: iadd
    //   25: istore #9
    //   27: iconst_0
    //   28: istore #10
    //   30: iconst_0
    //   31: istore #11
    //   33: aload_0
    //   34: getfield context : Lorg/bouncycastle/crypto/tls/TlsContext;
    //   37: invokestatic isSSL : (Lorg/bouncycastle/crypto/tls/TlsContext;)Z
    //   40: ifeq -> 50
    //   43: iload #9
    //   45: iload #4
    //   47: if_icmpgt -> 59
    //   50: iload #5
    //   52: iload #9
    //   54: iadd
    //   55: iload_3
    //   56: if_icmple -> 65
    //   59: iconst_0
    //   60: istore #9
    //   62: goto -> 107
    //   65: iload #6
    //   67: iload #9
    //   69: isub
    //   70: istore #12
    //   72: iload #11
    //   74: aload_1
    //   75: iload #12
    //   77: iinc #12, 1
    //   80: baload
    //   81: iload #7
    //   83: ixor
    //   84: ior
    //   85: i2b
    //   86: istore #11
    //   88: iload #12
    //   90: iload #6
    //   92: if_icmplt -> 72
    //   95: iload #9
    //   97: istore #10
    //   99: iload #11
    //   101: ifeq -> 107
    //   104: iconst_0
    //   105: istore #9
    //   107: aload_0
    //   108: getfield randomData : [B
    //   111: astore #12
    //   113: iload #10
    //   115: sipush #256
    //   118: if_icmpge -> 141
    //   121: iload #11
    //   123: aload #12
    //   125: iload #10
    //   127: iinc #10, 1
    //   130: baload
    //   131: iload #7
    //   133: ixor
    //   134: ior
    //   135: i2b
    //   136: istore #11
    //   138: goto -> 113
    //   141: aload #12
    //   143: iconst_0
    //   144: dup2
    //   145: baload
    //   146: iload #11
    //   148: ixor
    //   149: i2b
    //   150: bastore
    //   151: iload #9
    //   153: ireturn
  }
  
  protected int chooseExtraPadBlocks(SecureRandom paramSecureRandom, int paramInt) {
    int i = paramSecureRandom.nextInt();
    int j = lowestBitSet(i);
    return Math.min(j, paramInt);
  }
  
  protected int lowestBitSet(int paramInt) {
    if (paramInt == 0)
      return 32; 
    byte b = 0;
    while ((paramInt & 0x1) == 0) {
      b++;
      paramInt >>= 1;
    } 
    return b;
  }
}
