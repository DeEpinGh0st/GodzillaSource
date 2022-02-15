package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class CVCertificateRequest extends ASN1Object {
  private final ASN1ApplicationSpecific original;
  
  private CertificateBody certificateBody;
  
  private byte[] innerSignature = null;
  
  private byte[] outerSignature = null;
  
  private static final int bodyValid = 1;
  
  private static final int signValid = 2;
  
  private CVCertificateRequest(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    this.original = paramASN1ApplicationSpecific;
    if (paramASN1ApplicationSpecific.isConstructed() && paramASN1ApplicationSpecific.getApplicationTag() == 7) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1ApplicationSpecific.getObject(16));
      initCertBody(ASN1ApplicationSpecific.getInstance(aSN1Sequence.getObjectAt(0)));
      this.outerSignature = ASN1ApplicationSpecific.getInstance(aSN1Sequence.getObjectAt(aSN1Sequence.size() - 1)).getContents();
    } else {
      initCertBody(paramASN1ApplicationSpecific);
    } 
  }
  
  private void initCertBody(ASN1ApplicationSpecific paramASN1ApplicationSpecific) throws IOException {
    if (paramASN1ApplicationSpecific.getApplicationTag() == 33) {
      int i = 0;
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1ApplicationSpecific.getObject(16));
      Enumeration enumeration = aSN1Sequence.getObjects();
      while (enumeration.hasMoreElements()) {
        ASN1ApplicationSpecific aSN1ApplicationSpecific = ASN1ApplicationSpecific.getInstance(enumeration.nextElement());
        switch (aSN1ApplicationSpecific.getApplicationTag()) {
          case 78:
            this.certificateBody = CertificateBody.getInstance(aSN1ApplicationSpecific);
            i |= 0x1;
            continue;
          case 55:
            this.innerSignature = aSN1ApplicationSpecific.getContents();
            i |= 0x2;
            continue;
        } 
        throw new IOException("Invalid tag, not an CV Certificate Request element:" + aSN1ApplicationSpecific.getApplicationTag());
      } 
      if ((i & 0x3) == 0)
        throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + paramASN1ApplicationSpecific.getApplicationTag()); 
    } else {
      throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + paramASN1ApplicationSpecific.getApplicationTag());
    } 
  }
  
  public static CVCertificateRequest getInstance(Object paramObject) {
    if (paramObject instanceof CVCertificateRequest)
      return (CVCertificateRequest)paramObject; 
    if (paramObject != null)
      try {
        return new CVCertificateRequest(ASN1ApplicationSpecific.getInstance(paramObject));
      } catch (IOException iOException) {
        throw new ASN1ParsingException("unable to parse data: " + iOException.getMessage(), iOException);
      }  
    return null;
  }
  
  public CertificateBody getCertificateBody() {
    return this.certificateBody;
  }
  
  public PublicKeyDataObject getPublicKey() {
    return this.certificateBody.getPublicKey();
  }
  
  public byte[] getInnerSignature() {
    return Arrays.clone(this.innerSignature);
  }
  
  public byte[] getOuterSignature() {
    return Arrays.clone(this.outerSignature);
  }
  
  public boolean hasOuterSignature() {
    return (this.outerSignature != null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    if (this.original != null)
      return (ASN1Primitive)this.original; 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certificateBody);
    try {
      aSN1EncodableVector.add((ASN1Encodable)new DERApplicationSpecific(false, 55, (ASN1Encodable)new DEROctetString(this.innerSignature)));
    } catch (IOException iOException) {
      throw new IllegalStateException("unable to convert signature!");
    } 
    return (ASN1Primitive)new DERApplicationSpecific(33, aSN1EncodableVector);
  }
}
