package org.bouncycastle.jcajce.provider.asymmetric.dstu;

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
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
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
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class BCDSTU4145PublicKey implements ECPublicKey, ECPublicKey, ECPointEncoder {
  static final long serialVersionUID = 7026240464295649314L;
  
  private String algorithm = "DSTU4145";
  
  private boolean withCompression;
  
  private transient ECPublicKeyParameters ecPublicKey;
  
  private transient ECParameterSpec ecSpec;
  
  private transient DSTU4145Params dstuParams;
  
  public BCDSTU4145PublicKey(BCDSTU4145PublicKey paramBCDSTU4145PublicKey) {
    this.ecPublicKey = paramBCDSTU4145PublicKey.ecPublicKey;
    this.ecSpec = paramBCDSTU4145PublicKey.ecSpec;
    this.withCompression = paramBCDSTU4145PublicKey.withCompression;
    this.dstuParams = paramBCDSTU4145PublicKey.dstuParams;
  }
  
  public BCDSTU4145PublicKey(ECPublicKeySpec paramECPublicKeySpec) {
    this.ecSpec = paramECPublicKeySpec.getParams();
    this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, paramECPublicKeySpec.getW(), false), EC5Util.getDomainParameters(null, this.ecSpec));
  }
  
  public BCDSTU4145PublicKey(ECPublicKeySpec paramECPublicKeySpec, ProviderConfiguration paramProviderConfiguration) {
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
  
  public BCDSTU4145PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
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
  
  public BCDSTU4145PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPublicKeyParameters.getParameters();
    this.algorithm = paramString;
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = createSpec(ellipticCurve, eCDomainParameters);
    } else {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(paramECParameterSpec.getCurve(), paramECParameterSpec.getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECParameterSpec);
    } 
    this.ecPublicKey = paramECPublicKeyParameters;
  }
  
  public BCDSTU4145PublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters) {
    this.algorithm = paramString;
    this.ecPublicKey = paramECPublicKeyParameters;
    this.ecSpec = null;
  }
  
  private ECParameterSpec createSpec(EllipticCurve paramEllipticCurve, ECDomainParameters paramECDomainParameters) {
    return new ECParameterSpec(paramEllipticCurve, new ECPoint(paramECDomainParameters.getG().getAffineXCoord().toBigInteger(), paramECDomainParameters.getG().getAffineYCoord().toBigInteger()), paramECDomainParameters.getN(), paramECDomainParameters.getH().intValue());
  }
  
  BCDSTU4145PublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    populateFromPubKeyInfo(paramSubjectPublicKeyInfo);
  }
  
  private void reverseBytes(byte[] paramArrayOfbyte) {
    for (byte b = 0; b < paramArrayOfbyte.length / 2; b++) {
      byte b1 = paramArrayOfbyte[b];
      paramArrayOfbyte[b] = paramArrayOfbyte[paramArrayOfbyte.length - 1 - b];
      paramArrayOfbyte[paramArrayOfbyte.length - 1 - b] = b1;
    } 
  }
  
  private void populateFromPubKeyInfo(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    ASN1OctetString aSN1OctetString;
    ECParameterSpec eCParameterSpec;
    DERBitString dERBitString = paramSubjectPublicKeyInfo.getPublicKeyData();
    this.algorithm = "DSTU4145";
    try {
      aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(dERBitString.getBytes());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error recovering public key");
    } 
    byte[] arrayOfByte = aSN1OctetString.getOctets();
    if (paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le))
      reverseBytes(arrayOfByte); 
    this.dstuParams = DSTU4145Params.getInstance(paramSubjectPublicKeyInfo.getAlgorithm().getParameters());
    ECNamedCurveParameterSpec eCNamedCurveParameterSpec = null;
    if (this.dstuParams.isNamedCurve()) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = this.dstuParams.getNamedCurve();
      ECDomainParameters eCDomainParameters = DSTU4145NamedCurves.getByOID(aSN1ObjectIdentifier);
      eCNamedCurveParameterSpec = new ECNamedCurveParameterSpec(aSN1ObjectIdentifier.getId(), eCDomainParameters.getCurve(), eCDomainParameters.getG(), eCDomainParameters.getN(), eCDomainParameters.getH(), eCDomainParameters.getSeed());
    } else {
      DSTU4145ECBinary dSTU4145ECBinary = this.dstuParams.getECBinary();
      byte[] arrayOfByte1 = dSTU4145ECBinary.getB();
      if (paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le))
        reverseBytes(arrayOfByte1); 
      DSTU4145BinaryField dSTU4145BinaryField = dSTU4145ECBinary.getField();
      ECCurve.F2m f2m = new ECCurve.F2m(dSTU4145BinaryField.getM(), dSTU4145BinaryField.getK1(), dSTU4145BinaryField.getK2(), dSTU4145BinaryField.getK3(), dSTU4145ECBinary.getA(), new BigInteger(1, arrayOfByte1));
      byte[] arrayOfByte2 = dSTU4145ECBinary.getG();
      if (paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le))
        reverseBytes(arrayOfByte2); 
      eCParameterSpec = new ECParameterSpec((ECCurve)f2m, DSTU4145PointEncoder.decodePoint((ECCurve)f2m, arrayOfByte2), dSTU4145ECBinary.getN());
    } 
    ECCurve eCCurve = eCParameterSpec.getCurve();
    EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCParameterSpec.getSeed());
    if (this.dstuParams.isNamedCurve()) {
      this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(this.dstuParams.getNamedCurve().getId(), ellipticCurve, new ECPoint(eCParameterSpec.getG().getAffineXCoord().toBigInteger(), eCParameterSpec.getG().getAffineYCoord().toBigInteger()), eCParameterSpec.getN(), eCParameterSpec.getH());
    } else {
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(eCParameterSpec.getG().getAffineXCoord().toBigInteger(), eCParameterSpec.getG().getAffineYCoord().toBigInteger()), eCParameterSpec.getN(), eCParameterSpec.getH().intValue());
    } 
    this.ecPublicKey = new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(eCCurve, arrayOfByte), EC5Util.getDomainParameters(null, this.ecSpec));
  }
  
  public byte[] getSbox() {
    return (null != this.dstuParams) ? this.dstuParams.getDKE() : DSTU4145Params.getDefaultDKE();
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
    if (this.dstuParams != null) {
      DSTU4145Params dSTU4145Params = this.dstuParams;
    } else if (this.ecSpec instanceof ECNamedCurveSpec) {
      DSTU4145Params dSTU4145Params = new DSTU4145Params(new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()));
    } else {
      ECCurve eCCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
      X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
      x962Parameters = new X962Parameters(x9ECParameters);
    } 
    byte[] arrayOfByte = DSTU4145PointEncoder.encodePoint(this.ecPublicKey.getQ());
    try {
      subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, (ASN1Encodable)x962Parameters), (ASN1Encodable)new DEROctetString(arrayOfByte));
    } catch (IOException iOException) {
      return null;
    } 
    return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
  }
  
  public ECParameterSpec getParams() {
    return this.ecSpec;
  }
  
  public ECParameterSpec getParameters() {
    return (this.ecSpec == null) ? null : EC5Util.convertSpec(this.ecSpec, this.withCompression);
  }
  
  public ECPoint getW() {
    ECPoint eCPoint = this.ecPublicKey.getQ();
    return new ECPoint(eCPoint.getAffineXCoord().toBigInteger(), eCPoint.getAffineYCoord().toBigInteger());
  }
  
  public ECPoint getQ() {
    ECPoint eCPoint = this.ecPublicKey.getQ();
    return (this.ecSpec == null) ? eCPoint.getDetachedPoint() : eCPoint;
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
    if (!(paramObject instanceof BCDSTU4145PublicKey))
      return false; 
    BCDSTU4145PublicKey bCDSTU4145PublicKey = (BCDSTU4145PublicKey)paramObject;
    return (this.ecPublicKey.getQ().equals(bCDSTU4145PublicKey.ecPublicKey.getQ()) && engineGetSpec().equals(bCDSTU4145PublicKey.engineGetSpec()));
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
}
