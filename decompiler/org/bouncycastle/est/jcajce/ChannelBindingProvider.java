package org.bouncycastle.est.jcajce;

import java.net.Socket;

public interface ChannelBindingProvider {
  boolean canAccessChannelBinding(Socket paramSocket);
  
  byte[] getChannelBinding(Socket paramSocket, String paramString);
}
