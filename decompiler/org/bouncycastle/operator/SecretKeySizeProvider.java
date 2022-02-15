package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface SecretKeySizeProvider {
  int getKeySize(AlgorithmIdentifier paramAlgorithmIdentifier);
  
  int getKeySize(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
}
