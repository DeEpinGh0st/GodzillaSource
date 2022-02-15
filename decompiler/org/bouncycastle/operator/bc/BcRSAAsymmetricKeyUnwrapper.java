package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class BcRSAAsymmetricKeyUnwrapper extends BcAsymmetricKeyUnwrapper {
  public BcRSAAsymmetricKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    super(paramAlgorithmIdentifier, paramAsymmetricKeyParameter);
  }
  
  protected AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSAEngine());
  }
}
