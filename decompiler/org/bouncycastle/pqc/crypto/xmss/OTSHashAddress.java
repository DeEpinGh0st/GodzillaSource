package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class OTSHashAddress extends XMSSAddress {
  private static final int TYPE = 0;
  
  private final int otsAddress;
  
  private final int chainAddress;
  
  private final int hashAddress;
  
  private OTSHashAddress(Builder paramBuilder) {
    super(paramBuilder);
    this.otsAddress = paramBuilder.otsAddress;
    this.chainAddress = paramBuilder.chainAddress;
    this.hashAddress = paramBuilder.hashAddress;
  }
  
  protected byte[] toByteArray() {
    byte[] arrayOfByte = super.toByteArray();
    Pack.intToBigEndian(this.otsAddress, arrayOfByte, 16);
    Pack.intToBigEndian(this.chainAddress, arrayOfByte, 20);
    Pack.intToBigEndian(this.hashAddress, arrayOfByte, 24);
    return arrayOfByte;
  }
  
  protected int getOTSAddress() {
    return this.otsAddress;
  }
  
  protected int getChainAddress() {
    return this.chainAddress;
  }
  
  protected int getHashAddress() {
    return this.hashAddress;
  }
  
  protected static class Builder extends XMSSAddress.Builder<Builder> {
    private int otsAddress = 0;
    
    private int chainAddress = 0;
    
    private int hashAddress = 0;
    
    protected Builder() {
      super(0);
    }
    
    protected Builder withOTSAddress(int param1Int) {
      this.otsAddress = param1Int;
      return this;
    }
    
    protected Builder withChainAddress(int param1Int) {
      this.chainAddress = param1Int;
      return this;
    }
    
    protected Builder withHashAddress(int param1Int) {
      this.hashAddress = param1Int;
      return this;
    }
    
    protected XMSSAddress build() {
      return new OTSHashAddress(this);
    }
    
    protected Builder getThis() {
      return this;
    }
  }
}
