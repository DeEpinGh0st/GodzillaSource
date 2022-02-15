package org.bouncycastle.pqc.crypto.gmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSUtil;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSVerify;
import org.bouncycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.bouncycastle.util.Arrays;

public class GMSSSigner implements MessageSigner {
  private GMSSUtil gmssUtil = new GMSSUtil();
  
  private byte[] pubKeyBytes;
  
  private Digest messDigestTrees;
  
  private int mdLength;
  
  private int numLayer;
  
  private Digest messDigestOTS;
  
  private WinternitzOTSignature ots;
  
  private GMSSDigestProvider digestProvider;
  
  private int[] index;
  
  private byte[][][] currentAuthPaths;
  
  private byte[][] subtreeRootSig;
  
  private GMSSParameters gmssPS;
  
  private GMSSRandom gmssRandom;
  
  GMSSKeyParameters key;
  
  private SecureRandom random;
  
  public GMSSSigner(GMSSDigestProvider paramGMSSDigestProvider) {
    this.digestProvider = paramGMSSDigestProvider;
    this.messDigestTrees = paramGMSSDigestProvider.get();
    this.messDigestOTS = this.messDigestTrees;
    this.mdLength = this.messDigestTrees.getDigestSize();
    this.gmssRandom = new GMSSRandom(this.messDigestTrees);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.random = parametersWithRandom.getRandom();
        this.key = (GMSSPrivateKeyParameters)parametersWithRandom.getParameters();
        initSign();
      } else {
        this.random = new SecureRandom();
        this.key = (GMSSPrivateKeyParameters)paramCipherParameters;
        initSign();
      } 
    } else {
      this.key = (GMSSPublicKeyParameters)paramCipherParameters;
      initVerify();
    } 
  }
  
  private void initSign() {
    this.messDigestTrees.reset();
    GMSSPrivateKeyParameters gMSSPrivateKeyParameters = (GMSSPrivateKeyParameters)this.key;
    if (gMSSPrivateKeyParameters.isUsed())
      throw new IllegalStateException("Private key already used"); 
    if (gMSSPrivateKeyParameters.getIndex(0) >= gMSSPrivateKeyParameters.getNumLeafs(0))
      throw new IllegalStateException("No more signatures can be generated"); 
    this.gmssPS = gMSSPrivateKeyParameters.getParameters();
    this.numLayer = this.gmssPS.getNumOfLayers();
    byte[] arrayOfByte1 = gMSSPrivateKeyParameters.getCurrentSeeds()[this.numLayer - 1];
    byte[] arrayOfByte2 = new byte[this.mdLength];
    byte[] arrayOfByte3 = new byte[this.mdLength];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, this.mdLength);
    arrayOfByte2 = this.gmssRandom.nextSeed(arrayOfByte3);
    this.ots = new WinternitzOTSignature(arrayOfByte2, this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[this.numLayer - 1]);
    byte[][][] arrayOfByte = gMSSPrivateKeyParameters.getCurrentAuthPaths();
    this.currentAuthPaths = new byte[this.numLayer][][];
    for (byte b1 = 0; b1 < this.numLayer; b1++) {
      this.currentAuthPaths[b1] = new byte[(arrayOfByte[b1]).length][this.mdLength];
      for (byte b = 0; b < (arrayOfByte[b1]).length; b++)
        System.arraycopy(arrayOfByte[b1][b], 0, this.currentAuthPaths[b1][b], 0, this.mdLength); 
    } 
    this.index = new int[this.numLayer];
    System.arraycopy(gMSSPrivateKeyParameters.getIndex(), 0, this.index, 0, this.numLayer);
    this.subtreeRootSig = new byte[this.numLayer - 1][];
    for (byte b2 = 0; b2 < this.numLayer - 1; b2++) {
      byte[] arrayOfByte4 = gMSSPrivateKeyParameters.getSubtreeRootSig(b2);
      this.subtreeRootSig[b2] = new byte[arrayOfByte4.length];
      System.arraycopy(arrayOfByte4, 0, this.subtreeRootSig[b2], 0, arrayOfByte4.length);
    } 
    gMSSPrivateKeyParameters.markUsed();
  }
  
  public byte[] generateSignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = new byte[this.mdLength];
    arrayOfByte1 = this.ots.getSignature(paramArrayOfbyte);
    byte[] arrayOfByte2 = this.gmssUtil.concatenateArray(this.currentAuthPaths[this.numLayer - 1]);
    byte[] arrayOfByte3 = this.gmssUtil.intToBytesLittleEndian(this.index[this.numLayer - 1]);
    byte[] arrayOfByte4 = new byte[arrayOfByte3.length + arrayOfByte1.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 0, arrayOfByte3.length);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte4, arrayOfByte3.length, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, arrayOfByte3.length + arrayOfByte1.length, arrayOfByte2.length);
    byte[] arrayOfByte5 = new byte[0];
    for (int i = this.numLayer - 1 - 1; i >= 0; i--) {
      arrayOfByte2 = this.gmssUtil.concatenateArray(this.currentAuthPaths[i]);
      arrayOfByte3 = this.gmssUtil.intToBytesLittleEndian(this.index[i]);
      byte[] arrayOfByte = new byte[arrayOfByte5.length];
      System.arraycopy(arrayOfByte5, 0, arrayOfByte, 0, arrayOfByte5.length);
      arrayOfByte5 = new byte[arrayOfByte.length + arrayOfByte3.length + (this.subtreeRootSig[i]).length + arrayOfByte2.length];
      System.arraycopy(arrayOfByte, 0, arrayOfByte5, 0, arrayOfByte.length);
      System.arraycopy(arrayOfByte3, 0, arrayOfByte5, arrayOfByte.length, arrayOfByte3.length);
      System.arraycopy(this.subtreeRootSig[i], 0, arrayOfByte5, arrayOfByte.length + arrayOfByte3.length, (this.subtreeRootSig[i]).length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte5, arrayOfByte.length + arrayOfByte3.length + (this.subtreeRootSig[i]).length, arrayOfByte2.length);
    } 
    byte[] arrayOfByte6 = new byte[arrayOfByte4.length + arrayOfByte5.length];
    System.arraycopy(arrayOfByte4, 0, arrayOfByte6, 0, arrayOfByte4.length);
    System.arraycopy(arrayOfByte5, 0, arrayOfByte6, arrayOfByte4.length, arrayOfByte5.length);
    return arrayOfByte6;
  }
  
  private void initVerify() {
    this.messDigestTrees.reset();
    GMSSPublicKeyParameters gMSSPublicKeyParameters = (GMSSPublicKeyParameters)this.key;
    this.pubKeyBytes = gMSSPublicKeyParameters.getPublicKey();
    this.gmssPS = gMSSPublicKeyParameters.getParameters();
    this.numLayer = this.gmssPS.getNumOfLayers();
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    boolean bool = false;
    this.messDigestOTS.reset();
    byte[] arrayOfByte = paramArrayOfbyte1;
    int i = 0;
    for (int j = this.numLayer - 1; j >= 0; j--) {
      WinternitzOTSVerify winternitzOTSVerify = new WinternitzOTSVerify(this.digestProvider.get(), this.gmssPS.getWinternitzParameter()[j]);
      int k = winternitzOTSVerify.getSignatureLength();
      paramArrayOfbyte1 = arrayOfByte;
      int m = this.gmssUtil.bytesToIntLittleEndian(paramArrayOfbyte2, i);
      i += true;
      byte[] arrayOfByte1 = new byte[k];
      System.arraycopy(paramArrayOfbyte2, i, arrayOfByte1, 0, k);
      i += k;
      byte[] arrayOfByte2 = winternitzOTSVerify.Verify(paramArrayOfbyte1, arrayOfByte1);
      if (arrayOfByte2 == null) {
        System.err.println("OTS Public Key is null in GMSSSignature.verify");
        return false;
      } 
      byte[][] arrayOfByte3 = new byte[this.gmssPS.getHeightOfTrees()[j]][this.mdLength];
      int n;
      for (n = 0; n < arrayOfByte3.length; n++) {
        System.arraycopy(paramArrayOfbyte2, i, arrayOfByte3[n], 0, this.mdLength);
        i += this.mdLength;
      } 
      arrayOfByte = new byte[this.mdLength];
      arrayOfByte = arrayOfByte2;
      n = 1 << arrayOfByte3.length;
      n += m;
      for (byte b = 0; b < arrayOfByte3.length; b++) {
        byte[] arrayOfByte4 = new byte[this.mdLength << 1];
        if (n % 2 == 0) {
          System.arraycopy(arrayOfByte, 0, arrayOfByte4, 0, this.mdLength);
          System.arraycopy(arrayOfByte3[b], 0, arrayOfByte4, this.mdLength, this.mdLength);
          n /= 2;
        } else {
          System.arraycopy(arrayOfByte3[b], 0, arrayOfByte4, 0, this.mdLength);
          System.arraycopy(arrayOfByte, 0, arrayOfByte4, this.mdLength, arrayOfByte.length);
          n = (n - 1) / 2;
        } 
        this.messDigestTrees.update(arrayOfByte4, 0, arrayOfByte4.length);
        arrayOfByte = new byte[this.messDigestTrees.getDigestSize()];
        this.messDigestTrees.doFinal(arrayOfByte, 0);
      } 
    } 
    if (Arrays.areEqual(this.pubKeyBytes, arrayOfByte))
      bool = true; 
    return bool;
  }
}
