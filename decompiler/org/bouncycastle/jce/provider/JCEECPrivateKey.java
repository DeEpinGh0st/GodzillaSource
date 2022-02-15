package org.bouncycastle.jce.provider;

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
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKeyStructure;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class JCEECPrivateKey implements ECPrivateKey, ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
  private String algorithm = "EC";
  
  private BigInteger d;
  
  private ECParameterSpec ecSpec;
  
  private boolean withCompression;
  
  private DERBitString publicKey;
  
  private PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected JCEECPrivateKey() {}
  
  public JCEECPrivateKey(ECPrivateKey paramECPrivateKey) {
    this.d = paramECPrivateKey.getS();
    this.algorithm = paramECPrivateKey.getAlgorithm();
    this.ecSpec = paramECPrivateKey.getParams();
  }
  
  public JCEECPrivateKey(String paramString, ECPrivateKeySpec paramECPrivateKeySpec) {
    this.algorithm = paramString;
    this.d = paramECPrivateKeySpec.getD();
    if (paramECPrivateKeySpec.getParams() != null) {
      ECCurve eCCurve = paramECPrivateKeySpec.getParams().getCurve();
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCCurve, paramECPrivateKeySpec.getParams().getSeed());
      this.ecSpec = EC5Util.convertSpec(ellipticCurve, paramECPrivateKeySpec.getParams());
    } else {
      this.ecSpec = null;
    } 
  }
  
  public JCEECPrivateKey(String paramString, ECPrivateKeySpec paramECPrivateKeySpec) {
    this.algorithm = paramString;
    this.d = paramECPrivateKeySpec.getS();
    this.ecSpec = paramECPrivateKeySpec.getParams();
  }
  
  public JCEECPrivateKey(String paramString, JCEECPrivateKey paramJCEECPrivateKey) {
    this.algorithm = paramString;
    this.d = paramJCEECPrivateKey.d;
    this.ecSpec = paramJCEECPrivateKey.ecSpec;
    this.withCompression = paramJCEECPrivateKey.withCompression;
    this.attrCarrier = paramJCEECPrivateKey.attrCarrier;
    this.publicKey = paramJCEECPrivateKey.publicKey;
  }
  
  public JCEECPrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, JCEECPublicKey paramJCEECPublicKey, ECParameterSpec paramECParameterSpec) {
    ECDomainParameters eCDomainParameters = paramECPrivateKeyParameters.getParameters();
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    if (paramECParameterSpec == null) {
      EllipticCurve ellipticCurve = EC5Util.convertCurve(eCDomainParameters.getCurve(), eCDomainParameters.getSeed());
      this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(eCDomainParameters.getG().getAffineXCoord().toBigInteger(), eCDomainParameters.getG().getAffineYCoord().toBigInteger()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    } else {
      this.ecSpec = paramECParameterSpec;
    } 
    this.publicKey = getPublicKeyDetails(paramJCEECPublicKey);
  }
  
  public JCEECPrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters, JCEECPublicKey paramJCEECPublicKey, ECParameterSpec paramECParameterSpec) {
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
    this.publicKey = getPublicKeyDetails(paramJCEECPublicKey);
  }
  
  public JCEECPrivateKey(String paramString, ECPrivateKeyParameters paramECPrivateKeyParameters) {
    this.algorithm = paramString;
    this.d = paramECPrivateKeyParameters.getD();
    this.ecSpec = null;
  }
  
  JCEECPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    populateFromPrivKeyInfo(paramPrivateKeyInfo);
  }
  
  private void populateFromPrivKeyInfo(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    X962Parameters x962Parameters = new X962Parameters((ASN1Primitive)paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
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
      ECPrivateKeyStructure eCPrivateKeyStructure = new ECPrivateKeyStructure((ASN1Sequence)aSN1Encodable);
      this.d = eCPrivateKeyStructure.getKey();
      this.publicKey = eCPrivateKeyStructure.getPublicKey();
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
    ECPrivateKeyStructure eCPrivateKeyStructure;
    if (this.ecSpec instanceof ECNamedCurveSpec) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
      if (aSN1ObjectIdentifier == null)
        aSN1ObjectIdentifier = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()); 
      x962Parameters = new X962Parameters(aSN1ObjectIdentifier);
    } else if (this.ecSpec == null) {
      x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
    } else {
      ECCurve eCCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
      X9ECParameters x9ECParameters = new X9ECParameters(eCCurve, EC5Util.convertPoint(eCCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
      x962Parameters = new X962Parameters(x9ECParameters);
    } 
    if (this.publicKey != null) {
      eCPrivateKeyStructure = new ECPrivateKeyStructure(getS(), this.publicKey, (ASN1Encodable)x962Parameters);
    } else {
      eCPrivateKeyStructure = new ECPrivateKeyStructure(getS(), (ASN1Encodable)x962Parameters);
    } 
    try {
      PrivateKeyInfo privateKeyInfo;
      if (this.algorithm.equals("ECGOST3410")) {
        privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, (ASN1Encodable)x962Parameters.toASN1Primitive()), (ASN1Encodable)eCPrivateKeyStructure.toASN1Primitive());
      } else {
        privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters.toASN1Primitive()), (ASN1Encodable)eCPrivateKeyStructure.toASN1Primitive());
      } 
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
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
    if (!(paramObject instanceof JCEECPrivateKey))
      return false; 
    JCEECPrivateKey jCEECPrivateKey = (JCEECPrivateKey)paramObject;
    return (getD().equals(jCEECPrivateKey.getD()) && engineGetSpec().equals(jCEECPrivateKey.engineGetSpec()));
  }
  
  public int hashCode() {
    return getD().hashCode() ^ engineGetSpec().hashCode();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("EC Private Key").append(str);
    stringBuffer.append("             S: ").append(this.d.toString(16)).append(str);
    return stringBuffer.toString();
  }
  
  private DERBitString getPublicKeyDetails(JCEECPublicKey paramJCEECPublicKey) {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramJCEECPublicKey.getEncoded()));
      return subjectPublicKeyInfo.getPublicKeyData();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    byte[] arrayOfByte = (byte[])paramObjectInputStream.readObject();
    populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)));
    this.algorithm = (String)paramObjectInputStream.readObject();
    this.withCompression = paramObjectInputStream.readBoolean();
    this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    this.attrCarrier.readObject(paramObjectInputStream);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getEncoded());
    paramObjectOutputStream.writeObject(this.algorithm);
    paramObjectOutputStream.writeBoolean(this.withCompression);
    this.attrCarrier.writeObject(paramObjectOutputStream);
  }
}
