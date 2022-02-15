package org.bouncycastle.cms.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Integers;

public class BcCMSContentEncryptorBuilder {
  private static Map keySizes = new HashMap<Object, Object>();
  
  private final ASN1ObjectIdentifier encryptionOID;
  
  private final int keySize;
  
  private EnvelopedDataHelper helper = new EnvelopedDataHelper();
  
  private SecureRandom random;
  
  private static int getKeySize(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Integer integer = (Integer)keySizes.get(paramASN1ObjectIdentifier);
    return (integer != null) ? integer.intValue() : -1;
  }
  
  public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, getKeySize(paramASN1ObjectIdentifier));
  }
  
  public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt) {
    this.encryptionOID = paramASN1ObjectIdentifier;
    this.keySize = paramInt;
  }
  
  public BcCMSContentEncryptorBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public OutputEncryptor build() throws CMSException {
    return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.random);
  }
  
  static {
    keySizes.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
    keySizes.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
    keySizes.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
    keySizes.put(CMSAlgorithm.CAMELLIA128_CBC, Integers.valueOf(128));
    keySizes.put(CMSAlgorithm.CAMELLIA192_CBC, Integers.valueOf(192));
    keySizes.put(CMSAlgorithm.CAMELLIA256_CBC, Integers.valueOf(256));
  }
  
  private class CMSOutputEncryptor implements OutputEncryptor {
    private KeyParameter encKey;
    
    private AlgorithmIdentifier algorithmIdentifier;
    
    private Object cipher;
    
    CMSOutputEncryptor(ASN1ObjectIdentifier param1ASN1ObjectIdentifier, int param1Int, SecureRandom param1SecureRandom) throws CMSException {
      if (param1SecureRandom == null)
        param1SecureRandom = new SecureRandom(); 
      CipherKeyGenerator cipherKeyGenerator = BcCMSContentEncryptorBuilder.this.helper.createKeyGenerator(param1ASN1ObjectIdentifier, param1SecureRandom);
      this.encKey = new KeyParameter(cipherKeyGenerator.generateKey());
      this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateAlgorithmIdentifier(param1ASN1ObjectIdentifier, (CipherParameters)this.encKey, param1SecureRandom);
      BcCMSContentEncryptorBuilder.this.helper;
      this.cipher = EnvelopedDataHelper.createContentCipher(true, (CipherParameters)this.encKey, this.algorithmIdentifier);
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
    }
    
    public OutputStream getOutputStream(OutputStream param1OutputStream) {
      return (OutputStream)((this.cipher instanceof BufferedBlockCipher) ? new CipherOutputStream(param1OutputStream, (BufferedBlockCipher)this.cipher) : new CipherOutputStream(param1OutputStream, (StreamCipher)this.cipher));
    }
    
    public GenericKey getKey() {
      return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
    }
  }
}
