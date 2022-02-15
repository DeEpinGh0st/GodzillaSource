package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class TSTInfo extends ASN1Object {
  private ASN1Integer version;
  
  private ASN1ObjectIdentifier tsaPolicyId;
  
  private MessageImprint messageImprint;
  
  private ASN1Integer serialNumber;
  
  private ASN1GeneralizedTime genTime;
  
  private Accuracy accuracy;
  
  private ASN1Boolean ordering;
  
  private ASN1Integer nonce;
  
  private GeneralName tsa;
  
  private Extensions extensions;
  
  public static TSTInfo getInstance(Object paramObject) {
    return (paramObject instanceof TSTInfo) ? (TSTInfo)paramObject : ((paramObject != null) ? new TSTInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private TSTInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Object> enumeration = paramASN1Sequence.getObjects();
    this.version = ASN1Integer.getInstance(enumeration.nextElement());
    this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
    this.messageImprint = MessageImprint.getInstance(enumeration.nextElement());
    this.serialNumber = ASN1Integer.getInstance(enumeration.nextElement());
    this.genTime = ASN1GeneralizedTime.getInstance(enumeration.nextElement());
    this.ordering = ASN1Boolean.getInstance(false);
    while (enumeration.hasMoreElements()) {
      ASN1Object aSN1Object = enumeration.nextElement();
      if (aSN1Object instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Object;
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            this.tsa = GeneralName.getInstance(aSN1TaggedObject, true);
            continue;
          case 1:
            this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
            continue;
        } 
        throw new IllegalArgumentException("Unknown tag value " + aSN1TaggedObject.getTagNo());
      } 
      if (aSN1Object instanceof ASN1Sequence || aSN1Object instanceof Accuracy) {
        this.accuracy = Accuracy.getInstance(aSN1Object);
        continue;
      } 
      if (aSN1Object instanceof ASN1Boolean) {
        this.ordering = ASN1Boolean.getInstance(aSN1Object);
        continue;
      } 
      if (aSN1Object instanceof ASN1Integer)
        this.nonce = ASN1Integer.getInstance(aSN1Object); 
    } 
  }
  
  public TSTInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, MessageImprint paramMessageImprint, ASN1Integer paramASN1Integer1, ASN1GeneralizedTime paramASN1GeneralizedTime, Accuracy paramAccuracy, ASN1Boolean paramASN1Boolean, ASN1Integer paramASN1Integer2, GeneralName paramGeneralName, Extensions paramExtensions) {
    this.version = new ASN1Integer(1L);
    this.tsaPolicyId = paramASN1ObjectIdentifier;
    this.messageImprint = paramMessageImprint;
    this.serialNumber = paramASN1Integer1;
    this.genTime = paramASN1GeneralizedTime;
    this.accuracy = paramAccuracy;
    this.ordering = paramASN1Boolean;
    this.nonce = paramASN1Integer2;
    this.tsa = paramGeneralName;
    this.extensions = paramExtensions;
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public MessageImprint getMessageImprint() {
    return this.messageImprint;
  }
  
  public ASN1ObjectIdentifier getPolicy() {
    return this.tsaPolicyId;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public Accuracy getAccuracy() {
    return this.accuracy;
  }
  
  public ASN1GeneralizedTime getGenTime() {
    return this.genTime;
  }
  
  public ASN1Boolean getOrdering() {
    return this.ordering;
  }
  
  public ASN1Integer getNonce() {
    return this.nonce;
  }
  
  public GeneralName getTsa() {
    return this.tsa;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.tsaPolicyId);
    aSN1EncodableVector.add((ASN1Encodable)this.messageImprint);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.genTime);
    if (this.accuracy != null)
      aSN1EncodableVector.add((ASN1Encodable)this.accuracy); 
    if (this.ordering != null && this.ordering.isTrue())
      aSN1EncodableVector.add((ASN1Encodable)this.ordering); 
    if (this.nonce != null)
      aSN1EncodableVector.add((ASN1Encodable)this.nonce); 
    if (this.tsa != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.tsa)); 
    if (this.extensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.extensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
