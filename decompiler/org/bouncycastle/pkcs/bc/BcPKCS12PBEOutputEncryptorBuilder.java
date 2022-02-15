package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class BcPKCS12PBEOutputEncryptorBuilder {
  private ExtendedDigest digest;
  
  private BufferedBlockCipher engine;
  
  private ASN1ObjectIdentifier algorithm;
  
  private SecureRandom random;
  
  private int iterationCount = 1024;
  
  public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, BlockCipher paramBlockCipher) {
    this(paramASN1ObjectIdentifier, paramBlockCipher, (ExtendedDigest)new SHA1Digest());
  }
  
  public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, BlockCipher paramBlockCipher, ExtendedDigest paramExtendedDigest) {
    this.algorithm = paramASN1ObjectIdentifier;
    this.engine = (BufferedBlockCipher)new PaddedBufferedBlockCipher(paramBlockCipher, (BlockCipherPadding)new PKCS7Padding());
    this.digest = paramExtendedDigest;
  }
  
  public BcPKCS12PBEOutputEncryptorBuilder setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
    return this;
  }
  
  public OutputEncryptor build(final char[] password) {
    if (this.random == null)
      this.random = new SecureRandom(); 
    byte[] arrayOfByte = new byte[20];
    this.random.nextBytes(arrayOfByte);
    final PKCS12PBEParams pbeParams = new PKCS12PBEParams(arrayOfByte, this.iterationCount);
    CipherParameters cipherParameters = PKCS12PBEUtils.createCipherParameters(this.algorithm, this.digest, this.engine.getBlockSize(), pKCS12PBEParams, password);
    this.engine.init(true, cipherParameters);
    return new OutputEncryptor() {
        public AlgorithmIdentifier getAlgorithmIdentifier() {
          return new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pbeParams);
        }
        
        public OutputStream getOutputStream(OutputStream param1OutputStream) {
          return (OutputStream)new CipherOutputStream(param1OutputStream, BcPKCS12PBEOutputEncryptorBuilder.this.engine);
        }
        
        public GenericKey getKey() {
          return new GenericKey(new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, (ASN1Encodable)pbeParams), PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
        }
      };
  }
}
