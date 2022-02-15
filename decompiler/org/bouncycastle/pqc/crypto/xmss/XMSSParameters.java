package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Digest;

public final class XMSSParameters {
  private final XMSSOid oid;
  
  private final WOTSPlus wotsPlus;
  
  private final int height;
  
  private final int k;
  
  public XMSSParameters(int paramInt, Digest paramDigest) {
    if (paramInt < 2)
      throw new IllegalArgumentException("height must be >= 2"); 
    if (paramDigest == null)
      throw new NullPointerException("digest == null"); 
    this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(paramDigest));
    this.height = paramInt;
    this.k = determineMinK();
    this.oid = DefaultXMSSOid.lookup(getDigest().getAlgorithmName(), getDigestSize(), getWinternitzParameter(), this.wotsPlus.getParams().getLen(), paramInt);
  }
  
  private int determineMinK() {
    for (byte b = 2; b <= this.height; b++) {
      if ((this.height - b) % 2 == 0)
        return b; 
    } 
    throw new IllegalStateException("should never happen...");
  }
  
  protected Digest getDigest() {
    return this.wotsPlus.getParams().getDigest();
  }
  
  public int getDigestSize() {
    return this.wotsPlus.getParams().getDigestSize();
  }
  
  public int getWinternitzParameter() {
    return this.wotsPlus.getParams().getWinternitzParameter();
  }
  
  public int getHeight() {
    return this.height;
  }
  
  WOTSPlus getWOTSPlus() {
    return this.wotsPlus;
  }
  
  int getK() {
    return this.k;
  }
}
