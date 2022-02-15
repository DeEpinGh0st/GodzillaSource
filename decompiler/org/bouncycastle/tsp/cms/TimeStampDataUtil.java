package org.bouncycastle.tsp.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Arrays;

class TimeStampDataUtil {
  private final TimeStampAndCRL[] timeStamps;
  
  private final MetaDataUtil metaDataUtil;
  
  TimeStampDataUtil(TimeStampedData paramTimeStampedData) {
    this.metaDataUtil = new MetaDataUtil(paramTimeStampedData.getMetaData());
    Evidence evidence = paramTimeStampedData.getTemporalEvidence();
    this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
  }
  
  TimeStampDataUtil(TimeStampedDataParser paramTimeStampedDataParser) throws IOException {
    this.metaDataUtil = new MetaDataUtil(paramTimeStampedDataParser.getMetaData());
    Evidence evidence = paramTimeStampedDataParser.getTemporalEvidence();
    this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
  }
  
  TimeStampToken getTimeStampToken(TimeStampAndCRL paramTimeStampAndCRL) throws CMSException {
    ContentInfo contentInfo = paramTimeStampAndCRL.getTimeStampToken();
    try {
      return new TimeStampToken(contentInfo);
    } catch (IOException iOException) {
      throw new CMSException("unable to parse token data: " + iOException.getMessage(), iOException);
    } catch (TSPException tSPException) {
      if (tSPException.getCause() instanceof CMSException)
        throw (CMSException)tSPException.getCause(); 
      throw new CMSException("token data invalid: " + tSPException.getMessage(), tSPException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("token data invalid: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  void initialiseMessageImprintDigestCalculator(DigestCalculator paramDigestCalculator) throws CMSException {
    this.metaDataUtil.initialiseMessageImprintDigestCalculator(paramDigestCalculator);
  }
  
  DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider paramDigestCalculatorProvider) throws OperatorCreationException {
    try {
      TimeStampToken timeStampToken = getTimeStampToken(this.timeStamps[0]);
      TimeStampTokenInfo timeStampTokenInfo = timeStampToken.getTimeStampInfo();
      ASN1ObjectIdentifier aSN1ObjectIdentifier = timeStampTokenInfo.getMessageImprintAlgOID();
      DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(new AlgorithmIdentifier(aSN1ObjectIdentifier));
      initialiseMessageImprintDigestCalculator(digestCalculator);
      return digestCalculator;
    } catch (CMSException cMSException) {
      throw new OperatorCreationException("unable to extract algorithm ID: " + cMSException.getMessage(), cMSException);
    } 
  }
  
  TimeStampToken[] getTimeStampTokens() throws CMSException {
    TimeStampToken[] arrayOfTimeStampToken = new TimeStampToken[this.timeStamps.length];
    for (byte b = 0; b < this.timeStamps.length; b++)
      arrayOfTimeStampToken[b] = getTimeStampToken(this.timeStamps[b]); 
    return arrayOfTimeStampToken;
  }
  
  TimeStampAndCRL[] getTimeStamps() {
    return this.timeStamps;
  }
  
  byte[] calculateNextHash(DigestCalculator paramDigestCalculator) throws CMSException {
    TimeStampAndCRL timeStampAndCRL = this.timeStamps[this.timeStamps.length - 1];
    OutputStream outputStream = paramDigestCalculator.getOutputStream();
    try {
      outputStream.write(timeStampAndCRL.getEncoded("DER"));
      outputStream.close();
      return paramDigestCalculator.getDigest();
    } catch (IOException iOException) {
      throw new CMSException("exception calculating hash: " + iOException.getMessage(), iOException);
    } 
  }
  
  void validate(DigestCalculatorProvider paramDigestCalculatorProvider, byte[] paramArrayOfbyte) throws ImprintDigestInvalidException, CMSException {
    byte[] arrayOfByte = paramArrayOfbyte;
    for (byte b = 0; b < this.timeStamps.length; b++) {
      try {
        TimeStampToken timeStampToken = getTimeStampToken(this.timeStamps[b]);
        if (b > 0) {
          TimeStampTokenInfo timeStampTokenInfo = timeStampToken.getTimeStampInfo();
          DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(timeStampTokenInfo.getHashAlgorithm());
          digestCalculator.getOutputStream().write(this.timeStamps[b - 1].getEncoded("DER"));
          arrayOfByte = digestCalculator.getDigest();
        } 
        compareDigest(timeStampToken, arrayOfByte);
      } catch (IOException iOException) {
        throw new CMSException("exception calculating hash: " + iOException.getMessage(), iOException);
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMSException("cannot create digest: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
    } 
  }
  
  void validate(DigestCalculatorProvider paramDigestCalculatorProvider, byte[] paramArrayOfbyte, TimeStampToken paramTimeStampToken) throws ImprintDigestInvalidException, CMSException {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1 = paramArrayOfbyte;
    try {
      arrayOfByte2 = paramTimeStampToken.getEncoded();
    } catch (IOException iOException) {
      throw new CMSException("exception encoding timeStampToken: " + iOException.getMessage(), iOException);
    } 
    for (byte b = 0; b < this.timeStamps.length; b++) {
      try {
        TimeStampToken timeStampToken = getTimeStampToken(this.timeStamps[b]);
        if (b > 0) {
          TimeStampTokenInfo timeStampTokenInfo = timeStampToken.getTimeStampInfo();
          DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(timeStampTokenInfo.getHashAlgorithm());
          digestCalculator.getOutputStream().write(this.timeStamps[b - 1].getEncoded("DER"));
          arrayOfByte1 = digestCalculator.getDigest();
        } 
        compareDigest(timeStampToken, arrayOfByte1);
        if (Arrays.areEqual(timeStampToken.getEncoded(), arrayOfByte2))
          return; 
      } catch (IOException iOException) {
        throw new CMSException("exception calculating hash: " + iOException.getMessage(), iOException);
      } catch (OperatorCreationException operatorCreationException) {
        throw new CMSException("cannot create digest: " + operatorCreationException.getMessage(), operatorCreationException);
      } 
    } 
    throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", paramTimeStampToken);
  }
  
  private void compareDigest(TimeStampToken paramTimeStampToken, byte[] paramArrayOfbyte) throws ImprintDigestInvalidException {
    TimeStampTokenInfo timeStampTokenInfo = paramTimeStampToken.getTimeStampInfo();
    byte[] arrayOfByte = timeStampTokenInfo.getMessageImprintDigest();
    if (!Arrays.areEqual(paramArrayOfbyte, arrayOfByte))
      throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", paramTimeStampToken); 
  }
  
  String getFileName() {
    return this.metaDataUtil.getFileName();
  }
  
  String getMediaType() {
    return this.metaDataUtil.getMediaType();
  }
  
  AttributeTable getOtherMetaData() {
    return new AttributeTable(this.metaDataUtil.getOtherMetaData());
  }
}
