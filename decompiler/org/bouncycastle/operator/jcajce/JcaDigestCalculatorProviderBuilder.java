package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaDigestCalculatorProviderBuilder {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  public JcaDigestCalculatorProviderBuilder setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JcaDigestCalculatorProviderBuilder setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public DigestCalculatorProvider build() throws OperatorCreationException {
    return new DigestCalculatorProvider() {
        public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
          final JcaDigestCalculatorProviderBuilder.DigestOutputStream stream;
          try {
            MessageDigest messageDigest = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithm);
            digestOutputStream = new JcaDigestCalculatorProviderBuilder.DigestOutputStream(messageDigest);
          } catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("exception on setup: " + generalSecurityException, generalSecurityException);
          } 
          return new DigestCalculator() {
              public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
              }
              
              public OutputStream getOutputStream() {
                return stream;
              }
              
              public byte[] getDigest() {
                return stream.getDigest();
              }
            };
        }
      };
  }
  
  private class DigestOutputStream extends OutputStream {
    private MessageDigest dig;
    
    DigestOutputStream(MessageDigest param1MessageDigest) {
      this.dig = param1MessageDigest;
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      this.dig.update(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      this.dig.update(param1ArrayOfbyte);
    }
    
    public void write(int param1Int) throws IOException {
      this.dig.update((byte)param1Int);
    }
    
    byte[] getDigest() {
      return this.dig.digest();
    }
  }
}
