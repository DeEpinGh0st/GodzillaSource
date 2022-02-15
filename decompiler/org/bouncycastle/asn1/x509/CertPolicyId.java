package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

public class CertPolicyId extends ASN1Object {
  private ASN1ObjectIdentifier id;
  
  private CertPolicyId(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.id = paramASN1ObjectIdentifier;
  }
  
  public static CertPolicyId getInstance(Object paramObject) {
    return (paramObject instanceof CertPolicyId) ? (CertPolicyId)paramObject : ((paramObject != null) ? new CertPolicyId(ASN1ObjectIdentifier.getInstance(paramObject)) : null);
  }
  
  public String getId() {
    return this.id.getId();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.id;
  }
}
