package org.bouncycastle.cert.crmf.jcajce;

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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCRMFEncryptorBuilder {
  private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
  
  private final ASN1ObjectIdentifier encryptionOID;
  
  private final int keySize;
  
  private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private SecureRandom random;
  
  public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, -1);
  }
  
  public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt) {
    this.encryptionOID = paramASN1ObjectIdentifier;
    this.keySize = paramInt;
  }
  
  public JceCRMFEncryptorBuilder setProvider(Provider paramProvider) {
    this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceCRMFEncryptorBuilder setProvider(String paramString) {
    this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public JceCRMFEncryptorBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public OutputEncryptor build() throws CRMFException {
    return new CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
  }
  
  private class CRMFOutputEncryptor implements OutputEncryptor {
    private SecretKey encKey;
    
    private AlgorithmIdentifier algorithmIdentifier;
    
    private Cipher cipher;
    
    CRMFOutputEncryptor(ASN1ObjectIdentifier param1ASN1ObjectIdentifier, int param1Int, SecureRandom param1SecureRandom) throws CRMFException {
      KeyGenerator keyGenerator = JceCRMFEncryptorBuilder.this.helper.createKeyGenerator(param1ASN1ObjectIdentifier);
      if (param1SecureRandom == null)
        param1SecureRandom = new SecureRandom(); 
      if (param1Int < 0)
        param1Int = JceCRMFEncryptorBuilder.KEY_SIZE_PROVIDER.getKeySize(param1ASN1ObjectIdentifier); 
      if (param1Int < 0) {
        keyGenerator.init(param1SecureRandom);
      } else {
        keyGenerator.init(param1Int, param1SecureRandom);
      } 
      this.cipher = JceCRMFEncryptorBuilder.this.helper.createCipher(param1ASN1ObjectIdentifier);
      this.encKey = keyGenerator.generateKey();
      AlgorithmParameters algorithmParameters = JceCRMFEncryptorBuilder.this.helper.generateParameters(param1ASN1ObjectIdentifier, this.encKey, param1SecureRandom);
      try {
        this.cipher.init(1, this.encKey, algorithmParameters, param1SecureRandom);
      } catch (GeneralSecurityException generalSecurityException) {
        throw new CRMFException("unable to initialize cipher: " + generalSecurityException.getMessage(), generalSecurityException);
      } 
      if (algorithmParameters == null)
        algorithmParameters = this.cipher.getParameters(); 
      this.algorithmIdentifier = JceCRMFEncryptorBuilder.this.helper.getAlgorithmIdentifier(param1ASN1ObjectIdentifier, algorithmParameters);
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
