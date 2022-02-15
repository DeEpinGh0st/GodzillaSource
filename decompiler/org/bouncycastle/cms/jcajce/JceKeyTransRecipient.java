package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.util.Arrays;

public abstract class JceKeyTransRecipient implements KeyTransRecipient {
  private PrivateKey recipientKey;
  
  protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  protected EnvelopedDataHelper contentHelper = this.helper;
  
  protected Map extraMappings = new HashMap<Object, Object>();
  
  protected boolean validateKeySize = false;
  
  protected boolean unwrappedKeyMustBeEncodable;
  
  public JceKeyTransRecipient(PrivateKey paramPrivateKey) {
    this.recipientKey = paramPrivateKey;
  }
  
  public JceKeyTransRecipient setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKeyTransRecipient setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    this.contentHelper = this.helper;
    return this;
  }
  
  public JceKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    this.extraMappings.put(paramASN1ObjectIdentifier, paramString);
    return this;
  }
  
  public JceKeyTransRecipient setContentProvider(Provider paramProvider) {
    this.contentHelper = CMSUtils.createContentHelper(paramProvider);
    return this;
  }
  
  public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(boolean paramBoolean) {
    this.unwrappedKeyMustBeEncodable = paramBoolean;
    return this;
  }
  
  public JceKeyTransRecipient setContentProvider(String paramString) {
    this.contentHelper = CMSUtils.createContentHelper(paramString);
    return this;
  }
  
  public JceKeyTransRecipient setKeySizeValidation(boolean paramBoolean) {
    this.validateKeySize = paramBoolean;
    return this;
  }
  
  protected Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) throws CMSException {
    if (CMSUtils.isGOST(paramAlgorithmIdentifier1.getAlgorithm()))
      try {
        GostR3410KeyTransport gostR3410KeyTransport = GostR3410KeyTransport.getInstance(paramArrayOfbyte);
        GostR3410TransportParameters gostR3410TransportParameters = gostR3410KeyTransport.getTransportParameters();
        KeyFactory keyFactory = this.helper.createKeyFactory(paramAlgorithmIdentifier1.getAlgorithm());
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(gostR3410TransportParameters.getEphemeralPublicKey().getEncoded()));
        KeyAgreement keyAgreement = this.helper.createKeyAgreement(paramAlgorithmIdentifier1.getAlgorithm());
        keyAgreement.init(this.recipientKey, (AlgorithmParameterSpec)new UserKeyingMaterialSpec(gostR3410TransportParameters.getUkm()));
        keyAgreement.doPhase(publicKey, true);
        SecretKey secretKey = keyAgreement.generateSecret("GOST28147");
        Cipher cipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
        cipher.init(4, secretKey, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(gostR3410TransportParameters.getEncryptionParamSet(), gostR3410TransportParameters.getUkm()));
        Gost2814789EncryptedKey gost2814789EncryptedKey = gostR3410KeyTransport.getSessionEncryptedKey();
        return cipher.unwrap(Arrays.concatenate(gost2814789EncryptedKey.getEncryptedKey(), gost2814789EncryptedKey.getMacKey()), this.helper.getBaseCipherName(paramAlgorithmIdentifier2.getAlgorithm()), 3);
      } catch (Exception exception) {
        throw new CMSException("exception unwrapping key: " + exception.getMessage(), exception);
      }  
    JceAsymmetricKeyUnwrapper jceAsymmetricKeyUnwrapper = this.helper.createAsymmetricUnwrapper(paramAlgorithmIdentifier1, this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
    if (!this.extraMappings.isEmpty())
      for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.extraMappings.keySet())
        jceAsymmetricKeyUnwrapper.setAlgorithmMapping(aSN1ObjectIdentifier, (String)this.extraMappings.get(aSN1ObjectIdentifier));  
    try {
      Key key = this.helper.getJceKey(paramAlgorithmIdentifier2.getAlgorithm(), jceAsymmetricKeyUnwrapper.generateUnwrappedKey(paramAlgorithmIdentifier2, paramArrayOfbyte));
      if (this.validateKeySize)
        this.helper.keySizeCheck(paramAlgorithmIdentifier2, key); 
      return key;
    } catch (OperatorException operatorException) {
      throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
    } 
  }
}
