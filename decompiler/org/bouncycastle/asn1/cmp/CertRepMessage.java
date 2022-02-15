package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertRepMessage extends ASN1Object {
  private ASN1Sequence caPubs;
  
  private ASN1Sequence response;
  
  private CertRepMessage(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.size() > 1)
      this.caPubs = ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b++), true); 
    this.response = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(b));
  }
  
  public static CertRepMessage getInstance(Object paramObject) {
    return (paramObject instanceof CertRepMessage) ? (CertRepMessage)paramObject : ((paramObject != null) ? new CertRepMessage(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public CertRepMessage(CMPCertificate[] paramArrayOfCMPCertificate, CertResponse[] paramArrayOfCertResponse) {
    if (paramArrayOfCertResponse == null)
      throw new IllegalArgumentException("'response' cannot be null"); 
    if (paramArrayOfCMPCertificate != null) {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      for (byte b1 = 0; b1 < paramArrayOfCMPCertificate.length; b1++)
        aSN1EncodableVector1.add((ASN1Encodable)paramArrayOfCMPCertificate[b1]); 
      this.caPubs = (ASN1Sequence)new DERSequence(aSN1EncodableVector1);
    } 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfCertResponse.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfCertResponse[b]); 
    this.response = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public CMPCertificate[] getCaPubs() {
    if (this.caPubs == null)
      return null; 
    CMPCertificate[] arrayOfCMPCertificate = new CMPCertificate[this.caPubs.size()];
    for (byte b = 0; b != arrayOfCMPCertificate.length; b++)
      arrayOfCMPCertificate[b] = CMPCertificate.getInstance(this.caPubs.getObjectAt(b)); 
    return arrayOfCMPCertificate;
  }
  
  public CertResponse[] getResponse() {
    CertResponse[] arrayOfCertResponse = new CertResponse[this.response.size()];
    for (byte b = 0; b != arrayOfCertResponse.length; b++)
      arrayOfCertResponse[b] = CertResponse.getInstance(this.response.getObjectAt(b)); 
    return arrayOfCertResponse;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.caPubs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.caPubs)); 
    aSN1EncodableVector.add((ASN1Encodable)this.response);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
