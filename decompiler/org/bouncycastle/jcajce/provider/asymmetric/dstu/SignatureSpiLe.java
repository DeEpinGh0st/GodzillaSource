package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SignatureSpiLe extends SignatureSpi {
  void reverseBytes(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < paramArrayOfbyte.length / 2; b++) {
      byte b1 = paramArrayOfbyte[b];
      paramArrayOfbyte[b] = paramArrayOfbyte[paramArrayOfbyte.length - 1 - b];
      paramArrayOfbyte[paramArrayOfbyte.length - 1 - b] = b1;
    } 
  }
  
  protected byte[] engineSign() throws SignatureException {
    byte[] arrayOfByte = ASN1OctetString.getInstance(super.engineSign()).getOctets();
    reverseBytes(arrayOfByte);
    try {
      return (new DEROctetString(arrayOfByte)).getEncoded();
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = ((ASN1OctetString)ASN1OctetString.fromByteArray(paramArrayOfbyte)).getOctets();
    } catch (IOException iOException) {
      throw new SignatureException("error decoding signature bytes.");
    } 
    reverseBytes(arrayOfByte);
    try {
      return super.engineVerify((new DEROctetString(arrayOfByte)).getEncoded());
    } catch (SignatureException signatureException) {
      throw signatureException;
    } catch (Exception exception) {
      throw new SignatureException(exception.toString());
    } 
  }
}
