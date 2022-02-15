package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.Encodable;

public class CMSCompressedData implements Encodable {
  ContentInfo contentInfo;
  
  CompressedData comData;
  
  public CMSCompressedData(byte[] paramArrayOfbyte) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSCompressedData(InputStream paramInputStream) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream));
  }
  
  public CMSCompressedData(ContentInfo paramContentInfo) throws CMSException {
    this.contentInfo = paramContentInfo;
    try {
      this.comData = CompressedData.getInstance(paramContentInfo.getContent());
    } catch (ClassCastException classCastException) {
      throw new CMSException("Malformed content.", classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("Malformed content.", illegalArgumentException);
    } 
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.contentInfo.getContentType();
  }
  
  public byte[] getContent(InputExpanderProvider paramInputExpanderProvider) throws CMSException {
    ContentInfo contentInfo = this.comData.getEncapContentInfo();
    ASN1OctetString aSN1OctetString = (ASN1OctetString)contentInfo.getContent();
    InputExpander inputExpander = paramInputExpanderProvider.get(this.comData.getCompressionAlgorithmIdentifier());
    InputStream inputStream = inputExpander.getInputStream(aSN1OctetString.getOctetStream());
    try {
      return CMSUtils.streamToByteArray(inputStream);
    } catch (IOException iOException) {
      throw new CMSException("exception reading compressed stream.", iOException);
    } 
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
}
