package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

class LazyEncodedSequence extends ASN1Sequence {
  private byte[] encoded;
  
  LazyEncodedSequence(byte[] paramArrayOfbyte) throws IOException {
    this.encoded = paramArrayOfbyte;
  }
  
  private void parse() {
    LazyConstructionEnumeration lazyConstructionEnumeration = new LazyConstructionEnumeration(this.encoded);
    while (lazyConstructionEnumeration.hasMoreElements())
      this.seq.addElement(lazyConstructionEnumeration.nextElement()); 
    this.encoded = null;
  }
  
  public synchronized ASN1Encodable getObjectAt(int paramInt) {
    if (this.encoded != null)
      parse(); 
    return super.getObjectAt(paramInt);
  }
  
  public synchronized Enumeration getObjects() {
    return (this.encoded == null) ? super.getObjects() : new LazyConstructionEnumeration(this.encoded);
  }
  
  public synchronized int size() {
    if (this.encoded != null)
      parse(); 
    return super.size();
  }
  
  ASN1Primitive toDERObject() {
    if (this.encoded != null)
      parse(); 
    return super.toDERObject();
  }
  
  ASN1Primitive toDLObject() {
    if (this.encoded != null)
      parse(); 
    return super.toDLObject();
  }
  
  int encodedLength() throws IOException {
    return (this.encoded != null) ? (1 + StreamUtil.calculateBodyLength(this.encoded.length) + this.encoded.length) : super.toDLObject().encodedLength();
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    if (this.encoded != null) {
      paramASN1OutputStream.writeEncoded(48, this.encoded);
    } else {
      super.toDLObject().encode(paramASN1OutputStream);
    } 
  }
}
