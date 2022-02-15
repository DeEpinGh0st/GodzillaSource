package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.DSAParameter;

public class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  DSAParameterSpec currentSpec;
  
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected AlgorithmParameterSpec engineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException {
    if (paramClass == null)
      throw new NullPointerException("argument to getParameterSpec must not be null"); 
    return localEngineGetParameterSpec(paramClass);
  }
  
  protected byte[] engineGetEncoded() {
    DSAParameter dSAParameter = new DSAParameter(this.currentSpec.getP(), this.currentSpec.getQ(), this.currentSpec.getG());
    try {
      return dSAParameter.getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException("Error encoding DSAParameters");
    } 
  }
  
  protected byte[] engineGetEncoded(String paramString) {
    return isASN1FormatString(paramString) ? engineGetEncoded() : null;
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<DSAParameterSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == DSAParameterSpec.class || paramClass == AlgorithmParameterSpec.class)
      return this.currentSpec; 
    throw new InvalidParameterSpecException("unknown parameter spec passed to DSA parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec))
      throw new InvalidParameterSpecException("DSAParameterSpec required to initialise a DSA algorithm parameters object"); 
    this.currentSpec = (DSAParameterSpec)paramAlgorithmParameterSpec;
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    try {
      DSAParameter dSAParameter = DSAParameter.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
      this.currentSpec = new DSAParameterSpec(dSAParameter.getP(), dSAParameter.getQ(), dSAParameter.getG());
    } catch (ClassCastException classCastException) {
      throw new IOException("Not a valid DSA Parameter encoding.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new IOException("Not a valid DSA Parameter encoding.");
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
    return "DSA Parameters";
  }
}
