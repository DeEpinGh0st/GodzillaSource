package org.bouncycastle.util.io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PemObject implements PemObjectGenerator {
  private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
  
  private String type;
  
  private List headers;
  
  private byte[] content;
  
  public PemObject(String paramString, byte[] paramArrayOfbyte) {
    this(paramString, EMPTY_LIST, paramArrayOfbyte);
  }
  
  public PemObject(String paramString, List<?> paramList, byte[] paramArrayOfbyte) {
    this.type = paramString;
    this.headers = Collections.unmodifiableList(paramList);
    this.content = paramArrayOfbyte;
  }
  
  public String getType() {
    return this.type;
  }
  
  public List getHeaders() {
    return this.headers;
  }
  
  public byte[] getContent() {
    return this.content;
  }
  
  public PemObject generate() throws PemGenerationException {
    return this;
  }
}
