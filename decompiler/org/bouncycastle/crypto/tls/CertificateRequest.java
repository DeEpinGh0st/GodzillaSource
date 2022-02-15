package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;

public class CertificateRequest {
  protected short[] certificateTypes;
  
  protected Vector supportedSignatureAlgorithms;
  
  protected Vector certificateAuthorities;
  
  public CertificateRequest(short[] paramArrayOfshort, Vector paramVector1, Vector paramVector2) {
    this.certificateTypes = paramArrayOfshort;
    this.supportedSignatureAlgorithms = paramVector1;
    this.certificateAuthorities = paramVector2;
  }
  
  public short[] getCertificateTypes() {
    return this.certificateTypes;
  }
  
  public Vector getSupportedSignatureAlgorithms() {
    return this.supportedSignatureAlgorithms;
  }
  
  public Vector getCertificateAuthorities() {
    return this.certificateAuthorities;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    if (this.certificateTypes == null || this.certificateTypes.length == 0) {
      TlsUtils.writeUint8(0, paramOutputStream);
    } else {
      TlsUtils.writeUint8ArrayWithUint8Length(this.certificateTypes, paramOutputStream);
    } 
    if (this.supportedSignatureAlgorithms != null)
      TlsUtils.encodeSupportedSignatureAlgorithms(this.supportedSignatureAlgorithms, false, paramOutputStream); 
    if (this.certificateAuthorities == null || this.certificateAuthorities.isEmpty()) {
      TlsUtils.writeUint16(0, paramOutputStream);
    } else {
      Vector<byte[]> vector = new Vector(this.certificateAuthorities.size());
      int i = 0;
      byte b;
      for (b = 0; b < this.certificateAuthorities.size(); b++) {
        X500Name x500Name = this.certificateAuthorities.elementAt(b);
        byte[] arrayOfByte = x500Name.getEncoded("DER");
        vector.addElement(arrayOfByte);
        i += arrayOfByte.length + 2;
      } 
      TlsUtils.checkUint16(i);
      TlsUtils.writeUint16(i, paramOutputStream);
      for (b = 0; b < vector.size(); b++) {
        byte[] arrayOfByte = vector.elementAt(b);
        TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
      } 
    } 
  }
  
  public static CertificateRequest parse(TlsContext paramTlsContext, InputStream paramInputStream) throws IOException {
    short s = TlsUtils.readUint8(paramInputStream);
    short[] arrayOfShort = new short[s];
    for (byte b = 0; b < s; b++)
      arrayOfShort[b] = TlsUtils.readUint8(paramInputStream); 
    Vector vector = null;
    if (TlsUtils.isTLSv12(paramTlsContext))
      vector = TlsUtils.parseSupportedSignatureAlgorithms(false, paramInputStream); 
    Vector<X500Name> vector1 = new Vector();
    byte[] arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    while (byteArrayInputStream.available() > 0) {
      byte[] arrayOfByte1 = TlsUtils.readOpaque16(byteArrayInputStream);
      ASN1Primitive aSN1Primitive = TlsUtils.readDERObject(arrayOfByte1);
      vector1.addElement(X500Name.getInstance(aSN1Primitive));
    } 
    return new CertificateRequest(arrayOfShort, vector, vector1);
  }
}
