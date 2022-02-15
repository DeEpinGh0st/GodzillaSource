package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSMacCalculatorBuilder {
  private final ASN1ObjectIdentifier macOID;
  
  private final int keySize;
  
  private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
  
  private AlgorithmParameters algorithmParameters;
  
  private SecureRandom random;
  
  public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, -1);
  }
  
  public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt) {
    this.macOID = paramASN1ObjectIdentifier;
    this.keySize = paramInt;
  }
  
  public JceCMSMacCalculatorBuilder setProvider(Provider paramProvider) {
    this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(paramProvider));
    return this;
  }
  
  public JceCMSMacCalculatorBuilder setProvider(String paramString) {
    this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(paramString));
    return this;
  }
  
  public JceCMSMacCalculatorBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public JceCMSMacCalculatorBuilder setAlgorithmParameters(AlgorithmParameters paramAlgorithmParameters) {
    this.algorithmParameters = paramAlgorithmParameters;
    return this;
  }
  
  public MacCalculator build() throws CMSException {
    return new CMSMacCalculator(this.macOID, this.keySize, this.algorithmParameters, this.random);
  }
  
  private class CMSMacCalculator implements MacCalculator {
    private SecretKey encKey;
    
    private AlgorithmIdentifier algorithmIdentifier;
    
    private Mac mac;
    
    CMSMacCalculator(ASN1ObjectIdentifier param1ASN1ObjectIdentifier, int param1Int, AlgorithmParameters param1AlgorithmParameters, SecureRandom param1SecureRandom) throws CMSException {
      KeyGenerator keyGenerator = JceCMSMacCalculatorBuilder.this.helper.createKeyGenerator(param1ASN1ObjectIdentifier);
      if (param1SecureRandom == null)
        param1SecureRandom = new SecureRandom(); 
      if (param1Int < 0) {
        keyGenerator.init(param1SecureRandom);
      } else {
        keyGenerator.init(param1Int, param1SecureRandom);
      } 
      this.encKey = keyGenerator.generateKey();
      if (param1AlgorithmParameters == null)
        param1AlgorithmParameters = JceCMSMacCalculatorBuilder.this.helper.generateParameters(param1ASN1ObjectIdentifier, this.encKey, param1SecureRandom); 
      this.algorithmIdentifier = JceCMSMacCalculatorBuilder.this.helper.getAlgorithmIdentifier(param1ASN1ObjectIdentifier, param1AlgorithmParameters);
      this.mac = JceCMSMacCalculatorBuilder.this.helper.createContentMac(this.encKey, this.algorithmIdentifier);
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
    }
    
    public OutputStream getOutputStream() {
      return (OutputStream)new MacOutputStream(this.mac);
    }
    
    public byte[] getMac() {
      return this.mac.doFinal();
    }
    
    public GenericKey getKey() {
      return (GenericKey)new JceGenericKey(this.algorithmIdentifier, this.encKey);
    }
  }
}
