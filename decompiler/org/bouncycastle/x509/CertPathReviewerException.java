package org.bouncycastle.x509;

import java.security.cert.CertPath;
import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.i18n.LocalizedException;

public class CertPathReviewerException extends LocalizedException {
  private int index = -1;
  
  private CertPath certPath = null;
  
  public CertPathReviewerException(ErrorBundle paramErrorBundle, Throwable paramThrowable) {
    super(paramErrorBundle, paramThrowable);
  }
  
  public CertPathReviewerException(ErrorBundle paramErrorBundle) {
    super(paramErrorBundle);
  }
  
  public CertPathReviewerException(ErrorBundle paramErrorBundle, Throwable paramThrowable, CertPath paramCertPath, int paramInt) {
    super(paramErrorBundle, paramThrowable);
    if (paramCertPath == null || paramInt == -1)
      throw new IllegalArgumentException(); 
    if (paramInt < -1 || (paramCertPath != null && paramInt >= paramCertPath.getCertificates().size()))
      throw new IndexOutOfBoundsException(); 
    this.certPath = paramCertPath;
    this.index = paramInt;
  }
  
  public CertPathReviewerException(ErrorBundle paramErrorBundle, CertPath paramCertPath, int paramInt) {
    super(paramErrorBundle);
    if (paramCertPath == null || paramInt == -1)
      throw new IllegalArgumentException(); 
    if (paramInt < -1 || (paramCertPath != null && paramInt >= paramCertPath.getCertificates().size()))
      throw new IndexOutOfBoundsException(); 
    this.certPath = paramCertPath;
    this.index = paramInt;
  }
  
  public CertPath getCertPath() {
    return this.certPath;
  }
  
  public int getIndex() {
    return this.index;
  }
}
