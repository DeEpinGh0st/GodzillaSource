package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;

public class OCSPReqBuilder {
  private List list = new ArrayList();
  
  private GeneralName requestorName = null;
  
  private Extensions requestExtensions = null;
  
  public OCSPReqBuilder addRequest(CertificateID paramCertificateID) {
    this.list.add(new RequestObject(paramCertificateID, null));
    return this;
  }
  
  public OCSPReqBuilder addRequest(CertificateID paramCertificateID, Extensions paramExtensions) {
    this.list.add(new RequestObject(paramCertificateID, paramExtensions));
    return this;
  }
  
  public OCSPReqBuilder setRequestorName(X500Name paramX500Name) {
    this.requestorName = new GeneralName(4, (ASN1Encodable)paramX500Name);
    return this;
  }
  
  public OCSPReqBuilder setRequestorName(GeneralName paramGeneralName) {
    this.requestorName = paramGeneralName;
    return this;
  }
  
  public OCSPReqBuilder setRequestExtensions(Extensions paramExtensions) {
    this.requestExtensions = paramExtensions;
    return this;
  }
  
  private OCSPReq generateRequest(ContentSigner paramContentSigner, X509CertificateHolder[] paramArrayOfX509CertificateHolder) throws OCSPException {
    Iterator<RequestObject> iterator = this.list.iterator();
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    while (iterator.hasNext()) {
      try {
        aSN1EncodableVector.add((ASN1Encodable)((RequestObject)iterator.next()).toRequest());
      } catch (Exception exception) {
        throw new OCSPException("exception creating Request", exception);
      } 
    } 
    TBSRequest tBSRequest = new TBSRequest(this.requestorName, (ASN1Sequence)new DERSequence(aSN1EncodableVector), this.requestExtensions);
    Signature signature = null;
    if (paramContentSigner != null) {
      if (this.requestorName == null)
        throw new OCSPException("requestorName must be specified if request is signed."); 
      try {
        OutputStream outputStream = paramContentSigner.getOutputStream();
        outputStream.write(tBSRequest.getEncoded("DER"));
        outputStream.close();
      } catch (Exception exception) {
        throw new OCSPException("exception processing TBSRequest: " + exception, exception);
      } 
      DERBitString dERBitString = new DERBitString(paramContentSigner.getSignature());
      AlgorithmIdentifier algorithmIdentifier = paramContentSigner.getAlgorithmIdentifier();
      if (paramArrayOfX509CertificateHolder != null && paramArrayOfX509CertificateHolder.length > 0) {
        ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
        for (byte b = 0; b != paramArrayOfX509CertificateHolder.length; b++)
          aSN1EncodableVector1.add((ASN1Encodable)paramArrayOfX509CertificateHolder[b].toASN1Structure()); 
        signature = new Signature(algorithmIdentifier, dERBitString, (ASN1Sequence)new DERSequence(aSN1EncodableVector1));
      } else {
        signature = new Signature(algorithmIdentifier, dERBitString);
      } 
    } 
    return new OCSPReq(new OCSPRequest(tBSRequest, signature));
  }
  
  public OCSPReq build() throws OCSPException {
    return generateRequest(null, null);
  }
  
  public OCSPReq build(ContentSigner paramContentSigner, X509CertificateHolder[] paramArrayOfX509CertificateHolder) throws OCSPException, IllegalArgumentException {
    if (paramContentSigner == null)
      throw new IllegalArgumentException("no signer specified"); 
    return generateRequest(paramContentSigner, paramArrayOfX509CertificateHolder);
  }
  
  private class RequestObject {
    CertificateID certId;
    
    Extensions extensions;
    
    public RequestObject(CertificateID param1CertificateID, Extensions param1Extensions) {
      this.certId = param1CertificateID;
      this.extensions = param1Extensions;
    }
    
    public Request toRequest() throws Exception {
      return new Request(this.certId.toASN1Primitive(), this.extensions);
    }
  }
}
