package org.bouncycastle.crypto.tls;

class DTLSEpoch {
  private final DTLSReplayWindow replayWindow = new DTLSReplayWindow();
  
  private final int epoch;
  
  private final TlsCipher cipher;
  
  private long sequenceNumber = 0L;
  
  DTLSEpoch(int paramInt, TlsCipher paramTlsCipher) {
    if (paramInt < 0)
      throw new IllegalArgumentException("'epoch' must be >= 0"); 
    if (paramTlsCipher == null)
      throw new IllegalArgumentException("'cipher' cannot be null"); 
    this.epoch = paramInt;
    this.cipher = paramTlsCipher;
  }
  
  long allocateSequenceNumber() {
    return this.sequenceNumber++;
  }
  
  TlsCipher getCipher() {
    return this.cipher;
  }
  
  int getEpoch() {
    return this.epoch;
  }
  
  DTLSReplayWindow getReplayWindow() {
    return this.replayWindow;
  }
  
  long getSequenceNumber() {
    return this.sequenceNumber;
  }
}
