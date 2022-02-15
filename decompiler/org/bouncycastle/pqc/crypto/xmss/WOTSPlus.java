package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

final class WOTSPlus {
  private final WOTSPlusParameters params;
  
  private final KeyedHashFunctions khf;
  
  private byte[] secretKeySeed;
  
  private byte[] publicSeed;
  
  protected WOTSPlus(WOTSPlusParameters paramWOTSPlusParameters) {
    if (paramWOTSPlusParameters == null)
      throw new NullPointerException("params == null"); 
    this.params = paramWOTSPlusParameters;
    int i = paramWOTSPlusParameters.getDigestSize();
    this.khf = new KeyedHashFunctions(paramWOTSPlusParameters.getDigest(), i);
    this.secretKeySeed = new byte[i];
    this.publicSeed = new byte[i];
  }
  
  void importKeys(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("secretKeySeed == null"); 
    if (paramArrayOfbyte1.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of secretKeySeed needs to be equal to size of digest"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("publicSeed == null"); 
    if (paramArrayOfbyte2.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of publicSeed needs to be equal to size of digest"); 
    this.secretKeySeed = paramArrayOfbyte1;
    this.publicSeed = paramArrayOfbyte2;
  }
  
  protected WOTSPlusSignature sign(byte[] paramArrayOfbyte, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("messageDigest == null"); 
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    List<Integer> list1 = convertToBaseW(paramArrayOfbyte, this.params.getWinternitzParameter(), this.params.getLen1());
    int i = 0;
    int j;
    for (j = 0; j < this.params.getLen1(); j++)
      i += this.params.getWinternitzParameter() - 1 - ((Integer)list1.get(j)).intValue(); 
    i <<= 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8;
    j = (int)Math.ceil((this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter())) / 8.0D);
    List<Integer> list2 = convertToBaseW(XMSSUtil.toBytesBigEndian(i, j), this.params.getWinternitzParameter(), this.params.getLen2());
    list1.addAll(list2);
    byte[][] arrayOfByte = new byte[this.params.getLen()][];
    for (byte b = 0; b < this.params.getLen(); b++) {
      paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).withChainAddress(b).withHashAddress(paramOTSHashAddress.getHashAddress()).withKeyAndMask(paramOTSHashAddress.getKeyAndMask()).build();
      arrayOfByte[b] = chain(expandSecretKeySeed(b), 0, ((Integer)list1.get(b)).intValue(), paramOTSHashAddress);
    } 
    return new WOTSPlusSignature(this.params, arrayOfByte);
  }
  
  protected boolean verifySignature(byte[] paramArrayOfbyte, WOTSPlusSignature paramWOTSPlusSignature, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("messageDigest == null"); 
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramWOTSPlusSignature == null)
      throw new NullPointerException("signature == null"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    byte[][] arrayOfByte = getPublicKeyFromSignature(paramArrayOfbyte, paramWOTSPlusSignature, paramOTSHashAddress).toByteArray();
    return XMSSUtil.areEqual(arrayOfByte, getPublicKey(paramOTSHashAddress).toByteArray());
  }
  
  protected WOTSPlusPublicKeyParameters getPublicKeyFromSignature(byte[] paramArrayOfbyte, WOTSPlusSignature paramWOTSPlusSignature, OTSHashAddress paramOTSHashAddress) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("messageDigest == null"); 
    if (paramArrayOfbyte.length != this.params.getDigestSize())
      throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest"); 
    if (paramWOTSPlusSignature == null)
      throw new NullPointerException("signature == null"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    List<Integer> list1 = convertToBaseW(paramArrayOfbyte, this.params.getWinternitzParameter(), this.params.getLen1());
    int i = 0;
    int j;
    for (j = 0; j < this.params.getLen1(); j++)
      i += this.params.getWinternitzParameter() - 1 - ((Integer)list1.get(j)).intValue(); 
    i <<= 8 - this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()) % 8;
    j = (int)Math.ceil((this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter())) / 8.0D);
    List<Integer> list2 = convertToBaseW(XMSSUtil.toBytesBigEndian(i, j), this.params.getWinternitzParameter(), this.params.getLen2());
    list1.addAll(list2);
    byte[][] arrayOfByte = new byte[this.params.getLen()][];
    for (byte b = 0; b < this.params.getLen(); b++) {
      paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).withChainAddress(b).withHashAddress(paramOTSHashAddress.getHashAddress()).withKeyAndMask(paramOTSHashAddress.getKeyAndMask()).build();
      arrayOfByte[b] = chain(paramWOTSPlusSignature.toByteArray()[b], ((Integer)list1.get(b)).intValue(), this.params.getWinternitzParameter() - 1 - ((Integer)list1.get(b)).intValue(), paramOTSHashAddress);
    } 
    return new WOTSPlusPublicKeyParameters(this.params, arrayOfByte);
  }
  
  private byte[] chain(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, OTSHashAddress paramOTSHashAddress) {
    int i = this.params.getDigestSize();
    if (paramArrayOfbyte == null)
      throw new NullPointerException("startHash == null"); 
    if (paramArrayOfbyte.length != i)
      throw new IllegalArgumentException("startHash needs to be " + i + "bytes"); 
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    if (paramOTSHashAddress.toByteArray() == null)
      throw new NullPointerException("otsHashAddress byte array == null"); 
    if (paramInt1 + paramInt2 > this.params.getWinternitzParameter() - 1)
      throw new IllegalArgumentException("max chain length must not be greater than w"); 
    if (paramInt2 == 0)
      return paramArrayOfbyte; 
    null = chain(paramArrayOfbyte, paramInt1, paramInt2 - 1, paramOTSHashAddress);
    paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).withChainAddress(paramOTSHashAddress.getChainAddress()).withHashAddress(paramInt1 + paramInt2 - 1).withKeyAndMask(0).build();
    byte[] arrayOfByte1 = this.khf.PRF(this.publicSeed, paramOTSHashAddress.toByteArray());
    paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).withChainAddress(paramOTSHashAddress.getChainAddress()).withHashAddress(paramOTSHashAddress.getHashAddress()).withKeyAndMask(1).build();
    byte[] arrayOfByte2 = this.khf.PRF(this.publicSeed, paramOTSHashAddress.toByteArray());
    byte[] arrayOfByte3 = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte3[b] = (byte)(null[b] ^ arrayOfByte2[b]); 
    return this.khf.F(arrayOfByte1, arrayOfByte3);
  }
  
  private List<Integer> convertToBaseW(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("msg == null"); 
    if (paramInt1 != 4 && paramInt1 != 16)
      throw new IllegalArgumentException("w needs to be 4 or 16"); 
    int i = XMSSUtil.log2(paramInt1);
    if (paramInt2 > 8 * paramArrayOfbyte.length / i)
      throw new IllegalArgumentException("outLength too big"); 
    ArrayList<Integer> arrayList = new ArrayList();
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      int j;
      for (j = 8 - i; j >= 0; j -= i) {
        arrayList.add(Integer.valueOf(paramArrayOfbyte[b] >> j & paramInt1 - 1));
        if (arrayList.size() == paramInt2)
          return arrayList; 
      } 
    } 
    return arrayList;
  }
  
  protected byte[] getWOTSPlusSecretKey(byte[] paramArrayOfbyte, OTSHashAddress paramOTSHashAddress) {
    paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).build();
    return this.khf.PRF(paramArrayOfbyte, paramOTSHashAddress.toByteArray());
  }
  
  private byte[] expandSecretKeySeed(int paramInt) {
    if (paramInt < 0 || paramInt >= this.params.getLen())
      throw new IllegalArgumentException("index out of bounds"); 
    return this.khf.PRF(this.secretKeySeed, XMSSUtil.toBytesBigEndian(paramInt, 32));
  }
  
  protected WOTSPlusParameters getParams() {
    return this.params;
  }
  
  protected KeyedHashFunctions getKhf() {
    return this.khf;
  }
  
  protected byte[] getSecretKeySeed() {
    return XMSSUtil.cloneArray(getSecretKeySeed());
  }
  
  protected byte[] getPublicSeed() {
    return XMSSUtil.cloneArray(this.publicSeed);
  }
  
  protected WOTSPlusPrivateKeyParameters getPrivateKey() {
    byte[][] arrayOfByte = new byte[this.params.getLen()][];
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = expandSecretKeySeed(b); 
    return new WOTSPlusPrivateKeyParameters(this.params, arrayOfByte);
  }
  
  protected WOTSPlusPublicKeyParameters getPublicKey(OTSHashAddress paramOTSHashAddress) {
    if (paramOTSHashAddress == null)
      throw new NullPointerException("otsHashAddress == null"); 
    byte[][] arrayOfByte = new byte[this.params.getLen()][];
    for (byte b = 0; b < this.params.getLen(); b++) {
      paramOTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(paramOTSHashAddress.getLayerAddress()).withTreeAddress(paramOTSHashAddress.getTreeAddress()).withOTSAddress(paramOTSHashAddress.getOTSAddress()).withChainAddress(b).withHashAddress(paramOTSHashAddress.getHashAddress()).withKeyAndMask(paramOTSHashAddress.getKeyAndMask()).build();
      arrayOfByte[b] = chain(expandSecretKeySeed(b), 0, this.params.getWinternitzParameter() - 1, paramOTSHashAddress);
    } 
    return new WOTSPlusPublicKeyParameters(this.params, arrayOfByte);
  }
}
