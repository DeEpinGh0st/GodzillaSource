package org.bouncycastle.jce.provider;

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
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;

public final class BouncyCastleProvider extends Provider implements ConfigurableProvider {
  private static String info = "BouncyCastle Security Provider v1.58";
  
  public static final String PROVIDER_NAME = "BC";
  
  public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
  
  private static final Map keyInfoConverters = new HashMap<Object, Object>();
  
  private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
  
  private static final String[] SYMMETRIC_GENERIC = new String[] { "PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF" };
  
  private static final String[] SYMMETRIC_MACS = new String[] { "SipHash", "Poly1305" };
  
  private static final String[] SYMMETRIC_CIPHERS = new String[] { 
      "AES", "ARC4", "ARIA", "Blowfish", "Camellia", "CAST5", "CAST6", "ChaCha", "DES", "DESede", 
      "GOST28147", "Grainv1", "Grain128", "HC128", "HC256", "IDEA", "Noekeon", "RC2", "RC5", "RC6", 
      "Rijndael", "Salsa20", "SEED", "Serpent", "Shacal2", "Skipjack", "SM4", "TEA", "Twofish", "Threefish", 
      "VMPC", "VMPCKSA3", "XTEA", "XSalsa20", "OpenSSLPBKDF", "DSTU7624" };
  
  private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
  
  private static final String[] ASYMMETRIC_GENERIC = new String[] { "X509", "IES" };
  
  private static final String[] ASYMMETRIC_CIPHERS = new String[] { "DSA", "DH", "EC", "RSA", "GOST", "ECGOST", "ElGamal", "DSTU4145", "GM" };
  
  private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
  
  private static final String[] DIGESTS = new String[] { 
      "GOST3411", "Keccak", "MD2", "MD4", "MD5", "SHA1", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", 
      "SHA224", "SHA256", "SHA384", "SHA512", "SHA3", "Skein", "SM3", "Tiger", "Whirlpool", "Blake2b", 
      "DSTU7564" };
  
  private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
  
  private static final String[] KEYSTORES = new String[] { "BC", "BCFKS", "PKCS12" };
  
  private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
  
  private static final String[] SECURE_RANDOMS = new String[] { "DRBG" };
  
  public BouncyCastleProvider() {
    super("BC", 1.58D, info);
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            BouncyCastleProvider.this.setup();
            return null;
          }
        });
  }
  
  private void setup() {
    loadAlgorithms("org.bouncycastle.jcajce.provider.digest.", DIGESTS);
    loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_GENERIC);
    loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_MACS);
    loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_CIPHERS);
    loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", ASYMMETRIC_GENERIC);
    loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", ASYMMETRIC_CIPHERS);
    loadAlgorithms("org.bouncycastle.jcajce.provider.keystore.", KEYSTORES);
    loadAlgorithms("org.bouncycastle.jcajce.provider.drbg.", SECURE_RANDOMS);
    loadPQCKeys();
    put("X509Store.CERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertCollection");
    put("X509Store.ATTRIBUTECERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreAttrCertCollection");
    put("X509Store.CRL/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCRLCollection");
    put("X509Store.CERTIFICATEPAIR/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertPairCollection");
    put("X509Store.CERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCerts");
    put("X509Store.CRL/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCRLs");
    put("X509Store.ATTRIBUTECERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPAttrCerts");
    put("X509Store.CERTIFICATEPAIR/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCertPairs");
    put("X509StreamParser.CERTIFICATE", "org.bouncycastle.jce.provider.X509CertParser");
    put("X509StreamParser.ATTRIBUTECERTIFICATE", "org.bouncycastle.jce.provider.X509AttrCertParser");
    put("X509StreamParser.CRL", "org.bouncycastle.jce.provider.X509CRLParser");
    put("X509StreamParser.CERTIFICATEPAIR", "org.bouncycastle.jce.provider.X509CertPairParser");
    put("Cipher.BROKENPBEWITHMD5ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
    put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
    put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
    put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
    put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
    put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
    put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
    put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
    put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
    put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
    put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
    put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
    put("Alg.Alias.CertStore.X509LDAP", "LDAP");
  }
  
  private void loadAlgorithms(String paramString, String[] paramArrayOfString) {
    for (byte b = 0; b != paramArrayOfString.length; b++) {
      Class<AlgorithmProvider> clazz = ClassUtil.loadClass(BouncyCastleProvider.class, paramString + paramArrayOfString[b] + "$Mappings");
      if (clazz != null)
        try {
          ((AlgorithmProvider)clazz.newInstance()).configure(this);
        } catch (Exception exception) {
          throw new InternalError("cannot create instance of " + paramString + paramArrayOfString[b] + "$Mappings : " + exception);
        }  
    } 
  }
  
  private void loadPQCKeys() {
    addKeyInfoConverter(PQCObjectIdentifiers.sphincs256, (AsymmetricKeyInfoConverter)new Sphincs256KeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.newHope, (AsymmetricKeyInfoConverter)new NHKeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.xmss, (AsymmetricKeyInfoConverter)new XMSSKeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.xmss_mt, (AsymmetricKeyInfoConverter)new XMSSMTKeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.mcEliece, (AsymmetricKeyInfoConverter)new McElieceKeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.mcElieceCca2, (AsymmetricKeyInfoConverter)new McElieceCCA2KeyFactorySpi());
    addKeyInfoConverter(PQCObjectIdentifiers.rainbow, (AsymmetricKeyInfoConverter)new RainbowKeyFactorySpi());
  }
  
  public void setParameter(String paramString, Object paramObject) {
    synchronized (CONFIGURATION) {
      ((BouncyCastleProviderConfiguration)CONFIGURATION).setParameter(paramString, paramObject);
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
}
