package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;

public class BasicOCSPRespBuilder {
  private List list = new ArrayList();
  
  private Extensions responseExtensions = null;
  
  private RespID responderID;
  
  public BasicOCSPRespBuilder(RespID paramRespID) {
    this.responderID = paramRespID;
  }
  
  public BasicOCSPRespBuilder(SubjectPublicKeyInfo paramSubjectPublicKeyInfo, DigestCalculator paramDigestCalculator) throws OCSPException {
    this.responderID = new RespID(paramSubjectPublicKeyInfo, paramDigestCalculator);
  }
  
  public BasicOCSPRespBuilder addResponse(CertificateID paramCertificateID, CertificateStatus paramCertificateStatus) {
    addResponse(paramCertificateID, paramCertificateStatus, new Date(), null, null);
    return this;
  }
  
  public BasicOCSPRespBuilder addResponse(CertificateID paramCertificateID, CertificateStatus paramCertificateStatus, Extensions paramExtensions) {
    addResponse(paramCertificateID, paramCertificateStatus, new Date(), null, paramExtensions);
    return this;
  }
  
  public BasicOCSPRespBuilder addResponse(CertificateID paramCertificateID, CertificateStatus paramCertificateStatus, Date paramDate, Extensions paramExtensions) {
    addResponse(paramCertificateID, paramCertificateStatus, new Date(), paramDate, paramExtensions);
    return this;
  }
  
  public BasicOCSPRespBuilder addResponse(CertificateID paramCertificateID, CertificateStatus paramCertificateStatus, Date paramDate1, Date paramDate2) {
    addResponse(paramCertificateID, paramCertificateStatus, paramDate1, paramDate2, null);
    return this;
  }
  
  public BasicOCSPRespBuilder addResponse(CertificateID paramCertificateID, CertificateStatus paramCertificateStatus, Date paramDate1, Date paramDate2, Extensions paramExtensions) {
    this.list.add(new ResponseObject(paramCertificateID, paramCertificateStatus, paramDate1, paramDate2, paramExtensions));
    return this;
  }
  
  public BasicOCSPRespBuilder setResponseExtensions(Extensions paramExtensions) {
    this.responseExtensions = paramExtensions;
    return this;
  }
  
  public BasicOCSPResp build(ContentSigner paramContentSigner, X509CertificateHolder[] paramArrayOfX509CertificateHolder, Date paramDate) throws OCSPException {
    DERBitString dERBitString;
    Iterator<ResponseObject> iterator = this.list.iterator();
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    while (iterator.hasNext()) {
      try {
        aSN1EncodableVector.add((ASN1Encodable)((ResponseObject)iterator.next()).toResponse());
      } catch (Exception exception) {
        throw new OCSPException("exception creating Request", exception);
      } 
    } 
    ResponseData responseData = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(paramDate), (ASN1Sequence)new DERSequence(aSN1EncodableVector), this.responseExtensions);
    try {
      OutputStream outputStream = paramContentSigner.getOutputStream();
      outputStream.write(responseData.getEncoded("DER"));
      outputStream.close();
      dERBitString = new DERBitString(paramContentSigner.getSignature());
    } catch (Exception exception) {
      throw new OCSPException("exception processing TBSRequest: " + exception.getMessage(), exception);
    } 
    AlgorithmIdentifier algorithmIdentifier = paramContentSigner.getAlgorithmIdentifier();
    DERSequence dERSequence = null;
    if (paramArrayOfX509CertificateHolder != null && paramArrayOfX509CertificateHolder.length > 0) {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      for (byte b = 0; b != paramArrayOfX509CertificateHolder.length; b++)
        aSN1EncodableVector1.add((ASN1Encodable)paramArrayOfX509CertificateHolder[b].toASN1Structure()); 
      dERSequence = new DERSequence(aSN1EncodableVector1);
    } 
    return new BasicOCSPResp(new BasicOCSPResponse(responseData, algorithmIdentifier, dERBitString, (ASN1Sequence)dERSequence));
  }
  
  private class ResponseObject {
    CertificateID certId;
    
    CertStatus certStatus;
    
    ASN1GeneralizedTime thisUpdate;
    
    ASN1GeneralizedTime nextUpdate;
    
    Extensions extensions;
    
    public ResponseObject(CertificateID param1CertificateID, CertificateStatus param1CertificateStatus, Date param1Date1, Date param1Date2, Extensions param1Extensions) {
      this.certId = param1CertificateID;
      if (param1CertificateStatus == null) {
        this.certStatus = new CertStatus();
      } else if (param1CertificateStatus instanceof UnknownStatus) {
        this.certStatus = new CertStatus(2, (ASN1Encodable)DERNull.INSTANCE);
      } else {
        RevokedStatus revokedStatus = (RevokedStatus)param1CertificateStatus;
        if (revokedStatus.hasRevocationReason()) {
          this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), CRLReason.lookup(revokedStatus.getRevocationReason())));
        } else {
          this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), null));
        } 
      } 
      this.thisUpdate = (ASN1GeneralizedTime)new DERGeneralizedTime(param1Date1);
      if (param1Date2 != null) {
        this.nextUpdate = (ASN1GeneralizedTime)new DERGeneralizedTime(param1Date2);
      } else {
        this.nextUpdate = null;
      } 
      this.extensions = param1Extensions;
    }
    
    public SingleResponse toResponse() throws Exception {
      return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
    }
  }
}
