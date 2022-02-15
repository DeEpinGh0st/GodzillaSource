package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.Digest;

public class DigestRandomGenerator implements RandomGenerator {
  private static long CYCLE_COUNT = 10L;
  
  private long stateCounter;
  
  private long seedCounter;
  
  private Digest digest;
  
  private byte[] state;
  
  private byte[] seed;
  
  public DigestRandomGenerator(Digest paramDigest) {
    this.digest = paramDigest;
    this.seed = new byte[paramDigest.getDigestSize()];
    this.seedCounter = 1L;
    this.state = new byte[paramDigest.getDigestSize()];
    this.stateCounter = 1L;
  }
  
  public void addSeedMaterial(byte[] paramArrayOfbyte) {
    synchronized (this) {
      digestUpdate(paramArrayOfbyte);
      digestUpdate(this.seed);
      digestDoFinal(this.seed);
    } 
  }
  
  public void addSeedMaterial(long paramLong) {
    synchronized (this) {
      digestAddCounter(paramLong);
      digestUpdate(this.seed);
      digestDoFinal(this.seed);
    } 
  }
  
  public void nextBytes(byte[] paramArrayOfbyte) {
    nextBytes(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void nextBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    synchronized (this) {
      byte b = 0;
      generateState();
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j != i; j++) {
        if (b == this.state.length) {
          generateState();
          b = 0;
        } 
        paramArrayOfbyte[j] = this.state[b++];
      } 
    } 
  }
  
  private void cycleSeed() {
    digestUpdate(this.seed);
    digestAddCounter(this.seedCounter++);
    digestDoFinal(this.seed);
  }
  
  private void generateState() {
    digestAddCounter(this.stateCounter++);
    digestUpdate(this.state);
    digestUpdate(this.seed);
    digestDoFinal(this.state);
    if (this.stateCounter % CYCLE_COUNT == 0L)
      cycleSeed(); 
  }
  
  private void digestAddCounter(long paramLong) {
    for (byte b = 0; b != 8; b++) {
      this.digest.update((byte)(int)paramLong);
      paramLong >>>= 8L;
    } 
  }
  
  private void digestUpdate(byte[] paramArrayOfbyte) {
    this.digest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  private void digestDoFinal(byte[] paramArrayOfbyte) {
    this.digest.doFinal(paramArrayOfbyte, 0);
  }
}
