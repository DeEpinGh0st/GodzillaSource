package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class HexEncoder implements Encoder {
  protected final byte[] encodingTable = new byte[] { 
      48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
      97, 98, 99, 100, 101, 102 };
  
  protected final byte[] decodingTable = new byte[128];
  
  protected void initialiseDecodingTable() {
    byte b;
    for (b = 0; b < this.decodingTable.length; b++)
      this.decodingTable[b] = -1; 
    for (b = 0; b < this.encodingTable.length; b++)
      this.decodingTable[this.encodingTable[b]] = (byte)b; 
    this.decodingTable[65] = this.decodingTable[97];
    this.decodingTable[66] = this.decodingTable[98];
    this.decodingTable[67] = this.decodingTable[99];
    this.decodingTable[68] = this.decodingTable[100];
    this.decodingTable[69] = this.decodingTable[101];
    this.decodingTable[70] = this.decodingTable[102];
  }
  
  public HexEncoder() {
    initialiseDecodingTable();
  }
  
  public int encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
      int j = paramArrayOfbyte[i] & 0xFF;
      paramOutputStream.write(this.encodingTable[j >>> 4]);
      paramOutputStream.write(this.encodingTable[j & 0xF]);
    } 
    return paramInt2 * 2;
  }
  
  private static boolean ignore(char paramChar) {
    return (paramChar == '\n' || paramChar == '\r' || paramChar == '\t' || paramChar == ' ');
  }
  
  public int decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
    byte b = 0;
    int i;
    for (i = paramInt1 + paramInt2; i > paramInt1 && ignore((char)paramArrayOfbyte[i - 1]); i--);
    int j = paramInt1;
    while (j < i) {
      while (j < i && ignore((char)paramArrayOfbyte[j]))
        j++; 
      byte b1 = this.decodingTable[paramArrayOfbyte[j++]];
      while (j < i && ignore((char)paramArrayOfbyte[j]))
        j++; 
      byte b2 = this.decodingTable[paramArrayOfbyte[j++]];
      if ((b1 | b2) < 0)
        throw new IOException("invalid characters encountered in Hex data"); 
      paramOutputStream.write(b1 << 4 | b2);
      b++;
    } 
    return b;
  }
  
  public int decode(String paramString, OutputStream paramOutputStream) throws IOException {
    byte b1 = 0;
    int i;
    for (i = paramString.length(); i > 0 && ignore(paramString.charAt(i - 1)); i--);
    byte b2 = 0;
    while (b2 < i) {
      while (b2 < i && ignore(paramString.charAt(b2)))
        b2++; 
      byte b3 = this.decodingTable[paramString.charAt(b2++)];
      while (b2 < i && ignore(paramString.charAt(b2)))
        b2++; 
      byte b4 = this.decodingTable[paramString.charAt(b2++)];
      if ((b3 | b4) < 0)
        throw new IOException("invalid characters encountered in Hex string"); 
      paramOutputStream.write(b3 << 4 | b4);
      b1++;
    } 
    return b1;
  }
}
