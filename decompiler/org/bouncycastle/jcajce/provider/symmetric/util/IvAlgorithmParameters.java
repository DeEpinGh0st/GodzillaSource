package org.bouncycastle.jcajce.provider.symmetric.util;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class IvAlgorithmParameters extends BaseAlgorithmParameters {
  private byte[] iv;
  
  protected byte[] engineGetEncoded() throws IOException {
    return engineGetEncoded("ASN.1");
  }
  
  protected byte[] engineGetEncoded(String paramString) throws IOException {
    return isASN1FormatString(paramString) ? (new DEROctetString(engineGetEncoded("RAW"))).getEncoded() : (paramString.equals("RAW") ? Arrays.clone(this.iv) : null);
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IvParameterSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == IvParameterSpec.class || paramClass == AlgorithmParameterSpec.class)
      return new IvParameterSpec(this.iv); 
    throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof IvParameterSpec))
      throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object"); 
    this.iv = ((IvParameterSpec)paramAlgorithmParameterSpec).getIV();
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte.length % 8 != 0 && paramArrayOfbyte[0] == 4 && paramArrayOfbyte[1] == paramArrayOfbyte.length - 2) {
      ASN1OctetString aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(paramArrayOfbyte);
      paramArrayOfbyte = aSN1OctetString.getOctets();
    } 
    this.iv = Arrays.clone(paramArrayOfbyte);
  }
  
  protected void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException {
    if (isASN1FormatString(paramString)) {
      try {
        ASN1OctetString aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(paramArrayOfbyte);
        engineInit(aSN1OctetString.getOctets());
      } catch (Exception exception) {
        throw new IOException("Exception decoding: " + exception);
      } 
      return;
    } 
    if (paramString.equals("RAW")) {
      engineInit(paramArrayOfbyte);
      return;
    } 
    throw new IOException("Unknown parameters format in IV parameters object");
  }
  
  protected String engineToString() {
    return "IV Parameters";
  }
}
