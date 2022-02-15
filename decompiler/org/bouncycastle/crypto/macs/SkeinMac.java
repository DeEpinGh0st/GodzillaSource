package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;

public class SkeinMac implements Mac {
  public static final int SKEIN_256 = 256;
  
  public static final int SKEIN_512 = 512;
  
  public static final int SKEIN_1024 = 1024;
  
  private SkeinEngine engine;
  
  public SkeinMac(int paramInt1, int paramInt2) {
    this.engine = new SkeinEngine(paramInt1, paramInt2);
  }
  
  public SkeinMac(SkeinMac paramSkeinMac) {
    this.engine = new SkeinEngine(paramSkeinMac.engine);
  }
  
  public String getAlgorithmName() {
    return "Skein-MAC-" + (this.engine.getBlockSize() * 8) + "-" + (this.engine.getOutputSize() * 8);
  }
  
  public void init(CipherParameters paramCipherParameters) throws IllegalArgumentException {
    SkeinParameters skeinParameters;
    if (paramCipherParameters instanceof SkeinParameters) {
      skeinParameters = (SkeinParameters)paramCipherParameters;
    } else if (paramCipherParameters instanceof KeyParameter) {
      skeinParameters = (new SkeinParameters.Builder()).setKey(((KeyParameter)paramCipherParameters).getKey()).build();
    } else {
      throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - " + paramCipherParameters.getClass().getName());
    } 
    if (skeinParameters.getKey() == null)
      throw new IllegalArgumentException("Skein MAC requires a key parameter."); 
    this.engine.init(skeinParameters);
  }
  
  public int getMacSize() {
    return this.engine.getOutputSize();
  }
  
  public void reset() {
    this.engine.reset();
  }
  
  public void update(byte paramByte) {
    this.engine.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.engine.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    return this.engine.doFinal(paramArrayOfbyte, paramInt);
  }
}
