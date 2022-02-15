package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

public class TlsDSSSigner extends TlsDSASigner {
  public boolean isValidPublicKey(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.DSAPublicKeyParameters;
  }
  
  protected DSA createDSAImpl(short paramShort) {
    return (DSA)new DSASigner((DSAKCalculator)new HMacDSAKCalculator(TlsUtils.createHash(paramShort)));
  }
  
  protected short getSignatureAlgorithm() {
    return 2;
  }
}
