package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.Strings;

public class URLAndHash {
  protected String url;
  
  protected byte[] sha1Hash;
  
  public URLAndHash(String paramString, byte[] paramArrayOfbyte) {
    if (paramString == null || paramString.length() < 1 || paramString.length() >= 65536)
      throw new IllegalArgumentException("'url' must have length from 1 to (2^16 - 1)"); 
    if (paramArrayOfbyte != null && paramArrayOfbyte.length != 20)
      throw new IllegalArgumentException("'sha1Hash' must have length == 20, if present"); 
    this.url = paramString;
    this.sha1Hash = paramArrayOfbyte;
  }
  
  public String getURL() {
    return this.url;
  }
  
  public byte[] getSHA1Hash() {
    return this.sha1Hash;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = Strings.toByteArray(this.url);
    TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
    if (this.sha1Hash == null) {
      TlsUtils.writeUint8(0, paramOutputStream);
    } else {
      TlsUtils.writeUint8(1, paramOutputStream);
      paramOutputStream.write(this.sha1Hash);
    } 
  }
  
  public static URLAndHash parse(TlsContext paramTlsContext, InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte1 = TlsUtils.readOpaque16(paramInputStream);
    if (arrayOfByte1.length < 1)
      throw new TlsFatalAlert((short)47); 
    String str = Strings.fromByteArray(arrayOfByte1);
    byte[] arrayOfByte2 = null;
    short s = TlsUtils.readUint8(paramInputStream);
    switch (s) {
      case 0:
        if (TlsUtils.isTLSv12(paramTlsContext))
          throw new TlsFatalAlert((short)47); 
        return new URLAndHash(str, arrayOfByte2);
      case 1:
        arrayOfByte2 = TlsUtils.readFully(20, paramInputStream);
        return new URLAndHash(str, arrayOfByte2);
    } 
    throw new TlsFatalAlert((short)47);
  }
}
