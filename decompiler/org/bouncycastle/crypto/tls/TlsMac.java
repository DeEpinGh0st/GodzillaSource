package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class TlsMac {
  protected TlsContext context;
  
  protected byte[] secret;
  
  protected Mac mac;
  
  protected int digestBlockSize;
  
  protected int digestOverhead;
  
  protected int macLength;
  
  public TlsMac(TlsContext paramTlsContext, Digest paramDigest, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.context = paramTlsContext;
    KeyParameter keyParameter = new KeyParameter(paramArrayOfbyte, paramInt1, paramInt2);
    this.secret = Arrays.clone(keyParameter.getKey());
    if (paramDigest instanceof org.bouncycastle.crypto.digests.LongDigest) {
      this.digestBlockSize = 128;
      this.digestOverhead = 16;
    } else {
      this.digestBlockSize = 64;
      this.digestOverhead = 8;
    } 
    if (TlsUtils.isSSL(paramTlsContext)) {
      this.mac = new SSL3Mac(paramDigest);
      if (paramDigest.getDigestSize() == 20)
        this.digestOverhead = 4; 
    } else {
      this.mac = (Mac)new HMac(paramDigest);
    } 
    this.mac.init((CipherParameters)keyParameter);
    this.macLength = this.mac.getMacSize();
    if ((paramTlsContext.getSecurityParameters()).truncatedHMac)
      this.macLength = Math.min(this.macLength, 10); 
  }
  
  public byte[] getMACSecret() {
    return this.secret;
  }
  
  public int getSize() {
    return this.macLength;
  }
  
  public byte[] calculateMac(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    ProtocolVersion protocolVersion = this.context.getServerVersion();
    boolean bool = protocolVersion.isSSL();
    byte[] arrayOfByte1 = new byte[bool ? 11 : 13];
    TlsUtils.writeUint64(paramLong, arrayOfByte1, 0);
    TlsUtils.writeUint8(paramShort, arrayOfByte1, 8);
    if (!bool)
      TlsUtils.writeVersion(protocolVersion, arrayOfByte1, 9); 
    TlsUtils.writeUint16(paramInt2, arrayOfByte1, arrayOfByte1.length - 2);
    this.mac.update(arrayOfByte1, 0, arrayOfByte1.length);
    this.mac.update(paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte2 = new byte[this.mac.getMacSize()];
    this.mac.doFinal(arrayOfByte2, 0);
    return truncate(arrayOfByte2);
  }
  
  public byte[] calculateMacConstantTime(long paramLong, short paramShort, byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = calculateMac(paramLong, paramShort, paramArrayOfbyte1, paramInt1, paramInt2);
    byte b = TlsUtils.isSSL(this.context) ? 11 : 13;
    int i = getDigestBlockCount(b + paramInt3) - getDigestBlockCount(b + paramInt2);
    while (--i >= 0)
      this.mac.update(paramArrayOfbyte2, 0, this.digestBlockSize); 
    this.mac.update(paramArrayOfbyte2[0]);
    this.mac.reset();
    return arrayOfByte;
  }
  
  protected int getDigestBlockCount(int paramInt) {
    return (paramInt + this.digestOverhead) / this.digestBlockSize;
  }
  
  protected byte[] truncate(byte[] paramArrayOfbyte) {
    return (paramArrayOfbyte.length <= this.macLength) ? paramArrayOfbyte : Arrays.copyOf(paramArrayOfbyte, this.macLength);
  }
}
