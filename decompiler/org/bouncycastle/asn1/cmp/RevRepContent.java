package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContent extends ASN1Object {
  private ASN1Sequence status;
  
  private ASN1Sequence revCerts;
  
  private ASN1Sequence crls;
  
  private RevRepContent(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.status = ASN1Sequence.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.revCerts = ASN1Sequence.getInstance(aSN1TaggedObject, true);
        continue;
      } 
      this.crls = ASN1Sequence.getInstance(aSN1TaggedObject, true);
    } 
  }
  
  public static RevRepContent getInstance(Object paramObject) {
    return (paramObject instanceof RevRepContent) ? (RevRepContent)paramObject : ((paramObject != null) ? new RevRepContent(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PKIStatusInfo[] getStatus() {
    PKIStatusInfo[] arrayOfPKIStatusInfo = new PKIStatusInfo[this.status.size()];
    for (byte b = 0; b != arrayOfPKIStatusInfo.length; b++)
      arrayOfPKIStatusInfo[b] = PKIStatusInfo.getInstance(this.status.getObjectAt(b)); 
    return arrayOfPKIStatusInfo;
  }
  
  public CertId[] getRevCerts() {
    if (this.revCerts == null)
      return null; 
    CertId[] arrayOfCertId = new CertId[this.revCerts.size()];
    for (byte b = 0; b != arrayOfCertId.length; b++)
      arrayOfCertId[b] = CertId.getInstance(this.revCerts.getObjectAt(b)); 
    return arrayOfCertId;
  }
  
  public CertificateList[] getCrls() {
    if (this.crls == null)
      return null; 
    CertificateList[] arrayOfCertificateList = new CertificateList[this.crls.size()];
    for (byte b = 0; b != arrayOfCertificateList.length; b++)
      arrayOfCertificateList[b] = CertificateList.getInstance(this.crls.getObjectAt(b)); 
    return arrayOfCertificateList;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.status);
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.revCerts);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.crls);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
