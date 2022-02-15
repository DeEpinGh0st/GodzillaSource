package org.bouncycastle.asn1;

import java.math.BigInteger;

public class DERInteger extends ASN1Integer {
  public DERInteger(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte, true);
  }
  
  public DERInteger(BigInteger paramBigInteger) {
    super(paramBigInteger);
  }
  
  public DERInteger(long paramLong) {
    super(paramLong);
  }
}
