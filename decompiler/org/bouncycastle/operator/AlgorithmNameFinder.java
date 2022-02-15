package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface AlgorithmNameFinder {
  boolean hasAlgorithmName(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
  
  String getAlgorithmName(ASN1ObjectIdentifier paramASN1ObjectIdentifier);
  
  String getAlgorithmName(AlgorithmIdentifier paramAlgorithmIdentifier);
}
