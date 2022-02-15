package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class EC5Util {
  private static Map customCurves = new HashMap<Object, Object>();
  
  public static ECCurve getCurve(ProviderConfiguration paramProviderConfiguration, X962Parameters paramX962Parameters) {
    ECCurve eCCurve;
    Set set = paramProviderConfiguration.getAcceptableNamedCurves();
    if (paramX962Parameters.isNamedCurve()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(paramX962Parameters.getParameters());
      if (set.isEmpty() || set.contains(aSN1ObjectIdentifier)) {
        X9ECParameters x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
        if (x9ECParameters == null)
          x9ECParameters = (X9ECParameters)paramProviderConfiguration.getAdditionalECParameters().get(aSN1ObjectIdentifier); 
        eCCurve = x9ECParameters.getCurve();
      } else {
        throw new IllegalStateException("named curve not acceptable");
      } 
    } else if (paramX962Parameters.isImplicitlyCA()) {
      eCCurve = paramProviderConfiguration.getEcImplicitlyCa().getCurve();
    } else if (set.isEmpty()) {
      X9ECParameters x9ECParameters = X9ECParameters.getInstance(paramX962Parameters.getParameters());
      eCCurve = x9ECParameters.getCurve();
    } else {
      throw new IllegalStateException("encoded parameters not acceptable");
    } 
    return eCCurve;
  }
  
  public static ECDomainParameters getDomainParameters(ProviderConfiguration paramProviderConfiguration, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters;
    if (paramECParameterSpec == null) {
      ECParameterSpec eCParameterSpec = paramProviderConfiguration.getEcImplicitlyCa();
      eCDomainParameters = new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH(), eCParameterSpec.getSeed());
    } else {
      eCDomainParameters = ECUtil.getDomainParameters(paramProviderConfiguration, convertSpec(paramECParameterSpec, false));
    } 
    return eCDomainParameters;
  }
  
  public static ECParameterSpec convertToSpec(X962Parameters paramX962Parameters, ECCurve paramECCurve) {
    ECParameterSpec eCParameterSpec;
    if (paramX962Parameters.isNamedCurve()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)paramX962Parameters.getParameters();
      X9ECParameters x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
      if (x9ECParameters == null) {
        Map map = BouncyCastleProvider.CONFIGURATION.getAdditionalECParameters();
        if (!map.isEmpty())
          x9ECParameters = (X9ECParameters)map.get(aSN1ObjectIdentifier); 
      } 
      EllipticCurve ellipticCurve = convertCurve(paramECCurve, x9ECParameters.getSeed());
      ECNamedCurveSpec eCNamedCurveSpec = new ECNamedCurveSpec(ECUtil.getCurveName(aSN1ObjectIdentifier), ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH());
    } else if (paramX962Parameters.isImplicitlyCA()) {
      eCParameterSpec = null;
    } else {
      X9ECParameters x9ECParameters = X9ECParameters.getInstance(paramX962Parameters.getParameters());
      EllipticCurve ellipticCurve = convertCurve(paramECCurve, x9ECParameters.getSeed());
      if (x9ECParameters.getH() != null) {
        eCParameterSpec = new ECParameterSpec(ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
      } else {
        eCParameterSpec = new ECParameterSpec(ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), 1);
      } 
    } 
    return eCParameterSpec;
  }
  
  public static ECParameterSpec convertToSpec(X9ECParameters paramX9ECParameters) {
    return new ECParameterSpec(convertCurve(paramX9ECParameters.getCurve(), null), new ECPoint(paramX9ECParameters.getG().getAffineXCoord().toBigInteger(), paramX9ECParameters.getG().getAffineYCoord().toBigInteger()), paramX9ECParameters.getN(), paramX9ECParameters.getH().intValue());
  }
  
  public static EllipticCurve convertCurve(ECCurve paramECCurve, byte[] paramArrayOfbyte) {
    ECField eCField = convertField(paramECCurve.getField());
    BigInteger bigInteger1 = paramECCurve.getA().toBigInteger();
    BigInteger bigInteger2 = paramECCurve.getB().toBigInteger();
    return new EllipticCurve(eCField, bigInteger1, bigInteger2, null);
  }
  
  public static ECCurve convertCurve(EllipticCurve paramEllipticCurve) {
    ECField eCField = paramEllipticCurve.getField();
    BigInteger bigInteger1 = paramEllipticCurve.getA();
    BigInteger bigInteger2 = paramEllipticCurve.getB();
    if (eCField instanceof ECFieldFp) {
      ECCurve.Fp fp = new ECCurve.Fp(((ECFieldFp)eCField).getP(), bigInteger1, bigInteger2);
      return (ECCurve)(customCurves.containsKey(fp) ? customCurves.get(fp) : fp);
    } 
    ECFieldF2m eCFieldF2m = (ECFieldF2m)eCField;
    int i = eCFieldF2m.getM();
    int[] arrayOfInt = ECUtil.convertMidTerms(eCFieldF2m.getMidTermsOfReductionPolynomial());
    return (ECCurve)new ECCurve.F2m(i, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], bigInteger1, bigInteger2);
  }
  
  public static ECField convertField(FiniteField paramFiniteField) {
    if (ECAlgorithms.isFpField(paramFiniteField))
      return new ECFieldFp(paramFiniteField.getCharacteristic()); 
    Polynomial polynomial = ((PolynomialExtensionField)paramFiniteField).getMinimalPolynomial();
    int[] arrayOfInt1 = polynomial.getExponentsPresent();
    int[] arrayOfInt2 = Arrays.reverse(Arrays.copyOfRange(arrayOfInt1, 1, arrayOfInt1.length - 1));
    return new ECFieldF2m(polynomial.getDegree(), arrayOfInt2);
  }
  
  public static ECParameterSpec convertSpec(EllipticCurve paramEllipticCurve, ECParameterSpec paramECParameterSpec) {
    return (ECParameterSpec)((paramECParameterSpec instanceof ECNamedCurveParameterSpec) ? new ECNamedCurveSpec(((ECNamedCurveParameterSpec)paramECParameterSpec).getName(), paramEllipticCurve, new ECPoint(paramECParameterSpec.getG().getAffineXCoord().toBigInteger(), paramECParameterSpec.getG().getAffineYCoord().toBigInteger()), paramECParameterSpec.getN(), paramECParameterSpec.getH()) : new ECParameterSpec(paramEllipticCurve, new ECPoint(paramECParameterSpec.getG().getAffineXCoord().toBigInteger(), paramECParameterSpec.getG().getAffineYCoord().toBigInteger()), paramECParameterSpec.getN(), paramECParameterSpec.getH().intValue()));
  }
  
  public static ECParameterSpec convertSpec(ECParameterSpec paramECParameterSpec, boolean paramBoolean) {
    ECCurve eCCurve = convertCurve(paramECParameterSpec.getCurve());
    return new ECParameterSpec(eCCurve, convertPoint(eCCurve, paramECParameterSpec.getGenerator(), paramBoolean), paramECParameterSpec.getOrder(), BigInteger.valueOf(paramECParameterSpec.getCofactor()), paramECParameterSpec.getCurve().getSeed());
  }
  
  public static ECPoint convertPoint(ECParameterSpec paramECParameterSpec, ECPoint paramECPoint, boolean paramBoolean) {
    return convertPoint(convertCurve(paramECParameterSpec.getCurve()), paramECPoint, paramBoolean);
  }
  
  public static ECPoint convertPoint(ECCurve paramECCurve, ECPoint paramECPoint, boolean paramBoolean) {
    return paramECCurve.createPoint(paramECPoint.getAffineX(), paramECPoint.getAffineY());
  }
  
  static {
    Enumeration<String> enumeration = CustomNamedCurves.getNames();
    while (enumeration.hasMoreElements()) {
      String str = enumeration.nextElement();
      X9ECParameters x9ECParameters1 = ECNamedCurveTable.getByName(str);
      if (x9ECParameters1 != null)
        customCurves.put(x9ECParameters1.getCurve(), CustomNamedCurves.getByName(str).getCurve()); 
    } 
    X9ECParameters x9ECParameters = CustomNamedCurves.getByName("Curve25519");
    customCurves.put(new ECCurve.Fp(x9ECParameters.getCurve().getField().getCharacteristic(), x9ECParameters.getCurve().getA().toBigInteger(), x9ECParameters.getCurve().getB().toBigInteger()), x9ECParameters.getCurve());
  }
}
