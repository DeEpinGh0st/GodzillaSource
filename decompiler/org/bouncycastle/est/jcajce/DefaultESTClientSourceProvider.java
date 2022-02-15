package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Strings;

class DefaultESTClientSourceProvider implements ESTClientSourceProvider {
  private final SSLSocketFactory sslSocketFactory;
  
  private final JsseHostnameAuthorizer hostNameAuthorizer;
  
  private final int timeout;
  
  private final ChannelBindingProvider bindingProvider;
  
  private final Set<String> cipherSuites;
  
  private final Long absoluteLimit;
  
  private final boolean filterSupportedSuites;
  
  public DefaultESTClientSourceProvider(SSLSocketFactory paramSSLSocketFactory, JsseHostnameAuthorizer paramJsseHostnameAuthorizer, int paramInt, ChannelBindingProvider paramChannelBindingProvider, Set<String> paramSet, Long paramLong, boolean paramBoolean) throws GeneralSecurityException {
    this.sslSocketFactory = paramSSLSocketFactory;
    this.hostNameAuthorizer = paramJsseHostnameAuthorizer;
    this.timeout = paramInt;
    this.bindingProvider = paramChannelBindingProvider;
    this.cipherSuites = paramSet;
    this.absoluteLimit = paramLong;
    this.filterSupportedSuites = paramBoolean;
  }
  
  public Source makeSource(String paramString, int paramInt) throws IOException {
    SSLSocket sSLSocket = (SSLSocket)this.sslSocketFactory.createSocket(paramString, paramInt);
    sSLSocket.setSoTimeout(this.timeout);
    if (this.cipherSuites != null && !this.cipherSuites.isEmpty())
      if (this.filterSupportedSuites) {
        HashSet<String> hashSet = new HashSet();
        String[] arrayOfString = sSLSocket.getSupportedCipherSuites();
        for (byte b = 0; b != arrayOfString.length; b++)
          hashSet.add(arrayOfString[b]); 
        ArrayList<String> arrayList = new ArrayList();
        for (String str1 : this.cipherSuites) {
          if (hashSet.contains(str1))
            arrayList.add(str1); 
        } 
        if (arrayList.isEmpty())
          throw new IllegalStateException("No supplied cipher suite is supported by the provider."); 
        sSLSocket.setEnabledCipherSuites(arrayList.<String>toArray(new String[arrayList.size()]));
      } else {
        sSLSocket.setEnabledCipherSuites(this.cipherSuites.<String>toArray(new String[this.cipherSuites.size()]));
      }  
    sSLSocket.startHandshake();
    if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(paramString, sSLSocket.getSession()))
      throw new IOException("Host name could not be verified."); 
    String str = Strings.toLowerCase(sSLSocket.getSession().getCipherSuite());
    if (str.contains("_des_") || str.contains("_des40_") || str.contains("_3des_"))
      throw new IOException("EST clients must not use DES ciphers"); 
    if (Strings.toLowerCase(sSLSocket.getSession().getCipherSuite()).contains("null"))
      throw new IOException("EST clients must not use NULL ciphers"); 
    if (Strings.toLowerCase(sSLSocket.getSession().getCipherSuite()).contains("anon"))
      throw new IOException("EST clients must not use anon ciphers"); 
    if (Strings.toLowerCase(sSLSocket.getSession().getCipherSuite()).contains("export"))
      throw new IOException("EST clients must not use export ciphers"); 
    if (sSLSocket.getSession().getProtocol().equalsIgnoreCase("tlsv1")) {
      try {
        sSLSocket.close();
      } catch (Exception exception) {}
      throw new IOException("EST clients must not use TLSv1");
    } 
    if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(paramString, sSLSocket.getSession()))
      throw new IOException("Hostname was not verified: " + paramString); 
    return new LimitedSSLSocketSource(sSLSocket, this.bindingProvider, this.absoluteLimit);
  }
}
