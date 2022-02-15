package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
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
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class JcePKCS12MacCalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  public JcePKCS12MacCalculatorBuilderProvider setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcePKCS12MacCalculatorBuilderProvider setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
    return new PKCS12MacCalculatorBuilder() {
        public MacCalculator build(char[] param1ArrayOfchar) throws OperatorCreationException {
          final PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
          try {
            final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
            final Mac mac = JcePKCS12MacCalculatorBuilderProvider.this.helper.createMac(aSN1ObjectIdentifier.getId());
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
            final PKCS12Key key = new PKCS12Key(param1ArrayOfchar);
            mac.init((Key)pKCS12Key, pBEParameterSpec);
            return new MacCalculator() {
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return new AlgorithmIdentifier(algorithm, (ASN1Encodable)pbeParams);
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
        
        public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
          return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
        }
      };
  }
}
