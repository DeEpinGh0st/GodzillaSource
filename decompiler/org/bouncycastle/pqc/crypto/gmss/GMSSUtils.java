package org.bouncycastle.pqc.crypto.gmss;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

class GMSSUtils {
  static GMSSLeaf[] clone(GMSSLeaf[] paramArrayOfGMSSLeaf) {
    if (paramArrayOfGMSSLeaf == null)
      return null; 
    GMSSLeaf[] arrayOfGMSSLeaf = new GMSSLeaf[paramArrayOfGMSSLeaf.length];
    System.arraycopy(paramArrayOfGMSSLeaf, 0, arrayOfGMSSLeaf, 0, paramArrayOfGMSSLeaf.length);
    return arrayOfGMSSLeaf;
  }
  
  static GMSSRootCalc[] clone(GMSSRootCalc[] paramArrayOfGMSSRootCalc) {
    if (paramArrayOfGMSSRootCalc == null)
      return null; 
    GMSSRootCalc[] arrayOfGMSSRootCalc = new GMSSRootCalc[paramArrayOfGMSSRootCalc.length];
    System.arraycopy(paramArrayOfGMSSRootCalc, 0, arrayOfGMSSRootCalc, 0, paramArrayOfGMSSRootCalc.length);
    return arrayOfGMSSRootCalc;
  }
  
  static GMSSRootSig[] clone(GMSSRootSig[] paramArrayOfGMSSRootSig) {
    if (paramArrayOfGMSSRootSig == null)
      return null; 
    GMSSRootSig[] arrayOfGMSSRootSig = new GMSSRootSig[paramArrayOfGMSSRootSig.length];
    System.arraycopy(paramArrayOfGMSSRootSig, 0, arrayOfGMSSRootSig, 0, paramArrayOfGMSSRootSig.length);
    return arrayOfGMSSRootSig;
  }
  
  static byte[][] clone(byte[][] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return (byte[][])null; 
    byte[][] arrayOfByte = new byte[paramArrayOfbyte.length][];
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      arrayOfByte[b] = Arrays.clone(paramArrayOfbyte[b]); 
    return arrayOfByte;
  }
  
  static byte[][][] clone(byte[][][] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return (byte[][][])null; 
    byte[][][] arrayOfByte = new byte[paramArrayOfbyte.length][][];
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      arrayOfByte[b] = clone(paramArrayOfbyte[b]); 
    return arrayOfByte;
  }
  
  static Treehash[] clone(Treehash[] paramArrayOfTreehash) {
    if (paramArrayOfTreehash == null)
      return null; 
    Treehash[] arrayOfTreehash = new Treehash[paramArrayOfTreehash.length];
    System.arraycopy(paramArrayOfTreehash, 0, arrayOfTreehash, 0, paramArrayOfTreehash.length);
    return arrayOfTreehash;
  }
  
  static Treehash[][] clone(Treehash[][] paramArrayOfTreehash) {
    if (paramArrayOfTreehash == null)
      return (Treehash[][])null; 
    Treehash[][] arrayOfTreehash = new Treehash[paramArrayOfTreehash.length][];
    for (byte b = 0; b != paramArrayOfTreehash.length; b++)
      arrayOfTreehash[b] = clone(paramArrayOfTreehash[b]); 
    return arrayOfTreehash;
  }
  
  static Vector[] clone(Vector[] paramArrayOfVector) {
    if (paramArrayOfVector == null)
      return null; 
    Vector[] arrayOfVector = new Vector[paramArrayOfVector.length];
    for (byte b = 0; b != paramArrayOfVector.length; b++) {
      arrayOfVector[b] = new Vector();
      Enumeration<?> enumeration = paramArrayOfVector[b].elements();
      while (enumeration.hasMoreElements())
        arrayOfVector[b].addElement(enumeration.nextElement()); 
    } 
    return arrayOfVector;
  }
  
  static Vector[][] clone(Vector[][] paramArrayOfVector) {
    if (paramArrayOfVector == null)
      return (Vector[][])null; 
    Vector[][] arrayOfVector = new Vector[paramArrayOfVector.length][];
    for (byte b = 0; b != paramArrayOfVector.length; b++)
      arrayOfVector[b] = clone(paramArrayOfVector[b]); 
    return arrayOfVector;
  }
}
