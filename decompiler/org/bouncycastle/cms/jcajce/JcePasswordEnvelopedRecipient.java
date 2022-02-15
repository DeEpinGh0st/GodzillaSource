package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JcePasswordEnvelopedRecipient extends JcePasswordRecipient {
  public JcePasswordEnvelopedRecipient(char[] paramArrayOfchar) {
    super(paramArrayOfchar);
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CMSException {
    Key key = extractSecretKey(paramAlgorithmIdentifier1, contentEncryptionAlgorithm, paramArrayOfbyte1, paramArrayOfbyte2);
    final Cipher dataCipher = this.helper.createContentCipher(key, contentEncryptionAlgorithm);
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
