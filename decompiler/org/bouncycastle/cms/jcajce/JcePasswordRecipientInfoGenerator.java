package org.bouncycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;

public class JcePasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator {
  private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  public JcePasswordRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier, char[] paramArrayOfchar) {
    super(paramASN1ObjectIdentifier, paramArrayOfchar);
  }
  
  public JcePasswordRecipientInfoGenerator setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JcePasswordRecipientInfoGenerator setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  protected byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException {
    return this.helper.calculateDerivedKey(paramInt1, this.password, paramAlgorithmIdentifier, paramInt2);
  }
  
  public byte[] generateEncryptedBytes(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte, GenericKey paramGenericKey) throws CMSException {
    Key key = this.helper.getJceKey(paramGenericKey);
    Cipher cipher = this.helper.createRFC3211Wrapper(paramAlgorithmIdentifier.getAlgorithm());
    try {
      IvParameterSpec ivParameterSpec = new IvParameterSpec(ASN1OctetString.getInstance(paramAlgorithmIdentifier.getParameters()).getOctets());
      cipher.init(3, new SecretKeySpec(paramArrayOfbyte, cipher.getAlgorithm()), ivParameterSpec);
      return cipher.wrap(key);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CMSException("cannot process content encryption key: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
}
