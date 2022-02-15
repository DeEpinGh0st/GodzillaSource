package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class BCECGOST3410_2012PublicKey implements ECPublicKey, ECPublicKey, ECPointEncoder {
  static final long serialVersionUID = 7026240464295649314L;
  
  private String algorithm = "ECGOST3410-2012";
  
  private boolean withCompression;
  
  private transient ECPublicKeyParameters ecPublicKey;
  
  private transient ECParameterSpec ecSpec;
  
  private transient GOST3410PublicKeyAlgParameters gostParams;
  
  public BCECGOST3410_2012PublicKey(BCECGOST3410_2012PublicKey paramBCECGOST3410_2012PublicKey) {
    this.ecPublicKey = paramBCECGOST3410_2012PublicKey.ecPublicKey;
    this.ecSpec = paramBCECGOST3410_2012PublicKey.ecSpec;
    this.withCompression = paramBCECGOST3410_2012PublicKey.withCompression;
    this.gostParams = paramBCECGOST3410_2012PublicKey.gostParams;
  }
  
  public BCECGOST3410_2012PublicKey(ECPublicKeySpec paramECPublicKeySpec) {
    this.ecSpec = paramECPublicKeySpec.getParams();
    this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, paramECPublicKeySpec.getW(), false), EC5Util.getDomainParameters(null, paramECPublicKeySpec.getParams()));
  }
  
  public BCECGOST3410_2012PublicKey(ECPublicKeySpec paramECPublicKeySpec, ProviderConfiguration paramProviderConfiguration) {
    if (paramECPublicKeySpec.getParams() != null) {
      ECCurve eCCurve = paramECPublicKeySpec.getParams().getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, paramECPublicKeySpec.getParams().getSeed());
      this.ecPublicKey = new ECPublicKeyParameters(paramECPublicKeySpec.getQ(), ECUtil.getDomainParameters(paramProviderConfiguration, paramECPublicKeySpec.getParams()));
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECPublicKeySpec.getParams());
    } else {
      ECParameterSpec eCParameterSpec = paramProviderConfiguration.getEcImplicitlyCa();
      this.ecPublicKey = new ECPublicKeyParameters(eCParameterSpec.getCurve().createPoint(paramECPublicKeySpec.getQ().getAffineXCoord().toBigInteger(), paramECPublicKeySpec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(paramProviderConfiguration, (ECParameterSpec)null));
      this.ecSpec = null;
    } 
  }
  
  public BCECGOST3410_2012PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPublicKeyParameters.getParameters();
    this.algorithm = paramString;
    this.ecPublicKey = paramECPublicKeyParameters;
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = createSpec(ellipticCurve, eCDomainParameters);
    } else {
      this.ecSpec = paramECParameterSpec;
    } 
  }
  
  public BCECGOST3410_2012PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPublicKeyParameters.getParameters();
    this.algorithm = paramString;
    this.ecPublicKey = paramECPublicKeyParameters;
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = createSpec(ellipticCurve, eCDomainParameters);
    } else {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(paramECParameterSpec.getCurve(), paramECParameterSpec.getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECParameterSpec);
    } 
  }
  
  public BCECGOST3410_2012PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters) {
    this.algorithm = paramString;
    this.ecPublicKey = paramECPublicKeyParameters;
    this.ecSpec = null;
  }
  
  private ECParameterSpec createSpec(EllipticCurve paramEllipticCurve, ECDomainParameters paramECDomainParameters) {
    return new ECParameterSpec(paramEllipticCurve, new ECPoint(paramECDomainParameters.getG().getAffineXCoord().toBigInteger(), paramECDomainParameters.getG().getAffineYCoord().toBigInteger()), paramECDomainParameters.getN(), paramECDomainParameters.getH().intValue());
  }
  
  public BCECGOST3410_2012PublicKey(ECPublicKey paramECPublicKey) {
    this.algorithm = paramECPublicKey.getAlgorithm();
    this.ecSpec = paramECPublicKey.getParams();
    this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, paramECPublicKey.getW(), false), EC5Util.getDomainParameters(null, paramECPublicKey.getParams()));
  }
  
  BCECGOST3410_2012PublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    populateFromPubKeyInfo(paramSubjectPublicKeyInfo);
  }
  
  private void populateFromPubKeyInfo(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    ASN1OctetString aSN1OctetString;
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    DERBitString dERBitString = paramSubjectPublicKeyInfo.getPublicKeyData();
    this.algorithm = "ECGOST3410-2012";
    try {
      aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(dERBitString.getBytes());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error recovering public key");
    } 
    byte[] arrayOfByte1 = aSN1OctetString.getOctets();
    char c = '@';
    if (aSN1ObjectIdentifier.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512))
      c = ''; 
    int i = c / 2;
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    byte b;
    for (b = 0; b != arrayOfByte2.length; b++)
      arrayOfByte2[b] = arrayOfByte1[i - 1 - b]; 
    for (b = 0; b != arrayOfByte3.length; b++)
      arrayOfByte3[b] = arrayOfByte1[c - 1 - b]; 
    this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(paramSubjectPublicKeyInfo.getAlgorithm().getParameters());
    ECNamedCurveParameterSpec eCNamedCurveParameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
    ECCurve eCCurve = eCNamedCurveParameterSpec.getCurve();
    EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCNamedCurveParameterSpec.getSeed());
    this.ecPublicKey = new ECPublicKeyParameters(eCCurve.createPoint(new BigInteger(1, arrayOfByte2), new BigInteger(1, arrayOfByte3)), ECUtil.getDomainParameters(null, (ECParameterSpec)eCNamedCurveParameterSpec));
    this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), ellipticCurve, new ECPoint(eCNamedCurveParameterSpec.getG().getAffineXCoord().toBigInteger(), eCNamedCurveParameterSpec.getG().getAffineYCoord().toBigInteger()), eCNamedCurveParameterSpec.getN(), eCNamedCurveParameterSpec.getH());
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    X962Parameters x962Parameters;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    byte b1;
    byte b2;
    ASN1ObjectIdentifier aSN1ObjectIdentifier;
    BigInteger bigInteger1 = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
    BigInteger bigInteger2 = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
    boolean bool = (bigInteger1.bitLength() > 256) ? true : false;
    if (this.gostParams != null) {
      GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = this.gostParams;
    } else if (this.ecSpec instanceof ECNamedCurveSpec) {
      if (bool) {
        GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
      } else {
        GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
      } 
    } else {
      ECCurve eCCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
      X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
      x962Parameters = new X962Parameters(x9ECParameters);
    } 
    if (bool) {
      b1 = 128;
      b2 = 64;
      aSN1ObjectIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
    } else {
      b1 = 64;
      b2 = 32;
      aSN1ObjectIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
    } 
    byte[] arrayOfByte = new byte[b1];
    extractBytes(arrayOfByte, b1 / 2, 0, bigInteger1);
    extractBytes(arrayOfByte, b1 / 2, b2, bigInteger2);
    try {
      subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(aSN1ObjectIdentifier, (ASN1Encodable)x962Parameters), (ASN1Encodable)new DEROctetString(arrayOfByte));
    } catch (IOException iOException) {
      return null;
    } 
    return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
  }
  
  private void extractBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte.length < paramInt1) {
      byte[] arrayOfByte1 = new byte[paramInt1];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte.length, arrayOfByte.length);
      arrayOfByte = arrayOfByte1;
    } 
    for (int i = 0; i != paramInt1; i++)
      paramArrayOfbyte[paramInt2 + i] = arrayOfByte[arrayOfByte.length - 1 - i]; 
  }
  
  public ECParameterSpec getParams() {
    return this.ecSpec;
  }
  
  public ECParameterSpec getParameters() {
    return (this.ecSpec == null) ? null : EC5Util.convertSpec(this.ecSpec, this.withCompression);
  }
  
  public ECPoint getW() {
    return new ECPoint(this.ecPublicKey.getQ().getAffineXCoord().toBigInteger(), this.ecPublicKey.getQ().getAffineYCoord().toBigInteger());
  }
  
  public ECPoint getQ() {
    return (this.ecSpec == null) ? this.ecPublicKey.getQ().getDetachedPoint() : this.ecPublicKey.getQ();
  }
  
  ECPublicKeyParameters engineGetKeyParameters() {
    return this.ecPublicKey;
  }
  
  ECParameterSpec engineGetSpec() {
    return (this.ecSpec != null) ? EC5Util.convertSpec(this.ecSpec, this.withCompression) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
  }
  
  public String toString() {
    return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), engineGetSpec());
  }
  
  public void setPointFormat(String paramString) {
    this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(paramString);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BCECGOST3410_2012PublicKey))
      return false; 
    BCECGOST3410_2012PublicKey bCECGOST3410_2012PublicKey = (BCECGOST3410_2012PublicKey)paramObject;
    return (this.ecPublicKey.getQ().equals(bCECGOST3410_2012PublicKey.ecPublicKey.getQ()) && engineGetSpec().equals(bCECGOST3410_2012PublicKey.engineGetSpec()));
  }
  
  public int hashCode() {
    return this.ecPublicKey.getQ().hashCode() ^ engineGetSpec().hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    byte[] arrayOfByte = (byte[])paramObjectInputStream.readObject();
    populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getEncoded());
  }
  
  public GOST3410PublicKeyAlgParameters getGostParams() {
    return this.gostParams;
  }
}
