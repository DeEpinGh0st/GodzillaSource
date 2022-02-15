package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class CMSSignedData implements Encodable {
  private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
  
  SignedData signedData;
  
  ContentInfo contentInfo;
  
  CMSTypedData signedContent;
  
  SignerInformationStore signerInfoStore;
  
  private Map hashes;
  
  private CMSSignedData(CMSSignedData paramCMSSignedData) {
    this.signedData = paramCMSSignedData.signedData;
    this.contentInfo = paramCMSSignedData.contentInfo;
    this.signedContent = paramCMSSignedData.signedContent;
    this.signerInfoStore = paramCMSSignedData.signerInfoStore;
  }
  
  public CMSSignedData(byte[] paramArrayOfbyte) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSSignedData(CMSProcessable paramCMSProcessable, byte[] paramArrayOfbyte) throws CMSException {
    this(paramCMSProcessable, CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSSignedData(Map paramMap, byte[] paramArrayOfbyte) throws CMSException {
    this(paramMap, CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSSignedData(CMSProcessable paramCMSProcessable, InputStream paramInputStream) throws CMSException {
    this(paramCMSProcessable, CMSUtils.readContentInfo((InputStream)new ASN1InputStream(paramInputStream)));
  }
  
  public CMSSignedData(InputStream paramInputStream) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream));
  }
  
  public CMSSignedData(final CMSProcessable signedContent, ContentInfo paramContentInfo) throws CMSException {
    if (signedContent instanceof CMSTypedData) {
      this.signedContent = (CMSTypedData)signedContent;
    } else {
      this.signedContent = new CMSTypedData() {
          public ASN1ObjectIdentifier getContentType() {
            return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
          }
          
          public void write(OutputStream param1OutputStream) throws IOException, CMSException {
            signedContent.write(param1OutputStream);
          }
          
          public Object getContent() {
            return signedContent.getContent();
          }
        };
    } 
    this.contentInfo = paramContentInfo;
    this.signedData = getSignedData();
  }
  
  public CMSSignedData(Map paramMap, ContentInfo paramContentInfo) throws CMSException {
    this.hashes = paramMap;
    this.contentInfo = paramContentInfo;
    this.signedData = getSignedData();
  }
  
  public CMSSignedData(ContentInfo paramContentInfo) throws CMSException {
    this.contentInfo = paramContentInfo;
    this.signedData = getSignedData();
    ASN1Encodable aSN1Encodable = this.signedData.getEncapContentInfo().getContent();
    if (aSN1Encodable != null) {
      if (aSN1Encodable instanceof ASN1OctetString) {
        this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString)aSN1Encodable).getOctets());
      } else {
        this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), aSN1Encodable);
      } 
    } else {
      this.signedContent = null;
    } 
  }
  
  private SignedData getSignedData() throws CMSException {
    try {
      return SignedData.getInstance(this.contentInfo.getContent());
    } catch (ClassCastException classCastException) {
      throw new CMSException("Malformed content.", classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("Malformed content.", illegalArgumentException);
    } 
  }
  
  public int getVersion() {
    return this.signedData.getVersion().getValue().intValue();
  }
  
  public SignerInformationStore getSignerInfos() {
    if (this.signerInfoStore == null) {
      ASN1Set aSN1Set = this.signedData.getSignerInfos();
      ArrayList<SignerInformation> arrayList = new ArrayList();
      for (byte b = 0; b != aSN1Set.size(); b++) {
        SignerInfo signerInfo = SignerInfo.getInstance(aSN1Set.getObjectAt(b));
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.signedData.getEncapContentInfo().getContentType();
        if (this.hashes == null) {
          arrayList.add(new SignerInformation(signerInfo, aSN1ObjectIdentifier, this.signedContent, null));
        } else {
          Object object = this.hashes.keySet().iterator().next();
          byte[] arrayOfByte = (object instanceof String) ? (byte[])this.hashes.get(signerInfo.getDigestAlgorithm().getAlgorithm().getId()) : (byte[])this.hashes.get(signerInfo.getDigestAlgorithm().getAlgorithm());
          arrayList.add(new SignerInformation(signerInfo, aSN1ObjectIdentifier, null, arrayOfByte));
        } 
      } 
      this.signerInfoStore = new SignerInformationStore(arrayList);
    } 
    return this.signerInfoStore;
  }
  
  public boolean isDetachedSignature() {
    return (this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0);
  }
  
  public boolean isCertificateManagementMessage() {
    return (this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0);
  }
  
  public Store<X509CertificateHolder> getCertificates() {
    return HELPER.getCertificates(this.signedData.getCertificates());
  }
  
  public Store<X509CRLHolder> getCRLs() {
    return HELPER.getCRLs(this.signedData.getCRLs());
  }
  
  public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
    return HELPER.getAttributeCertificates(this.signedData.getCertificates());
  }
  
  public Store getOtherRevocationInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return HELPER.getOtherRevocationInfo(paramASN1ObjectIdentifier, this.signedData.getCRLs());
  }
  
  public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
    HashSet<AlgorithmIdentifier> hashSet = new HashSet(this.signedData.getDigestAlgorithms().size());
    Enumeration enumeration = this.signedData.getDigestAlgorithms().getObjects();
    while (enumeration.hasMoreElements())
      hashSet.add(AlgorithmIdentifier.getInstance(enumeration.nextElement())); 
    return Collections.unmodifiableSet(hashSet);
  }
  
  public String getSignedContentTypeOID() {
    return this.signedData.getEncapContentInfo().getContentType().getId();
  }
  
  public CMSTypedData getSignedContent() {
    return this.signedContent;
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
  
  public boolean verifySignatures(SignerInformationVerifierProvider paramSignerInformationVerifierProvider) throws CMSException {
    return verifySignatures(paramSignerInformationVerifierProvider, false);
  }
  
  public boolean verifySignatures(SignerInformationVerifierProvider paramSignerInformationVerifierProvider, boolean paramBoolean) throws CMSException {
    Collection<SignerInformation> collection = getSignerInfos().getSigners();
    for (SignerInformation signerInformation : collection) {
      try {
        SignerInformationVerifier signerInformationVerifier = paramSignerInformationVerifierProvider.get(signerInformation.getSID());
        if (!signerInformation.verify(signerInformationVerifier))
          return false; 
        if (!paramBoolean) {
          Collection<SignerInformation> collection1 = signerInformation.getCounterSignatures().getSigners();
          Iterator<SignerInformation> iterator = collection1.iterator();
          while (iterator.hasNext()) {
            if (!verifyCounterSignature(iterator.next(), paramSignerInformationVerifierProvider))
              return false; 
          } 
        } 
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMSException("failure in verifier provider: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
    } 
    return true;
  }
  
  private boolean verifyCounterSignature(SignerInformation paramSignerInformation, SignerInformationVerifierProvider paramSignerInformationVerifierProvider) throws OperatorCreationException, CMSException {
    SignerInformationVerifier signerInformationVerifier = paramSignerInformationVerifierProvider.get(paramSignerInformation.getSID());
    if (!paramSignerInformation.verify(signerInformationVerifier))
      return false; 
    Collection<SignerInformation> collection = paramSignerInformation.getCounterSignatures().getSigners();
    Iterator<SignerInformation> iterator = collection.iterator();
    while (iterator.hasNext()) {
      if (!verifyCounterSignature(iterator.next(), paramSignerInformationVerifierProvider))
        return false; 
    } 
    return true;
  }
  
  public static CMSSignedData replaceSigners(CMSSignedData paramCMSSignedData, SignerInformationStore paramSignerInformationStore) {
    CMSSignedData cMSSignedData = new CMSSignedData(paramCMSSignedData);
    cMSSignedData.signerInfoStore = paramSignerInformationStore;
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (SignerInformation signerInformation : paramSignerInformationStore.getSigners()) {
      aSN1EncodableVector1.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID()));
      aSN1EncodableVector2.add((ASN1Encodable)signerInformation.toASN1Structure());
    } 
    DERSet dERSet1 = new DERSet(aSN1EncodableVector1);
    DERSet dERSet2 = new DERSet(aSN1EncodableVector2);
    ASN1Sequence aSN1Sequence = (ASN1Sequence)paramCMSSignedData.signedData.toASN1Primitive();
    aSN1EncodableVector2 = new ASN1EncodableVector();
    aSN1EncodableVector2.add(aSN1Sequence.getObjectAt(0));
    aSN1EncodableVector2.add((ASN1Encodable)dERSet1);
    for (byte b = 2; b != aSN1Sequence.size() - 1; b++)
      aSN1EncodableVector2.add(aSN1Sequence.getObjectAt(b)); 
    aSN1EncodableVector2.add((ASN1Encodable)dERSet2);
    cMSSignedData.signedData = SignedData.getInstance(new BERSequence(aSN1EncodableVector2));
    cMSSignedData.contentInfo = new ContentInfo(cMSSignedData.contentInfo.getContentType(), (ASN1Encodable)cMSSignedData.signedData);
    return cMSSignedData;
  }
  
  public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData paramCMSSignedData, Store paramStore1, Store paramStore2, Store paramStore3) throws CMSException {
    CMSSignedData cMSSignedData = new CMSSignedData(paramCMSSignedData);
    ASN1Set aSN1Set1 = null;
    ASN1Set aSN1Set2 = null;
    if (paramStore1 != null || paramStore2 != null) {
      ArrayList arrayList = new ArrayList();
      if (paramStore1 != null)
        arrayList.addAll(CMSUtils.getCertificatesFromStore(paramStore1)); 
      if (paramStore2 != null)
        arrayList.addAll(CMSUtils.getAttributeCertificatesFromStore(paramStore2)); 
      ASN1Set aSN1Set = CMSUtils.createBerSetFromList(arrayList);
      if (aSN1Set.size() != 0)
        aSN1Set1 = aSN1Set; 
    } 
    if (paramStore3 != null) {
      ASN1Set aSN1Set = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(paramStore3));
      if (aSN1Set.size() != 0)
        aSN1Set2 = aSN1Set; 
    } 
    cMSSignedData.signedData = new SignedData(paramCMSSignedData.signedData.getDigestAlgorithms(), paramCMSSignedData.signedData.getEncapContentInfo(), aSN1Set1, aSN1Set2, paramCMSSignedData.signedData.getSignerInfos());
    cMSSignedData.contentInfo = new ContentInfo(cMSSignedData.contentInfo.getContentType(), (ASN1Encodable)cMSSignedData.signedData);
    return cMSSignedData;
  }
}
