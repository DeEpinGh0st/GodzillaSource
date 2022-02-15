package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class PKIArchiveOptions extends ASN1Object implements ASN1Choice {
  public static final int encryptedPrivKey = 0;
  
  public static final int keyGenParameters = 1;
  
  public static final int archiveRemGenPrivKey = 2;
  
  private ASN1Encodable value;
  
  public static PKIArchiveOptions getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof PKIArchiveOptions)
      return (PKIArchiveOptions)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new PKIArchiveOptions((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("unknown object: " + paramObject);
  }
  
  private PKIArchiveOptions(ASN1TaggedObject paramASN1TaggedObject) {
    switch (paramASN1TaggedObject.getTagNo()) {
      case 0:
        this.value = (ASN1Encodable)EncryptedKey.getInstance(paramASN1TaggedObject.getObject());
        return;
      case 1:
        this.value = (ASN1Encodable)ASN1OctetString.getInstance(paramASN1TaggedObject, false);
        return;
      case 2:
        this.value = (ASN1Encodable)ASN1Boolean.getInstance(paramASN1TaggedObject, false);
        return;
    } 
    throw new IllegalArgumentException("unknown tag number: " + paramASN1TaggedObject.getTagNo());
  }
  
  public PKIArchiveOptions(EncryptedKey paramEncryptedKey) {
    this.value = (ASN1Encodable)paramEncryptedKey;
  }
  
  public PKIArchiveOptions(ASN1OctetString paramASN1OctetString) {
    this.value = (ASN1Encodable)paramASN1OctetString;
  }
  
  public PKIArchiveOptions(boolean paramBoolean) {
    this.value = (ASN1Encodable)ASN1Boolean.getInstance(paramBoolean);
  }
  
  public int getType() {
    return (this.value instanceof EncryptedKey) ? 0 : ((this.value instanceof ASN1OctetString) ? 1 : 2);
  }
  
  public ASN1Encodable getValue() {
    return this.value;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.value instanceof EncryptedKey) ? new DERTaggedObject(true, 0, this.value) : ((this.value instanceof ASN1OctetString) ? new DERTaggedObject(false, 1, this.value) : new DERTaggedObject(false, 2, this.value)));
  }
}
