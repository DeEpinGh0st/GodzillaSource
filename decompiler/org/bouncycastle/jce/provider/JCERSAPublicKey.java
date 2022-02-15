package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class JCERSAPublicKey implements RSAPublicKey {
  static final long serialVersionUID = 2675817738516720772L;
  
  private BigInteger modulus;
  
  private BigInteger publicExponent;
  
  JCERSAPublicKey(RSAKeyParameters paramRSAKeyParameters) {
    this.modulus = paramRSAKeyParameters.getModulus();
    this.publicExponent = paramRSAKeyParameters.getExponent();
  }
  
  JCERSAPublicKey(RSAPublicKeySpec paramRSAPublicKeySpec) {
    this.modulus = paramRSAPublicKeySpec.getModulus();
    this.publicExponent = paramRSAPublicKeySpec.getPublicExponent();
  }
  
  JCERSAPublicKey(RSAPublicKey paramRSAPublicKey) {
    this.modulus = paramRSAPublicKey.getModulus();
    this.publicExponent = paramRSAPublicKey.getPublicExponent();
  }
  
  JCERSAPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    try {
      RSAPublicKey rSAPublicKey = RSAPublicKey.getInstance(paramSubjectPublicKeyInfo.parsePublicKey());
      this.modulus = rSAPublicKey.getModulus();
      this.publicExponent = rSAPublicKey.getPublicExponent();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("invalid info structure in RSA public key");
    } 
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPublicExponent() {
    return this.publicExponent;
  }
  
  public String getAlgorithm() {
    return "RSA";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)new RSAPublicKey(getModulus(), getPublicExponent()));
  }
  
  public int hashCode() {
    return getModulus().hashCode() ^ getPublicExponent().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof RSAPublicKey))
      return false; 
    RSAPublicKey rSAPublicKey = (RSAPublicKey)paramObject;
    return (getModulus().equals(rSAPublicKey.getModulus()) && getPublicExponent().equals(rSAPublicKey.getPublicExponent()));
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("RSA Public Key").append(str);
    stringBuffer.append("            modulus: ").append(getModulus().toString(16)).append(str);
    stringBuffer.append("    public exponent: ").append(getPublicExponent().toString(16)).append(str);
    return stringBuffer.toString();
  }
}
