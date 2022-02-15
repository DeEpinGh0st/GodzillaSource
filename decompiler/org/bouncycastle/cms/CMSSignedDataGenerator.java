package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;

public class CMSSignedDataGenerator extends CMSSignedGenerator {
  private List signerInfs = new ArrayList();
  
  public CMSSignedData generate(CMSTypedData paramCMSTypedData) throws CMSException {
    return generate(paramCMSTypedData, false);
  }
  
  public CMSSignedData generate(CMSTypedData paramCMSTypedData, boolean paramBoolean) throws CMSException {
    if (!this.signerInfs.isEmpty())
      throw new IllegalStateException("this method can only be used with SignerInfoGenerator"); 
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    this.digests.clear();
    for (SignerInformation signerInformation : this._signers) {
      aSN1EncodableVector1.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID()));
      aSN1EncodableVector2.add((ASN1Encodable)signerInformation.toASN1Structure());
    } 
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramCMSTypedData.getContentType();
    BEROctetString bEROctetString = null;
    if (paramCMSTypedData.getContent() != null) {
      ByteArrayOutputStream byteArrayOutputStream = null;
      if (paramBoolean)
        byteArrayOutputStream = new ByteArrayOutputStream(); 
      OutputStream outputStream = CMSUtils.attachSignersToOutputStream(this.signerGens, byteArrayOutputStream);
      outputStream = CMSUtils.getSafeOutputStream(outputStream);
      try {
        paramCMSTypedData.write(outputStream);
        outputStream.close();
      } catch (IOException iOException) {
        throw new CMSException("data processing exception: " + iOException.getMessage(), iOException);
      } 
      if (paramBoolean)
        bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray()); 
    } 
    for (SignerInfoGenerator signerInfoGenerator : this.signerGens) {
      SignerInfo signerInfo = signerInfoGenerator.generate(aSN1ObjectIdentifier);
      aSN1EncodableVector1.add((ASN1Encodable)signerInfo.getDigestAlgorithm());
      aSN1EncodableVector2.add((ASN1Encodable)signerInfo);
      byte[] arrayOfByte = signerInfoGenerator.getCalculatedDigest();
      if (arrayOfByte != null)
        this.digests.put(signerInfo.getDigestAlgorithm().getAlgorithm().getId(), arrayOfByte); 
    } 
    ASN1Set aSN1Set1 = null;
    if (this.certs.size() != 0)
      aSN1Set1 = CMSUtils.createBerSetFromList(this.certs); 
    ASN1Set aSN1Set2 = null;
    if (this.crls.size() != 0)
      aSN1Set2 = CMSUtils.createBerSetFromList(this.crls); 
    ContentInfo contentInfo1 = new ContentInfo(aSN1ObjectIdentifier, (ASN1Encodable)bEROctetString);
    SignedData signedData = new SignedData((ASN1Set)new DERSet(aSN1EncodableVector1), contentInfo1, aSN1Set1, aSN1Set2, (ASN1Set)new DERSet(aSN1EncodableVector2));
    ContentInfo contentInfo2 = new ContentInfo(CMSObjectIdentifiers.signedData, (ASN1Encodable)signedData);
    return new CMSSignedData(paramCMSTypedData, contentInfo2);
  }
  
  public SignerInformationStore generateCounterSigners(SignerInformation paramSignerInformation) throws CMSException {
    return generate(new CMSProcessableByteArray(null, paramSignerInformation.getSignature()), false).getSignerInfos();
  }
}
