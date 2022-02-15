package org.bouncycastle.operator.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public class JceSymmetricKeyWrapper extends SymmetricKeyWrapper {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private SecureRandom random;
  
  private SecretKey wrappingKey;
  
  public JceSymmetricKeyWrapper(SecretKey paramSecretKey) {
    super(determineKeyEncAlg(paramSecretKey));
    this.wrappingKey = paramSecretKey;
  }
  
  public JceSymmetricKeyWrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceSymmetricKeyWrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JceSymmetricKeyWrapper setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public byte[] generateWrappedKey(GenericKey paramGenericKey) throws OperatorException {
    Key key = OperatorUtils.getJceKey(paramGenericKey);
    Cipher cipher = this.helper.createSymmetricWrapper(getAlgorithmIdentifier().getAlgorithm());
    try {
      cipher.init(3, this.wrappingKey, this.random);
      return cipher.wrap(key);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new OperatorException("cannot wrap key: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  private static AlgorithmIdentifier determineKeyEncAlg(SecretKey paramSecretKey) {
    return determineKeyEncAlg(paramSecretKey.getAlgorithm(), (paramSecretKey.getEncoded()).length * 8);
  }
  
  static AlgorithmIdentifier determineKeyEncAlg(String paramString, int paramInt) {
    if (paramString.startsWith("DES") || paramString.startsWith("TripleDES"))
      return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramString.startsWith("RC2"))
      return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.7"), (ASN1Encodable)new ASN1Integer(58L)); 
    if (paramString.startsWith("AES")) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier;
      if (paramInt == 128) {
        aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_wrap;
      } else if (paramInt == 192) {
        aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes192_wrap;
      } else if (paramInt == 256) {
        aSN1ObjectIdentifier = NISTObjectIdentifiers.id_aes256_wrap;
      } else {
        throw new IllegalArgumentException("illegal keysize in AES");
      } 
      return new AlgorithmIdentifier(aSN1ObjectIdentifier);
    } 
    if (paramString.startsWith("SEED"))
      return new AlgorithmIdentifier(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap); 
    if (paramString.startsWith("Camellia")) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier;
      if (paramInt == 128) {
        aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia128_wrap;
      } else if (paramInt == 192) {
        aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia192_wrap;
      } else if (paramInt == 256) {
        aSN1ObjectIdentifier = NTTObjectIdentifiers.id_camellia256_wrap;
      } else {
        throw new IllegalArgumentException("illegal keysize in Camellia");
      } 
      return new AlgorithmIdentifier(aSN1ObjectIdentifier);
    } 
    throw new IllegalArgumentException("unknown algorithm");
  }
}
