package org.bouncycastle.x509.extension;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.util.Integers;

public class X509ExtensionUtil {
  public static ASN1Primitive fromExtensionValue(byte[] paramArrayOfbyte) throws IOException {
    ASN1OctetString aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(paramArrayOfbyte);
    return ASN1Primitive.fromByteArray(aSN1OctetString.getOctets());
  }
  
  public static Collection getIssuerAlternativeNames(X509Certificate paramX509Certificate) throws CertificateParsingException {
    byte[] arrayOfByte = paramX509Certificate.getExtensionValue(X509Extension.issuerAlternativeName.getId());
    return getAlternativeNames(arrayOfByte);
  }
  
  public static Collection getSubjectAlternativeNames(X509Certificate paramX509Certificate) throws CertificateParsingException {
    byte[] arrayOfByte = paramX509Certificate.getExtensionValue(X509Extension.subjectAlternativeName.getId());
    return getAlternativeNames(arrayOfByte);
  }
  
  private static Collection getAlternativeNames(byte[] paramArrayOfbyte) throws CertificateParsingException {
    if (paramArrayOfbyte == null)
      return Collections.EMPTY_LIST; 
    try {
      ArrayList<ArrayList<Integer>> arrayList = new ArrayList();
      Enumeration enumeration = DERSequence.getInstance(fromExtensionValue(paramArrayOfbyte)).getObjects();
      while (enumeration.hasMoreElements()) {
        GeneralName generalName = GeneralName.getInstance(enumeration.nextElement());
        ArrayList<Integer> arrayList1 = new ArrayList();
        arrayList1.add(Integers.valueOf(generalName.getTagNo()));
        switch (generalName.getTagNo()) {
          case 0:
          case 3:
          case 5:
            arrayList1.add(generalName.getName().toASN1Primitive());
            break;
          case 4:
            arrayList1.add(X500Name.getInstance(generalName.getName()).toString());
            break;
          case 1:
          case 2:
          case 6:
            arrayList1.add(((ASN1String)generalName.getName()).getString());
            break;
          case 8:
            arrayList1.add(ASN1ObjectIdentifier.getInstance(generalName.getName()).getId());
            break;
          case 7:
            arrayList1.add(DEROctetString.getInstance(generalName.getName()).getOctets());
            break;
          default:
            throw new IOException("Bad tag number: " + generalName.getTagNo());
        } 
        arrayList.add(arrayList1);
      } 
      return Collections.unmodifiableCollection(arrayList);
    } catch (Exception exception) {
      throw new CertificateParsingException(exception.getMessage());
    } 
  }
}
