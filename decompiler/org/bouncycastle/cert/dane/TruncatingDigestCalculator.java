package org.bouncycastle.cert.dane;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

public class TruncatingDigestCalculator implements DigestCalculator {
  private final DigestCalculator baseCalculator;
  
  private final int length;
  
  public TruncatingDigestCalculator(DigestCalculator paramDigestCalculator) {
    this(paramDigestCalculator, 28);
  }
  
  public TruncatingDigestCalculator(DigestCalculator paramDigestCalculator, int paramInt) {
    this.baseCalculator = paramDigestCalculator;
    this.length = paramInt;
  }
  
  public AlgorithmIdentifier getAlgorithmIdentifier() {
    return this.baseCalculator.getAlgorithmIdentifier();
  }
  
  public OutputStream getOutputStream() {
    return this.baseCalculator.getOutputStream();
  }
  
  public byte[] getDigest() {
    byte[] arrayOfByte1 = new byte[this.length];
    byte[] arrayOfByte2 = this.baseCalculator.getDigest();
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, arrayOfByte1.length);
    return arrayOfByte1;
  }
}
