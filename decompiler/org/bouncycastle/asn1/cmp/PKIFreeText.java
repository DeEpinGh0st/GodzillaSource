package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class PKIFreeText extends ASN1Object {
  ASN1Sequence strings;
  
  public static PKIFreeText getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static PKIFreeText getInstance(Object paramObject) {
    return (paramObject instanceof PKIFreeText) ? (PKIFreeText)paramObject : ((paramObject != null) ? new PKIFreeText(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private PKIFreeText(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      if (!(enumeration.nextElement() instanceof DERUTF8String))
        throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText"); 
    } 
    this.strings = paramASN1Sequence;
  }
  
  public PKIFreeText(DERUTF8String paramDERUTF8String) {
    this.strings = (ASN1Sequence)new DERSequence((ASN1Encodable)paramDERUTF8String);
  }
  
  public PKIFreeText(String paramString) {
    this(new DERUTF8String(paramString));
  }
  
  public PKIFreeText(DERUTF8String[] paramArrayOfDERUTF8String) {
    this.strings = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfDERUTF8String);
  }
  
  public PKIFreeText(String[] paramArrayOfString) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b < paramArrayOfString.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)new DERUTF8String(paramArrayOfString[b])); 
    this.strings = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public int size() {
    return this.strings.size();
  }
  
  public DERUTF8String getStringAt(int paramInt) {
    return (DERUTF8String)this.strings.getObjectAt(paramInt);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.strings;
  }
}
