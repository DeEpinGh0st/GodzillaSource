package org.bouncycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.util.Integers;

public class BDSStateMap implements Serializable {
  private final Map<Integer, BDS> bdsState = new TreeMap<Integer, BDS>();
  
  BDSStateMap() {}
  
  BDSStateMap(XMSSMTParameters paramXMSSMTParameters, long paramLong, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    long l;
    for (l = 0L; l < paramLong; l++)
      updateState(paramXMSSMTParameters, l, paramArrayOfbyte1, paramArrayOfbyte2); 
  }
  
  BDSStateMap(BDSStateMap paramBDSStateMap, XMSSMTParameters paramXMSSMTParameters, long paramLong, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (Integer integer : paramBDSStateMap.bdsState.keySet())
      this.bdsState.put(integer, paramBDSStateMap.bdsState.get(integer)); 
    updateState(paramXMSSMTParameters, paramLong, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  private void updateState(XMSSMTParameters paramXMSSMTParameters, long paramLong, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    XMSSParameters xMSSParameters = paramXMSSMTParameters.getXMSSParameters();
    int i = xMSSParameters.getHeight();
    long l = XMSSUtil.getTreeIndex(paramLong, i);
    int j = XMSSUtil.getLeafIndex(paramLong, i);
    OTSHashAddress oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withTreeAddress(l).withOTSAddress(j).build();
    if (j < (1 << i) - 1) {
      if (get(0) == null || j == 0)
        put(0, new BDS(xMSSParameters, paramArrayOfbyte1, paramArrayOfbyte2, oTSHashAddress)); 
      update(0, paramArrayOfbyte1, paramArrayOfbyte2, oTSHashAddress);
    } 
    for (byte b = 1; b < paramXMSSMTParameters.getLayers(); b++) {
      j = XMSSUtil.getLeafIndex(l, i);
      l = XMSSUtil.getTreeIndex(l, i);
      oTSHashAddress = (OTSHashAddress)(new OTSHashAddress.Builder()).withLayerAddress(b).withTreeAddress(l).withOTSAddress(j).build();
      if (j < (1 << i) - 1 && XMSSUtil.isNewAuthenticationPathNeeded(paramLong, i, b)) {
        if (get(b) == null)
          put(b, new BDS(paramXMSSMTParameters.getXMSSParameters(), paramArrayOfbyte1, paramArrayOfbyte2, oTSHashAddress)); 
        update(b, paramArrayOfbyte1, paramArrayOfbyte2, oTSHashAddress);
      } 
    } 
  }
  
  void setXMSS(XMSSParameters paramXMSSParameters) {
    for (Integer integer : this.bdsState.keySet()) {
      BDS bDS = this.bdsState.get(integer);
      bDS.setXMSS(paramXMSSParameters);
      bDS.validate();
    } 
  }
  
  public boolean isEmpty() {
    return this.bdsState.isEmpty();
  }
  
  public BDS get(int paramInt) {
    return this.bdsState.get(Integers.valueOf(paramInt));
  }
  
  public BDS update(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, OTSHashAddress paramOTSHashAddress) {
    return this.bdsState.put(Integers.valueOf(paramInt), ((BDS)this.bdsState.get(Integers.valueOf(paramInt))).getNextState(paramArrayOfbyte1, paramArrayOfbyte2, paramOTSHashAddress));
  }
  
  public void put(int paramInt, BDS paramBDS) {
    this.bdsState.put(Integers.valueOf(paramInt), paramBDS);
  }
}
