package org.bouncycastle.cert.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.cert.CertIOException;

public class GeneralPKIMessage {
  private final PKIMessage pkiMessage;
  
  private static PKIMessage parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return PKIMessage.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new CertIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CertIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public GeneralPKIMessage(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public GeneralPKIMessage(PKIMessage paramPKIMessage) {
    this.pkiMessage = paramPKIMessage;
  }
  
  public PKIHeader getHeader() {
    return this.pkiMessage.getHeader();
  }
  
  public PKIBody getBody() {
    return this.pkiMessage.getBody();
  }
  
  public boolean hasProtection() {
    return (this.pkiMessage.getHeader().getProtectionAlg() != null);
  }
  
  public PKIMessage toASN1Structure() {
    return this.pkiMessage;
  }
}
