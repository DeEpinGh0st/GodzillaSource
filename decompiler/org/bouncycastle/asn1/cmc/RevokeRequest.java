package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.util.Arrays;

public class RevokeRequest extends ASN1Object {
  private final X500Name name;
  
  private final ASN1Integer serialNumber;
  
  private final CRLReason reason;
  
  private ASN1GeneralizedTime invalidityDate;
  
  private ASN1OctetString passphrase;
  
  private DERUTF8String comment;
  
  public RevokeRequest(X500Name paramX500Name, ASN1Integer paramASN1Integer, CRLReason paramCRLReason, ASN1GeneralizedTime paramASN1GeneralizedTime, ASN1OctetString paramASN1OctetString, DERUTF8String paramDERUTF8String) {
    this.name = paramX500Name;
    this.serialNumber = paramASN1Integer;
    this.reason = paramCRLReason;
    this.invalidityDate = paramASN1GeneralizedTime;
    this.passphrase = paramASN1OctetString;
    this.comment = paramDERUTF8String;
  }
  
  private RevokeRequest(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 3 || paramASN1Sequence.size() > 6)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.name = X500Name.getInstance(paramASN1Sequence.getObjectAt(0));
    this.serialNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
    this.reason = CRLReason.getInstance(paramASN1Sequence.getObjectAt(2));
    byte b = 3;
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof ASN1GeneralizedTime)
      this.invalidityDate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof ASN1OctetString)
      this.passphrase = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (paramASN1Sequence.size() > b && paramASN1Sequence.getObjectAt(b).toASN1Primitive() instanceof DERUTF8String)
      this.comment = DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public static RevokeRequest getInstance(Object paramObject) {
    return (paramObject instanceof RevokeRequest) ? (RevokeRequest)paramObject : ((paramObject != null) ? new RevokeRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public X500Name getName() {
    return this.name;
  }
  
  public BigInteger getSerialNumber() {
    return this.serialNumber.getValue();
  }
  
  public CRLReason getReason() {
    return this.reason;
  }
  
  public ASN1GeneralizedTime getInvalidityDate() {
    return this.invalidityDate;
  }
  
  public void setInvalidityDate(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.invalidityDate = paramASN1GeneralizedTime;
  }
  
  public ASN1OctetString getPassphrase() {
    return this.passphrase;
  }
  
  public void setPassphrase(ASN1OctetString paramASN1OctetString) {
    this.passphrase = paramASN1OctetString;
  }
  
  public DERUTF8String getComment() {
    return this.comment;
  }
  
  public void setComment(DERUTF8String paramDERUTF8String) {
    this.comment = paramDERUTF8String;
  }
  
  public byte[] getPassPhrase() {
    return (this.passphrase != null) ? Arrays.clone(this.passphrase.getOctets()) : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.name);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.reason);
    if (this.invalidityDate != null)
      aSN1EncodableVector.add((ASN1Encodable)this.invalidityDate); 
    if (this.passphrase != null)
      aSN1EncodableVector.add((ASN1Encodable)this.passphrase); 
    if (this.comment != null)
      aSN1EncodableVector.add((ASN1Encodable)this.comment); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
