package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class X509ExtensionsGenerator {
  private Hashtable extensions = new Hashtable<Object, Object>();
  
  private Vector extOrdering = new Vector();
  
  public void reset() {
    this.extensions = new Hashtable<Object, Object>();
    this.extOrdering = new Vector();
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) {
    try {
      addExtension(paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error encoding value: " + iOException);
    } 
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) {
    if (this.extensions.containsKey(paramASN1ObjectIdentifier))
      throw new IllegalArgumentException("extension " + paramASN1ObjectIdentifier + " already added"); 
    this.extOrdering.addElement(paramASN1ObjectIdentifier);
    this.extensions.put(paramASN1ObjectIdentifier, new X509Extension(paramBoolean, (ASN1OctetString)new DEROctetString(paramArrayOfbyte)));
  }
  
  public boolean isEmpty() {
    return this.extOrdering.isEmpty();
  }
  
  public X509Extensions generate() {
    return new X509Extensions(this.extOrdering, this.extensions);
  }
}
