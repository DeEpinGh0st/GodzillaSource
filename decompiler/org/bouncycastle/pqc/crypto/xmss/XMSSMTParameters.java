package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Digest;

public final class XMSSMTParameters {
  private final XMSSOid oid;
  
  private final XMSSParameters xmssParams;
  
  private final int height;
  
  private final int layers;
  
  public XMSSMTParameters(int paramInt1, int paramInt2, Digest paramDigest) {
    this.height = paramInt1;
    this.layers = paramInt2;
    this.xmssParams = new XMSSParameters(xmssTreeHeight(paramInt1, paramInt2), paramDigest);
    this.oid = DefaultXMSSMTOid.lookup(getDigest().getAlgorithmName(), getDigestSize(), getWinternitzParameter(), getLen(), getHeight(), paramInt2);
  }
  
  private static int xmssTreeHeight(int paramInt1, int paramInt2) throws IllegalArgumentException {
    if (paramInt1 < 2)
      throw new IllegalArgumentException("totalHeight must be > 1"); 
    if (paramInt1 % paramInt2 != 0)
      throw new IllegalArgumentException("layers must divide totalHeight without remainder"); 
    if (paramInt1 / paramInt2 == 1)
      throw new IllegalArgumentException("height / layers must be greater than 1"); 
    return paramInt1 / paramInt2;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public int getLayers() {
    return this.layers;
  }
  
  protected XMSSParameters getXMSSParameters() {
    return this.xmssParams;
  }
  
  protected WOTSPlus getWOTSPlus() {
    return this.xmssParams.getWOTSPlus();
  }
  
  protected Digest getDigest() {
    return this.xmssParams.getDigest();
  }
  
  public int getDigestSize() {
    return this.xmssParams.getDigestSize();
  }
  
  public int getWinternitzParameter() {
    return this.xmssParams.getWinternitzParameter();
  }
  
  protected int getLen() {
    return this.xmssParams.getWOTSPlus().getParams().getLen();
  }
}
