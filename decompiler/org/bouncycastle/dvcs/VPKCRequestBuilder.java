package org.bouncycastle.dvcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.dvcs.CertEtcToken;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;

public class VPKCRequestBuilder extends DVCSRequestBuilder {
  private List chains = new ArrayList();
  
  public VPKCRequestBuilder() {
    super(new DVCSRequestInformationBuilder(ServiceType.VPKC));
  }
  
  public void addTargetChain(X509CertificateHolder paramX509CertificateHolder) {
    this.chains.add(new TargetEtcChain(new CertEtcToken(0, (ASN1Encodable)paramX509CertificateHolder.toASN1Structure())));
  }
  
  public void addTargetChain(Extension paramExtension) {
    this.chains.add(new TargetEtcChain(new CertEtcToken(paramExtension)));
  }
  
  public void addTargetChain(TargetChain paramTargetChain) {
    this.chains.add(paramTargetChain.toASN1Structure());
  }
  
  public void setRequestTime(Date paramDate) {
    this.requestInformationBuilder.setRequestTime(new DVCSTime(paramDate));
  }
  
  public DVCSRequest build() throws DVCSException {
    Data data = new Data((TargetEtcChain[])this.chains.toArray((Object[])new TargetEtcChain[this.chains.size()]));
    return createDVCRequest(data);
  }
}
