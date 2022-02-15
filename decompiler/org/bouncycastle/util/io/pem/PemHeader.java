package org.bouncycastle.util.io.pem;

public class PemHeader {
  private String name;
  
  private String value;
  
  public PemHeader(String paramString1, String paramString2) {
    this.name = paramString1;
    this.value = paramString2;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public int hashCode() {
    return getHashCode(this.name) + 31 * getHashCode(this.value);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PemHeader))
      return false; 
    PemHeader pemHeader = (PemHeader)paramObject;
    return (pemHeader == this || (isEqual(this.name, pemHeader.name) && isEqual(this.value, pemHeader.value)));
  }
  
  private int getHashCode(String paramString) {
    return (paramString == null) ? 1 : paramString.hashCode();
  }
  
  private boolean isEqual(String paramString1, String paramString2) {
    return (paramString1 == paramString2) ? true : ((paramString1 == null || paramString2 == null) ? false : paramString1.equals(paramString2));
  }
}
