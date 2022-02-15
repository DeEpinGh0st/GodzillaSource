package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6Server {
  protected BigInteger N;
  
  protected BigInteger g;
  
  protected BigInteger v;
  
  protected SecureRandom random;
  
  protected Digest digest;
  
  protected BigInteger A;
  
  protected BigInteger b;
  
  protected BigInteger B;
  
  protected BigInteger u;
  
  protected BigInteger S;
  
  protected BigInteger M1;
  
  protected BigInteger M2;
  
  protected BigInteger Key;
  
  public void init(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, Digest paramDigest, SecureRandom paramSecureRandom) {
    this.N = paramBigInteger1;
    this.g = paramBigInteger2;
    this.v = paramBigInteger3;
    this.random = paramSecureRandom;
    this.digest = paramDigest;
  }
  
  public void init(SRP6GroupParameters paramSRP6GroupParameters, BigInteger paramBigInteger, Digest paramDigest, SecureRandom paramSecureRandom) {
    init(paramSRP6GroupParameters.getN(), paramSRP6GroupParameters.getG(), paramBigInteger, paramDigest, paramSecureRandom);
  }
  
  public BigInteger generateServerCredentials() {
    BigInteger bigInteger = SRP6Util.calculateK(this.digest, this.N, this.g);
    this.b = selectPrivateValue();
    this.B = bigInteger.multiply(this.v).mod(this.N).add(this.g.modPow(this.b, this.N)).mod(this.N);
    return this.B;
  }
  
  public BigInteger calculateSecret(BigInteger paramBigInteger) throws CryptoException {
    this.A = SRP6Util.validatePublicValue(this.N, paramBigInteger);
    this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
    this.S = calculateS();
    return this.S;
  }
  
  protected BigInteger selectPrivateValue() {
    return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
  }
  
  private BigInteger calculateS() {
    return this.v.modPow(this.u, this.N).multiply(this.A).mod(this.N).modPow(this.b, this.N);
  }
  
  public boolean verifyClientEvidenceMessage(BigInteger paramBigInteger) throws CryptoException {
    if (this.A == null || this.B == null || this.S == null)
      throw new CryptoException("Impossible to compute and verify M1: some data are missing from the previous operations (A,B,S)"); 
    BigInteger bigInteger = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
    if (bigInteger.equals(paramBigInteger)) {
      this.M1 = paramBigInteger;
      return true;
    } 
    return false;
  }
  
  public BigInteger calculateServerEvidenceMessage() throws CryptoException {
    if (this.A == null || this.M1 == null || this.S == null)
      throw new CryptoException("Impossible to compute M2: some data are missing from the previous operations (A,M1,S)"); 
    this.M2 = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
    return this.M2;
  }
  
  public BigInteger calculateSessionKey() throws CryptoException {
    if (this.S == null || this.M1 == null || this.M2 == null)
      throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)"); 
    this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
    return this.Key;
  }
}
