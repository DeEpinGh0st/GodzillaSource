package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Primitive;

public class Certificate {
  public static final Certificate EMPTY_CHAIN = new Certificate(new org.bouncycastle.asn1.x509.Certificate[0]);
  
  protected org.bouncycastle.asn1.x509.Certificate[] certificateList;
  
  public Certificate(org.bouncycastle.asn1.x509.Certificate[] paramArrayOfCertificate) {
    if (paramArrayOfCertificate == null)
      throw new IllegalArgumentException("'certificateList' cannot be null"); 
    this.certificateList = paramArrayOfCertificate;
  }
  
  public org.bouncycastle.asn1.x509.Certificate[] getCertificateList() {
    return cloneCertificateList();
  }
  
  public org.bouncycastle.asn1.x509.Certificate getCertificateAt(int paramInt) {
    return this.certificateList[paramInt];
  }
  
  public int getLength() {
    return this.certificateList.length;
  }
  
  public boolean isEmpty() {
    return (this.certificateList.length == 0);
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    Vector<byte[]> vector = new Vector(this.certificateList.length);
    int i = 0;
    byte b;
    for (b = 0; b < this.certificateList.length; b++) {
      byte[] arrayOfByte = this.certificateList[b].getEncoded("DER");
      vector.addElement(arrayOfByte);
      i += arrayOfByte.length + 3;
    } 
    TlsUtils.checkUint24(i);
    TlsUtils.writeUint24(i, paramOutputStream);
    for (b = 0; b < vector.size(); b++) {
      byte[] arrayOfByte = vector.elementAt(b);
      TlsUtils.writeOpaque24(arrayOfByte, paramOutputStream);
    } 
  }
  
  public static Certificate parse(InputStream paramInputStream) throws IOException {
    int i = TlsUtils.readUint24(paramInputStream);
    if (i == 0)
      return EMPTY_CHAIN; 
    byte[] arrayOfByte = TlsUtils.readFully(i, paramInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    Vector<org.bouncycastle.asn1.x509.Certificate> vector = new Vector();
    while (byteArrayInputStream.available() > 0) {
      byte[] arrayOfByte1 = TlsUtils.readOpaque24(byteArrayInputStream);
      ASN1Primitive aSN1Primitive = TlsUtils.readASN1Object(arrayOfByte1);
      vector.addElement(org.bouncycastle.asn1.x509.Certificate.getInstance(aSN1Primitive));
    } 
    org.bouncycastle.asn1.x509.Certificate[] arrayOfCertificate = new org.bouncycastle.asn1.x509.Certificate[vector.size()];
    for (byte b = 0; b < vector.size(); b++)
      arrayOfCertificate[b] = vector.elementAt(b); 
    return new Certificate(arrayOfCertificate);
  }
  
  protected org.bouncycastle.asn1.x509.Certificate[] cloneCertificateList() {
    org.bouncycastle.asn1.x509.Certificate[] arrayOfCertificate = new org.bouncycastle.asn1.x509.Certificate[this.certificateList.length];
    System.arraycopy(this.certificateList, 0, arrayOfCertificate, 0, arrayOfCertificate.length);
    return arrayOfCertificate;
  }
}
