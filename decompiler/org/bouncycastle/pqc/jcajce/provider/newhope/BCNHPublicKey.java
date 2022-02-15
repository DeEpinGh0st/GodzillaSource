package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.NHPublicKey;
import org.bouncycastle.util.Arrays;

public class BCNHPublicKey implements NHPublicKey {
  private static final long serialVersionUID = 1L;
  
  private final NHPublicKeyParameters params;
  
  public BCNHPublicKey(NHPublicKeyParameters paramNHPublicKeyParameters) {
    this.params = paramNHPublicKeyParameters;
  }
  
  public BCNHPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.params = new NHPublicKeyParameters(paramSubjectPublicKeyInfo.getPublicKeyData().getBytes());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof BCNHPublicKey))
      return false; 
    BCNHPublicKey bCNHPublicKey = (BCNHPublicKey)paramObject;
    return Arrays.areEqual(this.params.getPubData(), bCNHPublicKey.params.getPubData());
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.params.getPubData());
  }
  
  public final String getAlgorithm() {
    return "NH";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
      SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, this.params.getPubData());
      return subjectPublicKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getPublicData() {
    return this.params.getPubData();
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.params;
  }
}
