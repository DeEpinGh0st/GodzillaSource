package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

public class BCMcElieceCCA2PrivateKey implements PrivateKey {
  private static final long serialVersionUID = 1L;
  
  private McElieceCCA2PrivateKeyParameters params;
  
  public BCMcElieceCCA2PrivateKey(McElieceCCA2PrivateKeyParameters paramMcElieceCCA2PrivateKeyParameters) {
    this.params = paramMcElieceCCA2PrivateKeyParameters;
  }
  
  public String getAlgorithm() {
    return "McEliece-CCA2";
  }
  
  public int getN() {
    return this.params.getN();
  }
  
  public int getK() {
    return this.params.getK();
  }
  
  public int getT() {
    return this.params.getGoppaPoly().getDegree();
  }
  
  public GF2mField getField() {
    return this.params.getField();
  }
  
  public PolynomialGF2mSmallM getGoppaPoly() {
    return this.params.getGoppaPoly();
  }
  
  public Permutation getP() {
    return this.params.getP();
  }
  
  public GF2Matrix getH() {
    return this.params.getH();
  }
  
  public PolynomialGF2mSmallM[] getQInv() {
    return this.params.getQInv();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof BCMcElieceCCA2PrivateKey))
      return false; 
    BCMcElieceCCA2PrivateKey bCMcElieceCCA2PrivateKey = (BCMcElieceCCA2PrivateKey)paramObject;
    return (getN() == bCMcElieceCCA2PrivateKey.getN() && getK() == bCMcElieceCCA2PrivateKey.getK() && getField().equals(bCMcElieceCCA2PrivateKey.getField()) && getGoppaPoly().equals(bCMcElieceCCA2PrivateKey.getGoppaPoly()) && getP().equals(bCMcElieceCCA2PrivateKey.getP()) && getH().equals(bCMcElieceCCA2PrivateKey.getH()));
  }
  
  public int hashCode() {
    int i = this.params.getK();
    i = i * 37 + this.params.getN();
    i = i * 37 + this.params.getField().hashCode();
    i = i * 37 + this.params.getGoppaPoly().hashCode();
    i = i * 37 + this.params.getP().hashCode();
    return i * 37 + this.params.getH().hashCode();
  }
  
  public byte[] getEncoded() {
    try {
      McElieceCCA2PrivateKey mcElieceCCA2PrivateKey = new McElieceCCA2PrivateKey(getN(), getK(), getField(), getGoppaPoly(), getP(), Utils.getDigAlgId(this.params.getDigest()));
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)mcElieceCCA2PrivateKey);
      return privateKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  AsymmetricKeyParameter getKeyParams() {
    return (AsymmetricKeyParameter)this.params;
  }
}
