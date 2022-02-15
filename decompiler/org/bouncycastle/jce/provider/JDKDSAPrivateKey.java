package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class JDKDSAPrivateKey implements DSAPrivateKey, PKCS12BagAttributeCarrier {
  private static final long serialVersionUID = -4677259546958385734L;
  
  BigInteger x;
  
  DSAParams dsaSpec;
  
  private PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected JDKDSAPrivateKey() {}
  
  JDKDSAPrivateKey(DSAPrivateKey paramDSAPrivateKey) {
    this.x = paramDSAPrivateKey.getX();
    this.dsaSpec = paramDSAPrivateKey.getParams();
  }
  
  JDKDSAPrivateKey(DSAPrivateKeySpec paramDSAPrivateKeySpec) {
    this.x = paramDSAPrivateKeySpec.getX();
    this.dsaSpec = new DSAParameterSpec(paramDSAPrivateKeySpec.getP(), paramDSAPrivateKeySpec.getQ(), paramDSAPrivateKeySpec.getG());
  }
  
  JDKDSAPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    DSAParameter dSAParameter = DSAParameter.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
    ASN1Integer aSN1Integer = ASN1Integer.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    this.x = aSN1Integer.getValue();
    this.dsaSpec = new DSAParameterSpec(dSAParameter.getP(), dSAParameter.getQ(), dSAParameter.getG());
  }
  
  JDKDSAPrivateKey(DSAPrivateKeyParameters paramDSAPrivateKeyParameters) {
    this.x = paramDSAPrivateKeyParameters.getX();
    this.dsaSpec = new DSAParameterSpec(paramDSAPrivateKeyParameters.getParameters().getP(), paramDSAPrivateKeyParameters.getParameters().getQ(), paramDSAPrivateKeyParameters.getParameters().getG());
  }
  
  public String getAlgorithm() {
    return "DSA";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    try {
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())), (ASN1Encodable)new ASN1Integer(getX()));
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public DSAParams getParams() {
    return this.dsaSpec;
  }
  
  public BigInteger getX() {
    return this.x;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DSAPrivateKey))
      return false; 
    DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramObject;
    return (getX().equals(dSAPrivateKey.getX()) && getParams().getG().equals(dSAPrivateKey.getParams().getG()) && getParams().getP().equals(dSAPrivateKey.getParams().getP()) && getParams().getQ().equals(dSAPrivateKey.getParams().getQ()));
  }
  
  public int hashCode() {
    return getX().hashCode() ^ getParams().getG().hashCode() ^ getParams().getP().hashCode() ^ getParams().getQ().hashCode();
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
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.x = (BigInteger)paramObjectInputStream.readObject();
    this.dsaSpec = new DSAParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
    this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    this.attrCarrier.readObject(paramObjectInputStream);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(this.x);
    paramObjectOutputStream.writeObject(this.dsaSpec.getP());
    paramObjectOutputStream.writeObject(this.dsaSpec.getQ());
    paramObjectOutputStream.writeObject(this.dsaSpec.getG());
    this.attrCarrier.writeObject(paramObjectOutputStream);
  }
}
