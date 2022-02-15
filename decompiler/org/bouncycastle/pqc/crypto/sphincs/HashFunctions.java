package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Strings;

class HashFunctions {
  private static final byte[] hashc = Strings.toByteArray("expand 32-byte to 64-byte state!");
  
  private final Digest dig256;
  
  private final Digest dig512;
  
  private final Permute perm = new Permute();
  
  HashFunctions(Digest paramDigest) {
    this(paramDigest, null);
  }
  
  HashFunctions(Digest paramDigest1, Digest paramDigest2) {
    this.dig256 = paramDigest1;
    this.dig512 = paramDigest2;
  }
  
  int varlen_hash(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    this.dig256.update(paramArrayOfbyte2, 0, paramInt2);
    this.dig256.doFinal(paramArrayOfbyte1, paramInt1);
    return 0;
  }
  
  Digest getMessageHash() {
    return this.dig512;
  }
  
  int hash_2n_n(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    byte[] arrayOfByte = new byte[64];
    byte b;
    for (b = 0; b < 32; b++) {
      arrayOfByte[b] = paramArrayOfbyte2[paramInt2 + b];
      arrayOfByte[b + 32] = hashc[b];
    } 
    this.perm.chacha_permute(arrayOfByte, arrayOfByte);
    for (b = 0; b < 32; b++)
      arrayOfByte[b] = (byte)(arrayOfByte[b] ^ paramArrayOfbyte2[paramInt2 + b + 32]); 
    this.perm.chacha_permute(arrayOfByte, arrayOfByte);
    for (b = 0; b < 32; b++)
      paramArrayOfbyte1[paramInt1 + b] = arrayOfByte[b]; 
    return 0;
  }
  
  int hash_2n_n_mask(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, int paramInt3) {
    byte[] arrayOfByte = new byte[64];
    for (byte b = 0; b < 64; b++)
      arrayOfByte[b] = (byte)(paramArrayOfbyte2[paramInt2 + b] ^ paramArrayOfbyte3[paramInt3 + b]); 
    return hash_2n_n(paramArrayOfbyte1, paramInt1, arrayOfByte, 0);
  }
  
  int hash_n_n(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    byte[] arrayOfByte = new byte[64];
    byte b;
    for (b = 0; b < 32; b++) {
      arrayOfByte[b] = paramArrayOfbyte2[paramInt2 + b];
      arrayOfByte[b + 32] = hashc[b];
    } 
    this.perm.chacha_permute(arrayOfByte, arrayOfByte);
    for (b = 0; b < 32; b++)
      paramArrayOfbyte1[paramInt1 + b] = arrayOfByte[b]; 
    return 0;
  }
  
  int hash_n_n_mask(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, int paramInt3) {
    byte[] arrayOfByte = new byte[32];
    for (byte b = 0; b < 32; b++)
      arrayOfByte[b] = (byte)(paramArrayOfbyte2[paramInt2 + b] ^ paramArrayOfbyte3[paramInt3 + b]); 
    return hash_n_n(paramArrayOfbyte1, paramInt1, arrayOfByte, 0);
  }
}
