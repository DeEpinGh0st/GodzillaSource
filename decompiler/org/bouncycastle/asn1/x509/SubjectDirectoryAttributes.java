package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SubjectDirectoryAttributes extends ASN1Object {
  private Vector attributes = new Vector();
  
  public static SubjectDirectoryAttributes getInstance(Object paramObject) {
    return (paramObject instanceof SubjectDirectoryAttributes) ? (SubjectDirectoryAttributes)paramObject : ((paramObject != null) ? new SubjectDirectoryAttributes(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SubjectDirectoryAttributes(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(enumeration.nextElement());
      this.attributes.addElement(Attribute.getInstance(aSN1Sequence));
    } 
  }
  
  public SubjectDirectoryAttributes(Vector paramVector) {
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements())
      this.attributes.addElement(enumeration.nextElement()); 
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration<Attribute> enumeration = this.attributes.elements();
    while (enumeration.hasMoreElements())
      aSN1EncodableVector.add((ASN1Encodable)enumeration.nextElement()); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public Vector getAttributes() {
    return this.attributes;
  }
}
