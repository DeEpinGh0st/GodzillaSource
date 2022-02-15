package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

public class XMSSReducedSignature implements XMSSStoreableObjectInterface {
  private final XMSSParameters params;
  
  private final WOTSPlusSignature wotsPlusSignature;
  
  private final List<XMSSNode> authPath;
  
  protected XMSSReducedSignature(Builder paramBuilder) {
    this.params = paramBuilder.params;
    if (this.params == null)
      throw new NullPointerException("params == null"); 
    int i = this.params.getDigestSize();
    int j = this.params.getWOTSPlus().getParams().getLen();
    int k = this.params.getHeight();
    byte[] arrayOfByte = paramBuilder.reducedSignature;
    if (arrayOfByte != null) {
      int m = j * i;
      int n = k * i;
      int i1 = m + n;
      if (arrayOfByte.length != i1)
        throw new IllegalArgumentException("signature has wrong size"); 
      int i2 = 0;
      byte[][] arrayOfByte1 = new byte[j][];
      for (byte b1 = 0; b1 < arrayOfByte1.length; b1++) {
        arrayOfByte1[b1] = XMSSUtil.extractBytesAtOffset(arrayOfByte, i2, i);
        i2 += i;
      } 
      this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), arrayOfByte1);
      ArrayList<XMSSNode> arrayList = new ArrayList();
      for (byte b2 = 0; b2 < k; b2++) {
        arrayList.add(new XMSSNode(b2, XMSSUtil.extractBytesAtOffset(arrayOfByte, i2, i)));
        i2 += i;
      } 
      this.authPath = arrayList;
    } else {
      WOTSPlusSignature wOTSPlusSignature = paramBuilder.wotsPlusSignature;
      if (wOTSPlusSignature != null) {
        this.wotsPlusSignature = wOTSPlusSignature;
      } else {
        this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), new byte[j][i]);
      } 
      List<XMSSNode> list = paramBuilder.authPath;
      if (list != null) {
        if (list.size() != k)
          throw new IllegalArgumentException("size of authPath needs to be equal to height of tree"); 
        this.authPath = list;
      } else {
        this.authPath = new ArrayList<XMSSNode>();
      } 
    } 
  }
  
  public byte[] toByteArray() {
    int i = this.params.getDigestSize();
    int j = this.params.getWOTSPlus().getParams().getLen() * i;
    int k = this.params.getHeight() * i;
    int m = j + k;
    byte[] arrayOfByte = new byte[m];
    int n = 0;
    byte[][] arrayOfByte1 = this.wotsPlusSignature.toByteArray();
    byte b;
    for (b = 0; b < arrayOfByte1.length; b++) {
      XMSSUtil.copyBytesAtOffset(arrayOfByte, arrayOfByte1[b], n);
      n += i;
    } 
    for (b = 0; b < this.authPath.size(); b++) {
      byte[] arrayOfByte2 = ((XMSSNode)this.authPath.get(b)).getValue();
      XMSSUtil.copyBytesAtOffset(arrayOfByte, arrayOfByte2, n);
      n += i;
    } 
    return arrayOfByte;
  }
  
  public XMSSParameters getParams() {
    return this.params;
  }
  
  public WOTSPlusSignature getWOTSPlusSignature() {
    return this.wotsPlusSignature;
  }
  
  public List<XMSSNode> getAuthPath() {
    return this.authPath;
  }
  
  public static class Builder {
    private final XMSSParameters params;
    
    private WOTSPlusSignature wotsPlusSignature = null;
    
    private List<XMSSNode> authPath = null;
    
    private byte[] reducedSignature = null;
    
    public Builder(XMSSParameters param1XMSSParameters) {
      this.params = param1XMSSParameters;
    }
    
    public Builder withWOTSPlusSignature(WOTSPlusSignature param1WOTSPlusSignature) {
      this.wotsPlusSignature = param1WOTSPlusSignature;
      return this;
    }
    
    public Builder withAuthPath(List<XMSSNode> param1List) {
      this.authPath = param1List;
      return this;
    }
    
    public Builder withReducedSignature(byte[] param1ArrayOfbyte) {
      this.reducedSignature = XMSSUtil.cloneArray(param1ArrayOfbyte);
      return this;
    }
    
    public XMSSReducedSignature build() {
      return new XMSSReducedSignature(this);
    }
  }
}
