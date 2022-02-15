package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class BodyPartReference extends ASN1Object implements ASN1Choice {
  private final BodyPartID bodyPartID;
  
  private final BodyPartPath bodyPartPath;
  
  public BodyPartReference(BodyPartID paramBodyPartID) {
    this.bodyPartID = paramBodyPartID;
    this.bodyPartPath = null;
  }
  
  public BodyPartReference(BodyPartPath paramBodyPartPath) {
    this.bodyPartID = null;
    this.bodyPartPath = paramBodyPartPath;
  }
  
  public static BodyPartReference getInstance(Object paramObject) {
    if (paramObject instanceof BodyPartReference)
      return (BodyPartReference)paramObject; 
    if (paramObject != null) {
      if (paramObject instanceof ASN1Encodable) {
        ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Integer)
          return new BodyPartReference(BodyPartID.getInstance(aSN1Primitive)); 
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Sequence)
          return new BodyPartReference(BodyPartPath.getInstance(aSN1Primitive)); 
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
  
  public boolean isBodyPartID() {
    return (this.bodyPartID != null);
  }
  
  public BodyPartID getBodyPartID() {
    return this.bodyPartID;
  }
  
  public BodyPartPath getBodyPartPath() {
    return this.bodyPartPath;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (this.bodyPartID != null) ? this.bodyPartID.toASN1Primitive() : this.bodyPartPath.toASN1Primitive();
  }
}
