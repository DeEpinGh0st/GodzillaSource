package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public abstract class ECFieldElement implements ECConstants {
  public abstract BigInteger toBigInteger();
  
  public abstract String getFieldName();
  
  public abstract int getFieldSize();
  
  public abstract ECFieldElement add(ECFieldElement paramECFieldElement);
  
  public abstract ECFieldElement addOne();
  
  public abstract ECFieldElement subtract(ECFieldElement paramECFieldElement);
  
  public abstract ECFieldElement multiply(ECFieldElement paramECFieldElement);
  
  public abstract ECFieldElement divide(ECFieldElement paramECFieldElement);
  
  public abstract ECFieldElement negate();
  
  public abstract ECFieldElement square();
  
  public abstract ECFieldElement invert();
  
  public abstract ECFieldElement sqrt();
  
  public int bitLength() {
    return toBigInteger().bitLength();
  }
  
  public boolean isOne() {
    return (bitLength() == 1);
  }
  
  public boolean isZero() {
    return (0 == toBigInteger().signum());
  }
  
  public ECFieldElement multiplyMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiply(paramECFieldElement1).subtract(paramECFieldElement2.multiply(paramECFieldElement3));
  }
  
  public ECFieldElement multiplyPlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2, ECFieldElement paramECFieldElement3) {
    return multiply(paramECFieldElement1).add(paramECFieldElement2.multiply(paramECFieldElement3));
  }
  
  public ECFieldElement squareMinusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return square().subtract(paramECFieldElement1.multiply(paramECFieldElement2));
  }
  
  public ECFieldElement squarePlusProduct(ECFieldElement paramECFieldElement1, ECFieldElement paramECFieldElement2) {
    return square().add(paramECFieldElement1.multiply(paramECFieldElement2));
  }
  
  public ECFieldElement squarePow(int paramInt) {
    ECFieldElement eCFieldElement = this;
    for (byte b = 0; b < paramInt; b++)
      eCFieldElement = eCFieldElement.square(); 
    return eCFieldElement;
  }
  
  public boolean testBitZero() {
    return toBigInteger().testBit(0);
  }
  
  public String toString() {
    return toBigInteger().toString(16);
  }
  
  public byte[] getEncoded() {
    return BigIntegers.asUnsignedByteArray((getFieldSize() + 7) / 8, toBigInteger());
  }
  
  public static class F2m extends ECFieldElement {
    public static final int GNB = 1;
    
    public static final int TPB = 2;
    
    public static final int PPB = 3;
    
    private int representation;
    
    private int m;
    
    private int[] ks;
    
    private LongArray x;
    
    public F2m(int param1Int1, int param1Int2, int param1Int3, int param1Int4, BigInteger param1BigInteger) {
      if (param1BigInteger == null || param1BigInteger.signum() < 0 || param1BigInteger.bitLength() > param1Int1)
        throw new IllegalArgumentException("x value invalid in F2m field element"); 
      if (param1Int3 == 0 && param1Int4 == 0) {
        this.representation = 2;
        this.ks = new int[] { param1Int2 };
      } else {
        if (param1Int3 >= param1Int4)
          throw new IllegalArgumentException("k2 must be smaller than k3"); 
        if (param1Int3 <= 0)
          throw new IllegalArgumentException("k2 must be larger than 0"); 
        this.representation = 3;
        this.ks = new int[] { param1Int2, param1Int3, param1Int4 };
      } 
      this.m = param1Int1;
      this.x = new LongArray(param1BigInteger);
    }
    
    public F2m(int param1Int1, int param1Int2, BigInteger param1BigInteger) {
      this(param1Int1, param1Int2, 0, 0, param1BigInteger);
    }
    
    private F2m(int param1Int, int[] param1ArrayOfint, LongArray param1LongArray) {
      this.m = param1Int;
      this.representation = (param1ArrayOfint.length == 1) ? 2 : 3;
      this.ks = param1ArrayOfint;
      this.x = param1LongArray;
    }
    
    public int bitLength() {
      return this.x.degree();
    }
    
    public boolean isOne() {
      return this.x.isOne();
    }
    
    public boolean isZero() {
      return this.x.isZero();
    }
    
    public boolean testBitZero() {
      return this.x.testBitZero();
    }
    
    public BigInteger toBigInteger() {
      return this.x.toBigInteger();
    }
    
    public String getFieldName() {
      return "F2m";
    }
    
    public int getFieldSize() {
      return this.m;
    }
    
    public static void checkFieldElements(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      if (!(param1ECFieldElement1 instanceof F2m) || !(param1ECFieldElement2 instanceof F2m))
        throw new IllegalArgumentException("Field elements are not both instances of ECFieldElement.F2m"); 
      F2m f2m1 = (F2m)param1ECFieldElement1;
      F2m f2m2 = (F2m)param1ECFieldElement2;
      if (f2m1.representation != f2m2.representation)
        throw new IllegalArgumentException("One of the F2m field elements has incorrect representation"); 
      if (f2m1.m != f2m2.m || !Arrays.areEqual(f2m1.ks, f2m2.ks))
        throw new IllegalArgumentException("Field elements are not elements of the same field F2m"); 
    }
    
    public ECFieldElement add(ECFieldElement param1ECFieldElement) {
      LongArray longArray = (LongArray)this.x.clone();
      F2m f2m = (F2m)param1ECFieldElement;
      longArray.addShiftedByWords(f2m.x, 0);
      return new F2m(this.m, this.ks, longArray);
    }
    
    public ECFieldElement addOne() {
      return new F2m(this.m, this.ks, this.x.addOne());
    }
    
    public ECFieldElement subtract(ECFieldElement param1ECFieldElement) {
      return add(param1ECFieldElement);
    }
    
    public ECFieldElement multiply(ECFieldElement param1ECFieldElement) {
      return new F2m(this.m, this.ks, this.x.modMultiply(((F2m)param1ECFieldElement).x, this.m, this.ks));
    }
    
    public ECFieldElement multiplyMinusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement param1ECFieldElement3) {
      return multiplyPlusProduct(param1ECFieldElement1, param1ECFieldElement2, param1ECFieldElement3);
    }
    
    public ECFieldElement multiplyPlusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement param1ECFieldElement3) {
      LongArray longArray1 = this.x;
      LongArray longArray2 = ((F2m)param1ECFieldElement1).x;
      LongArray longArray3 = ((F2m)param1ECFieldElement2).x;
      LongArray longArray4 = ((F2m)param1ECFieldElement3).x;
      LongArray longArray5 = longArray1.multiply(longArray2, this.m, this.ks);
      LongArray longArray6 = longArray3.multiply(longArray4, this.m, this.ks);
      if (longArray5 == longArray1 || longArray5 == longArray2)
        longArray5 = (LongArray)longArray5.clone(); 
      longArray5.addShiftedByWords(longArray6, 0);
      longArray5.reduce(this.m, this.ks);
      return new F2m(this.m, this.ks, longArray5);
    }
    
    public ECFieldElement divide(ECFieldElement param1ECFieldElement) {
      ECFieldElement eCFieldElement = param1ECFieldElement.invert();
      return multiply(eCFieldElement);
    }
    
    public ECFieldElement negate() {
      return this;
    }
    
    public ECFieldElement square() {
      return new F2m(this.m, this.ks, this.x.modSquare(this.m, this.ks));
    }
    
    public ECFieldElement squareMinusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      return squarePlusProduct(param1ECFieldElement1, param1ECFieldElement2);
    }
    
    public ECFieldElement squarePlusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      LongArray longArray1 = this.x;
      LongArray longArray2 = ((F2m)param1ECFieldElement1).x;
      LongArray longArray3 = ((F2m)param1ECFieldElement2).x;
      LongArray longArray4 = longArray1.square(this.m, this.ks);
      LongArray longArray5 = longArray2.multiply(longArray3, this.m, this.ks);
      if (longArray4 == longArray1)
        longArray4 = (LongArray)longArray4.clone(); 
      longArray4.addShiftedByWords(longArray5, 0);
      longArray4.reduce(this.m, this.ks);
      return new F2m(this.m, this.ks, longArray4);
    }
    
    public ECFieldElement squarePow(int param1Int) {
      return (param1Int < 1) ? this : new F2m(this.m, this.ks, this.x.modSquareN(param1Int, this.m, this.ks));
    }
    
    public ECFieldElement invert() {
      return new F2m(this.m, this.ks, this.x.modInverse(this.m, this.ks));
    }
    
    public ECFieldElement sqrt() {
      return (this.x.isZero() || this.x.isOne()) ? this : squarePow(this.m - 1);
    }
    
    public int getRepresentation() {
      return this.representation;
    }
    
    public int getM() {
      return this.m;
    }
    
    public int getK1() {
      return this.ks[0];
    }
    
    public int getK2() {
      return (this.ks.length >= 2) ? this.ks[1] : 0;
    }
    
    public int getK3() {
      return (this.ks.length >= 3) ? this.ks[2] : 0;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof F2m))
        return false; 
      F2m f2m = (F2m)param1Object;
      return (this.m == f2m.m && this.representation == f2m.representation && Arrays.areEqual(this.ks, f2m.ks) && this.x.equals(f2m.x));
    }
    
    public int hashCode() {
      return this.x.hashCode() ^ this.m ^ Arrays.hashCode(this.ks);
    }
  }
  
  public static class Fp extends ECFieldElement {
    BigInteger q;
    
    BigInteger r;
    
    BigInteger x;
    
    static BigInteger calculateResidue(BigInteger param1BigInteger) {
      int i = param1BigInteger.bitLength();
      if (i >= 96) {
        BigInteger bigInteger = param1BigInteger.shiftRight(i - 64);
        if (bigInteger.longValue() == -1L)
          return ONE.shiftLeft(i).subtract(param1BigInteger); 
      } 
      return null;
    }
    
    public Fp(BigInteger param1BigInteger1, BigInteger param1BigInteger2) {
      this(param1BigInteger1, calculateResidue(param1BigInteger1), param1BigInteger2);
    }
    
    Fp(BigInteger param1BigInteger1, BigInteger param1BigInteger2, BigInteger param1BigInteger3) {
      if (param1BigInteger3 == null || param1BigInteger3.signum() < 0 || param1BigInteger3.compareTo(param1BigInteger1) >= 0)
        throw new IllegalArgumentException("x value invalid in Fp field element"); 
      this.q = param1BigInteger1;
      this.r = param1BigInteger2;
      this.x = param1BigInteger3;
    }
    
    public BigInteger toBigInteger() {
      return this.x;
    }
    
    public String getFieldName() {
      return "Fp";
    }
    
    public int getFieldSize() {
      return this.q.bitLength();
    }
    
    public BigInteger getQ() {
      return this.q;
    }
    
    public ECFieldElement add(ECFieldElement param1ECFieldElement) {
      return new Fp(this.q, this.r, modAdd(this.x, param1ECFieldElement.toBigInteger()));
    }
    
    public ECFieldElement addOne() {
      BigInteger bigInteger = this.x.add(ECConstants.ONE);
      if (bigInteger.compareTo(this.q) == 0)
        bigInteger = ECConstants.ZERO; 
      return new Fp(this.q, this.r, bigInteger);
    }
    
    public ECFieldElement subtract(ECFieldElement param1ECFieldElement) {
      return new Fp(this.q, this.r, modSubtract(this.x, param1ECFieldElement.toBigInteger()));
    }
    
    public ECFieldElement multiply(ECFieldElement param1ECFieldElement) {
      return new Fp(this.q, this.r, modMult(this.x, param1ECFieldElement.toBigInteger()));
    }
    
    public ECFieldElement multiplyMinusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement param1ECFieldElement3) {
      BigInteger bigInteger1 = this.x;
      BigInteger bigInteger2 = param1ECFieldElement1.toBigInteger();
      BigInteger bigInteger3 = param1ECFieldElement2.toBigInteger();
      BigInteger bigInteger4 = param1ECFieldElement3.toBigInteger();
      BigInteger bigInteger5 = bigInteger1.multiply(bigInteger2);
      BigInteger bigInteger6 = bigInteger3.multiply(bigInteger4);
      return new Fp(this.q, this.r, modReduce(bigInteger5.subtract(bigInteger6)));
    }
    
    public ECFieldElement multiplyPlusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2, ECFieldElement param1ECFieldElement3) {
      BigInteger bigInteger1 = this.x;
      BigInteger bigInteger2 = param1ECFieldElement1.toBigInteger();
      BigInteger bigInteger3 = param1ECFieldElement2.toBigInteger();
      BigInteger bigInteger4 = param1ECFieldElement3.toBigInteger();
      BigInteger bigInteger5 = bigInteger1.multiply(bigInteger2);
      BigInteger bigInteger6 = bigInteger3.multiply(bigInteger4);
      return new Fp(this.q, this.r, modReduce(bigInteger5.add(bigInteger6)));
    }
    
    public ECFieldElement divide(ECFieldElement param1ECFieldElement) {
      return new Fp(this.q, this.r, modMult(this.x, modInverse(param1ECFieldElement.toBigInteger())));
    }
    
    public ECFieldElement negate() {
      return (this.x.signum() == 0) ? this : new Fp(this.q, this.r, this.q.subtract(this.x));
    }
    
    public ECFieldElement square() {
      return new Fp(this.q, this.r, modMult(this.x, this.x));
    }
    
    public ECFieldElement squareMinusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      BigInteger bigInteger1 = this.x;
      BigInteger bigInteger2 = param1ECFieldElement1.toBigInteger();
      BigInteger bigInteger3 = param1ECFieldElement2.toBigInteger();
      BigInteger bigInteger4 = bigInteger1.multiply(bigInteger1);
      BigInteger bigInteger5 = bigInteger2.multiply(bigInteger3);
      return new Fp(this.q, this.r, modReduce(bigInteger4.subtract(bigInteger5)));
    }
    
    public ECFieldElement squarePlusProduct(ECFieldElement param1ECFieldElement1, ECFieldElement param1ECFieldElement2) {
      BigInteger bigInteger1 = this.x;
      BigInteger bigInteger2 = param1ECFieldElement1.toBigInteger();
      BigInteger bigInteger3 = param1ECFieldElement2.toBigInteger();
      BigInteger bigInteger4 = bigInteger1.multiply(bigInteger1);
      BigInteger bigInteger5 = bigInteger2.multiply(bigInteger3);
      return new Fp(this.q, this.r, modReduce(bigInteger4.add(bigInteger5)));
    }
    
    public ECFieldElement invert() {
      return new Fp(this.q, this.r, modInverse(this.x));
    }
    
    public ECFieldElement sqrt() {
      if (isZero() || isOne())
        return this; 
      if (!this.q.testBit(0))
        throw new RuntimeException("not done yet"); 
      if (this.q.testBit(1)) {
        BigInteger bigInteger = this.q.shiftRight(2).add(ECConstants.ONE);
        return checkSqrt(new Fp(this.q, this.r, this.x.modPow(bigInteger, this.q)));
      } 
      if (this.q.testBit(2)) {
        BigInteger bigInteger6 = this.x.modPow(this.q.shiftRight(3), this.q);
        BigInteger bigInteger7 = modMult(bigInteger6, this.x);
        BigInteger bigInteger8 = modMult(bigInteger7, bigInteger6);
        if (bigInteger8.equals(ECConstants.ONE))
          return checkSqrt(new Fp(this.q, this.r, bigInteger7)); 
        BigInteger bigInteger9 = ECConstants.TWO.modPow(this.q.shiftRight(2), this.q);
        BigInteger bigInteger10 = modMult(bigInteger7, bigInteger9);
        return checkSqrt(new Fp(this.q, this.r, bigInteger10));
      } 
      BigInteger bigInteger1 = this.q.shiftRight(1);
      if (!this.x.modPow(bigInteger1, this.q).equals(ECConstants.ONE))
        return null; 
      BigInteger bigInteger2 = this.x;
      BigInteger bigInteger3 = modDouble(modDouble(bigInteger2));
      BigInteger bigInteger4 = bigInteger1.add(ECConstants.ONE);
      BigInteger bigInteger5 = this.q.subtract(ECConstants.ONE);
      Random random = new Random();
      while (true) {
        BigInteger bigInteger = new BigInteger(this.q.bitLength(), random);
        if (bigInteger.compareTo(this.q) < 0 && modReduce(bigInteger.multiply(bigInteger).subtract(bigInteger3)).modPow(bigInteger1, this.q).equals(bigInteger5)) {
          BigInteger[] arrayOfBigInteger = lucasSequence(bigInteger, bigInteger2, bigInteger4);
          BigInteger bigInteger6 = arrayOfBigInteger[0];
          BigInteger bigInteger7 = arrayOfBigInteger[1];
          if (modMult(bigInteger7, bigInteger7).equals(bigInteger3))
            return new Fp(this.q, this.r, modHalfAbs(bigInteger7)); 
          if (!bigInteger6.equals(ECConstants.ONE) && !bigInteger6.equals(bigInteger5))
            return null; 
        } 
      } 
    }
    
    private ECFieldElement checkSqrt(ECFieldElement param1ECFieldElement) {
      return param1ECFieldElement.square().equals(this) ? param1ECFieldElement : null;
    }
    
    private BigInteger[] lucasSequence(BigInteger param1BigInteger1, BigInteger param1BigInteger2, BigInteger param1BigInteger3) {
      int i = param1BigInteger3.bitLength();
      int j = param1BigInteger3.getLowestSetBit();
      BigInteger bigInteger1 = ECConstants.ONE;
      BigInteger bigInteger2 = ECConstants.TWO;
      BigInteger bigInteger3 = param1BigInteger1;
      BigInteger bigInteger4 = ECConstants.ONE;
      BigInteger bigInteger5 = ECConstants.ONE;
      int k;
      for (k = i - 1; k >= j + 1; k--) {
        bigInteger4 = modMult(bigInteger4, bigInteger5);
        if (param1BigInteger3.testBit(k)) {
          bigInteger5 = modMult(bigInteger4, param1BigInteger2);
          bigInteger1 = modMult(bigInteger1, bigInteger3);
          bigInteger2 = modReduce(bigInteger3.multiply(bigInteger2).subtract(param1BigInteger1.multiply(bigInteger4)));
          bigInteger3 = modReduce(bigInteger3.multiply(bigInteger3).subtract(bigInteger5.shiftLeft(1)));
        } else {
          bigInteger5 = bigInteger4;
          bigInteger1 = modReduce(bigInteger1.multiply(bigInteger2).subtract(bigInteger4));
          bigInteger3 = modReduce(bigInteger3.multiply(bigInteger2).subtract(param1BigInteger1.multiply(bigInteger4)));
          bigInteger2 = modReduce(bigInteger2.multiply(bigInteger2).subtract(bigInteger4.shiftLeft(1)));
        } 
      } 
      bigInteger4 = modMult(bigInteger4, bigInteger5);
      bigInteger5 = modMult(bigInteger4, param1BigInteger2);
      bigInteger1 = modReduce(bigInteger1.multiply(bigInteger2).subtract(bigInteger4));
      bigInteger2 = modReduce(bigInteger3.multiply(bigInteger2).subtract(param1BigInteger1.multiply(bigInteger4)));
      bigInteger4 = modMult(bigInteger4, bigInteger5);
      for (k = 1; k <= j; k++) {
        bigInteger1 = modMult(bigInteger1, bigInteger2);
        bigInteger2 = modReduce(bigInteger2.multiply(bigInteger2).subtract(bigInteger4.shiftLeft(1)));
        bigInteger4 = modMult(bigInteger4, bigInteger4);
      } 
      return new BigInteger[] { bigInteger1, bigInteger2 };
    }
    
    protected BigInteger modAdd(BigInteger param1BigInteger1, BigInteger param1BigInteger2) {
      BigInteger bigInteger = param1BigInteger1.add(param1BigInteger2);
      if (bigInteger.compareTo(this.q) >= 0)
        bigInteger = bigInteger.subtract(this.q); 
      return bigInteger;
    }
    
    protected BigInteger modDouble(BigInteger param1BigInteger) {
      BigInteger bigInteger = param1BigInteger.shiftLeft(1);
      if (bigInteger.compareTo(this.q) >= 0)
        bigInteger = bigInteger.subtract(this.q); 
      return bigInteger;
    }
    
    protected BigInteger modHalf(BigInteger param1BigInteger) {
      if (param1BigInteger.testBit(0))
        param1BigInteger = this.q.add(param1BigInteger); 
      return param1BigInteger.shiftRight(1);
    }
    
    protected BigInteger modHalfAbs(BigInteger param1BigInteger) {
      if (param1BigInteger.testBit(0))
        param1BigInteger = this.q.subtract(param1BigInteger); 
      return param1BigInteger.shiftRight(1);
    }
    
    protected BigInteger modInverse(BigInteger param1BigInteger) {
      int i = getFieldSize();
      int j = i + 31 >> 5;
      int[] arrayOfInt1 = Nat.fromBigInteger(i, this.q);
      int[] arrayOfInt2 = Nat.fromBigInteger(i, param1BigInteger);
      int[] arrayOfInt3 = Nat.create(j);
      Mod.invert(arrayOfInt1, arrayOfInt2, arrayOfInt3);
      return Nat.toBigInteger(j, arrayOfInt3);
    }
    
    protected BigInteger modMult(BigInteger param1BigInteger1, BigInteger param1BigInteger2) {
      return modReduce(param1BigInteger1.multiply(param1BigInteger2));
    }
    
    protected BigInteger modReduce(BigInteger param1BigInteger) {
      if (this.r != null) {
        boolean bool = (param1BigInteger.signum() < 0) ? true : false;
        if (bool)
          param1BigInteger = param1BigInteger.abs(); 
        int i = this.q.bitLength();
        boolean bool1 = this.r.equals(ECConstants.ONE);
        while (param1BigInteger.bitLength() > i + 1) {
          BigInteger bigInteger1 = param1BigInteger.shiftRight(i);
          BigInteger bigInteger2 = param1BigInteger.subtract(bigInteger1.shiftLeft(i));
          if (!bool1)
            bigInteger1 = bigInteger1.multiply(this.r); 
          param1BigInteger = bigInteger1.add(bigInteger2);
        } 
        while (param1BigInteger.compareTo(this.q) >= 0)
          param1BigInteger = param1BigInteger.subtract(this.q); 
        if (bool && param1BigInteger.signum() != 0)
          param1BigInteger = this.q.subtract(param1BigInteger); 
      } else {
        param1BigInteger = param1BigInteger.mod(this.q);
      } 
      return param1BigInteger;
    }
    
    protected BigInteger modSubtract(BigInteger param1BigInteger1, BigInteger param1BigInteger2) {
      BigInteger bigInteger = param1BigInteger1.subtract(param1BigInteger2);
      if (bigInteger.signum() < 0)
        bigInteger = bigInteger.add(this.q); 
      return bigInteger;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Fp))
        return false; 
      Fp fp = (Fp)param1Object;
      return (this.q.equals(fp.q) && this.x.equals(fp.x));
    }
    
    public int hashCode() {
      return this.q.hashCode() ^ this.x.hashCode();
    }
  }
}
