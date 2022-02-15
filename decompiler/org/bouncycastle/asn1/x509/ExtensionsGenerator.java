package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class ExtensionsGenerator {
  private Hashtable extensions = new Hashtable<Object, Object>();
  
  private Vector extOrdering = new Vector();
  
  public void reset() {
    this.extensions = new Hashtable<Object, Object>();
    this.extOrdering = new Vector();
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws IOException {
    addExtension(paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) {
    if (this.extensions.containsKey(paramASN1ObjectIdentifier))
      throw new IllegalArgumentException("extension " + paramASN1ObjectIdentifier + " already added"); 
    this.extOrdering.addElement(paramASN1ObjectIdentifier);
    this.extensions.put(paramASN1ObjectIdentifier, new Extension(paramASN1ObjectIdentifier, paramBoolean, (ASN1OctetString)new DEROctetString(paramArrayOfbyte)));
  }
  
  public void addExtension(Extension paramExtension) {
    if (this.extensions.containsKey(paramExtension.getExtnId()))
      throw new IllegalArgumentException("extension " + paramExtension.getExtnId() + " already added"); 
    this.extOrdering.addElement(paramExtension.getExtnId());
    this.extensions.put(paramExtension.getExtnId(), paramExtension);
  }
  
  public boolean isEmpty() {
    return this.extOrdering.isEmpty();
  }
  
  public Extensions generate() {
    Extension[] arrayOfExtension = new Extension[this.extOrdering.size()];
    for (byte b = 0; b != this.extOrdering.size(); b++)
      arrayOfExtension[b] = (Extension)this.extensions.get(this.extOrdering.elementAt(b)); 
    return new Extensions(arrayOfExtension);
  }
}
