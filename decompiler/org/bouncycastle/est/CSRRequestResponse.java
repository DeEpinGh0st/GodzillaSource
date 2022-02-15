package org.bouncycastle.est;

public class CSRRequestResponse {
  private final CSRAttributesResponse attributesResponse;
  
  private final Source source;
  
  public CSRRequestResponse(CSRAttributesResponse paramCSRAttributesResponse, Source paramSource) {
    this.attributesResponse = paramCSRAttributesResponse;
    this.source = paramSource;
  }
  
  public boolean hasAttributesResponse() {
    return (this.attributesResponse != null);
  }
  
  public CSRAttributesResponse getAttributesResponse() {
    if (this.attributesResponse == null)
      throw new IllegalStateException("Response has no CSRAttributesResponse."); 
    return this.attributesResponse;
  }
  
  public Object getSession() {
    return this.source.getSession();
  }
  
  public Source getSource() {
    return this.source;
  }
}
