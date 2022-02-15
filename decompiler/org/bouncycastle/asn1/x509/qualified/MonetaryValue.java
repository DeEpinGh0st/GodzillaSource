package org.bouncycastle.asn1.x509.qualified;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class MonetaryValue extends ASN1Object {
  private Iso4217CurrencyCode currency;
  
  private ASN1Integer amount;
  
  private ASN1Integer exponent;
  
  public static MonetaryValue getInstance(Object paramObject) {
    return (paramObject instanceof MonetaryValue) ? (MonetaryValue)paramObject : ((paramObject != null) ? new MonetaryValue(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private MonetaryValue(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.currency = Iso4217CurrencyCode.getInstance(enumeration.nextElement());
    this.amount = ASN1Integer.getInstance(enumeration.nextElement());
    this.exponent = ASN1Integer.getInstance(enumeration.nextElement());
  }
  
  public MonetaryValue(Iso4217CurrencyCode paramIso4217CurrencyCode, int paramInt1, int paramInt2) {
    this.currency = paramIso4217CurrencyCode;
    this.amount = new ASN1Integer(paramInt1);
    this.exponent = new ASN1Integer(paramInt2);
  }
  
  public Iso4217CurrencyCode getCurrency() {
    return this.currency;
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
