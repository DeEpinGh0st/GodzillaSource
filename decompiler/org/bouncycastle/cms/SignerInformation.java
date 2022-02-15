package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInformation {
  private final SignerId sid;
  
  private final CMSProcessable content;
  
  private final byte[] signature;
  
  private final ASN1ObjectIdentifier contentType;
  
  private final boolean isCounterSignature;
  
  private AttributeTable signedAttributeValues;
  
  private AttributeTable unsignedAttributeValues;
  
  private byte[] resultDigest;
  
  protected final SignerInfo info;
  
  protected final AlgorithmIdentifier digestAlgorithm;
  
  protected final AlgorithmIdentifier encryptionAlgorithm;
  
  protected final ASN1Set signedAttributeSet;
  
  protected final ASN1Set unsignedAttributeSet;
  
  SignerInformation(SignerInfo paramSignerInfo, ASN1ObjectIdentifier paramASN1ObjectIdentifier, CMSProcessable paramCMSProcessable, byte[] paramArrayOfbyte) {
    this.info = paramSignerInfo;
    this.contentType = paramASN1ObjectIdentifier;
    this.isCounterSignature = (paramASN1ObjectIdentifier == null);
    SignerIdentifier signerIdentifier = paramSignerInfo.getSID();
    if (signerIdentifier.isTagged()) {
      ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(signerIdentifier.getId());
      this.sid = new SignerId(aSN1OctetString.getOctets());
    } else {
      IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(signerIdentifier.getId());
      this.sid = new SignerId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
    } 
    this.digestAlgorithm = paramSignerInfo.getDigestAlgorithm();
    this.signedAttributeSet = paramSignerInfo.getAuthenticatedAttributes();
    this.unsignedAttributeSet = paramSignerInfo.getUnauthenticatedAttributes();
    this.encryptionAlgorithm = paramSignerInfo.getDigestEncryptionAlgorithm();
    this.signature = paramSignerInfo.getEncryptedDigest().getOctets();
    this.content = paramCMSProcessable;
    this.resultDigest = paramArrayOfbyte;
  }
  
  protected SignerInformation(SignerInformation paramSignerInformation) {
    this.info = paramSignerInformation.info;
    this.contentType = paramSignerInformation.contentType;
    this.isCounterSignature = paramSignerInformation.isCounterSignature();
    this.sid = paramSignerInformation.getSID();
    this.digestAlgorithm = this.info.getDigestAlgorithm();
    this.signedAttributeSet = this.info.getAuthenticatedAttributes();
    this.unsignedAttributeSet = this.info.getUnauthenticatedAttributes();
    this.encryptionAlgorithm = this.info.getDigestEncryptionAlgorithm();
    this.signature = this.info.getEncryptedDigest().getOctets();
    this.content = paramSignerInformation.content;
    this.resultDigest = paramSignerInformation.resultDigest;
  }
  
  public boolean isCounterSignature() {
    return this.isCounterSignature;
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.contentType;
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
  
  public SignerId getSID() {
    return this.sid;
  }
  
  public int getVersion() {
    return this.info.getVersion().getValue().intValue();
  }
  
  public AlgorithmIdentifier getDigestAlgorithmID() {
    return this.digestAlgorithm;
  }
  
  public String getDigestAlgOID() {
    return this.digestAlgorithm.getAlgorithm().getId();
  }
  
  public byte[] getDigestAlgParams() {
    try {
      return encodeObj(this.digestAlgorithm.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting digest parameters " + exception);
    } 
  }
  
  public byte[] getContentDigest() {
    if (this.resultDigest == null)
      throw new IllegalStateException("method can only be called after verify."); 
    return Arrays.clone(this.resultDigest);
  }
  
  public String getEncryptionAlgOID() {
    return this.encryptionAlgorithm.getAlgorithm().getId();
  }
  
  public byte[] getEncryptionAlgParams() {
    try {
      return encodeObj(this.encryptionAlgorithm.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public AttributeTable getSignedAttributes() {
    if (this.signedAttributeSet != null && this.signedAttributeValues == null)
      this.signedAttributeValues = new AttributeTable(this.signedAttributeSet); 
    return this.signedAttributeValues;
  }
  
  public AttributeTable getUnsignedAttributes() {
    if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null)
      this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet); 
    return this.unsignedAttributeValues;
  }
  
  public byte[] getSignature() {
    return Arrays.clone(this.signature);
  }
  
  public SignerInformationStore getCounterSignatures() {
    AttributeTable attributeTable = getUnsignedAttributes();
    if (attributeTable == null)
      return new SignerInformationStore(new ArrayList<SignerInformation>(0)); 
    ArrayList<SignerInformation> arrayList = new ArrayList();
    ASN1EncodableVector aSN1EncodableVector = attributeTable.getAll(CMSAttributes.counterSignature);
    for (byte b = 0; b < aSN1EncodableVector.size(); b++) {
      Attribute attribute = (Attribute)aSN1EncodableVector.get(b);
      ASN1Set aSN1Set = attribute.getAttrValues();
      if (aSN1Set.size() < 1);
      Enumeration enumeration = aSN1Set.getObjects();
      while (enumeration.hasMoreElements()) {
        SignerInfo signerInfo = SignerInfo.getInstance(enumeration.nextElement());
        arrayList.add(new SignerInformation(signerInfo, null, new CMSProcessableByteArray(getSignature()), null));
      } 
    } 
    return new SignerInformationStore(arrayList);
  }
  
  public byte[] getEncodedSignedAttributes() throws IOException {
    return (this.signedAttributeSet != null) ? this.signedAttributeSet.getEncoded("DER") : null;
  }
  
  private boolean doVerify(SignerInformationVerifier paramSignerInformationVerifier) throws CMSException {
    ContentVerifier contentVerifier;
    String str = CMSSignedHelper.INSTANCE.getEncryptionAlgName(getEncryptionAlgOID());
    try {
      contentVerifier = paramSignerInformationVerifier.getContentVerifier(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
    } catch (OperatorCreationException operatorCreationException) {
      throw new CMSException("can't create content verifier: " + operatorCreationException.getMessage(), operatorCreationException);
    } 
    try {
      OutputStream outputStream = contentVerifier.getOutputStream();
      if (this.resultDigest == null) {
        DigestCalculator digestCalculator = paramSignerInformationVerifier.getDigestCalculator(getDigestAlgorithmID());
        if (this.content != null) {
          OutputStream outputStream1 = digestCalculator.getOutputStream();
          if (this.signedAttributeSet == null) {
            if (contentVerifier instanceof RawContentVerifier) {
              this.content.write(outputStream1);
            } else {
              TeeOutputStream teeOutputStream = new TeeOutputStream(outputStream1, outputStream);
              this.content.write((OutputStream)teeOutputStream);
              teeOutputStream.close();
            } 
          } else {
            this.content.write(outputStream1);
            outputStream.write(getEncodedSignedAttributes());
          } 
          outputStream1.close();
        } else if (this.signedAttributeSet != null) {
          outputStream.write(getEncodedSignedAttributes());
        } else {
          throw new CMSException("data not encapsulated in signature - use detached constructor.");
        } 
        this.resultDigest = digestCalculator.getDigest();
      } else if (this.signedAttributeSet == null) {
        if (this.content != null)
          this.content.write(outputStream); 
      } else {
        outputStream.write(getEncodedSignedAttributes());
      } 
      outputStream.close();
    } catch (IOException iOException) {
      throw new CMSException("can't process mime object to create signature.", iOException);
    } catch (OperatorCreationException operatorCreationException) {
      throw new CMSException("can't create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
    } 
    ASN1Primitive aSN1Primitive1 = getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
    if (aSN1Primitive1 == null) {
      if (!this.isCounterSignature && this.signedAttributeSet != null)
        throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data"); 
    } else {
      if (this.isCounterSignature)
        throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute"); 
      if (!(aSN1Primitive1 instanceof ASN1ObjectIdentifier))
        throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'"); 
      ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Primitive1;
      if (!aSN1ObjectIdentifier.equals(this.contentType))
        throw new CMSException("content-type attribute value does not match eContentType"); 
    } 
    AttributeTable attributeTable1 = getSignedAttributes();
    AttributeTable attributeTable3 = getUnsignedAttributes();
    if (attributeTable3 != null && attributeTable3.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0)
      throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute"); 
    if (attributeTable1 != null) {
      ASN1EncodableVector aSN1EncodableVector = attributeTable1.getAll(CMSAttributes.cmsAlgorithmProtect);
      if (aSN1EncodableVector.size() > 1)
        throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present"); 
      if (aSN1EncodableVector.size() > 0) {
        Attribute attribute = Attribute.getInstance(aSN1EncodableVector.get(0));
        if (attribute.getAttrValues().size() != 1)
          throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value"); 
        CMSAlgorithmProtection cMSAlgorithmProtection = CMSAlgorithmProtection.getInstance(attribute.getAttributeValues()[0]);
        if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getDigestAlgorithm(), this.info.getDigestAlgorithm()))
          throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm"); 
        if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm()))
          throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm"); 
      } 
    } 
    ASN1Primitive aSN1Primitive2 = getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
    if (aSN1Primitive2 == null) {
      if (this.signedAttributeSet != null)
        throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present"); 
    } else {
      if (!(aSN1Primitive2 instanceof ASN1OctetString))
        throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'"); 
      ASN1OctetString aSN1OctetString = (ASN1OctetString)aSN1Primitive2;
      if (!Arrays.constantTimeAreEqual(this.resultDigest, aSN1OctetString.getOctets()))
        throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value"); 
    } 
    if (attributeTable1 != null && attributeTable1.getAll(CMSAttributes.counterSignature).size() > 0)
      throw new CMSException("A countersignature attribute MUST NOT be a signed attribute"); 
    AttributeTable attributeTable2 = getUnsignedAttributes();
    if (attributeTable2 != null) {
      ASN1EncodableVector aSN1EncodableVector = attributeTable2.getAll(CMSAttributes.counterSignature);
      for (byte b = 0; b < aSN1EncodableVector.size(); b++) {
        Attribute attribute = Attribute.getInstance(aSN1EncodableVector.get(b));
        if (attribute.getAttrValues().size() < 1)
          throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue"); 
      } 
    } 
    try {
      if (this.signedAttributeSet == null && this.resultDigest != null && contentVerifier instanceof RawContentVerifier) {
        RawContentVerifier rawContentVerifier = (RawContentVerifier)contentVerifier;
        if (str.equals("RSA")) {
          DigestInfo digestInfo = new DigestInfo(new AlgorithmIdentifier(this.digestAlgorithm.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), this.resultDigest);
          return rawContentVerifier.verify(digestInfo.getEncoded("DER"), getSignature());
        } 
        return rawContentVerifier.verify(this.resultDigest, getSignature());
      } 
      return contentVerifier.verify(getSignature());
    } catch (IOException iOException) {
      throw new CMSException("can't process mime object to create signature.", iOException);
    } 
  }
  
  public boolean verify(SignerInformationVerifier paramSignerInformationVerifier) throws CMSException {
    Time time = getSigningTime();
    if (paramSignerInformationVerifier.hasAssociatedCertificate() && time != null) {
      X509CertificateHolder x509CertificateHolder = paramSignerInformationVerifier.getAssociatedCertificate();
      if (!x509CertificateHolder.isValidOn(time.getDate()))
        throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime"); 
    } 
    return doVerify(paramSignerInformationVerifier);
  }
  
  public SignerInfo toASN1Structure() {
    return this.info;
  }
  
  private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) throws CMSException {
    Attribute attribute;
    ASN1Set aSN1Set;
    AttributeTable attributeTable1 = getUnsignedAttributes();
    if (attributeTable1 != null && attributeTable1.getAll(paramASN1ObjectIdentifier).size() > 0)
      throw new CMSException("The " + paramString + " attribute MUST NOT be an unsigned attribute"); 
    AttributeTable attributeTable2 = getSignedAttributes();
    if (attributeTable2 == null)
      return null; 
    ASN1EncodableVector aSN1EncodableVector = attributeTable2.getAll(paramASN1ObjectIdentifier);
    switch (aSN1EncodableVector.size()) {
      case 0:
        return null;
      case 1:
        attribute = (Attribute)aSN1EncodableVector.get(0);
        aSN1Set = attribute.getAttrValues();
        if (aSN1Set.size() != 1)
          throw new CMSException("A " + paramString + " attribute MUST have a single attribute value"); 
        return aSN1Set.getObjectAt(0).toASN1Primitive();
    } 
    throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + paramString + " attribute");
  }
  
  private Time getSigningTime() throws CMSException {
    ASN1Primitive aSN1Primitive = getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
    if (aSN1Primitive == null)
      return null; 
    try {
      return Time.getInstance(aSN1Primitive);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("signing-time attribute value not a valid 'Time' structure");
    } 
  }
  
  public static SignerInformation replaceUnsignedAttributes(SignerInformation paramSignerInformation, AttributeTable paramAttributeTable) {
    SignerInfo signerInfo = paramSignerInformation.info;
    DERSet dERSet = null;
    if (paramAttributeTable != null)
      dERSet = new DERSet(paramAttributeTable.toASN1EncodableVector()); 
    return new SignerInformation(new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), (ASN1Set)dERSet), paramSignerInformation.contentType, paramSignerInformation.content, null);
  }
  
  public static SignerInformation addCounterSigners(SignerInformation paramSignerInformation, SignerInformationStore paramSignerInformationStore) {
    ASN1EncodableVector aSN1EncodableVector1;
    SignerInfo signerInfo = paramSignerInformation.info;
    AttributeTable attributeTable = paramSignerInformation.getUnsignedAttributes();
    if (attributeTable != null) {
      aSN1EncodableVector1 = attributeTable.toASN1EncodableVector();
    } else {
      aSN1EncodableVector1 = new ASN1EncodableVector();
    } 
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    Iterator<SignerInformation> iterator = paramSignerInformationStore.getSigners().iterator();
    while (iterator.hasNext())
      aSN1EncodableVector2.add((ASN1Encodable)((SignerInformation)iterator.next()).toASN1Structure()); 
    aSN1EncodableVector1.add((ASN1Encodable)new Attribute(CMSAttributes.counterSignature, (ASN1Set)new DERSet(aSN1EncodableVector2)));
    return new SignerInformation(new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), (ASN1Set)new DERSet(aSN1EncodableVector1)), paramSignerInformation.contentType, paramSignerInformation.content, null);
  }
}
