package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BERGenerator extends ASN1Generator {
  private boolean _tagged = false;
  
  private boolean _isExplicit;
  
  private int _tagNo;
  
  protected BERGenerator(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  protected BERGenerator(OutputStream paramOutputStream, int paramInt, boolean paramBoolean) {
    super(paramOutputStream);
    this._tagged = true;
    this._isExplicit = paramBoolean;
    this._tagNo = paramInt;
  }
  
  public OutputStream getRawOutputStream() {
    return this._out;
  }
  
  private void writeHdr(int paramInt) throws IOException {
    this._out.write(paramInt);
    this._out.write(128);
  }
  
  protected void writeBERHeader(int paramInt) throws IOException {
    if (this._tagged) {
      int i = this._tagNo | 0x80;
      if (this._isExplicit) {
        writeHdr(i | 0x20);
        writeHdr(paramInt);
      } else if ((paramInt & 0x20) != 0) {
        writeHdr(i | 0x20);
      } else {
        writeHdr(i);
      } 
    } else {
      writeHdr(paramInt);
    } 
  }
  
  protected void writeBEREnd() throws IOException {
    this._out.write(0);
    this._out.write(0);
    if (this._tagged && this._isExplicit) {
      this._out.write(0);
      this._out.write(0);
    } 
  }
}
