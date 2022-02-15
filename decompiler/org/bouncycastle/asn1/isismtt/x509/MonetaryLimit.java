package org.bouncycastle.asn1.isismtt.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;

public class MonetaryLimit extends ASN1Object {
  DERPrintableString currency;
  
  ASN1Integer amount;
  
  ASN1Integer exponent;
  
  public static MonetaryLimit getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof MonetaryLimit)
      return (MonetaryLimit)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new MonetaryLimit(ASN1Sequence.getInstance(paramObject)); 
    throw new IllegalArgumentException("unknown object in getInstance");
  }
  
  private MonetaryLimit(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.currency = DERPrintableString.getInstance(enumeration.nextElement());
    this.amount = ASN1Integer.getInstance(enumeration.nextElement());
    this.exponent = ASN1Integer.getInstance(enumeration.nextElement());
  }
  
  public MonetaryLimit(String paramString, int paramInt1, int paramInt2) {
    this.currency = new DERPrintableString(paramString, true);
    this.amount = new ASN1Integer(paramInt1);
    this.exponent = new ASN1Integer(paramInt2);
  }
  
  public String getCurrency() {
    return this.currency.getString();
  }
  
  public BigInteger getAmount() {
    return this.amount.getValue();
  }
  
  public BigInteger getExponent() {
    return this.exponent.getValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.currency);
    aSN1EncodableVector.add((ASN1Encodable)this.amount);
    aSN1EncodableVector.add((ASN1Encodable)this.exponent);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
