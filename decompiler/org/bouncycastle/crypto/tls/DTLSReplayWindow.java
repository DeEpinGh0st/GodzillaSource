package org.bouncycastle.crypto.tls;

class DTLSReplayWindow {
  private static final long VALID_SEQ_MASK = 281474976710655L;
  
  private static final long WINDOW_SIZE = 64L;
  
  private long latestConfirmedSeq = -1L;
  
  private long bitmap = 0L;
  
  boolean shouldDiscard(long paramLong) {
    if ((paramLong & 0xFFFFFFFFFFFFL) != paramLong)
      return true; 
    if (paramLong <= this.latestConfirmedSeq) {
      long l = this.latestConfirmedSeq - paramLong;
      if (l >= 64L)
        return true; 
      if ((this.bitmap & 1L << (int)l) != 0L)
        return true; 
    } 
    return false;
  }
  
  void reportAuthenticated(long paramLong) {
    if ((paramLong & 0xFFFFFFFFFFFFL) != paramLong)
      throw new IllegalArgumentException("'seq' out of range"); 
    if (paramLong <= this.latestConfirmedSeq) {
      long l = this.latestConfirmedSeq - paramLong;
      if (l < 64L)
        this.bitmap |= 1L << (int)l; 
    } else {
      long l = paramLong - this.latestConfirmedSeq;
      if (l >= 64L) {
        this.bitmap = 1L;
      } else {
        this.bitmap <<= (int)l;
        this.bitmap |= 0x1L;
      } 
      this.latestConfirmedSeq = paramLong;
    } 
  }
  
  void reset() {
    this.latestConfirmedSeq = -1L;
    this.bitmap = 0L;
  }
}
