package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class BodyPartID extends ASN1Object {
  public static final long bodyIdMax = 4294967295L;
  
  private final long id;
  
  public BodyPartID(long paramLong) {
    if (paramLong < 0L || paramLong > 4294967295L)
      throw new IllegalArgumentException("id out of range"); 
    this.id = paramLong;
  }
  
  private static long convert(BigInteger paramBigInteger) {
    if (paramBigInteger.bitLength() > 32)
      throw new IllegalArgumentException("id out of range"); 
    return paramBigInteger.longValue();
  }
  
  private BodyPartID(ASN1Integer paramASN1Integer) {
    this(convert(paramASN1Integer.getValue()));
  }
  
  public static BodyPartID getInstance(Object paramObject) {
    return (paramObject instanceof BodyPartID) ? (BodyPartID)paramObject : ((paramObject != null) ? new BodyPartID(ASN1Integer.getInstance(paramObject)) : null);
  }
  
  public long getID() {
    return this.id;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new ASN1Integer(this.id);
  }
}
