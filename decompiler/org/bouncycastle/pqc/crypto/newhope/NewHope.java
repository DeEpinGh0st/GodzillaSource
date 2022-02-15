package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;

class NewHope {
  private static final boolean STATISTICAL_TEST = false;
  
  public static final int AGREEMENT_SIZE = 32;
  
  public static final int POLY_SIZE = 1024;
  
  public static final int SENDA_BYTES = 1824;
  
  public static final int SENDB_BYTES = 2048;
  
  public static void keygen(SecureRandom paramSecureRandom, byte[] paramArrayOfbyte, short[] paramArrayOfshort) {
    byte[] arrayOfByte1 = new byte[32];
    paramSecureRandom.nextBytes(arrayOfByte1);
    short[] arrayOfShort1 = new short[1024];
    generateA(arrayOfShort1, arrayOfByte1);
    byte[] arrayOfByte2 = new byte[32];
    paramSecureRandom.nextBytes(arrayOfByte2);
    Poly.getNoise(paramArrayOfshort, arrayOfByte2, (byte)0);
    Poly.toNTT(paramArrayOfshort);
    short[] arrayOfShort2 = new short[1024];
    Poly.getNoise(arrayOfShort2, arrayOfByte2, (byte)1);
    Poly.toNTT(arrayOfShort2);
    short[] arrayOfShort3 = new short[1024];
    Poly.pointWise(arrayOfShort1, paramArrayOfshort, arrayOfShort3);
    short[] arrayOfShort4 = new short[1024];
    Poly.add(arrayOfShort3, arrayOfShort2, arrayOfShort4);
    encodeA(paramArrayOfbyte, arrayOfShort4, arrayOfByte1);
  }
  
  public static void sharedB(SecureRandom paramSecureRandom, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    short[] arrayOfShort1 = new short[1024];
    byte[] arrayOfByte1 = new byte[32];
    decodeA(arrayOfShort1, arrayOfByte1, paramArrayOfbyte3);
    short[] arrayOfShort2 = new short[1024];
    generateA(arrayOfShort2, arrayOfByte1);
    byte[] arrayOfByte2 = new byte[32];
    paramSecureRandom.nextBytes(arrayOfByte2);
    short[] arrayOfShort3 = new short[1024];
    Poly.getNoise(arrayOfShort3, arrayOfByte2, (byte)0);
    Poly.toNTT(arrayOfShort3);
    short[] arrayOfShort4 = new short[1024];
    Poly.getNoise(arrayOfShort4, arrayOfByte2, (byte)1);
    Poly.toNTT(arrayOfShort4);
    short[] arrayOfShort5 = new short[1024];
    Poly.pointWise(arrayOfShort2, arrayOfShort3, arrayOfShort5);
    Poly.add(arrayOfShort5, arrayOfShort4, arrayOfShort5);
    short[] arrayOfShort6 = new short[1024];
    Poly.pointWise(arrayOfShort1, arrayOfShort3, arrayOfShort6);
    Poly.fromNTT(arrayOfShort6);
    short[] arrayOfShort7 = new short[1024];
    Poly.getNoise(arrayOfShort7, arrayOfByte2, (byte)2);
    Poly.add(arrayOfShort6, arrayOfShort7, arrayOfShort6);
    short[] arrayOfShort8 = new short[1024];
    ErrorCorrection.helpRec(arrayOfShort8, arrayOfShort6, arrayOfByte2, (byte)3);
    encodeB(paramArrayOfbyte2, arrayOfShort5, arrayOfShort8);
    ErrorCorrection.rec(paramArrayOfbyte1, arrayOfShort6, arrayOfShort8);
    sha3(paramArrayOfbyte1);
  }
  
  public static void sharedA(byte[] paramArrayOfbyte1, short[] paramArrayOfshort, byte[] paramArrayOfbyte2) {
    short[] arrayOfShort1 = new short[1024];
    short[] arrayOfShort2 = new short[1024];
    decodeB(arrayOfShort1, arrayOfShort2, paramArrayOfbyte2);
    short[] arrayOfShort3 = new short[1024];
    Poly.pointWise(paramArrayOfshort, arrayOfShort1, arrayOfShort3);
    Poly.fromNTT(arrayOfShort3);
    ErrorCorrection.rec(paramArrayOfbyte1, arrayOfShort3, arrayOfShort2);
    sha3(paramArrayOfbyte1);
  }
  
  static void decodeA(short[] paramArrayOfshort, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    Poly.fromBytes(paramArrayOfshort, paramArrayOfbyte2);
    System.arraycopy(paramArrayOfbyte2, 1792, paramArrayOfbyte1, 0, 32);
  }
  
  static void decodeB(short[] paramArrayOfshort1, short[] paramArrayOfshort2, byte[] paramArrayOfbyte) {
    Poly.fromBytes(paramArrayOfshort1, paramArrayOfbyte);
    for (byte b = 0; b < 'Ā'; b++) {
      int i = 4 * b;
      int j = paramArrayOfbyte[1792 + b] & 0xFF;
      paramArrayOfshort2[i + 0] = (short)(j & 0x3);
      paramArrayOfshort2[i + 1] = (short)(j >>> 2 & 0x3);
      paramArrayOfshort2[i + 2] = (short)(j >>> 4 & 0x3);
      paramArrayOfshort2[i + 3] = (short)(j >>> 6);
    } 
  }
  
  static void encodeA(byte[] paramArrayOfbyte1, short[] paramArrayOfshort, byte[] paramArrayOfbyte2) {
    Poly.toBytes(paramArrayOfbyte1, paramArrayOfshort);
    System.arraycopy(paramArrayOfbyte2, 0, paramArrayOfbyte1, 1792, 32);
  }
  
  static void encodeB(byte[] paramArrayOfbyte, short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    Poly.toBytes(paramArrayOfbyte, paramArrayOfshort1);
    for (byte b = 0; b < 'Ā'; b++) {
      int i = 4 * b;
      paramArrayOfbyte[1792 + b] = (byte)(paramArrayOfshort2[i] | paramArrayOfshort2[i + 1] << 2 | paramArrayOfshort2[i + 2] << 4 | paramArrayOfshort2[i + 3] << 6);
    } 
  }
  
  static void generateA(short[] paramArrayOfshort, byte[] paramArrayOfbyte) {
    Poly.uniform(paramArrayOfshort, paramArrayOfbyte);
  }
  
  static void sha3(byte[] paramArrayOfbyte) {
    SHA3Digest sHA3Digest = new SHA3Digest(256);
    sHA3Digest.update(paramArrayOfbyte, 0, 32);
    sHA3Digest.doFinal(paramArrayOfbyte, 0);
  }
}
