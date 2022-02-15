package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

public class PEMUtil {
  private final String _header1;
  
  private final String _header2;
  
  private final String _footer1;
  
  private final String _footer2;
  
  PEMUtil(String paramString) {
    this._header1 = "-----BEGIN " + paramString + "-----";
    this._header2 = "-----BEGIN X509 " + paramString + "-----";
    this._footer1 = "-----END " + paramString + "-----";
    this._footer2 = "-----END X509 " + paramString + "-----";
  }
  
  private String readLine(InputStream paramInputStream) throws IOException {
    int i;
    StringBuffer stringBuffer = new StringBuffer();
    while (true) {
      if ((i = paramInputStream.read()) != 13 && i != 10 && i >= 0) {
        if (i == 13)
          continue; 
        stringBuffer.append((char)i);
        continue;
      } 
      if (i < 0 || stringBuffer.length() != 0)
        break; 
    } 
    return (i < 0) ? null : stringBuffer.toString();
  }
  
  ASN1Sequence readPEMObject(InputStream paramInputStream) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    String str;
    do {
    
    } while ((str = readLine(paramInputStream)) != null && !str.startsWith(this._header1) && !str.startsWith(this._header2));
    while ((str = readLine(paramInputStream)) != null && !str.startsWith(this._footer1) && !str.startsWith(this._footer2))
      stringBuffer.append(str); 
    if (stringBuffer.length() != 0) {
      ASN1Primitive aSN1Primitive = (new ASN1InputStream(Base64.decode(stringBuffer.toString()))).readObject();
      if (!(aSN1Primitive instanceof ASN1Sequence))
        throw new IOException("malformed PEM data encountered"); 
      return (ASN1Sequence)aSN1Primitive;
    } 
    return null;
  }
}
