package org.bouncycastle.pqc.jcajce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class BouncyCastlePQCProvider extends Provider implements ConfigurableProvider {
  private static String info = "BouncyCastle Post-Quantum Security Provider v1.58";
  
  public static String PROVIDER_NAME = "BCPQC";
  
  public static final ProviderConfiguration CONFIGURATION = null;
  
  private static final Map keyInfoConverters = new HashMap<Object, Object>();
  
  private static final String ALGORITHM_PACKAGE = "org.bouncycastle.pqc.jcajce.provider.";
  
  private static final String[] ALGORITHMS = new String[] { "Rainbow", "McEliece", "SPHINCS", "NH", "XMSS" };
  
  public BouncyCastlePQCProvider() {
    super(PROVIDER_NAME, 1.58D, info);
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            BouncyCastlePQCProvider.this.setup();
            return null;
          }
        });
  }
  
  private void setup() {
    loadAlgorithms("org.bouncycastle.pqc.jcajce.provider.", ALGORITHMS);
  }
  
  private void loadAlgorithms(String paramString, String[] paramArrayOfString) {
    for (byte b = 0; b != paramArrayOfString.length; b++) {
      Class<AlgorithmProvider> clazz = loadClass(BouncyCastlePQCProvider.class, paramString + paramArrayOfString[b] + "$Mappings");
      if (clazz != null)
        try {
          ((AlgorithmProvider)clazz.newInstance()).configure(this);
        } catch (Exception exception) {
          throw new InternalError("cannot create instance of " + paramString + paramArrayOfString[b] + "$Mappings : " + exception);
        }  
    } 
  }
  
  public void setParameter(String paramString, Object paramObject) {
    synchronized (CONFIGURATION) {
    
    } 
  }
  
  public boolean hasAlgorithm(String paramString1, String paramString2) {
    return (containsKey(paramString1 + "." + paramString2) || containsKey("Alg.Alias." + paramString1 + "." + paramString2));
  }
  
  public void addAlgorithm(String paramString1, String paramString2) {
    if (containsKey(paramString1))
      throw new IllegalStateException("duplicate provider key (" + paramString1 + ") found"); 
    put(paramString1, paramString2);
  }
  
  public void addAlgorithm(String paramString1, ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString2) {
    if (!containsKey(paramString1 + "." + paramString2))
      throw new IllegalStateException("primary key (" + paramString1 + "." + paramString2 + ") not found"); 
    addAlgorithm(paramString1 + "." + paramASN1ObjectIdentifier, paramString2);
    addAlgorithm(paramString1 + ".OID." + paramASN1ObjectIdentifier, paramString2);
  }
  
  public void addKeyInfoConverter(ASN1ObjectIdentifier paramASN1ObjectIdentifier, AsymmetricKeyInfoConverter paramAsymmetricKeyInfoConverter) {
    synchronized (keyInfoConverters) {
      keyInfoConverters.put(paramASN1ObjectIdentifier, paramAsymmetricKeyInfoConverter);
    } 
  }
  
  public void addAttributes(String paramString, Map<String, String> paramMap) {
    for (String str1 : paramMap.keySet()) {
      String str2 = paramString + " " + str1;
      if (containsKey(str2))
        throw new IllegalStateException("duplicate provider attribute key (" + str2 + ") found"); 
      put(str2, paramMap.get(str1));
    } 
  }
  
  private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    synchronized (keyInfoConverters) {
      return (AsymmetricKeyInfoConverter)keyInfoConverters.get(paramASN1ObjectIdentifier);
    } 
  }
  
  public static PublicKey getPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm());
    return (asymmetricKeyInfoConverter == null) ? null : asymmetricKeyInfoConverter.generatePublic(paramSubjectPublicKeyInfo);
  }
  
  public static PrivateKey getPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = getAsymmetricKeyInfoConverter(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
    return (asymmetricKeyInfoConverter == null) ? null : asymmetricKeyInfoConverter.generatePrivate(paramPrivateKeyInfo);
  }
  
  static Class loadClass(Class paramClass, final String className) {
    try {
      ClassLoader classLoader = paramClass.getClassLoader();
      return (classLoader != null) ? classLoader.loadClass(className) : AccessController.<Class<?>>doPrivileged(new PrivilegedAction<Class<?>>() {
            public Object run() {
              try {
                return Class.forName(className);
              } catch (Exception exception) {
                return null;
              } 
            }
          });
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
  }
}
