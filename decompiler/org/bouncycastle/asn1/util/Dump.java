package org.bouncycastle.asn1.util;

import java.io.FileInputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;

public class Dump {
  public static void main(String[] paramArrayOfString) throws Exception {
    FileInputStream fileInputStream = new FileInputStream(paramArrayOfString[0]);
    ASN1InputStream aSN1InputStream = new ASN1InputStream(fileInputStream);
    ASN1Primitive aSN1Primitive = null;
    while ((aSN1Primitive = aSN1InputStream.readObject()) != null)
      System.out.println(ASN1Dump.dumpAsString(aSN1Primitive)); 
  }
}
