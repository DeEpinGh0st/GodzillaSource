package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6VerifierGenerator {
  protected BigInteger N;
  
  protected BigInteger g;
  
  protected Digest digest;
  
  public void init(BigInteger paramBigInteger1, BigInteger paramBigInteger2, Digest paramDigest) {
    this.N = paramBigInteger1;
    this.g = paramBigInteger2;
    this.digest = paramDigest;
  }
  
  public void init(SRP6GroupParameters paramSRP6GroupParameters, Digest paramDigest) {
    this.N = paramSRP6GroupParameters.getN();
    this.g = paramSRP6GroupParameters.getG();
    this.digest = paramDigest;
  }
  
  public BigInteger generateVerifier(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    BigInteger bigInteger = SRP6Util.calculateX(this.digest, this.N, paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3);
    return this.g.modPow(bigInteger, this.N);
  }
}
