package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;

public class JceKeyAgreeRecipientInfoGenerator extends KeyAgreeRecipientInfoGenerator {
  private SecretKeySizeProvider keySizeProvider = (SecretKeySizeProvider)new DefaultSecretKeySizeProvider();
  
  private List recipientIDs = new ArrayList();
  
  private List recipientKeys = new ArrayList();
  
  private PublicKey senderPublicKey;
  
  private PrivateKey senderPrivateKey;
  
  private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  private SecureRandom random;
  
  private KeyPair ephemeralKP;
  
  private byte[] userKeyingMaterial;
  
  private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
  
  public JceKeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier1, PrivateKey paramPrivateKey, PublicKey paramPublicKey, ASN1ObjectIdentifier paramASN1ObjectIdentifier2) {
    super(paramASN1ObjectIdentifier1, SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()), paramASN1ObjectIdentifier2);
    this.senderPublicKey = paramPublicKey;
    this.senderPrivateKey = paramPrivateKey;
  }
  
  public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(byte[] paramArrayOfbyte) {
    this.userKeyingMaterial = Arrays.clone(paramArrayOfbyte);
    return this;
  }
  
  public JceKeyAgreeRecipientInfoGenerator setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JceKeyAgreeRecipientInfoGenerator setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  public JceKeyAgreeRecipientInfoGenerator setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public JceKeyAgreeRecipientInfoGenerator addRecipient(X509Certificate paramX509Certificate) throws CertificateEncodingException {
    this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(paramX509Certificate)));
    this.recipientKeys.add(paramX509Certificate.getPublicKey());
    return this;
  }
  
  public JceKeyAgreeRecipientInfoGenerator addRecipient(byte[] paramArrayOfbyte, PublicKey paramPublicKey) throws CertificateEncodingException {
    this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(paramArrayOfbyte)));
    this.recipientKeys.add(paramPublicKey);
    return this;
  }
  
  public ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, GenericKey paramGenericKey) throws CMSException {
    if (this.recipientIDs.isEmpty())
      throw new CMSException("No recipients associated with generator - use addRecipient()"); 
    init(paramAlgorithmIdentifier1.getAlgorithm());
    PrivateKey privateKey = this.senderPrivateKey;
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramAlgorithmIdentifier1.getAlgorithm();
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != this.recipientIDs.size(); b++) {
      PublicKey publicKey = this.recipientKeys.get(b);
      KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier = this.recipientIDs.get(b);
      try {
        UserKeyingMaterialSpec userKeyingMaterialSpec;
        DEROctetString dEROctetString;
        ASN1ObjectIdentifier aSN1ObjectIdentifier1 = paramAlgorithmIdentifier2.getAlgorithm();
        if (CMSUtils.isMQV(aSN1ObjectIdentifier)) {
          MQVParameterSpec mQVParameterSpec = new MQVParameterSpec(this.ephemeralKP, publicKey, this.userKeyingMaterial);
        } else if (CMSUtils.isEC(aSN1ObjectIdentifier)) {
          byte[] arrayOfByte = ecc_cms_Generator.generateKDFMaterial(paramAlgorithmIdentifier2, this.keySizeProvider.getKeySize(aSN1ObjectIdentifier1), this.userKeyingMaterial);
          userKeyingMaterialSpec = new UserKeyingMaterialSpec(arrayOfByte);
        } else if (CMSUtils.isRFC2631(aSN1ObjectIdentifier)) {
          if (this.userKeyingMaterial != null) {
            userKeyingMaterialSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
          } else {
            if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH))
              throw new CMSException("User keying material must be set for static keys."); 
            userKeyingMaterialSpec = null;
          } 
        } else if (CMSUtils.isGOST(aSN1ObjectIdentifier)) {
          if (this.userKeyingMaterial != null) {
            userKeyingMaterialSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
          } else {
            throw new CMSException("User keying material must be set for static keys.");
          } 
        } else {
          throw new CMSException("Unknown key agreement algorithm: " + aSN1ObjectIdentifier);
        } 
        KeyAgreement keyAgreement = this.helper.createKeyAgreement(aSN1ObjectIdentifier);
        keyAgreement.init(privateKey, (AlgorithmParameterSpec)userKeyingMaterialSpec, this.random);
        keyAgreement.doPhase(publicKey, true);
        SecretKey secretKey = keyAgreement.generateSecret(aSN1ObjectIdentifier1.getId());
        Cipher cipher = this.helper.createCipher(aSN1ObjectIdentifier1);
        if (aSN1ObjectIdentifier1.equals(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || aSN1ObjectIdentifier1.equals(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
          cipher.init(3, secretKey, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
          byte[] arrayOfByte = cipher.wrap(this.helper.getJceKey(paramGenericKey));
          Gost2814789EncryptedKey gost2814789EncryptedKey = new Gost2814789EncryptedKey(Arrays.copyOfRange(arrayOfByte, 0, arrayOfByte.length - 4), Arrays.copyOfRange(arrayOfByte, arrayOfByte.length - 4, arrayOfByte.length));
          dEROctetString = new DEROctetString(gost2814789EncryptedKey.getEncoded("DER"));
        } else {
          cipher.init(3, secretKey, this.random);
          byte[] arrayOfByte = cipher.wrap(this.helper.getJceKey(paramGenericKey));
          dEROctetString = new DEROctetString(arrayOfByte);
        } 
        aSN1EncodableVector.add((ASN1Encodable)new RecipientEncryptedKey(keyAgreeRecipientIdentifier, (ASN1OctetString)dEROctetString));
      } catch (GeneralSecurityException generalSecurityException) {
        throw new CMSException("cannot perform agreement step: " + generalSecurityException.getMessage(), generalSecurityException);
      } catch (IOException iOException) {
        throw new CMSException("unable to encode wrapped key: " + iOException.getMessage(), iOException);
      } 
    } 
    return (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  protected byte[] getUserKeyingMaterial(AlgorithmIdentifier paramAlgorithmIdentifier) throws CMSException {
    init(paramAlgorithmIdentifier.getAlgorithm());
    if (this.ephemeralKP != null) {
      OriginatorPublicKey originatorPublicKey = createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance(this.ephemeralKP.getPublic().getEncoded()));
      try {
        return (this.userKeyingMaterial != null) ? (new MQVuserKeyingMaterial(originatorPublicKey, (ASN1OctetString)new DEROctetString(this.userKeyingMaterial))).getEncoded() : (new MQVuserKeyingMaterial(originatorPublicKey, null)).getEncoded();
      } catch (IOException iOException) {
        throw new CMSException("unable to encode user keying material: " + iOException.getMessage(), iOException);
      } 
    } 
    return this.userKeyingMaterial;
  }
  
  private void init(ASN1ObjectIdentifier paramASN1ObjectIdentifier) throws CMSException {
    if (this.random == null)
      this.random = new SecureRandom(); 
    if (CMSUtils.isMQV(paramASN1ObjectIdentifier) && this.ephemeralKP == null)
      try {
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(this.senderPublicKey.getEncoded());
        AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(paramASN1ObjectIdentifier);
        algorithmParameters.init(subjectPublicKeyInfo.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
        KeyPairGenerator keyPairGenerator = this.helper.createKeyPairGenerator(paramASN1ObjectIdentifier);
        keyPairGenerator.initialize(algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class), this.random);
        this.ephemeralKP = keyPairGenerator.generateKeyPair();
      } catch (Exception exception) {
        throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + exception, exception);
      }  
  }
}
