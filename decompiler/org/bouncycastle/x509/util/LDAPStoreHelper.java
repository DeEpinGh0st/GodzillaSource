package org.bouncycastle.x509.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.jce.provider.X509AttrCertParser;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.jce.provider.X509CertPairParser;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CRLStoreSelector;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509CertificatePair;

public class LDAPStoreHelper {
  private X509LDAPCertStoreParameters params;
  
  private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
  
  private static String REFERRALS_IGNORE = "ignore";
  
  private static final String SEARCH_SECURITY_LEVEL = "none";
  
  private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
  
  private Map cacheMap = new HashMap<Object, Object>(cacheSize);
  
  private static int cacheSize = 32;
  
  private static long lifeTime = 60000L;
  
  public LDAPStoreHelper(X509LDAPCertStoreParameters paramX509LDAPCertStoreParameters) {
    this.params = paramX509LDAPCertStoreParameters;
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
    int i = str.toLowerCase().indexOf(paramString2.toLowerCase() + "=");
    if (i == -1)
      return ""; 
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
  
  private Set createCerts(List paramList, X509CertStoreSelector paramX509CertStoreSelector) throws StoreException {
    HashSet<X509Certificate> hashSet = new HashSet();
    Iterator<byte[]> iterator = paramList.iterator();
    X509CertParser x509CertParser = new X509CertParser();
    while (iterator.hasNext()) {
      try {
        x509CertParser.engineInit(new ByteArrayInputStream(iterator.next()));
        X509Certificate x509Certificate = (X509Certificate)x509CertParser.engineRead();
        if (paramX509CertStoreSelector.match(x509Certificate))
          hashSet.add(x509Certificate); 
      } catch (Exception exception) {}
    } 
    return hashSet;
  }
  
  private List certSubjectSerialSearch(X509CertStoreSelector paramX509CertStoreSelector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) throws StoreException {
    ArrayList arrayList = new ArrayList();
    String str1 = null;
    String str2 = null;
    str1 = getSubjectAsString(paramX509CertStoreSelector);
    if (paramX509CertStoreSelector.getSerialNumber() != null)
      str2 = paramX509CertStoreSelector.getSerialNumber().toString(); 
    if (paramX509CertStoreSelector.getCertificate() != null) {
      str1 = paramX509CertStoreSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
      str2 = paramX509CertStoreSelector.getCertificate().getSerialNumber().toString();
    } 
    String str3 = null;
    if (str1 != null)
      for (byte b = 0; b < paramArrayOfString3.length; b++) {
        str3 = parseDN(str1, paramArrayOfString3[b]);
        arrayList.addAll(search(paramArrayOfString2, "*" + str3 + "*", paramArrayOfString1));
      }  
    if (str2 != null && this.params.getSearchForSerialNumberIn() != null) {
      str3 = str2;
      arrayList.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), str3, paramArrayOfString1));
    } 
    if (str2 == null && str1 == null)
      arrayList.addAll(search(paramArrayOfString2, "*", paramArrayOfString1)); 
    return arrayList;
  }
  
  private List crossCertificatePairSubjectSearch(X509CertPairStoreSelector paramX509CertPairStoreSelector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) throws StoreException {
    ArrayList arrayList = new ArrayList();
    String str1 = null;
    if (paramX509CertPairStoreSelector.getForwardSelector() != null)
      str1 = getSubjectAsString(paramX509CertPairStoreSelector.getForwardSelector()); 
    if (paramX509CertPairStoreSelector.getCertPair() != null && paramX509CertPairStoreSelector.getCertPair().getForward() != null)
      str1 = paramX509CertPairStoreSelector.getCertPair().getForward().getSubjectX500Principal().getName("RFC1779"); 
    String str2 = null;
    if (str1 != null)
      for (byte b = 0; b < paramArrayOfString3.length; b++) {
        str2 = parseDN(str1, paramArrayOfString3[b]);
        arrayList.addAll(search(paramArrayOfString2, "*" + str2 + "*", paramArrayOfString1));
      }  
    if (str1 == null)
      arrayList.addAll(search(paramArrayOfString2, "*", paramArrayOfString1)); 
    return arrayList;
  }
  
  private List attrCertSubjectSerialSearch(X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) throws StoreException {
    ArrayList arrayList = new ArrayList();
    String str1 = null;
    str2 = null;
    HashSet<String> hashSet = new HashSet();
    Principal[] arrayOfPrincipal = null;
    if (paramX509AttributeCertStoreSelector.getHolder() != null) {
      if (paramX509AttributeCertStoreSelector.getHolder().getSerialNumber() != null)
        hashSet.add(paramX509AttributeCertStoreSelector.getHolder().getSerialNumber().toString()); 
      if (paramX509AttributeCertStoreSelector.getHolder().getEntityNames() != null)
        arrayOfPrincipal = paramX509AttributeCertStoreSelector.getHolder().getEntityNames(); 
    } 
    if (paramX509AttributeCertStoreSelector.getAttributeCert() != null) {
      if (paramX509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames() != null)
        arrayOfPrincipal = paramX509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames(); 
      hashSet.add(paramX509AttributeCertStoreSelector.getAttributeCert().getSerialNumber().toString());
    } 
    if (arrayOfPrincipal != null)
      if (arrayOfPrincipal[0] instanceof X500Principal) {
        str1 = ((X500Principal)arrayOfPrincipal[0]).getName("RFC1779");
      } else {
        str1 = arrayOfPrincipal[0].getName();
      }  
    if (paramX509AttributeCertStoreSelector.getSerialNumber() != null)
      hashSet.add(paramX509AttributeCertStoreSelector.getSerialNumber().toString()); 
    String str3 = null;
    if (str1 != null)
      for (byte b = 0; b < paramArrayOfString3.length; b++) {
        str3 = parseDN(str1, paramArrayOfString3[b]);
        arrayList.addAll(search(paramArrayOfString2, "*" + str3 + "*", paramArrayOfString1));
      }  
    if (hashSet.size() > 0 && this.params.getSearchForSerialNumberIn() != null)
      for (String str2 : hashSet)
        arrayList.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), str2, paramArrayOfString1));  
    if (hashSet.size() == 0 && str1 == null)
      arrayList.addAll(search(paramArrayOfString2, "*", paramArrayOfString1)); 
    return arrayList;
  }
  
  private List cRLIssuerSearch(X509CRLStoreSelector paramX509CRLStoreSelector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) throws StoreException {
    ArrayList arrayList = new ArrayList();
    String str = null;
    HashSet<X500Principal> hashSet = new HashSet();
    if (paramX509CRLStoreSelector.getIssuers() != null)
      hashSet.addAll(paramX509CRLStoreSelector.getIssuers()); 
    if (paramX509CRLStoreSelector.getCertificateChecking() != null)
      hashSet.add(getCertificateIssuer(paramX509CRLStoreSelector.getCertificateChecking())); 
    if (paramX509CRLStoreSelector.getAttrCertificateChecking() != null) {
      Principal[] arrayOfPrincipal = paramX509CRLStoreSelector.getAttrCertificateChecking().getIssuer().getPrincipals();
      for (byte b = 0; b < arrayOfPrincipal.length; b++) {
        if (arrayOfPrincipal[b] instanceof X500Principal)
          hashSet.add(arrayOfPrincipal[b]); 
      } 
    } 
    Iterator<X500Principal> iterator = hashSet.iterator();
    while (iterator.hasNext()) {
      str = ((X500Principal)iterator.next()).getName("RFC1779");
      String str1 = null;
      for (byte b = 0; b < paramArrayOfString3.length; b++) {
        str1 = parseDN(str, paramArrayOfString3[b]);
        arrayList.addAll(search(paramArrayOfString2, "*" + str1 + "*", paramArrayOfString1));
      } 
    } 
    if (str == null)
      arrayList.addAll(search(paramArrayOfString2, "*", paramArrayOfString1)); 
    return arrayList;
  }
  
  private List search(String[] paramArrayOfString1, String paramString, String[] paramArrayOfString2) throws StoreException {
    String str1 = null;
    if (paramArrayOfString1 == null) {
      str1 = null;
    } else {
      str1 = "";
      if (paramString.equals("**"))
        paramString = "*"; 
      for (byte b1 = 0; b1 < paramArrayOfString1.length; b1++)
        str1 = str1 + "(" + paramArrayOfString1[b1] + "=" + paramString + ")"; 
      str1 = "(|" + str1 + ")";
    } 
    String str2 = "";
    for (byte b = 0; b < paramArrayOfString2.length; b++)
      str2 = str2 + "(" + paramArrayOfString2[b] + "=*)"; 
    str2 = "(|" + str2 + ")";
    String str3 = "(&" + str1 + "" + str2 + ")";
    if (str1 == null)
      str3 = str2; 
    List list = getFromCache(str3);
    if (list != null)
      return list; 
    DirContext dirContext = null;
    list = new ArrayList();
    try {
      dirContext = connectLDAP();
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(2);
      searchControls.setCountLimit(0L);
      searchControls.setReturningAttributes(paramArrayOfString2);
      NamingEnumeration<SearchResult> namingEnumeration = dirContext.search(this.params.getBaseDN(), str3, searchControls);
      while (namingEnumeration.hasMoreElements()) {
        SearchResult searchResult = namingEnumeration.next();
        NamingEnumeration<?> namingEnumeration1 = ((Attribute)searchResult.getAttributes().getAll().next()).getAll();
        while (namingEnumeration1.hasMore())
          list.add(namingEnumeration1.next()); 
      } 
      addToCache(str3, list);
    } catch (NamingException namingException) {
      try {
        if (null != dirContext)
          dirContext.close(); 
      } catch (Exception exception) {}
    } finally {
      try {
        if (null != dirContext)
          dirContext.close(); 
      } catch (Exception exception) {}
    } 
    return list;
  }
  
  private Set createCRLs(List paramList, X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    HashSet<X509CRL> hashSet = new HashSet();
    X509CRLParser x509CRLParser = new X509CRLParser();
    Iterator<byte[]> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      try {
        x509CRLParser.engineInit(new ByteArrayInputStream(iterator.next()));
        X509CRL x509CRL = (X509CRL)x509CRLParser.engineRead();
        if (paramX509CRLStoreSelector.match(x509CRL))
          hashSet.add(x509CRL); 
      } catch (StreamParsingException streamParsingException) {}
    } 
    return hashSet;
  }
  
  private Set createCrossCertificatePairs(List<byte[]> paramList, X509CertPairStoreSelector paramX509CertPairStoreSelector) throws StoreException {
    HashSet<X509CertificatePair> hashSet = new HashSet();
    for (byte b = 0; b < paramList.size(); b++) {
      try {
        X509CertificatePair x509CertificatePair;
        try {
          X509CertPairParser x509CertPairParser = new X509CertPairParser();
          x509CertPairParser.engineInit(new ByteArrayInputStream(paramList.get(b)));
          x509CertificatePair = (X509CertificatePair)x509CertPairParser.engineRead();
        } catch (StreamParsingException streamParsingException) {
          byte[] arrayOfByte1 = paramList.get(b);
          byte[] arrayOfByte2 = paramList.get(b + 1);
          x509CertificatePair = new X509CertificatePair(new CertificatePair(Certificate.getInstance((new ASN1InputStream(arrayOfByte1)).readObject()), Certificate.getInstance((new ASN1InputStream(arrayOfByte2)).readObject())));
          b++;
        } 
        if (paramX509CertPairStoreSelector.match(x509CertificatePair))
          hashSet.add(x509CertificatePair); 
      } catch (CertificateParsingException certificateParsingException) {
      
      } catch (IOException iOException) {}
    } 
    return hashSet;
  }
  
  private Set createAttributeCertificates(List paramList, X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector) throws StoreException {
    HashSet<X509AttributeCertificate> hashSet = new HashSet();
    Iterator<byte[]> iterator = paramList.iterator();
    X509AttrCertParser x509AttrCertParser = new X509AttrCertParser();
    while (iterator.hasNext()) {
      try {
        x509AttrCertParser.engineInit(new ByteArrayInputStream(iterator.next()));
        X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)x509AttrCertParser.engineRead();
        if (paramX509AttributeCertStoreSelector.match(x509AttributeCertificate))
          hashSet.add(x509AttributeCertificate); 
      } catch (StreamParsingException streamParsingException) {}
    } 
    return hashSet;
  }
  
  public Collection getAuthorityRevocationLists(X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAuthorityRevocationListAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAuthorityRevocationListAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAuthorityRevocationListIssuerAttributeName());
    List list = cRLIssuerSearch(paramX509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCRLs(list, paramX509CRLStoreSelector);
    if (set.size() == 0) {
      X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
      list = cRLIssuerSearch(x509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCRLs(list, paramX509CRLStoreSelector));
    } 
    return set;
  }
  
  public Collection getAttributeCertificateRevocationLists(X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAttributeCertificateRevocationListAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAttributeCertificateRevocationListAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAttributeCertificateRevocationListIssuerAttributeName());
    List list = cRLIssuerSearch(paramX509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCRLs(list, paramX509CRLStoreSelector);
    if (set.size() == 0) {
      X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
      list = cRLIssuerSearch(x509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCRLs(list, paramX509CRLStoreSelector));
    } 
    return set;
  }
  
  public Collection getAttributeAuthorityRevocationLists(X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAttributeAuthorityRevocationListAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAttributeAuthorityRevocationListAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAttributeAuthorityRevocationListIssuerAttributeName());
    List list = cRLIssuerSearch(paramX509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCRLs(list, paramX509CRLStoreSelector);
    if (set.size() == 0) {
      X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
      list = cRLIssuerSearch(x509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCRLs(list, paramX509CRLStoreSelector));
    } 
    return set;
  }
  
  public Collection getCrossCertificatePairs(X509CertPairStoreSelector paramX509CertPairStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getCrossCertificateAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapCrossCertificateAttributeName());
    String[] arrayOfString3 = splitString(this.params.getCrossCertificateSubjectAttributeName());
    List list = crossCertificatePairSubjectSearch(paramX509CertPairStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCrossCertificatePairs(list, paramX509CertPairStoreSelector);
    if (set.size() == 0) {
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      X509CertPairStoreSelector x509CertPairStoreSelector = new X509CertPairStoreSelector();
      x509CertPairStoreSelector.setForwardSelector(x509CertStoreSelector);
      x509CertPairStoreSelector.setReverseSelector(x509CertStoreSelector);
      list = crossCertificatePairSubjectSearch(x509CertPairStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCrossCertificatePairs(list, paramX509CertPairStoreSelector));
    } 
    return set;
  }
  
  public Collection getUserCertificates(X509CertStoreSelector paramX509CertStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getUserCertificateAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapUserCertificateAttributeName());
    String[] arrayOfString3 = splitString(this.params.getUserCertificateSubjectAttributeName());
    List list = certSubjectSerialSearch(paramX509CertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCerts(list, paramX509CertStoreSelector);
    if (set.size() == 0) {
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      list = certSubjectSerialSearch(x509CertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCerts(list, paramX509CertStoreSelector));
    } 
    return set;
  }
  
  public Collection getAACertificates(X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAACertificateAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAACertificateAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAACertificateSubjectAttributeName());
    List list = attrCertSubjectSerialSearch(paramX509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createAttributeCertificates(list, paramX509AttributeCertStoreSelector);
    if (set.size() == 0) {
      X509AttributeCertStoreSelector x509AttributeCertStoreSelector = new X509AttributeCertStoreSelector();
      list = attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createAttributeCertificates(list, paramX509AttributeCertStoreSelector));
    } 
    return set;
  }
  
  public Collection getAttributeDescriptorCertificates(X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAttributeDescriptorCertificateAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAttributeDescriptorCertificateAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAttributeDescriptorCertificateSubjectAttributeName());
    List list = attrCertSubjectSerialSearch(paramX509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createAttributeCertificates(list, paramX509AttributeCertStoreSelector);
    if (set.size() == 0) {
      X509AttributeCertStoreSelector x509AttributeCertStoreSelector = new X509AttributeCertStoreSelector();
      list = attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createAttributeCertificates(list, paramX509AttributeCertStoreSelector));
    } 
    return set;
  }
  
  public Collection getCACertificates(X509CertStoreSelector paramX509CertStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getCACertificateAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapCACertificateAttributeName());
    String[] arrayOfString3 = splitString(this.params.getCACertificateSubjectAttributeName());
    List list = certSubjectSerialSearch(paramX509CertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCerts(list, paramX509CertStoreSelector);
    if (set.size() == 0) {
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      list = certSubjectSerialSearch(x509CertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCerts(list, paramX509CertStoreSelector));
    } 
    return set;
  }
  
  public Collection getDeltaCertificateRevocationLists(X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getDeltaRevocationListAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapDeltaRevocationListAttributeName());
    String[] arrayOfString3 = splitString(this.params.getDeltaRevocationListIssuerAttributeName());
    List list = cRLIssuerSearch(paramX509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCRLs(list, paramX509CRLStoreSelector);
    if (set.size() == 0) {
      X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
      list = cRLIssuerSearch(x509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCRLs(list, paramX509CRLStoreSelector));
    } 
    return set;
  }
  
  public Collection getAttributeCertificateAttributes(X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getAttributeCertificateAttributeAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapAttributeCertificateAttributeAttributeName());
    String[] arrayOfString3 = splitString(this.params.getAttributeCertificateAttributeSubjectAttributeName());
    List list = attrCertSubjectSerialSearch(paramX509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createAttributeCertificates(list, paramX509AttributeCertStoreSelector);
    if (set.size() == 0) {
      X509AttributeCertStoreSelector x509AttributeCertStoreSelector = new X509AttributeCertStoreSelector();
      list = attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createAttributeCertificates(list, paramX509AttributeCertStoreSelector));
    } 
    return set;
  }
  
  public Collection getCertificateRevocationLists(X509CRLStoreSelector paramX509CRLStoreSelector) throws StoreException {
    String[] arrayOfString1 = splitString(this.params.getCertificateRevocationListAttribute());
    String[] arrayOfString2 = splitString(this.params.getLdapCertificateRevocationListAttributeName());
    String[] arrayOfString3 = splitString(this.params.getCertificateRevocationListIssuerAttributeName());
    List list = cRLIssuerSearch(paramX509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
    Set set = createCRLs(list, paramX509CRLStoreSelector);
    if (set.size() == 0) {
      X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
      list = cRLIssuerSearch(x509CRLStoreSelector, arrayOfString1, arrayOfString2, arrayOfString3);
      set.addAll(createCRLs(list, paramX509CRLStoreSelector));
    } 
    return set;
  }
  
  private synchronized void addToCache(String paramString, List paramList) {
    Date date = new Date(System.currentTimeMillis());
    ArrayList<Date> arrayList = new ArrayList();
    arrayList.add(date);
    arrayList.add(paramList);
    if (this.cacheMap.containsKey(paramString)) {
      this.cacheMap.put(paramString, arrayList);
    } else {
      if (this.cacheMap.size() >= cacheSize) {
        Iterator<Map.Entry> iterator = this.cacheMap.entrySet().iterator();
        long l = date.getTime();
        Object object = null;
        while (iterator.hasNext()) {
          Map.Entry entry = iterator.next();
          long l1 = ((Date)((List<Date>)entry.getValue()).get(0)).getTime();
          if (l1 < l) {
            l = l1;
            object = entry.getKey();
          } 
        } 
        this.cacheMap.remove(object);
      } 
      this.cacheMap.put(paramString, arrayList);
    } 
  }
  
  private List getFromCache(String paramString) {
    List<Date> list = (List)this.cacheMap.get(paramString);
    long l = System.currentTimeMillis();
    return (list != null) ? ((((Date)list.get(0)).getTime() < l - lifeTime) ? null : (List)list.get(1)) : null;
  }
  
  private String[] splitString(String paramString) {
    return paramString.split("\\s+");
  }
  
  private String getSubjectAsString(X509CertStoreSelector paramX509CertStoreSelector) {
    try {
      byte[] arrayOfByte = paramX509CertStoreSelector.getSubjectAsBytes();
      if (arrayOfByte != null)
        return (new X500Principal(arrayOfByte)).getName("RFC1779"); 
    } catch (IOException iOException) {
      throw new StoreException("exception processing name: " + iOException.getMessage(), iOException);
    } 
    return null;
  }
  
  private X500Principal getCertificateIssuer(X509Certificate paramX509Certificate) {
    return paramX509Certificate.getIssuerX500Principal();
  }
}
