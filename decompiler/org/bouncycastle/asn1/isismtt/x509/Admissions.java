package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class Admissions extends ASN1Object {
  private GeneralName admissionAuthority;
  
  private NamingAuthority namingAuthority;
  
  private ASN1Sequence professionInfos;
  
  public static Admissions getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof Admissions)
      return (Admissions)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new Admissions((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private Admissions(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    ASN1Encodable aSN1Encodable = enumeration.nextElement();
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      switch (((ASN1TaggedObject)aSN1Encodable).getTagNo()) {
        case 0:
          this.admissionAuthority = GeneralName.getInstance((ASN1TaggedObject)aSN1Encodable, true);
          break;
        case 1:
          this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)aSN1Encodable, true);
          break;
        default:
          throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)aSN1Encodable).getTagNo());
      } 
      aSN1Encodable = enumeration.nextElement();
    } 
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      switch (((ASN1TaggedObject)aSN1Encodable).getTagNo()) {
        case 1:
          this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)aSN1Encodable, true);
          break;
        default:
          throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)aSN1Encodable).getTagNo());
      } 
      aSN1Encodable = enumeration.nextElement();
    } 
    this.professionInfos = ASN1Sequence.getInstance(aSN1Encodable);
    if (enumeration.hasMoreElements())
      throw new IllegalArgumentException("Bad object encountered: " + enumeration.nextElement().getClass()); 
  }
  
  public Admissions(GeneralName paramGeneralName, NamingAuthority paramNamingAuthority, ProfessionInfo[] paramArrayOfProfessionInfo) {
    this.admissionAuthority = paramGeneralName;
    this.namingAuthority = paramNamingAuthority;
    this.professionInfos = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfProfessionInfo);
  }
  
  public GeneralName getAdmissionAuthority() {
    return this.admissionAuthority;
  }
  
  public NamingAuthority getNamingAuthority() {
    return this.namingAuthority;
  }
  
  public ProfessionInfo[] getProfessionInfos() {
    ProfessionInfo[] arrayOfProfessionInfo = new ProfessionInfo[this.professionInfos.size()];
    byte b = 0;
    Enumeration enumeration = this.professionInfos.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfProfessionInfo[b++] = ProfessionInfo.getInstance(enumeration.nextElement()); 
    return arrayOfProfessionInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.admissionAuthority != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.admissionAuthority)); 
    if (this.namingAuthority != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.namingAuthority)); 
    aSN1EncodableVector.add((ASN1Encodable)this.professionInfos);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
