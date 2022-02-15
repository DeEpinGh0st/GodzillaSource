package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.io.Streams;

public class CMSTimeStampedDataGenerator extends CMSTimeStampedGenerator {
  public CMSTimeStampedData generate(TimeStampToken paramTimeStampToken) throws CMSException {
    return generate(paramTimeStampToken, (InputStream)null);
  }
  
  public CMSTimeStampedData generate(TimeStampToken paramTimeStampToken, byte[] paramArrayOfbyte) throws CMSException {
    return generate(paramTimeStampToken, new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSTimeStampedData generate(TimeStampToken paramTimeStampToken, InputStream paramInputStream) throws CMSException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    if (paramInputStream != null)
      try {
        Streams.pipeAll(paramInputStream, byteArrayOutputStream);
      } catch (IOException iOException) {
        throw new CMSException("exception encapsulating content: " + iOException.getMessage(), iOException);
      }  
    BEROctetString bEROctetString = null;
    if (byteArrayOutputStream.size() != 0)
      bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray()); 
    TimeStampAndCRL timeStampAndCRL = new TimeStampAndCRL(paramTimeStampToken.toCMSSignedData().toASN1Structure());
    DERIA5String dERIA5String = null;
    if (this.dataUri != null)
      dERIA5String = new DERIA5String(this.dataUri.toString()); 
    return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData(dERIA5String, this.metaData, (ASN1OctetString)bEROctetString, new Evidence(new TimeStampTokenEvidence(timeStampAndCRL)))));
  }
}
