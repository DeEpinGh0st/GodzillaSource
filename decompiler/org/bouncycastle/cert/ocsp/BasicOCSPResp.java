package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class BasicOCSPResp implements Encodable {
  private BasicOCSPResponse resp;
  
  private ResponseData data;
  
  private Extensions extensions;
  
  public BasicOCSPResp(BasicOCSPResponse paramBasicOCSPResponse) {
    this.resp = paramBasicOCSPResponse;
    this.data = paramBasicOCSPResponse.getTbsResponseData();
    this.extensions = Extensions.getInstance(paramBasicOCSPResponse.getTbsResponseData().getResponseExtensions());
  }
  
  public byte[] getTBSResponseData() {
    try {
      return this.resp.getTbsResponseData().getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public AlgorithmIdentifier getSignatureAlgorithmID() {
    return this.resp.getSignatureAlgorithm();
  }
  
  public int getVersion() {
    return this.data.getVersion().getValue().intValue() + 1;
  }
  
  public RespID getResponderId() {
    return new RespID(this.data.getResponderID());
  }
  
  public Date getProducedAt() {
    return OCSPUtils.extractDate(this.data.getProducedAt());
  }
  
  public SingleResp[] getResponses() {
    ASN1Sequence aSN1Sequence = this.data.getResponses();
    SingleResp[] arrayOfSingleResp = new SingleResp[aSN1Sequence.size()];
    for (byte b = 0; b != arrayOfSingleResp.length; b++)
      arrayOfSingleResp[b] = new SingleResp(SingleResponse.getInstance(aSN1Sequence.getObjectAt(b))); 
    return arrayOfSingleResp;
  }
  
  public boolean hasExtensions() {
    return (this.extensions != null);
  }
  
  public Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (this.extensions != null) ? this.extensions.getExtension(paramASN1ObjectIdentifier) : null;
  }
  
  public List getExtensionOIDs() {
    return OCSPUtils.getExtensionOIDs(this.extensions);
  }
  
  public Set getCriticalExtensionOIDs() {
    return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
  }
  
  public ASN1ObjectIdentifier getSignatureAlgOID() {
    return this.resp.getSignatureAlgorithm().getAlgorithm();
  }
  
  public byte[] getSignature() {
    return this.resp.getSignature().getOctets();
  }
  
  public X509CertificateHolder[] getCerts() {
    if (this.resp.getCerts() != null) {
      ASN1Sequence aSN1Sequence = this.resp.getCerts();
      if (aSN1Sequence != null) {
        X509CertificateHolder[] arrayOfX509CertificateHolder = new X509CertificateHolder[aSN1Sequence.size()];
        for (byte b = 0; b != arrayOfX509CertificateHolder.length; b++)
          arrayOfX509CertificateHolder[b] = new X509CertificateHolder(Certificate.getInstance(aSN1Sequence.getObjectAt(b))); 
        return arrayOfX509CertificateHolder;
      } 
      return OCSPUtils.EMPTY_CERTS;
    } 
    return OCSPUtils.EMPTY_CERTS;
  }
  
  public boolean isSignatureValid(ContentVerifierProvider paramContentVerifierProvider) throws OCSPException {
    try {
      ContentVerifier contentVerifier = paramContentVerifierProvider.get(this.resp.getSignatureAlgorithm());
      OutputStream outputStream = contentVerifier.getOutputStream();
      outputStream.write(this.resp.getTbsResponseData().getEncoded("DER"));
      outputStream.close();
      return contentVerifier.verify(getSignature());
    } catch (Exception exception) {
      throw new OCSPException("exception processing sig: " + exception, exception);
    } 
  }
  
  public byte[] getEncoded() throws IOException {
    return this.resp.getEncoded();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof BasicOCSPResp))
      return false; 
    BasicOCSPResp basicOCSPResp = (BasicOCSPResp)paramObject;
    return this.resp.equals(basicOCSPResp.resp);
  }
  
  public int hashCode() {
    return this.resp.hashCode();
  }
}
