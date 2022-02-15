package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class CamelliaUtil {
  static AlgorithmIdentifier determineKeyEncAlg(KeyParameter paramKeyParameter) {
    ASN1ObjectIdentifier aSN1ObjectIdentifier;
    int i = (paramKeyParameter.getKey()).length * 8;
    if (i == 128) {
      aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia128_wrap;
    } else if (i == 192) {
      aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia192_wrap;
    } else if (i == 256) {
      aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia256_wrap;
    } else {
      throw new IllegalArgumentException("illegal keysize in Camellia");
    } 
    return new AlgorithmIdentifier(aSN1ObjectIdentifier);
  }
}
