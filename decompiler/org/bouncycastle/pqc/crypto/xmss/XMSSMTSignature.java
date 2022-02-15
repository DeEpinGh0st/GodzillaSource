package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

public final class XMSSMTSignature implements XMSSStoreableObjectInterface {
  private final XMSSMTParameters params;
  
  private final long index;
  
  private final byte[] random;
  
  private final List<XMSSReducedSignature> reducedSignatures;
  
  private XMSSMTSignature(Builder paramBuilder) {
    this.params = paramBuilder.params;
    if (this.params == null)
      throw new NullPointerException("params == null"); 
    int i = this.params.getDigestSize();
    byte[] arrayOfByte = paramBuilder.signature;
    if (arrayOfByte != null) {
      int j = this.params.getWOTSPlus().getParams().getLen();
      int k = (int)Math.ceil(this.params.getHeight() / 8.0D);
      int m = i;
      int n = (this.params.getHeight() / this.params.getLayers() + j) * i;
      int i1 = n * this.params.getLayers();
      int i2 = k + m + i1;
      if (arrayOfByte.length != i2)
        throw new IllegalArgumentException("signature has wrong size"); 
      int i3 = 0;
      this.index = XMSSUtil.bytesToXBigEndian(arrayOfByte, i3, k);
      if (!XMSSUtil.isIndexValid(this.params.getHeight(), this.index))
        throw new IllegalArgumentException("index out of bounds"); 
      i3 += k;
      this.random = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, m);
      i3 += m;
      this.reducedSignatures = new ArrayList<XMSSReducedSignature>();
      while (i3 < arrayOfByte.length) {
        XMSSReducedSignature xMSSReducedSignature = (new XMSSReducedSignature.Builder(this.params.getXMSSParameters())).withReducedSignature(XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, n)).build();
        this.reducedSignatures.add(xMSSReducedSignature);
        i3 += n;
      } 
    } else {
      this.index = paramBuilder.index;
      byte[] arrayOfByte1 = paramBuilder.random;
      if (arrayOfByte1 != null) {
        if (arrayOfByte1.length != i)
          throw new IllegalArgumentException("size of random needs to be equal to size of digest"); 
        this.random = arrayOfByte1;
      } else {
        this.random = new byte[i];
      } 
      List<XMSSReducedSignature> list = paramBuilder.reducedSignatures;
      if (list != null) {
        this.reducedSignatures = list;
      } else {
        this.reducedSignatures = new ArrayList<XMSSReducedSignature>();
      } 
    } 
  }
  
  public byte[] toByteArray() {
    int i = this.params.getDigestSize();
    int j = this.params.getWOTSPlus().getParams().getLen();
    int k = (int)Math.ceil(this.params.getHeight() / 8.0D);
    int m = i;
    int n = (this.params.getHeight() / this.params.getLayers() + j) * i;
    int i1 = n * this.params.getLayers();
    int i2 = k + m + i1;
    byte[] arrayOfByte1 = new byte[i2];
    int i3 = 0;
    byte[] arrayOfByte2 = XMSSUtil.toBytesBigEndian(this.index, k);
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, arrayOfByte2, i3);
    i3 += k;
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, this.random, i3);
    i3 += m;
    for (XMSSReducedSignature xMSSReducedSignature : this.reducedSignatures) {
      byte[] arrayOfByte = xMSSReducedSignature.toByteArray();
      XMSSUtil.copyBytesAtOffset(arrayOfByte1, arrayOfByte, i3);
      i3 += n;
    } 
    return arrayOfByte1;
  }
  
  public long getIndex() {
    return this.index;
  }
  
  public byte[] getRandom() {
    return XMSSUtil.cloneArray(this.random);
  }
  
  public List<XMSSReducedSignature> getReducedSignatures() {
    return this.reducedSignatures;
  }
  
  public static class Builder {
    private final XMSSMTParameters params;
    
    private long index = 0L;
    
    private byte[] random = null;
    
    private List<XMSSReducedSignature> reducedSignatures = null;
    
    private byte[] signature = null;
    
    public Builder(XMSSMTParameters param1XMSSMTParameters) {
      this.params = param1XMSSMTParameters;
    }
    
    public Builder withIndex(long param1Long) {
      this.index = param1Long;
      return this;
    }
    
    public Builder withRandom(byte[] param1ArrayOfbyte) {
      this.random = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withReducedSignatures(List<XMSSReducedSignature> param1List) {
      this.reducedSignatures = param1List;
      return this;
    }
    
    public Builder withSignature(byte[] param1ArrayOfbyte) {
      this.signature = param1ArrayOfbyte;
      return this;
    }
    
    public XMSSMTSignature build() {
      return new XMSSMTSignature(this);
    }
  }
}
