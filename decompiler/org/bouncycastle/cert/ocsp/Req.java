package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.x509.Extensions;

public class Req {
  private Request req;
  
  public Req(Request paramRequest) {
    this.req = paramRequest;
  }
  
  public CertificateID getCertID() {
    return new CertificateID(this.req.getReqCert());
  }
  
  public Extensions getSingleRequestExtensions() {
    return this.req.getSingleRequestExtensions();
  }
}
