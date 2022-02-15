package org.bouncycastle.pqc.crypto.xmss;

class XMSSVerifierUtil {
  static XMSSNode getRootNodeFromSignature(WOTSPlus paramWOTSPlus, int paramInt1, byte[] paramArrayOfbyte, XMSSReducedSignature paramXMSSReducedSignature, OTSHashAddress paramOTSHashAddress, int paramInt2) {
    if (paramArrayOfbyte.length != paramWOTSPlus.getParams().getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramXMSSReducedSignature == null)
      throw new NullPointerException("signature == null"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    LTreeAddress lTreeAddress = (LTreeAddress)(new LTreeAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withLTreeAddress(paramOTSHashAddress.getOTSAddress()).build();
    HashTreeAddress hashTreeAddress = (HashTreeAddress)(new HashTreeAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withTreeIndex(paramOTSHashAddress.getOTSAddress()).build();
    WOTSPlusPublicKeyParameters wOTSPlusPublicKeyParameters = paramWOTSPlus.getPublicKeyFromSignature(paramArrayOfbyte, paramXMSSReducedSignature.getWOTSPlusSignature(), paramOTSHashAddress);
    XMSSNode[] arrayOfXMSSNode = new XMSSNode[2];
    arrayOfXMSSNode[0] = XMSSNodeUtil.lTree(paramWOTSPlus, wOTSPlusPublicKeyParameters, lTreeAddress);
    for (byte b = 0; b < paramInt1; b++) {
      hashTreeAddress = (HashTreeAddress)(new HashTreeAddress.Builder()).withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(b).withTreeIndex(hashTreeAddress.getTreeIndex()).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
      if (Math.floor((paramInt2 / (1 << b))) % 2.0D == 0.0D) {
        hashTreeAddress = (HashTreeAddress)(new HashTreeAddress.Builder()).withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex(hashTreeAddress.getTreeIndex() / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
        arrayOfXMSSNode[1] = XMSSNodeUtil.randomizeHash(paramWOTSPlus, arrayOfXMSSNode[0], paramXMSSReducedSignature.getAuthPath().get(b), hashTreeAddress);
        arrayOfXMSSNode[1] = new XMSSNode(arrayOfXMSSNode[1].getHeight() + 1, arrayOfXMSSNode[1].getValue());
      } else {
        hashTreeAddress = (HashTreeAddress)(new HashTreeAddress.Builder()).withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
        arrayOfXMSSNode[1] = XMSSNodeUtil.randomizeHash(paramWOTSPlus, paramXMSSReducedSignature.getAuthPath().get(b), arrayOfXMSSNode[0], hashTreeAddress);
        arrayOfXMSSNode[1] = new XMSSNode(arrayOfXMSSNode[1].getHeight() + 1, arrayOfXMSSNode[1].getValue());
      } 
      arrayOfXMSSNode[0] = arrayOfXMSSNode[1];
    } 
    return arrayOfXMSSNode[0];
  }
}
