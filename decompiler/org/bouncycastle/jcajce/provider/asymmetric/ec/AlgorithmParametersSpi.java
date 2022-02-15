package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;

public class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  private ECParameterSpec ecParameterSpec;
  
  private String curveName;
  
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (paramAlgorithmParameterSpec instanceof ECGenParameterSpec) {
      ECGenParameterSpec eCGenParameterSpec = (ECGenParameterSpec)paramAlgorithmParameterSpec;
      X9ECParameters x9ECParameters = ECUtils.getDomainParametersFromGenSpec(eCGenParameterSpec);
      if (x9ECParameters == null)
        throw new InvalidParameterSpecException("EC curve name not recognized: " + eCGenParameterSpec.getName()); 
      this.curveName = eCGenParameterSpec.getName();
      this.ecParameterSpec = EC5Util.convertToSpec(x9ECParameters);
    } else if (paramAlgorithmParameterSpec instanceof ECParameterSpec) {
      if (paramAlgorithmParameterSpec instanceof ECNamedCurveSpec) {
        this.curveName = ((ECNamedCurveSpec)paramAlgorithmParameterSpec).getName();
      } else {
        this.curveName = null;
      } 
      this.ecParameterSpec = (ECParameterSpec)paramAlgorithmParameterSpec;
    } else {
      throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramAlgorithmParameterSpec.getClass().getName());
    } 
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    engineInit(paramArrayOfbyte, "ASN.1");
  }
  
  protected void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException {
    if (isASN1FormatString(paramString)) {
      X962Parameters x962Parameters = X962Parameters.getInstance(paramArrayOfbyte);
      ECCurve eCCurve = EC5Util.getCurve(BouncyCastleProvider.CONFIGURATION, x962Parameters);
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
        this.curveName = ECNamedCurveTable.getName(aSN1ObjectIdentifier);
        if (this.curveName == null)
          this.curveName = aSN1ObjectIdentifier.getId(); 
      } 
      this.ecParameterSpec = EC5Util.convertToSpec(x962Parameters, eCCurve);
    } else {
      throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + paramString);
    } 
  }
  
  protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramClass) throws InvalidParameterSpecException {
    if (ECParameterSpec.class.isAssignableFrom(paramClass) || paramClass == AlgorithmParameterSpec.class)
      return (T)this.ecParameterSpec; 
    if (ECGenParameterSpec.class.isAssignableFrom(paramClass)) {
      if (this.curveName != null) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier1 = ECUtil.getNamedCurveOid(this.curveName);
        return (T)((aSN1ObjectIdentifier1 != null) ? new ECGenParameterSpec(aSN1ObjectIdentifier1.getId()) : new ECGenParameterSpec(this.curveName));
      } 
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ECUtil.getNamedCurveOid(EC5Util.convertSpec(this.ecParameterSpec, false));
      if (aSN1ObjectIdentifier != null)
        return (T)new ECGenParameterSpec(aSN1ObjectIdentifier.getId()); 
    } 
    throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + paramClass.getName());
  }
  
  protected byte[] engineGetEncoded() throws IOException {
    return engineGetEncoded("ASN.1");
  }
  
  protected byte[] engineGetEncoded(String paramString) throws IOException {
    if (isASN1FormatString(paramString)) {
      X962Parameters x962Parameters;
      if (this.ecParameterSpec == null) {
        x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
      } else if (this.curveName != null) {
        x962Parameters = new X962Parameters(ECUtil.getNamedCurveOid(this.curveName));
      } else {
        ECParameterSpec eCParameterSpec = EC5Util.convertSpec(this.ecParameterSpec, false);
        X9ECParameters x9ECParameters = new X9ECParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH(), eCParameterSpec.getSeed());
        x962Parameters = new X962Parameters(x9ECParameters);
      } 
      return x962Parameters.getEncoded();
    } 
    throw new IOException("Unknown parameters format in AlgorithmParameters object: " + paramString);
  }
  
  protected String engineToString() {
    return "EC AlgorithmParameters ";
  }
}
