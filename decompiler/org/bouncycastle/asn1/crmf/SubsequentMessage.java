package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Integer;

public class SubsequentMessage extends ASN1Integer {
  public static final SubsequentMessage encrCert = new SubsequentMessage(0);
  
  public static final SubsequentMessage challengeResp = new SubsequentMessage(1);
  
  private SubsequentMessage(int paramInt) {
    super(paramInt);
  }
  
  public static SubsequentMessage valueOf(int paramInt) {
    if (paramInt == 0)
      return encrCert; 
    if (paramInt == 1)
      return challengeResp; 
    throw new IllegalArgumentException("unknown value: " + paramInt);
  }
}
