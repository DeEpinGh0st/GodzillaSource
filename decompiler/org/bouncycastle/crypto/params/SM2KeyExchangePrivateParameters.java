package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

public class SM2KeyExchangePrivateParameters implements CipherParameters {
  private final boolean initiator;
  
  private final ECPrivateKeyParameters staticPrivateKey;
  
  private final ECPoint staticPublicPoint;
  
  private final ECPrivateKeyParameters ephemeralPrivateKey;
  
  private final ECPoint ephemeralPublicPoint;
  
  public SM2KeyExchangePrivateParameters(boolean paramBoolean, ECPrivateKeyParameters paramECPrivateKeyParameters1, ECPrivateKeyParameters paramECPrivateKeyParameters2) {
    if (paramECPrivateKeyParameters1 == null)
      throw new NullPointerException("staticPrivateKey cannot be null"); 
    if (paramECPrivateKeyParameters2 == null)
      throw new NullPointerException("ephemeralPrivateKey cannot be null"); 
    ECDomainParameters eCDomainParameters = paramECPrivateKeyParameters1.getParameters();
    if (!eCDomainParameters.equals(paramECPrivateKeyParameters2.getParameters()))
      throw new IllegalArgumentException("Static and ephemeral private keys have different domain parameters"); 
    this.initiator = paramBoolean;
    this.staticPrivateKey = paramECPrivateKeyParameters1;
    this.staticPublicPoint = eCDomainParameters.getG().multiply(paramECPrivateKeyParameters1.getD()).normalize();
    this.ephemeralPrivateKey = paramECPrivateKeyParameters2;
    this.ephemeralPublicPoint = eCDomainParameters.getG().multiply(paramECPrivateKeyParameters2.getD()).normalize();
  }
  
  public boolean isInitiator() {
    return this.initiator;
  }
  
  public ECPrivateKeyParameters getStaticPrivateKey() {
    return this.staticPrivateKey;
  }
  
  public ECPoint getStaticPublicPoint() {
    return this.staticPublicPoint;
  }
  
  public ECPrivateKeyParameters getEphemeralPrivateKey() {
    return this.ephemeralPrivateKey;
  }
  
  public ECPoint getEphemeralPublicPoint() {
    return this.ephemeralPublicPoint;
  }
}
