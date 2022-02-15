package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSContentEncryptorBuilder {
  private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
  
  private final ASN1ObjectIdentifier encryptionOID;
  
  private final int keySize;
  
  private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  private SecureRandom random;
  
  private AlgorithmParameters algorithmParameters;
  
  public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, KEY_SIZE_PROVIDER.getKeySize(paramASN1ObjectIdentifier));
  }
  
  public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt) {
    this.encryptionOID = paramASN1ObjectIdentifier;
    int i = KEY_SIZE_PROVIDER.getKeySize(paramASN1ObjectIdentifier);
    if (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
      if (paramInt != 168 && paramInt != i)
        throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder."); 
      this.keySize = 168;
    } else if (paramASN1ObjectIdentifier.equals(OIWObjectIdentifiers.desCBC)) {
      if (paramInt != 56 && paramInt != i)
        throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder."); 
      this.keySize = 56;
    } else {
      if (i > 0 && i != paramInt)
        throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder."); 
      this.keySize = paramInt;
    } 
  }
  
  public JceCMSContentEncryptorBuilder setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JceCMSContentEncryptorBuilder setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters paramAlgorithmParameters) {
    this.algorithmParameters = paramAlgorithmParameters;
    return this;
  }
  
  public OutputEncryptor build() throws CMSException {
    return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
  }
  
  private class CMSOutputEncryptor implements OutputEncryptor {
    private SecretKey encKey;
    
    private AlgorithmIdentifier algorithmIdentifier;
    
    private Cipher cipher;
    
    CMSOutputEncryptor(ASN1ObjectIdentifier param1ASN1ObjectIdentifier, int param1Int, AlgorithmParameters param1AlgorithmParameters, SecureRandom param1SecureRandom) throws CMSException {
      KeyGenerator keyGenerator = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(param1ASN1ObjectIdentifier);
      if (param1SecureRandom == null)
        param1SecureRandom = new SecureRandom(); 
      if (param1Int < 0) {
        keyGenerator.init(param1SecureRandom);
      } else {
        keyGenerator.init(param1Int, param1SecureRandom);
      } 
      this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(param1ASN1ObjectIdentifier);
      this.encKey = keyGenerator.generateKey();
      if (param1AlgorithmParameters == null)
        param1AlgorithmParameters = JceCMSContentEncryptorBuilder.this.helper.generateParameters(param1ASN1ObjectIdentifier, this.encKey, param1SecureRandom); 
      try {
        this.cipher.init(1, this.encKey, param1AlgorithmParameters, param1SecureRandom);
      } catch (GeneralSecurityException generalSecurityException) {
        throw new CMSException("unable to initialize cipher: " + generalSecurityException.getMessage(), generalSecurityException);
      } 
      if (param1AlgorithmParameters == null)
        param1AlgorithmParameters = this.cipher.getParameters(); 
      this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(param1ASN1ObjectIdentifier, param1AlgorithmParameters);
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
    }
    
    public OutputStream getOutputStream(OutputStream param1OutputStream) {
      return new CipherOutputStream(param1OutputStream, this.cipher);
    }
    
    public GenericKey getKey() {
      return (GenericKey)new JceGenericKey(this.algorithmIdentifier, this.encKey);
    }
  }
}
