package org.bouncycastle.est;

import java.net.URL;
import org.bouncycastle.util.Arrays;

public class ESTRequestBuilder {
  private final String method;
  
  private URL url;
  
  private HttpUtil.Headers headers;
  
  ESTHijacker hijacker;
  
  ESTSourceConnectionListener listener;
  
  ESTClient client;
  
  private byte[] data;
  
  public ESTRequestBuilder(ESTRequest paramESTRequest) {
    this.method = paramESTRequest.method;
    this.url = paramESTRequest.url;
    this.listener = paramESTRequest.listener;
    this.data = paramESTRequest.data;
    this.hijacker = paramESTRequest.hijacker;
    this.headers = (HttpUtil.Headers)paramESTRequest.headers.clone();
    this.client = paramESTRequest.getClient();
  }
  
  public ESTRequestBuilder(String paramString, URL paramURL) {
    this.method = paramString;
    this.url = paramURL;
    this.headers = new HttpUtil.Headers();
  }
  
  public ESTRequestBuilder withConnectionListener(ESTSourceConnectionListener paramESTSourceConnectionListener) {
    this.listener = paramESTSourceConnectionListener;
    return this;
  }
  
  public ESTRequestBuilder withHijacker(ESTHijacker paramESTHijacker) {
    this.hijacker = paramESTHijacker;
    return this;
  }
  
  public ESTRequestBuilder withURL(URL paramURL) {
    this.url = paramURL;
    return this;
  }
  
  public ESTRequestBuilder withData(byte[] paramArrayOfbyte) {
    this.data = Arrays.clone(paramArrayOfbyte);
    return this;
  }
  
  public ESTRequestBuilder addHeader(String paramString1, String paramString2) {
    this.headers.add(paramString1, paramString2);
    return this;
  }
  
  public ESTRequestBuilder setHeader(String paramString1, String paramString2) {
    this.headers.set(paramString1, paramString2);
    return this;
  }
  
  public ESTRequestBuilder withClient(ESTClient paramESTClient) {
    this.client = paramESTClient;
    return this;
  }
  
  public ESTRequest build() {
    return new ESTRequest(this.method, this.url, this.data, this.hijacker, this.listener, this.headers, this.client);
  }
}
