package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

public final class XMSSSignature extends XMSSReducedSignature implements XMSSStoreableObjectInterface {
  private final int index;
  
  private final byte[] random;
  
  private XMSSSignature(Builder paramBuilder) {
    super(paramBuilder);
    this.index = paramBuilder.index;
    int i = getParams().getDigestSize();
    byte[] arrayOfByte = paramBuilder.random;
    if (arrayOfByte != null) {
      if (arrayOfByte.length != i)
        throw new IllegalArgumentException("size of random needs to be equal to size of digest"); 
      this.random = arrayOfByte;
    } else {
      this.random = new byte[i];
    } 
  }
  
  public byte[] toByteArray() {
    int i = getParams().getDigestSize();
    byte b1 = 4;
    int j = i;
    int k = getParams().getWOTSPlus().getParams().getLen() * i;
    int m = getParams().getHeight() * i;
    int n = b1 + j + k + m;
    byte[] arrayOfByte = new byte[n];
    int i1 = 0;
    Pack.intToBigEndian(this.index, arrayOfByte, i1);
    i1 += b1;
    XMSSUtil.copyBytesAtOffset(arrayOfByte, this.random, i1);
    i1 += j;
    byte[][] arrayOfByte1 = getWOTSPlusSignature().toByteArray();
    byte b2;
    for (b2 = 0; b2 < arrayOfByte1.length; b2++) {
      XMSSUtil.copyBytesAtOffset(arrayOfByte, arrayOfByte1[b2], i1);
      i1 += i;
    } 
    for (b2 = 0; b2 < getAuthPath().size(); b2++) {
      byte[] arrayOfByte2 = ((XMSSNode)getAuthPath().get(b2)).getValue();
      XMSSUtil.copyBytesAtOffset(arrayOfByte, arrayOfByte2, i1);
      i1 += i;
    } 
    return arrayOfByte;
  }
  
  public int getIndex() {
    return this.index;
  }
  
  public byte[] getRandom() {
    return XMSSUtil.cloneArray(this.random);
  }
  
  public static class Builder extends XMSSReducedSignature.Builder {
    private final XMSSParameters params;
    
    private int index = 0;
    
    private byte[] random = null;
    
    public Builder(XMSSParameters param1XMSSParameters) {
      super(param1XMSSParameters);
      this.params = param1XMSSParameters;
    }
    
    public Builder withIndex(int param1Int) {
      this.index = param1Int;
      return this;
    }
    
    public Builder withRandom(byte[] param1ArrayOfbyte) {
      this.random = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withSignature(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte == null)
        throw new NullPointerException("signature == null"); 
      int i = this.params.getDigestSize();
      int j = this.params.getWOTSPlus().getParams().getLen();
      int k = this.params.getHeight();
      byte b = 4;
      int m = i;
      int n = j * i;
      int i1 = k * i;
      int i2 = 0;
      this.index = Pack.bigEndianToInt(param1ArrayOfbyte, i2);
      i2 += b;
      this.random = XMSSUtil.extractBytesAtOffset(param1ArrayOfbyte, i2, m);
      i2 += m;
      withReducedSignature(XMSSUtil.extractBytesAtOffset(param1ArrayOfbyte, i2, n + i1));
      return this;
    }
    
    public XMSSSignature build() {
      return new XMSSSignature(this);
    }
  }
}
