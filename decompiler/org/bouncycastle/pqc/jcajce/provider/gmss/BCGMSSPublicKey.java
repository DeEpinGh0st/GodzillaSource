package org.bouncycastle.pqc.jcajce.provider.gmss;

import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.GMSSPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.ParSet;
import org.bouncycastle.pqc.crypto.gmss.GMSSParameters;
import org.bouncycastle.pqc.crypto.gmss.GMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.util.encoders.Hex;

public class BCGMSSPublicKey implements CipherParameters, PublicKey {
  private static final long serialVersionUID = 1L;
  
  private byte[] publicKeyBytes;
  
  private GMSSParameters gmssParameterSet;
  
  private GMSSParameters gmssParams;
  
  public BCGMSSPublicKey(byte[] paramArrayOfbyte, GMSSParameters paramGMSSParameters) {
    this.gmssParameterSet = paramGMSSParameters;
    this.publicKeyBytes = paramArrayOfbyte;
  }
  
  public BCGMSSPublicKey(GMSSPublicKeyParameters paramGMSSPublicKeyParameters) {
    this(paramGMSSPublicKeyParameters.getPublicKey(), paramGMSSPublicKeyParameters.getParameters());
  }
  
  public String getAlgorithm() {
    return "GMSS";
  }
  
  public byte[] getPublicKeyBytes() {
    return this.publicKeyBytes;
  }
  
  public GMSSParameters getParameterSet() {
    return this.gmssParameterSet;
  }
  
  public String toString() {
    String str = "GMSS public key : " + new String(Hex.encode(this.publicKeyBytes)) + "\n" + "Height of Trees: \n";
    for (byte b = 0; b < (this.gmssParameterSet.getHeightOfTrees()).length; b++)
      str = str + "Layer " + b + " : " + this.gmssParameterSet.getHeightOfTrees()[b] + " WinternitzParameter: " + this.gmssParameterSet.getWinternitzParameter()[b] + " K: " + this.gmssParameterSet.getK()[b] + "\n"; 
    return str;
  }
  
  public byte[] getEncoded() {
    return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.gmss, (ASN1Encodable)(new ParSet(this.gmssParameterSet.getNumOfLayers(), this.gmssParameterSet.getHeightOfTrees(), this.gmssParameterSet.getWinternitzParameter(), this.gmssParameterSet.getK())).toASN1Primitive()), (ASN1Encodable)new GMSSPublicKey(this.publicKeyBytes));
  }
  
  public String getFormat() {
    return "X.509";
  }
}
