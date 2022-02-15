package org.bouncycastle.cert.jcajce;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class JcaCertStoreBuilder {
  private List certs = new ArrayList();
  
  private List crls = new ArrayList();
  
  private Object provider;
  
  private JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
  
  private JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();
  
  private String type = "Collection";
  
  public JcaCertStoreBuilder addCertificates(Store paramStore) {
    this.certs.addAll(paramStore.getMatches(null));
    return this;
  }
  
  public JcaCertStoreBuilder addCertificate(X509CertificateHolder paramX509CertificateHolder) {
    this.certs.add(paramX509CertificateHolder);
    return this;
  }
  
  public JcaCertStoreBuilder addCRLs(Store paramStore) {
    this.crls.addAll(paramStore.getMatches(null));
    return this;
  }
  
  public JcaCertStoreBuilder addCRL(X509CRLHolder paramX509CRLHolder) {
    this.crls.add(paramX509CRLHolder);
    return this;
  }
  
  public JcaCertStoreBuilder setProvider(String paramString) {
    this.certificateConverter.setProvider(paramString);
    this.crlConverter.setProvider(paramString);
    this.provider = paramString;
    return this;
  }
  
  public JcaCertStoreBuilder setProvider(Provider paramProvider) {
    this.certificateConverter.setProvider(paramProvider);
    this.crlConverter.setProvider(paramProvider);
    this.provider = paramProvider;
    return this;
  }
  
  public JcaCertStoreBuilder setType(String paramString) {
    this.type = paramString;
    return this;
  }
  
  public CertStore build() throws GeneralSecurityException {
    CollectionCertStoreParameters collectionCertStoreParameters = convertHolders(this.certificateConverter, this.crlConverter);
    return (this.provider instanceof String) ? CertStore.getInstance(this.type, collectionCertStoreParameters, (String)this.provider) : ((this.provider instanceof Provider) ? CertStore.getInstance(this.type, collectionCertStoreParameters, (Provider)this.provider) : CertStore.getInstance(this.type, collectionCertStoreParameters));
  }
  
  private CollectionCertStoreParameters convertHolders(JcaX509CertificateConverter paramJcaX509CertificateConverter, JcaX509CRLConverter paramJcaX509CRLConverter) throws CertificateException, CRLException {
    ArrayList<X509Certificate> arrayList = new ArrayList(this.certs.size() + this.crls.size());
    Iterator<X509CertificateHolder> iterator = this.certs.iterator();
    while (iterator.hasNext())
      arrayList.add(paramJcaX509CertificateConverter.getCertificate(iterator.next())); 
    iterator = this.crls.iterator();
    while (iterator.hasNext())
      arrayList.add(paramJcaX509CRLConverter.getCRL((X509CRLHolder)iterator.next())); 
    return new CollectionCertStoreParameters(arrayList);
  }
}
