package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BEROctetStringGenerator extends BERGenerator {
  public BEROctetStringGenerator(OutputStream paramOutputStream) throws IOException {
    super(paramOutputStream);
    writeBERHeader(36);
  }
  
  public BEROctetStringGenerator(OutputStream paramOutputStream, int paramInt, boolean paramBoolean) throws IOException {
    super(paramOutputStream, paramInt, paramBoolean);
    writeBERHeader(36);
  }
  
  public OutputStream getOctetOutputStream() {
    return getOctetOutputStream(new byte[1000]);
  }
  
  public OutputStream getOctetOutputStream(byte[] paramArrayOfbyte) {
    return new BufferedBEROctetStream(paramArrayOfbyte);
  }
  
  private class BufferedBEROctetStream extends OutputStream {
    private byte[] _buf;
    
    private int _off;
    
    private DEROutputStream _derOut;
    
    BufferedBEROctetStream(byte[] param1ArrayOfbyte) {
      this._buf = param1ArrayOfbyte;
      this._off = 0;
      this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
    }
    
    public void write(int param1Int) throws IOException {
      this._buf[this._off++] = (byte)param1Int;
      if (this._off == this._buf.length) {
        DEROctetString.encode(this._derOut, this._buf);
        this._off = 0;
      } 
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      while (param1Int2 > 0) {
        int i = Math.min(param1Int2, this._buf.length - this._off);
        System.arraycopy(param1ArrayOfbyte, param1Int1, this._buf, this._off, i);
        this._off += i;
        if (this._off < this._buf.length)
          break; 
        DEROctetString.encode(this._derOut, this._buf);
        this._off = 0;
        param1Int1 += i;
        param1Int2 -= i;
      } 
    }
    
    public void close() throws IOException {
      if (this._off != 0) {
        byte[] arrayOfByte = new byte[this._off];
        System.arraycopy(this._buf, 0, arrayOfByte, 0, this._off);
        DEROctetString.encode(this._derOut, arrayOfByte);
      } 
      BEROctetStringGenerator.this.writeBEREnd();
    }
  }
}
