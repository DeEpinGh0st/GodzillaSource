package org.bouncycastle.est;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class EnrollmentResponse {
  private final Store<X509CertificateHolder> store;
  
  private final long notBefore;
  
  private final ESTRequest requestToRetry;
  
  private final Source source;
  
  public EnrollmentResponse(Store<X509CertificateHolder> paramStore, long paramLong, ESTRequest paramESTRequest, Source paramSource) {
    this.store = paramStore;
    this.notBefore = paramLong;
    this.requestToRetry = paramESTRequest;
    this.source = paramSource;
  }
  
  public boolean canRetry() {
    return (this.notBefore < System.currentTimeMillis());
  }
  
  public Store<X509CertificateHolder> getStore() {
    return this.store;
  }
  
  public long getNotBefore() {
    return this.notBefore;
  }
  
  public ESTRequest getRequestToRetry() {
    return this.requestToRetry;
  }
  
  public Object getSession() {
    return this.source.getSession();
  }
  
  public Source getSource() {
    return this.source;
  }
  
  public boolean isCompleted() {
    return (this.requestToRetry == null);
  }
}
