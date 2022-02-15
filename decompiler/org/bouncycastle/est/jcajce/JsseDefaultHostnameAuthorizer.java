package org.bouncycastle.est.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLSession;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.util.Strings;

public class JsseDefaultHostnameAuthorizer implements JsseHostnameAuthorizer {
  private final Set<String> knownSuffixes;
  
  public JsseDefaultHostnameAuthorizer(Set<String> paramSet) {
    this.knownSuffixes = paramSet;
  }
  
  public boolean verified(String paramString, SSLSession paramSSLSession) throws IOException {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
      X509Certificate x509Certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(paramSSLSession.getPeerCertificates()[0].getEncoded()));
      return verify(paramString, x509Certificate);
    } catch (Exception exception) {
      if (exception instanceof ESTException)
        throw (ESTException)exception; 
      throw new ESTException(exception.getMessage(), exception);
    } 
  }
  
  public boolean verify(String paramString, X509Certificate paramX509Certificate) throws IOException {
    try {
      Collection<List<?>> collection = paramX509Certificate.getSubjectAlternativeNames();
      if (collection != null) {
        for (List<E> list : collection) {
          switch (((Number)list.get(0)).intValue()) {
            case 2:
              if (isValidNameMatch(paramString, list.get(1).toString(), this.knownSuffixes))
                return true; 
              continue;
            case 7:
              if (InetAddress.getByName(paramString).equals(InetAddress.getByName(list.get(1).toString())))
                return true; 
              continue;
          } 
          throw new RuntimeException("Unable to handle ");
        } 
        return false;
      } 
    } catch (Exception exception) {
      throw new ESTException(exception.getMessage(), exception);
    } 
    if (paramX509Certificate.getSubjectX500Principal() == null)
      return false; 
    RDN[] arrayOfRDN = X500Name.getInstance(paramX509Certificate.getSubjectX500Principal().getEncoded()).getRDNs();
    for (byte b = 0; b != arrayOfRDN.length; b++) {
      RDN rDN = arrayOfRDN[b];
      AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = rDN.getTypesAndValues();
      for (byte b1 = 0; b1 != arrayOfAttributeTypeAndValue.length; b1++) {
        AttributeTypeAndValue attributeTypeAndValue = arrayOfAttributeTypeAndValue[b1];
        if (attributeTypeAndValue.getType().equals(BCStyle.CN))
          return isValidNameMatch(paramString, rDN.getFirst().getValue().toString(), this.knownSuffixes); 
      } 
    } 
    return false;
  }
  
  public static boolean isValidNameMatch(String paramString1, String paramString2, Set<String> paramSet) throws IOException {
    if (paramString2.contains("*")) {
      int i = paramString2.indexOf('*');
      if (i == paramString2.lastIndexOf("*")) {
        if (paramString2.contains("..") || paramString2.charAt(paramString2.length() - 1) == '*')
          return false; 
        int j = paramString2.indexOf('.', i);
        if (paramSet != null && paramSet.contains(Strings.toLowerCase(paramString2.substring(j))))
          throw new IOException("Wildcard `" + paramString2 + "` matches known public suffix."); 
        String str1 = Strings.toLowerCase(paramString2.substring(i + 1));
        String str2 = Strings.toLowerCase(paramString1);
        if (str2.equals(str1))
          return false; 
        if (str1.length() > str2.length())
          return false; 
        if (i > 0)
          return (str2.startsWith(paramString2.substring(0, i - 1)) && str2.endsWith(str1)) ? ((str2.substring(i, str2.length() - str1.length()).indexOf('.') < 0)) : false; 
        String str3 = str2.substring(0, str2.length() - str1.length());
        return (str3.indexOf('.') > 0) ? false : str2.endsWith(str1);
      } 
      return false;
    } 
    return paramString1.equalsIgnoreCase(paramString2);
  }
}
