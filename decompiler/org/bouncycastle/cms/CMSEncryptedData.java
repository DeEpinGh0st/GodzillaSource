package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;

public class CMSEncryptedData {
  private ContentInfo contentInfo;
  
  private EncryptedData encryptedData;
  
  public CMSEncryptedData(ContentInfo paramContentInfo) {
    this.contentInfo = paramContentInfo;
    this.encryptedData = EncryptedData.getInstance(paramContentInfo.getContent());
  }
  
  public byte[] getContent(InputDecryptorProvider paramInputDecryptorProvider) throws CMSException {
    try {
      return CMSUtils.streamToByteArray(getContentStream(paramInputDecryptorProvider).getContentStream());
    } catch (IOException iOException) {
      throw new CMSException("unable to parse internal stream: " + iOException.getMessage(), iOException);
    } 
  }
  
  public CMSTypedStream getContentStream(InputDecryptorProvider paramInputDecryptorProvider) throws CMSException {
    try {
      EncryptedContentInfo encryptedContentInfo = this.encryptedData.getEncryptedContentInfo();
      InputDecryptor inputDecryptor = paramInputDecryptorProvider.get(encryptedContentInfo.getContentEncryptionAlgorithm());
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encryptedContentInfo.getEncryptedContent().getOctets());
      return new CMSTypedStream(encryptedContentInfo.getContentType(), inputDecryptor.getInputStream(byteArrayInputStream));
    } catch (Exception exception) {
      throw new CMSException("unable to create stream: " + exception.getMessage(), exception);
    } 
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
}
