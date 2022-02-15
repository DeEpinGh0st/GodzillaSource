package org.bouncycastle.asn1;

import java.io.IOException;

public class BERTaggedObjectParser implements ASN1TaggedObjectParser {
  private boolean _constructed;
  
  private int _tagNumber;
  
  private ASN1StreamParser _parser;
  
  BERTaggedObjectParser(boolean paramBoolean, int paramInt, ASN1StreamParser paramASN1StreamParser) {
    this._constructed = paramBoolean;
    this._tagNumber = paramInt;
    this._parser = paramASN1StreamParser;
  }
  
  public boolean isConstructed() {
    return this._constructed;
  }
  
  public int getTagNo() {
    return this._tagNumber;
  }
  
  public ASN1Encodable getObjectParser(int paramInt, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      if (!this._constructed)
        throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)"); 
      return this._parser.readObject();
    } 
    return this._parser.readImplicit(this._constructed, paramInt);
  }
  
  public ASN1Primitive getLoadedObject() throws IOException {
    return this._parser.readTaggedObject(this._constructed, this._tagNumber);
  }
  
  public ASN1Primitive toASN1Primitive() {
    try {
      return getLoadedObject();
    } catch (IOException iOException) {
      throw new ASN1ParsingException(iOException.getMessage());
    } 
  }
}
