package org.bouncycastle.jce.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;

public class X509LDAPCertStoreSpi extends CertStoreSpi {
  private X509LDAPCertStoreParameters params;
  
  private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
  
  private static String REFERRALS_IGNORE = "ignore";
  
  private static final String SEARCH_SECURITY_LEVEL = "none";
  
  private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
  
  public X509LDAPCertStoreSpi(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof X509LDAPCertStoreParameters))
      throw new InvalidAlgorithmParameterException(X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + paramCertStoreParameters.toString()); 
    this.params = (X509LDAPCertStoreParameters)paramCertStoreParameters;
  }
  
  private DirContext connectLDAP() throws NamingException {
    Properties properties = new Properties();
    properties.setProperty("java.naming.factory.initial", LDAP_PROVIDER);
    properties.setProperty("java.naming.batchsize", "0");
    properties.setProperty("java.naming.provider.url", this.params.getLdapURL());
    properties.setProperty("java.naming.factory.url.pkgs", "com.sun.jndi.url");
    properties.setProperty("java.naming.referral", REFERRALS_IGNORE);
    properties.setProperty("java.naming.security.authentication", "none");
    return new InitialDirContext(properties);
  }
  
  private String parseDN(String paramString1, String paramString2) {
    String str = paramString1;
    int i = str.toLowerCase().indexOf(paramString2.toLowerCase());
    str = str.substring(i + paramString2.length());
    int j = str.indexOf(',');
    if (j == -1)
      j = str.length(); 
    while (str.charAt(j - 1) == '\\') {
      j = str.indexOf(',', j + 1);
      if (j == -1)
        j = str.length(); 
    } 
    str = str.substring(0, j);
    i = str.indexOf('=');
    str = str.substring(i + 1);
    if (str.charAt(0) == ' ')
      str = str.substring(1); 
    if (str.startsWith("\""))
      str = str.substring(1); 
    if (str.endsWith("\""))
      str = str.substring(0, str.length() - 1); 
    return str;
  }
  
  public Collection engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    if (!(paramCertSelector instanceof X509CertSelector))
      throw new CertStoreException("selector is not a X509CertSelector"); 
    X509CertSelector x509CertSelector = (X509CertSelector)paramCertSelector;
    HashSet<Certificate> hashSet = new HashSet();
    Set set = getEndCertificates(x509CertSelector);
    set.addAll(getCACertificates(x509CertSelector));
    set.addAll(getCrossCertificates(x509CertSelector));
    Iterator<byte[]> iterator = set.iterator();
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
      while (iterator.hasNext()) {
        byte[] arrayOfByte = iterator.next();
        if (arrayOfByte == null || arrayOfByte.length == 0)
          continue; 
        ArrayList<byte[]> arrayList = new ArrayList();
        arrayList.add(arrayOfByte);
        try {
          CertificatePair certificatePair = CertificatePair.getInstance((new ASN1InputStream(arrayOfByte)).readObject());
          arrayList.clear();
          if (certificatePair.getForward() != null)
            arrayList.add(certificatePair.getForward().getEncoded()); 
          if (certificatePair.getReverse() != null)
            arrayList.add(certificatePair.getReverse().getEncoded()); 
        } catch (IOException iOException) {
        
        } catch (IllegalArgumentException illegalArgumentException) {}
        Iterator<byte> iterator1 = arrayList.iterator();
        while (iterator1.hasNext()) {
          ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[])iterator1.next());
          try {
            Certificate certificate = certificateFactory.generateCertificate(byteArrayInputStream);
            if (x509CertSelector.match(certificate))
              hashSet.add(certificate); 
          } catch (Exception exception) {}
        } 
      } 
    } catch (Exception exception) {
      throw new CertStoreException("certificate cannot be constructed from LDAP result: " + exception);
    } 
    return hashSet;
  }
  
  private Set certSubjectSerialSearch(X509CertSelector paramX509CertSelector, String[] paramArrayOfString, String paramString1, String paramString2) throws CertStoreException {
    HashSet hashSet = new HashSet();
    try {
      if (paramX509CertSelector.getSubjectAsBytes() != null || paramX509CertSelector.getSubjectAsString() != null || paramX509CertSelector.getCertificate() != null) {
        String str1 = null;
        String str2 = null;
        if (paramX509CertSelector.getCertificate() != null) {
          str1 = paramX509CertSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
          str2 = paramX509CertSelector.getCertificate().getSerialNumber().toString();
        } else if (paramX509CertSelector.getSubjectAsBytes() != null) {
          str1 = (new X500Principal(paramX509CertSelector.getSubjectAsBytes())).getName("RFC1779");
        } else {
          str1 = paramX509CertSelector.getSubjectAsString();
        } 
        String str3 = parseDN(str1, paramString2);
        hashSet.addAll(search(paramString1, "*" + str3 + "*", paramArrayOfString));
        if (str2 != null && this.params.getSearchForSerialNumberIn() != null) {
          str3 = str2;
          paramString1 = this.params.getSearchForSerialNumberIn();
          hashSet.addAll(search(paramString1, "*" + str3 + "*", paramArrayOfString));
        } 
      } else {
        hashSet.addAll(search(paramString1, "*", paramArrayOfString));
      } 
    } catch (IOException iOException) {
      throw new CertStoreException("exception processing selector: " + iOException);
    } 
    return hashSet;
  }
  
  private Set getEndCertificates(X509CertSelector paramX509CertSelector) throws CertStoreException {
    String[] arrayOfString = { this.params.getUserCertificateAttribute() };
    String str1 = this.params.getLdapUserCertificateAttributeName();
    String str2 = this.params.getUserCertificateSubjectAttributeName();
    return certSubjectSerialSearch(paramX509CertSelector, arrayOfString, str1, str2);
  }
  
  private Set getCACertificates(X509CertSelector paramX509CertSelector) throws CertStoreException {
    String[] arrayOfString = { this.params.getCACertificateAttribute() };
    String str1 = this.params.getLdapCACertificateAttributeName();
    String str2 = this.params.getCACertificateSubjectAttributeName();
    Set set = certSubjectSerialSearch(paramX509CertSelector, arrayOfString, str1, str2);
    if (set.isEmpty())
      set.addAll(search(null, "*", arrayOfString)); 
    return set;
  }
  
  private Set getCrossCertificates(X509CertSelector paramX509CertSelector) throws CertStoreException {
    String[] arrayOfString = { this.params.getCrossCertificateAttribute() };
    String str1 = this.params.getLdapCrossCertificateAttributeName();
    String str2 = this.params.getCrossCertificateSubjectAttributeName();
    Set set = certSubjectSerialSearch(paramX509CertSelector, arrayOfString, str1, str2);
    if (set.isEmpty())
      set.addAll(search(null, "*", arrayOfString)); 
    return set;
  }
  
  public Collection engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    String[] arrayOfString = { this.params.getCertificateRevocationListAttribute() };
    if (!(paramCRLSelector instanceof X509CRLSelector))
      throw new CertStoreException("selector is not a X509CRLSelector"); 
    X509CRLSelector x509CRLSelector = (X509CRLSelector)paramCRLSelector;
    HashSet<CRL> hashSet = new HashSet();
    String str = this.params.getLdapCertificateRevocationListAttributeName();
    HashSet hashSet1 = new HashSet();
    if (x509CRLSelector.getIssuerNames() != null) {
      for (String str1 : x509CRLSelector.getIssuerNames()) {
        String str2 = null;
        if (str1 instanceof String) {
          String str3 = this.params.getCertificateRevocationListIssuerAttributeName();
          str2 = parseDN(str1, str3);
        } else {
          String str3 = this.params.getCertificateRevocationListIssuerAttributeName();
          str2 = parseDN((new X500Principal((byte[])str1)).getName("RFC1779"), str3);
        } 
        hashSet1.addAll(search(str, "*" + str2 + "*", arrayOfString));
      } 
    } else {
      hashSet1.addAll(search(str, "*", arrayOfString));
    } 
    hashSet1.addAll(search(null, "*", arrayOfString));
    Iterator<byte[]> iterator = hashSet1.iterator();
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
      while (iterator.hasNext()) {
        CRL cRL = certificateFactory.generateCRL(new ByteArrayInputStream(iterator.next()));
        if (x509CRLSelector.match(cRL))
          hashSet.add(cRL); 
      } 
    } catch (Exception exception) {
      throw new CertStoreException("CRL cannot be constructed from LDAP result " + exception);
    } 
    return hashSet;
  }
  
  private Set search(String paramString1, String paramString2, String[] paramArrayOfString) throws CertStoreException {
    String str = paramString1 + "=" + paramString2;
    if (paramString1 == null)
      str = null; 
    DirContext dirContext = null;
    HashSet<Object> hashSet = new HashSet();
    try {
      dirContext = connectLDAP();
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(2);
      searchControls.setCountLimit(0L);
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = paramArrayOfString[b];
        searchControls.setReturningAttributes(arrayOfString);
        String str1 = "(&(" + str + ")(" + arrayOfString[0] + "=*))";
        if (str == null)
          str1 = "(" + arrayOfString[0] + "=*)"; 
        NamingEnumeration<SearchResult> namingEnumeration = dirContext.search(this.params.getBaseDN(), str1, searchControls);
        while (namingEnumeration.hasMoreElements()) {
          SearchResult searchResult = namingEnumeration.next();
          NamingEnumeration<?> namingEnumeration1 = ((Attribute)searchResult.getAttributes().getAll().next()).getAll();
          while (namingEnumeration1.hasMore()) {
            Object object = namingEnumeration1.next();
            hashSet.add(object);
          } 
        } 
      } 
    } catch (Exception exception) {
      throw new CertStoreException("Error getting results from LDAP directory " + exception);
    } finally {
      try {
        if (null != dirContext)
          dirContext.close(); 
      } catch (Exception exception) {}
    } 
    return hashSet;
  }
}
