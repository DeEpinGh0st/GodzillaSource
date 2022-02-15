package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class X509Extensions extends ASN1Object {
  public static final ASN1ObjectIdentifier SubjectDirectoryAttributes = new ASN1ObjectIdentifier("2.5.29.9");
  
  public static final ASN1ObjectIdentifier SubjectKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.14");
  
  public static final ASN1ObjectIdentifier KeyUsage = new ASN1ObjectIdentifier("2.5.29.15");
  
  public static final ASN1ObjectIdentifier PrivateKeyUsagePeriod = new ASN1ObjectIdentifier("2.5.29.16");
  
  public static final ASN1ObjectIdentifier SubjectAlternativeName = new ASN1ObjectIdentifier("2.5.29.17");
  
  public static final ASN1ObjectIdentifier IssuerAlternativeName = new ASN1ObjectIdentifier("2.5.29.18");
  
  public static final ASN1ObjectIdentifier BasicConstraints = new ASN1ObjectIdentifier("2.5.29.19");
  
  public static final ASN1ObjectIdentifier CRLNumber = new ASN1ObjectIdentifier("2.5.29.20");
  
  public static final ASN1ObjectIdentifier ReasonCode = new ASN1ObjectIdentifier("2.5.29.21");
  
  public static final ASN1ObjectIdentifier InstructionCode = new ASN1ObjectIdentifier("2.5.29.23");
  
  public static final ASN1ObjectIdentifier InvalidityDate = new ASN1ObjectIdentifier("2.5.29.24");
  
  public static final ASN1ObjectIdentifier DeltaCRLIndicator = new ASN1ObjectIdentifier("2.5.29.27");
  
  public static final ASN1ObjectIdentifier IssuingDistributionPoint = new ASN1ObjectIdentifier("2.5.29.28");
  
  public static final ASN1ObjectIdentifier CertificateIssuer = new ASN1ObjectIdentifier("2.5.29.29");
  
  public static final ASN1ObjectIdentifier NameConstraints = new ASN1ObjectIdentifier("2.5.29.30");
  
  public static final ASN1ObjectIdentifier CRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31");
  
  public static final ASN1ObjectIdentifier CertificatePolicies = new ASN1ObjectIdentifier("2.5.29.32");
  
  public static final ASN1ObjectIdentifier PolicyMappings = new ASN1ObjectIdentifier("2.5.29.33");
  
  public static final ASN1ObjectIdentifier AuthorityKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.35");
  
  public static final ASN1ObjectIdentifier PolicyConstraints = new ASN1ObjectIdentifier("2.5.29.36");
  
  public static final ASN1ObjectIdentifier ExtendedKeyUsage = new ASN1ObjectIdentifier("2.5.29.37");
  
  public static final ASN1ObjectIdentifier FreshestCRL = new ASN1ObjectIdentifier("2.5.29.46");
  
  public static final ASN1ObjectIdentifier InhibitAnyPolicy = new ASN1ObjectIdentifier("2.5.29.54");
  
  public static final ASN1ObjectIdentifier AuthorityInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1");
  
  public static final ASN1ObjectIdentifier SubjectInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.11");
  
  public static final ASN1ObjectIdentifier LogoType = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.12");
  
  public static final ASN1ObjectIdentifier BiometricInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.2");
  
  public static final ASN1ObjectIdentifier QCStatements = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.3");
  
  public static final ASN1ObjectIdentifier AuditIdentity = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.4");
  
  public static final ASN1ObjectIdentifier NoRevAvail = new ASN1ObjectIdentifier("2.5.29.56");
  
  public static final ASN1ObjectIdentifier TargetInformation = new ASN1ObjectIdentifier("2.5.29.55");
  
  private Hashtable extensions = new Hashtable<Object, Object>();
  
  private Vector ordering = new Vector();
  
  public static X509Extensions getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static X509Extensions getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof X509Extensions)
      return (X509Extensions)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new X509Extensions((ASN1Sequence)paramObject); 
    if (paramObject instanceof Extensions)
      return new X509Extensions((ASN1Sequence)((Extensions)paramObject).toASN1Primitive()); 
    if (paramObject instanceof ASN1TaggedObject)
      return getInstance(((ASN1TaggedObject)paramObject).getObject()); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public X509Extensions(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(enumeration.nextElement());
      if (aSN1Sequence.size() == 3) {
        this.extensions.put(aSN1Sequence.getObjectAt(0), new X509Extension(ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(1)), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2))));
      } else if (aSN1Sequence.size() == 2) {
        this.extensions.put(aSN1Sequence.getObjectAt(0), new X509Extension(false, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1))));
      } else {
        throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
      } 
      this.ordering.addElement(aSN1Sequence.getObjectAt(0));
    } 
  }
  
  public X509Extensions(Hashtable paramHashtable) {
    this((Vector)null, paramHashtable);
  }
  
  public X509Extensions(Vector paramVector, Hashtable paramHashtable) {
    if (paramVector == null) {
      enumeration = paramHashtable.keys();
    } else {
      enumeration = paramVector.elements();
    } 
    while (enumeration.hasMoreElements())
      this.ordering.addElement(ASN1ObjectIdentifier.getInstance(enumeration.nextElement())); 
    Enumeration enumeration = this.ordering.elements();
    while (enumeration.hasMoreElements()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
      X509Extension x509Extension = (X509Extension)paramHashtable.get(aSN1ObjectIdentifier);
      this.extensions.put(aSN1ObjectIdentifier, x509Extension);
    } 
  }
  
  public X509Extensions(Vector paramVector1, Vector<X509Extension> paramVector2) {
    Enumeration<ASN1ObjectIdentifier> enumeration = paramVector1.elements();
    while (enumeration.hasMoreElements())
      this.ordering.addElement(enumeration.nextElement()); 
    byte b = 0;
    enumeration = this.ordering.elements();
    while (enumeration.hasMoreElements()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
      X509Extension x509Extension = paramVector2.elementAt(b);
      this.extensions.put(aSN1ObjectIdentifier, x509Extension);
      b++;
    } 
  }
  
  public Enumeration oids() {
    return this.ordering.elements();
  }
  
  public X509Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (X509Extension)this.extensions.get(paramASN1ObjectIdentifier);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration<ASN1ObjectIdentifier> enumeration = this.ordering.elements();
    while (enumeration.hasMoreElements()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
      X509Extension x509Extension = (X509Extension)this.extensions.get(aSN1ObjectIdentifier);
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      aSN1EncodableVector1.add((ASN1Encodable)aSN1ObjectIdentifier);
      if (x509Extension.isCritical())
        aSN1EncodableVector1.add((ASN1Encodable)ASN1Boolean.TRUE); 
      aSN1EncodableVector1.add((ASN1Encodable)x509Extension.getValue());
      aSN1EncodableVector.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public boolean equivalent(X509Extensions paramX509Extensions) {
    if (this.extensions.size() != paramX509Extensions.extensions.size())
      return false; 
    Enumeration<Object> enumeration = this.extensions.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (!this.extensions.get(object).equals(paramX509Extensions.extensions.get(object)))
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
      if (((X509Extension)this.extensions.get(object)).isCritical() == paramBoolean)
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
