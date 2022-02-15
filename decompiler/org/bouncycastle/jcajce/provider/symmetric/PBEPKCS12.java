package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class PBEPKCS12 {
  public static class AlgParams extends BaseAlgorithmParameters {
    PKCS12PBEParams params;
    
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
        return new PBEParameterSpec(this.params.getIV(), this.params.getIterations().intValue()); 
      throw new InvalidParameterSpecException("unknown parameter spec passed to PKCS12 PBE parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof PBEParameterSpec))
        throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PKCS12 PBE parameters algorithm parameters object"); 
      PBEParameterSpec pBEParameterSpec = (PBEParameterSpec)param1AlgorithmParameterSpec;
      this.params = new PKCS12PBEParams(pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      this.params = PKCS12PBEParams.getInstance(ASN1Primitive.fromByteArray(param1ArrayOfbyte));
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (isASN1FormatString(param1String)) {
        engineInit(param1ArrayOfbyte);
        return;
      } 
      throw new IOException("Unknown parameters format in PKCS12 PBE parameters object");
    }
    
    protected String engineToString() {
      return "PKCS12 PBE Parameters";
    }
  }
  
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = PBEPKCS12.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("AlgorithmParameters.PKCS12PBE", PREFIX + "$AlgParams");
    }
  }
}
