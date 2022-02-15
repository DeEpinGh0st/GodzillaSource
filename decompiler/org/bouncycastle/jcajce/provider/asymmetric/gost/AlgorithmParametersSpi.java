package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  GOST3410ParameterSpec currentSpec;
  
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected AlgorithmParameterSpec engineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException {
    if (paramClass == null)
      throw new NullPointerException("argument to getParameterSpec must not be null"); 
    return localEngineGetParameterSpec(paramClass);
  }
  
  protected byte[] engineGetEncoded() {
    GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.currentSpec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getDigestParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getEncryptionParamSetOID()));
    try {
      return gOST3410PublicKeyAlgParameters.getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException("Error encoding GOST3410Parameters");
    } 
  }
  
  protected byte[] engineGetEncoded(String paramString) {
    return (isASN1FormatString(paramString) || paramString.equalsIgnoreCase("X.509")) ? engineGetEncoded() : null;
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<GOST3410PublicKeyParameterSetSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == GOST3410PublicKeyParameterSetSpec.class || paramClass == AlgorithmParameterSpec.class)
      return (AlgorithmParameterSpec)this.currentSpec; 
    throw new InvalidParameterSpecException("unknown parameter spec passed to GOST3410 parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof GOST3410ParameterSpec))
      throw new InvalidParameterSpecException("GOST3410ParameterSpec required to initialise a GOST3410 algorithm parameters object"); 
    this.currentSpec = (GOST3410ParameterSpec)paramAlgorithmParameterSpec;
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    try {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(paramArrayOfbyte);
      this.currentSpec = GOST3410ParameterSpec.fromPublicKeyAlg(new GOST3410PublicKeyAlgParameters(aSN1Sequence));
    } catch (ClassCastException classCastException) {
      throw new IOException("Not a valid GOST3410 Parameter encoding.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new IOException("Not a valid GOST3410 Parameter encoding.");
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
    return "GOST3410 Parameters";
  }
}
