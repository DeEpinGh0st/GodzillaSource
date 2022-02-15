package org.bouncycastle.asn1;

import java.util.Date;

public class DERUTCTime extends ASN1UTCTime {
  DERUTCTime(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public DERUTCTime(Date paramDate) {
    super(paramDate);
  }
  
  public DERUTCTime(String paramString) {
    super(paramString);
  }
}
