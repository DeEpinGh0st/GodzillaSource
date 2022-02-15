package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;

public class VSDRequestData extends DVCSRequestData {
  private CMSSignedData doc;
  
  VSDRequestData(Data paramData) throws DVCSConstructionException {
    super(paramData);
    initDocument();
  }
  
  private void initDocument() throws DVCSConstructionException {
    if (this.doc == null) {
      if (this.data.getMessage() == null)
        throw new DVCSConstructionException("DVCSRequest.data.message should be specified for VSD service"); 
      try {
        this.doc = new CMSSignedData(this.data.getMessage().getOctets());
      } catch (CMSException cMSException) {
        throw new DVCSConstructionException("Can't read CMS SignedData from input", cMSException);
      } 
    } 
  }
  
  public byte[] getMessage() {
    return this.data.getMessage().getOctets();
  }
  
  public CMSSignedData getParsedMessage() {
    return this.doc;
  }
}
