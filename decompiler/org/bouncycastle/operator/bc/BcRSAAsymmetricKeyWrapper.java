package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;

public class BcRSAAsymmetricKeyWrapper extends BcAsymmetricKeyWrapper {
  public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier paramAlgorithmIdentifier, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    super(paramAlgorithmIdentifier, paramAsymmetricKeyParameter);
  }
  
  public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier paramAlgorithmIdentifier, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    super(paramAlgorithmIdentifier, PublicKeyFactory.createKey(paramSubjectPublicKeyInfo));
  }
  
  protected AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSAEngine());
  }
}
