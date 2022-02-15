package org.bouncycastle.pkcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.MacCalculator;

class MacDataGenerator {
  private PKCS12MacCalculatorBuilder builder;
  
  MacDataGenerator(PKCS12MacCalculatorBuilder paramPKCS12MacCalculatorBuilder) {
    this.builder = paramPKCS12MacCalculatorBuilder;
  }
  
  public MacData build(char[] paramArrayOfchar, byte[] paramArrayOfbyte) throws PKCSException {
    MacCalculator macCalculator;
    try {
      macCalculator = this.builder.build(paramArrayOfchar);
      OutputStream outputStream = macCalculator.getOutputStream();
      outputStream.write(paramArrayOfbyte);
      outputStream.close();
    } catch (Exception exception) {
      throw new PKCSException("unable to process data: " + exception.getMessage(), exception);
    } 
    AlgorithmIdentifier algorithmIdentifier = macCalculator.getAlgorithmIdentifier();
    DigestInfo digestInfo = new DigestInfo(this.builder.getDigestAlgorithmIdentifier(), macCalculator.getMac());
    PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
    return new MacData(digestInfo, pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
  }
}
