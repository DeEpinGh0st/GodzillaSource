package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public abstract class ASN1OctetString extends ASN1Primitive implements ASN1OctetStringParser {
  byte[] string;
  
  public static ASN1OctetString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1OctetString) ? getInstance(aSN1Primitive) : BEROctetString.fromSequence(ASN1Sequence.getInstance(aSN1Primitive));
  }
  
  public static ASN1OctetString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1OctetString)
      return (ASN1OctetString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + iOException.getMessage());
      }  
    if (paramObject instanceof ASN1Encodable) {
      ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
      if (aSN1Primitive instanceof ASN1OctetString)
        return (ASN1OctetString)aSN1Primitive; 
    } 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public ASN1OctetString(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("string cannot be null"); 
    this.string = paramArrayOfbyte;
  }
  
  public InputStream getOctetStream() {
    return new ByteArrayInputStream(this.string);
  }
  
  public ASN1OctetStringParser parser() {
    return this;
  }
  
  public byte[] getOctets() {
    return this.string;
  }
  
  public int hashCode() {
    return Arrays.hashCode(getOctets());
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1OctetString))
      return false; 
    ASN1OctetString aSN1OctetString = (ASN1OctetString)paramASN1Primitive;
    return Arrays.areEqual(this.string, aSN1OctetString.string);
  }
  
  public ASN1Primitive getLoadedObject() {
    return toASN1Primitive();
  }
  
  ASN1Primitive toDERObject() {
    return new DEROctetString(this.string);
  }
  
  ASN1Primitive toDLObject() {
    return new DEROctetString(this.string);
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  public String toString() {
    return "#" + Strings.fromByteArray(Hex.encode(this.string));
  }
}
