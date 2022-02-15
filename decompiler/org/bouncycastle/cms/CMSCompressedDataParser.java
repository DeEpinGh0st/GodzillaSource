package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.cms.CompressedDataParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;

public class CMSCompressedDataParser extends CMSContentInfoParser {
  public CMSCompressedDataParser(byte[] paramArrayOfbyte) throws CMSException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public CMSCompressedDataParser(InputStream paramInputStream) throws CMSException {
    super(paramInputStream);
  }
  
  public CMSTypedStream getContent(InputExpanderProvider paramInputExpanderProvider) throws CMSException {
    try {
      CompressedDataParser compressedDataParser = new CompressedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
      ContentInfoParser contentInfoParser = compressedDataParser.getEncapContentInfo();
      InputExpander inputExpander = paramInputExpanderProvider.get(compressedDataParser.getCompressionAlgorithmIdentifier());
      ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)contentInfoParser.getContent(4);
      return new CMSTypedStream(contentInfoParser.getContentType().getId(), inputExpander.getInputStream(aSN1OctetStringParser.getOctetStream()));
    } catch (IOException iOException) {
      throw new CMSException("IOException reading compressed content.", iOException);
    } 
  }
}
