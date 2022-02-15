package org.bouncycastle.pqc.crypto.sphincs;

class Tree {
  static void l_tree(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, int paramInt3) {
    int i = 67;
    byte b2 = 0;
    for (byte b1 = 0; b1 < 7; b1++) {
      for (b2 = 0; b2 < i >>> 1; b2++)
        paramHashFunctions.hash_2n_n_mask(paramArrayOfbyte2, paramInt2 + b2 * 32, paramArrayOfbyte2, paramInt2 + b2 * 2 * 32, paramArrayOfbyte3, paramInt3 + b1 * 2 * 32); 
      if ((i & 0x1) != 0) {
        System.arraycopy(paramArrayOfbyte2, paramInt2 + (i - 1) * 32, paramArrayOfbyte2, paramInt2 + (i >>> 1) * 32, 32);
        i = (i >>> 1) + 1;
      } else {
        i >>>= 1;
      } 
    } 
    System.arraycopy(paramArrayOfbyte2, paramInt2, paramArrayOfbyte1, paramInt1, 32);
  }
  
  static void treehash(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, leafaddr paramleafaddr, byte[] paramArrayOfbyte3, int paramInt3) {
    leafaddr leafaddr1 = new leafaddr(paramleafaddr);
    byte[] arrayOfByte = new byte[(paramInt2 + 1) * 32];
    int[] arrayOfInt = new int[paramInt2 + 1];
    byte b2 = 0;
    int i = (int)(leafaddr1.subleaf + (1 << paramInt2));
    while (leafaddr1.subleaf < i) {
      gen_leaf_wots(paramHashFunctions, arrayOfByte, b2 * 32, paramArrayOfbyte3, paramInt3, paramArrayOfbyte2, leafaddr1);
      arrayOfInt[b2] = 0;
      while (++b2 > 1 && arrayOfInt[b2 - 1] == arrayOfInt[b2 - 2]) {
        int j = 2 * (arrayOfInt[b2 - 1] + 7) * 32;
        paramHashFunctions.hash_2n_n_mask(arrayOfByte, (b2 - 2) * 32, arrayOfByte, (b2 - 2) * 32, paramArrayOfbyte3, paramInt3 + j);
        arrayOfInt[b2 - 2] = arrayOfInt[b2 - 2] + 1;
        b2--;
      } 
      leafaddr1.subleaf++;
    } 
    for (byte b1 = 0; b1 < 32; b1++)
      paramArrayOfbyte1[paramInt1 + b1] = arrayOfByte[b1]; 
  }
  
  static void gen_leaf_wots(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, byte[] paramArrayOfbyte3, leafaddr paramleafaddr) {
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = new byte[2144];
    Wots wots = new Wots();
    Seed.get_seed(paramHashFunctions, arrayOfByte1, 0, paramArrayOfbyte3, paramleafaddr);
    wots.wots_pkgen(paramHashFunctions, arrayOfByte2, 0, arrayOfByte1, 0, paramArrayOfbyte2, paramInt2);
    l_tree(paramHashFunctions, paramArrayOfbyte1, paramInt1, arrayOfByte2, 0, paramArrayOfbyte2, paramInt2);
  }
  
  static class leafaddr {
    int level;
    
    long subtree;
    
    long subleaf;
    
    public leafaddr() {}
    
    public leafaddr(leafaddr param1leafaddr) {
      this.level = param1leafaddr.level;
      this.subtree = param1leafaddr.subtree;
      this.subleaf = param1leafaddr.subleaf;
    }
  }
}
