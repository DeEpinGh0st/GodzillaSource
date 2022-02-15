package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410ValidationParameters;

public class GOST3410ParametersGenerator {
  private int size;
  
  private int typeproc;
  
  private SecureRandom init_random;
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  public void init(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    this.size = paramInt1;
    this.typeproc = paramInt2;
    this.init_random = paramSecureRandom;
  }
  
  private int procedure_A(int paramInt1, int paramInt2, BigInteger[] paramArrayOfBigInteger, int paramInt3) {
    while (paramInt1 < 0 || paramInt1 > 65536)
      paramInt1 = this.init_random.nextInt() / 32768; 
    while (true) {
      if (paramInt2 < 0 || paramInt2 > 65536 || paramInt2 / 2 == 0) {
        paramInt2 = this.init_random.nextInt() / 32768 + 1;
        continue;
      } 
      BigInteger bigInteger1 = new BigInteger(Integer.toString(paramInt2));
      BigInteger bigInteger2 = new BigInteger("19381");
      BigInteger[] arrayOfBigInteger1 = new BigInteger[1];
      arrayOfBigInteger1[0] = new BigInteger(Integer.toString(paramInt1));
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = paramInt3;
      int i = 0;
      for (byte b1 = 0; arrayOfInt[b1] >= 17; b1++) {
        int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, arrayOfInt.length);
        arrayOfInt = new int[arrayOfInt1.length];
        System.arraycopy(arrayOfInt1, 0, arrayOfInt, 0, arrayOfInt1.length);
        arrayOfInt[b1 + 1] = arrayOfInt[b1] / 2;
        i = b1 + 1;
      } 
      BigInteger[] arrayOfBigInteger2 = new BigInteger[i + 1];
      arrayOfBigInteger2[i] = new BigInteger("8003", 16);
      int j = i - 1;
      for (byte b2 = 0; b2 < i; b2++) {
        int k = arrayOfInt[j] / 16;
        label50: while (true) {
          BigInteger[] arrayOfBigInteger = new BigInteger[arrayOfBigInteger1.length];
          System.arraycopy(arrayOfBigInteger1, 0, arrayOfBigInteger, 0, arrayOfBigInteger1.length);
          arrayOfBigInteger1 = new BigInteger[k + 1];
          System.arraycopy(arrayOfBigInteger, 0, arrayOfBigInteger1, 0, arrayOfBigInteger.length);
          for (byte b3 = 0; b3 < k; b3++)
            arrayOfBigInteger1[b3 + 1] = arrayOfBigInteger1[b3].multiply(bigInteger2).add(bigInteger1).mod(TWO.pow(16)); 
          BigInteger bigInteger3 = new BigInteger("0");
          for (byte b4 = 0; b4 < k; b4++)
            bigInteger3 = bigInteger3.add(arrayOfBigInteger1[b4].multiply(TWO.pow(16 * b4))); 
          arrayOfBigInteger1[0] = arrayOfBigInteger1[k];
          BigInteger bigInteger4 = TWO.pow(arrayOfInt[j] - 1).divide(arrayOfBigInteger2[j + 1]).add(TWO.pow(arrayOfInt[j] - 1).multiply(bigInteger3).divide(arrayOfBigInteger2[j + 1].multiply(TWO.pow(16 * k))));
          if (bigInteger4.mod(TWO).compareTo(ONE) == 0)
            bigInteger4 = bigInteger4.add(ONE); 
          boolean bool = false;
          while (true) {
            arrayOfBigInteger2[j] = arrayOfBigInteger2[j + 1].multiply(bigInteger4.add(BigInteger.valueOf(bool))).add(ONE);
            if (arrayOfBigInteger2[j].compareTo(TWO.pow(arrayOfInt[j])) == 1)
              continue label50; 
            if (TWO.modPow(arrayOfBigInteger2[j + 1].multiply(bigInteger4.add(BigInteger.valueOf(bool))), arrayOfBigInteger2[j]).compareTo(ONE) == 0 && TWO.modPow(bigInteger4.add(BigInteger.valueOf(bool)), arrayOfBigInteger2[j]).compareTo(ONE) != 0) {
              j--;
            } else {
              bool += true;
              continue;
            } 
            if (j >= 0)
              break; 
            paramArrayOfBigInteger[0] = arrayOfBigInteger2[0];
            paramArrayOfBigInteger[1] = arrayOfBigInteger2[1];
            return arrayOfBigInteger1[0].intValue();
          } 
          break;
        } 
      } 
      return arrayOfBigInteger1[0].intValue();
    } 
  }
  
  private long procedure_Aa(long paramLong1, long paramLong2, BigInteger[] paramArrayOfBigInteger, int paramInt) {
    while (paramLong1 < 0L || paramLong1 > 4294967296L)
      paramLong1 = (this.init_random.nextInt() * 2); 
    while (true) {
      if (paramLong2 < 0L || paramLong2 > 4294967296L || paramLong2 / 2L == 0L) {
        paramLong2 = (this.init_random.nextInt() * 2 + 1);
        continue;
      } 
      BigInteger bigInteger1 = new BigInteger(Long.toString(paramLong2));
      BigInteger bigInteger2 = new BigInteger("97781173");
      BigInteger[] arrayOfBigInteger1 = new BigInteger[1];
      arrayOfBigInteger1[0] = new BigInteger(Long.toString(paramLong1));
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = paramInt;
      int i = 0;
      for (byte b1 = 0; arrayOfInt[b1] >= 33; b1++) {
        int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, arrayOfInt.length);
        arrayOfInt = new int[arrayOfInt1.length];
        System.arraycopy(arrayOfInt1, 0, arrayOfInt, 0, arrayOfInt1.length);
        arrayOfInt[b1 + 1] = arrayOfInt[b1] / 2;
        i = b1 + 1;
      } 
      BigInteger[] arrayOfBigInteger2 = new BigInteger[i + 1];
      arrayOfBigInteger2[i] = new BigInteger("8000000B", 16);
      int j = i - 1;
      for (byte b2 = 0; b2 < i; b2++) {
        int k = arrayOfInt[j] / 32;
        label50: while (true) {
          BigInteger[] arrayOfBigInteger = new BigInteger[arrayOfBigInteger1.length];
          System.arraycopy(arrayOfBigInteger1, 0, arrayOfBigInteger, 0, arrayOfBigInteger1.length);
          arrayOfBigInteger1 = new BigInteger[k + 1];
          System.arraycopy(arrayOfBigInteger, 0, arrayOfBigInteger1, 0, arrayOfBigInteger.length);
          for (byte b3 = 0; b3 < k; b3++)
            arrayOfBigInteger1[b3 + 1] = arrayOfBigInteger1[b3].multiply(bigInteger2).add(bigInteger1).mod(TWO.pow(32)); 
          BigInteger bigInteger3 = new BigInteger("0");
          for (byte b4 = 0; b4 < k; b4++)
            bigInteger3 = bigInteger3.add(arrayOfBigInteger1[b4].multiply(TWO.pow(32 * b4))); 
          arrayOfBigInteger1[0] = arrayOfBigInteger1[k];
          BigInteger bigInteger4 = TWO.pow(arrayOfInt[j] - 1).divide(arrayOfBigInteger2[j + 1]).add(TWO.pow(arrayOfInt[j] - 1).multiply(bigInteger3).divide(arrayOfBigInteger2[j + 1].multiply(TWO.pow(32 * k))));
          if (bigInteger4.mod(TWO).compareTo(ONE) == 0)
            bigInteger4 = bigInteger4.add(ONE); 
          boolean bool = false;
          while (true) {
            arrayOfBigInteger2[j] = arrayOfBigInteger2[j + 1].multiply(bigInteger4.add(BigInteger.valueOf(bool))).add(ONE);
            if (arrayOfBigInteger2[j].compareTo(TWO.pow(arrayOfInt[j])) == 1)
              continue label50; 
            if (TWO.modPow(arrayOfBigInteger2[j + 1].multiply(bigInteger4.add(BigInteger.valueOf(bool))), arrayOfBigInteger2[j]).compareTo(ONE) == 0 && TWO.modPow(bigInteger4.add(BigInteger.valueOf(bool)), arrayOfBigInteger2[j]).compareTo(ONE) != 0) {
              j--;
            } else {
              bool += true;
              continue;
            } 
            if (j >= 0)
              break; 
            paramArrayOfBigInteger[0] = arrayOfBigInteger2[0];
            paramArrayOfBigInteger[1] = arrayOfBigInteger2[1];
            return arrayOfBigInteger1[0].longValue();
          } 
          break;
        } 
      } 
      return arrayOfBigInteger1[0].longValue();
    } 
  }
  
  private void procedure_B(int paramInt1, int paramInt2, BigInteger[] paramArrayOfBigInteger) {
    while (paramInt1 < 0 || paramInt1 > 65536)
      paramInt1 = this.init_random.nextInt() / 32768; 
    while (true) {
      if (paramInt2 < 0 || paramInt2 > 65536 || paramInt2 / 2 == 0) {
        paramInt2 = this.init_random.nextInt() / 32768 + 1;
        continue;
      } 
      BigInteger[] arrayOfBigInteger1 = new BigInteger[2];
      BigInteger bigInteger1 = null;
      BigInteger bigInteger2 = null;
      BigInteger bigInteger3 = null;
      BigInteger bigInteger4 = new BigInteger(Integer.toString(paramInt2));
      BigInteger bigInteger5 = new BigInteger("19381");
      paramInt1 = procedure_A(paramInt1, paramInt2, arrayOfBigInteger1, 256);
      bigInteger1 = arrayOfBigInteger1[0];
      paramInt1 = procedure_A(paramInt1, paramInt2, arrayOfBigInteger1, 512);
      bigInteger2 = arrayOfBigInteger1[0];
      BigInteger[] arrayOfBigInteger2 = new BigInteger[65];
      arrayOfBigInteger2[0] = new BigInteger(Integer.toString(paramInt1));
      char c = 'Ѐ';
      label37: while (true) {
        for (byte b1 = 0; b1 < 64; b1++)
          arrayOfBigInteger2[b1 + 1] = arrayOfBigInteger2[b1].multiply(bigInteger5).add(bigInteger4).mod(TWO.pow(16)); 
        BigInteger bigInteger6 = new BigInteger("0");
        for (byte b2 = 0; b2 < 64; b2++)
          bigInteger6 = bigInteger6.add(arrayOfBigInteger2[b2].multiply(TWO.pow(16 * b2))); 
        arrayOfBigInteger2[0] = arrayOfBigInteger2[64];
        BigInteger bigInteger7 = TWO.pow(c - 1).divide(bigInteger1.multiply(bigInteger2)).add(TWO.pow(c - 1).multiply(bigInteger6).divide(bigInteger1.multiply(bigInteger2).multiply(TWO.pow(1024))));
        if (bigInteger7.mod(TWO).compareTo(ONE) == 0)
          bigInteger7 = bigInteger7.add(ONE); 
        for (boolean bool = false;; bool += true) {
          bigInteger3 = bigInteger1.multiply(bigInteger2).multiply(bigInteger7.add(BigInteger.valueOf(bool))).add(ONE);
          if (bigInteger3.compareTo(TWO.pow(c)) == 1)
            continue label37; 
          if (TWO.modPow(bigInteger1.multiply(bigInteger2).multiply(bigInteger7.add(BigInteger.valueOf(bool))), bigInteger3).compareTo(ONE) == 0 && TWO.modPow(bigInteger1.multiply(bigInteger7.add(BigInteger.valueOf(bool))), bigInteger3).compareTo(ONE) != 0) {
            paramArrayOfBigInteger[0] = bigInteger3;
            paramArrayOfBigInteger[1] = bigInteger1;
            return;
          } 
        } 
        break;
      } 
      break;
    } 
  }
  
  private void procedure_Bb(long paramLong1, long paramLong2, BigInteger[] paramArrayOfBigInteger) {
    while (paramLong1 < 0L || paramLong1 > 4294967296L)
      paramLong1 = (this.init_random.nextInt() * 2); 
    while (true) {
      if (paramLong2 < 0L || paramLong2 > 4294967296L || paramLong2 / 2L == 0L) {
        paramLong2 = (this.init_random.nextInt() * 2 + 1);
        continue;
      } 
      BigInteger[] arrayOfBigInteger1 = new BigInteger[2];
      BigInteger bigInteger1 = null;
      BigInteger bigInteger2 = null;
      BigInteger bigInteger3 = null;
      BigInteger bigInteger4 = new BigInteger(Long.toString(paramLong2));
      BigInteger bigInteger5 = new BigInteger("97781173");
      paramLong1 = procedure_Aa(paramLong1, paramLong2, arrayOfBigInteger1, 256);
      bigInteger1 = arrayOfBigInteger1[0];
      paramLong1 = procedure_Aa(paramLong1, paramLong2, arrayOfBigInteger1, 512);
      bigInteger2 = arrayOfBigInteger1[0];
      BigInteger[] arrayOfBigInteger2 = new BigInteger[33];
      arrayOfBigInteger2[0] = new BigInteger(Long.toString(paramLong1));
      char c = 'Ѐ';
      label37: while (true) {
        for (byte b1 = 0; b1 < 32; b1++)
          arrayOfBigInteger2[b1 + 1] = arrayOfBigInteger2[b1].multiply(bigInteger5).add(bigInteger4).mod(TWO.pow(32)); 
        BigInteger bigInteger6 = new BigInteger("0");
        for (byte b2 = 0; b2 < 32; b2++)
          bigInteger6 = bigInteger6.add(arrayOfBigInteger2[b2].multiply(TWO.pow(32 * b2))); 
        arrayOfBigInteger2[0] = arrayOfBigInteger2[32];
        BigInteger bigInteger7 = TWO.pow(c - 1).divide(bigInteger1.multiply(bigInteger2)).add(TWO.pow(c - 1).multiply(bigInteger6).divide(bigInteger1.multiply(bigInteger2).multiply(TWO.pow(1024))));
        if (bigInteger7.mod(TWO).compareTo(ONE) == 0)
          bigInteger7 = bigInteger7.add(ONE); 
        for (boolean bool = false;; bool += true) {
          bigInteger3 = bigInteger1.multiply(bigInteger2).multiply(bigInteger7.add(BigInteger.valueOf(bool))).add(ONE);
          if (bigInteger3.compareTo(TWO.pow(c)) == 1)
            continue label37; 
          if (TWO.modPow(bigInteger1.multiply(bigInteger2).multiply(bigInteger7.add(BigInteger.valueOf(bool))), bigInteger3).compareTo(ONE) == 0 && TWO.modPow(bigInteger1.multiply(bigInteger7.add(BigInteger.valueOf(bool))), bigInteger3).compareTo(ONE) != 0) {
            paramArrayOfBigInteger[0] = bigInteger3;
            paramArrayOfBigInteger[1] = bigInteger1;
            return;
          } 
        } 
        break;
      } 
      break;
    } 
  }
  
  private BigInteger procedure_C(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = paramBigInteger1.subtract(ONE);
    BigInteger bigInteger2 = bigInteger1.divide(paramBigInteger2);
    int i = paramBigInteger1.bitLength();
    while (true) {
      BigInteger bigInteger = new BigInteger(i, this.init_random);
      if (bigInteger.compareTo(ONE) > 0 && bigInteger.compareTo(bigInteger1) < 0) {
        BigInteger bigInteger3 = bigInteger.modPow(bigInteger2, paramBigInteger1);
        if (bigInteger3.compareTo(ONE) != 0)
          return bigInteger3; 
      } 
    } 
  }
  
  public GOST3410Parameters generateParameters() {
    BigInteger[] arrayOfBigInteger = new BigInteger[2];
    BigInteger bigInteger1 = null;
    BigInteger bigInteger2 = null;
    BigInteger bigInteger3 = null;
    if (this.typeproc == 1) {
      int i = this.init_random.nextInt();
      int j = this.init_random.nextInt();
      switch (this.size) {
        case 512:
          procedure_A(i, j, arrayOfBigInteger, 512);
          bigInteger2 = arrayOfBigInteger[0];
          bigInteger1 = arrayOfBigInteger[1];
          bigInteger3 = procedure_C(bigInteger2, bigInteger1);
          return new GOST3410Parameters(bigInteger2, bigInteger1, bigInteger3, new GOST3410ValidationParameters(i, j));
        case 1024:
          procedure_B(i, j, arrayOfBigInteger);
          bigInteger2 = arrayOfBigInteger[0];
          bigInteger1 = arrayOfBigInteger[1];
          bigInteger3 = procedure_C(bigInteger2, bigInteger1);
          return new GOST3410Parameters(bigInteger2, bigInteger1, bigInteger3, new GOST3410ValidationParameters(i, j));
      } 
      throw new IllegalArgumentException("Ooops! key size 512 or 1024 bit.");
    } 
    long l1 = this.init_random.nextLong();
    long l2 = this.init_random.nextLong();
    switch (this.size) {
      case 512:
        procedure_Aa(l1, l2, arrayOfBigInteger, 512);
        bigInteger2 = arrayOfBigInteger[0];
        bigInteger1 = arrayOfBigInteger[1];
        bigInteger3 = procedure_C(bigInteger2, bigInteger1);
        return new GOST3410Parameters(bigInteger2, bigInteger1, bigInteger3, new GOST3410ValidationParameters(l1, l2));
      case 1024:
        procedure_Bb(l1, l2, arrayOfBigInteger);
        bigInteger2 = arrayOfBigInteger[0];
        bigInteger1 = arrayOfBigInteger[1];
        bigInteger3 = procedure_C(bigInteger2, bigInteger1);
        return new GOST3410Parameters(bigInteger2, bigInteger1, bigInteger3, new GOST3410ValidationParameters(l1, l2));
    } 
    throw new IllegalStateException("Ooops! key size 512 or 1024 bit.");
  }
}
