package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class X9ECPoint extends ASN1Object {
  private final ASN1OctetString encoding;
  
  private ECCurve c;
  
  private ECPoint p;
  
  public X9ECPoint(ECPoint paramECPoint) {
    this(paramECPoint, false);
  }
  
  public X9ECPoint(ECPoint paramECPoint, boolean paramBoolean) {
    this.p = paramECPoint.normalize();
    this.encoding = (ASN1OctetString)new DEROctetString(paramECPoint.getEncoded(paramBoolean));
  }
  
  public X9ECPoint(ECCurve paramECCurve, byte[] paramArrayOfbyte) {
    this.c = paramECCurve;
    this.encoding = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
  }
  
  public X9ECPoint(ECCurve paramECCurve, ASN1OctetString paramASN1OctetString) {
    this(paramECCurve, paramASN1OctetString.getOctets());
  }
  
  public byte[] getPointEncoding() {
    return Arrays.clone(this.encoding.getOctets());
  }
  
  public synchronized ECPoint getPoint() {
    if (this.p == null)
      this.p = this.c.decodePoint(this.encoding.getOctets()).normalize(); 
    return this.p;
  }
  
  public boolean isPointCompressed() {
    byte[] arrayOfByte = this.encoding.getOctets();
    return (arrayOfByte != null && arrayOfByte.length > 0 && (arrayOfByte[0] == 2 || arrayOfByte[0] == 3));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.encoding;
  }
}
