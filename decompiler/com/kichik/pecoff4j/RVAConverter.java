package com.kichik.pecoff4j;









public class RVAConverter
{
  private int[] virtualAddress;
  private int[] pointerToRawData;
  
  public RVAConverter(int[] virtualAddress, int[] pointerToRawData) {
    this.virtualAddress = virtualAddress;
    this.pointerToRawData = pointerToRawData;
  }
  
  public int convertVirtualAddressToRawDataPointer(int virtualAddress) {
    for (int i = 0; i < this.virtualAddress.length; i++) {
      if (virtualAddress < this.virtualAddress[i]) {
        if (i > 0) {
          int j = this.pointerToRawData[i - 1];
          int k = this.virtualAddress[i - 1];
          return j + virtualAddress - k;
        } 
        return virtualAddress;
      } 
    } 


    
    int prd = this.pointerToRawData[this.virtualAddress.length - 1];
    int vad = this.virtualAddress[this.virtualAddress.length - 1];
    return prd + virtualAddress - vad;
  }
}
