package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class ServerNameList {
  protected Vector serverNameList;
  
  public ServerNameList(Vector paramVector) {
    if (paramVector == null)
      throw new IllegalArgumentException("'serverNameList' must not be null"); 
    this.serverNameList = paramVector;
  }
  
  public Vector getServerNameList() {
    return this.serverNameList;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    short[] arrayOfShort = new short[0];
    for (byte b = 0; b < this.serverNameList.size(); b++) {
      ServerName serverName = this.serverNameList.elementAt(b);
      arrayOfShort = checkNameType(arrayOfShort, serverName.getNameType());
      if (arrayOfShort == null)
        throw new TlsFatalAlert((short)80); 
      serverName.encode(byteArrayOutputStream);
    } 
    TlsUtils.checkUint16(byteArrayOutputStream.size());
    TlsUtils.writeUint16(byteArrayOutputStream.size(), paramOutputStream);
    Streams.writeBufTo(byteArrayOutputStream, paramOutputStream);
  }
  
  public static ServerNameList parse(InputStream paramInputStream) throws IOException {
    int i = TlsUtils.readUint16(paramInputStream);
    if (i < 1)
      throw new TlsFatalAlert((short)50); 
    byte[] arrayOfByte = TlsUtils.readFully(i, paramInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    short[] arrayOfShort = new short[0];
    Vector<ServerName> vector = new Vector();
    while (byteArrayInputStream.available() > 0) {
      ServerName serverName = ServerName.parse(byteArrayInputStream);
      arrayOfShort = checkNameType(arrayOfShort, serverName.getNameType());
      if (arrayOfShort == null)
        throw new TlsFatalAlert((short)47); 
      vector.addElement(serverName);
    } 
    return new ServerNameList(vector);
  }
  
  private static short[] checkNameType(short[] paramArrayOfshort, short paramShort) {
    return (!NameType.isValid(paramShort) || Arrays.contains(paramArrayOfshort, paramShort)) ? null : Arrays.append(paramArrayOfshort, paramShort);
  }
}
