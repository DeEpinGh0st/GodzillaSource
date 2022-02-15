package org.bouncycastle.cert.crmf;

public interface EncryptedValuePadder {
  byte[] getPaddedData(byte[] paramArrayOfbyte);
  
  byte[] getUnpaddedData(byte[] paramArrayOfbyte);
}
