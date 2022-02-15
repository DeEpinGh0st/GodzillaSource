package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class SignerInfoGeneratorBuilder {
  private DigestCalculatorProvider digestProvider;
  
  private boolean directSignature;
  
  private CMSAttributeTableGenerator signedGen;
  
  private CMSAttributeTableGenerator unsignedGen;
  
  private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
  
  public SignerInfoGeneratorBuilder(DigestCalculatorProvider paramDigestCalculatorProvider) {
    this(paramDigestCalculatorProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
  }
  
  public SignerInfoGeneratorBuilder(DigestCalculatorProvider paramDigestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder paramCMSSignatureEncryptionAlgorithmFinder) {
    this.digestProvider = paramDigestCalculatorProvider;
    this.sigEncAlgFinder = paramCMSSignatureEncryptionAlgorithmFinder;
  }
  
  public SignerInfoGeneratorBuilder setDirectSignature(boolean paramBoolean) {
    this.directSignature = paramBoolean;
    return this;
  }
  
  public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator paramCMSAttributeTableGenerator) {
    this.signedGen = paramCMSAttributeTableGenerator;
    return this;
  }
  
  public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator paramCMSAttributeTableGenerator) {
    this.unsignedGen = paramCMSAttributeTableGenerator;
    return this;
  }
  
  public SignerInfoGenerator build(ContentSigner paramContentSigner, X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException {
    SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(paramX509CertificateHolder.toASN1Structure()));
    SignerInfoGenerator signerInfoGenerator = createGenerator(paramContentSigner, signerIdentifier);
    signerInfoGenerator.setAssociatedCertificate(paramX509CertificateHolder);
    return signerInfoGenerator;
  }
  
  public SignerInfoGenerator build(ContentSigner paramContentSigner, byte[] paramArrayOfbyte) throws OperatorCreationException {
    SignerIdentifier signerIdentifier = new SignerIdentifier((ASN1OctetString)new DEROctetString(paramArrayOfbyte));
    return createGenerator(paramContentSigner, signerIdentifier);
  }
  
  private SignerInfoGenerator createGenerator(ContentSigner paramContentSigner, SignerIdentifier paramSignerIdentifier) throws OperatorCreationException {
    if (this.directSignature)
      return new SignerInfoGenerator(paramSignerIdentifier, paramContentSigner, this.digestProvider, this.sigEncAlgFinder, true); 
    if (this.signedGen != null || this.unsignedGen != null) {
      if (this.signedGen == null)
        this.signedGen = new DefaultSignedAttributeTableGenerator(); 
      return new SignerInfoGenerator(paramSignerIdentifier, paramContentSigner, this.digestProvider, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
    } 
    return new SignerInfoGenerator(paramSignerIdentifier, paramContentSigner, this.digestProvider, this.sigEncAlgFinder);
  }
}
