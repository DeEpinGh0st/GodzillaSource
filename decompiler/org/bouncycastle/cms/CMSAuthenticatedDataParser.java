package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthenticatedDataParser;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class CMSAuthenticatedDataParser extends CMSContentInfoParser {
  RecipientInformationStore recipientInfoStore;
  
  AuthenticatedDataParser authData = new AuthenticatedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
  
  private AlgorithmIdentifier macAlg;
  
  private byte[] mac;
  
  private AttributeTable authAttrs;
  
  private ASN1Set authAttrSet;
  
  private AttributeTable unauthAttrs;
  
  private boolean authAttrNotRead = true;
  
  private boolean unauthAttrNotRead;
  
  private OriginatorInformation originatorInfo;
  
  public CMSAuthenticatedDataParser(byte[] paramArrayOfbyte) throws CMSException, IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSAuthenticatedDataParser(byte[] paramArrayOfbyte, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException, IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte), paramDigestCalculatorProvider);
  }
  
  public CMSAuthenticatedDataParser(InputStream paramInputStream) throws CMSException, IOException {
    this(paramInputStream, (DigestCalculatorProvider)null);
  }
  
  public CMSAuthenticatedDataParser(InputStream paramInputStream, DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException, IOException {
    super(paramInputStream);
    OriginatorInfo originatorInfo = this.authData.getOriginatorInfo();
    if (originatorInfo != null)
      this.originatorInfo = new OriginatorInformation(originatorInfo); 
    ASN1Set aSN1Set = ASN1Set.getInstance(this.authData.getRecipientInfos().toASN1Primitive());
    this.macAlg = this.authData.getMacAlgorithm();
    AlgorithmIdentifier algorithmIdentifier = this.authData.getDigestAlgorithm();
    if (algorithmIdentifier != null) {
      if (paramDigestCalculatorProvider == null)
        throw new CMSException("a digest calculator provider is required if authenticated attributes are present"); 
      ContentInfoParser contentInfoParser = this.authData.getEncapsulatedContentInfo();
      CMSProcessableInputStream cMSProcessableInputStream = new CMSProcessableInputStream(((ASN1OctetStringParser)contentInfoParser.getContent(4)).getOctetStream());
      try {
        CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable cMSDigestAuthenticatedSecureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(paramDigestCalculatorProvider.get(algorithmIdentifier), cMSProcessableInputStream);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.macAlg, cMSDigestAuthenticatedSecureReadable, new AuthAttributesProvider() {
              public ASN1Set getAuthAttributes() {
                try {
                  return CMSAuthenticatedDataParser.this.getAuthAttrSet();
                } catch (IOException iOException) {
                  throw new IllegalStateException("can't parse authenticated attributes!");
                } 
              }
            });
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMSException("unable to create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
    } else {
      ContentInfoParser contentInfoParser = this.authData.getEncapsulatedContentInfo();
      CMSProcessableInputStream cMSProcessableInputStream = new CMSProcessableInputStream(((ASN1OctetStringParser)contentInfoParser.getContent(4)).getOctetStream());
      CMSEnvelopedHelper.CMSAuthenticatedSecureReadable cMSAuthenticatedSecureReadable = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, cMSProcessableInputStream);
      this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.macAlg, cMSAuthenticatedSecureReadable);
    } 
  }
  
  public OriginatorInformation getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlg;
  }
  
  public String getMacAlgOID() {
    return this.macAlg.getAlgorithm().toString();
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
  
  public byte[] getMac() throws IOException {
    if (this.mac == null) {
      getAuthAttrs();
      this.mac = this.authData.getMac().getOctets();
    } 
    return Arrays.clone(this.mac);
  }
  
  private ASN1Set getAuthAttrSet() throws IOException {
    if (this.authAttrs == null && this.authAttrNotRead) {
      ASN1SetParser aSN1SetParser = this.authData.getAuthAttrs();
      if (aSN1SetParser != null)
        this.authAttrSet = (ASN1Set)aSN1SetParser.toASN1Primitive(); 
      this.authAttrNotRead = false;
    } 
    return this.authAttrSet;
  }
  
  public AttributeTable getAuthAttrs() throws IOException {
    if (this.authAttrs == null && this.authAttrNotRead) {
      ASN1Set aSN1Set = getAuthAttrSet();
      if (aSN1Set != null)
        this.authAttrs = new AttributeTable(aSN1Set); 
    } 
    return this.authAttrs;
  }
  
  public AttributeTable getUnauthAttrs() throws IOException {
    if (this.unauthAttrs == null && this.unauthAttrNotRead) {
      ASN1SetParser aSN1SetParser = this.authData.getUnauthAttrs();
      this.unauthAttrNotRead = false;
      if (aSN1SetParser != null) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        ASN1Encodable aSN1Encodable;
        while ((aSN1Encodable = aSN1SetParser.readObject()) != null) {
          ASN1SequenceParser aSN1SequenceParser = (ASN1SequenceParser)aSN1Encodable;
          aSN1EncodableVector.add((ASN1Encodable)aSN1SequenceParser.toASN1Primitive());
        } 
        this.unauthAttrs = new AttributeTable((ASN1Set)new DERSet(aSN1EncodableVector));
      } 
    } 
    return this.unauthAttrs;
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
  
  public byte[] getContentDigest() {
    return (this.authAttrs != null) ? ASN1OctetString.getInstance(this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets() : null;
  }
}
