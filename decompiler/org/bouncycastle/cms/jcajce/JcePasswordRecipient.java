package org.bouncycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;

public abstract class JcePasswordRecipient implements PasswordRecipient {
  private int schemeID = 1;
  
  protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  private char[] password;
  
  JcePasswordRecipient(char[] paramArrayOfchar) {
    this.password = paramArrayOfchar;
  }
  
  public JcePasswordRecipient setPasswordConversionScheme(int paramInt) {
    this.schemeID = paramInt;
    return this;
  }
  
  public JcePasswordRecipient setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JcePasswordRecipient setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  protected Key extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CMSException {
    Cipher cipher = this.helper.createRFC3211Wrapper(paramAlgorithmIdentifier1.getAlgorithm());
    try {
      IvParameterSpec ivParameterSpec = new IvParameterSpec(ASN1OctetString.getInstance(paramAlgorithmIdentifier1.getParameters()).getOctets());
      cipher.init(4, new SecretKeySpec(paramArrayOfbyte1, cipher.getAlgorithm()), ivParameterSpec);
      return cipher.unwrap(paramArrayOfbyte2, paramAlgorithmIdentifier2.getAlgorithm().getId(), 3);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot process content encryption key: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
  
  public byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException {
    return this.helper.calculateDerivedKey(paramInt1, this.password, paramAlgorithmIdentifier, paramInt2);
  }
  
  public int getPasswordConversionScheme() {
    return this.schemeID;
  }
  
  public char[] getPassword() {
    return this.password;
  }
}
