package org.bouncycastle.dvcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DigestCalculator;

public class MessageImprintBuilder {
  private final DigestCalculator digestCalculator;
  
  public MessageImprintBuilder(DigestCalculator paramDigestCalculator) {
    this.digestCalculator = paramDigestCalculator;
  }
  
  public MessageImprint build(byte[] paramArrayOfbyte) throws DVCSException {
    try {
      OutputStream outputStream = this.digestCalculator.getOutputStream();
      outputStream.write(paramArrayOfbyte);
      outputStream.close();
      return new MessageImprint(new DigestInfo(this.digestCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
    } catch (Exception exception) {
      throw new DVCSException("unable to build MessageImprint: " + exception.getMessage(), exception);
    } 
  }
}
