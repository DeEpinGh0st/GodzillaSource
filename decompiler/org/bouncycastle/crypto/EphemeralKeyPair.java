package org.bouncycastle.crypto;

public class EphemeralKeyPair {
  private AsymmetricCipherKeyPair keyPair;
  
  private KeyEncoder publicKeyEncoder;
  
  public EphemeralKeyPair(AsymmetricCipherKeyPair paramAsymmetricCipherKeyPair, KeyEncoder paramKeyEncoder) {
    this.keyPair = paramAsymmetricCipherKeyPair;
    this.publicKeyEncoder = paramKeyEncoder;
  }
  
  public AsymmetricCipherKeyPair getKeyPair() {
    return this.keyPair;
  }
  
  public byte[] getEncodedPublicKey() {
    return this.publicKeyEncoder.getEncoded(this.keyPair.getPublic());
  }
}
