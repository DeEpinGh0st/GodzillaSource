package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DERExternal extends ASN1Primitive {
  private ASN1ObjectIdentifier directReference;
  
  private ASN1Integer indirectReference;
  
  private ASN1Primitive dataValueDescriptor;
  
  private int encoding;
  
  private ASN1Primitive externalContent;
  
  public DERExternal(ASN1EncodableVector paramASN1EncodableVector) {
    byte b = 0;
    ASN1Primitive aSN1Primitive = getObjFromVector(paramASN1EncodableVector, b);
    if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
      this.directReference = (ASN1ObjectIdentifier)aSN1Primitive;
      aSN1Primitive = getObjFromVector(paramASN1EncodableVector, ++b);
    } 
    if (aSN1Primitive instanceof ASN1Integer) {
      this.indirectReference = (ASN1Integer)aSN1Primitive;
      aSN1Primitive = getObjFromVector(paramASN1EncodableVector, ++b);
    } 
    if (!(aSN1Primitive instanceof ASN1TaggedObject)) {
      this.dataValueDescriptor = aSN1Primitive;
      aSN1Primitive = getObjFromVector(paramASN1EncodableVector, ++b);
    } 
    if (paramASN1EncodableVector.size() != b + 1)
      throw new IllegalArgumentException("input vector too large"); 
    if (!(aSN1Primitive instanceof ASN1TaggedObject))
      throw new IllegalArgumentException("No tagged object found in vector. Structure doesn't seem to be of type External"); 
    ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
    setEncoding(aSN1TaggedObject.getTagNo());
    this.externalContent = aSN1TaggedObject.getObject();
  }
  
  private ASN1Primitive getObjFromVector(ASN1EncodableVector paramASN1EncodableVector, int paramInt) {
    if (paramASN1EncodableVector.size() <= paramInt)
      throw new IllegalArgumentException("too few objects in input vector"); 
    return paramASN1EncodableVector.get(paramInt).toASN1Primitive();
  }
  
  public DERExternal(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Integer paramASN1Integer, ASN1Primitive paramASN1Primitive, DERTaggedObject paramDERTaggedObject) {
    this(paramASN1ObjectIdentifier, paramASN1Integer, paramASN1Primitive, paramDERTaggedObject.getTagNo(), paramDERTaggedObject.toASN1Primitive());
  }
  
  public DERExternal(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Integer paramASN1Integer, ASN1Primitive paramASN1Primitive1, int paramInt, ASN1Primitive paramASN1Primitive2) {
    setDirectReference(paramASN1ObjectIdentifier);
    setIndirectReference(paramASN1Integer);
    setDataValueDescriptor(paramASN1Primitive1);
    setEncoding(paramInt);
    setExternalContent(paramASN1Primitive2.toASN1Primitive());
  }
  
  public int hashCode() {
    int i = 0;
    if (this.directReference != null)
      i = this.directReference.hashCode(); 
    if (this.indirectReference != null)
      i ^= this.indirectReference.hashCode(); 
    if (this.dataValueDescriptor != null)
      i ^= this.dataValueDescriptor.hashCode(); 
    i ^= this.externalContent.hashCode();
    return i;
  }
  
  boolean isConstructed() {
    return true;
  }
  
  int encodedLength() throws IOException {
    return (getEncoded()).length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    if (this.directReference != null)
      byteArrayOutputStream.write(this.directReference.getEncoded("DER")); 
    if (this.indirectReference != null)
      byteArrayOutputStream.write(this.indirectReference.getEncoded("DER")); 
    if (this.dataValueDescriptor != null)
      byteArrayOutputStream.write(this.dataValueDescriptor.getEncoded("DER")); 
    DERTaggedObject dERTaggedObject = new DERTaggedObject(true, this.encoding, this.externalContent);
    byteArrayOutputStream.write(dERTaggedObject.getEncoded("DER"));
    paramASN1OutputStream.writeEncoded(32, 8, byteArrayOutputStream.toByteArray());
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERExternal))
      return false; 
    if (this == paramASN1Primitive)
      return true; 
    DERExternal dERExternal = (DERExternal)paramASN1Primitive;
    return (this.directReference != null && (dERExternal.directReference == null || !dERExternal.directReference.equals(this.directReference))) ? false : ((this.indirectReference != null && (dERExternal.indirectReference == null || !dERExternal.indirectReference.equals(this.indirectReference))) ? false : ((this.dataValueDescriptor != null && (dERExternal.dataValueDescriptor == null || !dERExternal.dataValueDescriptor.equals(this.dataValueDescriptor))) ? false : this.externalContent.equals(dERExternal.externalContent)));
  }
  
  public ASN1Primitive getDataValueDescriptor() {
    return this.dataValueDescriptor;
  }
  
  public ASN1ObjectIdentifier getDirectReference() {
    return this.directReference;
  }
  
  public int getEncoding() {
    return this.encoding;
  }
  
  public ASN1Primitive getExternalContent() {
    return this.externalContent;
  }
  
  public ASN1Integer getIndirectReference() {
    return this.indirectReference;
  }
  
  private void setDataValueDescriptor(ASN1Primitive paramASN1Primitive) {
    this.dataValueDescriptor = paramASN1Primitive;
  }
  
  private void setDirectReference(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.directReference = paramASN1ObjectIdentifier;
  }
  
  private void setEncoding(int paramInt) {
    if (paramInt < 0 || paramInt > 2)
      throw new IllegalArgumentException("invalid encoding value: " + paramInt); 
    this.encoding = paramInt;
  }
  
  private void setExternalContent(ASN1Primitive paramASN1Primitive) {
    this.externalContent = paramASN1Primitive;
  }
  
  private void setIndirectReference(ASN1Integer paramASN1Integer) {
    this.indirectReference = paramASN1Integer;
  }
}
