package org.bouncycastle.asn1;

import java.util.Date;

public class DERGeneralizedTime extends ASN1GeneralizedTime {
  DERGeneralizedTime(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public DERGeneralizedTime(Date paramDate) {
    super(paramDate);
  }
  
  public DERGeneralizedTime(String paramString) {
    super(paramString);
  }
}
