package org.bouncycastle.dvcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;

public class VPKCRequestData extends DVCSRequestData {
  private List chains;
  
  VPKCRequestData(Data paramData) throws DVCSConstructionException {
    super(paramData);
    TargetEtcChain[] arrayOfTargetEtcChain = paramData.getCerts();
    if (arrayOfTargetEtcChain == null)
      throw new DVCSConstructionException("DVCSRequest.data.certs should be specified for VPKC service"); 
    this.chains = new ArrayList(arrayOfTargetEtcChain.length);
    for (byte b = 0; b != arrayOfTargetEtcChain.length; b++)
      this.chains.add(new TargetChain(arrayOfTargetEtcChain[b])); 
  }
  
  public List getCerts() {
    return Collections.unmodifiableList(this.chains);
  }
}
