package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class BCRSAPrivateKey implements RSAPrivateKey, PKCS12BagAttributeCarrier {
  static final long serialVersionUID = 5110188922551353628L;
  
  private static BigInteger ZERO = BigInteger.valueOf(0L);
  
  protected BigInteger modulus;
  
  protected BigInteger privateExponent;
  
  private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected BCRSAPrivateKey() {}
  
  BCRSAPrivateKey(RSAKeyParameters paramRSAKeyParameters) {
    this.modulus = paramRSAKeyParameters.getModulus();
    this.privateExponent = paramRSAKeyParameters.getExponent();
  }
  
  BCRSAPrivateKey(RSAPrivateKeySpec paramRSAPrivateKeySpec) {
    this.modulus = paramRSAPrivateKeySpec.getModulus();
    this.privateExponent = paramRSAPrivateKeySpec.getPrivateExponent();
  }
  
  BCRSAPrivateKey(RSAPrivateKey paramRSAPrivateKey) {
    this.modulus = paramRSAPrivateKey.getModulus();
    this.privateExponent = paramRSAPrivateKey.getPrivateExponent();
  }
  
  BCRSAPrivateKey(RSAPrivateKey paramRSAPrivateKey) {
    this.modulus = paramRSAPrivateKey.getModulus();
    this.privateExponent = paramRSAPrivateKey.getPrivateExponent();
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPrivateExponent() {
    return this.privateExponent;
  }
  
  public String getAlgorithm() {
    return "RSA";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)new RSAPrivateKey(getModulus(), ZERO, getPrivateExponent(), ZERO, ZERO, ZERO, ZERO, ZERO));
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof RSAPrivateKey))
      return false; 
    if (paramObject == this)
      return true; 
    RSAPrivateKey rSAPrivateKey = (RSAPrivateKey)paramObject;
    return (getModulus().equals(rSAPrivateKey.getModulus()) && getPrivateExponent().equals(rSAPrivateKey.getPrivateExponent()));
  }
  
  public int hashCode() {
    return getModulus().hashCode() ^ getPrivateExponent().hashCode();
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
    paramObjectInputStream.defaultReadObject();
    this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
  }
}
