package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;

public class AttributeTable {
  private Hashtable attributes = new Hashtable<Object, Object>();
  
  public AttributeTable(Hashtable paramHashtable) {
    this.attributes = copyTable(paramHashtable);
  }
  
  public AttributeTable(ASN1EncodableVector paramASN1EncodableVector) {
    for (byte b = 0; b != paramASN1EncodableVector.size(); b++) {
      Attribute attribute = Attribute.getInstance(paramASN1EncodableVector.get(b));
      addAttribute(attribute.getAttrType(), attribute);
    } 
  }
  
  public AttributeTable(ASN1Set paramASN1Set) {
    for (byte b = 0; b != paramASN1Set.size(); b++) {
      Attribute attribute = Attribute.getInstance(paramASN1Set.getObjectAt(b));
      addAttribute(attribute.getAttrType(), attribute);
    } 
  }
  
  public AttributeTable(Attribute paramAttribute) {
    addAttribute(paramAttribute.getAttrType(), paramAttribute);
  }
  
  public AttributeTable(Attributes paramAttributes) {
    this(ASN1Set.getInstance(paramAttributes.toASN1Primitive()));
  }
  
  private void addAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Attribute paramAttribute) {
    Object object = this.attributes.get(paramASN1ObjectIdentifier);
    if (object == null) {
      this.attributes.put(paramASN1ObjectIdentifier, paramAttribute);
    } else {
      Vector<Attribute> vector;
      if (object instanceof Attribute) {
        Vector<Object> vector1 = new Vector();
        vector1.addElement(object);
        vector1.addElement(paramAttribute);
      } else {
        vector = (Vector)object;
        vector.addElement(paramAttribute);
      } 
      this.attributes.put(paramASN1ObjectIdentifier, vector);
    } 
  }
  
  public Attribute get(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Object object = this.attributes.get(paramASN1ObjectIdentifier);
    return (object instanceof Vector) ? ((Vector<Attribute>)object).elementAt(0) : (Attribute)object;
  }
  
  public ASN1EncodableVector getAll(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Object object = this.attributes.get(paramASN1ObjectIdentifier);
    if (object instanceof Vector) {
      Enumeration<Attribute> enumeration = ((Vector)object).elements();
      while (enumeration.hasMoreElements())
        aSN1EncodableVector.add((ASN1Encodable)enumeration.nextElement()); 
    } else if (object != null) {
      aSN1EncodableVector.add((ASN1Encodable)object);
    } 
    return aSN1EncodableVector;
  }
  
  public int size() {
    int i = 0;
    Enumeration<Object> enumeration = this.attributes.elements();
    while (enumeration.hasMoreElements()) {
      Vector vector = (Vector)enumeration.nextElement();
      if (vector instanceof Vector) {
        i += ((Vector)vector).size();
        continue;
      } 
      i++;
    } 
    return i;
  }
  
  public Hashtable toHashtable() {
    return copyTable(this.attributes);
  }
  
  public ASN1EncodableVector toASN1EncodableVector() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration<Object> enumeration = this.attributes.elements();
    while (enumeration.hasMoreElements()) {
      Vector vector = (Vector)enumeration.nextElement();
      if (vector instanceof Vector) {
        Enumeration enumeration1 = ((Vector)vector).elements();
        while (enumeration1.hasMoreElements())
          aSN1EncodableVector.add((ASN1Encodable)Attribute.getInstance(enumeration1.nextElement())); 
        continue;
      } 
      aSN1EncodableVector.add((ASN1Encodable)Attribute.getInstance(vector));
    } 
    return aSN1EncodableVector;
  }
  
  public Attributes toASN1Structure() {
    return new Attributes(toASN1EncodableVector());
  }
  
  private Hashtable copyTable(Hashtable paramHashtable) {
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    Enumeration<Object> enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      hashtable.put(object, paramHashtable.get(object));
    } 
    return hashtable;
  }
  
  public AttributeTable add(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    AttributeTable attributeTable = new AttributeTable(this.attributes);
    attributeTable.addAttribute(paramASN1ObjectIdentifier, new Attribute(paramASN1ObjectIdentifier, (ASN1Set)new DERSet(paramASN1Encodable)));
    return attributeTable;
  }
  
  public AttributeTable remove(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    AttributeTable attributeTable = new AttributeTable(this.attributes);
    attributeTable.attributes.remove(paramASN1ObjectIdentifier);
    return attributeTable;
  }
}
