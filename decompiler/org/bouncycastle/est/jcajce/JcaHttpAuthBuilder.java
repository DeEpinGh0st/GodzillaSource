package org.bouncycastle.est.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.est.HttpAuth;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaHttpAuthBuilder {
  private JcaDigestCalculatorProviderBuilder providerBuilder = new JcaDigestCalculatorProviderBuilder();
  
  private final String realm;
  
  private final String username;
  
  private final char[] password;
  
  private SecureRandom random = new SecureRandom();
  
  public JcaHttpAuthBuilder(String paramString, char[] paramArrayOfchar) {
    this(null, paramString, paramArrayOfchar);
  }
  
  public JcaHttpAuthBuilder(String paramString1, String paramString2, char[] paramArrayOfchar) {
    this.realm = paramString1;
    this.username = paramString2;
    this.password = paramArrayOfchar;
  }
  
  public JcaHttpAuthBuilder setProvider(Provider paramProvider) {
    this.providerBuilder.setProvider(paramProvider);
    return this;
  }
  
  public JcaHttpAuthBuilder setProvider(String paramString) {
    this.providerBuilder.setProvider(paramString);
    return this;
  }
  
  public JcaHttpAuthBuilder setNonceGenerator(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public HttpAuth build() throws OperatorCreationException {
    return new HttpAuth(this.realm, this.username, this.password, this.random, this.providerBuilder.build());
  }
}
