package org.bouncycastle.crypto.commitments;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Commitment;
import org.bouncycastle.crypto.Committer;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

public class GeneralHashCommitter implements Committer {
  private final Digest digest;
  
  private final int byteLength;
  
  private final SecureRandom random;
  
  public GeneralHashCommitter(ExtendedDigest paramExtendedDigest, SecureRandom paramSecureRandom) {
    this.digest = (Digest)paramExtendedDigest;
    this.byteLength = paramExtendedDigest.getByteLength();
    this.random = paramSecureRandom;
  }
  
  public Commitment commit(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length > this.byteLength / 2)
      throw new DataLengthException("Message to be committed to too large for digest."); 
    byte[] arrayOfByte = new byte[this.byteLength - paramArrayOfbyte.length];
    this.random.nextBytes(arrayOfByte);
    return new Commitment(arrayOfByte, calculateCommitment(arrayOfByte, paramArrayOfbyte));
  }
  
  public boolean isRevealed(Commitment paramCommitment, byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length + (paramCommitment.getSecret()).length != this.byteLength)
      throw new DataLengthException("Message and witness secret lengths do not match."); 
    byte[] arrayOfByte = calculateCommitment(paramCommitment.getSecret(), paramArrayOfbyte);
    return Arrays.constantTimeAreEqual(paramCommitment.getCommitment(), arrayOfByte);
  }
  
  private byte[] calculateCommitment(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    this.digest.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    this.digest.update((byte)(paramArrayOfbyte2.length >>> 8));
    this.digest.update((byte)paramArrayOfbyte2.length);
    this.digest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
}
