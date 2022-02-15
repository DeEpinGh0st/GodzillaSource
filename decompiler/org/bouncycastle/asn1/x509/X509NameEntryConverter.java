package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.util.Strings;

public abstract class X509NameEntryConverter {
  protected ASN1Primitive convertHexEncoded(String paramString, int paramInt) throws IOException {
    paramString = Strings.toLowerCase(paramString);
    byte[] arrayOfByte = new byte[(paramString.length() - paramInt) / 2];
    for (byte b = 0; b != arrayOfByte.length; b++) {
      char c1 = paramString.charAt(b * 2 + paramInt);
      char c2 = paramString.charAt(b * 2 + paramInt + 1);
      if (c1 < 'a') {
        arrayOfByte[b] = (byte)(c1 - 48 << 4);
      } else {
        arrayOfByte[b] = (byte)(c1 - 97 + 10 << 4);
      } 
      if (c2 < 'a') {
        arrayOfByte[b] = (byte)(arrayOfByte[b] | (byte)(c2 - 48));
      } else {
        arrayOfByte[b] = (byte)(arrayOfByte[b] | (byte)(c2 - 97 + 10));
      } 
    } 
    ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte);
    return aSN1InputStream.readObject();
  }
  
  protected boolean canBePrintable(String paramString) {
    return DERPrintableString.isPrintableString(paramString);
  }
  
  public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString);
}
