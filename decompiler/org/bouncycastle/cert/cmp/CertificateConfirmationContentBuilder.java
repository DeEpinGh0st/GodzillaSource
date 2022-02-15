package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CertificateConfirmationContentBuilder {
  private DigestAlgorithmIdentifierFinder digestAlgFinder;
  
  private List acceptedCerts = new ArrayList();
  
  private List acceptedReqIds = new ArrayList();
  
  public CertificateConfirmationContentBuilder() {
    this((DigestAlgorithmIdentifierFinder)new DefaultDigestAlgorithmIdentifierFinder());
  }
  
  public CertificateConfirmationContentBuilder(DigestAlgorithmIdentifierFinder paramDigestAlgorithmIdentifierFinder) {
    this.digestAlgFinder = paramDigestAlgorithmIdentifierFinder;
  }
  
  public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder paramX509CertificateHolder, BigInteger paramBigInteger) {
    this.acceptedCerts.add(paramX509CertificateHolder);
    this.acceptedReqIds.add(paramBigInteger);
    return this;
  }
  
  public CertificateConfirmationContent build(DigestCalculatorProvider paramDigestCalculatorProvider) throws CMPException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != this.acceptedCerts.size(); b++) {
      DigestCalculator digestCalculator;
      X509CertificateHolder x509CertificateHolder = this.acceptedCerts.get(b);
      BigInteger bigInteger = this.acceptedReqIds.get(b);
      AlgorithmIdentifier algorithmIdentifier = this.digestAlgFinder.find(x509CertificateHolder.toASN1Structure().getSignatureAlgorithm());
      if (algorithmIdentifier == null)
        throw new CMPException("cannot find algorithm for digest from signature"); 
      try {
        digestCalculator = paramDigestCalculatorProvider.get(algorithmIdentifier);
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMPException("unable to create digest: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
      CMPUtil.derEncodeToStream((ASN1Encodable)x509CertificateHolder.toASN1Structure(), digestCalculator.getOutputStream());
      aSN1EncodableVector.add((ASN1Encodable)new CertStatus(digestCalculator.getDigest(), bigInteger));
    } 
    return new CertificateConfirmationContent(CertConfirmContent.getInstance(new DERSequence(aSN1EncodableVector)), this.digestAlgFinder);
  }
}
