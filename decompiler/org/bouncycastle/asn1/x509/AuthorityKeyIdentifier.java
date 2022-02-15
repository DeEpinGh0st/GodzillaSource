package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class AuthorityKeyIdentifier extends ASN1Object {
  ASN1OctetString keyidentifier = null;
  
  GeneralNames certissuer = null;
  
  ASN1Integer certserno = null;
  
  public static AuthorityKeyIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static AuthorityKeyIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof AuthorityKeyIdentifier) ? (AuthorityKeyIdentifier)paramObject : ((paramObject != null) ? new AuthorityKeyIdentifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static AuthorityKeyIdentifier fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.authorityKeyIdentifier));
  }
  
  protected AuthorityKeyIdentifier(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = DERTaggedObject.getInstance(enumeration.nextElement());
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.keyidentifier = ASN1OctetString.getInstance(aSN1TaggedObject, false);
          continue;
        case 1:
          this.certissuer = GeneralNames.getInstance(aSN1TaggedObject, false);
          continue;
        case 2:
          this.certserno = ASN1Integer.getInstance(aSN1TaggedObject, false);
          continue;
      } 
      throw new IllegalArgumentException("illegal tag");
    } 
  }
  
  public AuthorityKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    SHA1Digest sHA1Digest = new SHA1Digest();
    byte[] arrayOfByte1 = new byte[sHA1Digest.getDigestSize()];
    byte[] arrayOfByte2 = paramSubjectPublicKeyInfo.getPublicKeyData().getBytes();
    sHA1Digest.update(arrayOfByte2, 0, arrayOfByte2.length);
    sHA1Digest.doFinal(arrayOfByte1, 0);
    this.keyidentifier = (ASN1OctetString)new DEROctetString(arrayOfByte1);
  }
  
  public AuthorityKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo, GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    SHA1Digest sHA1Digest = new SHA1Digest();
    byte[] arrayOfByte1 = new byte[sHA1Digest.getDigestSize()];
    byte[] arrayOfByte2 = paramSubjectPublicKeyInfo.getPublicKeyData().getBytes();
    sHA1Digest.update(arrayOfByte2, 0, arrayOfByte2.length);
    sHA1Digest.doFinal(arrayOfByte1, 0);
    this.keyidentifier = (ASN1OctetString)new DEROctetString(arrayOfByte1);
    this.certissuer = GeneralNames.getInstance(paramGeneralNames.toASN1Primitive());
    this.certserno = new ASN1Integer(paramBigInteger);
  }
  
  public AuthorityKeyIdentifier(GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    this((byte[])null, paramGeneralNames, paramBigInteger);
  }
  
  public AuthorityKeyIdentifier(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, (GeneralNames)null, (BigInteger)null);
  }
  
  public AuthorityKeyIdentifier(byte[] paramArrayOfbyte, GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    this.keyidentifier = (paramArrayOfbyte != null) ? (ASN1OctetString)new DEROctetString(paramArrayOfbyte) : null;
    this.certissuer = paramGeneralNames;
    this.certserno = (paramBigInteger != null) ? new ASN1Integer(paramBigInteger) : null;
  }
  
  public byte[] getKeyIdentifier() {
    return (this.keyidentifier != null) ? this.keyidentifier.getOctets() : null;
  }
  
  public GeneralNames getAuthorityCertIssuer() {
    return this.certissuer;
  }
  
  public BigInteger getAuthorityCertSerialNumber() {
    return (this.certserno != null) ? this.certserno.getValue() : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.keyidentifier != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.keyidentifier)); 
    if (this.certissuer != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.certissuer)); 
    if (this.certserno != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.certserno)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    return "AuthorityKeyIdentifier: KeyID(" + this.keyidentifier.getOctets() + ")";
  }
}
