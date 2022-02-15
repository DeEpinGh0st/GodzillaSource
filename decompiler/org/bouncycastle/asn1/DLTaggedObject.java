package org.bouncycastle.asn1;

import java.io.IOException;

public class DLTaggedObject extends ASN1TaggedObject {
  private static final byte[] ZERO_BYTES = new byte[0];
  
  public DLTaggedObject(boolean paramBoolean, int paramInt, ASN1Encodable paramASN1Encodable) {
    super(paramBoolean, paramInt, paramASN1Encodable);
  }
  
  boolean isConstructed() {
    if (!this.empty) {
      if (this.explicit)
        return true; 
      ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDLObject();
      return aSN1Primitive.isConstructed();
    } 
    return true;
  }
  
  int encodedLength() throws IOException {
    if (!this.empty) {
      int i = this.obj.toASN1Primitive().toDLObject().encodedLength();
      return this.explicit ? (StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(i) + i) : (StreamUtil.calculateTagLength(this.tagNo) + --i);
    } 
    return StreamUtil.calculateTagLength(this.tagNo) + 1;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    if (!this.empty) {
      ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDLObject();
      if (this.explicit) {
        paramASN1OutputStream.writeTag(160, this.tagNo);
        paramASN1OutputStream.writeLength(aSN1Primitive.encodedLength());
        paramASN1OutputStream.writeObject(aSN1Primitive);
      } else {
        char c;
        if (aSN1Primitive.isConstructed()) {
          c = ' ';
        } else {
          c = '';
        } 
        paramASN1OutputStream.writeTag(c, this.tagNo);
        paramASN1OutputStream.writeImplicitObject(aSN1Primitive);
      } 
    } else {
      paramASN1OutputStream.writeEncoded(160, this.tagNo, ZERO_BYTES);
    } 
  }
}
