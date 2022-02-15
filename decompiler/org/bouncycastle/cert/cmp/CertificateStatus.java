package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class CertificateStatus {
  private DigestAlgorithmIdentifierFinder digestAlgFinder;
  
  private CertStatus certStatus;
  
  CertificateStatus(DigestAlgorithmIdentifierFinder paramDigestAlgorithmIdentifierFinder, CertStatus paramCertStatus) {
    this.digestAlgFinder = paramDigestAlgorithmIdentifierFinder;
    this.certStatus = paramCertStatus;
  }
  
  public PKIStatusInfo getStatusInfo() {
    return this.certStatus.getStatusInfo();
  }
  
  public BigInteger getCertRequestID() {
    return this.certStatus.getCertReqId().getValue();
  }
  
  public boolean isVerified(X509CertificateHolder paramX509CertificateHolder, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMPException {
    DigestCalculator digestCalculator;
    AlgorithmIdentifier algorithmIdentifier = this.digestAlgFinder.find(paramX509CertificateHolder.toASN1Structure().getSignatureAlgorithm());
    if (algorithmIdentifier == null)
      throw new CMPException("cannot find algorithm for digest from signature"); 
    try {
      digestCalculator = paramDigestCalculatorProvider.get(algorithmIdentifier);
    } catch (OperatorCreationException operatorCreationException) {
      throw new CMPException("unable to create digester: " + operatorCreationException.getMessage(), operatorCreationException);
    } 
    CMPUtil.derEncodeToStream((ASN1Encodable)paramX509CertificateHolder.toASN1Structure(), digestCalculator.getOutputStream());
    return Arrays.areEqual(this.certStatus.getCertHash().getOctets(), digestCalculator.getDigest());
  }
}
