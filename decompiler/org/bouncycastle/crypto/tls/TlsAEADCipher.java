package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class TlsAEADCipher implements TlsCipher {
  public static final int NONCE_RFC5288 = 1;
  
  static final int NONCE_DRAFT_CHACHA20_POLY1305 = 2;
  
  protected TlsContext context;
  
  protected int macSize;
  
  protected int record_iv_length;
  
  protected AEADBlockCipher encryptCipher;
  
  protected AEADBlockCipher decryptCipher;
  
  protected byte[] encryptImplicitNonce;
  
  protected byte[] decryptImplicitNonce;
  
  protected int nonceMode;
  
  public TlsAEADCipher(TlsContext paramTlsContext, AEADBlockCipher paramAEADBlockCipher1, AEADBlockCipher paramAEADBlockCipher2, int paramInt1, int paramInt2) throws IOException {
    this(paramTlsContext, paramAEADBlockCipher1, paramAEADBlockCipher2, paramInt1, paramInt2, 1);
  }
  
  TlsAEADCipher(TlsContext paramTlsContext, AEADBlockCipher paramAEADBlockCipher1, AEADBlockCipher paramAEADBlockCipher2, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    byte b;
    KeyParameter keyParameter3;
    KeyParameter keyParameter4;
    if (!TlsUtils.isTLSv12(paramTlsContext))
      throw new TlsFatalAlert((short)80); 
    this.nonceMode = paramInt3;
    switch (paramInt3) {
      case 1:
        b = 4;
        this.record_iv_length = 8;
        break;
      case 2:
        b = 12;
        this.record_iv_length = 0;
        break;
      default:
        throw new TlsFatalAlert((short)80);
    } 
    this.context = paramTlsContext;
    this.macSize = paramInt2;
    int i = 2 * paramInt1 + 2 * b;
    byte[] arrayOfByte1 = TlsUtils.calculateKeyBlock(paramTlsContext, i);
    int j = 0;
    KeyParameter keyParameter1 = new KeyParameter(arrayOfByte1, j, paramInt1);
    j += paramInt1;
    KeyParameter keyParameter2 = new KeyParameter(arrayOfByte1, j, paramInt1);
    j += paramInt1;
    byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, j, j + b);
    j += b;
    byte[] arrayOfByte3 = Arrays.copyOfRange(arrayOfByte1, j, j + b);
    j += b;
    if (j != i)
      throw new TlsFatalAlert((short)80); 
    if (paramTlsContext.isServer()) {
      this.encryptCipher = paramAEADBlockCipher2;
      this.decryptCipher = paramAEADBlockCipher1;
      this.encryptImplicitNonce = arrayOfByte3;
      this.decryptImplicitNonce = arrayOfByte2;
      keyParameter3 = keyParameter2;
      keyParameter4 = keyParameter1;
    } else {
      this.encryptCipher = paramAEADBlockCipher1;
      this.decryptCipher = paramAEADBlockCipher2;
      this.encryptImplicitNonce = arrayOfByte2;
      this.decryptImplicitNonce = arrayOfByte3;
      keyParameter3 = keyParameter1;
      keyParameter4 = keyParameter2;
    } 
    byte[] arrayOfByte4 = new byte[b + this.record_iv_length];
    this.encryptCipher.init(true, (CipherParameters)new AEADParameters(keyParameter3, 8 * paramInt2, arrayOfByte4));
    this.decryptCipher.init(false, (CipherParameters)new AEADParameters(keyParameter4, 8 * paramInt2, arrayOfByte4));
  }
  
  public int getPlaintextLimit(int paramInt) {
    return paramInt - this.macSize - this.record_iv_length;
  }
  
  public byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    byte[] arrayOfByte1 = new byte[this.encryptImplicitNonce.length + this.record_iv_length];
    switch (this.nonceMode) {
      case 1:
        System.arraycopy(this.encryptImplicitNonce, 0, arrayOfByte1, 0, this.encryptImplicitNonce.length);
        TlsUtils.writeUint64(paramLong, arrayOfByte1, this.encryptImplicitNonce.length);
        break;
      case 2:
        TlsUtils.writeUint64(paramLong, arrayOfByte1, arrayOfByte1.length - 8);
        for (i = 0; i < this.encryptImplicitNonce.length; i++)
          arrayOfByte1[i] = (byte)(arrayOfByte1[i] ^ this.encryptImplicitNonce[i]); 
        break;
      default:
        throw new TlsFatalAlert((short)80);
    } 
    int i = paramInt1;
    int j = paramInt2;
    int k = this.encryptCipher.getOutputSize(j);
    byte[] arrayOfByte2 = new byte[this.record_iv_length + k];
    if (this.record_iv_length != 0)
      System.arraycopy(arrayOfByte1, arrayOfByte1.length - this.record_iv_length, arrayOfByte2, 0, this.record_iv_length); 
    int m = this.record_iv_length;
    byte[] arrayOfByte3 = getAdditionalData(paramLong, paramShort, j);
    AEADParameters aEADParameters = new AEADParameters(null, 8 * this.macSize, arrayOfByte1, arrayOfByte3);
    try {
      this.encryptCipher.init(true, (CipherParameters)aEADParameters);
      m += this.encryptCipher.processBytes(paramArrayOfbyte, i, j, arrayOfByte2, m);
      m += this.encryptCipher.doFinal(arrayOfByte2, m);
    } catch (Exception exception) {
      throw new TlsFatalAlert((short)80, exception);
    } 
    if (m != arrayOfByte2.length)
      throw new TlsFatalAlert((short)80); 
    return arrayOfByte2;
  }
  
  public byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (getPlaintextLimit(paramInt2) < 0)
      throw new TlsFatalAlert((short)50); 
    byte[] arrayOfByte1 = new byte[this.decryptImplicitNonce.length + this.record_iv_length];
    switch (this.nonceMode) {
      case 1:
        System.arraycopy(this.decryptImplicitNonce, 0, arrayOfByte1, 0, this.decryptImplicitNonce.length);
        System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte1, arrayOfByte1.length - this.record_iv_length, this.record_iv_length);
        break;
      case 2:
        TlsUtils.writeUint64(paramLong, arrayOfByte1, arrayOfByte1.length - 8);
        for (i = 0; i < this.decryptImplicitNonce.length; i++)
          arrayOfByte1[i] = (byte)(arrayOfByte1[i] ^ this.decryptImplicitNonce[i]); 
        break;
      default:
        throw new TlsFatalAlert((short)80);
    } 
    int i = paramInt1 + this.record_iv_length;
    int j = paramInt2 - this.record_iv_length;
    int k = this.decryptCipher.getOutputSize(j);
    byte[] arrayOfByte2 = new byte[k];
    int m = 0;
    byte[] arrayOfByte3 = getAdditionalData(paramLong, paramShort, k);
    AEADParameters aEADParameters = new AEADParameters(null, 8 * this.macSize, arrayOfByte1, arrayOfByte3);
    try {
      this.decryptCipher.init(false, (CipherParameters)aEADParameters);
      m += this.decryptCipher.processBytes(paramArrayOfbyte, i, j, arrayOfByte2, m);
      m += this.decryptCipher.doFinal(arrayOfByte2, m);
    } catch (Exception exception) {
      throw new TlsFatalAlert((short)20, exception);
    } 
    if (m != arrayOfByte2.length)
      throw new TlsFatalAlert((short)80); 
    return arrayOfByte2;
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
