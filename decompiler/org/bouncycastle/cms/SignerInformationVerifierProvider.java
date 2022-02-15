package org.bouncycastle.cms;

import org.bouncycastle.operator.OperatorCreationException;

public interface SignerInformationVerifierProvider {
  SignerInformationVerifier get(SignerId paramSignerId) throws OperatorCreationException;
}
