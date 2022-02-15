package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.DigestedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSDigestedData implements Encodable {
  private ContentInfo contentInfo;
  
  private DigestedData digestedData;
  
  public CMSDigestedData(byte[] paramArrayOfbyte) throws CMSException {
    this(CMSUtils.readContentInfo(paramArrayOfbyte));
  }
  
  public CMSDigestedData(InputStream paramInputStream) throws CMSException {
    this(CMSUtils.readContentInfo(paramInputStream));
  }
  
  public CMSDigestedData(ContentInfo paramContentInfo) throws CMSException {
    this.contentInfo = paramContentInfo;
    try {
      this.digestedData = DigestedData.getInstance(paramContentInfo.getContent());
    } catch (ClassCastException classCastException) {
      throw new CMSException("Malformed content.", classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("Malformed content.", illegalArgumentException);
    } 
  }
  
  public ASN1ObjectIdentifier getContentType() {
    return this.contentInfo.getContentType();
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return this.digestedData.getDigestAlgorithm();
  }
  
  public CMSProcessable getDigestedContent() throws CMSException {
    ContentInfo contentInfo = this.digestedData.getEncapContentInfo();
    try {
      return new CMSProcessableByteArray(contentInfo.getContentType(), ((ASN1OctetString)contentInfo.getContent()).getOctets());
    } catch (Exception exception) {
      throw new CMSException("exception reading digested stream.", exception);
    } 
  }
  
  public ContentInfo toASN1Structure() {
    return this.contentInfo;
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
  
  public boolean verify(DigestCalculatorProvider paramDigestCalculatorProvider) throws CMSException {
    try {
      ContentInfo contentInfo = this.digestedData.getEncapContentInfo();
      DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(this.digestedData.getDigestAlgorithm());
      OutputStream outputStream = digestCalculator.getOutputStream();
      outputStream.write(((ASN1OctetString)contentInfo.getContent()).getOctets());
      return Arrays.areEqual(this.digestedData.getDigest(), digestCalculator.getDigest());
    } catch (OperatorCreationException operatorCreationException) {
      throw new CMSException("unable to create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
    } catch (IOException iOException) {
      throw new CMSException("unable process content: " + iOException.getMessage(), iOException);
    } 
  }
}
