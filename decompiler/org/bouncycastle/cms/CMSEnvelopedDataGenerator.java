package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataGenerator extends CMSEnvelopedGenerator {
  private CMSEnvelopedData doGenerate(CMSTypedData paramCMSTypedData, OutputEncryptor paramOutputEncryptor) throws CMSException {
    if (!this.oldRecipientInfoGenerators.isEmpty())
      throw new IllegalStateException("can only use addRecipientGenerator() with this method"); 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      OutputStream outputStream = paramOutputEncryptor.getOutputStream(byteArrayOutputStream);
      paramCMSTypedData.write(outputStream);
      outputStream.close();
    } catch (IOException iOException) {
      throw new CMSException("");
    } 
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    AlgorithmIdentifier algorithmIdentifier = paramOutputEncryptor.getAlgorithmIdentifier();
    BEROctetString bEROctetString = new BEROctetString(arrayOfByte);
    GenericKey genericKey = paramOutputEncryptor.getKey();
    for (RecipientInfoGenerator recipientInfoGenerator : this.recipientInfoGenerators)
      aSN1EncodableVector.add((ASN1Encodable)recipientInfoGenerator.generate(genericKey)); 
    EncryptedContentInfo encryptedContentInfo = new EncryptedContentInfo(paramCMSTypedData.getContentType(), algorithmIdentifier, (ASN1OctetString)bEROctetString);
    BERSet bERSet = null;
    if (this.unprotectedAttributeGenerator != null) {
      AttributeTable attributeTable = this.unprotectedAttributeGenerator.getAttributes(new HashMap<Object, Object>());
      bERSet = new BERSet(attributeTable.toASN1EncodableVector());
    } 
    ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.envelopedData, (ASN1Encodable)new EnvelopedData(this.originatorInfo, (ASN1Set)new DERSet(aSN1EncodableVector), encryptedContentInfo, (ASN1Set)bERSet));
    return new CMSEnvelopedData(contentInfo);
  }
  
  public CMSEnvelopedData generate(CMSTypedData paramCMSTypedData, OutputEncryptor paramOutputEncryptor) throws CMSException {
    return doGenerate(paramCMSTypedData, paramOutputEncryptor);
  }
}
