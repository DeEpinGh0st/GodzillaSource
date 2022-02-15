package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class DHPublicKey extends ASN1Object {
  private ASN1Integer y;
  
  public static DHPublicKey getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Integer.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DHPublicKey getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DHPublicKey)
      return (DHPublicKey)paramObject; 
    if (paramObject instanceof ASN1Integer)
      return new DHPublicKey((ASN1Integer)paramObject); 
    throw new IllegalArgumentException("Invalid DHPublicKey: " + paramObject.getClass().getName());
  }
  
  private DHPublicKey(ASN1Integer paramASN1Integer) {
    if (paramASN1Integer == null)
      throw new IllegalArgumentException("'y' cannot be null"); 
    this.y = paramASN1Integer;
  }
  
  public DHPublicKey(BigInteger paramBigInteger) {
    if (paramBigInteger == null)
      throw new IllegalArgumentException("'y' cannot be null"); 
    this.y = new ASN1Integer(paramBigInteger);
  }
  
  public BigInteger getY() {
    return this.y.getPositiveValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.y;
  }
}
