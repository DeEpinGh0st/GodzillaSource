package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class X962Parameters extends ASN1Object implements ASN1Choice {
  private ASN1Primitive params = null;
  
  public static X962Parameters getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof X962Parameters)
      return (X962Parameters)paramObject; 
    if (paramObject instanceof ASN1Primitive)
      return new X962Parameters((ASN1Primitive)paramObject); 
    if (paramObject instanceof byte[])
      try {
        return new X962Parameters(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (Exception exception) {
        throw new IllegalArgumentException("unable to parse encoded data: " + exception.getMessage());
      }  
    throw new IllegalArgumentException("unknown object in getInstance()");
  }
  
  public static X962Parameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public X962Parameters(X9ECParameters paramX9ECParameters) {
    this.params = paramX9ECParameters.toASN1Primitive();
  }
  
  public X962Parameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.params = (ASN1Primitive)paramASN1ObjectIdentifier;
  }
  
  public X962Parameters(ASN1Null paramASN1Null) {
    this.params = (ASN1Primitive)paramASN1Null;
  }
  
  public X962Parameters(ASN1Primitive paramASN1Primitive) {
    this.params = paramASN1Primitive;
  }
  
  public boolean isNamedCurve() {
    return this.params instanceof ASN1ObjectIdentifier;
  }
  
  public boolean isImplicitlyCA() {
    return this.params instanceof ASN1Null;
  }
  
  public ASN1Primitive getParameters() {
    return this.params;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.params;
  }
}
