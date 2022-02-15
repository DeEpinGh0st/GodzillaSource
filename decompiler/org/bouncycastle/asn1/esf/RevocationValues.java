package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevocationValues extends ASN1Object {
  private ASN1Sequence crlVals;
  
  private ASN1Sequence ocspVals;
  
  private OtherRevVals otherRevVals;
  
  public static RevocationValues getInstance(Object paramObject) {
    return (paramObject instanceof RevocationValues) ? (RevocationValues)paramObject : ((paramObject != null) ? new RevocationValues(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private RevocationValues(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Sequence aSN1Sequence1;
      Enumeration enumeration1;
      ASN1Sequence aSN1Sequence2;
      Enumeration enumeration2;
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          aSN1Sequence1 = (ASN1Sequence)aSN1TaggedObject.getObject();
          enumeration1 = aSN1Sequence1.getObjects();
          while (enumeration1.hasMoreElements())
            CertificateList.getInstance(enumeration1.nextElement()); 
          this.crlVals = aSN1Sequence1;
          continue;
        case 1:
          aSN1Sequence2 = (ASN1Sequence)aSN1TaggedObject.getObject();
          enumeration2 = aSN1Sequence2.getObjects();
          while (enumeration2.hasMoreElements())
            BasicOCSPResponse.getInstance(enumeration2.nextElement()); 
          this.ocspVals = aSN1Sequence2;
          continue;
        case 2:
          this.otherRevVals = OtherRevVals.getInstance(aSN1TaggedObject.getObject());
          continue;
      } 
      throw new IllegalArgumentException("invalid tag: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public RevocationValues(CertificateList[] paramArrayOfCertificateList, BasicOCSPResponse[] paramArrayOfBasicOCSPResponse, OtherRevVals paramOtherRevVals) {
    if (null != paramArrayOfCertificateList)
      this.crlVals = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfCertificateList); 
    if (null != paramArrayOfBasicOCSPResponse)
      this.ocspVals = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfBasicOCSPResponse); 
    this.otherRevVals = paramOtherRevVals;
  }
  
  public CertificateList[] getCrlVals() {
    if (null == this.crlVals)
      return new CertificateList[0]; 
    CertificateList[] arrayOfCertificateList = new CertificateList[this.crlVals.size()];
    for (byte b = 0; b < arrayOfCertificateList.length; b++)
      arrayOfCertificateList[b] = CertificateList.getInstance(this.crlVals.getObjectAt(b)); 
    return arrayOfCertificateList;
  }
  
  public BasicOCSPResponse[] getOcspVals() {
    if (null == this.ocspVals)
      return new BasicOCSPResponse[0]; 
    BasicOCSPResponse[] arrayOfBasicOCSPResponse = new BasicOCSPResponse[this.ocspVals.size()];
    for (byte b = 0; b < arrayOfBasicOCSPResponse.length; b++)
      arrayOfBasicOCSPResponse[b] = BasicOCSPResponse.getInstance(this.ocspVals.getObjectAt(b)); 
    return arrayOfBasicOCSPResponse;
  }
  
  public OtherRevVals getOtherRevVals() {
    return this.otherRevVals;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (null != this.crlVals)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.crlVals)); 
    if (null != this.ocspVals)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ocspVals)); 
    if (null != this.otherRevVals)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.otherRevVals.toASN1Primitive())); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
