package org.bouncycastle.crypto.engines;

public class VMPCKSA3Engine extends VMPCEngine {
  public String getAlgorithmName() {
    return "VMPC-KSA3";
  }
  
  protected void initKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.s = 0;
    this.P = new byte[256];
    byte b;
    for (b = 0; b < 'Ā'; b++)
      this.P[b] = (byte)b; 
    for (b = 0; b < '̀'; b++) {
      this.s = this.P[this.s + this.P[b & 0xFF] + paramArrayOfbyte1[b % paramArrayOfbyte1.length] & 0xFF];
      byte b1 = this.P[b & 0xFF];
      this.P[b & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b1;
    } 
    for (b = 0; b < '̀'; b++) {
      this.s = this.P[this.s + this.P[b & 0xFF] + paramArrayOfbyte2[b % paramArrayOfbyte2.length] & 0xFF];
      byte b1 = this.P[b & 0xFF];
      this.P[b & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b1;
    } 
    for (b = 0; b < '̀'; b++) {
      this.s = this.P[this.s + this.P[b & 0xFF] + paramArrayOfbyte1[b % paramArrayOfbyte1.length] & 0xFF];
      byte b1 = this.P[b & 0xFF];
      this.P[b & 0xFF] = this.P[this.s & 0xFF];
      this.P[this.s & 0xFF] = b1;
    } 
    this.n = 0;
  }
}
