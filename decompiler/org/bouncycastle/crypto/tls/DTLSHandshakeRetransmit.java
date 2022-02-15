package org.bouncycastle.crypto.tls;

import java.io.IOException;

interface DTLSHandshakeRetransmit {
  void receivedHandshakeRecord(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) throws IOException;
}
