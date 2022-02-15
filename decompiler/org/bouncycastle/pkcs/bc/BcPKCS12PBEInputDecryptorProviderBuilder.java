package org.bouncycastle.pkcs.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;

public class BcPKCS12PBEInputDecryptorProviderBuilder {
  private ExtendedDigest digest;
  
  public BcPKCS12PBEInputDecryptorProviderBuilder() {
    this((ExtendedDigest)new SHA1Digest());
  }
  
  public BcPKCS12PBEInputDecryptorProviderBuilder(ExtendedDigest paramExtendedDigest) {
    this.digest = paramExtendedDigest;
  }
  
  public InputDecryptorProvider build(final char[] password) {
    return new InputDecryptorProvider() {
        public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier) {
          final PaddedBufferedBlockCipher engine = PKCS12PBEUtils.getEngine(algorithmIdentifier.getAlgorithm());
          PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
          CipherParameters cipherParameters = PKCS12PBEUtils.createCipherParameters(algorithmIdentifier.getAlgorithm(), BcPKCS12PBEInputDecryptorProviderBuilder.this.digest, paddedBufferedBlockCipher.getBlockSize(), pKCS12PBEParams, password);
          paddedBufferedBlockCipher.init(false, cipherParameters);
          return new InputDecryptor() {
              public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier;
              }
              
              public InputStream getInputStream(InputStream param2InputStream) {
                return (InputStream)new CipherInputStream(param2InputStream, (BufferedBlockCipher)engine);
              }
              
              public GenericKey getKey() {
                return new GenericKey(PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
              }
            };
        }
      };
  }
}
