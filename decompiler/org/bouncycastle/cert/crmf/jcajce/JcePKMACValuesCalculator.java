package org.bouncycastle.cert.crmf.jcajce;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JcePKMACValuesCalculator implements PKMACValuesCalculator {
  private MessageDigest digest;
  
  private Mac mac;
  
  private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  public JcePKMACValuesCalculator setProvider(Provider paramProvider) {
    this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JcePKMACValuesCalculator setProvider(String paramString) {
    this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public void setup(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) throws CRMFException {
    this.digest = this.helper.createDigest(paramAlgorithmIdentifier1.getAlgorithm());
    this.mac = this.helper.createMac(paramAlgorithmIdentifier2.getAlgorithm());
  }
  
  public byte[] calculateDigest(byte[] paramArrayOfbyte) {
    return this.digest.digest(paramArrayOfbyte);
  }
  
  public byte[] calculateMac(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CRMFException {
    try {
      this.mac.init(new SecretKeySpec(paramArrayOfbyte1, this.mac.getAlgorithm()));
      return this.mac.doFinal(paramArrayOfbyte2);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new CRMFException("failure in setup: " + generalSecurityException.getMessage(), generalSecurityException);
    } 
  }
}
