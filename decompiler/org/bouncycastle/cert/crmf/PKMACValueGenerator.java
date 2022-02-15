package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.MacCalculator;

class PKMACValueGenerator {
  private PKMACBuilder builder;
  
  public PKMACValueGenerator(PKMACBuilder paramPKMACBuilder) {
    this.builder = paramPKMACBuilder;
  }
  
  public PKMACValue generate(char[] paramArrayOfchar, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws CRMFException {
    MacCalculator macCalculator = this.builder.build(paramArrayOfchar);
    OutputStream outputStream = macCalculator.getOutputStream();
    try {
      outputStream.write(paramSubjectPublicKeyInfo.getEncoded("DER"));
      outputStream.close();
    } catch (IOException iOException) {
      throw new CRMFException("exception encoding mac input: " + iOException.getMessage(), iOException);
    } 
    return new PKMACValue(macCalculator.getAlgorithmIdentifier(), new DERBitString(macCalculator.getMac()));
  }
}
