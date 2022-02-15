package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GF2nONBElement extends GF2nElement {
  private static final long[] mBitmask = new long[] { 
      1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 
      1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 
      1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 
      1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 
      1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 
      1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 
      1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE };
  
  private static final long[] mMaxmask = new long[] { 
      1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 
      2047L, 4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 524287L, 1048575L, 
      2097151L, 4194303L, 8388607L, 16777215L, 33554431L, 67108863L, 134217727L, 268435455L, 536870911L, 1073741823L, 
      2147483647L, 4294967295L, 8589934591L, 17179869183L, 34359738367L, 68719476735L, 137438953471L, 274877906943L, 549755813887L, 1099511627775L, 
      2199023255551L, 4398046511103L, 8796093022207L, 17592186044415L, 35184372088831L, 70368744177663L, 140737488355327L, 281474976710655L, 562949953421311L, 1125899906842623L, 
      2251799813685247L, 4503599627370495L, 9007199254740991L, 18014398509481983L, 36028797018963967L, 72057594037927935L, 144115188075855871L, 288230376151711743L, 576460752303423487L, 1152921504606846975L, 
      2305843009213693951L, 4611686018427387903L, Long.MAX_VALUE, -1L };
  
  private static final int[] mIBY64 = new int[] { 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
      2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 5, 5 };
  
  private static final int MAXLONG = 64;
  
  private int mLength;
  
  private int mBit;
  
  private long[] mPol;
  
  public GF2nONBElement(GF2nONBField paramGF2nONBField, SecureRandom paramSecureRandom) {
    this.mField = paramGF2nONBField;
    this.mDegree = this.mField.getDegree();
    this.mLength = paramGF2nONBField.getONBLength();
    this.mBit = paramGF2nONBField.getONBBit();
    this.mPol = new long[this.mLength];
    if (this.mLength > 1) {
      for (byte b = 0; b < this.mLength - 1; b++)
        this.mPol[b] = paramSecureRandom.nextLong(); 
      long l = paramSecureRandom.nextLong();
      this.mPol[this.mLength - 1] = l >>> 64 - this.mBit;
    } else {
      this.mPol[0] = paramSecureRandom.nextLong();
      this.mPol[0] = this.mPol[0] >>> 64 - this.mBit;
    } 
  }
  
  public GF2nONBElement(GF2nONBField paramGF2nONBField, byte[] paramArrayOfbyte) {
    this.mField = paramGF2nONBField;
    this.mDegree = this.mField.getDegree();
    this.mLength = paramGF2nONBField.getONBLength();
    this.mBit = paramGF2nONBField.getONBBit();
    this.mPol = new long[this.mLength];
    assign(paramArrayOfbyte);
  }
  
  public GF2nONBElement(GF2nONBField paramGF2nONBField, BigInteger paramBigInteger) {
    this.mField = paramGF2nONBField;
    this.mDegree = this.mField.getDegree();
    this.mLength = paramGF2nONBField.getONBLength();
    this.mBit = paramGF2nONBField.getONBBit();
    this.mPol = new long[this.mLength];
    assign(paramBigInteger);
  }
  
  private GF2nONBElement(GF2nONBField paramGF2nONBField, long[] paramArrayOflong) {
    this.mField = paramGF2nONBField;
    this.mDegree = this.mField.getDegree();
    this.mLength = paramGF2nONBField.getONBLength();
    this.mBit = paramGF2nONBField.getONBBit();
    this.mPol = paramArrayOflong;
  }
  
  public GF2nONBElement(GF2nONBElement paramGF2nONBElement) {
    this.mField = paramGF2nONBElement.mField;
    this.mDegree = this.mField.getDegree();
    this.mLength = ((GF2nONBField)this.mField).getONBLength();
    this.mBit = ((GF2nONBField)this.mField).getONBBit();
    this.mPol = new long[this.mLength];
    assign(paramGF2nONBElement.getElement());
  }
  
  public Object clone() {
    return new GF2nONBElement(this);
  }
  
  public static GF2nONBElement ZERO(GF2nONBField paramGF2nONBField) {
    long[] arrayOfLong = new long[paramGF2nONBField.getONBLength()];
    return new GF2nONBElement(paramGF2nONBField, arrayOfLong);
  }
  
  public static GF2nONBElement ONE(GF2nONBField paramGF2nONBField) {
    int i = paramGF2nONBField.getONBLength();
    long[] arrayOfLong = new long[i];
    for (byte b = 0; b < i - 1; b++)
      arrayOfLong[b] = -1L; 
    arrayOfLong[i - 1] = mMaxmask[paramGF2nONBField.getONBBit() - 1];
    return new GF2nONBElement(paramGF2nONBField, arrayOfLong);
  }
  
  void assignZero() {
    this.mPol = new long[this.mLength];
  }
  
  void assignOne() {
    for (byte b = 0; b < this.mLength - 1; b++)
      this.mPol[b] = -1L; 
    this.mPol[this.mLength - 1] = mMaxmask[this.mBit - 1];
  }
  
  private void assign(BigInteger paramBigInteger) {
    assign(paramBigInteger.toByteArray());
  }
  
  private void assign(long[] paramArrayOflong) {
    System.arraycopy(paramArrayOflong, 0, this.mPol, 0, this.mLength);
  }
  
  private void assign(byte[] paramArrayOfbyte) {
    this.mPol = new long[this.mLength];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      this.mPol[b >>> 3] = this.mPol[b >>> 3] | (paramArrayOfbyte[paramArrayOfbyte.length - 1 - b] & 0xFFL) << (b & 0x7) << 3; 
  }
  
  public boolean isZero() {
    boolean bool = true;
    for (byte b = 0; b < this.mLength && bool; b++)
      bool = (bool && (this.mPol[b] & 0xFFFFFFFFFFFFFFFFL) == 0L) ? true : false; 
    return bool;
  }
  
  public boolean isOne() {
    boolean bool = true;
    for (byte b = 0; b < this.mLength - 1 && bool; b++)
      bool = (bool && (this.mPol[b] & 0xFFFFFFFFFFFFFFFFL) == -1L) ? true : false; 
    if (bool)
      bool = (bool && (this.mPol[this.mLength - 1] & mMaxmask[this.mBit - 1]) == mMaxmask[this.mBit - 1]) ? true : false; 
    return bool;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2nONBElement))
      return false; 
    GF2nONBElement gF2nONBElement = (GF2nONBElement)paramObject;
    for (byte b = 0; b < this.mLength; b++) {
      if (this.mPol[b] != gF2nONBElement.mPol[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    return this.mPol.hashCode();
  }
  
  public boolean testRightmostBit() {
    return ((this.mPol[this.mLength - 1] & mBitmask[this.mBit - 1]) != 0L);
  }
  
  boolean testBit(int paramInt) {
    if (paramInt < 0 || paramInt > this.mDegree)
      return false; 
    long l = this.mPol[paramInt >>> 6] & mBitmask[paramInt & 0x3F];
    return (l != 0L);
  }
  
  private long[] getElement() {
    long[] arrayOfLong = new long[this.mPol.length];
    System.arraycopy(this.mPol, 0, arrayOfLong, 0, this.mPol.length);
    return arrayOfLong;
  }
  
  private long[] getElementReverseOrder() {
    long[] arrayOfLong = new long[this.mPol.length];
    for (byte b = 0; b < this.mDegree; b++) {
      if (testBit(this.mDegree - b - 1))
        arrayOfLong[b >>> 6] = arrayOfLong[b >>> 6] | mBitmask[b & 0x3F]; 
    } 
    return arrayOfLong;
  }
  
  void reverseOrder() {
    this.mPol = getElementReverseOrder();
  }
  
  public GFElement add(GFElement paramGFElement) throws RuntimeException {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.addToThis(paramGFElement);
    return gF2nONBElement;
  }
  
  public void addToThis(GFElement paramGFElement) throws RuntimeException {
    if (!(paramGFElement instanceof GF2nONBElement))
      throw new RuntimeException(); 
    if (!this.mField.equals(((GF2nONBElement)paramGFElement).mField))
      throw new RuntimeException(); 
    for (byte b = 0; b < this.mLength; b++)
      this.mPol[b] = this.mPol[b] ^ ((GF2nONBElement)paramGFElement).mPol[b]; 
  }
  
  public GF2nElement increase() {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.increaseThis();
    return gF2nONBElement;
  }
  
  public void increaseThis() {
    addToThis(ONE((GF2nONBField)this.mField));
  }
  
  public GFElement multiply(GFElement paramGFElement) throws RuntimeException {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.multiplyThisBy(paramGFElement);
    return gF2nONBElement;
  }
  
  public void multiplyThisBy(GFElement paramGFElement) throws RuntimeException {
    if (!(paramGFElement instanceof GF2nONBElement))
      throw new RuntimeException("The elements have different representation: not yet implemented"); 
    if (!this.mField.equals(((GF2nONBElement)paramGFElement).mField))
      throw new RuntimeException(); 
    if (equals(paramGFElement)) {
      squareThis();
    } else {
      long[] arrayOfLong1 = this.mPol;
      long[] arrayOfLong2 = ((GF2nONBElement)paramGFElement).mPol;
      long[] arrayOfLong3 = new long[this.mLength];
      int[][] arrayOfInt = ((GF2nONBField)this.mField).mMult;
      int i = this.mLength - 1;
      int j = this.mBit - 1;
      int k = 0;
      long l1 = mBitmask[63];
      long l2 = mBitmask[j];
      for (byte b = 0; b < this.mDegree; b++) {
        k = 0;
        int i1;
        for (i1 = 0; i1 < this.mDegree; i1++) {
          int i2 = mIBY64[i1];
          int i4 = i1 & 0x3F;
          int i3 = mIBY64[arrayOfInt[i1][0]];
          int i5 = arrayOfInt[i1][0] & 0x3F;
          if ((arrayOfLong1[i2] & mBitmask[i4]) != 0L) {
            if ((arrayOfLong2[i3] & mBitmask[i5]) != 0L)
              k ^= 0x1; 
            if (arrayOfInt[i1][1] != -1) {
              i3 = mIBY64[arrayOfInt[i1][1]];
              i5 = arrayOfInt[i1][1] & 0x3F;
              if ((arrayOfLong2[i3] & mBitmask[i5]) != 0L)
                k ^= 0x1; 
            } 
          } 
        } 
        int m = mIBY64[b];
        int n = b & 0x3F;
        if (k != 0)
          arrayOfLong3[m] = arrayOfLong3[m] ^ mBitmask[n]; 
        if (this.mLength > 1) {
          boolean bool = ((arrayOfLong1[i] & 0x1L) == 1L) ? true : false;
          for (i1 = i - 1; i1 >= 0; i1--) {
            boolean bool1 = ((arrayOfLong1[i1] & 0x1L) != 0L) ? true : false;
            arrayOfLong1[i1] = arrayOfLong1[i1] >>> 1L;
            if (bool)
              arrayOfLong1[i1] = arrayOfLong1[i1] ^ l1; 
            bool = bool1;
          } 
          arrayOfLong1[i] = arrayOfLong1[i] >>> 1L;
          if (bool)
            arrayOfLong1[i] = arrayOfLong1[i] ^ l2; 
          bool = ((arrayOfLong2[i] & 0x1L) == 1L) ? true : false;
          for (i1 = i - 1; i1 >= 0; i1--) {
            boolean bool1 = ((arrayOfLong2[i1] & 0x1L) != 0L) ? true : false;
            arrayOfLong2[i1] = arrayOfLong2[i1] >>> 1L;
            if (bool)
              arrayOfLong2[i1] = arrayOfLong2[i1] ^ l1; 
            bool = bool1;
          } 
          arrayOfLong2[i] = arrayOfLong2[i] >>> 1L;
          if (bool)
            arrayOfLong2[i] = arrayOfLong2[i] ^ l2; 
        } else {
          boolean bool = ((arrayOfLong1[0] & 0x1L) == 1L) ? true : false;
          arrayOfLong1[0] = arrayOfLong1[0] >>> 1L;
          if (bool)
            arrayOfLong1[0] = arrayOfLong1[0] ^ l2; 
          bool = ((arrayOfLong2[0] & 0x1L) == 1L) ? true : false;
          arrayOfLong2[0] = arrayOfLong2[0] >>> 1L;
          if (bool)
            arrayOfLong2[0] = arrayOfLong2[0] ^ l2; 
        } 
      } 
      assign(arrayOfLong3);
    } 
  }
  
  public GF2nElement square() {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.squareThis();
    return gF2nONBElement;
  }
  
  public void squareThis() {
    long[] arrayOfLong = getElement();
    int i = this.mLength - 1;
    int j = this.mBit - 1;
    long l = mBitmask[63];
    boolean bool1 = ((arrayOfLong[i] & mBitmask[j]) != 0L) ? true : false;
    for (byte b = 0; b < i; b++) {
      boolean bool = ((arrayOfLong[b] & l) != 0L) ? true : false;
      arrayOfLong[b] = arrayOfLong[b] << 1L;
      if (bool1)
        arrayOfLong[b] = arrayOfLong[b] ^ 0x1L; 
      bool1 = bool;
    } 
    boolean bool2 = ((arrayOfLong[i] & mBitmask[j]) != 0L) ? true : false;
    arrayOfLong[i] = arrayOfLong[i] << 1L;
    if (bool1)
      arrayOfLong[i] = arrayOfLong[i] ^ 0x1L; 
    if (bool2)
      arrayOfLong[i] = arrayOfLong[i] ^ mBitmask[j + 1]; 
    assign(arrayOfLong);
  }
  
  public GFElement invert() throws ArithmeticException {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.invertThis();
    return gF2nONBElement;
  }
  
  public void invertThis() throws ArithmeticException {
    if (isZero())
      throw new ArithmeticException(); 
    byte b = 31;
    boolean bool = false;
    while (!bool && b >= 0) {
      if (((this.mDegree - 1) & mBitmask[b]) != 0L)
        bool = true; 
      b--;
    } 
    b++;
    GF2nONBElement gF2nONBElement1 = ZERO((GF2nONBField)this.mField);
    GF2nONBElement gF2nONBElement2 = new GF2nONBElement(this);
    int i = 1;
    for (int j = b - 1; j >= 0; j--) {
      GF2nElement gF2nElement = (GF2nElement)gF2nONBElement2.clone();
      for (byte b1 = 1; b1 <= i; b1++)
        gF2nElement.squareThis(); 
      gF2nONBElement2.multiplyThisBy(gF2nElement);
      i <<= 1;
      if (((this.mDegree - 1) & mBitmask[j]) != 0L) {
        gF2nONBElement2.squareThis();
        gF2nONBElement2.multiplyThisBy(this);
        i++;
      } 
    } 
    gF2nONBElement2.squareThis();
  }
  
  public GF2nElement squareRoot() {
    GF2nONBElement gF2nONBElement = new GF2nONBElement(this);
    gF2nONBElement.squareRootThis();
    return gF2nONBElement;
  }
  
  public void squareRootThis() {
    long[] arrayOfLong = getElement();
    int i = this.mLength - 1;
    int j = this.mBit - 1;
    long l = mBitmask[63];
    boolean bool = ((arrayOfLong[0] & 0x1L) != 0L) ? true : false;
    for (int k = i; k >= 0; k--) {
      boolean bool1 = ((arrayOfLong[k] & 0x1L) != 0L) ? true : false;
      arrayOfLong[k] = arrayOfLong[k] >>> 1L;
      if (bool)
        if (k == i) {
          arrayOfLong[k] = arrayOfLong[k] ^ mBitmask[j];
        } else {
          arrayOfLong[k] = arrayOfLong[k] ^ l;
        }  
      bool = bool1;
    } 
    assign(arrayOfLong);
  }
  
  public int trace() {
    int i = 0;
    int j = this.mLength - 1;
    int k;
    for (k = 0; k < j; k++) {
      for (byte b1 = 0; b1 < 64; b1++) {
        if ((this.mPol[k] & mBitmask[b1]) != 0L)
          i ^= 0x1; 
      } 
    } 
    k = this.mBit;
    for (byte b = 0; b < k; b++) {
      if ((this.mPol[j] & mBitmask[b]) != 0L)
        i ^= 0x1; 
    } 
    return i;
  }
  
  public GF2nElement solveQuadraticEquation() throws RuntimeException {
    if (trace() == 1)
      throw new RuntimeException(); 
    long l1 = mBitmask[63];
    long l2 = 0L;
    long l3 = 1L;
    long[] arrayOfLong = new long[this.mLength];
    long l4 = 0L;
    byte b = 1;
    int i;
    for (i = 0; i < this.mLength - 1; i++) {
      for (b = 1; b < 64; b++) {
        if (((mBitmask[b] & this.mPol[i]) == l2 || (l4 & mBitmask[b - 1]) == l2) && ((this.mPol[i] & mBitmask[b]) != l2 || (l4 & mBitmask[b - 1]) != l2))
          l4 ^= mBitmask[b]; 
      } 
      arrayOfLong[i] = l4;
      if (((l1 & l4) != l2 && (l3 & this.mPol[i + 1]) == l3) || ((l1 & l4) == l2 && (l3 & this.mPol[i + 1]) == l2)) {
        l4 = l2;
      } else {
        l4 = l3;
      } 
    } 
    i = this.mDegree & 0x3F;
    long l5 = this.mPol[this.mLength - 1];
    for (b = 1; b < i; b++) {
      if (((mBitmask[b] & l5) == l2 || (mBitmask[b - 1] & l4) == l2) && ((mBitmask[b] & l5) != l2 || (mBitmask[b - 1] & l4) != l2))
        l4 ^= mBitmask[b]; 
    } 
    arrayOfLong[this.mLength - 1] = l4;
    return new GF2nONBElement((GF2nONBField)this.mField, arrayOfLong);
  }
  
  public String toString() {
    return toString(16);
  }
  
  public String toString(int paramInt) {
    String str = "";
    long[] arrayOfLong = getElement();
    int i = this.mBit;
    if (paramInt == 2) {
      int j;
      for (j = i - 1; j >= 0; j--) {
        if ((arrayOfLong[arrayOfLong.length - 1] & 1L << j) == 0L) {
          str = str + "0";
        } else {
          str = str + "1";
        } 
      } 
      for (j = arrayOfLong.length - 2; j >= 0; j--) {
        for (byte b = 63; b >= 0; b--) {
          if ((arrayOfLong[j] & mBitmask[b]) == 0L) {
            str = str + "0";
          } else {
            str = str + "1";
          } 
        } 
      } 
    } else if (paramInt == 16) {
      char[] arrayOfChar = { 
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
          'a', 'b', 'c', 'd', 'e', 'f' };
      for (int j = arrayOfLong.length - 1; j >= 0; j--) {
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 60L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 56L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 52L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 48L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 44L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 40L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 36L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 32L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 28L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 24L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 20L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 16L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 12L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 8L) & 0xF];
        str = str + arrayOfChar[(int)(arrayOfLong[j] >>> 4L) & 0xF];
        str = str + arrayOfChar[(int)arrayOfLong[j] & 0xF];
        str = str + " ";
      } 
    } 
    return str;
  }
  
  public BigInteger toFlexiBigInt() {
    return new BigInteger(1, toByteArray());
  }
  
  public byte[] toByteArray() {
    int i = (this.mDegree - 1 >> 3) + 1;
    byte[] arrayOfByte = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte[i - b - 1] = (byte)(int)((this.mPol[b >>> 3] & 255L << (b & 0x7) << 3) >>> (b & 0x7) << 3); 
    return arrayOfByte;
  }
}
