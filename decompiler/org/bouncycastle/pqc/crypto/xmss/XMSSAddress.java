package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

public abstract class XMSSAddress {
  private final int layerAddress;
  
  private final long treeAddress;
  
  private final int type;
  
  private final int keyAndMask;
  
  protected XMSSAddress(Builder paramBuilder) {
    this.layerAddress = paramBuilder.layerAddress;
    this.treeAddress = paramBuilder.treeAddress;
    this.type = paramBuilder.type;
    this.keyAndMask = paramBuilder.keyAndMask;
  }
  
  protected byte[] toByteArray() {
    byte[] arrayOfByte = new byte[32];
    Pack.intToBigEndian(this.layerAddress, arrayOfByte, 0);
    Pack.longToBigEndian(this.treeAddress, arrayOfByte, 4);
    Pack.intToBigEndian(this.type, arrayOfByte, 12);
    Pack.intToBigEndian(this.keyAndMask, arrayOfByte, 28);
    return arrayOfByte;
  }
  
  protected final int getLayerAddress() {
    return this.layerAddress;
  }
  
  protected final long getTreeAddress() {
    return this.treeAddress;
  }
  
  public final int getType() {
    return this.type;
  }
  
  public final int getKeyAndMask() {
    return this.keyAndMask;
  }
  
  protected static abstract class Builder<T extends Builder> {
    private final int type;
    
    private int layerAddress = 0;
    
    private long treeAddress = 0L;
    
    private int keyAndMask = 0;
    
    protected Builder(int param1Int) {
      this.type = param1Int;
    }
    
    protected T withLayerAddress(int param1Int) {
      this.layerAddress = param1Int;
      return getThis();
    }
    
    protected T withTreeAddress(long param1Long) {
      this.treeAddress = param1Long;
      return getThis();
    }
    
    protected T withKeyAndMask(int param1Int) {
      this.keyAndMask = param1Int;
      return getThis();
    }
    
    protected abstract XMSSAddress build();
    
    protected abstract T getThis();
  }
}
