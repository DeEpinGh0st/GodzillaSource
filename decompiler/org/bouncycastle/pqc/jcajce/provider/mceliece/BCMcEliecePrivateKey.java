package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

public class BCMcEliecePrivateKey implements CipherParameters, PrivateKey {
  private static final long serialVersionUID = 1L;
  
  private McEliecePrivateKeyParameters params;
  
  public BCMcEliecePrivateKey(McEliecePrivateKeyParameters paramMcEliecePrivateKeyParameters) {
    this.params = paramMcEliecePrivateKeyParameters;
  }
  
  public String getAlgorithm() {
    return "McEliece";
  }
  
  public int getN() {
    return this.params.getN();
  }
  
  public int getK() {
    return this.params.getK();
  }
  
  public GF2mField getField() {
    return this.params.getField();
  }
  
  public PolynomialGF2mSmallM getGoppaPoly() {
    return this.params.getGoppaPoly();
  }
  
  public GF2Matrix getSInv() {
    return this.params.getSInv();
  }
  
  public Permutation getP1() {
    return this.params.getP1();
  }
  
  public Permutation getP2() {
    return this.params.getP2();
  }
  
  public GF2Matrix getH() {
    return this.params.getH();
  }
  
  public PolynomialGF2mSmallM[] getQInv() {
    return this.params.getQInv();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BCMcEliecePrivateKey))
      return false; 
    BCMcEliecePrivateKey bCMcEliecePrivateKey = (BCMcEliecePrivateKey)paramObject;
    return (getN() == bCMcEliecePrivateKey.getN() && getK() == bCMcEliecePrivateKey.getK() && getField().equals(bCMcEliecePrivateKey.getField()) && getGoppaPoly().equals(bCMcEliecePrivateKey.getGoppaPoly()) && getSInv().equals(bCMcEliecePrivateKey.getSInv()) && getP1().equals(bCMcEliecePrivateKey.getP1()) && getP2().equals(bCMcEliecePrivateKey.getP2()));
  }
  
  public int hashCode() {
    int i = this.params.getK();
    i = i * 37 + this.params.getN();
    i = i * 37 + this.params.getField().hashCode();
    i = i * 37 + this.params.getGoppaPoly().hashCode();
    i = i * 37 + this.params.getP1().hashCode();
    i = i * 37 + this.params.getP2().hashCode();
    return i * 37 + this.params.getSInv().hashCode();
  }
  
  public byte[] getEncoded() {
    PrivateKeyInfo privateKeyInfo;
    McEliecePrivateKey mcEliecePrivateKey = new McEliecePrivateKey(this.params.getN(), this.params.getK(), this.params.getField(), this.params.getGoppaPoly(), this.params.getP1(), this.params.getP2(), this.params.getSInv());
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
      privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)mcEliecePrivateKey);
    } catch (IOException iOException) {
      return null;
    } 
    try {
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
