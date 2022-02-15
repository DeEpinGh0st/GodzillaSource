package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class ECDHBasicAgreement implements BasicAgreement {
  private ECPrivateKeyParameters key;
  
  public void init(CipherParameters paramCipherParameters) {
    this.key = (ECPrivateKeyParameters)paramCipherParameters;
  }
  
  public int getFieldSize() {
    return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
  }
  
  public BigInteger calculateAgreement(CipherParameters paramCipherParameters) {
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)paramCipherParameters;
    if (!eCPublicKeyParameters.getParameters().equals(this.key.getParameters()))
      throw new IllegalStateException("ECDH public key has wrong domain parameters"); 
    ECPoint eCPoint = eCPublicKeyParameters.getQ().multiply(this.key.getD()).normalize();
    if (eCPoint.isInfinity())
      throw new IllegalStateException("Infinity is not a valid agreement value for ECDH"); 
    return eCPoint.getAffineXCoord().toBigInteger();
  }
}
