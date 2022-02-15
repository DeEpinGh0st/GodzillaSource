package org.bouncycastle.jcajce.spec;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class MQVParameterSpec implements AlgorithmParameterSpec {
  private final PublicKey ephemeralPublicKey;
  
  private final PrivateKey ephemeralPrivateKey;
  
  private final PublicKey otherPartyEphemeralKey;
  
  private final byte[] userKeyingMaterial;
  
  public MQVParameterSpec(PublicKey paramPublicKey1, PrivateKey paramPrivateKey, PublicKey paramPublicKey2, byte[] paramArrayOfbyte) {
    this.ephemeralPublicKey = paramPublicKey1;
    this.ephemeralPrivateKey = paramPrivateKey;
    this.otherPartyEphemeralKey = paramPublicKey2;
    this.userKeyingMaterial = Arrays.clone(paramArrayOfbyte);
  }
  
  public MQVParameterSpec(PublicKey paramPublicKey1, PrivateKey paramPrivateKey, PublicKey paramPublicKey2) {
    this(paramPublicKey1, paramPrivateKey, paramPublicKey2, null);
  }
  
  public MQVParameterSpec(KeyPair paramKeyPair, PublicKey paramPublicKey, byte[] paramArrayOfbyte) {
    this(paramKeyPair.getPublic(), paramKeyPair.getPrivate(), paramPublicKey, paramArrayOfbyte);
  }
  
  public MQVParameterSpec(PrivateKey paramPrivateKey, PublicKey paramPublicKey, byte[] paramArrayOfbyte) {
    this(null, paramPrivateKey, paramPublicKey, paramArrayOfbyte);
  }
  
  public MQVParameterSpec(KeyPair paramKeyPair, PublicKey paramPublicKey) {
    this(paramKeyPair.getPublic(), paramKeyPair.getPrivate(), paramPublicKey, null);
  }
  
  public MQVParameterSpec(PrivateKey paramPrivateKey, PublicKey paramPublicKey) {
    this(null, paramPrivateKey, paramPublicKey, null);
  }
  
  public PrivateKey getEphemeralPrivateKey() {
    return this.ephemeralPrivateKey;
  }
  
  public PublicKey getEphemeralPublicKey() {
    return this.ephemeralPublicKey;
  }
  
  public PublicKey getOtherPartyEphemeralKey() {
    return this.otherPartyEphemeralKey;
  }
  
  public byte[] getUserKeyingMaterial() {
    return Arrays.clone(this.userKeyingMaterial);
  }
}
