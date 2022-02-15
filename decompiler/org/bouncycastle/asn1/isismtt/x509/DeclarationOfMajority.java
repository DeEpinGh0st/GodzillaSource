package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class DeclarationOfMajority extends ASN1Object implements ASN1Choice {
  public static final int notYoungerThan = 0;
  
  public static final int fullAgeAtCountry = 1;
  
  public static final int dateOfBirth = 2;
  
  private ASN1TaggedObject declaration;
  
  public DeclarationOfMajority(int paramInt) {
    this.declaration = (ASN1TaggedObject)new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(paramInt));
  }
  
  public DeclarationOfMajority(boolean paramBoolean, String paramString) {
    if (paramString.length() > 2)
      throw new IllegalArgumentException("country can only be 2 characters"); 
    if (paramBoolean) {
      this.declaration = (ASN1TaggedObject)new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence((ASN1Encodable)new DERPrintableString(paramString, true)));
    } else {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      aSN1EncodableVector.add((ASN1Encodable)ASN1Boolean.FALSE);
      aSN1EncodableVector.add((ASN1Encodable)new DERPrintableString(paramString, true));
      this.declaration = (ASN1TaggedObject)new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence(aSN1EncodableVector));
    } 
  }
  
  public DeclarationOfMajority(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.declaration = (ASN1TaggedObject)new DERTaggedObject(false, 2, (ASN1Encodable)paramASN1GeneralizedTime);
  }
  
  public static DeclarationOfMajority getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DeclarationOfMajority)
      return (DeclarationOfMajority)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new DeclarationOfMajority((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private DeclarationOfMajority(ASN1TaggedObject paramASN1TaggedObject) {
    if (paramASN1TaggedObject.getTagNo() > 2)
      throw new IllegalArgumentException("Bad tag number: " + paramASN1TaggedObject.getTagNo()); 
    this.declaration = paramASN1TaggedObject;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.declaration;
  }
  
  public int getType() {
    return this.declaration.getTagNo();
  }
  
  public int notYoungerThan() {
    return (this.declaration.getTagNo() != 0) ? -1 : ASN1Integer.getInstance(this.declaration, false).getValue().intValue();
  }
  
  public ASN1Sequence fullAgeAtCountry() {
    return (this.declaration.getTagNo() != 1) ? null : ASN1Sequence.getInstance(this.declaration, false);
  }
  
  public ASN1GeneralizedTime getDateOfBirth() {
    return (this.declaration.getTagNo() != 2) ? null : ASN1GeneralizedTime.getInstance(this.declaration, false);
  }
}
