package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.util.io.Streams;

public class OCSPStatusRequest {
  protected Vector responderIDList;
  
  protected Extensions requestExtensions;
  
  public OCSPStatusRequest(Vector paramVector, Extensions paramExtensions) {
    this.responderIDList = paramVector;
    this.requestExtensions = paramExtensions;
  }
  
  public Vector getResponderIDList() {
    return this.responderIDList;
  }
  
  public Extensions getRequestExtensions() {
    return this.requestExtensions;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    if (this.responderIDList == null || this.responderIDList.isEmpty()) {
      TlsUtils.writeUint16(0, paramOutputStream);
    } else {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      for (byte b = 0; b < this.responderIDList.size(); b++) {
        ResponderID responderID = this.responderIDList.elementAt(b);
        byte[] arrayOfByte = responderID.getEncoded("DER");
        TlsUtils.writeOpaque16(arrayOfByte, byteArrayOutputStream);
      } 
      TlsUtils.checkUint16(byteArrayOutputStream.size());
      TlsUtils.writeUint16(byteArrayOutputStream.size(), paramOutputStream);
      Streams.writeBufTo(byteArrayOutputStream, paramOutputStream);
    } 
    if (this.requestExtensions == null) {
      TlsUtils.writeUint16(0, paramOutputStream);
    } else {
      byte[] arrayOfByte = this.requestExtensions.getEncoded("DER");
      TlsUtils.checkUint16(arrayOfByte.length);
      TlsUtils.writeUint16(arrayOfByte.length, paramOutputStream);
      paramOutputStream.write(arrayOfByte);
    } 
  }
  
  public static OCSPStatusRequest parse(InputStream paramInputStream) throws IOException {
    Vector<ResponderID> vector = new Vector();
    int i = TlsUtils.readUint16(paramInputStream);
    if (i > 0) {
      byte[] arrayOfByte = TlsUtils.readFully(i, paramInputStream);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      do {
        byte[] arrayOfByte1 = TlsUtils.readOpaque16(byteArrayInputStream);
        ResponderID responderID = ResponderID.getInstance(TlsUtils.readDERObject(arrayOfByte1));
        vector.addElement(responderID);
      } while (byteArrayInputStream.available() > 0);
    } 
    Extensions extensions = null;
    int j = TlsUtils.readUint16(paramInputStream);
    if (j > 0) {
      byte[] arrayOfByte = TlsUtils.readFully(j, paramInputStream);
      extensions = Extensions.getInstance(TlsUtils.readDERObject(arrayOfByte));
    } 
    return new OCSPStatusRequest(vector, extensions);
  }
}
