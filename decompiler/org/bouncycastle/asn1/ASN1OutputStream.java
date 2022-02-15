package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class ASN1OutputStream {
  private OutputStream os;
  
  public ASN1OutputStream(OutputStream paramOutputStream) {
    this.os = paramOutputStream;
  }
  
  void writeLength(int paramInt) throws IOException {
    if (paramInt > 127) {
      byte b = 1;
      int i = paramInt;
      while ((i >>>= 8) != 0)
        b++; 
      write((byte)(b | 0x80));
      for (int j = (b - 1) * 8; j >= 0; j -= 8)
        write((byte)(paramInt >> j)); 
    } else {
      write((byte)paramInt);
    } 
  }
  
  void write(int paramInt) throws IOException {
    this.os.write(paramInt);
  }
  
  void write(byte[] paramArrayOfbyte) throws IOException {
    this.os.write(paramArrayOfbyte);
  }
  
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.os.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  void writeEncoded(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    write(paramInt);
    writeLength(paramArrayOfbyte.length);
    write(paramArrayOfbyte);
  }
  
  void writeTag(int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 31) {
      write(paramInt1 | paramInt2);
    } else {
      write(paramInt1 | 0x1F);
      if (paramInt2 < 128) {
        write(paramInt2);
      } else {
        byte[] arrayOfByte = new byte[5];
        int i = arrayOfByte.length;
        arrayOfByte[--i] = (byte)(paramInt2 & 0x7F);
        while (true) {
          paramInt2 >>= 7;
          arrayOfByte[--i] = (byte)(paramInt2 & 0x7F | 0x80);
          if (paramInt2 <= 127) {
            write(arrayOfByte, i, arrayOfByte.length - i);
            return;
          } 
        } 
      } 
    } 
  }
  
  void writeEncoded(int paramInt1, int paramInt2, byte[] paramArrayOfbyte) throws IOException {
    writeTag(paramInt1, paramInt2);
    writeLength(paramArrayOfbyte.length);
    write(paramArrayOfbyte);
  }
  
  protected void writeNull() throws IOException {
    this.os.write(5);
    this.os.write(0);
  }
  
  public void writeObject(ASN1Encodable paramASN1Encodable) throws IOException {
    if (paramASN1Encodable != null) {
      paramASN1Encodable.toASN1Primitive().encode(this);
    } else {
      throw new IOException("null object detected");
    } 
  }
  
  void writeImplicitObject(ASN1Primitive paramASN1Primitive) throws IOException {
    if (paramASN1Primitive != null) {
      paramASN1Primitive.encode(new ImplicitOutputStream(this.os));
    } else {
      throw new IOException("null object detected");
    } 
  }
  
  public void close() throws IOException {
    this.os.close();
  }
  
  public void flush() throws IOException {
    this.os.flush();
  }
  
  ASN1OutputStream getDERSubStream() {
    return new DEROutputStream(this.os);
  }
  
  ASN1OutputStream getDLSubStream() {
    return new DLOutputStream(this.os);
  }
  
  private class ImplicitOutputStream extends ASN1OutputStream {
    private boolean first = true;
    
    public ImplicitOutputStream(OutputStream param1OutputStream) {
      super(param1OutputStream);
    }
    
    public void write(int param1Int) throws IOException {
      if (this.first) {
        this.first = false;
      } else {
        super.write(param1Int);
      } 
    }
  }
}
