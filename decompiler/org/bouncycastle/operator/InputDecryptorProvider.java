package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputDecryptorProvider {
  InputDecryptor get(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException;
}
