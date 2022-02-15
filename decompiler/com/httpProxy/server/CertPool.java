package com.httpProxy.server;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.WeakHashMap;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class CertPool {
  private PrivateKey privateKey;
  private X509Certificate ca;
  private KeyPair keyPair;
  Map<String, X509Certificate> cacehep = new WeakHashMap<>();
  
  public CertPool(PrivateKey privateKey, X509Certificate ca) {
    this.privateKey = privateKey;
    this.ca = ca;
    try {
      this.keyPair = CertUtil.genKeyPair();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  
  public SSLContext getSslContext(String host) {
    int index = -1;
    if ((index = host.indexOf(":")) != -1) {
      host = host.substring(0, index);
    }
    
    X509Certificate x509Certificate = null;
    try {
      x509Certificate = this.cacehep.get(host);
      if (x509Certificate == null) {
        x509Certificate = CertUtil.genCert(CertUtil.getSubject(this.ca), this.privateKey, this.ca
            .getNotBefore(), this.ca.getNotAfter(), this.keyPair
            .getPublic(), new String[] { host });
        this.cacehep.put(host, x509Certificate);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return getSslContext(this.keyPair.getPrivate(), new X509Certificate[] { x509Certificate });
  }

  
  private SSLContext getSslContext(PrivateKey key, X509Certificate... keyCertChain) {
    try {
      KeyStore ks = KeyStore.getInstance("jks");
      ks.load((InputStream)null, (char[])null);
      
      ks.setKeyEntry("key", key, new char[0], (Certificate[])keyCertChain);
      
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, new char[0]);
      
      SSLContext ctx = SSLContext.getInstance("TLS");
      
      ctx.init(kmf.getKeyManagers(), null, null);

      
      return ctx;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
}
