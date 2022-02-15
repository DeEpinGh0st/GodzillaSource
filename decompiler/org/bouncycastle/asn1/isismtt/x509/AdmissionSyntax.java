package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class AdmissionSyntax extends ASN1Object {
  private GeneralName admissionAuthority;
  
  private ASN1Sequence contentsOfAdmissions;
  
  public static AdmissionSyntax getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof AdmissionSyntax)
      return (AdmissionSyntax)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new AdmissionSyntax((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  private AdmissionSyntax(ASN1Sequence paramASN1Sequence) {
    switch (paramASN1Sequence.size()) {
      case 1:
        this.contentsOfAdmissions = DERSequence.getInstance(paramASN1Sequence.getObjectAt(0));
        return;
      case 2:
        this.admissionAuthority = GeneralName.getInstance(paramASN1Sequence.getObjectAt(0));
        this.contentsOfAdmissions = DERSequence.getInstance(paramASN1Sequence.getObjectAt(1));
        return;
    } 
    throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size());
  }
  
  public AdmissionSyntax(GeneralName paramGeneralName, ASN1Sequence paramASN1Sequence) {
    this.admissionAuthority = paramGeneralName;
    this.contentsOfAdmissions = paramASN1Sequence;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.admissionAuthority != null)
      aSN1EncodableVector.add((ASN1Encodable)this.admissionAuthority); 
    aSN1EncodableVector.add((ASN1Encodable)this.contentsOfAdmissions);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public GeneralName getAdmissionAuthority() {
    return this.admissionAuthority;
  }
  
  public Admissions[] getContentsOfAdmissions() {
    Admissions[] arrayOfAdmissions = new Admissions[this.contentsOfAdmissions.size()];
    byte b = 0;
    Enumeration enumeration = this.contentsOfAdmissions.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfAdmissions[b++] = Admissions.getInstance(enumeration.nextElement()); 
    return arrayOfAdmissions;
  }
}
