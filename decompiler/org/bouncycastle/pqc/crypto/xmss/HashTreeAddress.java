package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class HashTreeAddress extends XMSSAddress {
  private static final int TYPE = 2;
  
  private static final int PADDING = 0;
  
  private final int padding = 0;
  
  private final int treeHeight;
  
  private final int treeIndex;
  
  private HashTreeAddress(Builder paramBuilder) {
    super(paramBuilder);
    this.treeHeight = paramBuilder.treeHeight;
    this.treeIndex = paramBuilder.treeIndex;
  }
  
  protected byte[] toByteArray() {
    byte[] arrayOfByte = super.toByteArray();
    Pack.intToBigEndian(this.padding, arrayOfByte, 16);
    Pack.intToBigEndian(this.treeHeight, arrayOfByte, 20);
    Pack.intToBigEndian(this.treeIndex, arrayOfByte, 24);
    return arrayOfByte;
  }
  
  protected int getPadding() {
    return this.padding;
  }
  
  protected int getTreeHeight() {
    return this.treeHeight;
  }
  
  protected int getTreeIndex() {
    return this.treeIndex;
  }
  
  protected static class Builder extends XMSSAddress.Builder<Builder> {
    private int treeHeight = 0;
    
    private int treeIndex = 0;
    
    protected Builder() {
      super(2);
    }
    
    protected Builder withTreeHeight(int param1Int) {
      this.treeHeight = param1Int;
      return this;
    }
    
    protected Builder withTreeIndex(int param1Int) {
      this.treeIndex = param1Int;
      return this;
    }
    
    protected XMSSAddress build() {
      return new HashTreeAddress(this);
    }
    
    protected Builder getThis() {
      return this;
    }
  }
}
