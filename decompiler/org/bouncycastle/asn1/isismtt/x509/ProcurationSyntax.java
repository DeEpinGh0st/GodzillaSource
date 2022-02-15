package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class ProcurationSyntax extends ASN1Object {
  private String country;
  
  private DirectoryString typeOfSubstitution;
  
  private GeneralName thirdPerson;
  
  private IssuerSerial certRef;
  
  public static ProcurationSyntax getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ProcurationSyntax)
      return (ProcurationSyntax)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new ProcurationSyntax((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private ProcurationSyntax(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Primitive aSN1Primitive;
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      switch (aSN1TaggedObject.getTagNo()) {
        case 1:
          this.country = DERPrintableString.getInstance(aSN1TaggedObject, true).getString();
          continue;
        case 2:
          this.typeOfSubstitution = DirectoryString.getInstance(aSN1TaggedObject, true);
          continue;
        case 3:
          aSN1Primitive = aSN1TaggedObject.getObject();
          if (aSN1Primitive instanceof ASN1TaggedObject) {
            this.thirdPerson = GeneralName.getInstance(aSN1Primitive);
            continue;
          } 
          this.certRef = IssuerSerial.getInstance(aSN1Primitive);
          continue;
      } 
      throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public ProcurationSyntax(String paramString, DirectoryString paramDirectoryString, IssuerSerial paramIssuerSerial) {
    this.country = paramString;
    this.typeOfSubstitution = paramDirectoryString;
    this.thirdPerson = null;
    this.certRef = paramIssuerSerial;
  }
  
  public ProcurationSyntax(String paramString, DirectoryString paramDirectoryString, GeneralName paramGeneralName) {
    this.country = paramString;
    this.typeOfSubstitution = paramDirectoryString;
    this.thirdPerson = paramGeneralName;
    this.certRef = null;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public DirectoryString getTypeOfSubstitution() {
    return this.typeOfSubstitution;
  }
  
  public GeneralName getThirdPerson() {
    return this.thirdPerson;
  }
  
  public IssuerSerial getCertRef() {
    return this.certRef;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.country != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERPrintableString(this.country, true))); 
    if (this.typeOfSubstitution != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.typeOfSubstitution)); 
    if (this.thirdPerson != null) {
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.thirdPerson));
    } else {
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.certRef));
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
