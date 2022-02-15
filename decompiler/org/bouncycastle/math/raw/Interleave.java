package org.bouncycastle.math.raw;

public class Interleave {
  private static final long M32 = 1431655765L;
  
  private static final long M64 = 6148914691236517205L;
  
  public static int expand8to16(int paramInt) {
    paramInt &= 0xFF;
    paramInt = (paramInt | paramInt << 4) & 0xF0F;
    paramInt = (paramInt | paramInt << 2) & 0x3333;
    return (paramInt | paramInt << 1) & 0x5555;
  }
  
  public static int expand16to32(int paramInt) {
    paramInt &= 0xFFFF;
    paramInt = (paramInt | paramInt << 8) & 0xFF00FF;
    paramInt = (paramInt | paramInt << 4) & 0xF0F0F0F;
    paramInt = (paramInt | paramInt << 2) & 0x33333333;
    return (paramInt | paramInt << 1) & 0x55555555;
  }
  
  public static long expand32to64(int paramInt) {
    int i = (paramInt ^ paramInt >>> 8) & 0xFF00;
    paramInt ^= i ^ i << 8;
    i = (paramInt ^ paramInt >>> 4) & 0xF000F0;
    paramInt ^= i ^ i << 4;
    i = (paramInt ^ paramInt >>> 2) & 0xC0C0C0C;
    paramInt ^= i ^ i << 2;
    i = (paramInt ^ paramInt >>> 1) & 0x22222222;
    paramInt ^= i ^ i << 1;
    return ((paramInt >>> 1) & 0x55555555L) << 32L | paramInt & 0x55555555L;
  }
  
  public static void expand64To128(long paramLong, long[] paramArrayOflong, int paramInt) {
    long l = (paramLong ^ paramLong >>> 16L) & 0xFFFF0000L;
    paramLong ^= l ^ l << 16L;
    l = (paramLong ^ paramLong >>> 8L) & 0xFF000000FF00L;
    paramLong ^= l ^ l << 8L;
    l = (paramLong ^ paramLong >>> 4L) & 0xF000F000F000F0L;
    paramLong ^= l ^ l << 4L;
    l = (paramLong ^ paramLong >>> 2L) & 0xC0C0C0C0C0C0C0CL;
    paramLong ^= l ^ l << 2L;
    l = (paramLong ^ paramLong >>> 1L) & 0x2222222222222222L;
    paramLong ^= l ^ l << 1L;
    paramArrayOflong[paramInt] = paramLong & 0x5555555555555555L;
    paramArrayOflong[paramInt + 1] = paramLong >>> 1L & 0x5555555555555555L;
  }
  
  public static long unshuffle(long paramLong) {
    long l = (paramLong ^ paramLong >>> 1L) & 0x2222222222222222L;
    paramLong ^= l ^ l << 1L;
    l = (paramLong ^ paramLong >>> 2L) & 0xC0C0C0C0C0C0C0CL;
    paramLong ^= l ^ l << 2L;
    l = (paramLong ^ paramLong >>> 4L) & 0xF000F000F000F0L;
    paramLong ^= l ^ l << 4L;
    l = (paramLong ^ paramLong >>> 8L) & 0xFF000000FF00L;
    paramLong ^= l ^ l << 8L;
    l = (paramLong ^ paramLong >>> 16L) & 0xFFFF0000L;
    paramLong ^= l ^ l << 16L;
    return paramLong;
  }
}
