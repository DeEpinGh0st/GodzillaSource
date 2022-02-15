package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKeyAgreeEnvelopedRecipient extends JceKeyAgreeRecipient {
  public JceKeyAgreeEnvelopedRecipient(PrivateKey paramPrivateKey) {
    super(paramPrivateKey);
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentEncryptionAlgorithm, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, ASN1OctetString paramASN1OctetString, byte[] paramArrayOfbyte) throws CMSException {
    Key key = extractSecretKey(paramAlgorithmIdentifier1, contentEncryptionAlgorithm, paramSubjectPublicKeyInfo, paramASN1OctetString, paramArrayOfbyte);
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
