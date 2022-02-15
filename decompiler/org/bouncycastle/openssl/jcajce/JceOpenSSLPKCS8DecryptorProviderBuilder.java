package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Strings;

public class JceOpenSSLPKCS8DecryptorProviderBuilder {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  public JceOpenSSLPKCS8DecryptorProviderBuilder() {
    this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
  }
  
  public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public InputDecryptorProvider build(final char[] password) throws OperatorCreationException {
    return new InputDecryptorProvider() {
        public InputDecryptor get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
          try {
            final Cipher cipher;
            if (PEMUtilities.isPKCS5Scheme2(algorithm.getAlgorithm())) {
              SecretKey secretKey;
              PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(algorithm.getParameters());
              KeyDerivationFunc keyDerivationFunc = pBES2Parameters.getKeyDerivationFunc();
              EncryptionScheme encryptionScheme = pBES2Parameters.getEncryptionScheme();
              PBKDF2Params pBKDF2Params = (PBKDF2Params)keyDerivationFunc.getParameters();
              int i = pBKDF2Params.getIterationCount().intValue();
              byte[] arrayOfByte = pBKDF2Params.getSalt();
              String str = encryptionScheme.getAlgorithm().getId();
              if (PEMUtilities.isHmacSHA1(pBKDF2Params.getPrf())) {
                secretKey = PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, str, password, arrayOfByte, i);
              } else {
                secretKey = PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, str, password, arrayOfByte, i, pBKDF2Params.getPrf());
              } 
              cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(str);
              AlgorithmParameters algorithmParameters = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(str);
              algorithmParameters.init(encryptionScheme.getParameters().toASN1Primitive().getEncoded());
              cipher.init(2, secretKey, algorithmParameters);
            } else if (PEMUtilities.isPKCS12(algorithm.getAlgorithm())) {
              PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithm.getParameters());
              cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithm.getAlgorithm().getId());
              cipher.init(2, (Key)new PKCS12KeyWithParameters(password, pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue()));
            } else if (PEMUtilities.isPKCS5Scheme1(algorithm.getAlgorithm())) {
              PBEParameter pBEParameter = PBEParameter.getInstance(algorithm.getParameters());
              cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithm.getAlgorithm().getId());
              cipher.init(2, (Key)new PBKDF1KeyWithParameters(password, new CharToByteConverter() {
                      public String getType() {
                        return "ASCII";
                      }
                      
                      public byte[] convert(char[] param2ArrayOfchar) {
                        return Strings.toByteArray(param2ArrayOfchar);
                      }
                    },  pBEParameter.getSalt(), pBEParameter.getIterationCount().intValue()));
            } else {
              throw new PEMException("Unknown algorithm: " + algorithm.getAlgorithm());
            } 
            return new InputDecryptor() {
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return algorithm;
                }
                
                public InputStream getInputStream(InputStream param2InputStream) {
                  return new CipherInputStream(param2InputStream, cipher);
                }
              };
          } catch (IOException iOException) {
            throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + iOException.getMessage(), iOException);
          } catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + generalSecurityException.getMessage(), generalSecurityException);
          } 
        }
      };
  }
}
