package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class CertificateURL {
  protected short type;
  
  protected Vector urlAndHashList;
  
  public CertificateURL(short paramShort, Vector paramVector) {
    if (!CertChainType.isValid(paramShort))
      throw new IllegalArgumentException("'type' is not a valid CertChainType value"); 
    if (paramVector == null || paramVector.isEmpty())
      throw new IllegalArgumentException("'urlAndHashList' must have length > 0"); 
    this.type = paramShort;
    this.urlAndHashList = paramVector;
  }
  
  public short getType() {
    return this.type;
  }
  
  public Vector getURLAndHashList() {
    return this.urlAndHashList;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeUint8(this.type, paramOutputStream);
    ListBuffer16 listBuffer16 = new ListBuffer16();
    for (byte b = 0; b < this.urlAndHashList.size(); b++) {
      URLAndHash uRLAndHash = this.urlAndHashList.elementAt(b);
      uRLAndHash.encode(listBuffer16);
    } 
    listBuffer16.encodeTo(paramOutputStream);
  }
  
  public static CertificateURL parse(TlsContext paramTlsContext, InputStream paramInputStream) throws IOException {
    short s = TlsUtils.readUint8(paramInputStream);
    if (!CertChainType.isValid(s))
      throw new TlsFatalAlert((short)50); 
    int i = TlsUtils.readUint16(paramInputStream);
    if (i < 1)
      throw new TlsFatalAlert((short)50); 
    byte[] arrayOfByte = TlsUtils.readFully(i, paramInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    Vector<URLAndHash> vector = new Vector();
    while (byteArrayInputStream.available() > 0) {
      URLAndHash uRLAndHash = URLAndHash.parse(paramTlsContext, byteArrayInputStream);
      vector.addElement(uRLAndHash);
    } 
    return new CertificateURL(s, vector);
  }
  
  class ListBuffer16 extends ByteArrayOutputStream {
    ListBuffer16() throws IOException {
      TlsUtils.writeUint16(0, this);
    }
    
    void encodeTo(OutputStream param1OutputStream) throws IOException {
      int i = this.count - 2;
      TlsUtils.checkUint16(i);
      TlsUtils.writeUint16(i, this.buf, 0);
      param1OutputStream.write(this.buf, 0, this.count);
      this.buf = null;
    }
  }
}
