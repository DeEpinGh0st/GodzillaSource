package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KeyUnwrapper {
  AlgorithmIdentifier getAlgorithmIdentifier();
  
  GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException;
}
