package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6Client {
  protected BigInteger N;
  
  protected BigInteger g;
  
  protected BigInteger a;
  
  protected BigInteger A;
  
  protected BigInteger B;
  
  protected BigInteger x;
  
  protected BigInteger u;
  
  protected BigInteger S;
  
  protected BigInteger M1;
  
  protected BigInteger M2;
  
  protected BigInteger Key;
  
  protected Digest digest;
  
  protected SecureRandom random;
  
  public void init(BigInteger paramBigInteger1, BigInteger paramBigInteger2, Digest paramDigest, SecureRandom paramSecureRandom) {
    this.N = paramBigInteger1;
    this.g = paramBigInteger2;
    this.digest = paramDigest;
    this.random = paramSecureRandom;
  }
  
  public void init(SRP6GroupParameters paramSRP6GroupParameters, Digest paramDigest, SecureRandom paramSecureRandom) {
    init(paramSRP6GroupParameters.getN(), paramSRP6GroupParameters.getG(), paramDigest, paramSecureRandom);
  }
  
  public BigInteger generateClientCredentials(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    this.x = SRP6Util.calculateX(this.digest, this.N, paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3);
    this.a = selectPrivateValue();
    this.A = this.g.modPow(this.a, this.N);
    return this.A;
  }
  
  public BigInteger calculateSecret(BigInteger paramBigInteger) throws CryptoException {
    this.B = SRP6Util.validatePublicValue(this.N, paramBigInteger);
    this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
    this.S = calculateS();
    return this.S;
  }
  
  protected BigInteger selectPrivateValue() {
    return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
  }
  
  private BigInteger calculateS() {
    BigInteger bigInteger1 = SRP6Util.calculateK(this.digest, this.N, this.g);
    BigInteger bigInteger2 = this.u.multiply(this.x).add(this.a);
    BigInteger bigInteger3 = this.g.modPow(this.x, this.N).multiply(bigInteger1).mod(this.N);
    return this.B.subtract(bigInteger3).mod(this.N).modPow(bigInteger2, this.N);
  }
  
  public BigInteger calculateClientEvidenceMessage() throws CryptoException {
    if (this.A == null || this.B == null || this.S == null)
      throw new CryptoException("Impossible to compute M1: some data are missing from the previous operations (A,B,S)"); 
    this.M1 = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
    return this.M1;
  }
  
  public boolean verifyServerEvidenceMessage(BigInteger paramBigInteger) throws CryptoException {
    if (this.A == null || this.M1 == null || this.S == null)
      throw new CryptoException("Impossible to compute and verify M2: some data are missing from the previous operations (A,M1,S)"); 
    BigInteger bigInteger = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
    if (bigInteger.equals(paramBigInteger)) {
      this.M2 = paramBigInteger;
      return true;
    } 
    return false;
  }
  
  public BigInteger calculateSessionKey() throws CryptoException {
    if (this.S == null || this.M1 == null || this.M2 == null)
      throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)"); 
    this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
    return this.Key;
  }
}
