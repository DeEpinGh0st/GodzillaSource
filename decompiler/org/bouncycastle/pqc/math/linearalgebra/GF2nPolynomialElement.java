package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.util.Random;

public class GF2nPolynomialElement extends GF2nElement {
  private static final int[] bitMask = new int[] { 
      1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 
      1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 
      1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 
      1073741824, Integer.MIN_VALUE, 0 };
  
  private GF2Polynomial polynomial;
  
  public GF2nPolynomialElement(GF2nPolynomialField paramGF2nPolynomialField, Random paramRandom) {
    this.mField = paramGF2nPolynomialField;
    this.mDegree = this.mField.getDegree();
    this.polynomial = new GF2Polynomial(this.mDegree);
    randomize(paramRandom);
  }
  
  public GF2nPolynomialElement(GF2nPolynomialField paramGF2nPolynomialField, GF2Polynomial paramGF2Polynomial) {
    this.mField = paramGF2nPolynomialField;
    this.mDegree = this.mField.getDegree();
    this.polynomial = new GF2Polynomial(paramGF2Polynomial);
    this.polynomial.expandN(this.mDegree);
  }
  
  public GF2nPolynomialElement(GF2nPolynomialField paramGF2nPolynomialField, byte[] paramArrayOfbyte) {
    this.mField = paramGF2nPolynomialField;
    this.mDegree = this.mField.getDegree();
    this.polynomial = new GF2Polynomial(this.mDegree, paramArrayOfbyte);
    this.polynomial.expandN(this.mDegree);
  }
  
  public GF2nPolynomialElement(GF2nPolynomialField paramGF2nPolynomialField, int[] paramArrayOfint) {
    this.mField = paramGF2nPolynomialField;
    this.mDegree = this.mField.getDegree();
    this.polynomial = new GF2Polynomial(this.mDegree, paramArrayOfint);
    this.polynomial.expandN(paramGF2nPolynomialField.mDegree);
  }
  
  public GF2nPolynomialElement(GF2nPolynomialElement paramGF2nPolynomialElement) {
    this.mField = paramGF2nPolynomialElement.mField;
    this.mDegree = paramGF2nPolynomialElement.mDegree;
    this.polynomial = new GF2Polynomial(paramGF2nPolynomialElement.polynomial);
  }
  
  public Object clone() {
    return new GF2nPolynomialElement(this);
  }
  
  void assignZero() {
    this.polynomial.assignZero();
  }
  
  public static GF2nPolynomialElement ZERO(GF2nPolynomialField paramGF2nPolynomialField) {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(paramGF2nPolynomialField.getDegree());
    return new GF2nPolynomialElement(paramGF2nPolynomialField, gF2Polynomial);
  }
  
  public static GF2nPolynomialElement ONE(GF2nPolynomialField paramGF2nPolynomialField) {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(paramGF2nPolynomialField.getDegree(), new int[] { 1 });
    return new GF2nPolynomialElement(paramGF2nPolynomialField, gF2Polynomial);
  }
  
  void assignOne() {
    this.polynomial.assignOne();
  }
  
  private void randomize(Random paramRandom) {
    this.polynomial.expandN(this.mDegree);
    this.polynomial.randomize(paramRandom);
  }
  
  public boolean isZero() {
    return this.polynomial.isZero();
  }
  
  public boolean isOne() {
    return this.polynomial.isOne();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2nPolynomialElement))
      return false; 
    GF2nPolynomialElement gF2nPolynomialElement = (GF2nPolynomialElement)paramObject;
    return (this.mField != gF2nPolynomialElement.mField && !this.mField.getFieldPolynomial().equals(gF2nPolynomialElement.mField.getFieldPolynomial())) ? false : this.polynomial.equals(gF2nPolynomialElement.polynomial);
  }
  
  public int hashCode() {
    return this.mField.hashCode() + this.polynomial.hashCode();
  }
  
  private GF2Polynomial getGF2Polynomial() {
    return new GF2Polynomial(this.polynomial);
  }
  
  boolean testBit(int paramInt) {
    return this.polynomial.testBit(paramInt);
  }
  
  public boolean testRightmostBit() {
    return this.polynomial.testBit(0);
  }
  
  public GFElement add(GFElement paramGFElement) throws RuntimeException {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.addToThis(paramGFElement);
    return gF2nPolynomialElement;
  }
  
  public void addToThis(GFElement paramGFElement) throws RuntimeException {
    if (!(paramGFElement instanceof GF2nPolynomialElement))
      throw new RuntimeException(); 
    if (!this.mField.equals(((GF2nPolynomialElement)paramGFElement).mField))
      throw new RuntimeException(); 
    this.polynomial.addToThis(((GF2nPolynomialElement)paramGFElement).polynomial);
  }
  
  public GF2nElement increase() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.increaseThis();
    return gF2nPolynomialElement;
  }
  
  public void increaseThis() {
    this.polynomial.increaseThis();
  }
  
  public GFElement multiply(GFElement paramGFElement) throws RuntimeException {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.multiplyThisBy(paramGFElement);
    return gF2nPolynomialElement;
  }
  
  public void multiplyThisBy(GFElement paramGFElement) throws RuntimeException {
    if (!(paramGFElement instanceof GF2nPolynomialElement))
      throw new RuntimeException(); 
    if (!this.mField.equals(((GF2nPolynomialElement)paramGFElement).mField))
      throw new RuntimeException(); 
    if (equals(paramGFElement)) {
      squareThis();
      return;
    } 
    this.polynomial = this.polynomial.multiply(((GF2nPolynomialElement)paramGFElement).polynomial);
    reduceThis();
  }
  
  public GFElement invert() throws ArithmeticException {
    return invertMAIA();
  }
  
  public GF2nPolynomialElement invertEEA() throws ArithmeticException {
    if (isZero())
      throw new ArithmeticException(); 
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.mDegree + 32, "ONE");
    gF2Polynomial1.reduceN();
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree + 32);
    gF2Polynomial2.reduceN();
    GF2Polynomial gF2Polynomial3 = getGF2Polynomial();
    GF2Polynomial gF2Polynomial4 = this.mField.getFieldPolynomial();
    gF2Polynomial3.reduceN();
    while (!gF2Polynomial3.isOne()) {
      gF2Polynomial3.reduceN();
      gF2Polynomial4.reduceN();
      int i = gF2Polynomial3.getLength() - gF2Polynomial4.getLength();
      if (i < 0) {
        GF2Polynomial gF2Polynomial = gF2Polynomial3;
        gF2Polynomial3 = gF2Polynomial4;
        gF2Polynomial4 = gF2Polynomial;
        gF2Polynomial = gF2Polynomial1;
        gF2Polynomial1 = gF2Polynomial2;
        gF2Polynomial2 = gF2Polynomial;
        i = -i;
        gF2Polynomial2.reduceN();
      } 
      gF2Polynomial3.shiftLeftAddThis(gF2Polynomial4, i);
      gF2Polynomial1.shiftLeftAddThis(gF2Polynomial2, i);
    } 
    gF2Polynomial1.reduceN();
    return new GF2nPolynomialElement((GF2nPolynomialField)this.mField, gF2Polynomial1);
  }
  
  public GF2nPolynomialElement invertSquare() throws ArithmeticException {
    if (isZero())
      throw new ArithmeticException(); 
    int k = this.mField.getDegree() - 1;
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.polynomial.expandN((this.mDegree << 1) + 32);
    gF2nPolynomialElement.polynomial.reduceN();
    int j = 1;
    for (int i = IntegerFunctions.floorLog(k) - 1; i >= 0; i--) {
      GF2nPolynomialElement gF2nPolynomialElement1 = new GF2nPolynomialElement(gF2nPolynomialElement);
      for (byte b = 1; b <= j; b++)
        gF2nPolynomialElement1.squareThisPreCalc(); 
      gF2nPolynomialElement.multiplyThisBy(gF2nPolynomialElement1);
      j <<= 1;
      if ((k & bitMask[i]) != 0) {
        gF2nPolynomialElement.squareThisPreCalc();
        gF2nPolynomialElement.multiplyThisBy(this);
        j++;
      } 
    } 
    gF2nPolynomialElement.squareThisPreCalc();
    return gF2nPolynomialElement;
  }
  
  public GF2nPolynomialElement invertMAIA() throws ArithmeticException {
    if (isZero())
      throw new ArithmeticException(); 
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.mDegree, "ONE");
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree);
    GF2Polynomial gF2Polynomial3 = getGF2Polynomial();
    GF2Polynomial gF2Polynomial4 = this.mField.getFieldPolynomial();
    while (true) {
      while (!gF2Polynomial3.testBit(0)) {
        gF2Polynomial3.shiftRightThis();
        if (!gF2Polynomial1.testBit(0)) {
          gF2Polynomial1.shiftRightThis();
          continue;
        } 
        gF2Polynomial1.addToThis(this.mField.getFieldPolynomial());
        gF2Polynomial1.shiftRightThis();
      } 
      if (gF2Polynomial3.isOne())
        return new GF2nPolynomialElement((GF2nPolynomialField)this.mField, gF2Polynomial1); 
      gF2Polynomial3.reduceN();
      gF2Polynomial4.reduceN();
      if (gF2Polynomial3.getLength() < gF2Polynomial4.getLength()) {
        GF2Polynomial gF2Polynomial = gF2Polynomial3;
        gF2Polynomial3 = gF2Polynomial4;
        gF2Polynomial4 = gF2Polynomial;
        gF2Polynomial = gF2Polynomial1;
        gF2Polynomial1 = gF2Polynomial2;
        gF2Polynomial2 = gF2Polynomial;
      } 
      gF2Polynomial3.addToThis(gF2Polynomial4);
      gF2Polynomial1.addToThis(gF2Polynomial2);
    } 
  }
  
  public GF2nElement square() {
    return squarePreCalc();
  }
  
  public void squareThis() {
    squareThisPreCalc();
  }
  
  public GF2nPolynomialElement squareMatrix() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.squareThisMatrix();
    gF2nPolynomialElement.reduceThis();
    return gF2nPolynomialElement;
  }
  
  public void squareThisMatrix() {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(this.mDegree);
    for (byte b = 0; b < this.mDegree; b++) {
      if (this.polynomial.vectorMult(((GF2nPolynomialField)this.mField).squaringMatrix[this.mDegree - b - 1]))
        gF2Polynomial.setBit(b); 
    } 
    this.polynomial = gF2Polynomial;
  }
  
  public GF2nPolynomialElement squareBitwise() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.squareThisBitwise();
    gF2nPolynomialElement.reduceThis();
    return gF2nPolynomialElement;
  }
  
  public void squareThisBitwise() {
    this.polynomial.squareThisBitwise();
    reduceThis();
  }
  
  public GF2nPolynomialElement squarePreCalc() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.squareThisPreCalc();
    gF2nPolynomialElement.reduceThis();
    return gF2nPolynomialElement;
  }
  
  public void squareThisPreCalc() {
    this.polynomial.squareThisPreCalc();
    reduceThis();
  }
  
  public GF2nPolynomialElement power(int paramInt) {
    if (paramInt == 1)
      return new GF2nPolynomialElement(this); 
    GF2nPolynomialElement gF2nPolynomialElement1 = ONE((GF2nPolynomialField)this.mField);
    if (paramInt == 0)
      return gF2nPolynomialElement1; 
    GF2nPolynomialElement gF2nPolynomialElement2 = new GF2nPolynomialElement(this);
    gF2nPolynomialElement2.polynomial.expandN((gF2nPolynomialElement2.mDegree << 1) + 32);
    gF2nPolynomialElement2.polynomial.reduceN();
    for (byte b = 0; b < this.mDegree; b++) {
      if ((paramInt & 1 << b) != 0)
        gF2nPolynomialElement1.multiplyThisBy(gF2nPolynomialElement2); 
      gF2nPolynomialElement2.square();
    } 
    return gF2nPolynomialElement1;
  }
  
  public GF2nElement squareRoot() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    gF2nPolynomialElement.squareRootThis();
    return gF2nPolynomialElement;
  }
  
  public void squareRootThis() {
    this.polynomial.expandN((this.mDegree << 1) + 32);
    this.polynomial.reduceN();
    for (byte b = 0; b < this.mField.getDegree() - 1; b++)
      squareThis(); 
  }
  
  public GF2nElement solveQuadraticEquation() throws RuntimeException {
    if (isZero())
      return ZERO((GF2nPolynomialField)this.mField); 
    if ((this.mDegree & 0x1) == 1)
      return halfTrace(); 
    while (true) {
      GF2nPolynomialElement gF2nPolynomialElement3 = new GF2nPolynomialElement((GF2nPolynomialField)this.mField, new Random());
      GF2nPolynomialElement gF2nPolynomialElement1 = ZERO((GF2nPolynomialField)this.mField);
      GF2nPolynomialElement gF2nPolynomialElement2 = (GF2nPolynomialElement)gF2nPolynomialElement3.clone();
      for (byte b = 1; b < this.mDegree; b++) {
        gF2nPolynomialElement1.squareThis();
        gF2nPolynomialElement2.squareThis();
        gF2nPolynomialElement1.addToThis(gF2nPolynomialElement2.multiply(this));
        gF2nPolynomialElement2.addToThis(gF2nPolynomialElement3);
      } 
      if (!gF2nPolynomialElement2.isZero()) {
        if (!equals(gF2nPolynomialElement1.square().add(gF2nPolynomialElement1)))
          throw new RuntimeException(); 
        return gF2nPolynomialElement1;
      } 
    } 
  }
  
  public int trace() {
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    for (byte b = 1; b < this.mDegree; b++) {
      gF2nPolynomialElement.squareThis();
      gF2nPolynomialElement.addToThis(this);
    } 
    return gF2nPolynomialElement.isOne() ? 1 : 0;
  }
  
  private GF2nPolynomialElement halfTrace() throws RuntimeException {
    if ((this.mDegree & 0x1) == 0)
      throw new RuntimeException(); 
    GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this);
    for (byte b = 1; b <= this.mDegree - 1 >> 1; b++) {
      gF2nPolynomialElement.squareThis();
      gF2nPolynomialElement.squareThis();
      gF2nPolynomialElement.addToThis(this);
    } 
    return gF2nPolynomialElement;
  }
  
  private void reduceThis() {
    if (this.polynomial.getLength() > this.mDegree) {
      if (((GF2nPolynomialField)this.mField).isTrinomial()) {
        int i;
        try {
          i = ((GF2nPolynomialField)this.mField).getTc();
        } catch (RuntimeException runtimeException) {
          throw new RuntimeException("GF2nPolynomialElement.reduce: the field polynomial is not a trinomial");
        } 
        if (this.mDegree - i <= 32 || this.polynomial.getLength() > this.mDegree << 1) {
          reduceTrinomialBitwise(i);
          return;
        } 
        this.polynomial.reduceTrinomial(this.mDegree, i);
        return;
      } 
      if (((GF2nPolynomialField)this.mField).isPentanomial()) {
        int[] arrayOfInt;
        try {
          arrayOfInt = ((GF2nPolynomialField)this.mField).getPc();
        } catch (RuntimeException runtimeException) {
          throw new RuntimeException("GF2nPolynomialElement.reduce: the field polynomial is not a pentanomial");
        } 
        if (this.mDegree - arrayOfInt[2] <= 32 || this.polynomial.getLength() > this.mDegree << 1) {
          reducePentanomialBitwise(arrayOfInt);
          return;
        } 
        this.polynomial.reducePentanomial(this.mDegree, arrayOfInt);
        return;
      } 
      this.polynomial = this.polynomial.remainder(this.mField.getFieldPolynomial());
      this.polynomial.expandN(this.mDegree);
      return;
    } 
    if (this.polynomial.getLength() < this.mDegree)
      this.polynomial.expandN(this.mDegree); 
  }
  
  private void reduceTrinomialBitwise(int paramInt) {
    int j = this.mDegree - paramInt;
    for (int i = this.polynomial.getLength() - 1; i >= this.mDegree; i--) {
      if (this.polynomial.testBit(i)) {
        this.polynomial.xorBit(i);
        this.polynomial.xorBit(i - j);
        this.polynomial.xorBit(i - this.mDegree);
      } 
    } 
    this.polynomial.reduceN();
    this.polynomial.expandN(this.mDegree);
  }
  
  private void reducePentanomialBitwise(int[] paramArrayOfint) {
    int j = this.mDegree - paramArrayOfint[2];
    int k = this.mDegree - paramArrayOfint[1];
    int m = this.mDegree - paramArrayOfint[0];
    for (int i = this.polynomial.getLength() - 1; i >= this.mDegree; i--) {
      if (this.polynomial.testBit(i)) {
        this.polynomial.xorBit(i);
        this.polynomial.xorBit(i - j);
        this.polynomial.xorBit(i - k);
        this.polynomial.xorBit(i - m);
        this.polynomial.xorBit(i - this.mDegree);
      } 
    } 
    this.polynomial.reduceN();
    this.polynomial.expandN(this.mDegree);
  }
  
  public String toString() {
    return this.polynomial.toString(16);
  }
  
  public String toString(int paramInt) {
    return this.polynomial.toString(paramInt);
  }
  
  public byte[] toByteArray() {
    return this.polynomial.toByteArray();
  }
  
  public BigInteger toFlexiBigInt() {
    return this.polynomial.toFlexiBigInt();
  }
}
