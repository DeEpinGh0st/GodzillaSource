package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInfoGenerator {
  private final SignerIdentifier signerIdentifier;
  
  private final CMSAttributeTableGenerator sAttrGen;
  
  private final CMSAttributeTableGenerator unsAttrGen;
  
  private final ContentSigner signer;
  
  private final DigestCalculator digester;
  
  private final DigestAlgorithmIdentifierFinder digAlgFinder = (DigestAlgorithmIdentifierFinder)new DefaultDigestAlgorithmIdentifierFinder();
  
  private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
  
  private byte[] calculatedDigest = null;
  
  private X509CertificateHolder certHolder;
  
  SignerInfoGenerator(SignerIdentifier paramSignerIdentifier, ContentSigner paramContentSigner, DigestCalculatorProvider paramDigestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder paramCMSSignatureEncryptionAlgorithmFinder) throws OperatorCreationException {
    this(paramSignerIdentifier, paramContentSigner, paramDigestCalculatorProvider, paramCMSSignatureEncryptionAlgorithmFinder, false);
  }
  
  SignerInfoGenerator(SignerIdentifier paramSignerIdentifier, ContentSigner paramContentSigner, DigestCalculatorProvider paramDigestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder paramCMSSignatureEncryptionAlgorithmFinder, boolean paramBoolean) throws OperatorCreationException {
    this.signerIdentifier = paramSignerIdentifier;
    this.signer = paramContentSigner;
    if (paramDigestCalculatorProvider != null) {
      this.digester = paramDigestCalculatorProvider.get(this.digAlgFinder.find(paramContentSigner.getAlgorithmIdentifier()));
    } else {
      this.digester = null;
    } 
    if (paramBoolean) {
      this.sAttrGen = null;
      this.unsAttrGen = null;
    } else {
      this.sAttrGen = new DefaultSignedAttributeTableGenerator();
      this.unsAttrGen = null;
    } 
    this.sigEncAlgFinder = paramCMSSignatureEncryptionAlgorithmFinder;
  }
  
  public SignerInfoGenerator(SignerInfoGenerator paramSignerInfoGenerator, CMSAttributeTableGenerator paramCMSAttributeTableGenerator1, CMSAttributeTableGenerator paramCMSAttributeTableGenerator2) {
    this.signerIdentifier = paramSignerInfoGenerator.signerIdentifier;
    this.signer = paramSignerInfoGenerator.signer;
    this.digester = paramSignerInfoGenerator.digester;
    this.sigEncAlgFinder = paramSignerInfoGenerator.sigEncAlgFinder;
    this.sAttrGen = paramCMSAttributeTableGenerator1;
    this.unsAttrGen = paramCMSAttributeTableGenerator2;
  }
  
  SignerInfoGenerator(SignerIdentifier paramSignerIdentifier, ContentSigner paramContentSigner, DigestCalculatorProvider paramDigestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder paramCMSSignatureEncryptionAlgorithmFinder, CMSAttributeTableGenerator paramCMSAttributeTableGenerator1, CMSAttributeTableGenerator paramCMSAttributeTableGenerator2) throws OperatorCreationException {
    this.signerIdentifier = paramSignerIdentifier;
    this.signer = paramContentSigner;
    if (paramDigestCalculatorProvider != null) {
      this.digester = paramDigestCalculatorProvider.get(this.digAlgFinder.find(paramContentSigner.getAlgorithmIdentifier()));
    } else {
      this.digester = null;
    } 
    this.sAttrGen = paramCMSAttributeTableGenerator1;
    this.unsAttrGen = paramCMSAttributeTableGenerator2;
    this.sigEncAlgFinder = paramCMSSignatureEncryptionAlgorithmFinder;
  }
  
  public SignerIdentifier getSID() {
    return this.signerIdentifier;
  }
  
  public int getGeneratedVersion() {
    return this.signerIdentifier.isTagged() ? 3 : 1;
  }
  
  public boolean hasAssociatedCertificate() {
    return (this.certHolder != null);
  }
  
  public X509CertificateHolder getAssociatedCertificate() {
    return this.certHolder;
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return (this.digester != null) ? this.digester.getAlgorithmIdentifier() : this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
  }
  
  public OutputStream getCalculatingOutputStream() {
    return (OutputStream)((this.digester != null) ? ((this.sAttrGen == null) ? new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream()) : this.digester.getOutputStream()) : this.signer.getOutputStream());
  }
  
  public SignerInfo generate(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    try {
      ASN1Set aSN1Set1 = null;
      AlgorithmIdentifier algorithmIdentifier1 = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
      AlgorithmIdentifier algorithmIdentifier2 = null;
      if (this.sAttrGen != null) {
        algorithmIdentifier2 = this.digester.getAlgorithmIdentifier();
        this.calculatedDigest = this.digester.getDigest();
        Map<?, ?> map = getBaseParameters(paramASN1ObjectIdentifier, this.digester.getAlgorithmIdentifier(), algorithmIdentifier1, this.calculatedDigest);
        AttributeTable attributeTable = this.sAttrGen.getAttributes(Collections.unmodifiableMap(map));
        aSN1Set1 = getAttributeSet(attributeTable);
        OutputStream outputStream = this.signer.getOutputStream();
        outputStream.write(aSN1Set1.getEncoded("DER"));
        outputStream.close();
      } else if (this.digester != null) {
        algorithmIdentifier2 = this.digester.getAlgorithmIdentifier();
        this.calculatedDigest = this.digester.getDigest();
      } else {
        algorithmIdentifier2 = this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
        this.calculatedDigest = null;
      } 
      byte[] arrayOfByte = this.signer.getSignature();
      ASN1Set aSN1Set2 = null;
      if (this.unsAttrGen != null) {
        Map<String, byte[]> map = getBaseParameters(paramASN1ObjectIdentifier, algorithmIdentifier2, algorithmIdentifier1, this.calculatedDigest);
        map.put("encryptedDigest", Arrays.clone(arrayOfByte));
        AttributeTable attributeTable = this.unsAttrGen.getAttributes(Collections.unmodifiableMap((Map)map));
        aSN1Set2 = getAttributeSet(attributeTable);
      } 
      return new SignerInfo(this.signerIdentifier, algorithmIdentifier2, aSN1Set1, algorithmIdentifier1, (ASN1OctetString)new DEROctetString(arrayOfByte), aSN1Set2);
    } catch (IOException iOException) {
      throw new CMSException("encoding error.", iOException);
    } 
  }
  
  void setAssociatedCertificate(X509CertificateHolder paramX509CertificateHolder) {
    this.certHolder = paramX509CertificateHolder;
  }
  
  private ASN1Set getAttributeSet(AttributeTable paramAttributeTable) {
    return (ASN1Set)((paramAttributeTable != null) ? new DERSet(paramAttributeTable.toASN1EncodableVector()) : null);
  }
  
  private Map getBaseParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    if (paramASN1ObjectIdentifier != null)
      hashMap.put("contentType", paramASN1ObjectIdentifier); 
    hashMap.put("digestAlgID", paramAlgorithmIdentifier1);
    hashMap.put("signatureAlgID", paramAlgorithmIdentifier2);
    hashMap.put("digest", Arrays.clone(paramArrayOfbyte));
    return hashMap;
  }
  
  public byte[] getCalculatedDigest() {
    return (this.calculatedDigest != null) ? Arrays.clone(this.calculatedDigest) : null;
  }
  
  public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
    return this.sAttrGen;
  }
  
  public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
    return this.unsAttrGen;
  }
}
