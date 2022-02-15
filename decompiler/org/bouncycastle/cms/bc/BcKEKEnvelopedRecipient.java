package org.bouncycastle.cms.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.bc.BcSymmetricKeyUnwrapper;

public class BcKEKEnvelopedRecipient extends BcKEKRecipient {
  public BcKEKEnvelopedRecipient(BcSymmetricKeyUnwrapper paramBcSymmetricKeyUnwrapper) {
    super(paramBcSymmetricKeyUnwrapper);
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] paramArrayOfbyte) throws CMSException {
    KeyParameter keyParameter = (KeyParameter)extractSecretKey(paramAlgorithmIdentifier1, contentEncryptionAlgorithm, paramArrayOfbyte);
    final Object dataCipher = EnvelopedDataHelper.createContentCipher(false, (CipherParameters)keyParameter, contentEncryptionAlgorithm);
    return new RecipientOperator(new InputDecryptor() {
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return contentEncryptionAlgorithm;
          }
          
          public InputStream getInputStream(InputStream param1InputStream) {
            return (InputStream)((dataCipher instanceof BufferedBlockCipher) ? new CipherInputStream(param1InputStream, (BufferedBlockCipher)dataCipher) : new CipherInputStream(param1InputStream, (StreamCipher)dataCipher));
          }
        });
  }
}
