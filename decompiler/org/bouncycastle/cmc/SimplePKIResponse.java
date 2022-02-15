package org.bouncycastle.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class SimplePKIResponse implements Encodable {
  private final CMSSignedData certificateResponse;
  
  private static ContentInfo parseBytes(byte[] paramArrayOfbyte) throws CMCException {
    try {
      return ContentInfo.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (Exception exception) {
      throw new CMCException("malformed data: " + exception.getMessage(), exception);
    } 
  }
  
  public SimplePKIResponse(byte[] paramArrayOfbyte) throws CMCException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public SimplePKIResponse(ContentInfo paramContentInfo) throws CMCException {
    try {
      this.certificateResponse = new CMSSignedData(paramContentInfo);
    } catch (CMSException cMSException) {
      throw new CMCException("malformed response: " + cMSException.getMessage(), cMSException);
    } 
    if (this.certificateResponse.getSignerInfos().size() != 0)
      throw new CMCException("malformed response: SignerInfo structures found"); 
    if (this.certificateResponse.getSignedContent() != null)
      throw new CMCException("malformed response: Signed Content found"); 
  }
  
  public Store<X509CertificateHolder> getCertificates() {
    return this.certificateResponse.getCertificates();
  }
  
  public Store<X509CRLHolder> getCRLs() {
    return this.certificateResponse.getCRLs();
  }
  
  public byte[] getEncoded() throws IOException {
    return this.certificateResponse.getEncoded();
  }
}
