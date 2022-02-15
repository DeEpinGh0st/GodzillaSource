package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface DatagramTransport {
  int getReceiveLimit() throws IOException;
  
  int getSendLimit() throws IOException;
  
  int receive(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException;
  
  void send(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  void close() throws IOException;
}
