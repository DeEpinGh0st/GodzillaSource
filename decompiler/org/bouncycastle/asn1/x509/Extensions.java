package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class Extensions extends ASN1Object {
  private Hashtable extensions = new Hashtable<Object, Object>();
  
  private Vector ordering = new Vector();
  
  public static Extensions getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static Extensions getInstance(Object paramObject) {
    return (paramObject instanceof Extensions) ? (Extensions)paramObject : ((paramObject != null) ? new Extensions(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private Extensions(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      Extension extension = Extension.getInstance(enumeration.nextElement());
      if (this.extensions.containsKey(extension.getExtnId()))
        throw new IllegalArgumentException("repeated extension found: " + extension.getExtnId()); 
      this.extensions.put(extension.getExtnId(), extension);
      this.ordering.addElement(extension.getExtnId());
    } 
  }
  
  public Extensions(Extension paramExtension) {
    this.ordering.addElement(paramExtension.getExtnId());
    this.extensions.put(paramExtension.getExtnId(), paramExtension);
  }
  
  public Extensions(Extension[] paramArrayOfExtension) {
    for (byte b = 0; b != paramArrayOfExtension.length; b++) {
      Extension extension = paramArrayOfExtension[b];
      this.ordering.addElement(extension.getExtnId());
      this.extensions.put(extension.getExtnId(), extension);
    } 
  }
  
  public Enumeration oids() {
    return this.ordering.elements();
  }
  
  public Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (Extension)this.extensions.get(paramASN1ObjectIdentifier);
  }
  
  public ASN1Encodable getExtensionParsedValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Extension extension = getExtension(paramASN1ObjectIdentifier);
    return (extension != null) ? extension.getParsedValue() : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration<ASN1ObjectIdentifier> enumeration = this.ordering.elements();
    while (enumeration.hasMoreElements()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
      Extension extension = (Extension)this.extensions.get(aSN1ObjectIdentifier);
      aSN1EncodableVector.add((ASN1Encodable)extension);
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public boolean equivalent(Extensions paramExtensions) {
    if (this.extensions.size() != paramExtensions.extensions.size())
      return false; 
    Enumeration<Object> enumeration = this.extensions.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (!this.extensions.get(object).equals(paramExtensions.extensions.get(object)))
        return false; 
    } 
    return true;
  }
  
  public ASN1ObjectIdentifier[] getExtensionOIDs() {
    return toOidArray(this.ordering);
  }
  
  public ASN1ObjectIdentifier[] getNonCriticalExtensionOIDs() {
    return getExtensionOIDs(false);
  }
  
  public ASN1ObjectIdentifier[] getCriticalExtensionOIDs() {
    return getExtensionOIDs(true);
  }
  
  private ASN1ObjectIdentifier[] getExtensionOIDs(boolean paramBoolean) {
    Vector vector = new Vector();
    for (byte b = 0; b != this.ordering.size(); b++) {
      Object object = this.ordering.elementAt(b);
      if (((Extension)this.extensions.get(object)).isCritical() == paramBoolean)
        vector.addElement(object); 
    } 
    return toOidArray(vector);
  }
  
  private ASN1ObjectIdentifier[] toOidArray(Vector<ASN1ObjectIdentifier> paramVector) {
    ASN1ObjectIdentifier[] arrayOfASN1ObjectIdentifier = new ASN1ObjectIdentifier[paramVector.size()];
    for (byte b = 0; b != arrayOfASN1ObjectIdentifier.length; b++)
      arrayOfASN1ObjectIdentifier[b] = paramVector.elementAt(b); 
    return arrayOfASN1ObjectIdentifier;
  }
}
