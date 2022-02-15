package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;

public class IetfAttrSyntax extends ASN1Object {
  public static final int VALUE_OCTETS = 1;
  
  public static final int VALUE_OID = 2;
  
  public static final int VALUE_UTF8 = 3;
  
  GeneralNames policyAuthority = null;
  
  Vector values = new Vector();
  
  int valueChoice = -1;
  
  public static IetfAttrSyntax getInstance(Object paramObject) {
    return (paramObject instanceof IetfAttrSyntax) ? (IetfAttrSyntax)paramObject : ((paramObject != null) ? new IetfAttrSyntax(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private IetfAttrSyntax(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
      this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(0), false);
      b++;
    } else if (paramASN1Sequence.size() == 2) {
      this.policyAuthority = GeneralNames.getInstance(paramASN1Sequence.getObjectAt(0));
      b++;
    } 
    if (!(paramASN1Sequence.getObjectAt(b) instanceof ASN1Sequence))
      throw new IllegalArgumentException("Non-IetfAttrSyntax encoding"); 
    paramASN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(b);
    Enumeration<ASN1Primitive> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      boolean bool;
      ASN1Primitive aSN1Primitive = enumeration.nextElement();
      if (aSN1Primitive instanceof ASN1ObjectIdentifier) {
        bool = true;
      } else if (aSN1Primitive instanceof DERUTF8String) {
        bool = true;
      } else if (aSN1Primitive instanceof org.bouncycastle.asn1.DEROctetString) {
        bool = true;
      } else {
        throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax");
      } 
      if (this.valueChoice < 0)
        this.valueChoice = bool; 
      if (bool != this.valueChoice)
        throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax"); 
      this.values.addElement(aSN1Primitive);
    } 
  }
  
  public GeneralNames getPolicyAuthority() {
    return this.policyAuthority;
  }
  
  public int getValueType() {
    return this.valueChoice;
  }
  
  public Object[] getValues() {
    if (getValueType() == 1) {
      ASN1OctetString[] arrayOfASN1OctetString = new ASN1OctetString[this.values.size()];
      for (byte b1 = 0; b1 != arrayOfASN1OctetString.length; b1++)
        arrayOfASN1OctetString[b1] = this.values.elementAt(b1); 
      return (Object[])arrayOfASN1OctetString;
    } 
    if (getValueType() == 2) {
      ASN1ObjectIdentifier[] arrayOfASN1ObjectIdentifier = new ASN1ObjectIdentifier[this.values.size()];
      for (byte b1 = 0; b1 != arrayOfASN1ObjectIdentifier.length; b1++)
        arrayOfASN1ObjectIdentifier[b1] = this.values.elementAt(b1); 
      return (Object[])arrayOfASN1ObjectIdentifier;
    } 
    DERUTF8String[] arrayOfDERUTF8String = new DERUTF8String[this.values.size()];
    for (byte b = 0; b != arrayOfDERUTF8String.length; b++)
      arrayOfDERUTF8String[b] = this.values.elementAt(b); 
    return (Object[])arrayOfDERUTF8String;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    if (this.policyAuthority != null)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.policyAuthority)); 
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    Enumeration<ASN1Encodable> enumeration = this.values.elements();
    while (enumeration.hasMoreElements())
      aSN1EncodableVector2.add(enumeration.nextElement()); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
}
