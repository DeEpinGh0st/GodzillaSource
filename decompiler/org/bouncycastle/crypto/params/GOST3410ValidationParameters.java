package org.bouncycastle.crypto.params;

public class GOST3410ValidationParameters {
  private int x0;
  
  private int c;
  
  private long x0L;
  
  private long cL;
  
  public GOST3410ValidationParameters(int paramInt1, int paramInt2) {
    this.x0 = paramInt1;
    this.c = paramInt2;
  }
  
  public GOST3410ValidationParameters(long paramLong1, long paramLong2) {
    this.x0L = paramLong1;
    this.cL = paramLong2;
  }
  
  public int getC() {
    return this.c;
  }
  
  public int getX0() {
    return this.x0;
  }
  
  public long getCL() {
    return this.cL;
  }
  
  public long getX0L() {
    return this.x0L;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GOST3410ValidationParameters))
      return false; 
    GOST3410ValidationParameters gOST3410ValidationParameters = (GOST3410ValidationParameters)paramObject;
    return (gOST3410ValidationParameters.c != this.c) ? false : ((gOST3410ValidationParameters.x0 != this.x0) ? false : ((gOST3410ValidationParameters.cL != this.cL) ? false : (!(gOST3410ValidationParameters.x0L != this.x0L))));
  }
  
  public int hashCode() {
    return this.x0 ^ this.c ^ (int)this.x0L ^ (int)(this.x0L >> 32L) ^ (int)this.cL ^ (int)(this.cL >> 32L);
  }
}
