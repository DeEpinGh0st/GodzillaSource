package org.bouncycastle.est;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

public class ESTRequest {
  final String method;
  
  final URL url;
  
  HttpUtil.Headers headers = new HttpUtil.Headers();
  
  final byte[] data;
  
  final ESTHijacker hijacker;
  
  final ESTClient estClient;
  
  final ESTSourceConnectionListener listener;
  
  ESTRequest(String paramString, URL paramURL, byte[] paramArrayOfbyte, ESTHijacker paramESTHijacker, ESTSourceConnectionListener paramESTSourceConnectionListener, HttpUtil.Headers paramHeaders, ESTClient paramESTClient) {
    this.method = paramString;
    this.url = paramURL;
    this.data = paramArrayOfbyte;
    this.hijacker = paramESTHijacker;
    this.listener = paramESTSourceConnectionListener;
    this.headers = paramHeaders;
    this.estClient = paramESTClient;
  }
  
  public String getMethod() {
    return this.method;
  }
  
  public URL getURL() {
    return this.url;
  }
  
  public Map<String, String[]> getHeaders() {
    return (Map<String, String[]>)this.headers.clone();
  }
  
  public ESTHijacker getHijacker() {
    return this.hijacker;
  }
  
  public ESTClient getClient() {
    return this.estClient;
  }
  
  public ESTSourceConnectionListener getListener() {
    return this.listener;
  }
  
  public void writeData(OutputStream paramOutputStream) throws IOException {
    if (this.data != null)
      paramOutputStream.write(this.data); 
  }
}
