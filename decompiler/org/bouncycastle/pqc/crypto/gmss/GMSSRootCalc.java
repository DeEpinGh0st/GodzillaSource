package org.bouncycastle.pqc.crypto.gmss;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.encoders.Hex;

public class GMSSRootCalc {
  private int heightOfTree;
  
  private int mdLength;
  
  private Treehash[] treehash;
  
  private Vector[] retain;
  
  private byte[] root;
  
  private byte[][] AuthPath;
  
  private int K;
  
  private Vector tailStack;
  
  private Vector heightOfNodes;
  
  private Digest messDigestTree;
  
  private GMSSDigestProvider digestProvider;
  
  private int[] index;
  
  private boolean isInitialized;
  
  private boolean isFinished;
  
  private int indexForNextSeed;
  
  private int heightOfNextSeed;
  
  public GMSSRootCalc(Digest paramDigest, byte[][] paramArrayOfbyte, int[] paramArrayOfint, Treehash[] paramArrayOfTreehash, Vector[] paramArrayOfVector) {
    this.messDigestTree = this.digestProvider.get();
    this.digestProvider = this.digestProvider;
    this.heightOfTree = paramArrayOfint[0];
    this.mdLength = paramArrayOfint[1];
    this.K = paramArrayOfint[2];
    this.indexForNextSeed = paramArrayOfint[3];
    this.heightOfNextSeed = paramArrayOfint[4];
    if (paramArrayOfint[5] == 1) {
      this.isFinished = true;
    } else {
      this.isFinished = false;
    } 
    if (paramArrayOfint[6] == 1) {
      this.isInitialized = true;
    } else {
      this.isInitialized = false;
    } 
    int i = paramArrayOfint[7];
    this.index = new int[this.heightOfTree];
    byte b;
    for (b = 0; b < this.heightOfTree; b++)
      this.index[b] = paramArrayOfint[8 + b]; 
    this.heightOfNodes = new Vector();
    for (b = 0; b < i; b++)
      this.heightOfNodes.addElement(Integers.valueOf(paramArrayOfint[8 + this.heightOfTree + b])); 
    this.root = paramArrayOfbyte[0];
    this.AuthPath = new byte[this.heightOfTree][this.mdLength];
    for (b = 0; b < this.heightOfTree; b++)
      this.AuthPath[b] = paramArrayOfbyte[1 + b]; 
    this.tailStack = new Vector();
    for (b = 0; b < i; b++)
      this.tailStack.addElement(paramArrayOfbyte[1 + this.heightOfTree + b]); 
    this.treehash = GMSSUtils.clone(paramArrayOfTreehash);
    this.retain = GMSSUtils.clone(paramArrayOfVector);
  }
  
  public GMSSRootCalc(int paramInt1, int paramInt2, GMSSDigestProvider paramGMSSDigestProvider) {
    this.heightOfTree = paramInt1;
    this.digestProvider = paramGMSSDigestProvider;
    this.messDigestTree = paramGMSSDigestProvider.get();
    this.mdLength = this.messDigestTree.getDigestSize();
    this.K = paramInt2;
    this.index = new int[paramInt1];
    this.AuthPath = new byte[paramInt1][this.mdLength];
    this.root = new byte[this.mdLength];
    this.retain = new Vector[this.K - 1];
    for (byte b = 0; b < paramInt2 - 1; b++)
      this.retain[b] = new Vector(); 
  }
  
  public void initialize(Vector paramVector) {
    this.treehash = new Treehash[this.heightOfTree - this.K];
    byte b;
    for (b = 0; b < this.heightOfTree - this.K; b++)
      this.treehash[b] = new Treehash(paramVector, b, this.digestProvider.get()); 
    this.index = new int[this.heightOfTree];
    this.AuthPath = new byte[this.heightOfTree][this.mdLength];
    this.root = new byte[this.mdLength];
    this.tailStack = new Vector();
    this.heightOfNodes = new Vector();
    this.isInitialized = true;
    this.isFinished = false;
    for (b = 0; b < this.heightOfTree; b++)
      this.index[b] = -1; 
    this.retain = new Vector[this.K - 1];
    for (b = 0; b < this.K - 1; b++)
      this.retain[b] = new Vector(); 
    this.indexForNextSeed = 3;
    this.heightOfNextSeed = 0;
  }
  
  public void update(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (this.heightOfNextSeed < this.heightOfTree - this.K && this.indexForNextSeed - 2 == this.index[0]) {
      initializeTreehashSeed(paramArrayOfbyte1, this.heightOfNextSeed);
      this.heightOfNextSeed++;
      this.indexForNextSeed *= 2;
    } 
    update(paramArrayOfbyte2);
  }
  
  public void update(byte[] paramArrayOfbyte) {
    if (this.isFinished) {
      System.out.print("Too much updates for Tree!!");
      return;
    } 
    if (!this.isInitialized) {
      System.err.println("GMSSRootCalc not initialized!");
      return;
    } 
    this.index[0] = this.index[0] + 1;
    if (this.index[0] == 1) {
      System.arraycopy(paramArrayOfbyte, 0, this.AuthPath[0], 0, this.mdLength);
    } else if (this.index[0] == 3 && this.heightOfTree > this.K) {
      this.treehash[0].setFirstNode(paramArrayOfbyte);
    } 
    if ((this.index[0] - 3) % 2 == 0 && this.index[0] >= 3 && this.heightOfTree == this.K)
      this.retain[0].insertElementAt(paramArrayOfbyte, 0); 
    if (this.index[0] == 0) {
      this.tailStack.addElement(paramArrayOfbyte);
      this.heightOfNodes.addElement(Integers.valueOf(0));
    } else {
      byte[] arrayOfByte1 = new byte[this.mdLength];
      byte[] arrayOfByte2 = new byte[this.mdLength << 1];
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, this.mdLength);
      byte b = 0;
      while (this.tailStack.size() > 0 && b == ((Integer)this.heightOfNodes.lastElement()).intValue()) {
        System.arraycopy(this.tailStack.lastElement(), 0, arrayOfByte2, 0, this.mdLength);
        this.tailStack.removeElementAt(this.tailStack.size() - 1);
        this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, this.mdLength, this.mdLength);
        this.messDigestTree.update(arrayOfByte2, 0, arrayOfByte2.length);
        arrayOfByte1 = new byte[this.messDigestTree.getDigestSize()];
        this.messDigestTree.doFinal(arrayOfByte1, 0);
        if (++b < this.heightOfTree) {
          this.index[b] = this.index[b] + 1;
          if (this.index[b] == 1)
            System.arraycopy(arrayOfByte1, 0, this.AuthPath[b], 0, this.mdLength); 
          if (b >= this.heightOfTree - this.K) {
            if (b == 0)
              System.out.println("M���P"); 
            if ((this.index[b] - 3) % 2 == 0 && this.index[b] >= 3)
              this.retain[b - this.heightOfTree - this.K].insertElementAt(arrayOfByte1, 0); 
            continue;
          } 
          if (this.index[b] == 3)
            this.treehash[b].setFirstNode(arrayOfByte1); 
        } 
      } 
      this.tailStack.addElement(arrayOfByte1);
      this.heightOfNodes.addElement(Integers.valueOf(b));
      if (b == this.heightOfTree) {
        this.isFinished = true;
        this.isInitialized = false;
        this.root = this.tailStack.lastElement();
      } 
    } 
  }
  
  public void initializeTreehashSeed(byte[] paramArrayOfbyte, int paramInt) {
    this.treehash[paramInt].initializeSeed(paramArrayOfbyte);
  }
  
  public boolean wasInitialized() {
    return this.isInitialized;
  }
  
  public boolean wasFinished() {
    return this.isFinished;
  }
  
  public byte[][] getAuthPath() {
    return GMSSUtils.clone(this.AuthPath);
  }
  
  public Treehash[] getTreehash() {
    return GMSSUtils.clone(this.treehash);
  }
  
  public Vector[] getRetain() {
    return GMSSUtils.clone(this.retain);
  }
  
  public byte[] getRoot() {
    return Arrays.clone(this.root);
  }
  
  public Vector getStack() {
    Vector vector = new Vector();
    Enumeration enumeration = this.tailStack.elements();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector;
  }
  
  public byte[][] getStatByte() {
    int i;
    if (this.tailStack == null) {
      i = 0;
    } else {
      i = this.tailStack.size();
    } 
    byte[][] arrayOfByte = new byte[1 + this.heightOfTree + i][64];
    arrayOfByte[0] = this.root;
    byte b;
    for (b = 0; b < this.heightOfTree; b++)
      arrayOfByte[1 + b] = this.AuthPath[b]; 
    for (b = 0; b < i; b++)
      arrayOfByte[1 + this.heightOfTree + b] = this.tailStack.elementAt(b); 
    return arrayOfByte;
  }
  
  public int[] getStatInt() {
    int i;
    if (this.tailStack == null) {
      i = 0;
    } else {
      i = this.tailStack.size();
    } 
    int[] arrayOfInt = new int[8 + this.heightOfTree + i];
    arrayOfInt[0] = this.heightOfTree;
    arrayOfInt[1] = this.mdLength;
    arrayOfInt[2] = this.K;
    arrayOfInt[3] = this.indexForNextSeed;
    arrayOfInt[4] = this.heightOfNextSeed;
    if (this.isFinished) {
      arrayOfInt[5] = 1;
    } else {
      arrayOfInt[5] = 0;
    } 
    if (this.isInitialized) {
      arrayOfInt[6] = 1;
    } else {
      arrayOfInt[6] = 0;
    } 
    arrayOfInt[7] = i;
    byte b;
    for (b = 0; b < this.heightOfTree; b++)
      arrayOfInt[8 + b] = this.index[b]; 
    for (b = 0; b < i; b++)
      arrayOfInt[8 + this.heightOfTree + b] = ((Integer)this.heightOfNodes.elementAt(b)).intValue(); 
    return arrayOfInt;
  }
  
  public String toString() {
    int i;
    null = "";
    if (this.tailStack == null) {
      i = 0;
    } else {
      i = this.tailStack.size();
    } 
    byte b;
    for (b = 0; b < 8 + this.heightOfTree + i; b++)
      null = null + getStatInt()[b] + " "; 
    for (b = 0; b < 1 + this.heightOfTree + i; b++)
      null = null + new String(Hex.encode(getStatByte()[b])) + " "; 
    return null + "  " + this.digestProvider.get().getDigestSize();
  }
}
