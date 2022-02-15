package org.bouncycastle.util;

import org.bouncycastle.crypto.digests.SHA512tDigest;

public class Fingerprint {
  private static char[] encodingTable = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  private final byte[] fingerprint;
  
  public Fingerprint(byte[] paramArrayOfbyte) {
    this.fingerprint = calculateFingerprint(paramArrayOfbyte);
  }
  
  public byte[] getFingerprint() {
    return Arrays.clone(this.fingerprint);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b != this.fingerprint.length; b++) {
      if (b)
        stringBuffer.append(":"); 
      stringBuffer.append(encodingTable[this.fingerprint[b] >>> 4 & 0xF]);
      stringBuffer.append(encodingTable[this.fingerprint[b] & 0xF]);
    } 
    return stringBuffer.toString();
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == this) ? true : ((paramObject instanceof Fingerprint) ? Arrays.areEqual(((Fingerprint)paramObject).fingerprint, this.fingerprint) : false);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.fingerprint);
  }
  
  public static byte[] calculateFingerprint(byte[] paramArrayOfbyte) {
    SHA512tDigest sHA512tDigest = new SHA512tDigest(160);
    sHA512tDigest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    byte[] arrayOfByte = new byte[sHA512tDigest.getDigestSize()];
    sHA512tDigest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
}
