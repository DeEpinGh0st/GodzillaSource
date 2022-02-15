package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

class LazyConstructionEnumeration implements Enumeration {
  private ASN1InputStream aIn;
  
  private Object nextObj;
  
  public LazyConstructionEnumeration(byte[] paramArrayOfbyte) {
    this.aIn = new ASN1InputStream(paramArrayOfbyte, true);
    this.nextObj = readObject();
  }
  
  public boolean hasMoreElements() {
    return (this.nextObj != null);
  }
  
  public Object nextElement() {
    Object object = this.nextObj;
    this.nextObj = readObject();
    return object;
  }
  
  private Object readObject() {
    try {
      return this.aIn.readObject();
    } catch (IOException iOException) {
      throw new ASN1ParsingException("malformed DER construction: " + iOException, iOException);
    } 
  }
}
