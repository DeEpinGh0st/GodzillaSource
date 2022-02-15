package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class XMSS {
  private final XMSSParameters params;
  
  private WOTSPlus wotsPlus;
  
  private SecureRandom prng;
  
  private XMSSPrivateKeyParameters privateKey;
  
  private XMSSPublicKeyParameters publicKey;
  
  public XMSS(XMSSParameters paramXMSSParameters, SecureRandom paramSecureRandom) {
    if (paramXMSSParameters == null)
      throw new NullPointerException("params == null"); 
    this.params = paramXMSSParameters;
    this.wotsPlus = paramXMSSParameters.getWOTSPlus();
    this.prng = paramSecureRandom;
  }
  
  public void generateKeys() {
    XMSSKeyPairGenerator xMSSKeyPairGenerator = new XMSSKeyPairGenerator();
    xMSSKeyPairGenerator.init(new XMSSKeyGenerationParameters(getParams(), this.prng));
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = xMSSKeyPairGenerator.generateKeyPair();
    this.privateKey = (XMSSPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    this.publicKey = (XMSSPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
  }
  
  void importState(XMSSPrivateKeyParameters paramXMSSPrivateKeyParameters, XMSSPublicKeyParameters paramXMSSPublicKeyParameters) {
    if (!Arrays.areEqual(paramXMSSPrivateKeyParameters.getRoot(), paramXMSSPublicKeyParameters.getRoot()))
      throw new IllegalStateException("root of private key and public key do not match"); 
    if (!Arrays.areEqual(paramXMSSPrivateKeyParameters.getPublicSeed(), paramXMSSPublicKeyParameters.getPublicSeed()))
      throw new IllegalStateException("public seed of private key and public key do not match"); 
    this.privateKey = paramXMSSPrivateKeyParameters;
    this.publicKey = paramXMSSPublicKeyParameters;
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
  }
  
  public void importState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("privateKey == null"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("publicKey == null"); 
    XMSSPrivateKeyParameters xMSSPrivateKeyParameters = (new XMSSPrivateKeyParameters.Builder(this.params)).withPrivateKey(paramArrayOfbyte1, getParams()).build();
    XMSSPublicKeyParameters xMSSPublicKeyParameters = (new XMSSPublicKeyParameters.Builder(this.params)).withPublicKey(paramArrayOfbyte2).build();
    if (!Arrays.areEqual(xMSSPrivateKeyParameters.getRoot(), xMSSPublicKeyParameters.getRoot()))
      throw new IllegalStateException("root of private key and public key do not match"); 
    if (!Arrays.areEqual(xMSSPrivateKeyParameters.getPublicSeed(), xMSSPublicKeyParameters.getPublicSeed()))
      throw new IllegalStateException("public seed of private key and public key do not match"); 
    this.privateKey = xMSSPrivateKeyParameters;
    this.publicKey = xMSSPublicKeyParameters;
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
  }
  
  public byte[] sign(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("message == null"); 
    XMSSSigner xMSSSigner = new XMSSSigner();
    xMSSSigner.init(true, (CipherParameters)this.privateKey);
    byte[] arrayOfByte = xMSSSigner.generateSignature(paramArrayOfbyte);
    this.privateKey = (XMSSPrivateKeyParameters)xMSSSigner.getUpdatedPrivateKey();
    importState(this.privateKey, this.publicKey);
    return arrayOfByte;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) throws ParseException {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("message == null"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("signature == null"); 
    if (paramArrayOfbyte3 == null)
      throw new NullPointerException("publicKey == null"); 
    XMSSSigner xMSSSigner = new XMSSSigner();
    xMSSSigner.init(false, (CipherParameters)(new XMSSPublicKeyParameters.Builder(getParams())).withPublicKey(paramArrayOfbyte3).build());
    return xMSSSigner.verifySignature(paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public byte[] exportPrivateKey() {
    return this.privateKey.toByteArray();
  }
  
  public byte[] exportPublicKey() {
    return this.publicKey.toByteArray();
  }
  
  protected WOTSPlusSignature wotsSign(byte[] paramArrayOfbyte, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), paramOTSHashAddress), getPublicSeed());
    return this.wotsPlus.sign(paramArrayOfbyte, paramOTSHashAddress);
  }
  
  public XMSSParameters getParams() {
    return this.params;
  }
  
  protected WOTSPlus getWOTSPlus() {
    return this.wotsPlus;
  }
  
  public byte[] getRoot() {
    return this.privateKey.getRoot();
  }
  
  protected void setRoot(byte[] paramArrayOfbyte) {
    this.privateKey = (new XMSSPrivateKeyParameters.Builder(this.params)).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(getPublicSeed()).withRoot(paramArrayOfbyte).withBDSState(this.privateKey.getBDSState()).build();
    this.publicKey = (new XMSSPublicKeyParameters.Builder(this.params)).withRoot(paramArrayOfbyte).withPublicSeed(getPublicSeed()).build();
  }
  
  public int getIndex() {
    return this.privateKey.getIndex();
  }
  
  protected void setIndex(int paramInt) {
    this.privateKey = (new XMSSPrivateKeyParameters.Builder(this.params)).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(this.privateKey.getPublicSeed()).withRoot(this.privateKey.getRoot()).withBDSState(this.privateKey.getBDSState()).build();
  }
  
  public byte[] getPublicSeed() {
    return this.privateKey.getPublicSeed();
  }
  
  protected void setPublicSeed(byte[] paramArrayOfbyte) {
    this.privateKey = (new XMSSPrivateKeyParameters.Builder(this.params)).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(paramArrayOfbyte).withRoot(getRoot()).withBDSState(this.privateKey.getBDSState()).build();
    this.publicKey = (new XMSSPublicKeyParameters.Builder(this.params)).withRoot(getRoot()).withPublicSeed(paramArrayOfbyte).build();
    this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], paramArrayOfbyte);
  }
  
  public XMSSPrivateKeyParameters getPrivateKey() {
    return this.privateKey;
  }
}
