package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.Extensions;

public class RespData {
  private ResponseData data;
  
  public RespData(ResponseData paramResponseData) {
    this.data = paramResponseData;
  }
  
  public int getVersion() {
    return this.data.getVersion().getValue().intValue() + 1;
  }
  
  public RespID getResponderId() {
    return new RespID(this.data.getResponderID());
  }
  
  public Date getProducedAt() {
    return OCSPUtils.extractDate(this.data.getProducedAt());
  }
  
  public SingleResp[] getResponses() {
    ASN1Sequence aSN1Sequence = this.data.getResponses();
    SingleResp[] arrayOfSingleResp = new SingleResp[aSN1Sequence.size()];
    for (byte b = 0; b != arrayOfSingleResp.length; b++)
      arrayOfSingleResp[b] = new SingleResp(SingleResponse.getInstance(aSN1Sequence.getObjectAt(b))); 
    return arrayOfSingleResp;
  }
  
  public Extensions getResponseExtensions() {
    return this.data.getResponseExtensions();
  }
}
