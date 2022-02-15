package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

class PEMUtil {
  private final String _header1;
  
  private final String _header2;
  
  private final String _header3;
  
  private final String _footer1;
  
  private final String _footer2;
  
  private final String _footer3;
  
  PEMUtil(String paramString) {
    this._header1 = "-----BEGIN " + paramString + "-----";
    this._header2 = "-----BEGIN X509 " + paramString + "-----";
    this._header3 = "-----BEGIN PKCS7-----";
    this._footer1 = "-----END " + paramString + "-----";
    this._footer2 = "-----END X509 " + paramString + "-----";
    this._footer3 = "-----END PKCS7-----";
  }
  
  private String readLine(InputStream paramInputStream) throws IOException {
    int i;
    StringBuffer stringBuffer = new StringBuffer();
    while (true) {
      if ((i = paramInputStream.read()) != 13 && i != 10 && i >= 0) {
        stringBuffer.append((char)i);
        continue;
      } 
      if (i < 0 || stringBuffer.length() != 0)
        break; 
    } 
    if (i < 0)
      return null; 
    if (i == 13) {
      paramInputStream.mark(1);
      if ((i = paramInputStream.read()) == 10)
        paramInputStream.mark(1); 
      if (i > 0)
        paramInputStream.reset(); 
    } 
    return stringBuffer.toString();
  }
  
  ASN1Sequence readPEMObject(InputStream paramInputStream) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    String str;
    do {
    
    } while ((str = readLine(paramInputStream)) != null && !str.startsWith(this._header1) && !str.startsWith(this._header2) && !str.startsWith(this._header3));
    while ((str = readLine(paramInputStream)) != null && !str.startsWith(this._footer1) && !str.startsWith(this._footer2) && !str.startsWith(this._footer3))
      stringBuffer.append(str); 
    if (stringBuffer.length() != 0)
      try {
        return ASN1Sequence.getInstance(Base64.decode(stringBuffer.toString()));
      } catch (Exception exception) {
        throw new IOException("malformed PEM data encountered");
      }  
    return null;
  }
}
