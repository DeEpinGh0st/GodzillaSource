package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class AESUtil {
  static AlgorithmIdentifier determineKeyEncAlg(KeyParameter paramKeyParameter) {
    ASN1ObjectIdentifier aSN1ObjectIdentifier;
    int i = (paramKeyParameter.getKey()).length * 8;
    if (i == 128) {
      aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_wrap;
    } else if (i == 192) {
      aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes192_wrap;
    } else if (i == 256) {
      aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes256_wrap;
    } else {
      throw new IllegalArgumentException("illegal keysize in AES");
    } 
    return new AlgorithmIdentifier(aSN1ObjectIdentifier);
  }
}
