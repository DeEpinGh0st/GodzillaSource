package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

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
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
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

public class BCECGOST3410PrivateKey implements ECPrivateKey, ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
  static final long serialVersionUID = 7245981689601667138L;
  
  private String algorithm = "ECGOST3410";
  
  private boolean withCompression;
  
  private transient ASN1Encodable gostParams;
  
  private transient BigInteger d;
  
  private transient ECParameterSpec ecSpec;
  
  private transient DERBitString publicKey;
  
  private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected BCECGOST3410PrivateKey() {}
  
  public BCECGOST3410PrivateKey(ECPrivateKey paramECPrivateKey) {
    this.d = paramECPrivateKey.getS();
    this.algorithm = paramECPrivateKey.getAlgorithm();
    this.ecSpec = paramECPrivateKey.getParams();
  }
  
  public BCECGOST3410PrivateKey(ECPrivateKeySpec paramECPrivateKeySpec) {
    this.d = paramECPrivateKeySpec.getD();
    if (paramECPrivateKeySpec.getParams() != null) {
      ECCurve eCCurve = paramECPrivateKeySpec.getParams().getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, paramECPrivateKeySpec.getParams().getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECPrivateKeySpec.getParams());
    } else {
      this.ecSpec = null;
    } 
  }
  
  public BCECGOST3410PrivateKey(ECPrivateKeySpec paramECPrivateKeySpec) {
    this.d = paramECPrivateKeySpec.getS();
    this.ecSpec = paramECPrivateKeySpec.getParams();
  }
  
  public BCECGOST3410PrivateKey(BCECGOST3410PrivateKey paramBCECGOST3410PrivateKey) {
    this.d = paramBCECGOST3410PrivateKey.d;
    this.ecSpec = paramBCECGOST3410PrivateKey.ecSpec;
    this.withCompression = paramBCECGOST3410PrivateKey.withCompression;
    this.attrCarrier = paramBCECGOST3410PrivateKey.attrCarrier;
    this.publicKey = paramBCECGOST3410PrivateKey.publicKey;
    this.gostParams = paramBCECGOST3410PrivateKey.gostParams;
  }
  
  public BCECGOST3410PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, BCECGOST3410PublicKey paramBCECGOST3410PublicKey, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPrivateKeyParameters.getParameters();
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(eCDomainParameters.getG().getAffineXCoord().toBigInteger(), eCDomainParameters.getG().getAffineYCoord().toBigInteger()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    } else {
      this.ecSpec = paramECParameterSpec;
    } 
    this.gostParams = paramBCECGOST3410PublicKey.getGostParams();
    this.publicKey = getPublicKeyDetails(paramBCECGOST3410PublicKey);
  }
  
  public BCECGOST3410PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, BCECGOST3410PublicKey paramBCECGOST3410PublicKey, ECParameterSpec paramECParameterSpec) {
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
    this.gostParams = paramBCECGOST3410PublicKey.getGostParams();
    this.publicKey = getPublicKeyDetails(paramBCECGOST3410PublicKey);
  }
  
  public BCECGOST3410PrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters) {
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    this.ecSpec = null;
  }
  
  BCECGOST3410PrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    populateFromPrivKeyInfo(paramPrivateKeyInfo);
  }
  
  private void populateFromPrivKeyInfo(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters().toASN1Primitive();
    if (aSN1Primitive instanceof ASN1Sequence && (ASN1Sequence.getInstance(aSN1Primitive).size() == 2 || ASN1Sequence.getInstance(aSN1Primitive).size() == 3)) {
      GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = GOST3410PublicKeyAlgParameters.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
      this.gostParams = (ASN1Encodable)gOST3410PublicKeyAlgParameters;
      ECNamedCurveParameterSpec eCNamedCurveParameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet()));
      ECCurve eCCurve = eCNamedCurveParameterSpec.getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, eCNamedCurveParameterSpec.getSeed());
      this.ecSpec = (ECParameterSpec)new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet()), ellipticCurve, new ECPoint(eCNamedCurveParameterSpec.getG().getAffineXCoord().toBigInteger(), eCNamedCurveParameterSpec.getG().getAffineYCoord().toBigInteger()), eCNamedCurveParameterSpec.getN(), eCNamedCurveParameterSpec.getH());
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
    if (this.gostParams != null) {
      byte[] arrayOfByte = new byte[32];
      extractBytes(arrayOfByte, 0, getS());
      try {
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, this.gostParams), (ASN1Encodable)new DEROctetString(arrayOfByte));
        return privateKeyInfo.getEncoded("DER");
      } catch (IOException iOException) {
        return null;
      } 
    } 
    if (this.ecSpec instanceof ECNamedCurveSpec) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
      if (aSN1ObjectIdentifier == null)
        aSN1ObjectIdentifier = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()); 
      x962Parameters = new X962Parameters(aSN1ObjectIdentifier);
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
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, (ASN1Encodable)x962Parameters.toASN1Primitive()), (ASN1Encodable)eCPrivateKey.toASN1Primitive());
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
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
    if (!(paramObject instanceof BCECGOST3410PrivateKey))
      return false; 
    BCECGOST3410PrivateKey bCECGOST3410PrivateKey = (BCECGOST3410PrivateKey)paramObject;
    return (getD().equals(bCECGOST3410PrivateKey.getD()) && engineGetSpec().equals(bCECGOST3410PrivateKey.engineGetSpec()));
  }
  
  public int hashCode() {
    return getD().hashCode() ^ engineGetSpec().hashCode();
  }
  
  public String toString() {
    return ECUtil.privateKeyToString(this.algorithm, this.d, engineGetSpec());
  }
  
  private DERBitString getPublicKeyDetails(BCECGOST3410PublicKey paramBCECGOST3410PublicKey) {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramBCECGOST3410PublicKey.getEncoded()));
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
