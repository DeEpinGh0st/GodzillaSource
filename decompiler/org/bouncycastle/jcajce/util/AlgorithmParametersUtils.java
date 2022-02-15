package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

public class AlgorithmParametersUtils {
  public static ASN1Encodable extractParameters(AlgorithmParameters paramAlgorithmParameters) throws IOException {
    ASN1Primitive aSN1Primitive;
    try {
      aSN1Primitive = ASN1Primitive.fromByteArray(paramAlgorithmParameters.getEncoded("ASN.1"));
    } catch (Exception exception) {
      aSN1Primitive = ASN1Primitive.fromByteArray(paramAlgorithmParameters.getEncoded());
    } 
    return (ASN1Encodable)aSN1Primitive;
  }
  
  public static void loadParameters(AlgorithmParameters paramAlgorithmParameters, ASN1Encodable paramASN1Encodable) throws IOException {
    try {
      paramAlgorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded(), "ASN.1");
    } catch (Exception exception) {
      paramAlgorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded());
    } 
  }
}
