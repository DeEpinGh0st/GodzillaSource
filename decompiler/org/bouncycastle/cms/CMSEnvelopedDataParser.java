package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSEnvelopedDataParser extends CMSContentInfoParser {
  RecipientInformationStore recipientInfoStore;
  
  EnvelopedDataParser envelopedData = new EnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
  
  private AlgorithmIdentifier encAlg;
  
  private AttributeTable unprotectedAttributes;
  
  private boolean attrNotRead = true;
  
  private OriginatorInformation originatorInfo;
  
  public CMSEnvelopedDataParser(byte[] paramArrayOfbyte) throws CMSException, IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSEnvelopedDataParser(InputStream paramInputStream) throws CMSException, IOException {
    super(paramInputStream);
    OriginatorInfo originatorInfo = this.envelopedData.getOriginatorInfo();
    if (originatorInfo != null)
      this.originatorInfo = new OriginatorInformation(originatorInfo); 
    ASN1Set aSN1Set = ASN1Set.getInstance(this.envelopedData.getRecipientInfos().toASN1Primitive());
    EncryptedContentInfoParser encryptedContentInfoParser = this.envelopedData.getEncryptedContentInfo();
    this.encAlg = encryptedContentInfoParser.getContentEncryptionAlgorithm();
    CMSProcessableInputStream cMSProcessableInputStream = new CMSProcessableInputStream(((ASN1OctetStringParser)encryptedContentInfoParser.getEncryptedContent(4)).getOctetStream());
    CMSEnvelopedHelper.CMSEnvelopedSecureReadable cMSEnvelopedSecureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, cMSProcessableInputStream);
    this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.encAlg, cMSEnvelopedSecureReadable);
  }
  
  public String getEncryptionAlgOID() {
    return this.encAlg.getAlgorithm().toString();
  }
  
  public byte[] getEncryptionAlgParams() {
    try {
      return encodeObj(this.encAlg.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public AlgorithmIdentifier getContentEncryptionAlgorithm() {
    return this.encAlg;
  }
  
  public OriginatorInformation getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public RecipientInformationStore getRecipientInfos() {
    return this.recipientInfoStore;
  }
  
  public AttributeTable getUnprotectedAttributes() throws IOException {
    if (this.unprotectedAttributes == null && this.attrNotRead) {
      ASN1SetParser aSN1SetParser = this.envelopedData.getUnprotectedAttrs();
      this.attrNotRead = false;
      if (aSN1SetParser != null) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1Encodable aSN1Encodable;
        while ((aSN1Encodable = aSN1SetParser.readObject()) != null) {
          ASN1SequenceParser aSN1SequenceParser = (ASN1SequenceParser)aSN1Encodable;
          aSN1EncodableVector.add((ASN1Encodable)aSN1SequenceParser.toASN1Primitive());
        } 
        this.unprotectedAttributes = new AttributeTable((ASN1Set)new DERSet(aSN1EncodableVector));
      } 
    } 
    return this.unprotectedAttributes;
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
}
