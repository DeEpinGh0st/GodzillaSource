package org.bouncycastle.tsp.cms;

import org.bouncycastle.tsp.TimeStampToken;

public class ImprintDigestInvalidException extends Exception {
  private TimeStampToken token;
  
  public ImprintDigestInvalidException(String paramString, TimeStampToken paramTimeStampToken) {
    super(paramString);
    this.token = paramTimeStampToken;
  }
  
  public TimeStampToken getTimeStampToken() {
    return this.token;
  }
}
