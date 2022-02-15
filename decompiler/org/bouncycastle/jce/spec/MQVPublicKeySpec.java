package org.bouncycastle.jce.spec;

import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jce.interfaces.MQVPublicKey;

public class MQVPublicKeySpec implements KeySpec, MQVPublicKey {
  private PublicKey staticKey;
  
  private PublicKey ephemeralKey;
  
  public MQVPublicKeySpec(PublicKey paramPublicKey1, PublicKey paramPublicKey2) {
    this.staticKey = paramPublicKey1;
    this.ephemeralKey = paramPublicKey2;
  }
  
  public PublicKey getStaticKey() {
    return this.staticKey;
  }
  
  public PublicKey getEphemeralKey() {
    return this.ephemeralKey;
  }
  
  public String getAlgorithm() {
    return "ECMQV";
  }
  
  public String getFormat() {
    return null;
  }
  
  public byte[] getEncoded() {
    return null;
  }
}
