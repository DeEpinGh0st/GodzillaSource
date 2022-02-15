package org.bouncycastle.x509;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Strings;

class X509Util {
  private static Hashtable algorithms = new Hashtable<Object, Object>();
  
  private static Hashtable params = new Hashtable<Object, Object>();
  
  private static Set noParams = new HashSet();
  
  private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt) {
    return new RSASSAPSSparams(paramAlgorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)paramAlgorithmIdentifier), new ASN1Integer(paramInt), new ASN1Integer(1L));
  }
  
  static ASN1ObjectIdentifier getAlgorithmOID(String paramString) {
    paramString = Strings.toUpperCase(paramString);
    return algorithms.containsKey(paramString) ? (ASN1ObjectIdentifier)algorithms.get(paramString) : new ASN1ObjectIdentifier(paramString);
  }
  
  static AlgorithmIdentifier getSigAlgID(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    if (noParams.contains(paramASN1ObjectIdentifier))
      return new AlgorithmIdentifier(paramASN1ObjectIdentifier); 
    paramString = Strings.toUpperCase(paramString);
    return params.containsKey(paramString) ? new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)params.get(paramString)) : new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
  }
  
  static Iterator getAlgNames() {
    Enumeration enumeration = algorithms.keys();
    ArrayList arrayList = new ArrayList();
    while (enumeration.hasMoreElements())
      arrayList.add(enumeration.nextElement()); 
    return arrayList.iterator();
  }
  
  static Signature getSignatureInstance(String paramString) throws NoSuchAlgorithmException {
    return Signature.getInstance(paramString);
  }
  
  static Signature getSignatureInstance(String paramString1, String paramString2) throws NoSuchProviderException, NoSuchAlgorithmException {
    return (paramString2 != null) ? Signature.getInstance(paramString1, paramString2) : Signature.getInstance(paramString1);
  }
  
  static byte[] calculateSignature(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString, PrivateKey paramPrivateKey, SecureRandom paramSecureRandom, ASN1Encodable paramASN1Encodable) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (paramASN1ObjectIdentifier == null)
      throw new IllegalStateException("no signature algorithm specified"); 
    Signature signature = getSignatureInstance(paramString);
    if (paramSecureRandom != null) {
      signature.initSign(paramPrivateKey, paramSecureRandom);
    } else {
      signature.initSign(paramPrivateKey);
    } 
    signature.update(paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
    return signature.sign();
  }
  
  static byte[] calculateSignature(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString1, String paramString2, PrivateKey paramPrivateKey, SecureRandom paramSecureRandom, ASN1Encodable paramASN1Encodable) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (paramASN1ObjectIdentifier == null)
      throw new IllegalStateException("no signature algorithm specified"); 
    Signature signature = getSignatureInstance(paramString1, paramString2);
    if (paramSecureRandom != null) {
      signature.initSign(paramPrivateKey, paramSecureRandom);
    } else {
      signature.initSign(paramPrivateKey);
    } 
    signature.update(paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
    return signature.sign();
  }
  
  static X509Principal convertPrincipal(X500Principal paramX500Principal) {
    try {
      return new X509Principal(paramX500Principal.getEncoded());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("cannot convert principal");
    } 
  }
  
  static Implementation getImplementation(String paramString1, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException {
    String str1;
    for (paramString2 = Strings.toUpperCase(paramString2); (str1 = paramProvider.getProperty("Alg.Alias." + paramString1 + "." + paramString2)) != null; paramString2 = str1);
    String str2 = paramProvider.getProperty(paramString1 + "." + paramString2);
    if (str2 != null)
      try {
        Class<?> clazz;
        ClassLoader classLoader = paramProvider.getClass().getClassLoader();
        if (classLoader != null) {
          clazz = classLoader.loadClass(str2);
        } else {
          clazz = Class.forName(str2);
        } 
        return new Implementation(clazz.newInstance(), paramProvider);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new IllegalStateException("algorithm " + paramString2 + " in provider " + paramProvider.getName() + " but no class \"" + str2 + "\" found!");
      } catch (Exception exception) {
        throw new IllegalStateException("algorithm " + paramString2 + " in provider " + paramProvider.getName() + " but class \"" + str2 + "\" inaccessible!");
      }  
    throw new NoSuchAlgorithmException("cannot find implementation " + paramString2 + " for provider " + paramProvider.getName());
  }
  
  static Implementation getImplementation(String paramString1, String paramString2) throws NoSuchAlgorithmException {
    Provider[] arrayOfProvider = Security.getProviders();
    for (byte b = 0; b != arrayOfProvider.length; b++) {
      Implementation implementation = getImplementation(paramString1, Strings.toUpperCase(paramString2), arrayOfProvider[b]);
      if (implementation != null)
        return implementation; 
      try {
        implementation = getImplementation(paramString1, paramString2, arrayOfProvider[b]);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {}
    } 
    throw new NoSuchAlgorithmException("cannot find implementation " + paramString2);
  }
  
  static Provider getProvider(String paramString) throws NoSuchProviderException {
    Provider provider = Security.getProvider(paramString);
    if (provider == null)
      throw new NoSuchProviderException("Provider " + paramString + " not found"); 
    return provider;
  }
  
  static {
    algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
    algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
    algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
    algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
    algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
    algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
    algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
    algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
    algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
    algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
    algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
    algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
    algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
    algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
    algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
    algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
    algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
    algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
    algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
    algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
    algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
    algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
    algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
    algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
    algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
    algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
    algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
    algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
    algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
    algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
    algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
    algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
    algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
    algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    algorithms.put("GOST3411WITHGOST3410-94", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
    noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
    noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha384);
    noParams.add(NISTObjectIdentifiers.dsa_with_sha512);
    noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
    noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
    AlgorithmIdentifier algorithmIdentifier1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA1WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier1, 20));
    AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA224WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier2, 28));
    AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA256WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier3, 32));
    AlgorithmIdentifier algorithmIdentifier4 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA384WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier4, 48));
    AlgorithmIdentifier algorithmIdentifier5 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE);
    params.put("SHA512WITHRSAANDMGF1", creatPSSParams(algorithmIdentifier5, 64));
  }
  
  static class Implementation {
    Object engine;
    
    Provider provider;
    
    Implementation(Object param1Object, Provider param1Provider) {
      this.engine = param1Object;
      this.provider = param1Provider;
    }
    
    Object getEngine() {
      return this.engine;
    }
    
    Provider getProvider() {
      return this.provider;
    }
  }
}
