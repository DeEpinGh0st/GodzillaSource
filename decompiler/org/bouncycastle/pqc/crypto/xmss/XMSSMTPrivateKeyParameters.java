package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public final class XMSSMTPrivateKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface {
  private final XMSSMTParameters params;
  
  private final long index;
  
  private final byte[] secretKeySeed;
  
  private final byte[] secretKeyPRF;
  
  private final byte[] publicSeed;
  
  private final byte[] root;
  
  private final BDSStateMap bdsState;
  
  private XMSSMTPrivateKeyParameters(Builder paramBuilder) {
    super(true);
    this.params = paramBuilder.params;
    if (this.params == null)
      throw new NullPointerException("params == null"); 
    int i = this.params.getDigestSize();
    byte[] arrayOfByte = paramBuilder.privateKey;
    if (arrayOfByte != null) {
      if (paramBuilder.xmss == null)
        throw new NullPointerException("xmss == null"); 
      int j = this.params.getHeight();
      int k = (j + 7) / 8;
      int m = i;
      int n = i;
      int i1 = i;
      int i2 = i;
      int i3 = 0;
      this.index = XMSSUtil.bytesToXBigEndian(arrayOfByte, i3, k);
      if (!XMSSUtil.isIndexValid(j, this.index))
        throw new IllegalArgumentException("index out of bounds"); 
      i3 += k;
      this.secretKeySeed = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, m);
      i3 += m;
      this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, n);
      i3 += n;
      this.publicSeed = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, i1);
      i3 += i1;
      this.root = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, i2);
      i3 += i2;
      byte[] arrayOfByte1 = XMSSUtil.extractBytesAtOffset(arrayOfByte, i3, arrayOfByte.length - i3);
      BDSStateMap bDSStateMap = null;
      try {
        bDSStateMap = (BDSStateMap)XMSSUtil.deserialize(arrayOfByte1);
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } catch (ClassNotFoundException classNotFoundException) {
        classNotFoundException.printStackTrace();
      } 
      bDSStateMap.setXMSS(paramBuilder.xmss);
      this.bdsState = bDSStateMap;
    } else {
      this.index = paramBuilder.index;
      byte[] arrayOfByte1 = paramBuilder.secretKeySeed;
      if (arrayOfByte1 != null) {
        if (arrayOfByte1.length != i)
          throw new IllegalArgumentException("size of secretKeySeed needs to be equal size of digest"); 
        this.secretKeySeed = arrayOfByte1;
      } else {
        this.secretKeySeed = new byte[i];
      } 
      byte[] arrayOfByte2 = paramBuilder.secretKeyPRF;
      if (arrayOfByte2 != null) {
        if (arrayOfByte2.length != i)
          throw new IllegalArgumentException("size of secretKeyPRF needs to be equal size of digest"); 
        this.secretKeyPRF = arrayOfByte2;
      } else {
        this.secretKeyPRF = new byte[i];
      } 
      byte[] arrayOfByte3 = paramBuilder.publicSeed;
      if (arrayOfByte3 != null) {
        if (arrayOfByte3.length != i)
          throw new IllegalArgumentException("size of publicSeed needs to be equal size of digest"); 
        this.publicSeed = arrayOfByte3;
      } else {
        this.publicSeed = new byte[i];
      } 
      byte[] arrayOfByte4 = paramBuilder.root;
      if (arrayOfByte4 != null) {
        if (arrayOfByte4.length != i)
          throw new IllegalArgumentException("size of root needs to be equal size of digest"); 
        this.root = arrayOfByte4;
      } else {
        this.root = new byte[i];
      } 
      BDSStateMap bDSStateMap = paramBuilder.bdsState;
      if (bDSStateMap != null) {
        this.bdsState = bDSStateMap;
      } else {
        long l = paramBuilder.index;
        int j = this.params.getHeight();
        if (XMSSUtil.isIndexValid(j, l) && arrayOfByte3 != null && arrayOfByte1 != null) {
          this.bdsState = new BDSStateMap(this.params, paramBuilder.index, arrayOfByte3, arrayOfByte1);
        } else {
          this.bdsState = new BDSStateMap();
        } 
      } 
    } 
  }
  
  public byte[] toByteArray() {
    int i = this.params.getDigestSize();
    int j = (this.params.getHeight() + 7) / 8;
    int k = i;
    int m = i;
    int n = i;
    int i1 = i;
    int i2 = j + k + m + n + i1;
    byte[] arrayOfByte1 = new byte[i2];
    int i3 = 0;
    byte[] arrayOfByte2 = XMSSUtil.toBytesBigEndian(this.index, j);
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, arrayOfByte2, i3);
    i3 += j;
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, this.secretKeySeed, i3);
    i3 += k;
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, this.secretKeyPRF, i3);
    i3 += m;
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, this.publicSeed, i3);
    i3 += n;
    XMSSUtil.copyBytesAtOffset(arrayOfByte1, this.root, i3);
    byte[] arrayOfByte3 = null;
    try {
      arrayOfByte3 = XMSSUtil.serialize(this.bdsState);
    } catch (IOException iOException) {
      iOException.printStackTrace();
      throw new RuntimeException("error serializing bds state");
    } 
    return Arrays.concatenate(arrayOfByte1, arrayOfByte3);
  }
  
  public long getIndex() {
    return this.index;
  }
  
  public byte[] getSecretKeySeed() {
    return XMSSUtil.cloneArray(this.secretKeySeed);
  }
  
  public byte[] getSecretKeyPRF() {
    return XMSSUtil.cloneArray(this.secretKeyPRF);
  }
  
  public byte[] getPublicSeed() {
    return XMSSUtil.cloneArray(this.publicSeed);
  }
  
  public byte[] getRoot() {
    return XMSSUtil.cloneArray(this.root);
  }
  
  BDSStateMap getBDSState() {
    return this.bdsState;
  }
  
  public XMSSMTParameters getParameters() {
    return this.params;
  }
  
  public XMSSMTPrivateKeyParameters getNextKey() {
    BDSStateMap bDSStateMap = new BDSStateMap(this.bdsState, this.params, getIndex(), this.publicSeed, this.secretKeySeed);
    return (new Builder(this.params)).withIndex(this.index + 1L).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withBDSState(bDSStateMap).build();
  }
  
  public static class Builder {
    private final XMSSMTParameters params;
    
    private long index = 0L;
    
    private byte[] secretKeySeed = null;
    
    private byte[] secretKeyPRF = null;
    
    private byte[] publicSeed = null;
    
    private byte[] root = null;
    
    private BDSStateMap bdsState = null;
    
    private byte[] privateKey = null;
    
    private XMSSParameters xmss = null;
    
    public Builder(XMSSMTParameters param1XMSSMTParameters) {
      this.params = param1XMSSMTParameters;
    }
    
    public Builder withIndex(long param1Long) {
      this.index = param1Long;
      return this;
    }
    
    public Builder withSecretKeySeed(byte[] param1ArrayOfbyte) {
      this.secretKeySeed = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withSecretKeyPRF(byte[] param1ArrayOfbyte) {
      this.secretKeyPRF = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withPublicSeed(byte[] param1ArrayOfbyte) {
      this.publicSeed = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withRoot(byte[] param1ArrayOfbyte) {
      this.root = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public Builder withBDSState(BDSStateMap param1BDSStateMap) {
      this.bdsState = param1BDSStateMap;
      return this;
    }
    
    public Builder withPrivateKey(byte[] param1ArrayOfbyte, XMSSParameters param1XMSSParameters) {
      this.privateKey = XMSSUtil.cloneArray(param1ArrayOfbyte);
      this.xmss = param1XMSSParameters;
      return this;
    }
    
    public XMSSMTPrivateKeyParameters build() {
      return new XMSSMTPrivateKeyParameters(this);
    }
  }
}
