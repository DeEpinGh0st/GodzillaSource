package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSAuthenticatedData implements Encodable {
  RecipientInformationStore recipientInfoStore;
  
  ContentInfo contentInfo;
  
  private AlgorithmIdentifier macAlg;
  
  private ASN1Set authAttrs;
  
  private ASN1Set unauthAttrs;
  
  private byte[] mac;
  
  private OriginatorInformation originatorInfo;
  
  public CMSAuthenticatedData(byte[] paramArrayOfbyte) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSAuthenticatedData(byte[] paramArrayOfbyte, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte), paramDigestCalculatorProvider);
  }
  
  public CMSAuthenticatedData(InputStream paramInputStream) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream));
  }
  
  public CMSAuthenticatedData(InputStream paramInputStream, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream), paramDigestCalculatorProvider);
  }
  
  public CMSAuthenticatedData(ContentInfo paramContentInfo) throws CMSException {
    this(paramContentInfo, (DigestCalculatorProvider)null);
  }
  
  public CMSAuthenticatedData(ContentInfo paramContentInfo, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException {
    this.contentInfo = paramContentInfo;
    AuthenticatedData authenticatedData = AuthenticatedData.getInstance(paramContentInfo.getContent());
    if (authenticatedData.getOriginatorInfo() != null)
      this.originatorInfo = new OriginatorInformation(authenticatedData.getOriginatorInfo()); 
    ASN1Set aSN1Set = authenticatedData.getRecipientInfos();
    this.macAlg = authenticatedData.getMacAlgorithm();
    this.authAttrs = authenticatedData.getAuthAttrs();
    this.mac = authenticatedData.getMac().getOctets();
    this.unauthAttrs = authenticatedData.getUnauthAttrs();
    ContentInfo contentInfo = authenticatedData.getEncapsulatedContentInfo();
    CMSProcessableByteArray cMSProcessableByteArray = new CMSProcessableByteArray(ASN1OctetString.getInstance(contentInfo.getContent()).getOctets());
    if (this.authAttrs != null) {
      if (paramDigestCalculatorProvider == null)
        throw new CMSException("a digest calculator provider is required if authenticated attributes are present"); 
      AttributeTable attributeTable = new AttributeTable(this.authAttrs);
      ASN1EncodableVector aSN1EncodableVector = attributeTable.getAll(CMSAttributes.cmsAlgorithmProtect);
      if (aSN1EncodableVector.size() > 1)
        throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present"); 
      if (aSN1EncodableVector.size() > 0) {
        Attribute attribute = Attribute.getInstance(aSN1EncodableVector.get(0));
        if (attribute.getAttrValues().size() != 1)
          throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value"); 
        CMSAlgorithmProtection cMSAlgorithmProtection = CMSAlgorithmProtection.getInstance(attribute.getAttributeValues()[0]);
        if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getDigestAlgorithm(), authenticatedData.getDigestAlgorithm()))
          throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm"); 
        if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getMacAlgorithm(), this.macAlg))
          throw new CMSException("CMS Algorithm Identifier Protection check failed for macAlgorithm"); 
      } 
      try {
        CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable cMSDigestAuthenticatedSecureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(paramDigestCalculatorProvider.get(authenticatedData.getDigestAlgorithm()), cMSProcessableByteArray);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.macAlg, cMSDigestAuthenticatedSecureReadable, new AuthAttributesProvider() {
              public ASN1Set getAuthAttributes() {
                return CMSAuthenticatedData.this.authAttrs;
              }
            });
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMSException("unable to create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
    } else {
      CMSEnvelopedHelper.CMSAuthenticatedSecureReadable cMSAuthenticatedSecureReadable = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, cMSProcessableByteArray);
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.macAlg, cMSAuthenticatedSecureReadable);
    } 
  }
  
  public OriginatorInformation getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public byte[] getMac() {
    return Arrays.clone(this.mac);
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlg;
  }
  
  public String getMacAlgOID() {
    return this.macAlg.getAlgorithm().getId();
  }
  
  public byte[] getMacAlgParams() {
    try {
      return encodeObj(this.macAlg.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public RecipientInformationStore getRecipientInfos() {
    return this.recipientInfoStore;
  }
  
  public ContentInfo getContentInfo() {
    return this.contentInfo;
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
  
  public AttributeTable getAuthAttrs() {
    return (this.authAttrs == null) ? null : new AttributeTable(this.authAttrs);
  }
  
  public AttributeTable getUnauthAttrs() {
    return (this.unauthAttrs == null) ? null : new AttributeTable(this.unauthAttrs);
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
  
  public byte[] getContentDigest() {
    return (this.authAttrs != null) ? ASN1OctetString.getInstance(getAuthAttrs().get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets() : null;
  }
}
