package org.bouncycastle.asn1.bc;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.Arrays;

public class ObjectData extends ASN1Object {
  private final BigInteger type;
  
  private final String identifier;
  
  private final ASN1GeneralizedTime creationDate;
  
  private final ASN1GeneralizedTime lastModifiedDate;
  
  private final ASN1OctetString data;
  
  private final String comment;
  
  private ObjectData(ASN1Sequence paramASN1Sequence) {
    this.type = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0)).getValue();
    this.identifier = DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(1)).getString();
    this.creationDate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(2));
    this.lastModifiedDate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(3));
    this.data = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(4));
    this.comment = (paramASN1Sequence.size() == 6) ? DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(5)).getString() : null;
  }
  
  public ObjectData(BigInteger paramBigInteger, String paramString1, Date paramDate1, Date paramDate2, byte[] paramArrayOfbyte, String paramString2) {
    this.type = paramBigInteger;
    this.identifier = paramString1;
    this.creationDate = (ASN1GeneralizedTime)new DERGeneralizedTime(paramDate1);
    this.lastModifiedDate = (ASN1GeneralizedTime)new DERGeneralizedTime(paramDate2);
    this.data = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
    this.comment = paramString2;
  }
  
  public static ObjectData getInstance(Object paramObject) {
    return (paramObject instanceof ObjectData) ? (ObjectData)paramObject : ((paramObject != null) ? new ObjectData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public String getComment() {
    return this.comment;
  }
  
  public ASN1GeneralizedTime getCreationDate() {
    return this.creationDate;
  }
  
  public byte[] getData() {
    return Arrays.clone(this.data.getOctets());
  }
  
  public String getIdentifier() {
    return this.identifier;
  }
  
  public ASN1GeneralizedTime getLastModifiedDate() {
    return this.lastModifiedDate;
  }
  
  public BigInteger getType() {
    return this.type;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.type));
    aSN1EncodableVector.add((ASN1Encodable)new DERUTF8String(this.identifier));
    aSN1EncodableVector.add((ASN1Encodable)this.creationDate);
    aSN1EncodableVector.add((ASN1Encodable)this.lastModifiedDate);
    aSN1EncodableVector.add((ASN1Encodable)this.data);
    if (this.comment != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERUTF8String(this.comment)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
