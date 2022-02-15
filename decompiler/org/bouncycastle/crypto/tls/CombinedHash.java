package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;

class CombinedHash implements TlsHandshakeHash {
  protected TlsContext context;
  
  protected Digest md5;
  
  protected Digest sha1;
  
  CombinedHash() {
    this.md5 = TlsUtils.createHash((short)1);
    this.sha1 = TlsUtils.createHash((short)2);
  }
  
  CombinedHash(CombinedHash paramCombinedHash) {
    this.context = paramCombinedHash.context;
    this.md5 = TlsUtils.cloneHash((short)1, paramCombinedHash.md5);
    this.sha1 = TlsUtils.cloneHash((short)2, paramCombinedHash.sha1);
  }
  
  public void init(TlsContext paramTlsContext) {
    this.context = paramTlsContext;
  }
  
  public TlsHandshakeHash notifyPRFDetermined() {
    return this;
  }
  
  public void trackHashAlgorithm(short paramShort) {
    throw new IllegalStateException("CombinedHash only supports calculating the legacy PRF for handshake hash");
  }
  
  public void sealHashAlgorithms() {}
  
  public TlsHandshakeHash stopTracking() {
    return new CombinedHash(this);
  }
  
  public Digest forkPRFHash() {
    return new CombinedHash(this);
  }
  
  public byte[] getFinalHash(short paramShort) {
    throw new IllegalStateException("CombinedHash doesn't support multiple hashes");
  }
  
  public String getAlgorithmName() {
    return this.md5.getAlgorithmName() + " and " + this.sha1.getAlgorithmName();
  }
  
  public int getDigestSize() {
    return this.md5.getDigestSize() + this.sha1.getDigestSize();
  }
  
  public void update(byte paramByte) {
    this.md5.update(paramByte);
    this.sha1.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.md5.update(paramArrayOfbyte, paramInt1, paramInt2);
    this.sha1.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    if (this.context != null && TlsUtils.isSSL(this.context)) {
      ssl3Complete(this.md5, SSL3Mac.IPAD, SSL3Mac.OPAD, 48);
      ssl3Complete(this.sha1, SSL3Mac.IPAD, SSL3Mac.OPAD, 40);
    } 
    int i = this.md5.doFinal(paramArrayOfbyte, paramInt);
    int j = this.sha1.doFinal(paramArrayOfbyte, paramInt + i);
    return i + j;
  }
  
  public void reset() {
    this.md5.reset();
    this.sha1.reset();
  }
  
  protected void ssl3Complete(Digest paramDigest, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    byte[] arrayOfByte1 = (this.context.getSecurityParameters()).masterSecret;
    paramDigest.update(arrayOfByte1, 0, arrayOfByte1.length);
    paramDigest.update(paramArrayOfbyte1, 0, paramInt);
    byte[] arrayOfByte2 = new byte[paramDigest.getDigestSize()];
    paramDigest.doFinal(arrayOfByte2, 0);
    paramDigest.update(arrayOfByte1, 0, arrayOfByte1.length);
    paramDigest.update(paramArrayOfbyte2, 0, paramInt);
    paramDigest.update(arrayOfByte2, 0, arrayOfByte2.length);
  }
}
