package org.bouncycastle.jce.provider;

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
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;

public class JCEECPublicKey implements ECPublicKey, ECPublicKey, ECPointEncoder {
  private String algorithm = "EC";
  
  private ECPoint q;
  
  private ECParameterSpec ecSpec;
  
  private boolean withCompression;
  
  private GOST3410PublicKeyAlgParameters gostParams;
  
  public JCEECPublicKey(String paramString, JCEECPublicKey paramJCEECPublicKey) {
    this.algorithm = paramString;
    this.q = paramJCEECPublicKey.q;
    this.ecSpec = paramJCEECPublicKey.ecSpec;
    this.withCompression = paramJCEECPublicKey.withCompression;
    this.gostParams = paramJCEECPublicKey.gostParams;
  }
  
  public JCEECPublicKey(String paramString, ECPublicKeySpec paramECPublicKeySpec) {
    this.algorithm = paramString;
    this.ecSpec = paramECPublicKeySpec.getParams();
    this.q = EC5Util.convertPoint(this.ecSpec, paramECPublicKeySpec.getW(), false);
  }
  
  public JCEECPublicKey(String paramString, ECPublicKeySpec paramECPublicKeySpec) {
    this.algorithm = paramString;
    this.q = paramECPublicKeySpec.getQ();
    if (paramECPublicKeySpec.getParams() != null) {
      ECCurve eCCurve = paramECPublicKeySpec.getParams().getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, paramECPublicKeySpec.getParams().getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECPublicKeySpec.getParams());
    } else {
      if (this.q.getCurve() == null) {
        ECParameterSpec eCParameterSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
        this.q = eCParameterSpec.getCurve().createPoint(this.q.getAffineXCoord().toBigInteger(), this.q.getAffineYCoord().toBigInteger(), false);
      } 
      this.ecSpec = null;
    } 
  }
  
  public JCEECPublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPublicKeyParameters.getParameters();
    this.algorithm = paramString;
    this.q = paramECPublicKeyParameters.getQ();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = createSpec(ellipticCurve, eCDomainParameters);
    } else {
      this.ecSpec = paramECParameterSpec;
    } 
  }
  
  public JCEECPublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPublicKeyParameters.getParameters();
    this.algorithm = paramString;
    this.q = paramECPublicKeyParameters.getQ();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = createSpec(ellipticCurve, eCDomainParameters);
    } else {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(paramECParameterSpec.getCurve(), paramECParameterSpec.getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECParameterSpec);
    } 
  }
  
  public JCEECPublicKey(String paramString, ECPublicKeyParameters paramECPublicKeyParameters) {
    this.algorithm = paramString;
    this.q = paramECPublicKeyParameters.getQ();
    this.ecSpec = null;
  }
  
  private ECParameterSpec createSpec(EllipticCurve paramEllipticCurve, ECDomainParameters paramECDomainParameters) {
    return new ECParameterSpec(paramEllipticCurve, new ECPoint(paramECDomainParameters.getG().getAffineXCoord().toBigInteger(), paramECDomainParameters.getG().getAffineYCoord().toBigInteger()), paramECDomainParameters.getN(), paramECDomainParameters.getH().intValue());
  }
  
  public JCEECPublicKey(ECPublicKey paramECPublicKey) {
    this.algorithm = paramECPublicKey.getAlgorithm();
    this.ecSpec = paramECPublicKey.getParams();
    this.q = EC5Util.convertPoint(this.ecSpec, paramECPublicKey.getW(), false);
  }
  
  JCEECPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    populateFromPubKeyInfo(paramSubjectPublicKeyInfo);
  }
  
  private void populateFromPubKeyInfo(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    if (paramSubjectPublicKeyInfo.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
      ASN1OctetString aSN1OctetString;
      DERBitString dERBitString = paramSubjectPublicKeyInfo.getPublicKeyData();
      this.algorithm = "ECGOST3410";
      try {
        aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(dERBitString.getBytes());
      } catch (IOException iOException) {
        throw new IllegalArgumentException("error recovering public key");
      } 
      byte[] arrayOfByte1 = aSN1OctetString.getOctets();
      byte[] arrayOfByte2 = new byte[32];
      byte[] arrayOfByte3 = new byte[32];
      byte b;
      for (b = 0; b != arrayOfByte2.length; b++)
        arrayOfByte2[b] = arrayOfByte1[31 - b]; 
      for (b = 0; b != arrayOfByte3.length; b++)
        arrayOfByte3[b] = arrayOfByte1[63 - b]; 
      this.gostParams = new GOST3410PublicKeyAlgParameters((ASN1Sequence)paramSubjectPublicKeyInfo.getAlgorithmId().getParameters());
      ECNamedCurveParameterSpec eCNamedCurveParameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
      ECCurve eCCurve = eCNamedCurveParameterSpec.getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCNamedCurveParameterSpec.getSeed());
      this.q = eCCurve.createPoint(new BigInteger(1, arrayOfByte2), new BigInteger(1, arrayOfByte3), false);
      this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), ellipticCurve, new ECPoint(eCNamedCurveParameterSpec.getG().getAffineXCoord().toBigInteger(), eCNamedCurveParameterSpec.getG().getAffineYCoord().toBigInteger()), eCNamedCurveParameterSpec.getN(), eCNamedCurveParameterSpec.getH());
    } else {
      ECCurve eCCurve;
      ASN1OctetString aSN1OctetString;
      X962Parameters x962Parameters = new X962Parameters((ASN1Primitive)paramSubjectPublicKeyInfo.getAlgorithmId().getParameters());
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)x962Parameters.getParameters();
        X9ECParameters x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
        eCCurve = x9ECParameters.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, x9ECParameters.getSeed());
        this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECUtil.getCurveName(aSN1ObjectIdentifier), ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH());
      } else if (x962Parameters.isImplicitlyCA()) {
        this.ecSpec = null;
        eCCurve = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve();
      } else {
        X9ECParameters x9ECParameters = X9ECParameters.getInstance(x962Parameters.getParameters());
        eCCurve = x9ECParameters.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, x9ECParameters.getSeed());
        this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
      } 
      DERBitString dERBitString = paramSubjectPublicKeyInfo.getPublicKeyData();
      byte[] arrayOfByte = dERBitString.getBytes();
      DEROctetString dEROctetString = new DEROctetString(arrayOfByte);
      if (arrayOfByte[0] == 4 && arrayOfByte[1] == arrayOfByte.length - 2 && (arrayOfByte[2] == 2 || arrayOfByte[2] == 3)) {
        int i = (new X9IntegerConverter()).getByteLength(eCCurve);
        if (i >= arrayOfByte.length - 3)
          try {
            aSN1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(arrayOfByte);
          } catch (IOException iOException) {
            throw new IllegalArgumentException("error recovering public key");
          }  
      } 
      X9ECPoint x9ECPoint = new X9ECPoint(eCCurve, aSN1OctetString);
      this.q = x9ECPoint.getPoint();
    } 
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    if (this.algorithm.equals("ECGOST3410")) {
      X962Parameters x962Parameters;
      if (this.gostParams != null) {
        GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = this.gostParams;
      } else if (this.ecSpec instanceof ECNamedCurveSpec) {
        GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet);
      } else {
        ECCurve eCCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
        X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
        x962Parameters = new X962Parameters(x9ECParameters);
      } 
      BigInteger bigInteger1 = this.q.getAffineXCoord().toBigInteger();
      BigInteger bigInteger2 = this.q.getAffineYCoord().toBigInteger();
      byte[] arrayOfByte = new byte[64];
      extractBytes(arrayOfByte, 0, bigInteger1);
      extractBytes(arrayOfByte, 32, bigInteger2);
      try {
        subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, (ASN1Encodable)x962Parameters), (ASN1Encodable)new DEROctetString(arrayOfByte));
      } catch (IOException iOException) {
        return null;
      } 
    } else {
      X962Parameters x962Parameters;
      if (this.ecSpec instanceof ECNamedCurveSpec) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
        if (aSN1ObjectIdentifier == null)
          aSN1ObjectIdentifier = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()); 
        x962Parameters = new X962Parameters(aSN1ObjectIdentifier);
      } else if (this.ecSpec == null) {
        x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
      } else {
        ECCurve eCCurve1 = EC5Util.convertCurve(this.ecSpec.getCurve());
        X9ECParameters x9ECParameters = new X9ECParameters(eCCurve1, EC5Util.convertPoint(eCCurve1, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
        x962Parameters = new X962Parameters(x9ECParameters);
      } 
      ECCurve eCCurve = engineGetQ().getCurve();
      ASN1OctetString aSN1OctetString = (ASN1OctetString)(new X9ECPoint(eCCurve.createPoint(getQ().getAffineXCoord().toBigInteger(), getQ().getAffineYCoord().toBigInteger(), this.withCompression))).toASN1Primitive();
      subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters), aSN1OctetString.getOctets());
    } 
    return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
  }
  
  private void extractBytes(byte[] paramArrayOfbyte, int paramInt, BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte.length < 32) {
      byte[] arrayOfByte1 = new byte[32];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, arrayOfByte1.length - arrayOfByte.length, arrayOfByte.length);
      arrayOfByte = arrayOfByte1;
    } 
    for (byte b = 0; b != 32; b++)
      paramArrayOfbyte[paramInt + b] = arrayOfByte[arrayOfByte.length - 1 - b]; 
  }
  
  public ECParameterSpec getParams() {
    return this.ecSpec;
  }
  
  public ECParameterSpec getParameters() {
    return (this.ecSpec == null) ? null : EC5Util.convertSpec(this.ecSpec, this.withCompression);
  }
  
  public ECPoint getW() {
    return new ECPoint(this.q.getAffineXCoord().toBigInteger(), this.q.getAffineYCoord().toBigInteger());
  }
  
  public ECPoint getQ() {
    return (this.ecSpec == null) ? this.q.getDetachedPoint() : this.q;
  }
  
  public ECPoint engineGetQ() {
    return this.q;
  }
  
  ECParameterSpec engineGetSpec() {
    return (this.ecSpec != null) ? EC5Util.convertSpec(this.ecSpec, this.withCompression) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("EC Public Key").append(str);
    stringBuffer.append("            X: ").append(this.q.getAffineXCoord().toBigInteger().toString(16)).append(str);
    stringBuffer.append("            Y: ").append(this.q.getAffineYCoord().toBigInteger().toString(16)).append(str);
    return stringBuffer.toString();
  }
  
  public void setPointFormat(String paramString) {
    this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(paramString);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof JCEECPublicKey))
      return false; 
    JCEECPublicKey jCEECPublicKey = (JCEECPublicKey)paramObject;
    return (engineGetQ().equals(jCEECPublicKey.engineGetQ()) && engineGetSpec().equals(jCEECPublicKey.engineGetSpec()));
  }
  
  public int hashCode() {
    return engineGetQ().hashCode() ^ engineGetSpec().hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    byte[] arrayOfByte = (byte[])paramObjectInputStream.readObject();
    populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)));
    this.algorithm = (String)paramObjectInputStream.readObject();
    this.withCompression = paramObjectInputStream.readBoolean();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getEncoded());
    paramObjectOutputStream.writeObject(this.algorithm);
    paramObjectOutputStream.writeBoolean(this.withCompression);
  }
}
