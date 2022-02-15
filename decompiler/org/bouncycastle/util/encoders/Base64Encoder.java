package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class Base64Encoder implements Encoder {
  protected final byte[] encodingTable = new byte[] { 
      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
      121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
      56, 57, 43, 47 };
  
  protected byte padding = 61;
  
  protected final byte[] decodingTable = new byte[128];
  
  protected void initialiseDecodingTable() {
    byte b;
    for (b = 0; b < this.decodingTable.length; b++)
      this.decodingTable[b] = -1; 
    for (b = 0; b < this.encodingTable.length; b++)
      this.decodingTable[this.encodingTable[b]] = (byte)b; 
  }
  
  public Base64Encoder() {
    initialiseDecodingTable();
  }
  
  public int encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
    int m;
    int n;
    int i1;
    int i2;
    int i = paramInt2 % 3;
    int j = paramInt2 - i;
    int k;
    for (k = paramInt1; k < paramInt1 + j; k += 3) {
      int i3 = paramArrayOfbyte[k] & 0xFF;
      int i4 = paramArrayOfbyte[k + 1] & 0xFF;
      int i5 = paramArrayOfbyte[k + 2] & 0xFF;
      paramOutputStream.write(this.encodingTable[i3 >>> 2 & 0x3F]);
      paramOutputStream.write(this.encodingTable[(i3 << 4 | i4 >>> 4) & 0x3F]);
      paramOutputStream.write(this.encodingTable[(i4 << 2 | i5 >>> 6) & 0x3F]);
      paramOutputStream.write(this.encodingTable[i5 & 0x3F]);
    } 
    switch (i) {
      case 1:
        i1 = paramArrayOfbyte[paramInt1 + j] & 0xFF;
        k = i1 >>> 2 & 0x3F;
        m = i1 << 4 & 0x3F;
        paramOutputStream.write(this.encodingTable[k]);
        paramOutputStream.write(this.encodingTable[m]);
        paramOutputStream.write(this.padding);
        paramOutputStream.write(this.padding);
        break;
      case 2:
        i1 = paramArrayOfbyte[paramInt1 + j] & 0xFF;
        i2 = paramArrayOfbyte[paramInt1 + j + 1] & 0xFF;
        k = i1 >>> 2 & 0x3F;
        m = (i1 << 4 | i2 >>> 4) & 0x3F;
        n = i2 << 2 & 0x3F;
        paramOutputStream.write(this.encodingTable[k]);
        paramOutputStream.write(this.encodingTable[m]);
        paramOutputStream.write(this.encodingTable[n]);
        paramOutputStream.write(this.padding);
        break;
    } 
    return j / 3 * 4 + ((i == 0) ? 0 : 4);
  }
  
  private boolean ignore(char paramChar) {
    return (paramChar == '\n' || paramChar == '\r' || paramChar == '\t' || paramChar == ' ');
  }
  
  public int decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    int j;
    for (j = paramInt1 + paramInt2; j > paramInt1 && ignore((char)paramArrayOfbyte[j - 1]); j--);
    int k = paramInt1;
    int m = j - 4;
    for (k = nextI(paramArrayOfbyte, k, m); k < m; k = nextI(paramArrayOfbyte, k, m)) {
      byte b1 = this.decodingTable[paramArrayOfbyte[k++]];
      k = nextI(paramArrayOfbyte, k, m);
      byte b2 = this.decodingTable[paramArrayOfbyte[k++]];
      k = nextI(paramArrayOfbyte, k, m);
      byte b3 = this.decodingTable[paramArrayOfbyte[k++]];
      k = nextI(paramArrayOfbyte, k, m);
      byte b4 = this.decodingTable[paramArrayOfbyte[k++]];
      if ((b1 | b2 | b3 | b4) < 0)
        throw new IOException("invalid characters encountered in base64 data"); 
      paramOutputStream.write(b1 << 2 | b2 >> 4);
      paramOutputStream.write(b2 << 4 | b3 >> 2);
      paramOutputStream.write(b3 << 6 | b4);
      i += true;
    } 
    i += decodeLastBlock(paramOutputStream, (char)paramArrayOfbyte[j - 4], (char)paramArrayOfbyte[j - 3], (char)paramArrayOfbyte[j - 2], (char)paramArrayOfbyte[j - 1]);
    return i;
  }
  
  private int nextI(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2 && ignore((char)paramArrayOfbyte[paramInt1]))
      paramInt1++; 
    return paramInt1;
  }
  
  public int decode(String paramString, OutputStream paramOutputStream) throws IOException {
    int i = 0;
    int j;
    for (j = paramString.length(); j > 0 && ignore(paramString.charAt(j - 1)); j--);
    int k = 0;
    int m = j - 4;
    for (k = nextI(paramString, k, m); k < m; k = nextI(paramString, k, m)) {
      byte b1 = this.decodingTable[paramString.charAt(k++)];
      k = nextI(paramString, k, m);
      byte b2 = this.decodingTable[paramString.charAt(k++)];
      k = nextI(paramString, k, m);
      byte b3 = this.decodingTable[paramString.charAt(k++)];
      k = nextI(paramString, k, m);
      byte b4 = this.decodingTable[paramString.charAt(k++)];
      if ((b1 | b2 | b3 | b4) < 0)
        throw new IOException("invalid characters encountered in base64 data"); 
      paramOutputStream.write(b1 << 2 | b2 >> 4);
      paramOutputStream.write(b2 << 4 | b3 >> 2);
      paramOutputStream.write(b3 << 6 | b4);
      i += true;
    } 
    i += decodeLastBlock(paramOutputStream, paramString.charAt(j - 4), paramString.charAt(j - 3), paramString.charAt(j - 2), paramString.charAt(j - 1));
    return i;
  }
  
  private int decodeLastBlock(OutputStream paramOutputStream, char paramChar1, char paramChar2, char paramChar3, char paramChar4) throws IOException {
    if (paramChar3 == this.padding) {
      if (paramChar4 != this.padding)
        throw new IOException("invalid characters encountered at end of base64 data"); 
      byte b5 = this.decodingTable[paramChar1];
      byte b6 = this.decodingTable[paramChar2];
      if ((b5 | b6) < 0)
        throw new IOException("invalid characters encountered at end of base64 data"); 
      paramOutputStream.write(b5 << 2 | b6 >> 4);
      return 1;
    } 
    if (paramChar4 == this.padding) {
      byte b5 = this.decodingTable[paramChar1];
      byte b6 = this.decodingTable[paramChar2];
      byte b7 = this.decodingTable[paramChar3];
      if ((b5 | b6 | b7) < 0)
        throw new IOException("invalid characters encountered at end of base64 data"); 
      paramOutputStream.write(b5 << 2 | b6 >> 4);
      paramOutputStream.write(b6 << 4 | b7 >> 2);
      return 2;
    } 
    byte b1 = this.decodingTable[paramChar1];
    byte b2 = this.decodingTable[paramChar2];
    byte b3 = this.decodingTable[paramChar3];
    byte b4 = this.decodingTable[paramChar4];
    if ((b1 | b2 | b3 | b4) < 0)
      throw new IOException("invalid characters encountered at end of base64 data"); 
    paramOutputStream.write(b1 << 2 | b2 >> 4);
    paramOutputStream.write(b2 << 4 | b3 >> 2);
    paramOutputStream.write(b3 << 6 | b4);
    return 3;
  }
  
  private int nextI(String paramString, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2 && ignore(paramString.charAt(paramInt1)))
      paramInt1++; 
    return paramInt1;
  }
}
