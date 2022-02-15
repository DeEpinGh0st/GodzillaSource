package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public final class XMSSMT {
  private XMSSMTParameters params;
  
  private XMSSParameters xmssParams;
  
  private SecureRandom prng;
  
  private XMSSMTPrivateKeyParameters privateKey;
  
  private XMSSMTPublicKeyParameters publicKey;
  
  public XMSSMT(XMSSMTParameters paramXMSSMTParameters, SecureRandom paramSecureRandom) {
    if (paramXMSSMTParameters == null)
      throw new NullPointerException("params == null"); 
    this.params = paramXMSSMTParameters;
    this.xmssParams = paramXMSSMTParameters.getXMSSParameters();
    this.prng = paramSecureRandom;
    this.privateKey = (new XMSSMTPrivateKeyParameters.Builder(paramXMSSMTParameters)).build();
    this.publicKey = (new XMSSMTPublicKeyParameters.Builder(paramXMSSMTParameters)).build();
  }
  
  public void generateKeys() {
    XMSSMTKeyPairGenerator xMSSMTKeyPairGenerator = new XMSSMTKeyPairGenerator();
    xMSSMTKeyPairGenerator.init(new XMSSMTKeyGenerationParameters(getParams(), this.prng));
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = xMSSMTKeyPairGenerator.generateKeyPair();
    this.privateKey = (XMSSMTPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    this.publicKey = (XMSSMTPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    importState(this.privateKey, this.publicKey);
  }
  
  private void importState(XMSSMTPrivateKeyParameters paramXMSSMTPrivateKeyParameters, XMSSMTPublicKeyParameters paramXMSSMTPublicKeyParameters) {
    this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    this.privateKey = paramXMSSMTPrivateKeyParameters;
    this.publicKey = paramXMSSMTPublicKeyParameters;
  }
  
  public void importState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("privateKey == null"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("publicKey == null"); 
    XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters = (new XMSSMTPrivateKeyParameters.Builder(this.params)).withPrivateKey(paramArrayOfbyte1, this.xmssParams).build();
    XMSSMTPublicKeyParameters xMSSMTPublicKeyParameters = (new XMSSMTPublicKeyParameters.Builder(this.params)).withPublicKey(paramArrayOfbyte2).build();
    if (!Arrays.areEqual(xMSSMTPrivateKeyParameters.getRoot(), xMSSMTPublicKeyParameters.getRoot()))
      throw new IllegalStateException("root of private key and public key do not match"); 
    if (!Arrays.areEqual(xMSSMTPrivateKeyParameters.getPublicSeed(), xMSSMTPublicKeyParameters.getPublicSeed()))
      throw new IllegalStateException("public seed of private key and public key do not match"); 
    this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], xMSSMTPrivateKeyParameters.getPublicSeed());
    this.privateKey = xMSSMTPrivateKeyParameters;
    this.publicKey = xMSSMTPublicKeyParameters;
  }
  
  public byte[] sign(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("message == null"); 
    XMSSMTSigner xMSSMTSigner = new XMSSMTSigner();
    xMSSMTSigner.init(true, (CipherParameters)this.privateKey);
    byte[] arrayOfByte = xMSSMTSigner.generateSignature(paramArrayOfbyte);
    this.privateKey = (XMSSMTPrivateKeyParameters)xMSSMTSigner.getUpdatedPrivateKey();
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
    XMSSMTSigner xMSSMTSigner = new XMSSMTSigner();
    xMSSMTSigner.init(false, (CipherParameters)(new XMSSMTPublicKeyParameters.Builder(getParams())).withPublicKey(paramArrayOfbyte3).build());
    return xMSSMTSigner.verifySignature(paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public byte[] exportPrivateKey() {
    return this.privateKey.toByteArray();
  }
  
  public byte[] exportPublicKey() {
    return this.publicKey.toByteArray();
  }
  
  public XMSSMTParameters getParams() {
    return this.params;
  }
  
  public byte[] getPublicSeed() {
    return this.privateKey.getPublicSeed();
  }
  
  protected XMSSParameters getXMSS() {
    return this.xmssParams;
  }
}
