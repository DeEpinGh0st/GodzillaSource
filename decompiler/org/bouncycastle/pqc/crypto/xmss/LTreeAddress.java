package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class LTreeAddress extends XMSSAddress {
  private static final int TYPE = 1;
  
  private final int lTreeAddress;
  
  private final int treeHeight;
  
  private final int treeIndex;
  
  private LTreeAddress(Builder paramBuilder) {
    super(paramBuilder);
    this.lTreeAddress = paramBuilder.lTreeAddress;
    this.treeHeight = paramBuilder.treeHeight;
    this.treeIndex = paramBuilder.treeIndex;
  }
  
  protected byte[] toByteArray() {
    byte[] arrayOfByte = super.toByteArray();
    Pack.intToBigEndian(this.lTreeAddress, arrayOfByte, 16);
    Pack.intToBigEndian(this.treeHeight, arrayOfByte, 20);
    Pack.intToBigEndian(this.treeIndex, arrayOfByte, 24);
    return arrayOfByte;
  }
  
  protected int getLTreeAddress() {
    return this.lTreeAddress;
  }
  
  protected int getTreeHeight() {
    return this.treeHeight;
  }
  
  protected int getTreeIndex() {
    return this.treeIndex;
  }
  
  protected static class Builder extends XMSSAddress.Builder<Builder> {
    private int lTreeAddress = 0;
    
    private int treeHeight = 0;
    
    private int treeIndex = 0;
    
    protected Builder() {
      super(1);
    }
    
    protected Builder withLTreeAddress(int param1Int) {
      this.lTreeAddress = param1Int;
      return this;
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
      return new LTreeAddress(this);
    }
    
    protected Builder getThis() {
      return this;
    }
  }
}
