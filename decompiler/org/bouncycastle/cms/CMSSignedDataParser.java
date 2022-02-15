package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Generator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.SignedDataParser;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.io.Streams;

public class CMSSignedDataParser extends CMSContentInfoParser {
  private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
  
  private SignedDataParser _signedData;
  
  private ASN1ObjectIdentifier _signedContentType;
  
  private CMSTypedStream _signedContent;
  
  private Map digests;
  
  private Set<AlgorithmIdentifier> digestAlgorithms;
  
  private SignerInformationStore _signerInfoStore;
  
  private ASN1Set _certSet;
  
  private ASN1Set _crlSet;
  
  private boolean _isCertCrlParsed;
  
  public CMSSignedDataParser(DigestCalculatorProvider paramDigestCalculatorProvider, byte[] paramArrayOfbyte) throws CMSException {
    this(paramDigestCalculatorProvider, new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSSignedDataParser(DigestCalculatorProvider paramDigestCalculatorProvider, CMSTypedStream paramCMSTypedStream, byte[] paramArrayOfbyte) throws CMSException {
    this(paramDigestCalculatorProvider, paramCMSTypedStream, new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSSignedDataParser(DigestCalculatorProvider paramDigestCalculatorProvider, InputStream paramInputStream) throws CMSException {
    this(paramDigestCalculatorProvider, (CMSTypedStream)null, paramInputStream);
  }
  
  public CMSSignedDataParser(DigestCalculatorProvider paramDigestCalculatorProvider, CMSTypedStream paramCMSTypedStream, InputStream paramInputStream) throws CMSException {
    super(paramInputStream);
    try {
      this._signedContent = paramCMSTypedStream;
      this._signedData = SignedDataParser.getInstance(this._contentInfo.getContent(16));
      this.digests = new HashMap<Object, Object>();
      ASN1SetParser aSN1SetParser = this._signedData.getDigestAlgorithms();
      HashSet<AlgorithmIdentifier> hashSet = new HashSet();
      ASN1Encodable aSN1Encodable1;
      while ((aSN1Encodable1 = aSN1SetParser.readObject()) != null) {
        AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1Encodable1);
        hashSet.add(algorithmIdentifier);
        try {
          DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(algorithmIdentifier);
          if (digestCalculator != null)
            this.digests.put(algorithmIdentifier.getAlgorithm(), digestCalculator); 
        } catch (OperatorCreationException operatorCreationException) {}
      } 
      this.digestAlgorithms = Collections.unmodifiableSet(hashSet);
      ContentInfoParser contentInfoParser = this._signedData.getEncapContentInfo();
      ASN1Encodable aSN1Encodable2 = contentInfoParser.getContent(4);
      if (aSN1Encodable2 instanceof ASN1OctetStringParser) {
        ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)aSN1Encodable2;
        CMSTypedStream cMSTypedStream = new CMSTypedStream(contentInfoParser.getContentType(), aSN1OctetStringParser.getOctetStream());
        if (this._signedContent == null) {
          this._signedContent = cMSTypedStream;
        } else {
          cMSTypedStream.drain();
        } 
      } else if (aSN1Encodable2 != null) {
        PKCS7TypedStream pKCS7TypedStream = new PKCS7TypedStream(contentInfoParser.getContentType(), aSN1Encodable2);
        if (this._signedContent == null) {
          this._signedContent = pKCS7TypedStream;
        } else {
          pKCS7TypedStream.drain();
        } 
      } 
      if (paramCMSTypedStream == null) {
        this._signedContentType = contentInfoParser.getContentType();
      } else {
        this._signedContentType = this._signedContent.getContentType();
      } 
    } catch (IOException iOException) {
      throw new CMSException("io exception: " + iOException.getMessage(), iOException);
    } 
  }
  
  public int getVersion() {
    return this._signedData.getVersion().getValue().intValue();
  }
  
  public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
    return this.digestAlgorithms;
  }
  
  public SignerInformationStore getSignerInfos() throws CMSException {
    if (this._signerInfoStore == null) {
      populateCertCrlSets();
      ArrayList<SignerInformation> arrayList = new ArrayList();
      HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
      for (Object object : this.digests.keySet())
        hashMap.put(object, ((DigestCalculator)this.digests.get(object)).getDigest()); 
      try {
        ASN1SetParser aSN1SetParser = this._signedData.getSignerInfos();
        ASN1Encodable aSN1Encodable;
        while ((aSN1Encodable = aSN1SetParser.readObject()) != null) {
          SignerInfo signerInfo = SignerInfo.getInstance(aSN1Encodable.toASN1Primitive());
          byte[] arrayOfByte = (byte[])hashMap.get(signerInfo.getDigestAlgorithm().getAlgorithm());
          arrayList.add(new SignerInformation(signerInfo, this._signedContentType, null, arrayOfByte));
        } 
      } catch (IOException iOException) {
        throw new CMSException("io exception: " + iOException.getMessage(), iOException);
      } 
      this._signerInfoStore = new SignerInformationStore(arrayList);
    } 
    return this._signerInfoStore;
  }
  
  public Store getCertificates() throws CMSException {
    populateCertCrlSets();
    return HELPER.getCertificates(this._certSet);
  }
  
  public Store getCRLs() throws CMSException {
    populateCertCrlSets();
    return HELPER.getCRLs(this._crlSet);
  }
  
  public Store getAttributeCertificates() throws CMSException {
    populateCertCrlSets();
    return HELPER.getAttributeCertificates(this._certSet);
  }
  
  public Store getOtherRevocationInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    populateCertCrlSets();
    return HELPER.getOtherRevocationInfo(paramASN1ObjectIdentifier, this._crlSet);
  }
  
  private void populateCertCrlSets() throws CMSException {
    if (this._isCertCrlParsed)
      return; 
    this._isCertCrlParsed = true;
    try {
      this._certSet = getASN1Set(this._signedData.getCertificates());
      this._crlSet = getASN1Set(this._signedData.getCrls());
    } catch (IOException iOException) {
      throw new CMSException("problem parsing cert/crl sets", iOException);
    } 
  }
  
  public String getSignedContentTypeOID() {
    return this._signedContentType.getId();
  }
  
  public CMSTypedStream getSignedContent() {
    if (this._signedContent == null)
      return null; 
    InputStream inputStream = CMSUtils.attachDigestsToInputStream(this.digests.values(), this._signedContent.getContentStream());
    return new CMSTypedStream(this._signedContent.getContentType(), inputStream);
  }
  
  public static OutputStream replaceSigners(InputStream paramInputStream, SignerInformationStore paramSignerInformationStore, OutputStream paramOutputStream) throws CMSException, IOException {
    ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(paramInputStream);
    ContentInfoParser contentInfoParser1 = new ContentInfoParser((ASN1SequenceParser)aSN1StreamParser.readObject());
    SignedDataParser signedDataParser = SignedDataParser.getInstance(contentInfoParser1.getContent(16));
    BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream);
    bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
    BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
    bERSequenceGenerator2.addObject((ASN1Encodable)signedDataParser.getVersion());
    signedDataParser.getDigestAlgorithms().toASN1Primitive();
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    for (SignerInformation signerInformation : paramSignerInformationStore.getSigners())
      aSN1EncodableVector1.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID())); 
    bERSequenceGenerator2.getRawOutputStream().write((new DERSet(aSN1EncodableVector1)).getEncoded());
    ContentInfoParser contentInfoParser2 = signedDataParser.getEncapContentInfo();
    BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
    bERSequenceGenerator3.addObject((ASN1Encodable)contentInfoParser2.getContentType());
    pipeEncapsulatedOctetString(contentInfoParser2, bERSequenceGenerator3.getRawOutputStream());
    bERSequenceGenerator3.close();
    writeSetToGeneratorTagged((ASN1Generator)bERSequenceGenerator2, signedDataParser.getCertificates(), 0);
    writeSetToGeneratorTagged((ASN1Generator)bERSequenceGenerator2, signedDataParser.getCrls(), 1);
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (SignerInformation signerInformation : paramSignerInformationStore.getSigners())
      aSN1EncodableVector2.add((ASN1Encodable)signerInformation.toASN1Structure()); 
    bERSequenceGenerator2.getRawOutputStream().write((new DERSet(aSN1EncodableVector2)).getEncoded());
    bERSequenceGenerator2.close();
    bERSequenceGenerator1.close();
    return paramOutputStream;
  }
  
  public static OutputStream replaceCertificatesAndCRLs(InputStream paramInputStream, Store paramStore1, Store paramStore2, Store paramStore3, OutputStream paramOutputStream) throws CMSException, IOException {
    ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(paramInputStream);
    ContentInfoParser contentInfoParser1 = new ContentInfoParser((ASN1SequenceParser)aSN1StreamParser.readObject());
    SignedDataParser signedDataParser = SignedDataParser.getInstance(contentInfoParser1.getContent(16));
    BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream);
    bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
    BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
    bERSequenceGenerator2.addObject((ASN1Encodable)signedDataParser.getVersion());
    bERSequenceGenerator2.getRawOutputStream().write(signedDataParser.getDigestAlgorithms().toASN1Primitive().getEncoded());
    ContentInfoParser contentInfoParser2 = signedDataParser.getEncapContentInfo();
    BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
    bERSequenceGenerator3.addObject((ASN1Encodable)contentInfoParser2.getContentType());
    pipeEncapsulatedOctetString(contentInfoParser2, bERSequenceGenerator3.getRawOutputStream());
    bERSequenceGenerator3.close();
    getASN1Set(signedDataParser.getCertificates());
    getASN1Set(signedDataParser.getCrls());
    if (paramStore1 != null || paramStore3 != null) {
      ArrayList arrayList = new ArrayList();
      if (paramStore1 != null)
        arrayList.addAll(CMSUtils.getCertificatesFromStore(paramStore1)); 
      if (paramStore3 != null)
        arrayList.addAll(CMSUtils.getAttributeCertificatesFromStore(paramStore3)); 
      ASN1Set aSN1Set = CMSUtils.createBerSetFromList(arrayList);
      if (aSN1Set.size() > 0)
        bERSequenceGenerator2.getRawOutputStream().write((new DERTaggedObject(false, 0, (ASN1Encodable)aSN1Set)).getEncoded()); 
    } 
    if (paramStore2 != null) {
      ASN1Set aSN1Set = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(paramStore2));
      if (aSN1Set.size() > 0)
        bERSequenceGenerator2.getRawOutputStream().write((new DERTaggedObject(false, 1, (ASN1Encodable)aSN1Set)).getEncoded()); 
    } 
    bERSequenceGenerator2.getRawOutputStream().write(signedDataParser.getSignerInfos().toASN1Primitive().getEncoded());
    bERSequenceGenerator2.close();
    bERSequenceGenerator1.close();
    return paramOutputStream;
  }
  
  private static void writeSetToGeneratorTagged(ASN1Generator paramASN1Generator, ASN1SetParser paramASN1SetParser, int paramInt) throws IOException {
    ASN1Set aSN1Set = getASN1Set(paramASN1SetParser);
    if (aSN1Set != null)
      if (paramASN1SetParser instanceof org.bouncycastle.asn1.BERSetParser) {
        paramASN1Generator.getRawOutputStream().write((new BERTaggedObject(false, paramInt, (ASN1Encodable)aSN1Set)).getEncoded());
      } else {
        paramASN1Generator.getRawOutputStream().write((new DERTaggedObject(false, paramInt, (ASN1Encodable)aSN1Set)).getEncoded());
      }  
  }
  
  private static ASN1Set getASN1Set(ASN1SetParser paramASN1SetParser) {
    return (paramASN1SetParser == null) ? null : ASN1Set.getInstance(paramASN1SetParser.toASN1Primitive());
  }
  
  private static void pipeEncapsulatedOctetString(ContentInfoParser paramContentInfoParser, OutputStream paramOutputStream) throws IOException {
    ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)paramContentInfoParser.getContent(4);
    if (aSN1OctetStringParser != null)
      pipeOctetString(aSN1OctetStringParser, paramOutputStream); 
  }
  
  private static void pipeOctetString(ASN1OctetStringParser paramASN1OctetStringParser, OutputStream paramOutputStream) throws IOException {
    OutputStream outputStream = CMSUtils.createBEROctetOutputStream(paramOutputStream, 0, true, 0);
    Streams.pipeAll(paramASN1OctetStringParser.getOctetStream(), outputStream);
    outputStream.close();
  }
}
