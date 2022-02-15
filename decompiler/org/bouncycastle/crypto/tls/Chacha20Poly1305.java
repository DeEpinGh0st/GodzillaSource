package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Chacha20Poly1305 implements TlsCipher {
  private static final byte[] ZEROES = new byte[15];
  
  protected TlsContext context;
  
  protected ChaCha7539Engine encryptCipher;
  
  protected ChaCha7539Engine decryptCipher;
  
  protected byte[] encryptIV;
  
  protected byte[] decryptIV;
  
  public Chacha20Poly1305(TlsContext paramTlsContext) throws IOException {
    KeyParameter keyParameter3;
    KeyParameter keyParameter4;
    if (!TlsUtils.isTLSv12(paramTlsContext))
      throw new TlsFatalAlert((short)80); 
    this.context = paramTlsContext;
    byte b1 = 32;
    byte b2 = 12;
    int i = 2 * b1 + 2 * b2;
    byte[] arrayOfByte1 = TlsUtils.calculateKeyBlock(paramTlsContext, i);
    int j = 0;
    KeyParameter keyParameter1 = new KeyParameter(arrayOfByte1, j, b1);
    j += b1;
    KeyParameter keyParameter2 = new KeyParameter(arrayOfByte1, j, b1);
    j += b1;
    byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, j, j + b2);
    j += b2;
    byte[] arrayOfByte3 = Arrays.copyOfRange(arrayOfByte1, j, j + b2);
    j += b2;
    if (j != i)
      throw new TlsFatalAlert((short)80); 
    this.encryptCipher = new ChaCha7539Engine();
    this.decryptCipher = new ChaCha7539Engine();
    if (paramTlsContext.isServer()) {
      keyParameter3 = keyParameter2;
      keyParameter4 = keyParameter1;
      this.encryptIV = arrayOfByte3;
      this.decryptIV = arrayOfByte2;
    } else {
      keyParameter3 = keyParameter1;
      keyParameter4 = keyParameter2;
      this.encryptIV = arrayOfByte2;
      this.decryptIV = arrayOfByte3;
    } 
    this.encryptCipher.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)keyParameter3, this.encryptIV));
    this.decryptCipher.init(false, (CipherParameters)new ParametersWithIV((CipherParameters)keyParameter4, this.decryptIV));
  }
  
  public int getPlaintextLimit(int paramInt) {
    return paramInt - 16;
  }
  
  public byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    KeyParameter keyParameter = initRecord((StreamCipher)this.encryptCipher, true, paramLong, this.encryptIV);
    byte[] arrayOfByte1 = new byte[paramInt2 + 16];
    this.encryptCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte1, 0);
    byte[] arrayOfByte2 = getAdditionalData(paramLong, paramShort, paramInt2);
    byte[] arrayOfByte3 = calculateRecordMAC(keyParameter, arrayOfByte2, arrayOfByte1, 0, paramInt2);
    System.arraycopy(arrayOfByte3, 0, arrayOfByte1, paramInt2, arrayOfByte3.length);
    return arrayOfByte1;
  }
  
  public byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (getPlaintextLimit(paramInt2) < 0)
      throw new TlsFatalAlert((short)50); 
    KeyParameter keyParameter = initRecord((StreamCipher)this.decryptCipher, false, paramLong, this.decryptIV);
    int i = paramInt2 - 16;
    byte[] arrayOfByte1 = getAdditionalData(paramLong, paramShort, i);
    byte[] arrayOfByte2 = calculateRecordMAC(keyParameter, arrayOfByte1, paramArrayOfbyte, paramInt1, i);
    byte[] arrayOfByte3 = Arrays.copyOfRange(paramArrayOfbyte, paramInt1 + i, paramInt1 + paramInt2);
    if (!Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte3))
      throw new TlsFatalAlert((short)20); 
    byte[] arrayOfByte4 = new byte[i];
    this.decryptCipher.processBytes(paramArrayOfbyte, paramInt1, i, arrayOfByte4, 0);
    return arrayOfByte4;
  }
  
  protected KeyParameter initRecord(StreamCipher paramStreamCipher, boolean paramBoolean, long paramLong, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = calculateNonce(paramLong, paramArrayOfbyte);
    paramStreamCipher.init(paramBoolean, (CipherParameters)new ParametersWithIV(null, arrayOfByte));
    return generateRecordMACKey(paramStreamCipher);
  }
  
  protected byte[] calculateNonce(long paramLong, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[12];
    TlsUtils.writeUint64(paramLong, arrayOfByte, 4);
    for (byte b = 0; b < 12; b++)
      arrayOfByte[b] = (byte)(arrayOfByte[b] ^ paramArrayOfbyte[b]); 
    return arrayOfByte;
  }
  
  protected KeyParameter generateRecordMACKey(StreamCipher paramStreamCipher) {
    byte[] arrayOfByte = new byte[64];
    paramStreamCipher.processBytes(arrayOfByte, 0, arrayOfByte.length, arrayOfByte, 0);
    KeyParameter keyParameter = new KeyParameter(arrayOfByte, 0, 32);
    Arrays.fill(arrayOfByte, (byte)0);
    return keyParameter;
  }
  
  protected byte[] calculateRecordMAC(KeyParameter paramKeyParameter, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
    Poly1305 poly1305 = new Poly1305();
    poly1305.init((CipherParameters)paramKeyParameter);
    updateRecordMACText((Mac)poly1305, paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    updateRecordMACText((Mac)poly1305, paramArrayOfbyte2, paramInt1, paramInt2);
    updateRecordMACLength((Mac)poly1305, paramArrayOfbyte1.length);
    updateRecordMACLength((Mac)poly1305, paramInt2);
    byte[] arrayOfByte = new byte[poly1305.getMacSize()];
    poly1305.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  protected void updateRecordMACLength(Mac paramMac, int paramInt) {
    byte[] arrayOfByte = Pack.longToLittleEndian(paramInt & 0xFFFFFFFFL);
    paramMac.update(arrayOfByte, 0, arrayOfByte.length);
  }
  
  protected void updateRecordMACText(Mac paramMac, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    paramMac.update(paramArrayOfbyte, paramInt1, paramInt2);
    int i = paramInt2 % 16;
    if (i != 0)
      paramMac.update(ZEROES, 0, 16 - i); 
  }
  
  protected byte[] getAdditionalData(long paramLong, short paramShort, int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[13];
    TlsUtils.writeUint64(paramLong, arrayOfByte, 0);
    TlsUtils.writeUint8(paramShort, arrayOfByte, 8);
    TlsUtils.writeVersion(this.context.getServerVersion(), arrayOfByte, 9);
    TlsUtils.writeUint16(paramInt, arrayOfByte, 11);
    return arrayOfByte;
  }
}
