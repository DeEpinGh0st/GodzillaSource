package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ocsp.OCSPResponse;

public class CertificateStatus {
  protected short statusType;
  
  protected Object response;
  
  public CertificateStatus(short paramShort, Object paramObject) {
    if (!isCorrectType(paramShort, paramObject))
      throw new IllegalArgumentException("'response' is not an instance of the correct type"); 
    this.statusType = paramShort;
    this.response = paramObject;
  }
  
  public short getStatusType() {
    return this.statusType;
  }
  
  public Object getResponse() {
    return this.response;
  }
  
  public OCSPResponse getOCSPResponse() {
    if (!isCorrectType((short)1, this.response))
      throw new IllegalStateException("'response' is not an OCSPResponse"); 
    return (OCSPResponse)this.response;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte;
    TlsUtils.writeUint8(this.statusType, paramOutputStream);
    switch (this.statusType) {
      case 1:
        arrayOfByte = ((OCSPResponse)this.response).getEncoded("DER");
        TlsUtils.writeOpaque24(arrayOfByte, paramOutputStream);
        return;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public static CertificateStatus parse(InputStream paramInputStream) throws IOException {
    OCSPResponse oCSPResponse;
    byte[] arrayOfByte;
    short s = TlsUtils.readUint8(paramInputStream);
    switch (s) {
      case 1:
        arrayOfByte = TlsUtils.readOpaque24(paramInputStream);
        oCSPResponse = OCSPResponse.getInstance(TlsUtils.readDERObject(arrayOfByte));
        return new CertificateStatus(s, oCSPResponse);
    } 
    throw new TlsFatalAlert((short)50);
  }
  
  protected static boolean isCorrectType(short paramShort, Object paramObject) {
    switch (paramShort) {
      case 1:
        return paramObject instanceof OCSPResponse;
    } 
    throw new IllegalArgumentException("'statusType' is an unsupported CertificateStatusType");
  }
}
