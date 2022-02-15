package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;

public class SMIMEEncryptionKeyPreferenceAttribute extends Attribute {
  public SMIMEEncryptionKeyPreferenceAttribute(IssuerAndSerialNumber paramIssuerAndSerialNumber) {
    super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)paramIssuerAndSerialNumber)));
  }
  
  public SMIMEEncryptionKeyPreferenceAttribute(RecipientKeyIdentifier paramRecipientKeyIdentifier) {
    super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)paramRecipientKeyIdentifier)));
  }
  
  public SMIMEEncryptionKeyPreferenceAttribute(ASN1OctetString paramASN1OctetString) {
    super(SMIMEAttributes.encrypKeyPref, (ASN1Set)new DERSet((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)paramASN1OctetString)));
  }
}
