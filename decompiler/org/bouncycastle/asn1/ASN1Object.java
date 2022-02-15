package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.util.Encodable;

public abstract class ASN1Object implements ASN1Encodable, Encodable {
  public byte[] getEncoded() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
    aSN1OutputStream.writeObject(this);
    return byteArrayOutputStream.toByteArray();
  }
  
  public byte[] getEncoded(String paramString) throws IOException {
    if (paramString.equals("DER")) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
      dEROutputStream.writeObject(this);
      return byteArrayOutputStream.toByteArray();
    } 
    if (paramString.equals("DL")) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DLOutputStream dLOutputStream = new DLOutputStream(byteArrayOutputStream);
      dLOutputStream.writeObject(this);
      return byteArrayOutputStream.toByteArray();
    } 
    return getEncoded();
  }
  
  public int hashCode() {
    return toASN1Primitive().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ASN1Encodable))
      return false; 
    ASN1Encodable aSN1Encodable = (ASN1Encodable)paramObject;
    return toASN1Primitive().equals(aSN1Encodable.toASN1Primitive());
  }
  
  public ASN1Primitive toASN1Object() {
    return toASN1Primitive();
  }
  
  protected static boolean hasEncodedTagValue(Object paramObject, int paramInt) {
    return (paramObject instanceof byte[] && ((byte[])paramObject)[0] == paramInt);
  }
  
  public abstract ASN1Primitive toASN1Primitive();
}
