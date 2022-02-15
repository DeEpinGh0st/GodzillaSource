package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class DERGenerator extends ASN1Generator {
  private boolean _tagged = false;
  
  private boolean _isExplicit;
  
  private int _tagNo;
  
  protected DERGenerator(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public DERGenerator(OutputStream paramOutputStream, int paramInt, boolean paramBoolean) {
    super(paramOutputStream);
    this._tagged = true;
    this._isExplicit = paramBoolean;
    this._tagNo = paramInt;
  }
  
  private void writeLength(OutputStream paramOutputStream, int paramInt) throws IOException {
    if (paramInt > 127) {
      byte b = 1;
      int i = paramInt;
      while ((i >>>= 8) != 0)
        b++; 
      paramOutputStream.write((byte)(b | 0x80));
      for (int j = (b - 1) * 8; j >= 0; j -= 8)
        paramOutputStream.write((byte)(paramInt >> j)); 
    } else {
      paramOutputStream.write((byte)paramInt);
    } 
  }
  
  void writeDEREncoded(OutputStream paramOutputStream, int paramInt, byte[] paramArrayOfbyte) throws IOException {
    paramOutputStream.write(paramInt);
    writeLength(paramOutputStream, paramArrayOfbyte.length);
    paramOutputStream.write(paramArrayOfbyte);
  }
  
  void writeDEREncoded(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    if (this._tagged) {
      int i = this._tagNo | 0x80;
      if (this._isExplicit) {
        int j = this._tagNo | 0x20 | 0x80;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeDEREncoded(byteArrayOutputStream, paramInt, paramArrayOfbyte);
        writeDEREncoded(this._out, j, byteArrayOutputStream.toByteArray());
      } else if ((paramInt & 0x20) != 0) {
        writeDEREncoded(this._out, i | 0x20, paramArrayOfbyte);
      } else {
        writeDEREncoded(this._out, i, paramArrayOfbyte);
      } 
    } else {
      writeDEREncoded(this._out, paramInt, paramArrayOfbyte);
    } 
  }
}
