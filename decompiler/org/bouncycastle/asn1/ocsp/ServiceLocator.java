package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;

public class ServiceLocator extends ASN1Object {
  private final X500Name issuer;
  
  private final AuthorityInformationAccess locator;
  
  private ServiceLocator(ASN1Sequence paramASN1Sequence) {
    this.issuer = X500Name.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2) {
      this.locator = AuthorityInformationAccess.getInstance(paramASN1Sequence.getObjectAt(1));
    } else {
      this.locator = null;
    } 
  }
  
  public static ServiceLocator getInstance(Object paramObject) {
    return (paramObject instanceof ServiceLocator) ? (ServiceLocator)paramObject : ((paramObject != null) ? new ServiceLocator(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public AuthorityInformationAccess getLocator() {
    return this.locator;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    if (this.locator != null)
      aSN1EncodableVector.add((ASN1Encodable)this.locator); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
