package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Memoable;

public class SkeinDigest implements ExtendedDigest, Memoable {
  public static final int SKEIN_256 = 256;
  
  public static final int SKEIN_512 = 512;
  
  public static final int SKEIN_1024 = 1024;
  
  private SkeinEngine engine;
  
  public SkeinDigest(int paramInt1, int paramInt2) {
    this.engine = new SkeinEngine(paramInt1, paramInt2);
    init(null);
  }
  
  public SkeinDigest(SkeinDigest paramSkeinDigest) {
    this.engine = new SkeinEngine(paramSkeinDigest.engine);
  }
  
  public void reset(Memoable paramMemoable) {
    SkeinDigest skeinDigest = (SkeinDigest)paramMemoable;
    this.engine.reset(skeinDigest.engine);
  }
  
  public Memoable copy() {
    return new SkeinDigest(this);
  }
  
  public String getAlgorithmName() {
    return "Skein-" + (this.engine.getBlockSize() * 8) + "-" + (this.engine.getOutputSize() * 8);
  }
  
  public int getDigestSize() {
    return this.engine.getOutputSize();
  }
  
  public int getByteLength() {
    return this.engine.getBlockSize();
  }
  
  public void init(SkeinParameters paramSkeinParameters) {
    this.engine.init(paramSkeinParameters);
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
