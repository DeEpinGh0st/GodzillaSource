package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceKTSKeyTransAuthenticatedRecipient extends JceKTSKeyTransRecipient {
  public JceKTSKeyTransAuthenticatedRecipient(PrivateKey paramPrivateKey, KeyTransRecipientId paramKeyTransRecipientId) throws IOException {
    super(paramPrivateKey, getPartyVInfoFromRID(paramKeyTransRecipientId));
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentMacAlgorithm, byte[] paramArrayOfbyte) throws CMSException {
    final Key secretKey = extractSecretKey(paramAlgorithmIdentifier1, contentMacAlgorithm, paramArrayOfbyte);
    final Mac dataMac = this.contentHelper.createContentMac(key, contentMacAlgorithm);
    return new RecipientOperator(new MacCalculator() {
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return contentMacAlgorithm;
          }
          
          public GenericKey getKey() {
            return (GenericKey)new JceGenericKey(contentMacAlgorithm, secretKey);
          }
          
          public OutputStream getOutputStream() {
            return (OutputStream)new MacOutputStream(dataMac);
          }
          
          public byte[] getMac() {
            return dataMac.doFinal();
          }
        });
  }
}
