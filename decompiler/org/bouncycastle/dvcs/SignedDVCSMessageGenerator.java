package org.bouncycastle.dvcs;

import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;

public class SignedDVCSMessageGenerator {
  private final CMSSignedDataGenerator signedDataGen;
  
  public SignedDVCSMessageGenerator(CMSSignedDataGenerator paramCMSSignedDataGenerator) {
    this.signedDataGen = paramCMSSignedDataGenerator;
  }
  
  public CMSSignedData build(DVCSMessage paramDVCSMessage) throws DVCSException {
    try {
      byte[] arrayOfByte = paramDVCSMessage.getContent().toASN1Primitive().getEncoded("DER");
      return this.signedDataGen.generate((CMSTypedData)new CMSProcessableByteArray(paramDVCSMessage.getContentType(), arrayOfByte), true);
    } catch (CMSException cMSException) {
      throw new DVCSException("Could not sign DVCS request", cMSException);
    } catch (IOException iOException) {
      throw new DVCSException("Could not encode DVCS request", iOException);
    } 
  }
}
