package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class PKCS12BagAttributeCarrierImpl implements PKCS12BagAttributeCarrier {
  private Hashtable pkcs12Attributes;
  
  private Vector pkcs12Ordering;
  
  PKCS12BagAttributeCarrierImpl(Hashtable paramHashtable, Vector paramVector) {
    this.pkcs12Attributes = paramHashtable;
    this.pkcs12Ordering = paramVector;
  }
  
  public PKCS12BagAttributeCarrierImpl() {
    this(new Hashtable<Object, Object>(), new Vector());
  }
  
  public void setBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    if (this.pkcs12Attributes.containsKey(paramASN1ObjectIdentifier)) {
      this.pkcs12Attributes.put(paramASN1ObjectIdentifier, paramASN1Encodable);
    } else {
      this.pkcs12Attributes.put(paramASN1ObjectIdentifier, paramASN1Encodable);
      this.pkcs12Ordering.addElement(paramASN1ObjectIdentifier);
    } 
  }
  
  public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (ASN1Encodable)this.pkcs12Attributes.get(paramASN1ObjectIdentifier);
  }
  
  public Enumeration getBagAttributeKeys() {
    return this.pkcs12Ordering.elements();
  }
  
  int size() {
    return this.pkcs12Ordering.size();
  }
  
  Hashtable getAttributes() {
    return this.pkcs12Attributes;
  }
  
  Vector getOrdering() {
    return this.pkcs12Ordering;
  }
  
  public void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.pkcs12Ordering.size() == 0) {
      paramObjectOutputStream.writeObject(new Hashtable<Object, Object>());
      paramObjectOutputStream.writeObject(new Vector());
    } else {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
      Enumeration<ASN1ObjectIdentifier> enumeration = getBagAttributeKeys();
      while (enumeration.hasMoreElements()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
        aSN1OutputStream.writeObject((ASN1Encodable)aSN1ObjectIdentifier);
        aSN1OutputStream.writeObject((ASN1Encodable)this.pkcs12Attributes.get(aSN1ObjectIdentifier));
      } 
      paramObjectOutputStream.writeObject(byteArrayOutputStream.toByteArray());
    } 
  }
  
  public void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Object object = paramObjectInputStream.readObject();
    if (object instanceof Hashtable) {
      this.pkcs12Attributes = (Hashtable)object;
      this.pkcs12Ordering = (Vector)paramObjectInputStream.readObject();
    } else {
      ASN1InputStream aSN1InputStream = new ASN1InputStream((byte[])object);
      ASN1ObjectIdentifier aSN1ObjectIdentifier;
      while ((aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1InputStream.readObject()) != null)
        setBagAttribute(aSN1ObjectIdentifier, (ASN1Encodable)aSN1InputStream.readObject()); 
    } 
  }
}
