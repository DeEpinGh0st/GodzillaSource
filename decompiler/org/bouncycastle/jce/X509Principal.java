package org.bouncycastle.jce;

import java.io.IOException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Name;

public class X509Principal extends X509Name implements Principal {
  private static ASN1Sequence readSequence(ASN1InputStream paramASN1InputStream) throws IOException {
    try {
      return ASN1Sequence.getInstance(paramASN1InputStream.readObject());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException("not an ASN.1 Sequence: " + illegalArgumentException);
    } 
  }
  
  public X509Principal(byte[] paramArrayOfbyte) throws IOException {
    super(readSequence(new ASN1InputStream(paramArrayOfbyte)));
  }
  
  public X509Principal(X509Name paramX509Name) {
    super((ASN1Sequence)paramX509Name.toASN1Primitive());
  }
  
  public X509Principal(X500Name paramX500Name) {
    super((ASN1Sequence)paramX500Name.toASN1Primitive());
  }
  
  public X509Principal(Hashtable paramHashtable) {
    super(paramHashtable);
  }
  
  public X509Principal(Vector paramVector, Hashtable paramHashtable) {
    super(paramVector, paramHashtable);
  }
  
  public X509Principal(Vector paramVector1, Vector paramVector2) {
    super(paramVector1, paramVector2);
  }
  
  public X509Principal(String paramString) {
    super(paramString);
  }
  
  public X509Principal(boolean paramBoolean, String paramString) {
    super(paramBoolean, paramString);
  }
  
  public X509Principal(boolean paramBoolean, Hashtable paramHashtable, String paramString) {
    super(paramBoolean, paramHashtable, paramString);
  }
  
  public String getName() {
    return toString();
  }
  
  public byte[] getEncoded() {
    try {
      return getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
  }
}
