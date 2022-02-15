package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class JCERSAPrivateCrtKey extends JCERSAPrivateKey implements RSAPrivateCrtKey {
  static final long serialVersionUID = 7834723820638524718L;
  
  private BigInteger publicExponent;
  
  private BigInteger primeP;
  
  private BigInteger primeQ;
  
  private BigInteger primeExponentP;
  
  private BigInteger primeExponentQ;
  
  private BigInteger crtCoefficient;
  
  JCERSAPrivateCrtKey(RSAPrivateCrtKeyParameters paramRSAPrivateCrtKeyParameters) {
    super((RSAKeyParameters)paramRSAPrivateCrtKeyParameters);
    this.publicExponent = paramRSAPrivateCrtKeyParameters.getPublicExponent();
    this.primeP = paramRSAPrivateCrtKeyParameters.getP();
    this.primeQ = paramRSAPrivateCrtKeyParameters.getQ();
    this.primeExponentP = paramRSAPrivateCrtKeyParameters.getDP();
    this.primeExponentQ = paramRSAPrivateCrtKeyParameters.getDQ();
    this.crtCoefficient = paramRSAPrivateCrtKeyParameters.getQInv();
  }
  
  JCERSAPrivateCrtKey(RSAPrivateCrtKeySpec paramRSAPrivateCrtKeySpec) {
    this.modulus = paramRSAPrivateCrtKeySpec.getModulus();
    this.publicExponent = paramRSAPrivateCrtKeySpec.getPublicExponent();
    this.privateExponent = paramRSAPrivateCrtKeySpec.getPrivateExponent();
    this.primeP = paramRSAPrivateCrtKeySpec.getPrimeP();
    this.primeQ = paramRSAPrivateCrtKeySpec.getPrimeQ();
    this.primeExponentP = paramRSAPrivateCrtKeySpec.getPrimeExponentP();
    this.primeExponentQ = paramRSAPrivateCrtKeySpec.getPrimeExponentQ();
    this.crtCoefficient = paramRSAPrivateCrtKeySpec.getCrtCoefficient();
  }
  
  JCERSAPrivateCrtKey(RSAPrivateCrtKey paramRSAPrivateCrtKey) {
    this.modulus = paramRSAPrivateCrtKey.getModulus();
    this.publicExponent = paramRSAPrivateCrtKey.getPublicExponent();
    this.privateExponent = paramRSAPrivateCrtKey.getPrivateExponent();
    this.primeP = paramRSAPrivateCrtKey.getPrimeP();
    this.primeQ = paramRSAPrivateCrtKey.getPrimeQ();
    this.primeExponentP = paramRSAPrivateCrtKey.getPrimeExponentP();
    this.primeExponentQ = paramRSAPrivateCrtKey.getPrimeExponentQ();
    this.crtCoefficient = paramRSAPrivateCrtKey.getCrtCoefficient();
  }
  
  JCERSAPrivateCrtKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    this(RSAPrivateKey.getInstance(paramPrivateKeyInfo.parsePrivateKey()));
  }
  
  JCERSAPrivateCrtKey(RSAPrivateKey paramRSAPrivateKey) {
    this.modulus = paramRSAPrivateKey.getModulus();
    this.publicExponent = paramRSAPrivateKey.getPublicExponent();
    this.privateExponent = paramRSAPrivateKey.getPrivateExponent();
    this.primeP = paramRSAPrivateKey.getPrime1();
    this.primeQ = paramRSAPrivateKey.getPrime2();
    this.primeExponentP = paramRSAPrivateKey.getExponent1();
    this.primeExponentQ = paramRSAPrivateKey.getExponent2();
    this.crtCoefficient = paramRSAPrivateKey.getCoefficient();
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)new RSAPrivateKey(getModulus(), getPublicExponent(), getPrivateExponent(), getPrimeP(), getPrimeQ(), getPrimeExponentP(), getPrimeExponentQ(), getCrtCoefficient()));
  }
  
  public BigInteger getPublicExponent() {
    return this.publicExponent;
  }
  
  public BigInteger getPrimeP() {
    return this.primeP;
  }
  
  public BigInteger getPrimeQ() {
    return this.primeQ;
  }
  
  public BigInteger getPrimeExponentP() {
    return this.primeExponentP;
  }
  
  public BigInteger getPrimeExponentQ() {
    return this.primeExponentQ;
  }
  
  public BigInteger getCrtCoefficient() {
    return this.crtCoefficient;
  }
  
  public int hashCode() {
    return getModulus().hashCode() ^ getPublicExponent().hashCode() ^ getPrivateExponent().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof RSAPrivateCrtKey))
      return false; 
    RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey)paramObject;
    return (getModulus().equals(rSAPrivateCrtKey.getModulus()) && getPublicExponent().equals(rSAPrivateCrtKey.getPublicExponent()) && getPrivateExponent().equals(rSAPrivateCrtKey.getPrivateExponent()) && getPrimeP().equals(rSAPrivateCrtKey.getPrimeP()) && getPrimeQ().equals(rSAPrivateCrtKey.getPrimeQ()) && getPrimeExponentP().equals(rSAPrivateCrtKey.getPrimeExponentP()) && getPrimeExponentQ().equals(rSAPrivateCrtKey.getPrimeExponentQ()) && getCrtCoefficient().equals(rSAPrivateCrtKey.getCrtCoefficient()));
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("RSA Private CRT Key").append(str);
    stringBuffer.append("            modulus: ").append(getModulus().toString(16)).append(str);
    stringBuffer.append("    public exponent: ").append(getPublicExponent().toString(16)).append(str);
    stringBuffer.append("   private exponent: ").append(getPrivateExponent().toString(16)).append(str);
    stringBuffer.append("             primeP: ").append(getPrimeP().toString(16)).append(str);
    stringBuffer.append("             primeQ: ").append(getPrimeQ().toString(16)).append(str);
    stringBuffer.append("     primeExponentP: ").append(getPrimeExponentP().toString(16)).append(str);
    stringBuffer.append("     primeExponentQ: ").append(getPrimeExponentQ().toString(16)).append(str);
    stringBuffer.append("     crtCoefficient: ").append(getCrtCoefficient().toString(16)).append(str);
    return stringBuffer.toString();
  }
}
