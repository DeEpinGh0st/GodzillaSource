package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Pack;

public class SPHINCS256Signer implements MessageSigner {
  private final HashFunctions hashFunctions;
  
  private byte[] keyData;
  
  public SPHINCS256Signer(Digest paramDigest1, Digest paramDigest2) {
    if (paramDigest1.getDigestSize() != 32)
      throw new IllegalArgumentException("n-digest needs to produce 32 bytes of output"); 
    if (paramDigest2.getDigestSize() != 64)
      throw new IllegalArgumentException("2n-digest needs to produce 64 bytes of output"); 
    this.hashFunctions = new HashFunctions(paramDigest1, paramDigest2);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      this.keyData = ((SPHINCSPrivateKeyParameters)paramCipherParameters).getKeyData();
    } else {
      this.keyData = ((SPHINCSPublicKeyParameters)paramCipherParameters).getKeyData();
    } 
  }
  
  public byte[] generateSignature(byte[] paramArrayOfbyte) {
    return crypto_sign(this.hashFunctions, paramArrayOfbyte, this.keyData);
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return verify(this.hashFunctions, paramArrayOfbyte1, paramArrayOfbyte2, this.keyData);
  }
  
  static void validate_authpath(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, byte[] paramArrayOfbyte3, int paramInt2, byte[] paramArrayOfbyte4, int paramInt3) {
    byte[] arrayOfByte = new byte[64];
    if ((paramInt1 & 0x1) != 0) {
      byte b1;
      for (b1 = 0; b1 < 32; b1++)
        arrayOfByte[32 + b1] = paramArrayOfbyte2[b1]; 
      for (b1 = 0; b1 < 32; b1++)
        arrayOfByte[b1] = paramArrayOfbyte3[paramInt2 + b1]; 
    } else {
      byte b1;
      for (b1 = 0; b1 < 32; b1++)
        arrayOfByte[b1] = paramArrayOfbyte2[b1]; 
      for (b1 = 0; b1 < 32; b1++)
        arrayOfByte[32 + b1] = paramArrayOfbyte3[paramInt2 + b1]; 
    } 
    int i = paramInt2 + 32;
    for (byte b = 0; b < paramInt3 - 1; b++) {
      paramInt1 >>>= 1;
      if ((paramInt1 & 0x1) != 0) {
        paramHashFunctions.hash_2n_n_mask(arrayOfByte, 32, arrayOfByte, 0, paramArrayOfbyte4, 2 * (7 + b) * 32);
        for (byte b1 = 0; b1 < 32; b1++)
          arrayOfByte[b1] = paramArrayOfbyte3[i + b1]; 
      } else {
        paramHashFunctions.hash_2n_n_mask(arrayOfByte, 0, arrayOfByte, 0, paramArrayOfbyte4, 2 * (7 + b) * 32);
        for (byte b1 = 0; b1 < 32; b1++)
          arrayOfByte[b1 + 32] = paramArrayOfbyte3[i + b1]; 
      } 
      i += 32;
    } 
    paramHashFunctions.hash_2n_n_mask(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte4, 2 * (7 + paramInt3 - 1) * 32);
  }
  
  static void compute_authpath_wots(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, Tree.leafaddr paramleafaddr, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, int paramInt2) {
    Tree.leafaddr leafaddr1 = new Tree.leafaddr(paramleafaddr);
    byte[] arrayOfByte1 = new byte[2048];
    byte[] arrayOfByte2 = new byte[1024];
    byte[] arrayOfByte3 = new byte[68608];
    leafaddr1.subleaf = 0L;
    while (leafaddr1.subleaf < 32L) {
      Seed.get_seed(paramHashFunctions, arrayOfByte2, (int)(leafaddr1.subleaf * 32L), paramArrayOfbyte3, leafaddr1);
      leafaddr1.subleaf++;
    } 
    Wots wots = new Wots();
    leafaddr1.subleaf = 0L;
    while (leafaddr1.subleaf < 32L) {
      wots.wots_pkgen(paramHashFunctions, arrayOfByte3, (int)(leafaddr1.subleaf * 67L * 32L), arrayOfByte2, (int)(leafaddr1.subleaf * 32L), paramArrayOfbyte4, 0);
      leafaddr1.subleaf++;
    } 
    leafaddr1.subleaf = 0L;
    while (leafaddr1.subleaf < 32L) {
      Tree.l_tree(paramHashFunctions, arrayOfByte1, (int)(1024L + leafaddr1.subleaf * 32L), arrayOfByte3, (int)(leafaddr1.subleaf * 67L * 32L), paramArrayOfbyte4, 0);
      leafaddr1.subleaf++;
    } 
    byte b = 0;
    int i;
    for (i = 32; i > 0; i >>>= 1) {
      for (byte b1 = 0; b1 < i; b1 += 2)
        paramHashFunctions.hash_2n_n_mask(arrayOfByte1, (i >>> 1) * 32 + (b1 >>> 1) * 32, arrayOfByte1, i * 32 + b1 * 32, paramArrayOfbyte4, 2 * (7 + b) * 32); 
      b++;
    } 
    int j = (int)paramleafaddr.subleaf;
    for (i = 0; i < paramInt2; i++)
      System.arraycopy(arrayOfByte1, (32 >>> i) * 32 + (j >>> i ^ 0x1) * 32, paramArrayOfbyte2, paramInt1 + i * 32, 32); 
    System.arraycopy(arrayOfByte1, 32, paramArrayOfbyte1, 0, 32);
  }
  
  byte[] crypto_sign(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte1 = new byte[41000];
    byte[] arrayOfByte2 = new byte[32];
    byte[] arrayOfByte3 = new byte[64];
    long[] arrayOfLong = new long[8];
    byte[] arrayOfByte4 = new byte[32];
    byte[] arrayOfByte5 = new byte[32];
    byte[] arrayOfByte6 = new byte[1024];
    byte[] arrayOfByte7 = new byte[1088];
    byte b1;
    for (b1 = 0; b1 < 'р'; b1++)
      arrayOfByte7[b1] = paramArrayOfbyte2[b1]; 
    char c = 'ꀈ';
    System.arraycopy(arrayOfByte7, 1056, arrayOfByte1, c, 32);
    Digest digest = paramHashFunctions.getMessageHash();
    byte[] arrayOfByte8 = new byte[digest.getDigestSize()];
    digest.update(arrayOfByte1, c, 32);
    digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    digest.doFinal(arrayOfByte8, 0);
    zerobytes(arrayOfByte1, c, 32);
    for (byte b2 = 0; b2 != arrayOfLong.length; b2++)
      arrayOfLong[b2] = Pack.littleEndianToLong(arrayOfByte8, b2 * 8); 
    long l = arrayOfLong[0] & 0xFFFFFFFFFFFFFFFL;
    System.arraycopy(arrayOfByte8, 16, arrayOfByte2, 0, 32);
    c = '鯨';
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, c, 32);
    Tree.leafaddr leafaddr2 = new Tree.leafaddr();
    leafaddr2.level = 11;
    leafaddr2.subtree = 0L;
    leafaddr2.subleaf = 0L;
    int i = c + 32;
    System.arraycopy(arrayOfByte7, 32, arrayOfByte1, i, 1024);
    Tree.treehash(paramHashFunctions, arrayOfByte1, i + 1024, 5, arrayOfByte7, leafaddr2, arrayOfByte1, i);
    digest = paramHashFunctions.getMessageHash();
    digest.update(arrayOfByte1, c, 1088);
    digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    digest.doFinal(arrayOfByte3, 0);
    Tree.leafaddr leafaddr1 = new Tree.leafaddr();
    leafaddr1.level = 12;
    leafaddr1.subleaf = (int)(l & 0x1FL);
    leafaddr1.subtree = l >>> 5L;
    for (b1 = 0; b1 < 32; b1++)
      arrayOfByte1[b1] = arrayOfByte2[b1]; 
    int j = 32;
    System.arraycopy(arrayOfByte7, 32, arrayOfByte6, 0, 1024);
    for (b1 = 0; b1 < 8; b1++)
      arrayOfByte1[j + b1] = (byte)(int)(l >>> 8 * b1 & 0xFFL); 
    j += 8;
    Seed.get_seed(paramHashFunctions, arrayOfByte5, 0, arrayOfByte7, leafaddr1);
    Horst horst = new Horst();
    int k = Horst.horst_sign(paramHashFunctions, arrayOfByte1, j, arrayOfByte4, arrayOfByte5, arrayOfByte6, arrayOfByte3);
    j += k;
    Wots wots = new Wots();
    for (b1 = 0; b1 < 12; b1++) {
      leafaddr1.level = b1;
      Seed.get_seed(paramHashFunctions, arrayOfByte5, 0, arrayOfByte7, leafaddr1);
      wots.wots_sign(paramHashFunctions, arrayOfByte1, j, arrayOfByte4, arrayOfByte5, arrayOfByte6);
      j += 2144;
      compute_authpath_wots(paramHashFunctions, arrayOfByte4, arrayOfByte1, j, leafaddr1, arrayOfByte7, arrayOfByte6, 5);
      j += 160;
      leafaddr1.subleaf = (int)(leafaddr1.subtree & 0x1FL);
      leafaddr1.subtree >>>= 5L;
    } 
    zerobytes(arrayOfByte7, 0, 1088);
    return arrayOfByte1;
  }
  
  private void zerobytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    for (int i = 0; i != paramInt2; i++)
      paramArrayOfbyte[paramInt1 + i] = 0; 
  }
  
  boolean verify(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    int i = paramArrayOfbyte2.length;
    long l = 0L;
    byte[] arrayOfByte1 = new byte[2144];
    byte[] arrayOfByte2 = new byte[32];
    byte[] arrayOfByte3 = new byte[32];
    byte[] arrayOfByte4 = new byte[41000];
    byte[] arrayOfByte5 = new byte[1056];
    if (i != 41000)
      throw new IllegalArgumentException("signature wrong size"); 
    byte[] arrayOfByte6 = new byte[64];
    byte b1;
    for (b1 = 0; b1 < 'Р'; b1++)
      arrayOfByte5[b1] = paramArrayOfbyte3[b1]; 
    byte[] arrayOfByte7 = new byte[32];
    for (b1 = 0; b1 < 32; b1++)
      arrayOfByte7[b1] = paramArrayOfbyte2[b1]; 
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte4, 0, 41000);
    Digest digest = paramHashFunctions.getMessageHash();
    digest.update(arrayOfByte7, 0, 32);
    digest.update(arrayOfByte5, 0, 1056);
    digest.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    digest.doFinal(arrayOfByte6, 0);
    byte b2 = 0;
    b2 += true;
    i -= 32;
    for (b1 = 0; b1 < 8; b1++)
      l ^= (arrayOfByte4[b2 + b1] & 0xFF) << 8 * b1; 
    new Horst();
    Horst.horst_verify(paramHashFunctions, arrayOfByte3, arrayOfByte4, b2 + 8, arrayOfByte5, arrayOfByte6);
    b2 += 8;
    i -= 8;
    b2 += 13312;
    i -= 13312;
    Wots wots = new Wots();
    for (b1 = 0; b1 < 12; b1++) {
      wots.wots_verify(paramHashFunctions, arrayOfByte1, arrayOfByte4, b2, arrayOfByte3, arrayOfByte5);
      b2 += 2144;
      i -= 2144;
      Tree.l_tree(paramHashFunctions, arrayOfByte2, 0, arrayOfByte1, 0, arrayOfByte5, 0);
      validate_authpath(paramHashFunctions, arrayOfByte3, arrayOfByte2, (int)(l & 0x1FL), arrayOfByte4, b2, arrayOfByte5, 5);
      l >>= 5L;
      b2 += 160;
      i -= 160;
    } 
    boolean bool = true;
    for (b1 = 0; b1 < 32; b1++) {
      if (arrayOfByte3[b1] != arrayOfByte5[b1 + 1024])
        bool = false; 
    } 
    return bool;
  }
}
