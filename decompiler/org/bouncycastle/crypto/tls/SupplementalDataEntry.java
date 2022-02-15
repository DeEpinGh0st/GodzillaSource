package org.bouncycastle.crypto.tls;

public class SupplementalDataEntry {
  protected int dataType;
  
  protected byte[] data;
  
  public SupplementalDataEntry(int paramInt, byte[] paramArrayOfbyte) {
    this.dataType = paramInt;
    this.data = paramArrayOfbyte;
  }
  
  public int getDataType() {
    return this.dataType;
  }
  
  public byte[] getData() {
    return this.data;
  }
}
