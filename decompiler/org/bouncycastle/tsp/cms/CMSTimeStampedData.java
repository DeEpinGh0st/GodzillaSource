package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;

public class CMSTimeStampedData {
  private TimeStampedData timeStampedData;
  
  private ContentInfo contentInfo;
  
  private TimeStampDataUtil util;
  
  public CMSTimeStampedData(ContentInfo paramContentInfo) {
    initialize(paramContentInfo);
  }
  
  public CMSTimeStampedData(InputStream paramInputStream) throws IOException {
    try {
      initialize(ContentInfo.getInstance((new ASN1InputStream(paramInputStream)).readObject()));
    } catch (ClassCastException classCastException) {
      throw new IOException("Malformed content: " + classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException("Malformed content: " + illegalArgumentException);
    } 
  }
  
  public CMSTimeStampedData(byte[] paramArrayOfbyte) throws IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  private void initialize(ContentInfo paramContentInfo) {
    this.contentInfo = paramContentInfo;
    if (CMSObjectIdentifiers.timestampedData.equals(paramContentInfo.getContentType())) {
      this.timeStampedData = TimeStampedData.getInstance(paramContentInfo.getContent());
    } else {
      throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
    } 
    this.util = new TimeStampDataUtil(this.timeStampedData);
  }
  
  public byte[] calculateNextHash(DigestCalculator paramDigestCalculator) throws CMSException {
    return this.util.calculateNextHash(paramDigestCalculator);
  }
  
  public CMSTimeStampedData addTimeStamp(TimeStampToken paramTimeStampToken) throws CMSException {
    TimeStampAndCRL[] arrayOfTimeStampAndCRL1 = this.util.getTimeStamps();
    TimeStampAndCRL[] arrayOfTimeStampAndCRL2 = new TimeStampAndCRL[arrayOfTimeStampAndCRL1.length + 1];
    System.arraycopy(arrayOfTimeStampAndCRL1, 0, arrayOfTimeStampAndCRL2, 0, arrayOfTimeStampAndCRL1.length);
    arrayOfTimeStampAndCRL2[arrayOfTimeStampAndCRL1.length] = new TimeStampAndCRL(paramTimeStampToken.toCMSSignedData().toASN1Structure());
    return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData(this.timeStampedData.getDataUri(), this.timeStampedData.getMetaData(), this.timeStampedData.getContent(), new Evidence(new TimeStampTokenEvidence(arrayOfTimeStampAndCRL2)))));
  }
  
  public byte[] getContent() {
    return (this.timeStampedData.getContent() != null) ? this.timeStampedData.getContent().getOctets() : null;
  }
  
  public URI getDataUri() throws URISyntaxException {
    DERIA5String dERIA5String = this.timeStampedData.getDataUri();
    return (dERIA5String != null) ? new URI(dERIA5String.getString()) : null;
  }
  
  public String getFileName() {
    return this.util.getFileName();
  }
  
  public String getMediaType() {
    return this.util.getMediaType();
  }
  
  public AttributeTable getOtherMetaData() {
    return this.util.getOtherMetaData();
  }
  
  public TimeStampToken[] getTimeStampTokens() throws CMSException {
    return this.util.getTimeStampTokens();
  }
  
  public void initialiseMessageImprintDigestCalculator(DigestCalculator paramDigestCalculator) throws CMSException {
    this.util.initialiseMessageImprintDigestCalculator(paramDigestCalculator);
  }
  
  public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider paramDigestCalculatorProvider) throws OperatorCreationException {
    return this.util.getMessageImprintDigestCalculator(paramDigestCalculatorProvider);
  }
  
  public void validate(DigestCalculatorProvider paramDigestCalculatorProvider, byte[] paramArrayOfbyte) throws ImprintDigestInvalidException, CMSException {
    this.util.validate(paramDigestCalculatorProvider, paramArrayOfbyte);
  }
  
  public void validate(DigestCalculatorProvider paramDigestCalculatorProvider, byte[] paramArrayOfbyte, TimeStampToken paramTimeStampToken) throws ImprintDigestInvalidException, CMSException {
    this.util.validate(paramDigestCalculatorProvider, paramArrayOfbyte, paramTimeStampToken);
  }
  
  public byte[] getEncoded() throws IOException {
    return this.contentInfo.getEncoded();
  }
}
