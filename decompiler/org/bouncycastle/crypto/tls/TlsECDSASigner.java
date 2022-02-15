package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

public class TlsECDSASigner extends TlsDSASigner {
  public boolean isValidPublicKey(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.ECPublicKeyParameters;
  }
  
  protected DSA createDSAImpl(short paramShort) {
    return (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(TlsUtils.createHash(paramShort)));
  }
  
  protected short getSignatureAlgorithm() {
    return 3;
  }
}
