package org.bouncycastle.openssl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;

public class CertificateTrustBlock {
  private ASN1Sequence uses;
  
  private ASN1Sequence prohibitions;
  
  private String alias;
  
  public CertificateTrustBlock(Set<ASN1ObjectIdentifier> paramSet) {
    this(null, paramSet, null);
  }
  
  public CertificateTrustBlock(String paramString, Set<ASN1ObjectIdentifier> paramSet) {
    this(paramString, paramSet, null);
  }
  
  public CertificateTrustBlock(String paramString, Set<ASN1ObjectIdentifier> paramSet1, Set<ASN1ObjectIdentifier> paramSet2) {
    this.alias = paramString;
    this.uses = toSequence(paramSet1);
    this.prohibitions = toSequence(paramSet2);
  }
  
  CertificateTrustBlock(byte[] paramArrayOfbyte) {
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramArrayOfbyte);
    Enumeration<ASN1Encodable> enumeration = aSN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof ASN1Sequence) {
        this.uses = ASN1Sequence.getInstance(aSN1Encodable);
        continue;
      } 
      if (aSN1Encodable instanceof ASN1TaggedObject) {
        this.prohibitions = ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Encodable, false);
        continue;
      } 
      if (aSN1Encodable instanceof DERUTF8String)
        this.alias = DERUTF8String.getInstance(aSN1Encodable).getString(); 
    } 
  }
  
  public String getAlias() {
    return this.alias;
  }
  
  public Set<ASN1ObjectIdentifier> getUses() {
    return toSet(this.uses);
  }
  
  public Set<ASN1ObjectIdentifier> getProhibitions() {
    return toSet(this.prohibitions);
  }
  
  private Set<ASN1ObjectIdentifier> toSet(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence != null) {
      HashSet<ASN1ObjectIdentifier> hashSet = new HashSet(paramASN1Sequence.size());
      Enumeration enumeration = paramASN1Sequence.getObjects();
      while (enumeration.hasMoreElements())
        hashSet.add(ASN1ObjectIdentifier.getInstance(enumeration.nextElement())); 
      return hashSet;
    } 
    return Collections.EMPTY_SET;
  }
  
  private ASN1Sequence toSequence(Set<ASN1ObjectIdentifier> paramSet) {
    if (paramSet == null || paramSet.isEmpty())
      return null; 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Iterator<ASN1ObjectIdentifier> iterator = paramSet.iterator();
    while (iterator.hasNext())
      aSN1EncodableVector.add((ASN1Encodable)iterator.next()); 
    return (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  ASN1Sequence toASN1Sequence() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.uses != null)
      aSN1EncodableVector.add((ASN1Encodable)this.uses); 
    if (this.prohibitions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.prohibitions)); 
    if (this.alias != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERUTF8String(this.alias)); 
    return (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
}
