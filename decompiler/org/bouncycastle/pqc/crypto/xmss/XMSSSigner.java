package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

public class XMSSSigner implements StateAwareMessageSigner {
  private XMSSPrivateKeyParameters privateKey;
  
  private XMSSPrivateKeyParameters nextKeyGenerator;
  
  private XMSSPublicKeyParameters publicKey;
  
  private XMSSParameters params;
  
  private KeyedHashFunctions khf;
  
  private boolean initSign;
  
  private boolean hasGenerated;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      this.initSign = true;
      this.hasGenerated = false;
      this.privateKey = (XMSSPrivateKeyParameters)paramCipherParameters;
      this.nextKeyGenerator = this.privateKey;
      this.params = this.privateKey.getParameters();
      this.khf = this.params.getWOTSPlus().getKhf();
    } else {
      this.initSign = false;
      this.publicKey = (XMSSPublicKeyParameters)paramCipherParameters;
      this.params = this.publicKey.getParameters();
      this.khf = this.params.getWOTSPlus().getKhf();
    } 
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
    if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty())
      throw new IllegalStateException("not initialized"); 
    int i = this.privateKey.getIndex();
    if (!XMSSUtil.isIndexValid(this.params.getHeight(), i))
      throw new IllegalStateException("index out of bounds"); 
    byte[] arrayOfByte1 = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(i, 32));
    byte[] arrayOfByte2 = Arrays.concatenate(arrayOfByte1, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(i, this.params.getDigestSize()));
    byte[] arrayOfByte3 = this.khf.HMsg(arrayOfByte2, paramArrayOfbyte);
    OTSHashAddress oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withOTSAddress(i).build();
    WOTSPlusSignature wOTSPlusSignature = wotsSign(arrayOfByte3, oTSHashAddress);
    XMSSSignature xMSSSignature = (XMSSSignature)(new XMSSSignature.Builder(this.params)).withIndex(i).withRandom(arrayOfByte1).withWOTSPlusSignature(wOTSPlusSignature).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build();
    this.hasGenerated = true;
    if (this.nextKeyGenerator != null) {
      this.privateKey = this.nextKeyGenerator.getNextKey();
      this.nextKeyGenerator = this.privateKey;
    } else {
      this.privateKey = null;
    } 
    return xMSSSignature.toByteArray();
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    XMSSSignature xMSSSignature = (new XMSSSignature.Builder(this.params)).withSignature(paramArrayOfbyte2).build();
    int i = xMSSSignature.getIndex();
    this.params.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
    byte[] arrayOfByte1 = Arrays.concatenate(xMSSSignature.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(i, this.params.getDigestSize()));
    byte[] arrayOfByte2 = this.khf.HMsg(arrayOfByte1, paramArrayOfbyte1);
    int j = this.params.getHeight();
    int k = XMSSUtil.getLeafIndex(i, j);
    OTSHashAddress oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withOTSAddress(i).build();
    XMSSNode xMSSNode = XMSSVerifierUtil.getRootNodeFromSignature(this.params.getWOTSPlus(), j, arrayOfByte2, xMSSSignature, oTSHashAddress, k);
    return Arrays.constantTimeAreEqual(xMSSNode.getValue(), this.publicKey.getRoot());
  }
  
  public AsymmetricKeyParameter getUpdatedPrivateKey() {
    if (this.hasGenerated) {
      XMSSPrivateKeyParameters xMSSPrivateKeyParameters1 = this.privateKey;
      this.privateKey = null;
      this.nextKeyGenerator = null;
      return xMSSPrivateKeyParameters1;
    } 
    XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.nextKeyGenerator.getNextKey();
    this.nextKeyGenerator = null;
    return xMSSPrivateKeyParameters;
  }
  
  private WOTSPlusSignature wotsSign(byte[] paramArrayOfbyte, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    this.params.getWOTSPlus().importKeys(this.params.getWOTSPlus().getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), paramOTSHashAddress), this.privateKey.getPublicSeed());
    return this.params.getWOTSPlus().sign(paramArrayOfbyte, paramOTSHashAddress);
  }
}
