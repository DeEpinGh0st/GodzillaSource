package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

class ECUtils {
  static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    return (paramPublicKey instanceof BCECPublicKey) ? (AsymmetricKeyParameter)((BCECPublicKey)paramPublicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(paramPublicKey);
  }
  
  static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec paramECGenParameterSpec) {
    return getDomainParametersFromName(paramECGenParameterSpec.getName());
  }
  
  static X9ECParameters getDomainParametersFromName(String paramString) {
    X9ECParameters x9ECParameters;
    try {
      if (paramString.charAt(0) >= '0' && paramString.charAt(0) <= '2') {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(paramString);
        x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
      } else if (paramString.indexOf(' ') > 0) {
        paramString = paramString.substring(paramString.indexOf(' ') + 1);
        x9ECParameters = ECUtil.getNamedCurveByName(paramString);
      } else {
        x9ECParameters = ECUtil.getNamedCurveByName(paramString);
      } 
    } catch (IllegalArgumentException illegalArgumentException) {
      x9ECParameters = ECUtil.getNamedCurveByName(paramString);
    } 
    return x9ECParameters;
  }
  
  static X962Parameters getDomainParametersFromName(ECParameterSpec paramECParameterSpec, boolean paramBoolean) {
    X962Parameters x962Parameters;
    if (paramECParameterSpec instanceof ECNamedCurveSpec) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)paramECParameterSpec).getName());
      if (aSN1ObjectIdentifier == null)
        aSN1ObjectIdentifier = new ASN1ObjectIdentifier(((ECNamedCurveSpec)paramECParameterSpec).getName()); 
      x962Parameters = new X962Parameters(aSN1ObjectIdentifier);
    } else if (paramECParameterSpec == null) {
      x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
    } else {
      ECCurve eCCurve = EC5Util.convertCurve(paramECParameterSpec.getCurve());
      X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, paramECParameterSpec.getGenerator(), paramBoolean), paramECParameterSpec.getOrder(), BigInteger.valueOf(paramECParameterSpec.getCofactor()), paramECParameterSpec.getCurve().getSeed());
      x962Parameters = new X962Parameters(x9ECParameters);
    } 
    return x962Parameters;
  }
}
