package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class IndefiniteLengthInputStream extends LimitedInputStream {
  private int _b1;
  
  private int _b2;
  
  private boolean _eofReached = false;
  
  private boolean _eofOn00 = true;
  
  IndefiniteLengthInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    super(paramInputStream, paramInt);
    this._b1 = paramInputStream.read();
    this._b2 = paramInputStream.read();
    if (this._b2 < 0)
      throw new EOFException(); 
    checkForEof();
  }
  
  void setEofOn00(boolean paramBoolean) {
    this._eofOn00 = paramBoolean;
    checkForEof();
  }
  
  private boolean checkForEof() {
    if (!this._eofReached && this._eofOn00 && this._b1 == 0 && this._b2 == 0) {
      this._eofReached = true;
      setParentEofDetect(true);
    } 
    return this._eofReached;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this._eofOn00 || paramInt2 < 3)
      return super.read(paramArrayOfbyte, paramInt1, paramInt2); 
    if (this._eofReached)
      return -1; 
    int i = this._in.read(paramArrayOfbyte, paramInt1 + 2, paramInt2 - 2);
    if (i < 0)
      throw new EOFException(); 
    paramArrayOfbyte[paramInt1] = (byte)this._b1;
    paramArrayOfbyte[paramInt1 + 1] = (byte)this._b2;
    this._b1 = this._in.read();
    this._b2 = this._in.read();
    if (this._b2 < 0)
      throw new EOFException(); 
    return i + 2;
  }
  
  public int read() throws IOException {
    if (checkForEof())
      return -1; 
    int i = this._in.read();
    if (i < 0)
      throw new EOFException(); 
    int j = this._b1;
    this._b1 = this._b2;
    this._b2 = i;
    return j;
  }
}
