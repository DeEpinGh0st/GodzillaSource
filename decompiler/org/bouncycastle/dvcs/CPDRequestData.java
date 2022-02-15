package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;

public class CPDRequestData extends DVCSRequestData {
  CPDRequestData(Data paramData) throws DVCSConstructionException {
    super(paramData);
    initMessage();
  }
  
  private void initMessage() throws DVCSConstructionException {
    if (this.data.getMessage() == null)
      throw new DVCSConstructionException("DVCSRequest.data.message should be specified for CPD service"); 
  }
  
  public byte[] getMessage() {
    return this.data.getMessage().getOctets();
  }
}
