package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSKeyPairGenerator {
  private XMSSParameters params;
  
  private SecureRandom prng;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    XMSSKeyGenerationParameters xMSSKeyGenerationParameters = (XMSSKeyGenerationParameters)paramKeyGenerationParameters;
    this.prng = xMSSKeyGenerationParameters.getRandom();
    this.params = xMSSKeyGenerationParameters.getParameters();
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    XMSSPrivateKeyParameters xMSSPrivateKeyParameters = generatePrivateKey(this.params, this.prng);
    XMSSNode xMSSNode = xMSSPrivateKeyParameters.getBDSState().getRoot();
    xMSSPrivateKeyParameters = (new XMSSPrivateKeyParameters.Builder(this.params)).withSecretKeySeed(xMSSPrivateKeyParameters.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKeyParameters.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKeyParameters.getPublicSeed()).withRoot(xMSSNode.getValue()).withBDSState(xMSSPrivateKeyParameters.getBDSState()).build();
    XMSSPublicKeyParameters xMSSPublicKeyParameters = (new XMSSPublicKeyParameters.Builder(this.params)).withRoot(xMSSNode.getValue()).withPublicSeed(xMSSPrivateKeyParameters.getPublicSeed()).build();
    return new AsymmetricCipherKeyPair(xMSSPublicKeyParameters, xMSSPrivateKeyParameters);
  }
  
  private XMSSPrivateKeyParameters generatePrivateKey(XMSSParameters paramXMSSParameters, SecureRandom paramSecureRandom) {
    int i = paramXMSSParameters.getDigestSize();
    byte[] arrayOfByte1 = new byte[i];
    paramSecureRandom.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[i];
    paramSecureRandom.nextBytes(arrayOfByte2);
    byte[] arrayOfByte3 = new byte[i];
    paramSecureRandom.nextBytes(arrayOfByte3);
    return (new XMSSPrivateKeyParameters.Builder(paramXMSSParameters)).withSecretKeySeed(arrayOfByte1).withSecretKeyPRF(arrayOfByte2).withPublicSeed(arrayOfByte3).withBDSState(new BDS(paramXMSSParameters, arrayOfByte3, arrayOfByte1, (OTSHashAddress)(new OTSHashAddress.Builder()).build())).build();
  }
}
