package org.bouncycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.bouncycastle.util.encoders.Base64;

public class PemReader extends BufferedReader {
  private static final String BEGIN = "-----BEGIN ";
  
  private static final String END = "-----END ";
  
  public PemReader(Reader paramReader) {
    super(paramReader);
  }
  
  public PemObject readPemObject() throws IOException {
    String str;
    for (str = readLine(); str != null && !str.startsWith("-----BEGIN "); str = readLine());
    if (str != null) {
      str = str.substring("-----BEGIN ".length());
      int i = str.indexOf('-');
      String str1 = str.substring(0, i);
      if (i > 0)
        return loadObject(str1); 
    } 
    return null;
  }
  
  private PemObject loadObject(String paramString) throws IOException {
    String str2 = "-----END " + paramString;
    StringBuffer stringBuffer = new StringBuffer();
    ArrayList<PemHeader> arrayList = new ArrayList();
    String str1;
    while ((str1 = readLine()) != null) {
      if (str1.indexOf(":") >= 0) {
        int i = str1.indexOf(':');
        String str3 = str1.substring(0, i);
        String str4 = str1.substring(i + 1).trim();
        arrayList.add(new PemHeader(str3, str4));
        continue;
      } 
      if (str1.indexOf(str2) != -1)
        break; 
      stringBuffer.append(str1.trim());
    } 
    if (str1 == null)
      throw new IOException(str2 + " not found"); 
    return new PemObject(paramString, arrayList, Base64.decode(stringBuffer.toString()));
  }
}
