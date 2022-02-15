package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyAgreeRecipientId extends RecipientId {
  private X509CertificateHolderSelector baseSelector;
  
  private KeyAgreeRecipientId(X509CertificateHolderSelector paramX509CertificateHolderSelector) {
    super(2);
    this.baseSelector = paramX509CertificateHolderSelector;
  }
  
  public KeyAgreeRecipientId(byte[] paramArrayOfbyte) {
    this(null, null, paramArrayOfbyte);
  }
  
  public KeyAgreeRecipientId(X500Name paramX500Name, BigInteger paramBigInteger) {
    this(paramX500Name, paramBigInteger, null);
  }
  
  public KeyAgreeRecipientId(X500Name paramX500Name, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    this(new X509CertificateHolderSelector(paramX500Name, paramBigInteger, paramArrayOfbyte));
  }
  
  public BigInteger getSerialNumber() {
    return this.baseSelector.getSerialNumber();
  }
  
  public byte[] getSubjectKeyIdentifier() {
    return this.baseSelector.getSubjectKeyIdentifier();
  }
  
  public int hashCode() {
    return this.baseSelector.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof KeyAgreeRecipientId))
      return false; 
    KeyAgreeRecipientId keyAgreeRecipientId = (KeyAgreeRecipientId)paramObject;
    return this.baseSelector.equals(keyAgreeRecipientId.baseSelector);
  }
  
  public Object clone() {
    return new KeyAgreeRecipientId(this.baseSelector);
  }
  
  public boolean match(Object paramObject) {
    return (paramObject instanceof KeyAgreeRecipientInformation) ? ((KeyAgreeRecipientInformation)paramObject).getRID().equals(this) : this.baseSelector.match(paramObject);
  }
}
