package org.bouncycastle.est;

public interface ESTClientProvider {
  ESTClient makeClient() throws ESTException;
  
  boolean isTrusted();
}
