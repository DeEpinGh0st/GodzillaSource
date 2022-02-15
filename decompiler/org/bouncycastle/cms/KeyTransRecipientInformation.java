package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyTransRecipientInformation extends RecipientInformation {
  private KeyTransRecipientInfo info;
  
  KeyTransRecipientInformation(KeyTransRecipientInfo paramKeyTransRecipientInfo, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    super(paramKeyTransRecipientInfo.getKeyEncryptionAlgorithm(), paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    this.info = paramKeyTransRecipientInfo;
    RecipientIdentifier recipientIdentifier = paramKeyTransRecipientInfo.getRecipientIdentifier();
    if (recipientIdentifier.isTagged()) {
      ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(recipientIdentifier.getId());
      this.rid = new KeyTransRecipientId(aSN1OctetString.getOctets());
    } else {
      IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(recipientIdentifier.getId());
      this.rid = new KeyTransRecipientId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
    } 
  }
  
  protected RecipientOperator getRecipientOperator(Recipient paramRecipient) throws CMSException {
    return ((KeyTransRecipient)paramRecipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
  }
}
