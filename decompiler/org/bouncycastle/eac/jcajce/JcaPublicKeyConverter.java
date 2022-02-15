package org.bouncycastle.eac.jcajce;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.eac.RSAPublicKey;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class JcaPublicKeyConverter {
  private EACHelper helper = new DefaultEACHelper();
  
  public JcaPublicKeyConverter setProvider(String paramString) {
    this.helper = new NamedEACHelper(paramString);
    return this;
  }
  
  public JcaPublicKeyConverter setProvider(Provider paramProvider) {
    this.helper = new ProviderEACHelper(paramProvider);
    return this;
  }
  
  public PublicKey getKey(PublicKeyDataObject paramPublicKeyDataObject) throws EACException, InvalidKeySpecException {
    if (paramPublicKeyDataObject.getUsage().on(EACObjectIdentifiers.id_TA_ECDSA))
      return getECPublicKeyPublicKey((ECDSAPublicKey)paramPublicKeyDataObject); 
    RSAPublicKey rSAPublicKey = (RSAPublicKey)paramPublicKeyDataObject;
    RSAPublicKeySpec rSAPublicKeySpec = new RSAPublicKeySpec(rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
    try {
      KeyFactory keyFactory = this.helper.createKeyFactory("RSA");
      return keyFactory.generatePublic(rSAPublicKeySpec);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new EACException("cannot find provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new EACException("cannot find algorithm ECDSA: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    } 
  }
  
  private PublicKey getECPublicKeyPublicKey(ECDSAPublicKey paramECDSAPublicKey) throws EACException, InvalidKeySpecException {
    KeyFactory keyFactory;
    ECParameterSpec eCParameterSpec = getParams(paramECDSAPublicKey);
    ECPoint eCPoint = getPublicPoint(paramECDSAPublicKey);
    ECPublicKeySpec eCPublicKeySpec = new ECPublicKeySpec(eCPoint, eCParameterSpec);
    try {
      keyFactory = this.helper.createKeyFactory("ECDSA");
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new EACException("cannot find provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new EACException("cannot find algorithm ECDSA: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    } 
    return keyFactory.generatePublic(eCPublicKeySpec);
  }
  
  private ECPoint getPublicPoint(ECDSAPublicKey paramECDSAPublicKey) {
    if (!paramECDSAPublicKey.hasParameters())
      throw new IllegalArgumentException("Public key does not contains EC Params"); 
    BigInteger bigInteger = paramECDSAPublicKey.getPrimeModulusP();
    ECCurve.Fp fp = new ECCurve.Fp(bigInteger, paramECDSAPublicKey.getFirstCoefA(), paramECDSAPublicKey.getSecondCoefB(), paramECDSAPublicKey.getOrderOfBasePointR(), paramECDSAPublicKey.getCofactorF());
    ECPoint.Fp fp1 = (ECPoint.Fp)fp.decodePoint(paramECDSAPublicKey.getPublicPointY());
    return new ECPoint(fp1.getAffineXCoord().toBigInteger(), fp1.getAffineYCoord().toBigInteger());
  }
  
  private ECParameterSpec getParams(ECDSAPublicKey paramECDSAPublicKey) {
    if (!paramECDSAPublicKey.hasParameters())
      throw new IllegalArgumentException("Public key does not contains EC Params"); 
    BigInteger bigInteger1 = paramECDSAPublicKey.getPrimeModulusP();
    ECCurve.Fp fp = new ECCurve.Fp(bigInteger1, paramECDSAPublicKey.getFirstCoefA(), paramECDSAPublicKey.getSecondCoefB(), paramECDSAPublicKey.getOrderOfBasePointR(), paramECDSAPublicKey.getCofactorF());
    ECPoint eCPoint = fp.decodePoint(paramECDSAPublicKey.getBasePointG());
    BigInteger bigInteger2 = paramECDSAPublicKey.getOrderOfBasePointR();
    BigInteger bigInteger3 = paramECDSAPublicKey.getCofactorF();
    EllipticCurve ellipticCurve = convertCurve((ECCurve)fp);
    return new ECParameterSpec(ellipticCurve, new ECPoint(eCPoint.getAffineXCoord().toBigInteger(), eCPoint.getAffineYCoord().toBigInteger()), bigInteger2, bigInteger3.intValue());
  }
  
  public PublicKeyDataObject getPublicKeyDataObject(ASN1ObjectIdentifier paramASN1ObjectIdentifier, PublicKey paramPublicKey) {
    if (paramPublicKey instanceof RSAPublicKey) {
      RSAPublicKey rSAPublicKey = (RSAPublicKey)paramPublicKey;
      return (PublicKeyDataObject)new RSAPublicKey(paramASN1ObjectIdentifier, rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
    } 
    ECPublicKey eCPublicKey = (ECPublicKey)paramPublicKey;
    ECParameterSpec eCParameterSpec = eCPublicKey.getParams();
    return (PublicKeyDataObject)new ECDSAPublicKey(paramASN1ObjectIdentifier, ((ECFieldFp)eCParameterSpec.getCurve().getField()).getP(), eCParameterSpec.getCurve().getA(), eCParameterSpec.getCurve().getB(), convertPoint(convertCurve(eCParameterSpec.getCurve(), eCParameterSpec.getOrder(), eCParameterSpec.getCofactor()), eCParameterSpec.getGenerator()).getEncoded(), eCParameterSpec.getOrder(), convertPoint(convertCurve(eCParameterSpec.getCurve(), eCParameterSpec.getOrder(), eCParameterSpec.getCofactor()), eCPublicKey.getW()).getEncoded(), eCParameterSpec.getCofactor());
  }
  
  private static ECPoint convertPoint(ECCurve paramECCurve, ECPoint paramECPoint) {
    return paramECCurve.createPoint(paramECPoint.getAffineX(), paramECPoint.getAffineY());
  }
  
  private static ECCurve convertCurve(EllipticCurve paramEllipticCurve, BigInteger paramBigInteger, int paramInt) {
    ECField eCField = paramEllipticCurve.getField();
    BigInteger bigInteger1 = paramEllipticCurve.getA();
    BigInteger bigInteger2 = paramEllipticCurve.getB();
    if (eCField instanceof ECFieldFp)
      return (ECCurve)new ECCurve.Fp(((ECFieldFp)eCField).getP(), bigInteger1, bigInteger2, paramBigInteger, BigInteger.valueOf(paramInt)); 
    throw new IllegalStateException("not implemented yet!!!");
  }
  
  private static EllipticCurve convertCurve(ECCurve paramECCurve) {
    ECField eCField = convertField(paramECCurve.getField());
    BigInteger bigInteger1 = paramECCurve.getA().toBigInteger();
    BigInteger bigInteger2 = paramECCurve.getB().toBigInteger();
    return new EllipticCurve(eCField, bigInteger1, bigInteger2, null);
  }
  
  private static ECField convertField(FiniteField paramFiniteField) {
    if (ECAlgorithms.isFpField(paramFiniteField))
      return new ECFieldFp(paramFiniteField.getCharacteristic()); 
    Polynomial polynomial = ((PolynomialExtensionField)paramFiniteField).getMinimalPolynomial();
    int[] arrayOfInt1 = polynomial.getExponentsPresent();
    int[] arrayOfInt2 = Arrays.reverse(Arrays.copyOfRange(arrayOfInt1, 1, arrayOfInt1.length - 1));
    return new ECFieldF2m(polynomial.getDegree(), arrayOfInt2);
  }
}
