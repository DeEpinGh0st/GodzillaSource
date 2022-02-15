package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;

public class EncryptedKey extends ASN1Object implements ASN1Choice {
  private EnvelopedData envelopedData;
  
  private EncryptedValue encryptedValue;
  
  public static EncryptedKey getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedKey) ? (EncryptedKey)paramObject : ((paramObject instanceof ASN1TaggedObject) ? new EncryptedKey(EnvelopedData.getInstance((ASN1TaggedObject)paramObject, false)) : ((paramObject instanceof EncryptedValue) ? new EncryptedKey((EncryptedValue)paramObject) : new EncryptedKey(EncryptedValue.getInstance(paramObject))));
  }
  
  public EncryptedKey(EnvelopedData paramEnvelopedData) {
    this.envelopedData = paramEnvelopedData;
  }
  
  public EncryptedKey(EncryptedValue paramEncryptedValue) {
    this.encryptedValue = paramEncryptedValue;
  }
  
  public boolean isEncryptedValue() {
    return (this.encryptedValue != null);
  }
  
  public ASN1Encodable getValue() {
    return (ASN1Encodable)((this.encryptedValue != null) ? this.encryptedValue : this.envelopedData);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.encryptedValue != null) ? this.encryptedValue.toASN1Primitive() : new DERTaggedObject(false, 0, (ASN1Encodable)this.envelopedData));
  }
}
