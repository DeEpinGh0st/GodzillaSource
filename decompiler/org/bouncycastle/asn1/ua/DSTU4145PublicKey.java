package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECPoint;

public class DSTU4145PublicKey extends ASN1Object {
  private ASN1OctetString pubKey;
  
  public DSTU4145PublicKey(ECPoint paramECPoint) {
    this.pubKey = (ASN1OctetString)new DEROctetString(DSTU4145PointEncoder.encodePoint(paramECPoint));
  }
  
  private DSTU4145PublicKey(ASN1OctetString paramASN1OctetString) {
    this.pubKey = paramASN1OctetString;
  }
  
  public static DSTU4145PublicKey getInstance(Object paramObject) {
    return (paramObject instanceof DSTU4145PublicKey) ? (DSTU4145PublicKey)paramObject : ((paramObject != null) ? new DSTU4145PublicKey(ASN1OctetString.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.pubKey;
  }
}
