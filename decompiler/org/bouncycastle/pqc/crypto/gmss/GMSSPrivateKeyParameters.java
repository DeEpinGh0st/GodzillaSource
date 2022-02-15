package org.bouncycastle.pqc.crypto.gmss;

import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.util.Arrays;

public class GMSSPrivateKeyParameters extends GMSSKeyParameters {
  private int[] index;
  
  private byte[][] currentSeeds;
  
  private byte[][] nextNextSeeds;
  
  private byte[][][] currentAuthPaths;
  
  private byte[][][] nextAuthPaths;
  
  private Treehash[][] currentTreehash;
  
  private Treehash[][] nextTreehash;
  
  private Vector[] currentStack;
  
  private Vector[] nextStack;
  
  private Vector[][] currentRetain;
  
  private Vector[][] nextRetain;
  
  private byte[][][] keep;
  
  private GMSSLeaf[] nextNextLeaf;
  
  private GMSSLeaf[] upperLeaf;
  
  private GMSSLeaf[] upperTreehashLeaf;
  
  private int[] minTreehash;
  
  private GMSSParameters gmssPS;
  
  private byte[][] nextRoot;
  
  private GMSSRootCalc[] nextNextRoot;
  
  private byte[][] currentRootSig;
  
  private GMSSRootSig[] nextRootSig;
  
  private GMSSDigestProvider digestProvider;
  
  private boolean used = false;
  
  private int[] heightOfTrees;
  
  private int[] otsIndex;
  
  private int[] K;
  
  private int numLayer;
  
  private Digest messDigestTrees;
  
  private int mdLength;
  
  private GMSSRandom gmssRandom;
  
  private int[] numLeafs;
  
  public GMSSPrivateKeyParameters(byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2, byte[][][] paramArrayOfbyte3, byte[][][] paramArrayOfbyte4, Treehash[][] paramArrayOfTreehash1, Treehash[][] paramArrayOfTreehash2, Vector[] paramArrayOfVector1, Vector[] paramArrayOfVector2, Vector[][] paramArrayOfVector3, Vector[][] paramArrayOfVector4, byte[][] paramArrayOfbyte5, byte[][] paramArrayOfbyte6, GMSSParameters paramGMSSParameters, GMSSDigestProvider paramGMSSDigestProvider) {
    this(null, paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramArrayOfbyte4, (byte[][][])null, paramArrayOfTreehash1, paramArrayOfTreehash2, paramArrayOfVector1, paramArrayOfVector2, paramArrayOfVector3, paramArrayOfVector4, null, null, null, null, paramArrayOfbyte5, null, paramArrayOfbyte6, null, paramGMSSParameters, paramGMSSDigestProvider);
  }
  
  public GMSSPrivateKeyParameters(int[] paramArrayOfint1, byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2, byte[][][] paramArrayOfbyte3, byte[][][] paramArrayOfbyte4, byte[][][] paramArrayOfbyte5, Treehash[][] paramArrayOfTreehash1, Treehash[][] paramArrayOfTreehash2, Vector[] paramArrayOfVector1, Vector[] paramArrayOfVector2, Vector[][] paramArrayOfVector3, Vector[][] paramArrayOfVector4, GMSSLeaf[] paramArrayOfGMSSLeaf1, GMSSLeaf[] paramArrayOfGMSSLeaf2, GMSSLeaf[] paramArrayOfGMSSLeaf3, int[] paramArrayOfint2, byte[][] paramArrayOfbyte6, GMSSRootCalc[] paramArrayOfGMSSRootCalc, byte[][] paramArrayOfbyte7, GMSSRootSig[] paramArrayOfGMSSRootSig, GMSSParameters paramGMSSParameters, GMSSDigestProvider paramGMSSDigestProvider) {
    super(true, paramGMSSParameters);
    this.messDigestTrees = paramGMSSDigestProvider.get();
    this.mdLength = this.messDigestTrees.getDigestSize();
    this.gmssPS = paramGMSSParameters;
    this.otsIndex = paramGMSSParameters.getWinternitzParameter();
    this.K = paramGMSSParameters.getK();
    this.heightOfTrees = paramGMSSParameters.getHeightOfTrees();
    this.numLayer = this.gmssPS.getNumOfLayers();
    if (paramArrayOfint1 == null) {
      this.index = new int[this.numLayer];
      for (byte b1 = 0; b1 < this.numLayer; b1++)
        this.index[b1] = 0; 
    } else {
      this.index = paramArrayOfint1;
    } 
    this.currentSeeds = paramArrayOfbyte1;
    this.nextNextSeeds = paramArrayOfbyte2;
    this.currentAuthPaths = paramArrayOfbyte3;
    this.nextAuthPaths = paramArrayOfbyte4;
    if (paramArrayOfbyte5 == null) {
      this.keep = new byte[this.numLayer][][];
      for (byte b1 = 0; b1 < this.numLayer; b1++)
        this.keep[b1] = new byte[(int)Math.floor((this.heightOfTrees[b1] / 2))][this.mdLength]; 
    } else {
      this.keep = paramArrayOfbyte5;
    } 
    if (paramArrayOfVector1 == null) {
      this.currentStack = new Vector[this.numLayer];
      for (byte b1 = 0; b1 < this.numLayer; b1++)
        this.currentStack[b1] = new Vector(); 
    } else {
      this.currentStack = paramArrayOfVector1;
    } 
    if (paramArrayOfVector2 == null) {
      this.nextStack = new Vector[this.numLayer - 1];
      for (byte b1 = 0; b1 < this.numLayer - 1; b1++)
        this.nextStack[b1] = new Vector(); 
    } else {
      this.nextStack = paramArrayOfVector2;
    } 
    this.currentTreehash = paramArrayOfTreehash1;
    this.nextTreehash = paramArrayOfTreehash2;
    this.currentRetain = paramArrayOfVector3;
    this.nextRetain = paramArrayOfVector4;
    this.nextRoot = paramArrayOfbyte6;
    this.digestProvider = paramGMSSDigestProvider;
    if (paramArrayOfGMSSRootCalc == null) {
      this.nextNextRoot = new GMSSRootCalc[this.numLayer - 1];
      for (byte b1 = 0; b1 < this.numLayer - 1; b1++)
        this.nextNextRoot[b1] = new GMSSRootCalc(this.heightOfTrees[b1 + 1], this.K[b1 + 1], this.digestProvider); 
    } else {
      this.nextNextRoot = paramArrayOfGMSSRootCalc;
    } 
    this.currentRootSig = paramArrayOfbyte7;
    this.numLeafs = new int[this.numLayer];
    byte b;
    for (b = 0; b < this.numLayer; b++)
      this.numLeafs[b] = 1 << this.heightOfTrees[b]; 
    this.gmssRandom = new GMSSRandom(this.messDigestTrees);
    if (this.numLayer > 1) {
      if (paramArrayOfGMSSLeaf1 == null) {
        this.nextNextLeaf = new GMSSLeaf[this.numLayer - 2];
        for (b = 0; b < this.numLayer - 2; b++)
          this.nextNextLeaf[b] = new GMSSLeaf(paramGMSSDigestProvider.get(), this.otsIndex[b + 1], this.numLeafs[b + 2], this.nextNextSeeds[b]); 
      } else {
        this.nextNextLeaf = paramArrayOfGMSSLeaf1;
      } 
    } else {
      this.nextNextLeaf = new GMSSLeaf[0];
    } 
    if (paramArrayOfGMSSLeaf2 == null) {
      this.upperLeaf = new GMSSLeaf[this.numLayer - 1];
      for (b = 0; b < this.numLayer - 1; b++)
        this.upperLeaf[b] = new GMSSLeaf(paramGMSSDigestProvider.get(), this.otsIndex[b], this.numLeafs[b + 1], this.currentSeeds[b]); 
    } else {
      this.upperLeaf = paramArrayOfGMSSLeaf2;
    } 
    if (paramArrayOfGMSSLeaf3 == null) {
      this.upperTreehashLeaf = new GMSSLeaf[this.numLayer - 1];
      for (b = 0; b < this.numLayer - 1; b++)
        this.upperTreehashLeaf[b] = new GMSSLeaf(paramGMSSDigestProvider.get(), this.otsIndex[b], this.numLeafs[b + 1]); 
    } else {
      this.upperTreehashLeaf = paramArrayOfGMSSLeaf3;
    } 
    if (paramArrayOfint2 == null) {
      this.minTreehash = new int[this.numLayer - 1];
      for (b = 0; b < this.numLayer - 1; b++)
        this.minTreehash[b] = -1; 
    } else {
      this.minTreehash = paramArrayOfint2;
    } 
    byte[] arrayOfByte1 = new byte[this.mdLength];
    byte[] arrayOfByte2 = new byte[this.mdLength];
    if (paramArrayOfGMSSRootSig == null) {
      this.nextRootSig = new GMSSRootSig[this.numLayer - 1];
      for (byte b1 = 0; b1 < this.numLayer - 1; b1++) {
        System.arraycopy(paramArrayOfbyte1[b1], 0, arrayOfByte1, 0, this.mdLength);
        this.gmssRandom.nextSeed(arrayOfByte1);
        arrayOfByte2 = this.gmssRandom.nextSeed(arrayOfByte1);
        this.nextRootSig[b1] = new GMSSRootSig(paramGMSSDigestProvider.get(), this.otsIndex[b1], this.heightOfTrees[b1 + 1]);
        this.nextRootSig[b1].initSign(arrayOfByte2, paramArrayOfbyte6[b1]);
      } 
    } else {
      this.nextRootSig = paramArrayOfGMSSRootSig;
    } 
  }
  
  private GMSSPrivateKeyParameters(GMSSPrivateKeyParameters paramGMSSPrivateKeyParameters) {
    super(true, paramGMSSPrivateKeyParameters.getParameters());
    this.index = Arrays.clone(paramGMSSPrivateKeyParameters.index);
    this.currentSeeds = Arrays.clone(paramGMSSPrivateKeyParameters.currentSeeds);
    this.nextNextSeeds = Arrays.clone(paramGMSSPrivateKeyParameters.nextNextSeeds);
    this.currentAuthPaths = Arrays.clone(paramGMSSPrivateKeyParameters.currentAuthPaths);
    this.nextAuthPaths = Arrays.clone(paramGMSSPrivateKeyParameters.nextAuthPaths);
    this.currentTreehash = paramGMSSPrivateKeyParameters.currentTreehash;
    this.nextTreehash = paramGMSSPrivateKeyParameters.nextTreehash;
    this.currentStack = paramGMSSPrivateKeyParameters.currentStack;
    this.nextStack = paramGMSSPrivateKeyParameters.nextStack;
    this.currentRetain = paramGMSSPrivateKeyParameters.currentRetain;
    this.nextRetain = paramGMSSPrivateKeyParameters.nextRetain;
    this.keep = Arrays.clone(paramGMSSPrivateKeyParameters.keep);
    this.nextNextLeaf = paramGMSSPrivateKeyParameters.nextNextLeaf;
    this.upperLeaf = paramGMSSPrivateKeyParameters.upperLeaf;
    this.upperTreehashLeaf = paramGMSSPrivateKeyParameters.upperTreehashLeaf;
    this.minTreehash = paramGMSSPrivateKeyParameters.minTreehash;
    this.gmssPS = paramGMSSPrivateKeyParameters.gmssPS;
    this.nextRoot = Arrays.clone(paramGMSSPrivateKeyParameters.nextRoot);
    this.nextNextRoot = paramGMSSPrivateKeyParameters.nextNextRoot;
    this.currentRootSig = paramGMSSPrivateKeyParameters.currentRootSig;
    this.nextRootSig = paramGMSSPrivateKeyParameters.nextRootSig;
    this.digestProvider = paramGMSSPrivateKeyParameters.digestProvider;
    this.heightOfTrees = paramGMSSPrivateKeyParameters.heightOfTrees;
    this.otsIndex = paramGMSSPrivateKeyParameters.otsIndex;
    this.K = paramGMSSPrivateKeyParameters.K;
    this.numLayer = paramGMSSPrivateKeyParameters.numLayer;
    this.messDigestTrees = paramGMSSPrivateKeyParameters.messDigestTrees;
    this.mdLength = paramGMSSPrivateKeyParameters.mdLength;
    this.gmssRandom = paramGMSSPrivateKeyParameters.gmssRandom;
    this.numLeafs = paramGMSSPrivateKeyParameters.numLeafs;
  }
  
  public boolean isUsed() {
    return this.used;
  }
  
  public void markUsed() {
    this.used = true;
  }
  
  public GMSSPrivateKeyParameters nextKey() {
    GMSSPrivateKeyParameters gMSSPrivateKeyParameters = new GMSSPrivateKeyParameters(this);
    gMSSPrivateKeyParameters.nextKey(this.gmssPS.getNumOfLayers() - 1);
    return gMSSPrivateKeyParameters;
  }
  
  private void nextKey(int paramInt) {
    if (paramInt == this.numLayer - 1)
      this.index[paramInt] = this.index[paramInt] + 1; 
    if (this.index[paramInt] == this.numLeafs[paramInt]) {
      if (this.numLayer != 1) {
        nextTree(paramInt);
        this.index[paramInt] = 0;
      } 
    } else {
      updateKey(paramInt);
    } 
  }
  
  private void nextTree(int paramInt) {
    if (paramInt > 0) {
      this.index[paramInt - 1] = this.index[paramInt - 1] + 1;
      boolean bool = true;
      int i = paramInt;
      do {
        if (this.index[--i] >= this.numLeafs[i])
          continue; 
        bool = false;
      } while (bool && i > 0);
      if (!bool) {
        this.gmssRandom.nextSeed(this.currentSeeds[paramInt]);
        this.nextRootSig[paramInt - 1].updateSign();
        if (paramInt > 1)
          this.nextNextLeaf[paramInt - 1 - 1] = this.nextNextLeaf[paramInt - 1 - 1].nextLeaf(); 
        this.upperLeaf[paramInt - 1] = this.upperLeaf[paramInt - 1].nextLeaf();
        if (this.minTreehash[paramInt - 1] >= 0) {
          this.upperTreehashLeaf[paramInt - 1] = this.upperTreehashLeaf[paramInt - 1].nextLeaf();
          byte[] arrayOfByte = this.upperTreehashLeaf[paramInt - 1].getLeaf();
          try {
            this.currentTreehash[paramInt - 1][this.minTreehash[paramInt - 1]].update(this.gmssRandom, arrayOfByte);
            if (this.currentTreehash[paramInt - 1][this.minTreehash[paramInt - 1]].wasFinished());
          } catch (Exception exception) {
            System.out.println(exception);
          } 
        } 
        updateNextNextAuthRoot(paramInt);
        this.currentRootSig[paramInt - 1] = this.nextRootSig[paramInt - 1].getSig();
        byte b;
        for (b = 0; b < this.heightOfTrees[paramInt] - this.K[paramInt]; b++) {
          this.currentTreehash[paramInt][b] = this.nextTreehash[paramInt - 1][b];
          this.nextTreehash[paramInt - 1][b] = this.nextNextRoot[paramInt - 1].getTreehash()[b];
        } 
        for (b = 0; b < this.heightOfTrees[paramInt]; b++) {
          System.arraycopy(this.nextAuthPaths[paramInt - 1][b], 0, this.currentAuthPaths[paramInt][b], 0, this.mdLength);
          System.arraycopy(this.nextNextRoot[paramInt - 1].getAuthPath()[b], 0, this.nextAuthPaths[paramInt - 1][b], 0, this.mdLength);
        } 
        for (b = 0; b < this.K[paramInt] - 1; b++) {
          this.currentRetain[paramInt][b] = this.nextRetain[paramInt - 1][b];
          this.nextRetain[paramInt - 1][b] = this.nextNextRoot[paramInt - 1].getRetain()[b];
        } 
        this.currentStack[paramInt] = this.nextStack[paramInt - 1];
        this.nextStack[paramInt - 1] = this.nextNextRoot[paramInt - 1].getStack();
        this.nextRoot[paramInt - 1] = this.nextNextRoot[paramInt - 1].getRoot();
        byte[] arrayOfByte1 = new byte[this.mdLength];
        byte[] arrayOfByte2 = new byte[this.mdLength];
        System.arraycopy(this.currentSeeds[paramInt - 1], 0, arrayOfByte2, 0, this.mdLength);
        arrayOfByte1 = this.gmssRandom.nextSeed(arrayOfByte2);
        arrayOfByte1 = this.gmssRandom.nextSeed(arrayOfByte2);
        arrayOfByte1 = this.gmssRandom.nextSeed(arrayOfByte2);
        this.nextRootSig[paramInt - 1].initSign(arrayOfByte1, this.nextRoot[paramInt - 1]);
        nextKey(paramInt - 1);
      } 
    } 
  }
  
  private void updateKey(int paramInt) {
    computeAuthPaths(paramInt);
    if (paramInt > 0) {
      if (paramInt > 1)
        this.nextNextLeaf[paramInt - 1 - 1] = this.nextNextLeaf[paramInt - 1 - 1].nextLeaf(); 
      this.upperLeaf[paramInt - 1] = this.upperLeaf[paramInt - 1].nextLeaf();
      int i = (int)Math.floor((getNumLeafs(paramInt) * 2) / (this.heightOfTrees[paramInt - 1] - this.K[paramInt - 1]));
      if (this.index[paramInt] % i == 1) {
        if (this.index[paramInt] > 1 && this.minTreehash[paramInt - 1] >= 0) {
          byte[] arrayOfByte = this.upperTreehashLeaf[paramInt - 1].getLeaf();
          try {
            this.currentTreehash[paramInt - 1][this.minTreehash[paramInt - 1]].update(this.gmssRandom, arrayOfByte);
            if (this.currentTreehash[paramInt - 1][this.minTreehash[paramInt - 1]].wasFinished());
          } catch (Exception exception) {
            System.out.println(exception);
          } 
        } 
        this.minTreehash[paramInt - 1] = getMinTreehashIndex(paramInt - 1);
        if (this.minTreehash[paramInt - 1] >= 0) {
          byte[] arrayOfByte = this.currentTreehash[paramInt - 1][this.minTreehash[paramInt - 1]].getSeedActive();
          this.upperTreehashLeaf[paramInt - 1] = new GMSSLeaf(this.digestProvider.get(), this.otsIndex[paramInt - 1], i, arrayOfByte);
          this.upperTreehashLeaf[paramInt - 1] = this.upperTreehashLeaf[paramInt - 1].nextLeaf();
        } 
      } else if (this.minTreehash[paramInt - 1] >= 0) {
        this.upperTreehashLeaf[paramInt - 1] = this.upperTreehashLeaf[paramInt - 1].nextLeaf();
      } 
      this.nextRootSig[paramInt - 1].updateSign();
      if (this.index[paramInt] == 1)
        this.nextNextRoot[paramInt - 1].initialize(new Vector()); 
      updateNextNextAuthRoot(paramInt);
    } 
  }
  
  private int getMinTreehashIndex(int paramInt) {
    byte b = -1;
    for (byte b1 = 0; b1 < this.heightOfTrees[paramInt] - this.K[paramInt]; b1++) {
      if (this.currentTreehash[paramInt][b1].wasInitialized() && !this.currentTreehash[paramInt][b1].wasFinished())
        if (b == -1) {
          b = b1;
        } else if (this.currentTreehash[paramInt][b1].getLowestNodeHeight() < this.currentTreehash[paramInt][b].getLowestNodeHeight()) {
          b = b1;
        }  
    } 
    return b;
  }
  
  private void computeAuthPaths(int paramInt) {
    int i = this.index[paramInt];
    int j = this.heightOfTrees[paramInt];
    int k = this.K[paramInt];
    int m;
    for (m = 0; m < j - k; m++)
      this.currentTreehash[paramInt][m].updateNextSeed(this.gmssRandom); 
    m = heightOfPhi(i);
    byte[] arrayOfByte1 = new byte[this.mdLength];
    arrayOfByte1 = this.gmssRandom.nextSeed(this.currentSeeds[paramInt]);
    int n = i >>> m + 1 & 0x1;
    byte[] arrayOfByte2 = new byte[this.mdLength];
    if (m < j - 1 && n == 0)
      System.arraycopy(this.currentAuthPaths[paramInt][m], 0, arrayOfByte2, 0, this.mdLength); 
    byte[] arrayOfByte3 = new byte[this.mdLength];
    if (m == 0) {
      if (paramInt == this.numLayer - 1) {
        WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte1, this.digestProvider.get(), this.otsIndex[paramInt]);
        arrayOfByte3 = winternitzOTSignature.getPublicKey();
      } else {
        byte[] arrayOfByte = new byte[this.mdLength];
        System.arraycopy(this.currentSeeds[paramInt], 0, arrayOfByte, 0, this.mdLength);
        this.gmssRandom.nextSeed(arrayOfByte);
        arrayOfByte3 = this.upperLeaf[paramInt].getLeaf();
        this.upperLeaf[paramInt].initLeafCalc(arrayOfByte);
      } 
      System.arraycopy(arrayOfByte3, 0, this.currentAuthPaths[paramInt][0], 0, this.mdLength);
    } else {
      byte[] arrayOfByte = new byte[this.mdLength << 1];
      System.arraycopy(this.currentAuthPaths[paramInt][m - 1], 0, arrayOfByte, 0, this.mdLength);
      System.arraycopy(this.keep[paramInt][(int)Math.floor(((m - 1) / 2))], 0, arrayOfByte, this.mdLength, this.mdLength);
      this.messDigestTrees.update(arrayOfByte, 0, arrayOfByte.length);
      this.currentAuthPaths[paramInt][m] = new byte[this.messDigestTrees.getDigestSize()];
      this.messDigestTrees.doFinal(this.currentAuthPaths[paramInt][m], 0);
      for (byte b = 0; b < m; b++) {
        if (b < j - k)
          if (this.currentTreehash[paramInt][b].wasFinished()) {
            System.arraycopy(this.currentTreehash[paramInt][b].getFirstNode(), 0, this.currentAuthPaths[paramInt][b], 0, this.mdLength);
            this.currentTreehash[paramInt][b].destroy();
          } else {
            System.err.println("Treehash (" + paramInt + "," + b + ") not finished when needed in AuthPathComputation");
          }  
        if (b < j - 1 && b >= j - k && this.currentRetain[paramInt][b - j - k].size() > 0) {
          System.arraycopy(this.currentRetain[paramInt][b - j - k].lastElement(), 0, this.currentAuthPaths[paramInt][b], 0, this.mdLength);
          this.currentRetain[paramInt][b - j - k].removeElementAt(this.currentRetain[paramInt][b - j - k].size() - 1);
        } 
        if (b < j - k) {
          int i1 = i + 3 * (1 << b);
          if (i1 < this.numLeafs[paramInt])
            this.currentTreehash[paramInt][b].initialize(); 
        } 
      } 
    } 
    if (m < j - 1 && n == 0)
      System.arraycopy(arrayOfByte2, 0, this.keep[paramInt][(int)Math.floor((m / 2))], 0, this.mdLength); 
    if (paramInt == this.numLayer - 1) {
      for (byte b = 1; b <= (j - k) / 2; b++) {
        int i1 = getMinTreehashIndex(paramInt);
        if (i1 >= 0)
          try {
            byte[] arrayOfByte4 = new byte[this.mdLength];
            System.arraycopy(this.currentTreehash[paramInt][i1].getSeedActive(), 0, arrayOfByte4, 0, this.mdLength);
            byte[] arrayOfByte5 = this.gmssRandom.nextSeed(arrayOfByte4);
            WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte5, this.digestProvider.get(), this.otsIndex[paramInt]);
            byte[] arrayOfByte6 = winternitzOTSignature.getPublicKey();
            this.currentTreehash[paramInt][i1].update(this.gmssRandom, arrayOfByte6);
          } catch (Exception exception) {
            System.out.println(exception);
          }  
      } 
    } else {
      this.minTreehash[paramInt] = getMinTreehashIndex(paramInt);
    } 
  }
  
  private int heightOfPhi(int paramInt) {
    if (paramInt == 0)
      return -1; 
    byte b = 0;
    int i = 1;
    while (paramInt % i == 0) {
      i *= 2;
      b++;
    } 
    return b - 1;
  }
  
  private void updateNextNextAuthRoot(int paramInt) {
    byte[] arrayOfByte = new byte[this.mdLength];
    arrayOfByte = this.gmssRandom.nextSeed(this.nextNextSeeds[paramInt - 1]);
    if (paramInt == this.numLayer - 1) {
      WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte, this.digestProvider.get(), this.otsIndex[paramInt]);
      this.nextNextRoot[paramInt - 1].update(this.nextNextSeeds[paramInt - 1], winternitzOTSignature.getPublicKey());
    } else {
      this.nextNextRoot[paramInt - 1].update(this.nextNextSeeds[paramInt - 1], this.nextNextLeaf[paramInt - 1].getLeaf());
      this.nextNextLeaf[paramInt - 1].initLeafCalc(this.nextNextSeeds[paramInt - 1]);
    } 
  }
  
  public int[] getIndex() {
    return this.index;
  }
  
  public int getIndex(int paramInt) {
    return this.index[paramInt];
  }
  
  public byte[][] getCurrentSeeds() {
    return Arrays.clone(this.currentSeeds);
  }
  
  public byte[][][] getCurrentAuthPaths() {
    return Arrays.clone(this.currentAuthPaths);
  }
  
  public byte[] getSubtreeRootSig(int paramInt) {
    return this.currentRootSig[paramInt];
  }
  
  public GMSSDigestProvider getName() {
    return this.digestProvider;
  }
  
  public int getNumLeafs(int paramInt) {
    return this.numLeafs[paramInt];
  }
}
