package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DigestInfo;

public class Data extends ASN1Object implements ASN1Choice {
  private ASN1OctetString message;
  
  private DigestInfo messageImprint;
  
  private ASN1Sequence certs;
  
  public Data(byte[] paramArrayOfbyte) {
    this.message = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public Data(ASN1OctetString paramASN1OctetString) {
    this.message = paramASN1OctetString;
  }
  
  public Data(DigestInfo paramDigestInfo) {
    this.messageImprint = paramDigestInfo;
  }
  
  public Data(TargetEtcChain paramTargetEtcChain) {
    this.certs = (ASN1Sequence)new DERSequence((ASN1Encodable)paramTargetEtcChain);
  }
  
  public Data(TargetEtcChain[] paramArrayOfTargetEtcChain) {
    this.certs = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfTargetEtcChain);
  }
  
  private Data(ASN1Sequence paramASN1Sequence) {
    this.certs = paramASN1Sequence;
  }
  
  public static Data getInstance(Object paramObject) {
    if (paramObject instanceof Data)
      return (Data)paramObject; 
    if (paramObject instanceof ASN1OctetString)
      return new Data((ASN1OctetString)paramObject); 
    if (paramObject instanceof ASN1Sequence)
      return new Data(DigestInfo.getInstance(paramObject)); 
    if (paramObject instanceof ASN1TaggedObject)
      return new Data(ASN1Sequence.getInstance((ASN1TaggedObject)paramObject, false)); 
    throw new IllegalArgumentException("Unknown object submitted to getInstance: " + paramObject.getClass().getName());
  }
  
  public static Data getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.message != null) ? this.message.toASN1Primitive() : ((this.messageImprint != null) ? this.messageImprint.toASN1Primitive() : new DERTaggedObject(false, 0, (ASN1Encodable)this.certs)));
  }
  
  public String toString() {
    return (this.message != null) ? ("Data {\n" + this.message + "}\n") : ((this.messageImprint != null) ? ("Data {\n" + this.messageImprint + "}\n") : ("Data {\n" + this.certs + "}\n"));
  }
  
  public ASN1OctetString getMessage() {
    return this.message;
  }
  
  public DigestInfo getMessageImprint() {
    return this.messageImprint;
  }
  
  public TargetEtcChain[] getCerts() {
    if (this.certs == null)
      return null; 
    TargetEtcChain[] arrayOfTargetEtcChain = new TargetEtcChain[this.certs.size()];
    for (byte b = 0; b != arrayOfTargetEtcChain.length; b++)
      arrayOfTargetEtcChain[b] = TargetEtcChain.getInstance(this.certs.getObjectAt(b)); 
    return arrayOfTargetEtcChain;
  }
}
