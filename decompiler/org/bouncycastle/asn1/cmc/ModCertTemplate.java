package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;

public class ModCertTemplate extends ASN1Object {
  private final BodyPartPath pkiDataReference;
  
  private final BodyPartList certReferences;
  
  private final boolean replace;
  
  private final CertTemplate certTemplate;
  
  public ModCertTemplate(BodyPartPath paramBodyPartPath, BodyPartList paramBodyPartList, boolean paramBoolean, CertTemplate paramCertTemplate) {
    this.pkiDataReference = paramBodyPartPath;
    this.certReferences = paramBodyPartList;
    this.replace = paramBoolean;
    this.certTemplate = paramCertTemplate;
  }
  
  private ModCertTemplate(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 4 && paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.pkiDataReference = BodyPartPath.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certReferences = BodyPartList.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() == 4) {
      this.replace = ASN1Boolean.getInstance(paramASN1Sequence.getObjectAt(2)).isTrue();
      this.certTemplate = CertTemplate.getInstance(paramASN1Sequence.getObjectAt(3));
    } else {
      this.replace = true;
      this.certTemplate = CertTemplate.getInstance(paramASN1Sequence.getObjectAt(2));
    } 
  }
  
  public static ModCertTemplate getInstance(Object paramObject) {
    return (paramObject instanceof ModCertTemplate) ? (ModCertTemplate)paramObject : ((paramObject != null) ? new ModCertTemplate(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BodyPartPath getPkiDataReference() {
    return this.pkiDataReference;
  }
  
  public BodyPartList getCertReferences() {
    return this.certReferences;
  }
  
  public boolean isReplacingFields() {
    return this.replace;
  }
  
  public CertTemplate getCertTemplate() {
    return this.certTemplate;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pkiDataReference);
    aSN1EncodableVector.add((ASN1Encodable)this.certReferences);
    if (!this.replace)
      aSN1EncodableVector.add((ASN1Encodable)ASN1Boolean.getInstance(this.replace)); 
    aSN1EncodableVector.add((ASN1Encodable)this.certTemplate);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
