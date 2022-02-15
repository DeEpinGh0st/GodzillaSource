package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class TlsStreamCipher implements TlsCipher {
  protected TlsContext context;
  
  protected StreamCipher encryptCipher;
  
  protected StreamCipher decryptCipher;
  
  protected TlsMac writeMac;
  
  protected TlsMac readMac;
  
  protected boolean usesNonce;
  
  public TlsStreamCipher(TlsContext paramTlsContext, StreamCipher paramStreamCipher1, StreamCipher paramStreamCipher2, Digest paramDigest1, Digest paramDigest2, int paramInt, boolean paramBoolean) throws IOException {
    KeyParameter keyParameter3;
    ParametersWithIV parametersWithIV1;
    KeyParameter keyParameter4;
    ParametersWithIV parametersWithIV2;
    boolean bool = paramTlsContext.isServer();
    this.context = paramTlsContext;
    this.usesNonce = paramBoolean;
    this.encryptCipher = paramStreamCipher1;
    this.decryptCipher = paramStreamCipher2;
    int i = 2 * paramInt + paramDigest1.getDigestSize() + paramDigest2.getDigestSize();
    byte[] arrayOfByte = TlsUtils.calculateKeyBlock(paramTlsContext, i);
    int j = 0;
    TlsMac tlsMac1 = new TlsMac(paramTlsContext, paramDigest1, arrayOfByte, j, paramDigest1.getDigestSize());
    j += paramDigest1.getDigestSize();
    TlsMac tlsMac2 = new TlsMac(paramTlsContext, paramDigest2, arrayOfByte, j, paramDigest2.getDigestSize());
    j += paramDigest2.getDigestSize();
    KeyParameter keyParameter1 = new KeyParameter(arrayOfByte, j, paramInt);
    j += paramInt;
    KeyParameter keyParameter2 = new KeyParameter(arrayOfByte, j, paramInt);
    j += paramInt;
    if (j != i)
      throw new TlsFatalAlert((short)80); 
    if (bool) {
      this.writeMac = tlsMac2;
      this.readMac = tlsMac1;
      this.encryptCipher = paramStreamCipher2;
      this.decryptCipher = paramStreamCipher1;
      keyParameter3 = keyParameter2;
      keyParameter4 = keyParameter1;
    } else {
      this.writeMac = tlsMac1;
      this.readMac = tlsMac2;
      this.encryptCipher = paramStreamCipher1;
      this.decryptCipher = paramStreamCipher2;
      keyParameter3 = keyParameter1;
      keyParameter4 = keyParameter2;
    } 
    if (paramBoolean) {
      byte[] arrayOfByte1 = new byte[8];
      parametersWithIV1 = new ParametersWithIV((CipherParameters)keyParameter3, arrayOfByte1);
      parametersWithIV2 = new ParametersWithIV((CipherParameters)keyParameter4, arrayOfByte1);
    } 
    this.encryptCipher.init(true, (CipherParameters)parametersWithIV1);
    this.decryptCipher.init(false, (CipherParameters)parametersWithIV2);
  }
  
  public int getPlaintextLimit(int paramInt) {
    return paramInt - this.writeMac.getSize();
  }
  
  public byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.usesNonce)
      updateIV(this.encryptCipher, true, paramLong); 
    byte[] arrayOfByte1 = new byte[paramInt2 + this.writeMac.getSize()];
    this.encryptCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte1, 0);
    byte[] arrayOfByte2 = this.writeMac.calculateMac(paramLong, paramShort, paramArrayOfbyte, paramInt1, paramInt2);
    this.encryptCipher.processBytes(arrayOfByte2, 0, arrayOfByte2.length, arrayOfByte1, paramInt2);
    return arrayOfByte1;
  }
  
  public byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.usesNonce)
      updateIV(this.decryptCipher, false, paramLong); 
    int i = this.readMac.getSize();
    if (paramInt2 < i)
      throw new TlsFatalAlert((short)50); 
    int j = paramInt2 - i;
    byte[] arrayOfByte = new byte[paramInt2];
    this.decryptCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
    checkMAC(paramLong, paramShort, arrayOfByte, j, paramInt2, arrayOfByte, 0, j);
    return Arrays.copyOfRange(arrayOfByte, 0, j);
  }
  
  protected void checkMAC(long paramLong, short paramShort, byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3, int paramInt4) throws IOException {
    byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfbyte1, paramInt1, paramInt2);
    byte[] arrayOfByte2 = this.readMac.calculateMac(paramLong, paramShort, paramArrayOfbyte2, paramInt3, paramInt4);
    if (!Arrays.constantTimeAreEqual(arrayOfByte1, arrayOfByte2))
      throw new TlsFatalAlert((short)20); 
  }
  
  protected void updateIV(StreamCipher paramStreamCipher, boolean paramBoolean, long paramLong) {
    byte[] arrayOfByte = new byte[8];
    TlsUtils.writeUint64(paramLong, arrayOfByte, 0);
    paramStreamCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV(null, arrayOfByte));
  }
}
