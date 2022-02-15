package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.asn1.x500.DirectoryString;

public class NamingAuthority extends ASN1Object {
  public static final ASN1ObjectIdentifier id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern = new ASN1ObjectIdentifier(ISISMTTObjectIdentifiers.id_isismtt_at_namingAuthorities + ".1");
  
  private ASN1ObjectIdentifier namingAuthorityId;
  
  private String namingAuthorityUrl;
  
  private DirectoryString namingAuthorityText;
  
  public static NamingAuthority getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof NamingAuthority)
      return (NamingAuthority)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new NamingAuthority((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static NamingAuthority getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  private NamingAuthority(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    if (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof ASN1ObjectIdentifier) {
        this.namingAuthorityId = (ASN1ObjectIdentifier)aSN1Encodable;
      } else if (aSN1Encodable instanceof DERIA5String) {
        this.namingAuthorityUrl = DERIA5String.getInstance(aSN1Encodable).getString();
      } else if (aSN1Encodable instanceof org.bouncycastle.asn1.ASN1String) {
        this.namingAuthorityText = DirectoryString.getInstance(aSN1Encodable);
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
    if (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof DERIA5String) {
        this.namingAuthorityUrl = DERIA5String.getInstance(aSN1Encodable).getString();
      } else if (aSN1Encodable instanceof org.bouncycastle.asn1.ASN1String) {
        this.namingAuthorityText = DirectoryString.getInstance(aSN1Encodable);
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
    if (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof org.bouncycastle.asn1.ASN1String) {
        this.namingAuthorityText = DirectoryString.getInstance(aSN1Encodable);
      } else {
        throw new IllegalArgumentException("Bad object encountered: " + aSN1Encodable.getClass());
      } 
    } 
  }
  
  public ASN1ObjectIdentifier getNamingAuthorityId() {
    return this.namingAuthorityId;
  }
  
  public DirectoryString getNamingAuthorityText() {
    return this.namingAuthorityText;
  }
  
  public String getNamingAuthorityUrl() {
    return this.namingAuthorityUrl;
  }
  
  public NamingAuthority(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString, DirectoryString paramDirectoryString) {
    this.namingAuthorityId = paramASN1ObjectIdentifier;
    this.namingAuthorityUrl = paramString;
    this.namingAuthorityText = paramDirectoryString;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.namingAuthorityId != null)
      aSN1EncodableVector.add((ASN1Encodable)this.namingAuthorityId); 
    if (this.namingAuthorityUrl != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERIA5String(this.namingAuthorityUrl, true)); 
    if (this.namingAuthorityText != null)
      aSN1EncodableVector.add((ASN1Encodable)this.namingAuthorityText); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
