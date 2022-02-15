package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusPrivateKeyParameters {
  private final byte[][] privateKey;
  
  protected WOTSPlusPrivateKeyParameters(WOTSPlusParameters paramWOTSPlusParameters, byte[][] paramArrayOfbyte) {
    if (paramWOTSPlusParameters == null)
      throw new NullPointerException("params == null"); 
    if (paramArrayOfbyte == null)
      throw new NullPointerException("privateKey == null"); 
    if (XMSSUtil.hasNullPointer(paramArrayOfbyte))
      throw new NullPointerException("privateKey byte array == null"); 
    if (paramArrayOfbyte.length != paramWOTSPlusParameters.getLen())
      throw new IllegalArgumentException("wrong privateKey format"); 
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      if ((paramArrayOfbyte[b]).length != paramWOTSPlusParameters.getDigestSize())
        throw new IllegalArgumentException("wrong privateKey format"); 
    } 
    this.privateKey = XMSSUtil.cloneArray(paramArrayOfbyte);
  }
  
  protected byte[][] toByteArray() {
    return XMSSUtil.cloneArray(this.privateKey);
  }
}
