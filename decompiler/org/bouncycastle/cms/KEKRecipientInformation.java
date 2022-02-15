package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEKRecipientInformation extends RecipientInformation {
  private KEKRecipientInfo info;
  
  KEKRecipientInformation(KEKRecipientInfo paramKEKRecipientInfo, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    super(paramKEKRecipientInfo.getKeyEncryptionAlgorithm(), paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    this.info = paramKEKRecipientInfo;
    KEKIdentifier kEKIdentifier = paramKEKRecipientInfo.getKekid();
    this.rid = new KEKRecipientId(kEKIdentifier.getKeyIdentifier().getOctets());
  }
  
  protected RecipientOperator getRecipientOperator(Recipient paramRecipient) throws CMSException, IOException {
    return ((KEKRecipient)paramRecipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
  }
}
