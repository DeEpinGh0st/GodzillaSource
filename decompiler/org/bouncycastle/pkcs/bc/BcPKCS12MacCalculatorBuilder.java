package org.bouncycastle.pkcs.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class BcPKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder {
  private ExtendedDigest digest;
  
  private AlgorithmIdentifier algorithmIdentifier;
  
  private SecureRandom random;
  
  private int saltLength;
  
  private int iterationCount = 1024;
  
  public BcPKCS12MacCalculatorBuilder() {
    this((ExtendedDigest)new SHA1Digest(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
  }
  
  public BcPKCS12MacCalculatorBuilder(ExtendedDigest paramExtendedDigest, AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.digest = paramExtendedDigest;
    this.algorithmIdentifier = paramAlgorithmIdentifier;
    this.saltLength = paramExtendedDigest.getDigestSize();
  }
  
  public BcPKCS12MacCalculatorBuilder setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
    return this;
  }
  
  public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
    return this.algorithmIdentifier;
  }
  
  public MacCalculator build(char[] paramArrayOfchar) {
    if (this.random == null)
      this.random = new SecureRandom(); 
    byte[] arrayOfByte = new byte[this.saltLength];
    this.random.nextBytes(arrayOfByte);
    return PKCS12PBEUtils.createMacCalculator(this.algorithmIdentifier.getAlgorithm(), this.digest, new PKCS12PBEParams(arrayOfByte, this.iterationCount), paramArrayOfchar);
  }
}
