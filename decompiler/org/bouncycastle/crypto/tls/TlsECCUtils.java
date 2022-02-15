package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Hashtable;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;

public class TlsECCUtils {
  public static final Integer EXT_elliptic_curves = Integers.valueOf(10);
  
  public static final Integer EXT_ec_point_formats = Integers.valueOf(11);
  
  private static final String[] CURVE_NAMES = new String[] { 
      "sect163k1", "sect163r1", "sect163r2", "sect193r1", "sect193r2", "sect233k1", "sect233r1", "sect239k1", "sect283k1", "sect283r1", 
      "sect409k1", "sect409r1", "sect571k1", "sect571r1", "secp160k1", "secp160r1", "secp160r2", "secp192k1", "secp192r1", "secp224k1", 
      "secp224r1", "secp256k1", "secp256r1", "secp384r1", "secp521r1", "brainpoolP256r1", "brainpoolP384r1", "brainpoolP512r1" };
  
  public static void addSupportedEllipticCurvesExtension(Hashtable<Integer, byte[]> paramHashtable, int[] paramArrayOfint) throws IOException {
    paramHashtable.put(EXT_elliptic_curves, createSupportedEllipticCurvesExtension(paramArrayOfint));
  }
  
  public static void addSupportedPointFormatsExtension(Hashtable<Integer, byte[]> paramHashtable, short[] paramArrayOfshort) throws IOException {
    paramHashtable.put(EXT_ec_point_formats, createSupportedPointFormatsExtension(paramArrayOfshort));
  }
  
  public static int[] getSupportedEllipticCurvesExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_elliptic_curves);
    return (arrayOfByte == null) ? null : readSupportedEllipticCurvesExtension(arrayOfByte);
  }
  
  public static short[] getSupportedPointFormatsExtension(Hashtable paramHashtable) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, EXT_ec_point_formats);
    return (arrayOfByte == null) ? null : readSupportedPointFormatsExtension(arrayOfByte);
  }
  
  public static byte[] createSupportedEllipticCurvesExtension(int[] paramArrayOfint) throws IOException {
    if (paramArrayOfint == null || paramArrayOfint.length < 1)
      throw new TlsFatalAlert((short)80); 
    return TlsUtils.encodeUint16ArrayWithUint16Length(paramArrayOfint);
  }
  
  public static byte[] createSupportedPointFormatsExtension(short[] paramArrayOfshort) throws IOException {
    if (paramArrayOfshort == null || !Arrays.contains(paramArrayOfshort, (short)0))
      paramArrayOfshort = Arrays.append(paramArrayOfshort, (short)0); 
    return TlsUtils.encodeUint8ArrayWithUint8Length(paramArrayOfshort);
  }
  
  public static int[] readSupportedEllipticCurvesExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    int i = TlsUtils.readUint16(byteArrayInputStream);
    if (i < 2 || (i & 0x1) != 0)
      throw new TlsFatalAlert((short)50); 
    int[] arrayOfInt = TlsUtils.readUint16Array(i / 2, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    return arrayOfInt;
  }
  
  public static short[] readSupportedPointFormatsExtension(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("'extensionData' cannot be null"); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    short s = TlsUtils.readUint8(byteArrayInputStream);
    if (s < 1)
      throw new TlsFatalAlert((short)50); 
    short[] arrayOfShort = TlsUtils.readUint8Array(s, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    if (!Arrays.contains(arrayOfShort, (short)0))
      throw new TlsFatalAlert((short)47); 
    return arrayOfShort;
  }
  
  public static String getNameOfNamedCurve(int paramInt) {
    return isSupportedNamedCurve(paramInt) ? CURVE_NAMES[paramInt - 1] : null;
  }
  
  public static ECDomainParameters getParametersForNamedCurve(int paramInt) {
    String str = getNameOfNamedCurve(paramInt);
    if (str == null)
      return null; 
    X9ECParameters x9ECParameters = CustomNamedCurves.getByName(str);
    if (x9ECParameters == null) {
      x9ECParameters = ECNamedCurveTable.getByName(str);
      if (x9ECParameters == null)
        return null; 
    } 
    return new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
  }
  
  public static boolean hasAnySupportedNamedCurves() {
    return (CURVE_NAMES.length > 0);
  }
  
  public static boolean containsECCCipherSuites(int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (isECCCipherSuite(paramArrayOfint[b]))
        return true; 
    } 
    return false;
  }
  
  public static boolean isECCCipherSuite(int paramInt) {
    switch (paramInt) {
      case 49153:
      case 49154:
      case 49155:
      case 49156:
      case 49157:
      case 49158:
      case 49159:
      case 49160:
      case 49161:
      case 49162:
      case 49163:
      case 49164:
      case 49165:
      case 49166:
      case 49167:
      case 49168:
      case 49169:
      case 49170:
      case 49171:
      case 49172:
      case 49173:
      case 49174:
      case 49175:
      case 49176:
      case 49177:
      case 49187:
      case 49188:
      case 49189:
      case 49190:
      case 49191:
      case 49192:
      case 49193:
      case 49194:
      case 49195:
      case 49196:
      case 49197:
      case 49198:
      case 49199:
      case 49200:
      case 49201:
      case 49202:
      case 49203:
      case 49204:
      case 49205:
      case 49206:
      case 49207:
      case 49208:
      case 49209:
      case 49210:
      case 49211:
      case 49266:
      case 49267:
      case 49268:
      case 49269:
      case 49270:
      case 49271:
      case 49272:
      case 49273:
      case 49286:
      case 49287:
      case 49288:
      case 49289:
      case 49290:
      case 49291:
      case 49292:
      case 49293:
      case 49306:
      case 49307:
      case 49324:
      case 49325:
      case 49326:
      case 49327:
      case 52392:
      case 52393:
      case 52396:
      case 65282:
      case 65283:
      case 65284:
      case 65285:
      case 65300:
      case 65301:
        return true;
    } 
    return false;
  }
  
  public static boolean areOnSameCurve(ECDomainParameters paramECDomainParameters1, ECDomainParameters paramECDomainParameters2) {
    return (paramECDomainParameters1 != null && paramECDomainParameters1.equals(paramECDomainParameters2));
  }
  
  public static boolean isSupportedNamedCurve(int paramInt) {
    return (paramInt > 0 && paramInt <= CURVE_NAMES.length);
  }
  
  public static boolean isCompressionPreferred(short[] paramArrayOfshort, short paramShort) {
    if (paramArrayOfshort == null)
      return false; 
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      short s = paramArrayOfshort[b];
      if (s == 0)
        return false; 
      if (s == paramShort)
        return true; 
    } 
    return false;
  }
  
  public static byte[] serializeECFieldElement(int paramInt, BigInteger paramBigInteger) throws IOException {
    return BigIntegers.asUnsignedByteArray((paramInt + 7) / 8, paramBigInteger);
  }
  
  public static byte[] serializeECPoint(short[] paramArrayOfshort, ECPoint paramECPoint) throws IOException {
    ECCurve eCCurve = paramECPoint.getCurve();
    boolean bool = false;
    if (ECAlgorithms.isFpCurve(eCCurve)) {
      bool = isCompressionPreferred(paramArrayOfshort, (short)1);
    } else if (ECAlgorithms.isF2mCurve(eCCurve)) {
      bool = isCompressionPreferred(paramArrayOfshort, (short)2);
    } 
    return paramECPoint.getEncoded(bool);
  }
  
  public static byte[] serializeECPublicKey(short[] paramArrayOfshort, ECPublicKeyParameters paramECPublicKeyParameters) throws IOException {
    return serializeECPoint(paramArrayOfshort, paramECPublicKeyParameters.getQ());
  }
  
  public static BigInteger deserializeECFieldElement(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    int i = (paramInt + 7) / 8;
    if (paramArrayOfbyte.length != i)
      throw new TlsFatalAlert((short)50); 
    return new BigInteger(1, paramArrayOfbyte);
  }
  
  public static ECPoint deserializeECPoint(short[] paramArrayOfshort, ECCurve paramECCurve, byte[] paramArrayOfbyte) throws IOException {
    boolean bool;
    if (paramArrayOfbyte == null || paramArrayOfbyte.length < 1)
      throw new TlsFatalAlert((short)47); 
    switch (paramArrayOfbyte[0]) {
      case 2:
      case 3:
        if (ECAlgorithms.isF2mCurve(paramECCurve)) {
          byte b = 2;
          break;
        } 
        if (ECAlgorithms.isFpCurve(paramECCurve)) {
          boolean bool1 = true;
          break;
        } 
        throw new TlsFatalAlert((short)47);
      case 4:
        bool = false;
        break;
      default:
        throw new TlsFatalAlert((short)47);
    } 
    if (bool && (paramArrayOfshort == null || !Arrays.contains(paramArrayOfshort, bool)))
      throw new TlsFatalAlert((short)47); 
    return paramECCurve.decodePoint(paramArrayOfbyte);
  }
  
  public static ECPublicKeyParameters deserializeECPublicKey(short[] paramArrayOfshort, ECDomainParameters paramECDomainParameters, byte[] paramArrayOfbyte) throws IOException {
    try {
      ECPoint eCPoint = deserializeECPoint(paramArrayOfshort, paramECDomainParameters.getCurve(), paramArrayOfbyte);
      return new ECPublicKeyParameters(eCPoint, paramECDomainParameters);
    } catch (RuntimeException runtimeException) {
      throw new TlsFatalAlert((short)47, runtimeException);
    } 
  }
  
  public static byte[] calculateECDHBasicAgreement(ECPublicKeyParameters paramECPublicKeyParameters, ECPrivateKeyParameters paramECPrivateKeyParameters) {
    ECDHBasicAgreement eCDHBasicAgreement = new ECDHBasicAgreement();
    eCDHBasicAgreement.init((CipherParameters)paramECPrivateKeyParameters);
    BigInteger bigInteger = eCDHBasicAgreement.calculateAgreement((CipherParameters)paramECPublicKeyParameters);
    return BigIntegers.asUnsignedByteArray(eCDHBasicAgreement.getFieldSize(), bigInteger);
  }
  
  public static AsymmetricCipherKeyPair generateECKeyPair(SecureRandom paramSecureRandom, ECDomainParameters paramECDomainParameters) {
    ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
    eCKeyPairGenerator.init((KeyGenerationParameters)new ECKeyGenerationParameters(paramECDomainParameters, paramSecureRandom));
    return eCKeyPairGenerator.generateKeyPair();
  }
  
  public static ECPrivateKeyParameters generateEphemeralClientKeyExchange(SecureRandom paramSecureRandom, short[] paramArrayOfshort, ECDomainParameters paramECDomainParameters, OutputStream paramOutputStream) throws IOException {
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = generateECKeyPair(paramSecureRandom, paramECDomainParameters);
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    writeECPoint(paramArrayOfshort, eCPublicKeyParameters.getQ(), paramOutputStream);
    return (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
  }
  
  static ECPrivateKeyParameters generateEphemeralServerKeyExchange(SecureRandom paramSecureRandom, int[] paramArrayOfint, short[] paramArrayOfshort, OutputStream paramOutputStream) throws IOException {
    int i = -1;
    if (paramArrayOfint == null) {
      i = 23;
    } else {
      for (byte b = 0; b < paramArrayOfint.length; b++) {
        int j = paramArrayOfint[b];
        if (NamedCurve.isValid(j) && isSupportedNamedCurve(j)) {
          i = j;
          break;
        } 
      } 
    } 
    ECDomainParameters eCDomainParameters = null;
    if (i >= 0) {
      eCDomainParameters = getParametersForNamedCurve(i);
    } else if (Arrays.contains(paramArrayOfint, 65281)) {
      eCDomainParameters = getParametersForNamedCurve(23);
    } else if (Arrays.contains(paramArrayOfint, 65282)) {
      eCDomainParameters = getParametersForNamedCurve(10);
    } 
    if (eCDomainParameters == null)
      throw new TlsFatalAlert((short)80); 
    if (i < 0) {
      writeExplicitECParameters(paramArrayOfshort, eCDomainParameters, paramOutputStream);
    } else {
      writeNamedECParameters(i, paramOutputStream);
    } 
    return generateEphemeralClientKeyExchange(paramSecureRandom, paramArrayOfshort, eCDomainParameters, paramOutputStream);
  }
  
  public static ECPublicKeyParameters validateECPublicKey(ECPublicKeyParameters paramECPublicKeyParameters) throws IOException {
    return paramECPublicKeyParameters;
  }
  
  public static int readECExponent(int paramInt, InputStream paramInputStream) throws IOException {
    BigInteger bigInteger = readECParameter(paramInputStream);
    if (bigInteger.bitLength() < 32) {
      int i = bigInteger.intValue();
      if (i > 0 && i < paramInt)
        return i; 
    } 
    throw new TlsFatalAlert((short)47);
  }
  
  public static BigInteger readECFieldElement(int paramInt, InputStream paramInputStream) throws IOException {
    return deserializeECFieldElement(paramInt, TlsUtils.readOpaque8(paramInputStream));
  }
  
  public static BigInteger readECParameter(InputStream paramInputStream) throws IOException {
    return new BigInteger(1, TlsUtils.readOpaque8(paramInputStream));
  }
  
  public static ECDomainParameters readECParameters(int[] paramArrayOfint, short[] paramArrayOfshort, InputStream paramInputStream) throws IOException {
    try {
      BigInteger bigInteger1;
      int i;
      BigInteger bigInteger2;
      short s2;
      BigInteger bigInteger3;
      int j;
      byte[] arrayOfByte1;
      int k;
      BigInteger bigInteger4;
      int m;
      BigInteger bigInteger5;
      ECCurve.Fp fp;
      BigInteger bigInteger6;
      ECPoint eCPoint1;
      byte[] arrayOfByte2;
      BigInteger bigInteger7;
      BigInteger bigInteger8;
      ECCurve.F2m f2m;
      ECPoint eCPoint2;
      short s1 = TlsUtils.readUint8(paramInputStream);
      switch (s1) {
        case 1:
          checkNamedCurve(paramArrayOfint, 65281);
          bigInteger1 = readECParameter(paramInputStream);
          bigInteger2 = readECFieldElement(bigInteger1.bitLength(), paramInputStream);
          bigInteger3 = readECFieldElement(bigInteger1.bitLength(), paramInputStream);
          arrayOfByte1 = TlsUtils.readOpaque8(paramInputStream);
          bigInteger4 = readECParameter(paramInputStream);
          bigInteger5 = readECParameter(paramInputStream);
          fp = new ECCurve.Fp(bigInteger1, bigInteger2, bigInteger3, bigInteger4, bigInteger5);
          eCPoint1 = deserializeECPoint(paramArrayOfshort, (ECCurve)fp, arrayOfByte1);
          return new ECDomainParameters((ECCurve)fp, eCPoint1, bigInteger4, bigInteger5);
        case 2:
          checkNamedCurve(paramArrayOfint, 65282);
          i = TlsUtils.readUint16(paramInputStream);
          s2 = TlsUtils.readUint8(paramInputStream);
          if (!ECBasisType.isValid(s2))
            throw new TlsFatalAlert((short)47); 
          j = readECExponent(i, paramInputStream);
          k = -1;
          m = -1;
          if (s2 == 2) {
            k = readECExponent(i, paramInputStream);
            m = readECExponent(i, paramInputStream);
          } 
          bigInteger5 = readECFieldElement(i, paramInputStream);
          bigInteger6 = readECFieldElement(i, paramInputStream);
          arrayOfByte2 = TlsUtils.readOpaque8(paramInputStream);
          bigInteger7 = readECParameter(paramInputStream);
          bigInteger8 = readECParameter(paramInputStream);
          f2m = (s2 == 2) ? new ECCurve.F2m(i, j, k, m, bigInteger5, bigInteger6, bigInteger7, bigInteger8) : new ECCurve.F2m(i, j, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
          eCPoint2 = deserializeECPoint(paramArrayOfshort, (ECCurve)f2m, arrayOfByte2);
          return new ECDomainParameters((ECCurve)f2m, eCPoint2, bigInteger7, bigInteger8);
        case 3:
          i = TlsUtils.readUint16(paramInputStream);
          if (!NamedCurve.refersToASpecificNamedCurve(i))
            throw new TlsFatalAlert((short)47); 
          checkNamedCurve(paramArrayOfint, i);
          return getParametersForNamedCurve(i);
      } 
      throw new TlsFatalAlert((short)47);
    } catch (RuntimeException runtimeException) {
      throw new TlsFatalAlert((short)47, runtimeException);
    } 
  }
  
  private static void checkNamedCurve(int[] paramArrayOfint, int paramInt) throws IOException {
    if (paramArrayOfint != null && !Arrays.contains(paramArrayOfint, paramInt))
      throw new TlsFatalAlert((short)47); 
  }
  
  public static void writeECExponent(int paramInt, OutputStream paramOutputStream) throws IOException {
    BigInteger bigInteger = BigInteger.valueOf(paramInt);
    writeECParameter(bigInteger, paramOutputStream);
  }
  
  public static void writeECFieldElement(ECFieldElement paramECFieldElement, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeOpaque8(paramECFieldElement.getEncoded(), paramOutputStream);
  }
  
  public static void writeECFieldElement(int paramInt, BigInteger paramBigInteger, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeOpaque8(serializeECFieldElement(paramInt, paramBigInteger), paramOutputStream);
  }
  
  public static void writeECParameter(BigInteger paramBigInteger, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeOpaque8(BigIntegers.asUnsignedByteArray(paramBigInteger), paramOutputStream);
  }
  
  public static void writeExplicitECParameters(short[] paramArrayOfshort, ECDomainParameters paramECDomainParameters, OutputStream paramOutputStream) throws IOException {
    ECCurve eCCurve = paramECDomainParameters.getCurve();
    if (ECAlgorithms.isFpCurve(eCCurve)) {
      TlsUtils.writeUint8((short)1, paramOutputStream);
      writeECParameter(eCCurve.getField().getCharacteristic(), paramOutputStream);
    } else if (ECAlgorithms.isF2mCurve(eCCurve)) {
      PolynomialExtensionField polynomialExtensionField = (PolynomialExtensionField)eCCurve.getField();
      int[] arrayOfInt = polynomialExtensionField.getMinimalPolynomial().getExponentsPresent();
      TlsUtils.writeUint8((short)2, paramOutputStream);
      int i = arrayOfInt[arrayOfInt.length - 1];
      TlsUtils.checkUint16(i);
      TlsUtils.writeUint16(i, paramOutputStream);
      if (arrayOfInt.length == 3) {
        TlsUtils.writeUint8((short)1, paramOutputStream);
        writeECExponent(arrayOfInt[1], paramOutputStream);
      } else if (arrayOfInt.length == 5) {
        TlsUtils.writeUint8((short)2, paramOutputStream);
        writeECExponent(arrayOfInt[1], paramOutputStream);
        writeECExponent(arrayOfInt[2], paramOutputStream);
        writeECExponent(arrayOfInt[3], paramOutputStream);
      } else {
        throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
      } 
    } else {
      throw new IllegalArgumentException("'ecParameters' not a known curve type");
    } 
    writeECFieldElement(eCCurve.getA(), paramOutputStream);
    writeECFieldElement(eCCurve.getB(), paramOutputStream);
    TlsUtils.writeOpaque8(serializeECPoint(paramArrayOfshort, paramECDomainParameters.getG()), paramOutputStream);
    writeECParameter(paramECDomainParameters.getN(), paramOutputStream);
    writeECParameter(paramECDomainParameters.getH(), paramOutputStream);
  }
  
  public static void writeECPoint(short[] paramArrayOfshort, ECPoint paramECPoint, OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeOpaque8(serializeECPoint(paramArrayOfshort, paramECPoint), paramOutputStream);
  }
  
  public static void writeNamedECParameters(int paramInt, OutputStream paramOutputStream) throws IOException {
    if (!NamedCurve.refersToASpecificNamedCurve(paramInt))
      throw new TlsFatalAlert((short)80); 
    TlsUtils.writeUint8((short)3, paramOutputStream);
    TlsUtils.checkUint16(paramInt);
    TlsUtils.writeUint16(paramInt, paramOutputStream);
  }
}
