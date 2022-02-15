package org.bouncycastle.crypto;

public interface Committer {
  Commitment commit(byte[] paramArrayOfbyte);
  
  boolean isRevealed(Commitment paramCommitment, byte[] paramArrayOfbyte);
}
