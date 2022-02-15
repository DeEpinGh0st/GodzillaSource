package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.util.Times;

abstract class AbstractTlsContext implements TlsContext {
  private static long counter = Times.nanoTime();
  
  private RandomGenerator nonceRandom;
  
  private SecureRandom secureRandom;
  
  private SecurityParameters securityParameters;
  
  private ProtocolVersion clientVersion = null;
  
  private ProtocolVersion serverVersion = null;
  
  private TlsSession session = null;
  
  private Object userObject = null;
  
  private static synchronized long nextCounterValue() {
    return ++counter;
  }
  
  AbstractTlsContext(SecureRandom paramSecureRandom, SecurityParameters paramSecurityParameters) {
    Digest digest = TlsUtils.createHash((short)4);
    byte[] arrayOfByte = new byte[digest.getDigestSize()];
    paramSecureRandom.nextBytes(arrayOfByte);
    this.nonceRandom = (RandomGenerator)new DigestRandomGenerator(digest);
    this.nonceRandom.addSeedMaterial(nextCounterValue());
    this.nonceRandom.addSeedMaterial(Times.nanoTime());
    this.nonceRandom.addSeedMaterial(arrayOfByte);
    this.secureRandom = paramSecureRandom;
    this.securityParameters = paramSecurityParameters;
  }
  
  public RandomGenerator getNonceRandomGenerator() {
    return this.nonceRandom;
  }
  
  public SecureRandom getSecureRandom() {
    return this.secureRandom;
  }
  
  public SecurityParameters getSecurityParameters() {
    return this.securityParameters;
  }
  
  public ProtocolVersion getClientVersion() {
    return this.clientVersion;
  }
  
  void setClientVersion(ProtocolVersion paramProtocolVersion) {
    this.clientVersion = paramProtocolVersion;
  }
  
  public ProtocolVersion getServerVersion() {
    return this.serverVersion;
  }
  
  void setServerVersion(ProtocolVersion paramProtocolVersion) {
    this.serverVersion = paramProtocolVersion;
  }
  
  public TlsSession getResumableSession() {
    return this.session;
  }
  
  void setResumableSession(TlsSession paramTlsSession) {
    this.session = paramTlsSession;
  }
  
  public Object getUserObject() {
    return this.userObject;
  }
  
  public void setUserObject(Object paramObject) {
    this.userObject = paramObject;
  }
  
  public byte[] exportKeyingMaterial(String paramString, byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte != null && !TlsUtils.isValidUint16(paramArrayOfbyte.length))
      throw new IllegalArgumentException("'context_value' must have length less than 2^16 (or be null)"); 
    SecurityParameters securityParameters = getSecurityParameters();
    byte[] arrayOfByte1 = securityParameters.getClientRandom();
    byte[] arrayOfByte2 = securityParameters.getServerRandom();
    int i = arrayOfByte1.length + arrayOfByte2.length;
    if (paramArrayOfbyte != null)
      i += 2 + paramArrayOfbyte.length; 
    byte[] arrayOfByte3 = new byte[i];
    int j = 0;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, j, arrayOfByte1.length);
    j += arrayOfByte1.length;
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, j, arrayOfByte2.length);
    j += arrayOfByte2.length;
    if (paramArrayOfbyte != null) {
      TlsUtils.writeUint16(paramArrayOfbyte.length, arrayOfByte3, j);
      j += 2;
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte3, j, paramArrayOfbyte.length);
      j += paramArrayOfbyte.length;
    } 
    if (j != i)
      throw new IllegalStateException("error in calculation of seed for export"); 
    return TlsUtils.PRF(this, securityParameters.getMasterSecret(), paramString, arrayOfByte3, paramInt);
  }
}
