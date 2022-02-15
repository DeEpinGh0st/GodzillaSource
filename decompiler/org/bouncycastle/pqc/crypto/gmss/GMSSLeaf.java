package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class GMSSLeaf {
  private Digest messDigestOTS;
  
  private int mdsize;
  
  private int keysize;
  
  private GMSSRandom gmssRandom;
  
  private byte[] leaf;
  
  private byte[] concHashs;
  
  private int i;
  
  private int j;
  
  private int two_power_w;
  
  private int w;
  
  private int steps;
  
  private byte[] seed;
  
  byte[] privateKeyOTS;
  
  public GMSSLeaf(Digest paramDigest, byte[][] paramArrayOfbyte, int[] paramArrayOfint) {
    this.i = paramArrayOfint[0];
    this.j = paramArrayOfint[1];
    this.steps = paramArrayOfint[2];
    this.w = paramArrayOfint[3];
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.mdsize = this.messDigestOTS.getDigestSize();
    int i = this.mdsize << 3;
    int j = (int)Math.ceil(i / this.w);
    int k = getLog((j << this.w) + 1);
    this.keysize = j + (int)Math.ceil(k / this.w);
    this.two_power_w = 1 << this.w;
    this.privateKeyOTS = paramArrayOfbyte[0];
    this.seed = paramArrayOfbyte[1];
    this.concHashs = paramArrayOfbyte[2];
    this.leaf = paramArrayOfbyte[3];
  }
  
  GMSSLeaf(Digest paramDigest, int paramInt1, int paramInt2) {
    this.w = paramInt1;
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.mdsize = this.messDigestOTS.getDigestSize();
    int i = this.mdsize << 3;
    int j = (int)Math.ceil(i / paramInt1);
    int k = getLog((j << paramInt1) + 1);
    this.keysize = j + (int)Math.ceil(k / paramInt1);
    this.two_power_w = 1 << paramInt1;
    this.steps = (int)Math.ceil((((1 << paramInt1) - 1) * this.keysize + 1 + this.keysize) / paramInt2);
    this.seed = new byte[this.mdsize];
    this.leaf = new byte[this.mdsize];
    this.privateKeyOTS = new byte[this.mdsize];
    this.concHashs = new byte[this.mdsize * this.keysize];
  }
  
  public GMSSLeaf(Digest paramDigest, int paramInt1, int paramInt2, byte[] paramArrayOfbyte) {
    this.w = paramInt1;
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.mdsize = this.messDigestOTS.getDigestSize();
    int i = this.mdsize << 3;
    int j = (int)Math.ceil(i / paramInt1);
    int k = getLog((j << paramInt1) + 1);
    this.keysize = j + (int)Math.ceil(k / paramInt1);
    this.two_power_w = 1 << paramInt1;
    this.steps = (int)Math.ceil((((1 << paramInt1) - 1) * this.keysize + 1 + this.keysize) / paramInt2);
    this.seed = new byte[this.mdsize];
    this.leaf = new byte[this.mdsize];
    this.privateKeyOTS = new byte[this.mdsize];
    this.concHashs = new byte[this.mdsize * this.keysize];
    initLeafCalc(paramArrayOfbyte);
  }
  
  private GMSSLeaf(GMSSLeaf paramGMSSLeaf) {
    this.messDigestOTS = paramGMSSLeaf.messDigestOTS;
    this.mdsize = paramGMSSLeaf.mdsize;
    this.keysize = paramGMSSLeaf.keysize;
    this.gmssRandom = paramGMSSLeaf.gmssRandom;
    this.leaf = Arrays.clone(paramGMSSLeaf.leaf);
    this.concHashs = Arrays.clone(paramGMSSLeaf.concHashs);
    this.i = paramGMSSLeaf.i;
    this.j = paramGMSSLeaf.j;
    this.two_power_w = paramGMSSLeaf.two_power_w;
    this.w = paramGMSSLeaf.w;
    this.steps = paramGMSSLeaf.steps;
    this.seed = Arrays.clone(paramGMSSLeaf.seed);
    this.privateKeyOTS = Arrays.clone(paramGMSSLeaf.privateKeyOTS);
  }
  
  void initLeafCalc(byte[] paramArrayOfbyte) {
    this.i = 0;
    this.j = 0;
    byte[] arrayOfByte = new byte[this.mdsize];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, this.seed.length);
    this.seed = this.gmssRandom.nextSeed(arrayOfByte);
  }
  
  GMSSLeaf nextLeaf() {
    GMSSLeaf gMSSLeaf = new GMSSLeaf(this);
    gMSSLeaf.updateLeafCalc();
    return gMSSLeaf;
  }
  
  private void updateLeafCalc() {
    byte[] arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
    for (byte b = 0; b < this.steps + 10000; b++) {
      if (this.i == this.keysize && this.j == this.two_power_w - 1) {
        this.messDigestOTS.update(this.concHashs, 0, this.concHashs.length);
        this.leaf = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.leaf, 0);
        return;
      } 
      if (this.i == 0 || this.j == this.two_power_w - 1) {
        this.i++;
        this.j = 0;
        this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
      } else {
        this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
        this.privateKeyOTS = arrayOfByte;
        this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
        this.j++;
        if (this.j == this.two_power_w - 1)
          System.arraycopy(this.privateKeyOTS, 0, this.concHashs, this.mdsize * (this.i - 1), this.mdsize); 
      } 
    } 
    throw new IllegalStateException("unable to updateLeaf in steps: " + this.steps + " " + this.i + " " + this.j);
  }
  
  public byte[] getLeaf() {
    return Arrays.clone(this.leaf);
  }
  
  private int getLog(int paramInt) {
    byte b = 1;
    int i = 2;
    while (i < paramInt) {
      i <<= 1;
      b++;
    } 
    return b;
  }
  
  public byte[][] getStatByte() {
    byte[][] arrayOfByte = new byte[4][];
    arrayOfByte[0] = new byte[this.mdsize];
    arrayOfByte[1] = new byte[this.mdsize];
    arrayOfByte[2] = new byte[this.mdsize * this.keysize];
    arrayOfByte[3] = new byte[this.mdsize];
    arrayOfByte[0] = this.privateKeyOTS;
    arrayOfByte[1] = this.seed;
    arrayOfByte[2] = this.concHashs;
    arrayOfByte[3] = this.leaf;
    return arrayOfByte;
  }
  
  public int[] getStatInt() {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = this.i;
    arrayOfInt[1] = this.j;
    arrayOfInt[2] = this.steps;
    arrayOfInt[3] = this.w;
    return arrayOfInt;
  }
  
  public String toString() {
    String str = "";
    for (byte b1 = 0; b1 < 4; b1++)
      str = str + getStatInt()[b1] + " "; 
    str = str + " " + this.mdsize + " " + this.keysize + " " + this.two_power_w + " ";
    byte[][] arrayOfByte = getStatByte();
    for (byte b2 = 0; b2 < 4; b2++) {
      if (arrayOfByte[b2] != null) {
        str = str + new String(Hex.encode(arrayOfByte[b2])) + " ";
      } else {
        str = str + "null ";
      } 
    } 
    return str;
  }
}
