package org.bouncycastle.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Integers;

public class PasswordRecipientInformation extends RecipientInformation {
  static Map KEYSIZES = new HashMap<Object, Object>();
  
  static Map BLOCKSIZES = new HashMap<Object, Object>();
  
  private PasswordRecipientInfo info;
  
  PasswordRecipientInformation(PasswordRecipientInfo paramPasswordRecipientInfo, AlgorithmIdentifier paramAlgorithmIdentifier, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    super(paramPasswordRecipientInfo.getKeyEncryptionAlgorithm(), paramAlgorithmIdentifier, paramCMSSecureReadable, paramAuthAttributesProvider);
    this.info = paramPasswordRecipientInfo;
    this.rid = new PasswordRecipientId();
  }
  
  public String getKeyDerivationAlgOID() {
    return (this.info.getKeyDerivationAlgorithm() != null) ? this.info.getKeyDerivationAlgorithm().getAlgorithm().getId() : null;
  }
  
  public byte[] getKeyDerivationAlgParams() {
    try {
      if (this.info.getKeyDerivationAlgorithm() != null) {
        ASN1Encodable aSN1Encodable = this.info.getKeyDerivationAlgorithm().getParameters();
        if (aSN1Encodable != null)
          return aSN1Encodable.toASN1Primitive().getEncoded(); 
      } 
      return null;
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public AlgorithmIdentifier getKeyDerivationAlgorithm() {
    return this.info.getKeyDerivationAlgorithm();
  }
  
  protected RecipientOperator getRecipientOperator(Recipient paramRecipient) throws CMSException, IOException {
    PasswordRecipient passwordRecipient = (PasswordRecipient)paramRecipient;
    AlgorithmIdentifier algorithmIdentifier1 = AlgorithmIdentifier.getInstance(this.info.getKeyEncryptionAlgorithm());
    AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(algorithmIdentifier1.getParameters());
    int i = ((Integer)KEYSIZES.get(algorithmIdentifier2.getAlgorithm())).intValue();
    byte[] arrayOfByte = passwordRecipient.calculateDerivedKey(passwordRecipient.getPasswordConversionScheme(), getKeyDerivationAlgorithm(), i);
    return passwordRecipient.getRecipientOperator(algorithmIdentifier2, this.messageAlgorithm, arrayOfByte, this.info.getEncryptedKey().getOctets());
  }
  
  static {
    BLOCKSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(8));
    BLOCKSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(16));
    BLOCKSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(16));
    BLOCKSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(16));
    KEYSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(192));
    KEYSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
    KEYSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
    KEYSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
  }
}
