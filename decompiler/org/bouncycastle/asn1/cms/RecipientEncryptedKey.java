package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class RecipientEncryptedKey extends ASN1Object {
  private KeyAgreeRecipientIdentifier identifier;
  
  private ASN1OctetString encryptedKey;
  
  private RecipientEncryptedKey(ASN1Sequence paramASN1Sequence) {
    this.identifier = KeyAgreeRecipientIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.encryptedKey = (ASN1OctetString)paramASN1Sequence.getObjectAt(1);
  }
  
  public static RecipientEncryptedKey getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static RecipientEncryptedKey getInstance(Object paramObject) {
    return (paramObject instanceof RecipientEncryptedKey) ? (RecipientEncryptedKey)paramObject : ((paramObject != null) ? new RecipientEncryptedKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RecipientEncryptedKey(KeyAgreeRecipientIdentifier paramKeyAgreeRecipientIdentifier, ASN1OctetString paramASN1OctetString) {
    this.identifier = paramKeyAgreeRecipientIdentifier;
    this.encryptedKey = paramASN1OctetString;
  }
  
  public KeyAgreeRecipientIdentifier getIdentifier() {
    return this.identifier;
  }
  
  public ASN1OctetString getEncryptedKey() {
    return this.encryptedKey;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.identifier);
    aSN1EncodableVector.add((ASN1Encodable)this.encryptedKey);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
