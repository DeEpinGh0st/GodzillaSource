package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JcePasswordAuthenticatedRecipient extends JcePasswordRecipient {
  public JcePasswordAuthenticatedRecipient(char[] paramArrayOfchar) {
    super(paramArrayOfchar);
  }
  
  public RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, final AlgorithmIdentifier contentMacAlgorithm, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CMSException {
    final Key secretKey = extractSecretKey(paramAlgorithmIdentifier1, contentMacAlgorithm, paramArrayOfbyte1, paramArrayOfbyte2);
    final Mac dataMac = this.helper.createContentMac(key, contentMacAlgorithm);
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
