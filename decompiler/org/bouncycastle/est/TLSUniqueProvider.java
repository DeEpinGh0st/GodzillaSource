package org.bouncycastle.est;

public interface TLSUniqueProvider {
  boolean isTLSUniqueAvailable();
  
  byte[] getTLSUnique();
}
