package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class PBEPBKDF1 {
  public static class AlgParams extends BaseAlgorithmParameters {
    PBEParameter params;
    
    protected byte[] engineGetEncoded() {
      try {
        return this.params.getEncoded("DER");
      } catch (IOException iOException) {
        throw new RuntimeException("Oooops! " + iOException.toString());
      } 
    }
    
    protected byte[] engineGetEncoded(String param1String) {
      return isASN1FormatString(param1String) ? engineGetEncoded() : null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<PBEParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == PBEParameterSpec.class)
        return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue()); 
      throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF1 PBE parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF1 PBE parameters algorithm parameters object"); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      this.params = new PBEParameter(pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.params = PBEParameter.getInstance(param1ArrayOfbyte);
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (isASN1FormatString(param1String)) {
        engineInit(param1ArrayOfbyte);
        return;
      } 
      throw new IOException("Unknown parameters format in PBKDF2 parameters object");
    }
    
    protected String engineToString() {
      return "PBKDF1 Parameters";
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = PBEPBKDF1.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.PBKDF1", PREFIX + "$AlgParams");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBKDF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBKDF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC, "PBKDF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBKDF1");
      param1ConfigurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC, "PBKDF1");
    }
  }
}
