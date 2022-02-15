package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKEKEnvelopedRecipient extends JceKEKRecipient {
  public JceKEKEnvelopedRecipient(SecretKey paramSecretKey) {
    super(paramSecretKey);
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] paramArrayOfbyte) throws CMSException {
    Key key = extractSecretKey(paramAlgorithmIdentifier1, contentEncryptionAlgorithm, paramArrayOfbyte);
    final Cipher dataCipher = this.contentHelper.createContentCipher(key, contentEncryptionAlgorithm);
    return new RecipientOperator(new InputDecryptor() {
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return contentEncryptionAlgorithm;
          }
          
          public InputStream getInputStream(InputStream param1InputStream) {
            return (InputStream)new CipherInputStream(param1InputStream, dataCipher);
          }
        });
  }
}
