package org.bouncycastle.jce.interfaces;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface MQVPrivateKey extends PrivateKey {
  PrivateKey getStaticPrivateKey();
  
  PrivateKey getEphemeralPrivateKey();
  
  PublicKey getEphemeralPublicKey();
}
