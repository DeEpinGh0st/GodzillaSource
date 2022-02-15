package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.RainbowPublicKeySpec;
import org.bouncycastle.util.Arrays;

public class BCRainbowPublicKey implements PublicKey {
  private static final long serialVersionUID = 1L;
  
  private short[][] coeffquadratic;
  
  private short[][] coeffsingular;
  
  private short[] coeffscalar;
  
  private int docLength;
  
  private RainbowParameters rainbowParams;
  
  public BCRainbowPublicKey(int paramInt, short[][] paramArrayOfshort1, short[][] paramArrayOfshort2, short[] paramArrayOfshort) {
    this.docLength = paramInt;
    this.coeffquadratic = paramArrayOfshort1;
    this.coeffsingular = paramArrayOfshort2;
    this.coeffscalar = paramArrayOfshort;
  }
  
  public BCRainbowPublicKey(RainbowPublicKeySpec paramRainbowPublicKeySpec) {
    this(paramRainbowPublicKeySpec.getDocLength(), paramRainbowPublicKeySpec.getCoeffQuadratic(), paramRainbowPublicKeySpec.getCoeffSingular(), paramRainbowPublicKeySpec.getCoeffScalar());
  }
  
  public BCRainbowPublicKey(RainbowPublicKeyParameters paramRainbowPublicKeyParameters) {
    this(paramRainbowPublicKeyParameters.getDocLength(), paramRainbowPublicKeyParameters.getCoeffQuadratic(), paramRainbowPublicKeyParameters.getCoeffSingular(), paramRainbowPublicKeyParameters.getCoeffScalar());
  }
  
  public int getDocLength() {
    return this.docLength;
  }
  
  public short[][] getCoeffQuadratic() {
    return this.coeffquadratic;
  }
  
  public short[][] getCoeffSingular() {
    short[][] arrayOfShort = new short[this.coeffsingular.length][];
    for (byte b = 0; b != this.coeffsingular.length; b++)
      arrayOfShort[b] = Arrays.clone(this.coeffsingular[b]); 
    return arrayOfShort;
  }
  
  public short[] getCoeffScalar() {
    return Arrays.clone(this.coeffscalar);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof BCRainbowPublicKey))
      return false; 
    BCRainbowPublicKey bCRainbowPublicKey = (BCRainbowPublicKey)paramObject;
    return (this.docLength == bCRainbowPublicKey.getDocLength() && RainbowUtil.equals(this.coeffquadratic, bCRainbowPublicKey.getCoeffQuadratic()) && RainbowUtil.equals(this.coeffsingular, bCRainbowPublicKey.getCoeffSingular()) && RainbowUtil.equals(this.coeffscalar, bCRainbowPublicKey.getCoeffScalar()));
  }
  
  public int hashCode() {
    null = this.docLength;
    null = null * 37 + Arrays.hashCode(this.coeffquadratic);
    null = null * 37 + Arrays.hashCode(this.coeffsingular);
    return null * 37 + Arrays.hashCode(this.coeffscalar);
  }
  
  public final String getAlgorithm() {
    return "Rainbow";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    RainbowPublicKey rainbowPublicKey = new RainbowPublicKey(this.docLength, this.coeffquadratic, this.coeffsingular, this.coeffscalar);
    AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, (ASN1Encodable)DERNull.INSTANCE);
    return KeyUtil.getEncodedSubjectPublicKeyInfo(algorithmIdentifier, (ASN1Encodable)rainbowPublicKey);
  }
}
