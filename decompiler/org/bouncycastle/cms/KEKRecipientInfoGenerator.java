package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public abstract class KEKRecipientInfoGenerator implements RecipientInfoGenerator {
  private final KEKIdentifier kekIdentifier;
  
  protected final SymmetricKeyWrapper wrapper;
  
  protected KEKRecipientInfoGenerator(KEKIdentifier paramKEKIdentifier, SymmetricKeyWrapper paramSymmetricKeyWrapper) {
    this.kekIdentifier = paramKEKIdentifier;
    this.wrapper = paramSymmetricKeyWrapper;
  }
  
  public final RecipientInfo generate(GenericKey paramGenericKey) throws CMSException {
    try {
      DEROctetString dEROctetString = new DEROctetString(this.wrapper.generateWrappedKey(paramGenericKey));
      return new RecipientInfo(new KEKRecipientInfo(this.kekIdentifier, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)dEROctetString));
    } catch (OperatorException operatorException) {
      throw new CMSException("exception wrapping content key: " + operatorException.getMessage(), operatorException);
    } 
  }
}
