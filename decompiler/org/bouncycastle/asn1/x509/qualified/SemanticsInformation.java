package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class SemanticsInformation extends ASN1Object {
  private ASN1ObjectIdentifier semanticsIdentifier;
  
  private GeneralName[] nameRegistrationAuthorities;
  
  public static SemanticsInformation getInstance(Object paramObject) {
    return (paramObject instanceof SemanticsInformation) ? (SemanticsInformation)paramObject : ((paramObject != null) ? new SemanticsInformation(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SemanticsInformation(ASN1Sequence paramASN1Sequence) {
    Enumeration<Object> enumeration = paramASN1Sequence.getObjects();
    if (paramASN1Sequence.size() < 1)
      throw new IllegalArgumentException("no objects in SemanticsInformation"); 
    Object object = enumeration.nextElement();
    if (object instanceof ASN1ObjectIdentifier) {
      this.semanticsIdentifier = ASN1ObjectIdentifier.getInstance(object);
      if (enumeration.hasMoreElements()) {
        object = enumeration.nextElement();
      } else {
        object = null;
      } 
    } 
    if (object != null) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(object);
      this.nameRegistrationAuthorities = new GeneralName[aSN1Sequence.size()];
      for (byte b = 0; b < aSN1Sequence.size(); b++)
        this.nameRegistrationAuthorities[b] = GeneralName.getInstance(aSN1Sequence.getObjectAt(b)); 
    } 
  }
  
  public SemanticsInformation(ASN1ObjectIdentifier paramASN1ObjectIdentifier, GeneralName[] paramArrayOfGeneralName) {
    this.semanticsIdentifier = paramASN1ObjectIdentifier;
    this.nameRegistrationAuthorities = cloneNames(paramArrayOfGeneralName);
  }
  
  public SemanticsInformation(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.semanticsIdentifier = paramASN1ObjectIdentifier;
    this.nameRegistrationAuthorities = null;
  }
  
  public SemanticsInformation(GeneralName[] paramArrayOfGeneralName) {
    this.semanticsIdentifier = null;
    this.nameRegistrationAuthorities = cloneNames(paramArrayOfGeneralName);
  }
  
  public ASN1ObjectIdentifier getSemanticsIdentifier() {
    return this.semanticsIdentifier;
  }
  
  public GeneralName[] getNameRegistrationAuthorities() {
    return cloneNames(this.nameRegistrationAuthorities);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.semanticsIdentifier != null)
      aSN1EncodableVector.add((ASN1Encodable)this.semanticsIdentifier); 
    if (this.nameRegistrationAuthorities != null) {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      for (byte b = 0; b < this.nameRegistrationAuthorities.length; b++)
        aSN1EncodableVector1.add((ASN1Encodable)this.nameRegistrationAuthorities[b]); 
      aSN1EncodableVector.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private static GeneralName[] cloneNames(GeneralName[] paramArrayOfGeneralName) {
    if (paramArrayOfGeneralName != null) {
      GeneralName[] arrayOfGeneralName = new GeneralName[paramArrayOfGeneralName.length];
      System.arraycopy(paramArrayOfGeneralName, 0, arrayOfGeneralName, 0, paramArrayOfGeneralName.length);
      return arrayOfGeneralName;
    } 
    return null;
  }
}
