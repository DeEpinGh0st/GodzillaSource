package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.bouncycastle.est.LimitedSource;
import org.bouncycastle.est.Source;
import org.bouncycastle.est.TLSUniqueProvider;

class LimitedSSLSocketSource implements Source<SSLSession>, TLSUniqueProvider, LimitedSource {
  protected final SSLSocket socket;
  
  private final ChannelBindingProvider bindingProvider;
  
  private final Long absoluteReadLimit;
  
  public LimitedSSLSocketSource(SSLSocket paramSSLSocket, ChannelBindingProvider paramChannelBindingProvider, Long paramLong) {
    this.socket = paramSSLSocket;
    this.bindingProvider = paramChannelBindingProvider;
    this.absoluteReadLimit = paramLong;
  }
  
  public InputStream getInputStream() throws IOException {
    return this.socket.getInputStream();
  }
  
  public OutputStream getOutputStream() throws IOException {
    return this.socket.getOutputStream();
  }
  
  public SSLSession getSession() {
    return this.socket.getSession();
  }
  
  public byte[] getTLSUnique() {
    if (isTLSUniqueAvailable())
      return this.bindingProvider.getChannelBinding(this.socket, "tls-unique"); 
    throw new IllegalStateException("No binding provider.");
  }
  
  public boolean isTLSUniqueAvailable() {
    return this.bindingProvider.canAccessChannelBinding(this.socket);
  }
  
  public void close() throws IOException {
    this.socket.close();
  }
  
  public Long getAbsoluteReadLimit() {
    return this.absoluteReadLimit;
  }
}
