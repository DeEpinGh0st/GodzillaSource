package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

public class XMSSMTSigner implements StateAwareMessageSigner {
  private XMSSMTPrivateKeyParameters privateKey;
  
  private XMSSMTPrivateKeyParameters nextKeyGenerator;
  
  private XMSSMTPublicKeyParameters publicKey;
  
  private XMSSMTParameters params;
  
  private XMSSParameters xmssParams;
  
  private WOTSPlus wotsPlus;
  
  private boolean hasGenerated;
  
  private boolean initSign;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      this.initSign = true;
      this.hasGenerated = false;
      this.privateKey = (XMSSMTPrivateKeyParameters)paramCipherParameters;
      this.nextKeyGenerator = this.privateKey;
      this.params = this.privateKey.getParameters();
      this.xmssParams = this.params.getXMSSParameters();
    } else {
      this.initSign = false;
      this.publicKey = (XMSSMTPublicKeyParameters)paramCipherParameters;
      this.params = this.publicKey.getParameters();
      this.xmssParams = this.params.getXMSSParameters();
    } 
    this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(this.params.getDigest()));
  }
  
  public byte[] generateSignature(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("message == null"); 
    if (this.initSign) {
      if (this.privateKey == null)
        throw new IllegalStateException("signing key no longer usable"); 
    } else {
      throw new IllegalStateException("signer not initialized for signature generation");
    } 
    if (this.privateKey.getBDSState().isEmpty())
      throw new IllegalStateException("not initialized"); 
    BDSStateMap bDSStateMap = this.privateKey.getBDSState();
    long l1 = this.privateKey.getIndex();
    int i = this.params.getHeight();
    int j = this.xmssParams.getHeight();
    if (!XMSSUtil.isIndexValid(i, l1))
      throw new IllegalStateException("index out of bounds"); 
    byte[] arrayOfByte1 = this.wotsPlus.getKhf().PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(l1, 32));
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(l1, this.params.getDigestSize()));
    byte[] arrayOfByte3 = this.wotsPlus.getKhf().HMsg(arrayOfByte2, paramArrayOfbyte);
    XMSSMTSignature xMSSMTSignature = (new XMSSMTSignature.Builder(this.params)).withIndex(l1).withRandom(arrayOfByte1).build();
    long l2 = XMSSUtil.getTreeIndex(l1, j);
    int k = XMSSUtil.getLeafIndex(l1, j);
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    OTSHashAddress oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withTreeAddress(l2).withOTSAddress(k).build();
    if (bDSStateMap.get(0) == null || k == 0)
      bDSStateMap.put(0, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), oTSHashAddress)); 
    WOTSPlusSignature wOTSPlusSignature = wotsSign(arrayOfByte3, oTSHashAddress);
    XMSSReducedSignature xMSSReducedSignature = (new XMSSReducedSignature.Builder(this.xmssParams)).withWOTSPlusSignature(wOTSPlusSignature).withAuthPath(bDSStateMap.get(0).getAuthenticationPath()).build();
    xMSSMTSignature.getReducedSignatures().add(xMSSReducedSignature);
    for (byte b = 1; b < this.params.getLayers(); b++) {
      XMSSNode xMSSNode = bDSStateMap.get(b - 1).getRoot();
      k = XMSSUtil.getLeafIndex(l2, j);
      l2 = XMSSUtil.getTreeIndex(l2, j);
      oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(b).withTreeAddress(l2).withOTSAddress(k).build();
      wOTSPlusSignature = wotsSign(xMSSNode.getValue(), oTSHashAddress);
      if (bDSStateMap.get(b) == null || XMSSUtil.isNewBDSInitNeeded(l1, j, b))
        bDSStateMap.put(b, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), oTSHashAddress)); 
      xMSSReducedSignature = (new XMSSReducedSignature.Builder(this.xmssParams)).withWOTSPlusSignature(wOTSPlusSignature).withAuthPath(bDSStateMap.get(b).getAuthenticationPath()).build();
      xMSSMTSignature.getReducedSignatures().add(xMSSReducedSignature);
    } 
    this.hasGenerated = true;
    if (this.nextKeyGenerator != null) {
      this.privateKey = this.nextKeyGenerator.getNextKey();
      this.nextKeyGenerator = this.privateKey;
    } else {
      this.privateKey = null;
    } 
    return xMSSMTSignature.toByteArray();
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("message == null"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("signature == null"); 
    if (this.publicKey == null)
      throw new NullPointerException("publicKey == null"); 
    XMSSMTSignature xMSSMTSignature = (new XMSSMTSignature.Builder(this.params)).withSignature(paramArrayOfbyte2).build();
    byte[] arrayOfByte1 = Arrays.concatenate(xMSSMTSignature.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(xMSSMTSignature.getIndex(), this.params.getDigestSize()));
    byte[] arrayOfByte2 = this.wotsPlus.getKhf().HMsg(arrayOfByte1, paramArrayOfbyte1);
    long l1 = xMSSMTSignature.getIndex();
    int i = this.xmssParams.getHeight();
    long l2 = XMSSUtil.getTreeIndex(l1, i);
    int j = XMSSUtil.getLeafIndex(l1, i);
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
    OTSHashAddress oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withTreeAddress(l2).withOTSAddress(j).build();
    XMSSReducedSignature xMSSReducedSignature = xMSSMTSignature.getReducedSignatures().get(0);
    XMSSNode xMSSNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, i, arrayOfByte2, xMSSReducedSignature, oTSHashAddress, j);
    for (byte b = 1; b < this.params.getLayers(); b++) {
      xMSSReducedSignature = xMSSMTSignature.getReducedSignatures().get(b);
      j = XMSSUtil.getLeafIndex(l2, i);
      l2 = XMSSUtil.getTreeIndex(l2, i);
      oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(b).withTreeAddress(l2).withOTSAddress(j).build();
      xMSSNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, i, xMSSNode.getValue(), xMSSReducedSignature, oTSHashAddress, j);
    } 
    return Arrays.constantTimeAreEqual(xMSSNode.getValue(), this.publicKey.getRoot());
  }
  
  private WOTSPlusSignature wotsSign(byte[] paramArrayOfbyte, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), paramOTSHashAddress), this.privateKey.getPublicSeed());
    return this.wotsPlus.sign(paramArrayOfbyte, paramOTSHashAddress);
  }
  
  public AsymmetricKeyParameter getUpdatedPrivateKey() {
    if (this.hasGenerated) {
      XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters1 = this.privateKey;
      this.privateKey = null;
      this.nextKeyGenerator = null;
      return xMSSMTPrivateKeyParameters1;
    } 
    XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters = this.nextKeyGenerator.getNextKey();
    this.nextKeyGenerator = null;
    return xMSSMTPrivateKeyParameters;
  }
}
