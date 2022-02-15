package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser {
  int tagNo;
  
  boolean empty = false;
  
  boolean explicit = true;
  
  ASN1Encodable obj = null;
  
  public static ASN1TaggedObject getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (paramBoolean)
      return (ASN1TaggedObject)paramASN1TaggedObject.getObject(); 
    throw new IllegalArgumentException("implicitly tagged tagged object");
  }
  
  public static ASN1TaggedObject getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1TaggedObject)
      return (ASN1TaggedObject)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return getInstance(fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + iOException.getMessage());
      }  
    throw new IllegalArgumentException("unknown object in getInstance: " + paramObject.getClass().getName());
  }
  
  public ASN1TaggedObject(boolean paramBoolean, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable instanceof ASN1Choice) {
      this.explicit = true;
    } else {
      this.explicit = paramBoolean;
    } 
    this.tagNo = paramInt;
    if (this.explicit) {
      this.obj = paramASN1Encodable;
    } else {
      ASN1Primitive aSN1Primitive = paramASN1Encodable.toASN1Primitive();
      if (aSN1Primitive instanceof ASN1Set)
        Object object = null; 
      this.obj = paramASN1Encodable;
    } 
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1TaggedObject))
      return false; 
    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Primitive;
    if (this.tagNo != aSN1TaggedObject.tagNo || this.empty != aSN1TaggedObject.empty || this.explicit != aSN1TaggedObject.explicit)
      return false; 
    if (this.obj == null) {
      if (aSN1TaggedObject.obj != null)
        return false; 
    } else if (!this.obj.toASN1Primitive().equals(aSN1TaggedObject.obj.toASN1Primitive())) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    int i = this.tagNo;
    if (this.obj != null)
      i ^= this.obj.hashCode(); 
    return i;
  }
  
  public int getTagNo() {
    return this.tagNo;
  }
  
  public boolean isExplicit() {
    return this.explicit;
  }
  
  public boolean isEmpty() {
    return this.empty;
  }
  
  public ASN1Primitive getObject() {
    return (this.obj != null) ? this.obj.toASN1Primitive() : null;
  }
  
  public ASN1Encodable getObjectParser(int paramInt, boolean paramBoolean) throws IOException {
    switch (paramInt) {
      case 17:
        return ASN1Set.getInstance(this, paramBoolean).parser();
      case 16:
        return ASN1Sequence.getInstance(this, paramBoolean).parser();
      case 4:
        return ASN1OctetString.getInstance(this, paramBoolean).parser();
    } 
    if (paramBoolean)
      return getObject(); 
    throw new ASN1Exception("implicit tagging not implemented for tag: " + paramInt);
  }
  
  public ASN1Primitive getLoadedObject() {
    return toASN1Primitive();
  }
  
  ASN1Primitive toDERObject() {
    return new DERTaggedObject(this.explicit, this.tagNo, this.obj);
  }
  
  ASN1Primitive toDLObject() {
    return new DLTaggedObject(this.explicit, this.tagNo, this.obj);
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  public String toString() {
    return "[" + this.tagNo + "]" + this.obj;
  }
}
