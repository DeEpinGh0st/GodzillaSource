package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.Strings;

public class PKMACBuilder {
  private AlgorithmIdentifier owf;
  
  private int iterationCount;
  
  private AlgorithmIdentifier mac;
  
  private int saltLength = 20;
  
  private SecureRandom random;
  
  private PKMACValuesCalculator calculator;
  
  private PBMParameter parameters;
  
  private int maxIterations;
  
  public PKMACBuilder(PKMACValuesCalculator paramPKMACValuesCalculator) {
    this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, (ASN1Encodable)DERNull.INSTANCE), paramPKMACValuesCalculator);
  }
  
  public PKMACBuilder(PKMACValuesCalculator paramPKMACValuesCalculator, int paramInt) {
    this.maxIterations = paramInt;
    this.calculator = paramPKMACValuesCalculator;
  }
  
  private PKMACBuilder(AlgorithmIdentifier paramAlgorithmIdentifier1, int paramInt, AlgorithmIdentifier paramAlgorithmIdentifier2, PKMACValuesCalculator paramPKMACValuesCalculator) {
    this.owf = paramAlgorithmIdentifier1;
    this.iterationCount = paramInt;
    this.mac = paramAlgorithmIdentifier2;
    this.calculator = paramPKMACValuesCalculator;
  }
  
  public PKMACBuilder setSaltLength(int paramInt) {
    if (paramInt < 8)
      throw new IllegalArgumentException("salt length must be at least 8 bytes"); 
    this.saltLength = paramInt;
    return this;
  }
  
  public PKMACBuilder setIterationCount(int paramInt) {
    if (paramInt < 100)
      throw new IllegalArgumentException("iteration count must be at least 100"); 
    checkIterationCountCeiling(paramInt);
    this.iterationCount = paramInt;
    return this;
  }
  
  public PKMACBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public PKMACBuilder setParameters(PBMParameter paramPBMParameter) {
    checkIterationCountCeiling(paramPBMParameter.getIterationCount().getValue().intValue());
    this.parameters = paramPBMParameter;
    return this;
  }
  
  public MacCalculator build(char[] paramArrayOfchar) throws CRMFException {
    if (this.parameters != null)
      return genCalculator(this.parameters, paramArrayOfchar); 
    byte[] arrayOfByte = new byte[this.saltLength];
    if (this.random == null)
      this.random = new SecureRandom(); 
    this.random.nextBytes(arrayOfByte);
    return genCalculator(new PBMParameter(arrayOfByte, this.owf, this.iterationCount, this.mac), paramArrayOfchar);
  }
  
  private void checkIterationCountCeiling(int paramInt) {
    if (this.maxIterations > 0 && paramInt > this.maxIterations)
      throw new IllegalArgumentException("iteration count exceeds limit (" + paramInt + " > " + this.maxIterations + ")"); 
  }
  
  private MacCalculator genCalculator(final PBMParameter params, char[] paramArrayOfchar) throws CRMFException {
    byte[] arrayOfByte1 = Strings.toUTF8ByteArray(paramArrayOfchar);
    byte[] arrayOfByte2 = params.getSalt().getOctets();
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    this.calculator.setup(params.getOwf(), params.getMac());
    int i = params.getIterationCount().getValue().intValue();
    while (true) {
      arrayOfByte3 = this.calculator.calculateDigest(arrayOfByte3);
      if (--i <= 0) {
        final byte[] key = arrayOfByte3;
        return new MacCalculator() {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
              return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, (ASN1Encodable)params);
            }
            
            public GenericKey getKey() {
              return new GenericKey(getAlgorithmIdentifier(), key);
            }
            
            public OutputStream getOutputStream() {
              return this.bOut;
            }
            
            public byte[] getMac() {
              try {
                return PKMACBuilder.this.calculator.calculateMac(key, this.bOut.toByteArray());
              } catch (CRMFException cRMFException) {
                throw new RuntimeOperatorException("exception calculating mac: " + cRMFException.getMessage(), cRMFException);
              } 
            }
          };
      } 
    } 
  }
}
