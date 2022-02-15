package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.crypto.rainbow.Layer;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.pqc.jcajce.spec.RainbowPrivateKeySpec;
import org.bouncycastle.util.Arrays;

public class BCRainbowPrivateKey implements PrivateKey {
  private static final long serialVersionUID = 1L;
  
  private short[][] A1inv;
  
  private short[] b1;
  
  private short[][] A2inv;
  
  private short[] b2;
  
  private Layer[] layers;
  
  private int[] vi;
  
  public BCRainbowPrivateKey(short[][] paramArrayOfshort1, short[] paramArrayOfshort2, short[][] paramArrayOfshort3, short[] paramArrayOfshort4, int[] paramArrayOfint, Layer[] paramArrayOfLayer) {
    this.A1inv = paramArrayOfshort1;
    this.b1 = paramArrayOfshort2;
    this.A2inv = paramArrayOfshort3;
    this.b2 = paramArrayOfshort4;
    this.vi = paramArrayOfint;
    this.layers = paramArrayOfLayer;
  }
  
  public BCRainbowPrivateKey(RainbowPrivateKeySpec paramRainbowPrivateKeySpec) {
    this(paramRainbowPrivateKeySpec.getInvA1(), paramRainbowPrivateKeySpec.getB1(), paramRainbowPrivateKeySpec.getInvA2(), paramRainbowPrivateKeySpec.getB2(), paramRainbowPrivateKeySpec.getVi(), paramRainbowPrivateKeySpec.getLayers());
  }
  
  public BCRainbowPrivateKey(RainbowPrivateKeyParameters paramRainbowPrivateKeyParameters) {
    this(paramRainbowPrivateKeyParameters.getInvA1(), paramRainbowPrivateKeyParameters.getB1(), paramRainbowPrivateKeyParameters.getInvA2(), paramRainbowPrivateKeyParameters.getB2(), paramRainbowPrivateKeyParameters.getVi(), paramRainbowPrivateKeyParameters.getLayers());
  }
  
  public short[][] getInvA1() {
    return this.A1inv;
  }
  
  public short[] getB1() {
    return this.b1;
  }
  
  public short[] getB2() {
    return this.b2;
  }
  
  public short[][] getInvA2() {
    return this.A2inv;
  }
  
  public Layer[] getLayers() {
    return this.layers;
  }
  
  public int[] getVi() {
    return this.vi;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof BCRainbowPrivateKey))
      return false; 
    BCRainbowPrivateKey bCRainbowPrivateKey = (BCRainbowPrivateKey)paramObject;
    boolean bool = true;
    bool = (bool && RainbowUtil.equals(this.A1inv, bCRainbowPrivateKey.getInvA1()));
    bool = (bool && RainbowUtil.equals(this.A2inv, bCRainbowPrivateKey.getInvA2()));
    bool = (bool && RainbowUtil.equals(this.b1, bCRainbowPrivateKey.getB1()));
    bool = (bool && RainbowUtil.equals(this.b2, bCRainbowPrivateKey.getB2()));
    bool = (bool && Arrays.equals(this.vi, bCRainbowPrivateKey.getVi()));
    if (this.layers.length != (bCRainbowPrivateKey.getLayers()).length)
      return false; 
    for (int i = this.layers.length - 1; i >= 0; i--)
      bool &= this.layers[i].equals(bCRainbowPrivateKey.getLayers()[i]); 
    return bool;
  }
  
  public int hashCode() {
    int i = this.layers.length;
    i = i * 37 + Arrays.hashCode(this.A1inv);
    i = i * 37 + Arrays.hashCode(this.b1);
    i = i * 37 + Arrays.hashCode(this.A2inv);
    i = i * 37 + Arrays.hashCode(this.b2);
    i = i * 37 + Arrays.hashCode(this.vi);
    for (int j = this.layers.length - 1; j >= 0; j--)
      i = i * 37 + this.layers[j].hashCode(); 
    return i;
  }
  
  public final String getAlgorithm() {
    return "Rainbow";
  }
  
  public byte[] getEncoded() {
    PrivateKeyInfo privateKeyInfo;
    RainbowPrivateKey rainbowPrivateKey = new RainbowPrivateKey(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers);
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, (ASN1Encodable)DERNull.INSTANCE);
      privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)rainbowPrivateKey);
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
}
