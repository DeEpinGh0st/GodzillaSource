package org.bouncycastle.asn1.icao;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DataGroupHash extends ASN1Object {
  ASN1Integer dataGroupNumber;
  
  ASN1OctetString dataGroupHashValue;
  
  public static DataGroupHash getInstance(Object paramObject) {
    return (paramObject instanceof DataGroupHash) ? (DataGroupHash)paramObject : ((paramObject != null) ? new DataGroupHash(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private DataGroupHash(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.dataGroupNumber = ASN1Integer.getInstance(enumeration.nextElement());
    this.dataGroupHashValue = ASN1OctetString.getInstance(enumeration.nextElement());
  }
  
  public DataGroupHash(int paramInt, ASN1OctetString paramASN1OctetString) {
    this.dataGroupNumber = new ASN1Integer(paramInt);
    this.dataGroupHashValue = paramASN1OctetString;
  }
  
  public int getDataGroupNumber() {
    return this.dataGroupNumber.getValue().intValue();
  }
  
  public ASN1OctetString getDataGroupHashValue() {
    return this.dataGroupHashValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.dataGroupNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.dataGroupHashValue);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
