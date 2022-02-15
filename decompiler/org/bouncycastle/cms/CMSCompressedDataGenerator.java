package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataGenerator {
  public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
  
  public CMSCompressedData generate(CMSTypedData paramCMSTypedData, OutputCompressor paramOutputCompressor) throws CMSException {
    AlgorithmIdentifier algorithmIdentifier;
    BEROctetString bEROctetString;
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      OutputStream outputStream = paramOutputCompressor.getOutputStream(byteArrayOutputStream);
      paramCMSTypedData.write(outputStream);
      outputStream.close();
      algorithmIdentifier = paramOutputCompressor.getAlgorithmIdentifier();
      bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
    } catch (IOException iOException) {
      throw new CMSException("exception encoding data.", iOException);
    } 
    ContentInfo contentInfo1 = new ContentInfo(paramCMSTypedData.getContentType(), (ASN1Encodable)bEROctetString);
    ContentInfo contentInfo2 = new ContentInfo(CMSObjectIdentifiers.compressedData, (ASN1Encodable)new CompressedData(algorithmIdentifier, contentInfo1));
    return new CMSCompressedData(contentInfo2);
  }
}
