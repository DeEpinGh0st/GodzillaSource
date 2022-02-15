package org.bouncycastle.jce.spec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jce.interfaces.MQVPrivateKey;

public class MQVPrivateKeySpec implements KeySpec, MQVPrivateKey {
  private PrivateKey staticPrivateKey;
  
  private PrivateKey ephemeralPrivateKey;
  
  private PublicKey ephemeralPublicKey;
  
  public MQVPrivateKeySpec(PrivateKey paramPrivateKey1, PrivateKey paramPrivateKey2) {
    this(paramPrivateKey1, paramPrivateKey2, null);
  }
  
  public MQVPrivateKeySpec(PrivateKey paramPrivateKey1, PrivateKey paramPrivateKey2, PublicKey paramPublicKey) {
    this.staticPrivateKey = paramPrivateKey1;
    this.ephemeralPrivateKey = paramPrivateKey2;
    this.ephemeralPublicKey = paramPublicKey;
  }
  
  public PrivateKey getStaticPrivateKey() {
    return this.staticPrivateKey;
  }
  
  public PrivateKey getEphemeralPrivateKey() {
    return this.ephemeralPrivateKey;
  }
  
  public PublicKey getEphemeralPublicKey() {
    return this.ephemeralPublicKey;
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
