package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

class OriginatorId implements Selector {
  private byte[] subjectKeyId;
  
  private X500Name issuer;
  
  private BigInteger serialNumber;
  
  public OriginatorId(byte[] paramArrayOfbyte) {
    setSubjectKeyID(paramArrayOfbyte);
  }
  
  private void setSubjectKeyID(byte[] paramArrayOfbyte) {
    this.subjectKeyId = paramArrayOfbyte;
  }
  
  public OriginatorId(X500Name paramX500Name, BigInteger paramBigInteger) {
    setIssuerAndSerial(paramX500Name, paramBigInteger);
  }
  
  private void setIssuerAndSerial(X500Name paramX500Name, BigInteger paramBigInteger) {
    this.issuer = paramX500Name;
    this.serialNumber = paramBigInteger;
  }
  
  public OriginatorId(X500Name paramX500Name, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    setIssuerAndSerial(paramX500Name, paramBigInteger);
    setSubjectKeyID(paramArrayOfbyte);
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public Object clone() {
    return new OriginatorId(this.issuer, this.serialNumber, this.subjectKeyId);
  }
  
  public int hashCode() {
    int i = Arrays.hashCode(this.subjectKeyId);
    if (this.serialNumber != null)
      i ^= this.serialNumber.hashCode(); 
    if (this.issuer != null)
      i ^= this.issuer.hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof OriginatorId))
      return false; 
    OriginatorId originatorId = (OriginatorId)paramObject;
    return (Arrays.areEqual(this.subjectKeyId, originatorId.subjectKeyId) && equalsObj(this.serialNumber, originatorId.serialNumber) && equalsObj(this.issuer, originatorId.issuer));
  }
  
  private boolean equalsObj(Object paramObject1, Object paramObject2) {
    return (paramObject1 != null) ? paramObject1.equals(paramObject2) : ((paramObject2 == null));
  }
  
  public boolean match(Object paramObject) {
    return false;
  }
}
