package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class KeyTransRecipientInfoGenerator implements RecipientInfoGenerator {
  protected final AsymmetricKeyWrapper wrapper;
  
  private IssuerAndSerialNumber issuerAndSerial;
  
  private byte[] subjectKeyIdentifier;
  
  protected KeyTransRecipientInfoGenerator(IssuerAndSerialNumber paramIssuerAndSerialNumber, AsymmetricKeyWrapper paramAsymmetricKeyWrapper) {
    this.issuerAndSerial = paramIssuerAndSerialNumber;
    this.wrapper = paramAsymmetricKeyWrapper;
  }
  
  protected KeyTransRecipientInfoGenerator(byte[] paramArrayOfbyte, AsymmetricKeyWrapper paramAsymmetricKeyWrapper) {
    this.subjectKeyIdentifier = paramArrayOfbyte;
    this.wrapper = paramAsymmetricKeyWrapper;
  }
  
  public final RecipientInfo generate(GenericKey paramGenericKey) throws CMSException {
    byte[] arrayOfByte;
    RecipientIdentifier recipientIdentifier;
    try {
      arrayOfByte = this.wrapper.generateWrappedKey(paramGenericKey);
    } catch (OperatorException operatorException) {
      throw new CMSException("exception wrapping content key: " + operatorException.getMessage(), operatorException);
    } 
    if (this.issuerAndSerial != null) {
      recipientIdentifier = new RecipientIdentifier(this.issuerAndSerial);
    } else {
      recipientIdentifier = new RecipientIdentifier((ASN1OctetString)new DEROctetString(this.subjectKeyIdentifier));
    } 
    return new RecipientInfo(new KeyTransRecipientInfo(recipientIdentifier, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)new DEROctetString(arrayOfByte)));
  }
}
