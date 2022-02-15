package org.bouncycastle.dvcs;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Arrays;

public class DVCSRequestInfo {
  private DVCSRequestInformation data;
  
  public DVCSRequestInfo(byte[] paramArrayOfbyte) {
    this(DVCSRequestInformation.getInstance(paramArrayOfbyte));
  }
  
  public DVCSRequestInfo(DVCSRequestInformation paramDVCSRequestInformation) {
    this.data = paramDVCSRequestInformation;
  }
  
  public DVCSRequestInformation toASN1Structure() {
    return this.data;
  }
  
  public int getVersion() {
    return this.data.getVersion();
  }
  
  public int getServiceType() {
    return this.data.getService().getValue().intValue();
  }
  
  public BigInteger getNonce() {
    return this.data.getNonce();
  }
  
  public Date getRequestTime() throws DVCSParsingException {
    DVCSTime dVCSTime = this.data.getRequestTime();
    if (dVCSTime == null)
      return null; 
    try {
      if (dVCSTime.getGenTime() != null)
        return dVCSTime.getGenTime().getDate(); 
      TimeStampToken timeStampToken = new TimeStampToken(dVCSTime.getTimeStampToken());
      return timeStampToken.getTimeStampInfo().getGenTime();
    } catch (Exception exception) {
      throw new DVCSParsingException("unable to extract time: " + exception.getMessage(), exception);
    } 
  }
  
  public GeneralNames getRequester() {
    return this.data.getRequester();
  }
  
  public PolicyInformation getRequestPolicy() {
    return (this.data.getRequestPolicy() != null) ? this.data.getRequestPolicy() : null;
  }
  
  public GeneralNames getDVCSNames() {
    return this.data.getDVCS();
  }
  
  public GeneralNames getDataLocations() {
    return this.data.getDataLocations();
  }
  
  public static boolean validate(DVCSRequestInfo paramDVCSRequestInfo1, DVCSRequestInfo paramDVCSRequestInfo2) {
    DVCSRequestInformation dVCSRequestInformation1 = paramDVCSRequestInfo1.data;
    DVCSRequestInformation dVCSRequestInformation2 = paramDVCSRequestInfo2.data;
    if (dVCSRequestInformation1.getVersion() != dVCSRequestInformation2.getVersion())
      return false; 
    if (!clientEqualsServer(dVCSRequestInformation1.getService(), dVCSRequestInformation2.getService()))
      return false; 
    if (!clientEqualsServer(dVCSRequestInformation1.getRequestTime(), dVCSRequestInformation2.getRequestTime()))
      return false; 
    if (!clientEqualsServer(dVCSRequestInformation1.getRequestPolicy(), dVCSRequestInformation2.getRequestPolicy()))
      return false; 
    if (!clientEqualsServer(dVCSRequestInformation1.getExtensions(), dVCSRequestInformation2.getExtensions()))
      return false; 
    if (dVCSRequestInformation1.getNonce() != null) {
      if (dVCSRequestInformation2.getNonce() == null)
        return false; 
      byte[] arrayOfByte1 = dVCSRequestInformation1.getNonce().toByteArray();
      byte[] arrayOfByte2 = dVCSRequestInformation2.getNonce().toByteArray();
      if (arrayOfByte2.length < arrayOfByte1.length)
        return false; 
      if (!Arrays.areEqual(arrayOfByte1, Arrays.copyOfRange(arrayOfByte2, 0, arrayOfByte1.length)))
        return false; 
    } 
    return true;
  }
  
  private static boolean clientEqualsServer(Object paramObject1, Object paramObject2) {
    return ((paramObject1 == null && paramObject2 == null) || (paramObject1 != null && paramObject1.equals(paramObject2)));
  }
}
