package org.bouncycastle.cert.dane;

import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;

public class DANEEntry {
  public static final int CERT_USAGE_CA = 0;
  
  public static final int CERT_USAGE_PKIX_VALIDATE = 1;
  
  public static final int CERT_USAGE_TRUST_ANCHOR = 2;
  
  public static final int CERT_USAGE_ACCEPT = 3;
  
  static final int CERT_USAGE = 0;
  
  static final int SELECTOR = 1;
  
  static final int MATCHING_TYPE = 2;
  
  private final String domainName;
  
  private final byte[] flags;
  
  private final X509CertificateHolder certHolder;
  
  DANEEntry(String paramString, byte[] paramArrayOfbyte, X509CertificateHolder paramX509CertificateHolder) {
    this.flags = paramArrayOfbyte;
    this.domainName = paramString;
    this.certHolder = paramX509CertificateHolder;
  }
  
  public DANEEntry(String paramString, byte[] paramArrayOfbyte) throws IOException {
    this(paramString, Arrays.copyOfRange(paramArrayOfbyte, 0, 3), new X509CertificateHolder(Arrays.copyOfRange(paramArrayOfbyte, 3, paramArrayOfbyte.length)));
  }
  
  public byte[] getFlags() {
    return Arrays.clone(this.flags);
  }
  
  public X509CertificateHolder getCertificate() {
    return this.certHolder;
  }
  
  public String getDomainName() {
    return this.domainName;
  }
  
  public byte[] getRDATA() throws IOException {
    byte[] arrayOfByte1 = this.certHolder.getEncoded();
    byte[] arrayOfByte2 = new byte[this.flags.length + arrayOfByte1.length];
    System.arraycopy(this.flags, 0, arrayOfByte2, 0, this.flags.length);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, this.flags.length, arrayOfByte1.length);
    return arrayOfByte2;
  }
  
  public static boolean isValidCertificate(byte[] paramArrayOfbyte) {
    return ((paramArrayOfbyte[0] >= 0 || paramArrayOfbyte[0] <= 3) && paramArrayOfbyte[1] == 0 && paramArrayOfbyte[2] == 0);
  }
}
