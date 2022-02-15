package org.bouncycastle.asn1.est;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.Attribute;

public class AttrOrOID extends ASN1Object implements ASN1Choice {
  private final ASN1ObjectIdentifier oid;
  
  private final Attribute attribute;
  
  public AttrOrOID(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.oid = paramASN1ObjectIdentifier;
    this.attribute = null;
  }
  
  public AttrOrOID(Attribute paramAttribute) {
    this.oid = null;
    this.attribute = paramAttribute;
  }
  
  public static AttrOrOID getInstance(Object paramObject) {
    if (paramObject instanceof AttrOrOID)
      return (AttrOrOID)paramObject; 
    if (paramObject != null) {
      if (paramObject instanceof ASN1Encodable) {
        ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
        if (aSN1Primitive instanceof ASN1ObjectIdentifier)
          return new AttrOrOID(ASN1ObjectIdentifier.getInstance(aSN1Primitive)); 
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Sequence)
          return new AttrOrOID(Attribute.getInstance(aSN1Primitive)); 
      } 
      if (paramObject instanceof byte[])
        try {
          return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
        } catch (IOException iOException) {
          throw new IllegalArgumentException("unknown encoding in getInstance()");
        }  
      throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
    } 
    return null;
  }
  
  public boolean isOid() {
    return (this.oid != null);
  }
  
  public ASN1ObjectIdentifier getOid() {
    return this.oid;
  }
  
  public Attribute getAttribute() {
    return this.attribute;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.oid != null) ? this.oid : this.attribute.toASN1Primitive());
  }
}
