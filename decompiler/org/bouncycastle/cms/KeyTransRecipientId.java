package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyTransRecipientId extends RecipientId {
  private X509CertificateHolderSelector baseSelector;
  
  private KeyTransRecipientId(X509CertificateHolderSelector paramX509CertificateHolderSelector) {
    super(0);
    this.baseSelector = paramX509CertificateHolderSelector;
  }
  
  public KeyTransRecipientId(byte[] paramArrayOfbyte) {
    this(null, null, paramArrayOfbyte);
  }
  
  public KeyTransRecipientId(X500Name paramX500Name, BigInteger paramBigInteger) {
    this(paramX500Name, paramBigInteger, null);
  }
  
  public KeyTransRecipientId(X500Name paramX500Name, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    this(new X509CertificateHolderSelector(paramX500Name, paramBigInteger, paramArrayOfbyte));
  }
  
  public X500Name getIssuer() {
    return this.baseSelector.getIssuer();
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
    if (!(paramObject instanceof KeyTransRecipientId))
      return false; 
    KeyTransRecipientId keyTransRecipientId = (KeyTransRecipientId)paramObject;
    return this.baseSelector.equals(keyTransRecipientId.baseSelector);
  }
  
  public Object clone() {
    return new KeyTransRecipientId(this.baseSelector);
  }
  
  public boolean match(Object paramObject) {
    return (paramObject instanceof KeyTransRecipientInformation) ? ((KeyTransRecipientInformation)paramObject).getRID().equals(this) : this.baseSelector.match(paramObject);
  }
}
