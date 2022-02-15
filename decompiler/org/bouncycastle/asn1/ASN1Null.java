package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Null extends ASN1Primitive {
  public static ASN1Null getInstance(Object paramObject) {
    if (paramObject instanceof ASN1Null)
      return (ASN1Null)paramObject; 
    if (paramObject != null)
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct NULL from byte[]: " + iOException.getMessage());
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
      }  
    return null;
  }
  
  public int hashCode() {
    return -1;
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !!(paramASN1Primitive instanceof ASN1Null);
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  public String toString() {
    return "NULL";
  }
}
