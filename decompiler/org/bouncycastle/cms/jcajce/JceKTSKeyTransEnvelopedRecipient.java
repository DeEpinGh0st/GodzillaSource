package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKTSKeyTransEnvelopedRecipient extends JceKTSKeyTransRecipient {
  public JceKTSKeyTransEnvelopedRecipient(PrivateKey paramPrivateKey, KeyTransRecipientId paramKeyTransRecipientId) throws IOException {
    super(paramPrivateKey, getPartyVInfoFromRID(paramKeyTransRecipientId));
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
