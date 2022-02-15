package org.bouncycastle.asn1;

import java.io.IOException;

public class DERExternalParser implements ASN1Encodable, InMemoryRepresentable {
  private ASN1StreamParser _parser;
  
  public DERExternalParser(ASN1StreamParser paramASN1StreamParser) {
    this._parser = paramASN1StreamParser;
  }
  
  public ASN1Encodable readObject() throws IOException {
    return this._parser.readObject();
  }
  
  public ASN1Primitive getLoadedObject() throws IOException {
    try {
      return new DERExternal(this._parser.readVector());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ASN1Exception(illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public ASN1Primitive toASN1Primitive() {
    try {
      return getLoadedObject();
    } catch (IOException iOException) {
      throw new ASN1ParsingException("unable to get DER object", iOException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ASN1ParsingException("unable to get DER object", illegalArgumentException);
    } 
  }
}
