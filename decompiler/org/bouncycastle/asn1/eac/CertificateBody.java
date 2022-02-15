package org.bouncycastle.asn1.eac;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEROctetString;

public class CertificateBody extends ASN1Object {
  ASN1InputStream seq;
  
  private DERApplicationSpecific certificateProfileIdentifier;
  
  private DERApplicationSpecific certificationAuthorityReference;
  
  private PublicKeyDataObject publicKey;
  
  private DERApplicationSpecific certificateHolderReference;
  
  private CertificateHolderAuthorization certificateHolderAuthorization;
  
  private DERApplicationSpecific certificateEffectiveDate;
  
  private DERApplicationSpecific certificateExpirationDate;
  
  private int certificateType = 0;
  
  private static final int CPI = 1;
  
  private static final int CAR = 2;
  
  private static final int PK = 4;
  
  private static final int CHR = 8;
  
  private static final int CHA = 16;
  
  private static final int CEfD = 32;
  
  private static final int CExD = 64;
  
  public static final int profileType = 127;
  
  public static final int requestType = 13;
  
  private void setIso7816CertificateBody(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    byte[] arrayOfByte;
    if (paramASN1ApplicationSpecific.getApplicationTag() == 78) {
      arrayOfByte = paramASN1ApplicationSpecific.getContents();
    } else {
      throw new IOException("Bad tag : not an iso7816 CERTIFICATE_CONTENT_TEMPLATE");
    } 
    ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte);
    ASN1Primitive aSN1Primitive;
    while ((aSN1Primitive = aSN1InputStream.readObject()) != null) {
      DERApplicationSpecific dERApplicationSpecific;
      if (aSN1Primitive instanceof DERApplicationSpecific) {
        dERApplicationSpecific = (DERApplicationSpecific)aSN1Primitive;
      } else {
        throw new IOException("Not a valid iso7816 content : not a DERApplicationSpecific Object :" + EACTags.encodeTag(paramASN1ApplicationSpecific) + aSN1Primitive.getClass());
      } 
      switch (dERApplicationSpecific.getApplicationTag()) {
        case 41:
          setCertificateProfileIdentifier(dERApplicationSpecific);
          continue;
        case 2:
          setCertificationAuthorityReference(dERApplicationSpecific);
          continue;
        case 73:
          setPublicKey(PublicKeyDataObject.getInstance(dERApplicationSpecific.getObject(16)));
          continue;
        case 32:
          setCertificateHolderReference(dERApplicationSpecific);
          continue;
        case 76:
          setCertificateHolderAuthorization(new CertificateHolderAuthorization(dERApplicationSpecific));
          continue;
        case 37:
          setCertificateEffectiveDate(dERApplicationSpecific);
          continue;
        case 36:
          setCertificateExpirationDate(dERApplicationSpecific);
          continue;
      } 
      this.certificateType = 0;
      throw new IOException("Not a valid iso7816 DERApplicationSpecific tag " + dERApplicationSpecific.getApplicationTag());
    } 
    aSN1InputStream.close();
  }
  
  public CertificateBody(DERApplicationSpecific paramDERApplicationSpecific, CertificationAuthorityReference paramCertificationAuthorityReference, PublicKeyDataObject paramPublicKeyDataObject, CertificateHolderReference paramCertificateHolderReference, CertificateHolderAuthorization paramCertificateHolderAuthorization, PackedDate paramPackedDate1, PackedDate paramPackedDate2) {
    setCertificateProfileIdentifier(paramDERApplicationSpecific);
    setCertificationAuthorityReference(new DERApplicationSpecific(2, paramCertificationAuthorityReference.getEncoded()));
    setPublicKey(paramPublicKeyDataObject);
    setCertificateHolderReference(new DERApplicationSpecific(32, paramCertificateHolderReference.getEncoded()));
    setCertificateHolderAuthorization(paramCertificateHolderAuthorization);
    try {
      setCertificateEffectiveDate(new DERApplicationSpecific(false, 37, (ASN1Encodable)new DEROctetString(paramPackedDate1.getEncoding())));
      setCertificateExpirationDate(new DERApplicationSpecific(false, 36, (ASN1Encodable)new DEROctetString(paramPackedDate2.getEncoding())));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("unable to encode dates: " + iOException.getMessage());
    } 
  }
  
  private CertificateBody(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    setIso7816CertificateBody(paramASN1ApplicationSpecific);
  }
  
  private ASN1Primitive profileToASN1Object() throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certificateProfileIdentifier);
    aSN1EncodableVector.add((ASN1Encodable)this.certificationAuthorityReference);
    aSN1EncodableVector.add((ASN1Encodable)new DERApplicationSpecific(false, 73, (ASN1Encodable)this.publicKey));
    aSN1EncodableVector.add((ASN1Encodable)this.certificateHolderReference);
    aSN1EncodableVector.add((ASN1Encodable)this.certificateHolderAuthorization);
    aSN1EncodableVector.add((ASN1Encodable)this.certificateEffectiveDate);
    aSN1EncodableVector.add((ASN1Encodable)this.certificateExpirationDate);
    return (ASN1Primitive)new DERApplicationSpecific(78, aSN1EncodableVector);
  }
  
  private void setCertificateProfileIdentifier(DERApplicationSpecific paramDERApplicationSpecific) throws IllegalArgumentException {
    if (paramDERApplicationSpecific.getApplicationTag() == 41) {
      this.certificateProfileIdentifier = paramDERApplicationSpecific;
      this.certificateType |= 0x1;
    } else {
      throw new IllegalArgumentException("Not an Iso7816Tags.INTERCHANGE_PROFILE tag :" + EACTags.encodeTag(paramDERApplicationSpecific));
    } 
  }
  
  private void setCertificateHolderReference(DERApplicationSpecific paramDERApplicationSpecific) throws IllegalArgumentException {
    if (paramDERApplicationSpecific.getApplicationTag() == 32) {
      this.certificateHolderReference = paramDERApplicationSpecific;
      this.certificateType |= 0x8;
    } else {
      throw new IllegalArgumentException("Not an Iso7816Tags.CARDHOLDER_NAME tag");
    } 
  }
  
  private void setCertificationAuthorityReference(DERApplicationSpecific paramDERApplicationSpecific) throws IllegalArgumentException {
    if (paramDERApplicationSpecific.getApplicationTag() == 2) {
      this.certificationAuthorityReference = paramDERApplicationSpecific;
      this.certificateType |= 0x2;
    } else {
      throw new IllegalArgumentException("Not an Iso7816Tags.ISSUER_IDENTIFICATION_NUMBER tag");
    } 
  }
  
  private void setPublicKey(PublicKeyDataObject paramPublicKeyDataObject) {
    this.publicKey = PublicKeyDataObject.getInstance(paramPublicKeyDataObject);
    this.certificateType |= 0x4;
  }
  
  private ASN1Primitive requestToASN1Object() throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certificateProfileIdentifier);
    aSN1EncodableVector.add((ASN1Encodable)new DERApplicationSpecific(false, 73, (ASN1Encodable)this.publicKey));
    aSN1EncodableVector.add((ASN1Encodable)this.certificateHolderReference);
    return (ASN1Primitive)new DERApplicationSpecific(78, aSN1EncodableVector);
  }
  
  public ASN1Primitive toASN1Primitive() {
    try {
      if (this.certificateType == 127)
        return profileToASN1Object(); 
      if (this.certificateType == 13)
        return requestToASN1Object(); 
    } catch (IOException iOException) {
      return null;
    } 
    return null;
  }
  
  public int getCertificateType() {
    return this.certificateType;
  }
  
  public static CertificateBody getInstance(Object paramObject) throws IOException {
    return (paramObject instanceof CertificateBody) ? (CertificateBody)paramObject : ((paramObject != null) ? new CertificateBody(ASN1ApplicationSpecific.getInstance(paramObject)) : null);
  }
  
  public PackedDate getCertificateEffectiveDate() {
    return ((this.certificateType & 0x20) == 32) ? new PackedDate(this.certificateEffectiveDate.getContents()) : null;
  }
  
  private void setCertificateEffectiveDate(DERApplicationSpecific paramDERApplicationSpecific) throws IllegalArgumentException {
    if (paramDERApplicationSpecific.getApplicationTag() == 37) {
      this.certificateEffectiveDate = paramDERApplicationSpecific;
      this.certificateType |= 0x20;
    } else {
      throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EFFECTIVE_DATE tag :" + EACTags.encodeTag(paramDERApplicationSpecific));
    } 
  }
  
  public PackedDate getCertificateExpirationDate() throws IOException {
    if ((this.certificateType & 0x40) == 64)
      return new PackedDate(this.certificateExpirationDate.getContents()); 
    throw new IOException("certificate Expiration Date not set");
  }
  
  private void setCertificateExpirationDate(DERApplicationSpecific paramDERApplicationSpecific) throws IllegalArgumentException {
    if (paramDERApplicationSpecific.getApplicationTag() == 36) {
      this.certificateExpirationDate = paramDERApplicationSpecific;
      this.certificateType |= 0x40;
    } else {
      throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EXPIRATION_DATE tag");
    } 
  }
  
  public CertificateHolderAuthorization getCertificateHolderAuthorization() throws IOException {
    if ((this.certificateType & 0x10) == 16)
      return this.certificateHolderAuthorization; 
    throw new IOException("Certificate Holder Authorisation not set");
  }
  
  private void setCertificateHolderAuthorization(CertificateHolderAuthorization paramCertificateHolderAuthorization) {
    this.certificateHolderAuthorization = paramCertificateHolderAuthorization;
    this.certificateType |= 0x10;
  }
  
  public CertificateHolderReference getCertificateHolderReference() {
    return new CertificateHolderReference(this.certificateHolderReference.getContents());
  }
  
  public DERApplicationSpecific getCertificateProfileIdentifier() {
    return this.certificateProfileIdentifier;
  }
  
  public CertificationAuthorityReference getCertificationAuthorityReference() throws IOException {
    if ((this.certificateType & 0x2) == 2)
      return new CertificationAuthorityReference(this.certificationAuthorityReference.getContents()); 
    throw new IOException("Certification authority reference not set");
  }
  
  public PublicKeyDataObject getPublicKey() {
    return this.publicKey;
  }
}
