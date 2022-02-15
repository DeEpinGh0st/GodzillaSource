package org.bouncycastle.asn1.eac;

import java.io.UnsupportedEncodingException;

public class CertificateHolderReference {
  private static final String ReferenceEncoding = "ISO-8859-1";
  
  private String countryCode;
  
  private String holderMnemonic;
  
  private String sequenceNumber;
  
  public CertificateHolderReference(String paramString1, String paramString2, String paramString3) {
    this.countryCode = paramString1;
    this.holderMnemonic = paramString2;
    this.sequenceNumber = paramString3;
  }
  
  CertificateHolderReference(byte[] paramArrayOfbyte) {
    try {
      String str = new String(paramArrayOfbyte, "ISO-8859-1");
      this.countryCode = str.substring(0, 2);
      this.holderMnemonic = str.substring(2, str.length() - 5);
      this.sequenceNumber = str.substring(str.length() - 5);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new IllegalStateException(unsupportedEncodingException.toString());
    } 
  }
  
  public String getCountryCode() {
    return this.countryCode;
  }
  
  public String getHolderMnemonic() {
    return this.holderMnemonic;
  }
  
  public String getSequenceNumber() {
    return this.sequenceNumber;
  }
  
  public byte[] getEncoded() {
    String str = this.countryCode + this.holderMnemonic + this.sequenceNumber;
    try {
      return str.getBytes("ISO-8859-1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new IllegalStateException(unsupportedEncodingException.toString());
    } 
  }
}
