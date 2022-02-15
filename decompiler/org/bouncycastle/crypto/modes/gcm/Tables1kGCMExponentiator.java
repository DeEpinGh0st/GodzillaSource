package org.bouncycastle.crypto.modes.gcm;

import java.util.Vector;
import org.bouncycastle.util.Arrays;

public class Tables1kGCMExponentiator implements GCMExponentiator {
  private Vector lookupPowX2;
  
  public void init(byte[] paramArrayOfbyte) {
    int[] arrayOfInt = GCMUtil.asInts(paramArrayOfbyte);
    if (this.lookupPowX2 != null && Arrays.areEqual(arrayOfInt, this.lookupPowX2.elementAt(0)))
      return; 
    this.lookupPowX2 = new Vector(8);
    this.lookupPowX2.addElement(arrayOfInt);
  }
  
  public void exponentiateX(long paramLong, byte[] paramArrayOfbyte) {
    int[] arrayOfInt = GCMUtil.oneAsInts();
    byte b = 0;
    while (paramLong > 0L) {
      if ((paramLong & 0x1L) != 0L) {
        ensureAvailable(b);
        GCMUtil.multiply(arrayOfInt, this.lookupPowX2.elementAt(b));
      } 
      b++;
      paramLong >>>= 1L;
    } 
    GCMUtil.asBytes(arrayOfInt, paramArrayOfbyte);
  }
  
  private void ensureAvailable(int paramInt) {
    int i = this.lookupPowX2.size();
    if (i <= paramInt) {
      int[] arrayOfInt = this.lookupPowX2.elementAt(i - 1);
      do {
        arrayOfInt = Arrays.clone(arrayOfInt);
        GCMUtil.multiply(arrayOfInt, arrayOfInt);
        this.lookupPowX2.addElement(arrayOfInt);
      } while (++i <= paramInt);
    } 
  }
}
