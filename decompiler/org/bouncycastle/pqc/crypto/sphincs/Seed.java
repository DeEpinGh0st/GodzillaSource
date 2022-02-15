package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

class Seed {
  static void get_seed(HashFunctions paramHashFunctions, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2, Tree.leafaddr paramleafaddr) {
    byte[] arrayOfByte = new byte[40];
    for (byte b = 0; b < 32; b++)
      arrayOfByte[b] = paramArrayOfbyte2[b]; 
    long l = paramleafaddr.level;
    l |= paramleafaddr.subtree << 4L;
    l |= paramleafaddr.subleaf << 59L;
    Pack.longToLittleEndian(l, arrayOfByte, 32);
    paramHashFunctions.varlen_hash(paramArrayOfbyte1, paramInt, arrayOfByte, arrayOfByte.length);
  }
  
  static void prg(byte[] paramArrayOfbyte1, int paramInt1, long paramLong, byte[] paramArrayOfbyte2, int paramInt2) {
    byte[] arrayOfByte = new byte[8];
    ChaChaEngine chaChaEngine = new ChaChaEngine(12);
    chaChaEngine.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(paramArrayOfbyte2, paramInt2, 32), arrayOfByte));
    chaChaEngine.processBytes(paramArrayOfbyte1, paramInt1, (int)paramLong, paramArrayOfbyte1, paramInt1);
  }
}
