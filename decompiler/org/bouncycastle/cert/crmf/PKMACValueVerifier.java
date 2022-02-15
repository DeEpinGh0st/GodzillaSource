package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;

class PKMACValueVerifier {
  private final PKMACBuilder builder;
  
  public PKMACValueVerifier(PKMACBuilder paramPKMACBuilder) {
    this.builder = paramPKMACBuilder;
  }
  
  public boolean isValid(PKMACValue paramPKMACValue, char[] paramArrayOfchar, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws CRMFException {
    this.builder.setParameters(PBMParameter.getInstance(paramPKMACValue.getAlgId().getParameters()));
    MacCalculator macCalculator = this.builder.build(paramArrayOfchar);
    OutputStream outputStream = macCalculator.getOutputStream();
    try {
      outputStream.write(paramSubjectPublicKeyInfo.getEncoded("DER"));
      outputStream.close();
    } catch (IOException iOException) {
      throw new CRMFException("exception encoding mac input: " + iOException.getMessage(), iOException);
    } 
    return Arrays.areEqual(macCalculator.getMac(), paramPKMACValue.getValue().getBytes());
  }
}
