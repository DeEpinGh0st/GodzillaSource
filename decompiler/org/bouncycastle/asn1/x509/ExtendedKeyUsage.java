package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class ExtendedKeyUsage extends ASN1Object {
  Hashtable usageTable = new Hashtable<Object, Object>();
  
  ASN1Sequence seq;
  
  public static ExtendedKeyUsage getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static ExtendedKeyUsage getInstance(Object paramObject) {
    return (paramObject instanceof ExtendedKeyUsage) ? (ExtendedKeyUsage)paramObject : ((paramObject != null) ? new ExtendedKeyUsage(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static ExtendedKeyUsage fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.extendedKeyUsage));
  }
  
  public ExtendedKeyUsage(KeyPurposeId paramKeyPurposeId) {
    this.seq = (ASN1Sequence)new DERSequence((ASN1Encodable)paramKeyPurposeId);
    this.usageTable.put(paramKeyPurposeId, paramKeyPurposeId);
  }
  
  private ExtendedKeyUsage(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (!(aSN1Encodable.toASN1Primitive() instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier))
        throw new IllegalArgumentException("Only ASN1ObjectIdentifiers allowed in ExtendedKeyUsage."); 
      this.usageTable.put(aSN1Encodable, aSN1Encodable);
    } 
  }
  
  public ExtendedKeyUsage(KeyPurposeId[] paramArrayOfKeyPurposeId) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != paramArrayOfKeyPurposeId.length; b++) {
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfKeyPurposeId[b]);
      this.usageTable.put(paramArrayOfKeyPurposeId[b], paramArrayOfKeyPurposeId[b]);
    } 
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public ExtendedKeyUsage(Vector paramVector) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      KeyPurposeId keyPurposeId = KeyPurposeId.getInstance(enumeration.nextElement());
      aSN1EncodableVector.add((ASN1Encodable)keyPurposeId);
      this.usageTable.put(keyPurposeId, keyPurposeId);
    } 
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public boolean hasKeyPurposeId(KeyPurposeId paramKeyPurposeId) {
    return (this.usageTable.get(paramKeyPurposeId) != null);
  }
  
  public KeyPurposeId[] getUsages() {
    KeyPurposeId[] arrayOfKeyPurposeId = new KeyPurposeId[this.seq.size()];
    byte b = 0;
    Enumeration enumeration = this.seq.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfKeyPurposeId[b++] = KeyPurposeId.getInstance(enumeration.nextElement()); 
    return arrayOfKeyPurposeId;
  }
  
  public int size() {
    return this.usageTable.size();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
