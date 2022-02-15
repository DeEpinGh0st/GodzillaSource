package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipient;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public abstract class JceKeyAgreeRecipient implements KeyAgreeRecipient {
  private static final Set possibleOldMessages = new HashSet();
  
  private PrivateKey recipientKey;
  
  protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  protected EnvelopedDataHelper contentHelper = this.helper;
  
  private SecretKeySizeProvider keySizeProvider = (SecretKeySizeProvider)new DefaultSecretKeySizeProvider();
  
  private static KeyMaterialGenerator old_ecc_cms_Generator = new KeyMaterialGenerator() {
      public byte[] generateKDFMaterial(AlgorithmIdentifier param1AlgorithmIdentifier, int param1Int, byte[] param1ArrayOfbyte) {
        ECCCMSSharedInfo eCCCMSSharedInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(param1AlgorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), param1ArrayOfbyte, Pack.intToBigEndian(param1Int));
        try {
          return eCCCMSSharedInfo.getEncoded("DER");
        } catch (IOException iOException) {
          throw new IllegalStateException("Unable to create KDF material: " + iOException);
        } 
      }
    };
  
  private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
  
  public JceKeyAgreeRecipient(PrivateKey paramPrivateKey) {
    this.recipientKey = paramPrivateKey;
  }
  
  public JceKeyAgreeRecipient setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKeyAgreeRecipient setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKeyAgreeRecipient setContentProvider(Provider paramProvider) {
    this.contentHelper = CMSUtils.createContentHelper(paramProvider);
    return this;
  }
  
  public JceKeyAgreeRecipient setContentProvider(String paramString) {
    this.contentHelper = CMSUtils.createContentHelper(paramString);
    return this;
  }
  
  private SecretKey calculateAgreedWrapKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, PublicKey paramPublicKey, ASN1OctetString paramASN1OctetString, PrivateKey paramPrivateKey, KeyMaterialGenerator paramKeyMaterialGenerator) throws CMSException, GeneralSecurityException, IOException {
    if (CMSUtils.isMQV(paramAlgorithmIdentifier1.getAlgorithm())) {
      MQVuserKeyingMaterial mQVuserKeyingMaterial = MQVuserKeyingMaterial.getInstance(paramASN1OctetString.getOctets());
      SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(getPrivateKeyAlgorithmIdentifier(), mQVuserKeyingMaterial.getEphemeralPublicKey().getPublicKey().getBytes());
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
      KeyFactory keyFactory = this.helper.createKeyFactory(paramAlgorithmIdentifier1.getAlgorithm());
      PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
      KeyAgreement keyAgreement1 = this.helper.createKeyAgreement(paramAlgorithmIdentifier1.getAlgorithm());
      byte[] arrayOfByte = (mQVuserKeyingMaterial.getAddedukm() != null) ? mQVuserKeyingMaterial.getAddedukm().getOctets() : null;
      if (paramKeyMaterialGenerator == old_ecc_cms_Generator)
        arrayOfByte = old_ecc_cms_Generator.generateKDFMaterial(paramAlgorithmIdentifier2, this.keySizeProvider.getKeySize(paramAlgorithmIdentifier2), arrayOfByte); 
      keyAgreement1.init(paramPrivateKey, (AlgorithmParameterSpec)new MQVParameterSpec(paramPrivateKey, publicKey, arrayOfByte));
      keyAgreement1.doPhase(paramPublicKey, true);
      return keyAgreement1.generateSecret(paramAlgorithmIdentifier2.getAlgorithm().getId());
    } 
    KeyAgreement keyAgreement = this.helper.createKeyAgreement(paramAlgorithmIdentifier1.getAlgorithm());
    UserKeyingMaterialSpec userKeyingMaterialSpec = null;
    if (CMSUtils.isEC(paramAlgorithmIdentifier1.getAlgorithm())) {
      if (paramASN1OctetString != null) {
        byte[] arrayOfByte = paramKeyMaterialGenerator.generateKDFMaterial(paramAlgorithmIdentifier2, this.keySizeProvider.getKeySize(paramAlgorithmIdentifier2), paramASN1OctetString.getOctets());
        userKeyingMaterialSpec = new UserKeyingMaterialSpec(arrayOfByte);
      } else {
        byte[] arrayOfByte = paramKeyMaterialGenerator.generateKDFMaterial(paramAlgorithmIdentifier2, this.keySizeProvider.getKeySize(paramAlgorithmIdentifier2), null);
        userKeyingMaterialSpec = new UserKeyingMaterialSpec(arrayOfByte);
      } 
    } else if (CMSUtils.isRFC2631(paramAlgorithmIdentifier1.getAlgorithm())) {
      if (paramASN1OctetString != null)
        userKeyingMaterialSpec = new UserKeyingMaterialSpec(paramASN1OctetString.getOctets()); 
    } else if (CMSUtils.isGOST(paramAlgorithmIdentifier1.getAlgorithm())) {
      if (paramASN1OctetString != null)
        userKeyingMaterialSpec = new UserKeyingMaterialSpec(paramASN1OctetString.getOctets()); 
    } else {
      throw new CMSException("Unknown key agreement algorithm: " + paramAlgorithmIdentifier1.getAlgorithm());
    } 
    keyAgreement.init(paramPrivateKey, (AlgorithmParameterSpec)userKeyingMaterialSpec);
    keyAgreement.doPhase(paramPublicKey, true);
    return keyAgreement.generateSecret(paramAlgorithmIdentifier2.getAlgorithm().getId());
  }
  
  private Key unwrapSessionKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier1, SecretKey paramSecretKey, ASN1ObjectIdentifier paramASN1ObjectIdentifier2, byte[] paramArrayOfbyte) throws CMSException, InvalidKeyException, NoSuchAlgorithmException {
    Cipher cipher = this.helper.createCipher(paramASN1ObjectIdentifier1);
    cipher.init(4, paramSecretKey);
    return cipher.unwrap(paramArrayOfbyte, this.helper.getBaseCipherName(paramASN1ObjectIdentifier2), 3);
  }
  
  protected Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, ASN1OctetString paramASN1OctetString, byte[] paramArrayOfbyte) throws CMSException {
    try {
      AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(paramAlgorithmIdentifier1.getParameters());
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(paramSubjectPublicKeyInfo.getEncoded());
      KeyFactory keyFactory = this.helper.createKeyFactory(paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm());
      PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
      try {
        SecretKey secretKey = calculateAgreedWrapKey(paramAlgorithmIdentifier1, algorithmIdentifier, publicKey, paramASN1OctetString, this.recipientKey, ecc_cms_Generator);
        if (algorithmIdentifier.getAlgorithm().equals(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || algorithmIdentifier.getAlgorithm().equals(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
          Gost2814789EncryptedKey gost2814789EncryptedKey = Gost2814789EncryptedKey.getInstance(paramArrayOfbyte);
          Gost2814789KeyWrapParameters gost2814789KeyWrapParameters = Gost2814789KeyWrapParameters.getInstance(algorithmIdentifier.getParameters());
          Cipher cipher = this.helper.createCipher(algorithmIdentifier.getAlgorithm());
          cipher.init(4, secretKey, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(gost2814789KeyWrapParameters.getEncryptionParamSet(), paramASN1OctetString.getOctets()));
          return cipher.unwrap(Arrays.concatenate(gost2814789EncryptedKey.getEncryptedKey(), gost2814789EncryptedKey.getMacKey()), this.helper.getBaseCipherName(paramAlgorithmIdentifier2.getAlgorithm()), 3);
        } 
        return unwrapSessionKey(algorithmIdentifier.getAlgorithm(), secretKey, paramAlgorithmIdentifier2.getAlgorithm(), paramArrayOfbyte);
      } catch (InvalidKeyException invalidKeyException) {
        if (possibleOldMessages.contains(paramAlgorithmIdentifier1.getAlgorithm())) {
          SecretKey secretKey = calculateAgreedWrapKey(paramAlgorithmIdentifier1, algorithmIdentifier, publicKey, paramASN1OctetString, this.recipientKey, old_ecc_cms_Generator);
          return unwrapSessionKey(algorithmIdentifier.getAlgorithm(), secretKey, paramAlgorithmIdentifier2.getAlgorithm(), paramArrayOfbyte);
        } 
        throw invalidKeyException;
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CMSException("can't find algorithm.", noSuchAlgorithmException);
    } catch (InvalidKeyException invalidKeyException) {
      throw new CMSException("key invalid in message.", invalidKeyException);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new CMSException("originator key spec invalid.", invalidKeySpecException);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new CMSException("required padding not supported.", noSuchPaddingException);
    } catch (Exception exception) {
      throw new CMSException("originator key invalid.", exception);
    } 
  }
  
  public AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier() {
    return PrivateKeyInfo.getInstance(this.recipientKey.getEncoded()).getPrivateKeyAlgorithm();
  }
  
  static {
    possibleOldMessages.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
    possibleOldMessages.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
  }
}
