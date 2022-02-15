package org.bouncycastle.asn1.x509.sigi;

import java.math.BigInteger;
import java.util.Enumeration;
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
import org.bouncycastle.asn1.x500.DirectoryString;

public class PersonalData extends ASN1Object {
  private NameOrPseudonym nameOrPseudonym;
  
  private BigInteger nameDistinguisher;
  
  private ASN1GeneralizedTime dateOfBirth;
  
  private DirectoryString placeOfBirth;
  
  private String gender;
  
  private DirectoryString postalAddress;
  
  public static PersonalData getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof PersonalData)
      return (PersonalData)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new PersonalData((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private PersonalData(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.nameOrPseudonym = NameOrPseudonym.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      int i = aSN1TaggedObject.getTagNo();
      switch (i) {
        case 0:
          this.nameDistinguisher = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue();
          continue;
        case 1:
          this.dateOfBirth = ASN1GeneralizedTime.getInstance(aSN1TaggedObject, false);
          continue;
        case 2:
          this.placeOfBirth = DirectoryString.getInstance(aSN1TaggedObject, true);
          continue;
        case 3:
          this.gender = DERPrintableString.getInstance(aSN1TaggedObject, false).getString();
          continue;
        case 4:
          this.postalAddress = DirectoryString.getInstance(aSN1TaggedObject, true);
          continue;
      } 
      throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public PersonalData(NameOrPseudonym paramNameOrPseudonym, BigInteger paramBigInteger, ASN1GeneralizedTime paramASN1GeneralizedTime, DirectoryString paramDirectoryString1, String paramString, DirectoryString paramDirectoryString2) {
    this.nameOrPseudonym = paramNameOrPseudonym;
    this.dateOfBirth = paramASN1GeneralizedTime;
    this.gender = paramString;
    this.nameDistinguisher = paramBigInteger;
    this.postalAddress = paramDirectoryString2;
    this.placeOfBirth = paramDirectoryString1;
  }
  
  public NameOrPseudonym getNameOrPseudonym() {
    return this.nameOrPseudonym;
  }
  
  public BigInteger getNameDistinguisher() {
    return this.nameDistinguisher;
  }
  
  public ASN1GeneralizedTime getDateOfBirth() {
    return this.dateOfBirth;
  }
  
  public DirectoryString getPlaceOfBirth() {
    return this.placeOfBirth;
  }
  
  public String getGender() {
    return this.gender;
  }
  
  public DirectoryString getPostalAddress() {
    return this.postalAddress;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.nameOrPseudonym);
    if (this.nameDistinguisher != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(this.nameDistinguisher))); 
    if (this.dateOfBirth != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.dateOfBirth)); 
    if (this.placeOfBirth != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.placeOfBirth)); 
    if (this.gender != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)new DERPrintableString(this.gender, true))); 
    if (this.postalAddress != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 4, (ASN1Encodable)this.postalAddress)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
