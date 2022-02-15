package org.bouncycastle.est.jcajce;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;

public class JcaJceUtils {
  public static X509TrustManager getTrustAllTrustManager() {
    return new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {}
        
        public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {}
        
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      };
  }
  
  public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> anchors, final CRL[] revocationLists) {
    final X509Certificate[] x509CertificateTrustAnchors = new X509Certificate[anchors.size()];
    byte b = 0;
    for (TrustAnchor trustAnchor : anchors)
      arrayOfX509Certificate[b++] = trustAnchor.getTrustedCert(); 
    return new X509TrustManager[] { new X509TrustManager() {
          public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {}
          
          public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {
            try {
              CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList((Object[])param1ArrayOfX509Certificate)), "BC");
              CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX", "BC");
              X509CertSelector x509CertSelector = new X509CertSelector();
              x509CertSelector.setCertificate(param1ArrayOfX509Certificate[0]);
              PKIXBuilderParameters pKIXBuilderParameters = new PKIXBuilderParameters(anchors, x509CertSelector);
              pKIXBuilderParameters.addCertStore(certStore);
              if (revocationLists != null) {
                pKIXBuilderParameters.setRevocationEnabled(true);
                pKIXBuilderParameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList((Object[])revocationLists))));
              } else {
                pKIXBuilderParameters.setRevocationEnabled(false);
              } 
              PKIXCertPathValidatorResult pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)certPathBuilder.build(pKIXBuilderParameters);
              JcaJceUtils.validateServerCertUsage(param1ArrayOfX509Certificate[0]);
            } catch (CertificateException certificateException) {
              throw certificateException;
            } catch (GeneralSecurityException generalSecurityException) {
              throw new CertificateException("unable to process certificates: " + generalSecurityException.getMessage(), generalSecurityException);
            } 
          }
          
          public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] arrayOfX509Certificate = new X509Certificate[x509CertificateTrustAnchors.length];
            System.arraycopy(x509CertificateTrustAnchors, 0, arrayOfX509Certificate, 0, arrayOfX509Certificate.length);
            return arrayOfX509Certificate;
          }
        } };
  }
  
  public static void validateServerCertUsage(X509Certificate paramX509Certificate) throws CertificateException {
    try {
      X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(paramX509Certificate.getEncoded());
      KeyUsage keyUsage = KeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
      if (keyUsage != null) {
        if (keyUsage.hasUsages(4))
          throw new CertificateException("Key usage must not contain keyCertSign"); 
        if (!keyUsage.hasUsages(128) && !keyUsage.hasUsages(32))
          throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment"); 
      } 
      ExtendedKeyUsage extendedKeyUsage = ExtendedKeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
      if (extendedKeyUsage != null && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth) && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC) && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC))
        throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC"); 
    } catch (CertificateException certificateException) {
      throw certificateException;
    } catch (Exception exception) {
      throw new CertificateException(exception.getMessage(), exception);
    } 
  }
  
  public static KeyManagerFactory createKeyManagerFactory(String paramString1, String paramString2, KeyStore paramKeyStore, char[] paramArrayOfchar) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
    KeyManagerFactory keyManagerFactory = null;
    if (paramString1 == null && paramString2 == null) {
      keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    } else if (paramString2 == null) {
      keyManagerFactory = KeyManagerFactory.getInstance(paramString1);
    } else {
      keyManagerFactory = KeyManagerFactory.getInstance(paramString1, paramString2);
    } 
    keyManagerFactory.init(paramKeyStore, paramArrayOfchar);
    return keyManagerFactory;
  }
}
