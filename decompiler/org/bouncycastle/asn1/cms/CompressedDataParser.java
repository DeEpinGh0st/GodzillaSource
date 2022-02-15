package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CompressedDataParser {
  private ASN1Integer _version;
  
  private AlgorithmIdentifier _compressionAlgorithm;
  
  private ContentInfoParser _encapContentInfo;
  
  public CompressedDataParser(ASN1SequenceParser paramASN1SequenceParser) throws IOException {
    this._version = (ASN1Integer)paramASN1SequenceParser.readObject();
    this._compressionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1SequenceParser.readObject().toASN1Primitive());
    this._encapContentInfo = new ContentInfoParser((ASN1SequenceParser)paramASN1SequenceParser.readObject());
  }
  
  public ASN1Integer getVersion() {
    return this._version;
  }
  
  public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
    return this._compressionAlgorithm;
  }
  
  public ContentInfoParser getEncapContentInfo() {
    return this._encapContentInfo;
  }
}
