package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Accuracy extends ASN1Object {
  ASN1Integer seconds;
  
  ASN1Integer millis;
  
  ASN1Integer micros;
  
  protected static final int MIN_MILLIS = 1;
  
  protected static final int MAX_MILLIS = 999;
  
  protected static final int MIN_MICROS = 1;
  
  protected static final int MAX_MICROS = 999;
  
  protected Accuracy() {}
  
  public Accuracy(ASN1Integer paramASN1Integer1, ASN1Integer paramASN1Integer2, ASN1Integer paramASN1Integer3) {
    this.seconds = paramASN1Integer1;
    if (paramASN1Integer2 != null && (paramASN1Integer2.getValue().intValue() < 1 || paramASN1Integer2.getValue().intValue() > 999))
      throw new IllegalArgumentException("Invalid millis field : not in (1..999)"); 
    this.millis = paramASN1Integer2;
    if (paramASN1Integer3 != null && (paramASN1Integer3.getValue().intValue() < 1 || paramASN1Integer3.getValue().intValue() > 999))
      throw new IllegalArgumentException("Invalid micros field : not in (1..999)"); 
    this.micros = paramASN1Integer3;
  }
  
  private Accuracy(ASN1Sequence paramASN1Sequence) {
    this.seconds = null;
    this.millis = null;
    this.micros = null;
    for (byte b = 0; b < paramASN1Sequence.size(); b++) {
      if (paramASN1Sequence.getObjectAt(b) instanceof ASN1Integer) {
        this.seconds = (ASN1Integer)paramASN1Sequence.getObjectAt(b);
      } else if (paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(b);
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            this.millis = ASN1Integer.getInstance(aSN1TaggedObject, false);
            if (this.millis.getValue().intValue() < 1 || this.millis.getValue().intValue() > 999)
              throw new IllegalArgumentException("Invalid millis field : not in (1..999)."); 
            break;
          case 1:
            this.micros = ASN1Integer.getInstance(aSN1TaggedObject, false);
            if (this.micros.getValue().intValue() < 1 || this.micros.getValue().intValue() > 999)
              throw new IllegalArgumentException("Invalid micros field : not in (1..999)."); 
            break;
          default:
            throw new IllegalArgumentException("Invalig tag number");
        } 
      } 
    } 
  }
  
  public static Accuracy getInstance(Object paramObject) {
    return (paramObject instanceof Accuracy) ? (Accuracy)paramObject : ((paramObject != null) ? new Accuracy(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getSeconds() {
    return this.seconds;
  }
  
  public ASN1Integer getMillis() {
    return this.millis;
  }
  
  public ASN1Integer getMicros() {
    return this.micros;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.seconds != null)
      aSN1EncodableVector.add((ASN1Encodable)this.seconds); 
    if (this.millis != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.millis)); 
    if (this.micros != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.micros)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
