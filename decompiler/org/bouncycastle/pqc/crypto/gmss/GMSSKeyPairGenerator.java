package org.bouncycastle.pqc.crypto.gmss;

import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSVerify;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;

public class GMSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private GMSSRandom gmssRandom;
  
  private Digest messDigestTree;
  
  private byte[][] currentSeeds;
  
  private byte[][] nextNextSeeds;
  
  private byte[][] currentRootSigs;
  
  private GMSSDigestProvider digestProvider;
  
  private int mdLength;
  
  private int numLayer;
  
  private boolean initialized = false;
  
  private GMSSParameters gmssPS;
  
  private int[] heightOfTrees;
  
  private int[] otsIndex;
  
  private int[] K;
  
  private GMSSKeyGenerationParameters gmssParams;
  
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.3";
  
  public GMSSKeyPairGenerator(GMSSDigestProvider paramGMSSDigestProvider) {
    this.digestProvider = paramGMSSDigestProvider;
    this.messDigestTree = paramGMSSDigestProvider.get();
    this.mdLength = this.messDigestTree.getDigestSize();
    this.gmssRandom = new GMSSRandom(this.messDigestTree);
  }
  
  private AsymmetricCipherKeyPair genKeyPair() {
    if (!this.initialized)
      initializeDefault(); 
    byte[][][] arrayOfByte1 = new byte[this.numLayer][][];
    byte[][][] arrayOfByte2 = new byte[this.numLayer - 1][][];
    Treehash[][] arrayOfTreehash1 = new Treehash[this.numLayer][];
    Treehash[][] arrayOfTreehash2 = new Treehash[this.numLayer - 1][];
    Vector[] arrayOfVector1 = new Vector[this.numLayer];
    Vector[] arrayOfVector2 = new Vector[this.numLayer - 1];
    Vector[][] arrayOfVector3 = new Vector[this.numLayer][];
    Vector[][] arrayOfVector4 = new Vector[this.numLayer - 1][];
    for (byte b = 0; b < this.numLayer; b++) {
      arrayOfByte1[b] = new byte[this.heightOfTrees[b]][this.mdLength];
      arrayOfTreehash1[b] = new Treehash[this.heightOfTrees[b] - this.K[b]];
      if (b > 0) {
        arrayOfByte2[b - 1] = new byte[this.heightOfTrees[b]][this.mdLength];
        arrayOfTreehash2[b - 1] = new Treehash[this.heightOfTrees[b] - this.K[b]];
      } 
      arrayOfVector1[b] = new Vector();
      if (b > 0)
        arrayOfVector2[b - 1] = new Vector(); 
    } 
    byte[][] arrayOfByte3 = new byte[this.numLayer][this.mdLength];
    byte[][] arrayOfByte4 = new byte[this.numLayer - 1][this.mdLength];
    byte[][] arrayOfByte5 = new byte[this.numLayer][this.mdLength];
    int i;
    for (i = 0; i < this.numLayer; i++)
      System.arraycopy(this.currentSeeds[i], 0, arrayOfByte5[i], 0, this.mdLength); 
    this.currentRootSigs = new byte[this.numLayer - 1][this.mdLength];
    for (i = this.numLayer - 1; i >= 0; i--) {
      GMSSRootCalc gMSSRootCalc = new GMSSRootCalc(this.heightOfTrees[i], this.K[i], this.digestProvider);
      try {
        if (i == this.numLayer - 1) {
          gMSSRootCalc = generateCurrentAuthpathAndRoot(null, arrayOfVector1[i], arrayOfByte5[i], i);
        } else {
          gMSSRootCalc = generateCurrentAuthpathAndRoot(arrayOfByte3[i + 1], arrayOfVector1[i], arrayOfByte5[i], i);
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      for (byte b1 = 0; b1 < this.heightOfTrees[i]; b1++)
        System.arraycopy(gMSSRootCalc.getAuthPath()[b1], 0, arrayOfByte1[i][b1], 0, this.mdLength); 
      arrayOfVector3[i] = gMSSRootCalc.getRetain();
      arrayOfTreehash1[i] = gMSSRootCalc.getTreehash();
      System.arraycopy(gMSSRootCalc.getRoot(), 0, arrayOfByte3[i], 0, this.mdLength);
    } 
    for (i = this.numLayer - 2; i >= 0; i--) {
      GMSSRootCalc gMSSRootCalc = generateNextAuthpathAndRoot(arrayOfVector2[i], arrayOfByte5[i + 1], i + 1);
      for (byte b1 = 0; b1 < this.heightOfTrees[i + 1]; b1++)
        System.arraycopy(gMSSRootCalc.getAuthPath()[b1], 0, arrayOfByte2[i][b1], 0, this.mdLength); 
      arrayOfVector4[i] = gMSSRootCalc.getRetain();
      arrayOfTreehash2[i] = gMSSRootCalc.getTreehash();
      System.arraycopy(gMSSRootCalc.getRoot(), 0, arrayOfByte4[i], 0, this.mdLength);
      System.arraycopy(arrayOfByte5[i + 1], 0, this.nextNextSeeds[i], 0, this.mdLength);
    } 
    GMSSPublicKeyParameters gMSSPublicKeyParameters = new GMSSPublicKeyParameters(arrayOfByte3[0], this.gmssPS);
    GMSSPrivateKeyParameters gMSSPrivateKeyParameters = new GMSSPrivateKeyParameters(this.currentSeeds, this.nextNextSeeds, arrayOfByte1, arrayOfByte2, arrayOfTreehash1, arrayOfTreehash2, arrayOfVector1, arrayOfVector2, arrayOfVector3, arrayOfVector4, arrayOfByte4, this.currentRootSigs, this.gmssPS, this.digestProvider);
    return new AsymmetricCipherKeyPair(gMSSPublicKeyParameters, gMSSPrivateKeyParameters);
  }
  
  private GMSSRootCalc generateCurrentAuthpathAndRoot(byte[] paramArrayOfbyte1, Vector paramVector, byte[] paramArrayOfbyte2, int paramInt) {
    byte[] arrayOfByte1 = new byte[this.mdLength];
    byte[] arrayOfByte2 = new byte[this.mdLength];
    arrayOfByte2 = this.gmssRandom.nextSeed(paramArrayOfbyte2);
    GMSSRootCalc gMSSRootCalc = new GMSSRootCalc(this.heightOfTrees[paramInt], this.K[paramInt], this.digestProvider);
    gMSSRootCalc.initialize(paramVector);
    if (paramInt == this.numLayer - 1) {
      WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte2, this.digestProvider.get(), this.otsIndex[paramInt]);
      arrayOfByte1 = winternitzOTSignature.getPublicKey();
    } else {
      WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte2, this.digestProvider.get(), this.otsIndex[paramInt]);
      this.currentRootSigs[paramInt] = winternitzOTSignature.getSignature(paramArrayOfbyte1);
      WinternitzOTSVerify winternitzOTSVerify = new WinternitzOTSVerify(this.digestProvider.get(), this.otsIndex[paramInt]);
      arrayOfByte1 = winternitzOTSVerify.Verify(paramArrayOfbyte1, this.currentRootSigs[paramInt]);
    } 
    gMSSRootCalc.update(arrayOfByte1);
    int i = 3;
    byte b1 = 0;
    for (byte b2 = 1; b2 < 1 << this.heightOfTrees[paramInt]; b2++) {
      if (b2 == i && b1 < this.heightOfTrees[paramInt] - this.K[paramInt]) {
        gMSSRootCalc.initializeTreehashSeed(paramArrayOfbyte2, b1);
        i *= 2;
        b1++;
      } 
      arrayOfByte2 = this.gmssRandom.nextSeed(paramArrayOfbyte2);
      WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte2, this.digestProvider.get(), this.otsIndex[paramInt]);
      gMSSRootCalc.update(winternitzOTSignature.getPublicKey());
    } 
    if (gMSSRootCalc.wasFinished())
      return gMSSRootCalc; 
    System.err.println("Baum noch nicht fertig konstruiert!!!");
    return null;
  }
  
  private GMSSRootCalc generateNextAuthpathAndRoot(Vector paramVector, byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[this.numLayer];
    GMSSRootCalc gMSSRootCalc = new GMSSRootCalc(this.heightOfTrees[paramInt], this.K[paramInt], this.digestProvider);
    gMSSRootCalc.initialize(paramVector);
    int i = 3;
    byte b1 = 0;
    for (byte b2 = 0; b2 < 1 << this.heightOfTrees[paramInt]; b2++) {
      if (b2 == i && b1 < this.heightOfTrees[paramInt] - this.K[paramInt]) {
        gMSSRootCalc.initializeTreehashSeed(paramArrayOfbyte, b1);
        i *= 2;
        b1++;
      } 
      arrayOfByte = this.gmssRandom.nextSeed(paramArrayOfbyte);
      WinternitzOTSignature winternitzOTSignature = new WinternitzOTSignature(arrayOfByte, this.digestProvider.get(), this.otsIndex[paramInt]);
      gMSSRootCalc.update(winternitzOTSignature.getPublicKey());
    } 
    if (gMSSRootCalc.wasFinished())
      return gMSSRootCalc; 
    System.err.println("N�chster Baum noch nicht fertig konstruiert!!!");
    return null;
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    GMSSKeyGenerationParameters gMSSKeyGenerationParameters;
    if (paramInt <= 10) {
      int[] arrayOfInt1 = { 10 };
      int[] arrayOfInt2 = { 3 };
      int[] arrayOfInt3 = { 2 };
      gMSSKeyGenerationParameters = new GMSSKeyGenerationParameters(paramSecureRandom, new GMSSParameters(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3));
    } else if (paramInt <= 20) {
      int[] arrayOfInt1 = { 10, 10 };
      int[] arrayOfInt2 = { 5, 4 };
      int[] arrayOfInt3 = { 2, 2 };
      gMSSKeyGenerationParameters = new GMSSKeyGenerationParameters(paramSecureRandom, new GMSSParameters(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3));
    } else {
      int[] arrayOfInt1 = { 10, 10, 10, 10 };
      int[] arrayOfInt2 = { 9, 9, 9, 3 };
      int[] arrayOfInt3 = { 2, 2, 2, 2 };
      gMSSKeyGenerationParameters = new GMSSKeyGenerationParameters(paramSecureRandom, new GMSSParameters(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3));
    } 
    initialize(gMSSKeyGenerationParameters);
  }
  
  public void initialize(KeyGenerationParameters paramKeyGenerationParameters) {
    this.gmssParams = (GMSSKeyGenerationParameters)paramKeyGenerationParameters;
    this.gmssPS = new GMSSParameters(this.gmssParams.getParameters().getNumOfLayers(), this.gmssParams.getParameters().getHeightOfTrees(), this.gmssParams.getParameters().getWinternitzParameter(), this.gmssParams.getParameters().getK());
    this.numLayer = this.gmssPS.getNumOfLayers();
    this.heightOfTrees = this.gmssPS.getHeightOfTrees();
    this.otsIndex = this.gmssPS.getWinternitzParameter();
    this.K = this.gmssPS.getK();
    this.currentSeeds = new byte[this.numLayer][this.mdLength];
    this.nextNextSeeds = new byte[this.numLayer - 1][this.mdLength];
    SecureRandom secureRandom = new SecureRandom();
    for (byte b = 0; b < this.numLayer; b++) {
      secureRandom.nextBytes(this.currentSeeds[b]);
      this.gmssRandom.nextSeed(this.currentSeeds[b]);
    } 
    this.initialized = true;
  }
  
  private void initializeDefault() {
    int[] arrayOfInt1 = { 10, 10, 10, 10 };
    int[] arrayOfInt2 = { 3, 3, 3, 3 };
    int[] arrayOfInt3 = { 2, 2, 2, 2 };
    GMSSKeyGenerationParameters gMSSKeyGenerationParameters = new GMSSKeyGenerationParameters(new SecureRandom(), new GMSSParameters(arrayOfInt1.length, arrayOfInt1, arrayOfInt2, arrayOfInt3));
    initialize(gMSSKeyGenerationParameters);
  }
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    initialize(paramKeyGenerationParameters);
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    return genKeyPair();
  }
}
