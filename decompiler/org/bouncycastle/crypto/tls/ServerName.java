package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerName {
  protected short nameType;
  
  protected Object name;
  
  public ServerName(short paramShort, Object paramObject) {
    if (!isCorrectType(paramShort, paramObject))
      throw new IllegalArgumentException("'name' is not an instance of the correct type"); 
    this.nameType = paramShort;
    this.name = paramObject;
  }
  
  public short getNameType() {
    return this.nameType;
  }
  
  public Object getName() {
    return this.name;
  }
  
  public String getHostName() {
    if (!isCorrectType((short)0, this.name))
      throw new IllegalStateException("'name' is not a HostName string"); 
    return (String)this.name;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte;
    TlsUtils.writeUint8(this.nameType, paramOutputStream);
    switch (this.nameType) {
      case 0:
        arrayOfByte = ((String)this.name).getBytes("ASCII");
        if (arrayOfByte.length < 1)
          throw new TlsFatalAlert((short)80); 
        TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
        return;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static ServerName parse(InputStream paramInputStream) throws IOException {
    String str;
    byte[] arrayOfByte;
    short s = TlsUtils.readUint8(paramInputStream);
    switch (s) {
      case 0:
        arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
        if (arrayOfByte.length < 1)
          throw new TlsFatalAlert((short)50); 
        str = new String(arrayOfByte, "ASCII");
        return new ServerName(s, str);
    } 
    throw new TlsFatalAlert((short)50);
  }
  
  protected static boolean isCorrectType(short paramShort, Object paramObject) {
    switch (paramShort) {
      case 0:
        return paramObject instanceof String;
    } 
    throw new IllegalArgumentException("'nameType' is an unsupported NameType");
  }
}
