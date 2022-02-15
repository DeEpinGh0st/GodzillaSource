package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificateRequest;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateRequestHolder {
  private CVCertificateRequest request;
  
  private static CVCertificateRequest parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return CVCertificateRequest.getInstance(paramArrayOfbyte);
    } catch (ClassCastException classCastException) {
      throw new EACIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new EACIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } catch (ASN1ParsingException aSN1ParsingException) {
      if (aSN1ParsingException.getCause() instanceof IOException)
        throw (IOException)aSN1ParsingException.getCause(); 
      throw new EACIOException("malformed data: " + aSN1ParsingException.getMessage(), aSN1ParsingException);
    } 
  }
  
  public EACCertificateRequestHolder(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public EACCertificateRequestHolder(CVCertificateRequest paramCVCertificateRequest) {
    this.request = paramCVCertificateRequest;
  }
  
  public CVCertificateRequest toASN1Structure() {
    return this.request;
  }
  
  public PublicKeyDataObject getPublicKeyDataObject() {
    return this.request.getPublicKey();
  }
  
  public boolean isInnerSignatureValid(EACSignatureVerifier paramEACSignatureVerifier) throws EACException {
    try {
      OutputStream outputStream = paramEACSignatureVerifier.getOutputStream();
      outputStream.write(this.request.getCertificateBody().getEncoded("DER"));
      outputStream.close();
      return paramEACSignatureVerifier.verify(this.request.getInnerSignature());
    } catch (Exception exception) {
      throw new EACException("unable to process signature: " + exception.getMessage(), exception);
    } 
  }
}
