package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class JcePKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private ASN1ObjectIdentifier algorithm;
  
  private SecureRandom random;
  
  private int saltLength;
  
  private int iterationCount = 1024;
  
  public JcePKCS12MacCalculatorBuilder() {
    this(OIWObjectIdentifiers.idSHA1);
  }
  
  public JcePKCS12MacCalculatorBuilder(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.algorithm = paramASN1ObjectIdentifier;
  }
  
  public JcePKCS12MacCalculatorBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcePKCS12MacCalculatorBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JcePKCS12MacCalculatorBuilder setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
    return this;
  }
  
  public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
    return new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)DERNull.INSTANCE);
  }
  
  public MacCalculator build(char[] paramArrayOfchar) throws OperatorCreationException {
    if (this.random == null)
      this.random = new SecureRandom(); 
    try {
      final Mac mac = this.helper.createMac(this.algorithm.getId());
      this.saltLength = mac.getMacLength();
      final byte[] salt = new byte[this.saltLength];
      this.random.nextBytes(arrayOfByte);
      PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(arrayOfByte, this.iterationCount);
      final PKCS12Key key = new PKCS12Key(paramArrayOfchar);
      mac.init((Key)pKCS12Key, pBEParameterSpec);
      return new MacCalculator() {
          public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(JcePKCS12MacCalculatorBuilder.this.algorithm, (ASN1Encodable)new PKCS12PBEParams(salt, JcePKCS12MacCalculatorBuilder.this.iterationCount));
          }
          
          public OutputStream getOutputStream() {
            return (OutputStream)new MacOutputStream(mac);
          }
          
          public byte[] getMac() {
            return mac.doFinal();
          }
          
          public GenericKey getKey() {
            return new GenericKey(getAlgorithmIdentifier(), key.getEncoded());
          }
        };
    } catch (Exception exception) {
      throw new OperatorCreationException("unable to create MAC calculator: " + exception.getMessage(), exception);
    } 
  }
}
