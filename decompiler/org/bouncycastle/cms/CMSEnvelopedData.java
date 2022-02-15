package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Encodable;

public class CMSEnvelopedData implements Encodable {
  RecipientInformationStore recipientInfoStore;
  
  ContentInfo contentInfo;
  
  private AlgorithmIdentifier encAlg;
  
  private ASN1Set unprotectedAttributes;
  
  private OriginatorInformation originatorInfo;
  
  public CMSEnvelopedData(byte[] paramArrayOfbyte) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSEnvelopedData(InputStream paramInputStream) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream));
  }
  
  public CMSEnvelopedData(ContentInfo paramContentInfo) throws CMSException {
    this.contentInfo = paramContentInfo;
    try {
      EnvelopedData envelopedData = EnvelopedData.getInstance(paramContentInfo.getContent());
      if (envelopedData.getOriginatorInfo() != null)
        this.originatorInfo = new OriginatorInformation(envelopedData.getOriginatorInfo()); 
      ASN1Set aSN1Set = envelopedData.getRecipientInfos();
      EncryptedContentInfo encryptedContentInfo = envelopedData.getEncryptedContentInfo();
      this.encAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
      CMSProcessableByteArray cMSProcessableByteArray = new CMSProcessableByteArray(encryptedContentInfo.getEncryptedContent().getOctets());
      CMSEnvelopedHelper.CMSEnvelopedSecureReadable cMSEnvelopedSecureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, cMSProcessableByteArray);
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.encAlg, cMSEnvelopedSecureReadable);
      this.unprotectedAttributes = envelopedData.getUnprotectedAttrs();
    } catch (ClassCastException classCastException) {
      throw new CMSException("Malformed content.", classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("Malformed content.", illegalArgumentException);
    } 
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
  
  public OriginatorInformation getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public AlgorithmIdentifier getContentEncryptionAlgorithm() {
    return this.encAlg;
  }
  
  public String getEncryptionAlgOID() {
    return this.encAlg.getAlgorithm().getId();
  }
  
  public byte[] getEncryptionAlgParams() {
    try {
      return encodeObj(this.encAlg.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public RecipientInformationStore getRecipientInfos() {
    return this.recipientInfoStore;
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
  
  public AttributeTable getUnprotectedAttributes() {
    return (this.unprotectedAttributes == null) ? null : new AttributeTable(this.unprotectedAttributes);
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
}
