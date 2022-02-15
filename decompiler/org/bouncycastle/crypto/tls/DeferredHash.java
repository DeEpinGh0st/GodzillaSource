package org.bouncycastle.crypto.tls;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Shorts;

class DeferredHash implements TlsHandshakeHash {
  protected static final int BUFFERING_HASH_LIMIT = 4;
  
  protected TlsContext context;
  
  private DigestInputBuffer buf = new DigestInputBuffer();
  
  private Hashtable hashes = new Hashtable<Object, Object>();
  
  private Short prfHashAlgorithm = null;
  
  DeferredHash() {}
  
  private DeferredHash(Short paramShort, Digest paramDigest) {
    this.hashes.put(paramShort, paramDigest);
  }
  
  public void init(TlsContext paramTlsContext) {
    this.context = paramTlsContext;
  }
  
  public TlsHandshakeHash notifyPRFDetermined() {
    int i = this.context.getSecurityParameters().getPrfAlgorithm();
    if (i == 0) {
      CombinedHash combinedHash = new CombinedHash();
      combinedHash.init(this.context);
      this.buf.updateDigest(combinedHash);
      return combinedHash.notifyPRFDetermined();
    } 
    this.prfHashAlgorithm = Shorts.valueOf(TlsUtils.getHashAlgorithmForPRFAlgorithm(i));
    checkTrackingHash(this.prfHashAlgorithm);
    return this;
  }
  
  public void trackHashAlgorithm(short paramShort) {
    if (this.buf == null)
      throw new IllegalStateException("Too late to track more hash algorithms"); 
    checkTrackingHash(Shorts.valueOf(paramShort));
  }
  
  public void sealHashAlgorithms() {
    checkStopBuffering();
  }
  
  public TlsHandshakeHash stopTracking() {
    Digest digest = TlsUtils.cloneHash(this.prfHashAlgorithm.shortValue(), (Digest)this.hashes.get(this.prfHashAlgorithm));
    if (this.buf != null)
      this.buf.updateDigest(digest); 
    DeferredHash deferredHash = new DeferredHash(this.prfHashAlgorithm, digest);
    deferredHash.init(this.context);
    return deferredHash;
  }
  
  public Digest forkPRFHash() {
    checkStopBuffering();
    if (this.buf != null) {
      Digest digest = TlsUtils.createHash(this.prfHashAlgorithm.shortValue());
      this.buf.updateDigest(digest);
      return digest;
    } 
    return TlsUtils.cloneHash(this.prfHashAlgorithm.shortValue(), (Digest)this.hashes.get(this.prfHashAlgorithm));
  }
  
  public byte[] getFinalHash(short paramShort) {
    Digest digest = (Digest)this.hashes.get(Shorts.valueOf(paramShort));
    if (digest == null)
      throw new IllegalStateException("HashAlgorithm." + HashAlgorithm.getText(paramShort) + " is not being tracked"); 
    digest = TlsUtils.cloneHash(paramShort, digest);
    if (this.buf != null)
      this.buf.updateDigest(digest); 
    byte[] arrayOfByte = new byte[digest.getDigestSize()];
    digest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public String getAlgorithmName() {
    throw new IllegalStateException("Use fork() to get a definite Digest");
  }
  
  public int getDigestSize() {
    throw new IllegalStateException("Use fork() to get a definite Digest");
  }
  
  public void update(byte paramByte) {
    if (this.buf != null) {
      this.buf.write(paramByte);
      return;
    } 
    Enumeration<Digest> enumeration = this.hashes.elements();
    while (enumeration.hasMoreElements()) {
      Digest digest = enumeration.nextElement();
      digest.update(paramByte);
    } 
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.buf != null) {
      this.buf.write(paramArrayOfbyte, paramInt1, paramInt2);
      return;
    } 
    Enumeration<Digest> enumeration = this.hashes.elements();
    while (enumeration.hasMoreElements()) {
      Digest digest = enumeration.nextElement();
      digest.update(paramArrayOfbyte, paramInt1, paramInt2);
    } 
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    throw new IllegalStateException("Use fork() to get a definite Digest");
  }
  
  public void reset() {
    if (this.buf != null) {
      this.buf.reset();
      return;
    } 
    Enumeration<Digest> enumeration = this.hashes.elements();
    while (enumeration.hasMoreElements()) {
      Digest digest = enumeration.nextElement();
      digest.reset();
    } 
  }
  
  protected void checkStopBuffering() {
    if (this.buf != null && this.hashes.size() <= 4) {
      Enumeration<Digest> enumeration = this.hashes.elements();
      while (enumeration.hasMoreElements()) {
        Digest digest = enumeration.nextElement();
        this.buf.updateDigest(digest);
      } 
      this.buf = null;
    } 
  }
  
  protected void checkTrackingHash(Short paramShort) {
    if (!this.hashes.containsKey(paramShort)) {
      Digest digest = TlsUtils.createHash(paramShort.shortValue());
      this.hashes.put(paramShort, digest);
    } 
  }
}
