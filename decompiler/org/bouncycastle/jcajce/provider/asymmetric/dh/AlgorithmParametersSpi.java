package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.pkcs.DHParameter;

public class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  DHParameterSpec currentSpec;
  
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected AlgorithmParameterSpec engineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException {
    if (paramClass == null)
      throw new NullPointerException("argument to getParameterSpec must not be null"); 
    return localEngineGetParameterSpec(paramClass);
  }
  
  protected byte[] engineGetEncoded() {
    DHParameter dHParameter = new DHParameter(this.currentSpec.getP(), this.currentSpec.getG(), this.currentSpec.getL());
    try {
      return dHParameter.getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException("Error encoding DHParameters");
    } 
  }
  
  protected byte[] engineGetEncoded(String paramString) {
    return isASN1FormatString(paramString) ? engineGetEncoded() : null;
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<DHParameterSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == DHParameterSpec.class || paramClass == AlgorithmParameterSpec.class)
      return this.currentSpec; 
    throw new InvalidParameterSpecException("unknown parameter spec passed to DH parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof DHParameterSpec))
      throw new InvalidParameterSpecException("DHParameterSpec required to initialise a Diffie-Hellman algorithm parameters object"); 
    this.currentSpec = (DHParameterSpec)paramAlgorithmParameterSpec;
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    try {
      DHParameter dHParameter = DHParameter.getInstance(paramArrayOfbyte);
      if (dHParameter.getL() != null) {
        this.currentSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG(), dHParameter.getL().intValue());
      } else {
        this.currentSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG());
      } 
    } catch (ClassCastException classCastException) {
      throw new IOException("Not a valid DH Parameter encoding.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new IOException("Not a valid DH Parameter encoding.");
    } 
  }
  
  protected void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException {
    if (isASN1FormatString(paramString)) {
      engineInit(paramArrayOfbyte);
    } else {
      throw new IOException("Unknown parameter format " + paramString);
    } 
  }
  
  protected String engineToString() {
    return "Diffie-Hellman Parameters";
  }
}
