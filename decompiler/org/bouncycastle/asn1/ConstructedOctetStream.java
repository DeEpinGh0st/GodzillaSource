package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

class ConstructedOctetStream extends InputStream {
  private final ASN1StreamParser _parser;
  
  private boolean _first = true;
  
  private InputStream _currentStream;
  
  ConstructedOctetStream(ASN1StreamParser paramASN1StreamParser) {
    this._parser = paramASN1StreamParser;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this._currentStream == null) {
      if (!this._first)
        return -1; 
      ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
      if (aSN1OctetStringParser == null)
        return -1; 
      this._first = false;
      this._currentStream = aSN1OctetStringParser.getOctetStream();
    } 
    int i = 0;
    while (true) {
      int j = this._currentStream.read(paramArrayOfbyte, paramInt1 + i, paramInt2 - i);
      if (j >= 0) {
        i += j;
        if (i == paramInt2)
          return i; 
        continue;
      } 
      ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
      if (aSN1OctetStringParser == null) {
        this._currentStream = null;
        return (i < 1) ? -1 : i;
      } 
      this._currentStream = aSN1OctetStringParser.getOctetStream();
    } 
  }
  
  public int read() throws IOException {
    if (this._currentStream == null) {
      if (!this._first)
        return -1; 
      ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
      if (aSN1OctetStringParser == null)
        return -1; 
      this._first = false;
      this._currentStream = aSN1OctetStringParser.getOctetStream();
    } 
    while (true) {
      int i = this._currentStream.read();
      if (i >= 0)
        return i; 
      ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser)this._parser.readObject();
      if (aSN1OctetStringParser == null) {
        this._currentStream = null;
        return -1;
      } 
      this._currentStream = aSN1OctetStringParser.getOctetStream();
    } 
  }
}
