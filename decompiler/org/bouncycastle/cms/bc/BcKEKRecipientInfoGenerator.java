package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.bc.BcSymmetricKeyWrapper;

public class BcKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator {
  public BcKEKRecipientInfoGenerator(KEKIdentifier paramKEKIdentifier, BcSymmetricKeyWrapper paramBcSymmetricKeyWrapper) {
    super(paramKEKIdentifier, (SymmetricKeyWrapper)paramBcSymmetricKeyWrapper);
  }
  
  public BcKEKRecipientInfoGenerator(byte[] paramArrayOfbyte, BcSymmetricKeyWrapper paramBcSymmetricKeyWrapper) {
    this(new KEKIdentifier(paramArrayOfbyte, null, null), paramBcSymmetricKeyWrapper);
  }
}
