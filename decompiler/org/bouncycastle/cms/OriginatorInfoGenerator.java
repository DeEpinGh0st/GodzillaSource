package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class OriginatorInfoGenerator {
  private final List origCerts = new ArrayList(1);
  
  private final List origCRLs;
  
  public OriginatorInfoGenerator(X509CertificateHolder paramX509CertificateHolder) {
    this.origCRLs = null;
    this.origCerts.add(paramX509CertificateHolder.toASN1Structure());
  }
  
  public OriginatorInfoGenerator(Store paramStore) throws CMSException {
    this(paramStore, null);
  }
  
  public OriginatorInfoGenerator(Store paramStore1, Store paramStore2) throws CMSException {
    if (paramStore2 != null) {
      this.origCRLs = CMSUtils.getCRLsFromStore(paramStore2);
    } else {
      this.origCRLs = null;
    } 
  }
  
  public OriginatorInformation generate() {
    return (this.origCRLs != null) ? new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), CMSUtils.createDerSetFromList(this.origCRLs))) : new OriginatorInformation(new OriginatorInfo(CMSUtils.createDerSetFromList(this.origCerts), null));
  }
}
