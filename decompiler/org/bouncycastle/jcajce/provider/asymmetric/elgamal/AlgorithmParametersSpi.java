package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public class AlgorithmParametersSpi extends BaseAlgorithmParameters {
  ElGamalParameterSpec currentSpec;
  
  protected byte[] engineGetEncoded() {
    ElGamalParameter elGamalParameter = new ElGamalParameter(this.currentSpec.getP(), this.currentSpec.getG());
    try {
      return elGamalParameter.getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException("Error encoding ElGamalParameters");
    } 
  }
  
  protected byte[] engineGetEncoded(String paramString) {
    return (isASN1FormatString(paramString) || paramString.equalsIgnoreCase("X.509")) ? engineGetEncoded() : null;
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<ElGamalParameterSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == ElGamalParameterSpec.class || paramClass == AlgorithmParameterSpec.class)
      return (AlgorithmParameterSpec)this.currentSpec; 
    if (paramClass == DHParameterSpec.class)
      return new DHParameterSpec(this.currentSpec.getP(), this.currentSpec.getG()); 
    throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof ElGamalParameterSpec) && !(paramAlgorithmParameterSpec instanceof DHParameterSpec))
      throw new InvalidParameterSpecException("DHParameterSpec required to initialise a ElGamal algorithm parameters object"); 
    if (paramAlgorithmParameterSpec instanceof ElGamalParameterSpec) {
      this.currentSpec = (ElGamalParameterSpec)paramAlgorithmParameterSpec;
    } else {
      DHParameterSpec dHParameterSpec = (DHParameterSpec)paramAlgorithmParameterSpec;
      this.currentSpec = new ElGamalParameterSpec(dHParameterSpec.getP(), dHParameterSpec.getG());
    } 
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    try {
      ElGamalParameter elGamalParameter = ElGamalParameter.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
      this.currentSpec = new ElGamalParameterSpec(elGamalParameter.getP(), elGamalParameter.getG());
    } catch (ClassCastException classCastException) {
      throw new IOException("Not a valid ElGamal Parameter encoding.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new IOException("Not a valid ElGamal Parameter encoding.");
    } 
  }
  
  protected void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException {
    if (isASN1FormatString(paramString) || paramString.equalsIgnoreCase("X.509")) {
      engineInit(paramArrayOfbyte);
    } else {
      throw new IOException("Unknown parameter format " + paramString);
    } 
  }
  
  protected String engineToString() {
    return "ElGamal Parameters";
  }
}
