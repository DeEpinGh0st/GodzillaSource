package org.bouncycastle.asn1;

import java.math.BigInteger;

public class DEREnumerated extends ASN1Enumerated {
  DEREnumerated(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public DEREnumerated(BigInteger paramBigInteger) {
    super(paramBigInteger);
  }
  
  public DEREnumerated(int paramInt) {
    super(paramInt);
  }
}
