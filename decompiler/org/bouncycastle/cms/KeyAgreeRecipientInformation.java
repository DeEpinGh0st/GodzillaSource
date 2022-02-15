package org.bouncycastle.cms;

import java.io.IOException;
import java.util.List;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class KeyAgreeRecipientInformation extends RecipientInformation {
  private KeyAgreeRecipientInfo info;
  
  private ASN1OctetString encryptedKey;
  
  static void readRecipientInfo(List<KeyAgreeRecipientInformation> paramList, KeyAgreeRecipientInfo paramKeyAgreeRecipientInfo, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    ASN1Sequence aSN1Sequence = paramKeyAgreeRecipientInfo.getRecipientEncryptedKeys();
    for (byte b = 0; b < aSN1Sequence.size(); b++) {
      KeyAgreeRecipientId keyAgreeRecipientId;
      RecipientEncryptedKey recipientEncryptedKey = RecipientEncryptedKey.getInstance(aSN1Sequence.getObjectAt(b));
      KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier = recipientEncryptedKey.getIdentifier();
      IssuerAndSerialNumber issuerAndSerialNumber = keyAgreeRecipientIdentifier.getIssuerAndSerialNumber();
      if (issuerAndSerialNumber != null) {
        keyAgreeRecipientId = new KeyAgreeRecipientId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
      } else {
        RecipientKeyIdentifier recipientKeyIdentifier = keyAgreeRecipientIdentifier.getRKeyID();
        keyAgreeRecipientId = new KeyAgreeRecipientId(recipientKeyIdentifier.getSubjectKeyIdentifier().getOctets());
      } 
      paramList.add(new KeyAgreeRecipientInformation(paramKeyAgreeRecipientInfo, keyAgreeRecipientId, recipientEncryptedKey.getEncryptedKey(), paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider));
    } 
  }
  
  KeyAgreeRecipientInformation(KeyAgreeRecipientInfo paramKeyAgreeRecipientInfo, RecipientId paramRecipientId, ASN1OctetString paramASN1OctetString, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    super(paramKeyAgreeRecipientInfo.getKeyEncryptionAlgorithm(), paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    this.info = paramKeyAgreeRecipientInfo;
    this.rid = paramRecipientId;
    this.encryptedKey = paramASN1OctetString;
  }
  
  private SubjectPublicKeyInfo getSenderPublicKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, OriginatorIdentifierOrKey paramOriginatorIdentifierOrKey) throws CMSException, IOException {
    OriginatorId originatorId;
    OriginatorPublicKey originatorPublicKey = paramOriginatorIdentifierOrKey.getOriginatorKey();
    if (originatorPublicKey != null)
      return getPublicKeyInfoFromOriginatorPublicKey(paramAlgorithmIdentifier, originatorPublicKey); 
    IssuerAndSerialNumber issuerAndSerialNumber = paramOriginatorIdentifierOrKey.getIssuerAndSerialNumber();
    if (issuerAndSerialNumber != null) {
      originatorId = new OriginatorId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
    } else {
      SubjectKeyIdentifier subjectKeyIdentifier = paramOriginatorIdentifierOrKey.getSubjectKeyIdentifier();
      originatorId = new OriginatorId(subjectKeyIdentifier.getKeyIdentifier());
    } 
    return getPublicKeyInfoFromOriginatorId(originatorId);
  }
  
  private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(AlgorithmIdentifier paramAlgorithmIdentifier, OriginatorPublicKey paramOriginatorPublicKey) {
    return new SubjectPublicKeyInfo(paramAlgorithmIdentifier, paramOriginatorPublicKey.getPublicKey().getBytes());
  }
  
  private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(OriginatorId paramOriginatorId) throws CMSException {
    throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
  }
  
  protected RecipientOperator getRecipientOperator(Recipient paramRecipient) throws CMSException, IOException {
    KeyAgreeRecipient keyAgreeRecipient = (KeyAgreeRecipient)paramRecipient;
    AlgorithmIdentifier algorithmIdentifier = keyAgreeRecipient.getPrivateKeyAlgorithmIdentifier();
    return ((KeyAgreeRecipient)paramRecipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, getSenderPublicKeyInfo(algorithmIdentifier, this.info.getOriginator()), this.info.getUserKeyingMaterial(), this.encryptedKey.getOctets());
  }
}
