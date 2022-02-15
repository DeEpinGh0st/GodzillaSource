package org.bouncycastle.asn1.eac;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class CVCertificate extends ASN1Object {
  private CertificateBody certificateBody;
  
  private byte[] signature;
  
  private int valid;
  
  private static int bodyValid = 1;
  
  private static int signValid = 2;
  
  private void setPrivateData(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    this.valid = 0;
    if (paramASN1ApplicationSpecific.getApplicationTag() == 33) {
      ASN1InputStream aSN1InputStream = new ASN1InputStream(paramASN1ApplicationSpecific.getContents());
      ASN1Primitive aSN1Primitive;
      while ((aSN1Primitive = aSN1InputStream.readObject()) != null) {
        if (aSN1Primitive instanceof DERApplicationSpecific) {
          DERApplicationSpecific dERApplicationSpecific = (DERApplicationSpecific)aSN1Primitive;
          switch (dERApplicationSpecific.getApplicationTag()) {
            case 78:
              this.certificateBody = CertificateBody.getInstance(dERApplicationSpecific);
              this.valid |= bodyValid;
              continue;
            case 55:
              this.signature = dERApplicationSpecific.getContents();
              this.valid |= signValid;
              continue;
          } 
          throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + dERApplicationSpecific.getApplicationTag());
        } 
        throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
      } 
      aSN1InputStream.close();
    } else {
      throw new IOException("not a CARDHOLDER_CERTIFICATE :" + paramASN1ApplicationSpecific.getApplicationTag());
    } 
    if (this.valid != (signValid | bodyValid))
      throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + paramASN1ApplicationSpecific.getApplicationTag()); 
  }
  
  public CVCertificate(ASN1InputStream paramASN1InputStream) throws IOException {
    initFrom(paramASN1InputStream);
  }
  
  private void initFrom(ASN1InputStream paramASN1InputStream) throws IOException {
    ASN1Primitive aSN1Primitive;
    while ((aSN1Primitive = paramASN1InputStream.readObject()) != null) {
      if (aSN1Primitive instanceof DERApplicationSpecific) {
        setPrivateData((ASN1ApplicationSpecific)aSN1Primitive);
        continue;
      } 
      throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
    } 
  }
  
  private CVCertificate(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    setPrivateData(paramASN1ApplicationSpecific);
  }
  
  public CVCertificate(CertificateBody paramCertificateBody, byte[] paramArrayOfbyte) throws IOException {
    this.certificateBody = paramCertificateBody;
    this.signature = Arrays.clone(paramArrayOfbyte);
    this.valid |= bodyValid;
    this.valid |= signValid;
  }
  
  public static CVCertificate getInstance(Object paramObject) {
    if (paramObject instanceof CVCertificate)
      return (CVCertificate)paramObject; 
    if (paramObject != null)
      try {
        return new CVCertificate(DERApplicationSpecific.getInstance(paramObject));
      } catch (IOException iOException) {
        throw new ASN1ParsingException("unable to parse data: " + iOException.getMessage(), iOException);
      }  
    return null;
  }
  
  public byte[] getSignature() {
    return Arrays.clone(this.signature);
  }
  
  public CertificateBody getBody() {
    return this.certificateBody;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certificateBody);
    try {
      aSN1EncodableVector.add((ASN1Encodable)new DERApplicationSpecific(false, 55, (ASN1Encodable)new DEROctetString(this.signature)));
    } catch (IOException iOException) {
      throw new IllegalStateException("unable to convert signature!");
    } 
    return (ASN1Primitive)new DERApplicationSpecific(33, aSN1EncodableVector);
  }
  
  public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
    CertificateHolderAuthorization certificateHolderAuthorization = this.certificateBody.getCertificateHolderAuthorization();
    return certificateHolderAuthorization.getOid();
  }
  
  public PackedDate getEffectiveDate() throws IOException {
    return this.certificateBody.getCertificateEffectiveDate();
  }
  
  public int getCertificateType() {
    return this.certificateBody.getCertificateType();
  }
  
  public PackedDate getExpirationDate() throws IOException {
    return this.certificateBody.getCertificateExpirationDate();
  }
  
  public int getRole() throws IOException {
    CertificateHolderAuthorization certificateHolderAuthorization = this.certificateBody.getCertificateHolderAuthorization();
    return certificateHolderAuthorization.getAccessRights();
  }
  
  public CertificationAuthorityReference getAuthorityReference() throws IOException {
    return this.certificateBody.getCertificationAuthorityReference();
  }
  
  public CertificateHolderReference getHolderReference() throws IOException {
    return this.certificateBody.getCertificateHolderReference();
  }
  
  public int getHolderAuthorizationRole() throws IOException {
    int i = this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
    return i & 0xC0;
  }
  
  public Flags getHolderAuthorizationRights() throws IOException {
    return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 0x1F);
  }
}
