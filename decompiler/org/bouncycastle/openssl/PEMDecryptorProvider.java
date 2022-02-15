package org.bouncycastle.openssl;

import org.bouncycastle.operator.OperatorCreationException;

public interface PEMDecryptorProvider {
  PEMDecryptor get(String paramString) throws OperatorCreationException;
}
