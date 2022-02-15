package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERTaggedObject extends ASN1TaggedObject {
  public BERTaggedObject(int paramInt, ASN1Encodable paramASN1Encodable) {
    super(true, paramInt, paramASN1Encodable);
  }
  
  public BERTaggedObject(boolean paramBoolean, int paramInt, ASN1Encodable paramASN1Encodable) {
    super(paramBoolean, paramInt, paramASN1Encodable);
  }
  
  public BERTaggedObject(int paramInt) {
    super(false, paramInt, new BERSequence());
  }
  
  boolean isConstructed() {
    if (!this.empty) {
      if (this.explicit)
        return true; 
      ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
      return aSN1Primitive.isConstructed();
    } 
    return true;
  }
  
  int encodedLength() throws IOException {
    if (!this.empty) {
      ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive();
      int i = aSN1Primitive.encodedLength();
      return this.explicit ? (StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(i) + i) : (StreamUtil.calculateTagLength(this.tagNo) + --i);
    } 
    return StreamUtil.calculateTagLength(this.tagNo) + 1;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeTag(160, this.tagNo);
    paramASN1OutputStream.write(128);
    if (!this.empty)
      if (!this.explicit) {
        Enumeration<ASN1Encodable> enumeration;
        if (this.obj instanceof ASN1OctetString) {
          if (this.obj instanceof BEROctetString) {
            enumeration = ((BEROctetString)this.obj).getObjects();
          } else {
            ASN1OctetString aSN1OctetString = (ASN1OctetString)this.obj;
            BEROctetString bEROctetString = new BEROctetString(aSN1OctetString.getOctets());
            enumeration = bEROctetString.getObjects();
          } 
        } else if (this.obj instanceof ASN1Sequence) {
          enumeration = ((ASN1Sequence)this.obj).getObjects();
        } else if (this.obj instanceof ASN1Set) {
          enumeration = ((ASN1Set)this.obj).getObjects();
        } else {
          throw new ASN1Exception("not implemented: " + this.obj.getClass().getName());
        } 
        while (enumeration.hasMoreElements())
          paramASN1OutputStream.writeObject(enumeration.nextElement()); 
      } else {
        paramASN1OutputStream.writeObject(this.obj);
      }  
    paramASN1OutputStream.write(0);
    paramASN1OutputStream.write(0);
  }
}
