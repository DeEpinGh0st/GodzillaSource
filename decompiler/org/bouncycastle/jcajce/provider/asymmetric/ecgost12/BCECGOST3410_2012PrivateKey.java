package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCECGOST3410_2012PrivateKey implements ECPrivateKey, ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
  static final long serialVersionUID = 7245981689601667138L;
  
  private String algorithm = "ECGOST3410-2012";
  
  private boolean withCompression;
  
  private transient GOST3410PublicKeyAlgParameters gostParams;
  
  private transient BigInteger d;
  
  private transient ECParameterSpec ecSpec;
  
  private transient DERBitString publicKey;
  
  private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected BCECGOST3410_2012PrivateKey() {}
  
  public BCECGOST3410_2012PrivateKey(ECPrivateKey paramECPrivateKey) {
    this.d = paramECPrivateKey.getS();
    this.algorithm = paramECPrivateKey.getAlgorithm();
    this.ecSpec = paramECPrivateKey.getParams();
  }
  
  public BCECGOST3410_2012PrivateKey(ECPrivateKeySpec paramECPrivateKeySpec) {
    this.d = paramECPrivateKeySpec.getD();
    if (paramECPrivateKeySpec.getParams() != null) {
      ECCurve eCCurve = paramECPrivateKeySpec.getParams().getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, paramECPrivateKeySpec.getParams().getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECPrivateKeySpec.getParams());
    } else {
      this.ecSpec = null;
    } 
  }
  
  public BCECGOST3410_2012PrivateKey(ECPrivateKeySpec paramECPrivateKeySpec) {
    this.d = paramECPrivateKeySpec.getS();
    this.ecSpec = paramECPrivateKeySpec.getParams();
  }
  
  public BCECGOST3410_2012PrivateKey(BCECGOST3410_2012PrivateKey paramBCECGOST3410_2012PrivateKey) {
    this.d = paramBCECGOST3410_2012PrivateKey.d;
    this.ecSpec = paramBCECGOST3410_2012PrivateKey.ecSpec;
    this.withCompression = paramBCECGOST3410_2012PrivateKey.withCompression;
    this.attrCarrier = paramBCECGOST3410_2012PrivateKey.attrCarrier;
    this.publicKey = paramBCECGOST3410_2012PrivateKey.publicKey;
    this.gostParams = paramBCECGOST3410_2012PrivateKey.gostParams;
  }
  
  public BCECGOST3410_2012PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, BCECGOST3410_2012PublicKey paramBCECGOST3410_2012PublicKey, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPrivateKeyParameters.getParameters();
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(eCDomainParameters.getG().getAffineXCoord().toBigInteger(), eCDomainParameters.getG().getAffineYCoord().toBigInteger()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    } else {
      this.ecSpec = paramECParameterSpec;
    } 
    this.gostParams = paramBCECGOST3410_2012PublicKey.getGostParams();
    this.publicKey = getPublicKeyDetails(paramBCECGOST3410_2012PublicKey);
  }
  
  public BCECGOST3410_2012PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, BCECGOST3410_2012PublicKey paramBCECGOST3410_2012PublicKey, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPrivateKeyParameters.getParameters();
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(eCDomainParameters.getG().getAffineXCoord().toBigInteger(), eCDomainParameters.getG().getAffineYCoord().toBigInteger()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    } else {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(paramECParameterSpec.getCurve(), paramECParameterSpec.getSeed());
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(paramECParameterSpec.getG().getAffineXCoord().toBigInteger(), paramECParameterSpec.getG().getAffineYCoord().toBigInteger()), paramECParameterSpec.getN(), paramECParameterSpec.getH().intValue());
    } 
    this.gostParams = paramBCECGOST3410_2012PublicKey.getGostParams();
    this.publicKey = getPublicKeyDetails(paramBCECGOST3410_2012PublicKey);
  }
  
  public BCECGOST3410_2012PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters) {
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    this.ecSpec = null;
  }
  
  BCECGOST3410_2012PrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    populateFromPrivKeyInfo(paramPrivateKeyInfo);
  }
  
  private void populateFromPrivKeyInfo(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters().toASN1Primitive();
    if (aSN1Primitive instanceof ASN1Sequence && (ASN1Sequence.getInstance(aSN1Primitive).size() == 2 || ASN1Sequence.getInstance(aSN1Primitive).size() == 3)) {
      this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
      ECNamedCurveParameterSpec eCNamedCurveParameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
      ECCurve eCCurve = eCNamedCurveParameterSpec.getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCNamedCurveParameterSpec.getSeed());
      this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), ellipticCurve, new ECPoint(eCNamedCurveParameterSpec.getG().getAffineXCoord().toBigInteger(), eCNamedCurveParameterSpec.getG().getAffineYCoord().toBigInteger()), eCNamedCurveParameterSpec.getN(), eCNamedCurveParameterSpec.getH());
      ASN1Encodable aSN1Encodable = paramPrivateKeyInfo.parsePrivateKey();
      if (aSN1Encodable instanceof ASN1Integer) {
        this.d = ASN1Integer.getInstance(aSN1Encodable).getPositiveValue();
      } else {
        byte[] arrayOfByte1 = ASN1OctetString.getInstance(aSN1Encodable).getOctets();
        byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
        for (byte b = 0; b != arrayOfByte1.length; b++)
          arrayOfByte2[b] = arrayOfByte1[arrayOfByte1.length - 1 - b]; 
        this.d = new BigInteger(1, arrayOfByte2);
      } 
    } else {
      X962Parameters x962Parameters = X962Parameters.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
        X9ECParameters x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
        if (x9ECParameters == null) {
          ECDomainParameters eCDomainParameters = ECGOST3410NamedCurves.getByOID(aSN1ObjectIdentifier);
          EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
          this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(aSN1ObjectIdentifier), ellipticCurve, new ECPoint(eCDomainParameters.getG().getAffineXCoord().toBigInteger(), eCDomainParameters.getG().getAffineYCoord().toBigInteger()), eCDomainParameters.getN(), eCDomainParameters.getH());
        } else {
          EllipticCurve ellipticCurve = EC5Util.convertCurve(x9ECParameters.getCurve(), x9ECParameters.getSeed());
          this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECUtil.getCurveName(aSN1ObjectIdentifier), ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH());
        } 
      } else if (x962Parameters.isImplicitlyCA()) {
        this.ecSpec = null;
      } else {
        X9ECParameters x9ECParameters = X9ECParameters.getInstance(x962Parameters.getParameters());
        EllipticCurve ellipticCurve = EC5Util.convertCurve(x9ECParameters.getCurve(), x9ECParameters.getSeed());
        this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(x9ECParameters.getG().getAffineXCoord().toBigInteger(), x9ECParameters.getG().getAffineYCoord().toBigInteger()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
      } 
      ASN1Encodable aSN1Encodable = paramPrivateKeyInfo.parsePrivateKey();
      if (aSN1Encodable instanceof ASN1Integer) {
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Encodable);
        this.d = aSN1Integer.getValue();
      } else {
        ECPrivateKey eCPrivateKey = ECPrivateKey.getInstance(aSN1Encodable);
        this.d = eCPrivateKey.getKey();
        this.publicKey = eCPrivateKey.getPublicKey();
      } 
    } 
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    X962Parameters x962Parameters;
    int i;
    ECPrivateKey eCPrivateKey;
    boolean bool = (this.d.bitLength() > 256) ? true : false;
    ASN1ObjectIdentifier aSN1ObjectIdentifier = bool ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
    byte b = bool ? 64 : 32;
    if (this.gostParams != null) {
      byte[] arrayOfByte = new byte[b];
      extractBytes(arrayOfByte, b, 0, getS());
      try {
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(aSN1ObjectIdentifier, (ASN1Encodable)this.gostParams), (ASN1Encodable)new DEROctetString(arrayOfByte));
        return privateKeyInfo.getEncoded("DER");
      } catch (IOException iOException) {
        return null;
      } 
    } 
    if (this.ecSpec instanceof ECNamedCurveSpec) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier1 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
      if (aSN1ObjectIdentifier1 == null)
        aSN1ObjectIdentifier1 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()); 
      x962Parameters = new X962Parameters(aSN1ObjectIdentifier1);
      i = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), getS());
    } else if (this.ecSpec == null) {
      x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
      i = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, getS());
    } else {
      ECCurve eCCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
      X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
      x962Parameters = new X962Parameters(x9ECParameters);
      i = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), getS());
    } 
    if (this.publicKey != null) {
      eCPrivateKey = new ECPrivateKey(i, getS(), this.publicKey, (ASN1Encodable)x962Parameters);
    } else {
      eCPrivateKey = new ECPrivateKey(i, getS(), (ASN1Encodable)x962Parameters);
    } 
    try {
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(aSN1ObjectIdentifier, (ASN1Encodable)x962Parameters.toASN1Primitive()), (ASN1Encodable)eCPrivateKey.toASN1Primitive());
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
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
  
  ECParameterSpec engineGetSpec() {
    return (this.ecSpec != null) ? EC5Util.convertSpec(this.ecSpec, this.withCompression) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
  }
  
  public BigInteger getS() {
    return this.d;
  }
  
  public BigInteger getD() {
    return this.d;
  }
  
  public void setBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.attrCarrier.setBagAttribute(paramASN1ObjectIdentifier, paramASN1Encodable);
  }
  
  public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.attrCarrier.getBagAttribute(paramASN1ObjectIdentifier);
  }
  
  public Enumeration getBagAttributeKeys() {
    return this.attrCarrier.getBagAttributeKeys();
  }
  
  public void setPointFormat(String paramString) {
    this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(paramString);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BCECGOST3410_2012PrivateKey))
      return false; 
    BCECGOST3410_2012PrivateKey bCECGOST3410_2012PrivateKey = (BCECGOST3410_2012PrivateKey)paramObject;
    return (getD().equals(bCECGOST3410_2012PrivateKey.getD()) && engineGetSpec().equals(bCECGOST3410_2012PrivateKey.engineGetSpec()));
  }
  
  public int hashCode() {
    return getD().hashCode() ^ engineGetSpec().hashCode();
  }
  
  public String toString() {
    return ECUtil.privateKeyToString(this.algorithm, this.d, engineGetSpec());
  }
  
  private DERBitString getPublicKeyDetails(BCECGOST3410_2012PublicKey paramBCECGOST3410_2012PublicKey) {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramBCECGOST3410_2012PublicKey.getEncoded()));
      return subjectPublicKeyInfo.getPublicKeyData();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    byte[] arrayOfByte = (byte[])paramObjectInputStream.readObject();
    populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)));
    this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getEncoded());
  }
}
