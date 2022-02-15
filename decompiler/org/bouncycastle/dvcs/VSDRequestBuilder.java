package org.bouncycastle.dvcs;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.cms.CMSSignedData;

public class VSDRequestBuilder extends DVCSRequestBuilder {
  public VSDRequestBuilder() {
    super(new DVCSRequestInformationBuilder(ServiceType.VSD));
  }
  
  public void setRequestTime(Date paramDate) {
    this.requestInformationBuilder.setRequestTime(new DVCSTime(paramDate));
  }
  
  public DVCSRequest build(CMSSignedData paramCMSSignedData) throws DVCSException {
    try {
      Data data = new Data(paramCMSSignedData.getEncoded());
      return createDVCRequest(data);
    } catch (IOException iOException) {
      throw new DVCSException("Failed to encode CMS signed data", iOException);
    } 
  }
}
