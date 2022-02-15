package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class SubjectKeyIdentifier extends ASN1Object {
  private byte[] keyidentifier;
  
  public static SubjectKeyIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1OctetString.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static SubjectKeyIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof SubjectKeyIdentifier) ? (SubjectKeyIdentifier)paramObject : ((paramObject != null) ? new SubjectKeyIdentifier(ASN1OctetString.getInstance(paramObject)) : null);
  }
  
  public static SubjectKeyIdentifier fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.subjectKeyIdentifier));
  }
  
  public SubjectKeyIdentifier(byte[] paramArrayOfbyte) {
    this.keyidentifier = Arrays.clone(paramArrayOfbyte);
  }
  
  protected SubjectKeyIdentifier(ASN1OctetString paramASN1OctetString) {
    this(paramASN1OctetString.getOctets());
  }
  
  public byte[] getKeyIdentifier() {
    return Arrays.clone(this.keyidentifier);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DEROctetString(getKeyIdentifier());
  }
}
