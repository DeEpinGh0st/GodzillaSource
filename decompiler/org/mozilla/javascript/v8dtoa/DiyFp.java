package org.mozilla.javascript.v8dtoa;






































class DiyFp
{
  private long f;
  private int e;
  static final int kSignificandSize = 64;
  static final long kUint64MSB = -9223372036854775808L;
  
  DiyFp() {
    this.f = 0L;
    this.e = 0;
  }
  
  DiyFp(long f, int e) {
    this.f = f;
    this.e = e;
  }

  
  private static boolean uint64_gte(long a, long b) {
    if (a != b) { if ((((a > b) ? 1 : 0) ^ ((a < 0L) ? 1 : 0) ^ ((b < 0L) ? 1 : 0)) != 0); return false; }
  
  }



  
  void subtract(DiyFp other) {
    assert this.e == other.e;
    assert uint64_gte(this.f, other.f);
    this.f -= other.f;
  }



  
  static DiyFp minus(DiyFp a, DiyFp b) {
    DiyFp result = new DiyFp(a.f, a.e);
    result.subtract(b);
    return result;
  }






  
  void multiply(DiyFp other) {
    long kM32 = 4294967295L;
    long a = this.f >>> 32L;
    long b = this.f & 0xFFFFFFFFL;
    long c = other.f >>> 32L;
    long d = other.f & 0xFFFFFFFFL;
    long ac = a * c;
    long bc = b * c;
    long ad = a * d;
    long bd = b * d;
    long tmp = (bd >>> 32L) + (ad & 0xFFFFFFFFL) + (bc & 0xFFFFFFFFL);

    
    tmp += 2147483648L;
    long result_f = ac + (ad >>> 32L) + (bc >>> 32L) + (tmp >>> 32L);
    this.e += other.e + 64;
    this.f = result_f;
  }

  
  static DiyFp times(DiyFp a, DiyFp b) {
    DiyFp result = new DiyFp(a.f, a.e);
    result.multiply(b);
    return result;
  }
  
  void normalize() {
    assert this.f != 0L;
    long f = this.f;
    int e = this.e;


    
    long k10MSBits = -18014398509481984L;
    while ((f & 0xFFC0000000000000L) == 0L) {
      f <<= 10L;
      e -= 10;
    } 
    while ((f & Long.MIN_VALUE) == 0L) {
      f <<= 1L;
      e--;
    } 
    this.f = f;
    this.e = e;
  }
  
  static DiyFp normalize(DiyFp a) {
    DiyFp result = new DiyFp(a.f, a.e);
    result.normalize();
    return result;
  }
  
  long f() { return this.f; } int e() {
    return this.e;
  }
  void setF(long new_value) { this.f = new_value; } void setE(int new_value) {
    this.e = new_value;
  }
  
  public String toString() {
    return "[DiyFp f:" + this.f + ", e:" + this.e + "]";
  }
}
