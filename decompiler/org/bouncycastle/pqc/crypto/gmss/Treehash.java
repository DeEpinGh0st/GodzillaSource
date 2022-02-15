package org.bouncycastle.pqc.crypto.gmss;

import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.encoders.Hex;

public class Treehash {
  private int maxHeight;
  
  private Vector tailStack;
  
  private Vector heightOfNodes;
  
  private byte[] firstNode;
  
  private byte[] seedActive;
  
  private byte[] seedNext;
  
  private int tailLength;
  
  private int firstNodeHeight;
  
  private boolean isInitialized;
  
  private boolean isFinished;
  
  private boolean seedInitialized;
  
  private Digest messDigestTree;
  
  public Treehash(Digest paramDigest, byte[][] paramArrayOfbyte, int[] paramArrayOfint) {
    this.messDigestTree = paramDigest;
    this.maxHeight = paramArrayOfint[0];
    this.tailLength = paramArrayOfint[1];
    this.firstNodeHeight = paramArrayOfint[2];
    if (paramArrayOfint[3] == 1) {
      this.isFinished = true;
    } else {
      this.isFinished = false;
    } 
    if (paramArrayOfint[4] == 1) {
      this.isInitialized = true;
    } else {
      this.isInitialized = false;
    } 
    if (paramArrayOfint[5] == 1) {
      this.seedInitialized = true;
    } else {
      this.seedInitialized = false;
    } 
    this.heightOfNodes = new Vector();
    byte b;
    for (b = 0; b < this.tailLength; b++)
      this.heightOfNodes.addElement(Integers.valueOf(paramArrayOfint[6 + b])); 
    this.firstNode = paramArrayOfbyte[0];
    this.seedActive = paramArrayOfbyte[1];
    this.seedNext = paramArrayOfbyte[2];
    this.tailStack = new Vector();
    for (b = 0; b < this.tailLength; b++)
      this.tailStack.addElement(paramArrayOfbyte[3 + b]); 
  }
  
  public Treehash(Vector paramVector, int paramInt, Digest paramDigest) {
    this.tailStack = paramVector;
    this.maxHeight = paramInt;
    this.firstNode = null;
    this.isInitialized = false;
    this.isFinished = false;
    this.seedInitialized = false;
    this.messDigestTree = paramDigest;
    this.seedNext = new byte[this.messDigestTree.getDigestSize()];
    this.seedActive = new byte[this.messDigestTree.getDigestSize()];
  }
  
  public void initializeSeed(byte[] paramArrayOfbyte) {
    System.arraycopy(paramArrayOfbyte, 0, this.seedNext, 0, this.messDigestTree.getDigestSize());
    this.seedInitialized = true;
  }
  
  public void initialize() {
    if (!this.seedInitialized) {
      System.err.println("Seed " + this.maxHeight + " not initialized");
      return;
    } 
    this.heightOfNodes = new Vector();
    this.tailLength = 0;
    this.firstNode = null;
    this.firstNodeHeight = -1;
    this.isInitialized = true;
    System.arraycopy(this.seedNext, 0, this.seedActive, 0, this.messDigestTree.getDigestSize());
  }
  
  public void update(GMSSRandom paramGMSSRandom, byte[] paramArrayOfbyte) {
    if (this.isFinished) {
      System.err.println("No more update possible for treehash instance!");
      return;
    } 
    if (!this.isInitialized) {
      System.err.println("Treehash instance not initialized before update");
      return;
    } 
    byte[] arrayOfByte = new byte[this.messDigestTree.getDigestSize()];
    byte b = -1;
    paramGMSSRandom.nextSeed(this.seedActive);
    if (this.firstNode == null) {
      this.firstNode = paramArrayOfbyte;
      this.firstNodeHeight = 0;
    } else {
      arrayOfByte = paramArrayOfbyte;
      b = 0;
      while (this.tailLength > 0 && b == ((Integer)this.heightOfNodes.lastElement()).intValue()) {
        byte[] arrayOfByte1 = new byte[this.messDigestTree.getDigestSize() << 1];
        System.arraycopy(this.tailStack.lastElement(), 0, arrayOfByte1, 0, this.messDigestTree.getDigestSize());
        this.tailStack.removeElementAt(this.tailStack.size() - 1);
        this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
        this.messDigestTree.update(arrayOfByte1, 0, arrayOfByte1.length);
        arrayOfByte = new byte[this.messDigestTree.getDigestSize()];
        this.messDigestTree.doFinal(arrayOfByte, 0);
        b++;
        this.tailLength--;
      } 
      this.tailStack.addElement(arrayOfByte);
      this.heightOfNodes.addElement(Integers.valueOf(b));
      this.tailLength++;
      if (((Integer)this.heightOfNodes.lastElement()).intValue() == this.firstNodeHeight) {
        byte[] arrayOfByte1 = new byte[this.messDigestTree.getDigestSize() << 1];
        System.arraycopy(this.firstNode, 0, arrayOfByte1, 0, this.messDigestTree.getDigestSize());
        System.arraycopy(this.tailStack.lastElement(), 0, arrayOfByte1, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
        this.tailStack.removeElementAt(this.tailStack.size() - 1);
        this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
        this.messDigestTree.update(arrayOfByte1, 0, arrayOfByte1.length);
        this.firstNode = new byte[this.messDigestTree.getDigestSize()];
        this.messDigestTree.doFinal(this.firstNode, 0);
        this.firstNodeHeight++;
        this.tailLength = 0;
      } 
    } 
    if (this.firstNodeHeight == this.maxHeight)
      this.isFinished = true; 
  }
  
  public void destroy() {
    this.isInitialized = false;
    this.isFinished = false;
    this.firstNode = null;
    this.tailLength = 0;
    this.firstNodeHeight = -1;
  }
  
  public int getLowestNodeHeight() {
    return (this.firstNode == null) ? this.maxHeight : ((this.tailLength == 0) ? this.firstNodeHeight : Math.min(this.firstNodeHeight, ((Integer)this.heightOfNodes.lastElement()).intValue()));
  }
  
  public int getFirstNodeHeight() {
    return (this.firstNode == null) ? this.maxHeight : this.firstNodeHeight;
  }
  
  public boolean wasInitialized() {
    return this.isInitialized;
  }
  
  public boolean wasFinished() {
    return this.isFinished;
  }
  
  public byte[] getFirstNode() {
    return this.firstNode;
  }
  
  public byte[] getSeedActive() {
    return this.seedActive;
  }
  
  public void setFirstNode(byte[] paramArrayOfbyte) {
    if (!this.isInitialized)
      initialize(); 
    this.firstNode = paramArrayOfbyte;
    this.firstNodeHeight = this.maxHeight;
    this.isFinished = true;
  }
  
  public void updateNextSeed(GMSSRandom paramGMSSRandom) {
    paramGMSSRandom.nextSeed(this.seedNext);
  }
  
  public Vector getTailStack() {
    return this.tailStack;
  }
  
  public byte[][] getStatByte() {
    byte[][] arrayOfByte = new byte[3 + this.tailLength][this.messDigestTree.getDigestSize()];
    arrayOfByte[0] = this.firstNode;
    arrayOfByte[1] = this.seedActive;
    arrayOfByte[2] = this.seedNext;
    for (byte b = 0; b < this.tailLength; b++)
      arrayOfByte[3 + b] = this.tailStack.elementAt(b); 
    return arrayOfByte;
  }
  
  public int[] getStatInt() {
    int[] arrayOfInt = new int[6 + this.tailLength];
    arrayOfInt[0] = this.maxHeight;
    arrayOfInt[1] = this.tailLength;
    arrayOfInt[2] = this.firstNodeHeight;
    if (this.isFinished) {
      arrayOfInt[3] = 1;
    } else {
      arrayOfInt[3] = 0;
    } 
    if (this.isInitialized) {
      arrayOfInt[4] = 1;
    } else {
      arrayOfInt[4] = 0;
    } 
    if (this.seedInitialized) {
      arrayOfInt[5] = 1;
    } else {
      arrayOfInt[5] = 0;
    } 
    for (byte b = 0; b < this.tailLength; b++)
      arrayOfInt[6 + b] = ((Integer)this.heightOfNodes.elementAt(b)).intValue(); 
    return arrayOfInt;
  }
  
  public String toString() {
    null = "Treehash    : ";
    byte b;
    for (b = 0; b < 6 + this.tailLength; b++)
      null = null + getStatInt()[b] + " "; 
    for (b = 0; b < 3 + this.tailLength; b++) {
      if (getStatByte()[b] != null) {
        null = null + new String(Hex.encode(getStatByte()[b])) + " ";
      } else {
        null = null + "null ";
      } 
    } 
    return null + "  " + this.messDigestTree.getDigestSize();
  }
}
