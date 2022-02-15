package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class RSA {
  private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.rsa.";
  
  private static final Map<String, String> generalRsaAttributes = new HashMap<String, String>();
  
  static {
    generalRsaAttributes.put("SupportedKeyClasses", "javax.crypto.interfaces.RSAPublicKey|javax.crypto.interfaces.RSAPrivateKey");
    generalRsaAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
  }
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$OAEP");
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSAPSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSASSA-PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224withRSA/PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256withRSA/PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384withRSA/PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512withRSA/PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-224WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-256WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-384WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-512WITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.RAWRSAPSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAPSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSASSA-PSS", "PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAANDMGF1", "PSS");
      param1ConfigurableProvider.addAttributes("Cipher.RSA", RSA.generalRsaAttributes);
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/RAW", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/PKCS1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
      param1ConfigurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.rsaEncryption, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
      param1ConfigurableProvider.addAlgorithm("Cipher", X509ObjectIdentifiers.id_ea_rsa, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PrivateOnly");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PublicOnly");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
      param1ConfigurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.id_RSAES_OAEP, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
      param1ConfigurableProvider.addAlgorithm("Cipher.RSA/ISO9796-1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$ISO9796d1Padding");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//RAW", "RSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Cipher.RSA//ISO9796-1PADDING", "RSA/ISO9796-1");
      param1ConfigurableProvider.addAlgorithm("KeyFactory.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi");
      param1ConfigurableProvider.addAlgorithm("KeyPairGenerator.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi");
      KeyFactorySpi keyFactorySpi = new KeyFactorySpi();
      registerOid(param1ConfigurableProvider, PKCSObjectIdentifiers.rsaEncryption, "RSA", (AsymmetricKeyInfoConverter)keyFactorySpi);
      registerOid(param1ConfigurableProvider, X509ObjectIdentifiers.id_ea_rsa, "RSA", (AsymmetricKeyInfoConverter)keyFactorySpi);
      registerOid(param1ConfigurableProvider, PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA", (AsymmetricKeyInfoConverter)keyFactorySpi);
      registerOid(param1ConfigurableProvider, PKCSObjectIdentifiers.id_RSASSA_PSS, "RSA", (AsymmetricKeyInfoConverter)keyFactorySpi);
      registerOidAlgorithmParameters(param1ConfigurableProvider, PKCSObjectIdentifiers.rsaEncryption, "RSA");
      registerOidAlgorithmParameters(param1ConfigurableProvider, X509ObjectIdentifiers.id_ea_rsa, "RSA");
      registerOidAlgorithmParameters(param1ConfigurableProvider, PKCSObjectIdentifiers.id_RSAES_OAEP, "OAEP");
      registerOidAlgorithmParameters(param1ConfigurableProvider, PKCSObjectIdentifiers.id_RSASSA_PSS, "PSS");
      param1ConfigurableProvider.addAlgorithm("Signature.RSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
      param1ConfigurableProvider.addAlgorithm("Signature." + PKCSObjectIdentifiers.id_RSASSA_PSS, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
      param1ConfigurableProvider.addAlgorithm("Signature.OID." + PKCSObjectIdentifiers.id_RSASSA_PSS, "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
      param1ConfigurableProvider.addAlgorithm("Signature.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$noneRSA");
      param1ConfigurableProvider.addAlgorithm("Signature.RAWRSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$nonePSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.RAWRSA", "RSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSA", "RSA");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.RAWRSAPSS", "RAWRSASSA-PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAPSS", "RAWRSASSA-PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSASSA-PSS", "RAWRSASSA-PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAANDMGF1", "RAWRSASSA-PSS");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.RSAPSS", "RSASSA-PSS");
      addPSSSignature(param1ConfigurableProvider, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA3-224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA3-256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA3-384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSA");
      addPSSSignature(param1ConfigurableProvider, "SHA3-512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSA");
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "MD2"))
        addDigestSignature(param1ConfigurableProvider, "MD2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD2", PKCSObjectIdentifiers.md2WithRSAEncryption); 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "MD4"))
        addDigestSignature(param1ConfigurableProvider, "MD4", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD4", PKCSObjectIdentifiers.md4WithRSAEncryption); 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "MD5")) {
        addDigestSignature(param1ConfigurableProvider, "MD5", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD5", PKCSObjectIdentifiers.md5WithRSAEncryption);
        addISO9796Signature(param1ConfigurableProvider, "MD5", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$MD5WithRSAEncryption");
      } 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "SHA1")) {
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1withRSA/PSS", "PSS");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1WITHRSAANDMGF1", "PSS");
        addPSSSignature(param1ConfigurableProvider, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSA");
        addDigestSignature(param1ConfigurableProvider, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA1", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        addISO9796Signature(param1ConfigurableProvider, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA1WithRSAEncryption");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.OID." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        addX931Signature(param1ConfigurableProvider, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA1WithRSAEncryption");
      } 
      addDigestSignature(param1ConfigurableProvider, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA224", PKCSObjectIdentifiers.sha224WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA256", PKCSObjectIdentifiers.sha256WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA384", PKCSObjectIdentifiers.sha384WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512", PKCSObjectIdentifiers.sha512WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_224", PKCSObjectIdentifiers.sha512_224WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_256", PKCSObjectIdentifiers.sha512_256WithRSAEncryption);
      addDigestSignature(param1ConfigurableProvider, "SHA3-224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_224", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
      addDigestSignature(param1ConfigurableProvider, "SHA3-256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_256", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
      addDigestSignature(param1ConfigurableProvider, "SHA3-384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_384", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
      addDigestSignature(param1ConfigurableProvider, "SHA3-512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_512", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
      addISO9796Signature(param1ConfigurableProvider, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA224WithRSAEncryption");
      addISO9796Signature(param1ConfigurableProvider, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA256WithRSAEncryption");
      addISO9796Signature(param1ConfigurableProvider, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA384WithRSAEncryption");
      addISO9796Signature(param1ConfigurableProvider, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512WithRSAEncryption");
      addISO9796Signature(param1ConfigurableProvider, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_224WithRSAEncryption");
      addISO9796Signature(param1ConfigurableProvider, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_256WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA224WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA256WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA384WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_224WithRSAEncryption");
      addX931Signature(param1ConfigurableProvider, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_256WithRSAEncryption");
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "RIPEMD128")) {
        addDigestSignature(param1ConfigurableProvider, "RIPEMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        addDigestSignature(param1ConfigurableProvider, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", (ASN1ObjectIdentifier)null);
        addX931Signature(param1ConfigurableProvider, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
        addX931Signature(param1ConfigurableProvider, "RIPEMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
      } 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "RIPEMD160")) {
        addDigestSignature(param1ConfigurableProvider, "RIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        addDigestSignature(param1ConfigurableProvider, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", (ASN1ObjectIdentifier)null);
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.RIPEMD160WithRSA/ISO9796-2", "RIPEMD160withRSA/ISO9796-2");
        param1ConfigurableProvider.addAlgorithm("Signature.RIPEMD160withRSA/ISO9796-2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$RIPEMD160WithRSAEncryption");
        addX931Signature(param1ConfigurableProvider, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
        addX931Signature(param1ConfigurableProvider, "RIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
      } 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "RIPEMD256")) {
        addDigestSignature(param1ConfigurableProvider, "RIPEMD256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        addDigestSignature(param1ConfigurableProvider, "RMD256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", (ASN1ObjectIdentifier)null);
      } 
      if (param1ConfigurableProvider.hasAlgorithm("MessageDigest", "WHIRLPOOL")) {
        addISO9796Signature(param1ConfigurableProvider, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
        addISO9796Signature(param1ConfigurableProvider, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
        addX931Signature(param1ConfigurableProvider, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
        addX931Signature(param1ConfigurableProvider, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
      } 
    }
    
    private void addDigestSignature(ConfigurableProvider param1ConfigurableProvider, String param1String1, String param1String2, ASN1ObjectIdentifier param1ASN1ObjectIdentifier) {
      String str1 = param1String1 + "WITHRSA";
      String str2 = param1String1 + "withRSA";
      String str3 = param1String1 + "WithRSA";
      String str4 = param1String1 + "/" + "RSA";
      String str5 = param1String1 + "WITHRSAENCRYPTION";
      String str6 = param1String1 + "withRSAEncryption";
      String str7 = param1String1 + "WithRSAEncryption";
      param1ConfigurableProvider.addAlgorithm("Signature." + str1, param1String2);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str2, str1);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str3, str1);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str5, str1);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str6, str1);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str7, str1);
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + str4, str1);
      if (param1ASN1ObjectIdentifier != null) {
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1ASN1ObjectIdentifier, str1);
        param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature.OID." + param1ASN1ObjectIdentifier, str1);
      } 
    }
    
    private void addISO9796Signature(ConfigurableProvider param1ConfigurableProvider, String param1String1, String param1String2) {
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "withRSA/ISO9796-2", param1String1 + "WITHRSA/ISO9796-2");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "WithRSA/ISO9796-2", param1String1 + "WITHRSA/ISO9796-2");
      param1ConfigurableProvider.addAlgorithm("Signature." + param1String1 + "WITHRSA/ISO9796-2", param1String2);
    }
    
    private void addPSSSignature(ConfigurableProvider param1ConfigurableProvider, String param1String1, String param1String2) {
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "withRSA/PSS", param1String1 + "WITHRSAANDMGF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "WithRSA/PSS", param1String1 + "WITHRSAANDMGF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "withRSAandMGF1", param1String1 + "WITHRSAANDMGF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "WithRSAAndMGF1", param1String1 + "WITHRSAANDMGF1");
      param1ConfigurableProvider.addAlgorithm("Signature." + param1String1 + "WITHRSAANDMGF1", param1String2);
    }
    
    private void addX931Signature(ConfigurableProvider param1ConfigurableProvider, String param1String1, String param1String2) {
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "withRSA/X9.31", param1String1 + "WITHRSA/X9.31");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.Signature." + param1String1 + "WithRSA/X9.31", param1String1 + "WITHRSA/X9.31");
      param1ConfigurableProvider.addAlgorithm("Signature." + param1String1 + "WITHRSA/X9.31", param1String2);
    }
  }
}
