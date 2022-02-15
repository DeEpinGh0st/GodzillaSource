package org.bouncycastle.jce.provider;

import java.util.Date;

class CertStatus {
  public static final int UNREVOKED = 11;
  
  public static final int UNDETERMINED = 12;
  
  int certStatus = 11;
  
  Date revocationDate = null;
  
  public Date getRevocationDate() {
    return this.revocationDate;
  }
  
  public void setRevocationDate(Date paramDate) {
    this.revocationDate = paramDate;
  }
  
  public int getCertStatus() {
    return this.certStatus;
  }
  
  public void setCertStatus(int paramInt) {
    this.certStatus = paramInt;
  }
}
