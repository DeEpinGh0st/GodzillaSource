package org.mozilla.javascript;

import java.math.BigInteger;

class DToA {
  static final int DTOSTR_STANDARD = 0;
  static final int DTOSTR_STANDARD_EXPONENTIAL = 1;
  static final int DTOSTR_FIXED = 2;
  static final int DTOSTR_EXPONENTIAL = 3;
  static final int DTOSTR_PRECISION = 4;
  private static final int Frac_mask = 1048575;
  private static final int Exp_shift = 20;
  private static final int Exp_msk1 = 1048576;
  private static final long Frac_maskL = 4503599627370495L;
  private static final int Exp_shiftL = 52;
  private static final long Exp_msk1L = 4503599627370496L;
  private static final int Bias = 1023;
  private static final int P = 53;
  private static final int Exp_shift1 = 20;
  private static final int Exp_mask = 2146435072;
  private static final int Exp_mask_shifted = 2047;
  private static final int Bndry_mask = 1048575;
  private static final int Log2P = 1;
  private static final int Sign_bit = -2147483648;
  private static final int Exp_11 = 1072693248;
  private static final int Ten_pmax = 22;
  private static final int Quick_max = 14;
  private static final int Bletch = 16;
  private static final int Frac_mask1 = 1048575;
  private static final int Int_max = 14;
  private static final int n_bigtens = 5;
  
  private static char BASEDIGIT(int digit) {
    return (char)((digit >= 10) ? (87 + digit) : (48 + digit));
  }



































  
  private static final double[] tens = new double[] { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D };




  
  private static final double[] bigtens = new double[] { 1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D };


  
  private static int lo0bits(int y) {
    int x = y;
    
    if ((x & 0x7) != 0) {
      if ((x & 0x1) != 0)
        return 0; 
      if ((x & 0x2) != 0) {
        return 1;
      }
      return 2;
    } 
    int k = 0;
    if ((x & 0xFFFF) == 0) {
      k = 16;
      x >>>= 16;
    } 
    if ((x & 0xFF) == 0) {
      k += 8;
      x >>>= 8;
    } 
    if ((x & 0xF) == 0) {
      k += 4;
      x >>>= 4;
    } 
    if ((x & 0x3) == 0) {
      k += 2;
      x >>>= 2;
    } 
    if ((x & 0x1) == 0) {
      k++;
      x >>>= 1;
      if ((x & 0x1) == 0)
        return 32; 
    } 
    return k;
  }


  
  private static int hi0bits(int x) {
    int k = 0;
    
    if ((x & 0xFFFF0000) == 0) {
      k = 16;
      x <<= 16;
    } 
    if ((x & 0xFF000000) == 0) {
      k += 8;
      x <<= 8;
    } 
    if ((x & 0xF0000000) == 0) {
      k += 4;
      x <<= 4;
    } 
    if ((x & 0xC0000000) == 0) {
      k += 2;
      x <<= 2;
    } 
    if ((x & Integer.MIN_VALUE) == 0) {
      k++;
      if ((x & 0x40000000) == 0)
        return 32; 
    } 
    return k;
  }

  
  private static void stuffBits(byte[] bits, int offset, int val) {
    bits[offset] = (byte)(val >> 24);
    bits[offset + 1] = (byte)(val >> 16);
    bits[offset + 2] = (byte)(val >> 8);
    bits[offset + 3] = (byte)val;
  }




  
  private static BigInteger d2b(double d, int[] e, int[] bits) {
    byte[] dbl_bits;
    int i, k;
    long dBits = Double.doubleToLongBits(d);
    int d0 = (int)(dBits >>> 32L);
    int d1 = (int)dBits;
    
    int z = d0 & 0xFFFFF;
    d0 &= Integer.MAX_VALUE;
    int de;
    if ((de = d0 >>> 20) != 0)
      z |= 0x100000; 
    int y;
    if ((y = d1) != 0) {
      dbl_bits = new byte[8];
      k = lo0bits(y);
      y >>>= k;
      if (k != 0) {
        stuffBits(dbl_bits, 4, y | z << 32 - k);
        z >>= k;
      } else {
        
        stuffBits(dbl_bits, 4, y);
      }  stuffBits(dbl_bits, 0, z);
      i = (z != 0) ? 2 : 1;
    }
    else {
      
      dbl_bits = new byte[4];
      k = lo0bits(z);
      z >>>= k;
      stuffBits(dbl_bits, 0, z);
      k += 32;
      i = 1;
    } 
    if (de != 0) {
      e[0] = de - 1023 - 52 + k;
      bits[0] = 53 - k;
    } else {
      
      e[0] = de - 1023 - 52 + 1 + k;
      bits[0] = 32 * i - hi0bits(z);
    } 
    return new BigInteger(dbl_bits);
  }
  static String JS_dtobasestr(int base, double d) {
    boolean negative;
    String intDigits;
    if (2 > base || base > 36) {
      throw new IllegalArgumentException("Bad base: " + base);
    }
    
    if (Double.isNaN(d))
      return "NaN"; 
    if (Double.isInfinite(d))
      return (d > 0.0D) ? "Infinity" : "-Infinity"; 
    if (d == 0.0D)
    {
      return "0";
    }

    
    if (d >= 0.0D) {
      negative = false;
    } else {
      negative = true;
      d = -d;
    } 



    
    double dfloor = Math.floor(d);
    long lfloor = (long)dfloor;
    if (lfloor == dfloor) {
      
      intDigits = Long.toString(negative ? -lfloor : lfloor, base);
    } else {
      
      long mantissa, floorBits = Double.doubleToLongBits(dfloor);
      int exp = (int)(floorBits >> 52L) & 0x7FF;
      
      if (exp == 0) {
        mantissa = (floorBits & 0xFFFFFFFFFFFFFL) << 1L;
      } else {
        mantissa = floorBits & 0xFFFFFFFFFFFFFL | 0x10000000000000L;
      } 
      if (negative) {
        mantissa = -mantissa;
      }
      exp -= 1075;
      BigInteger x = BigInteger.valueOf(mantissa);
      if (exp > 0) {
        x = x.shiftLeft(exp);
      } else if (exp < 0) {
        x = x.shiftRight(-exp);
      } 
      intDigits = x.toString(base);
    } 
    
    if (d == dfloor)
    {
      return intDigits;
    }






    
    StringBuilder buffer = new StringBuilder();
    buffer.append(intDigits).append('.');
    double df = d - dfloor;
    
    long dBits = Double.doubleToLongBits(d);
    int word0 = (int)(dBits >> 32L);
    int word1 = (int)dBits;
    
    int[] e = new int[1];
    int[] bbits = new int[1];
    
    BigInteger b = d2b(df, e, bbits);


    
    int s2 = -(word0 >>> 20 & 0x7FF);
    if (s2 == 0)
      s2 = -1; 
    s2 += 1076;

    
    BigInteger mlo = BigInteger.valueOf(1L);
    BigInteger mhi = mlo;
    if (word1 == 0 && (word0 & 0xFFFFF) == 0 && (word0 & 0x7FE00000) != 0) {


      
      s2++;
      mhi = BigInteger.valueOf(2L);
    } 
    
    b = b.shiftLeft(e[0] + s2);
    BigInteger s = BigInteger.valueOf(1L);
    s = s.shiftLeft(s2);




    
    BigInteger bigBase = BigInteger.valueOf(base);
    
    boolean done = false;
    do {
      b = b.multiply(bigBase);
      BigInteger[] divResult = b.divideAndRemainder(s);
      b = divResult[1];
      int digit = (char)divResult[0].intValue();
      if (mlo == mhi) {
        mlo = mhi = mlo.multiply(bigBase);
      } else {
        mlo = mlo.multiply(bigBase);
        mhi = mhi.multiply(bigBase);
      } 

      
      int j = b.compareTo(mlo);
      
      BigInteger delta = s.subtract(mhi);
      int j1 = (delta.signum() <= 0) ? 1 : b.compareTo(delta);
      
      if (j1 == 0 && (word1 & 0x1) == 0) {
        if (j > 0)
          digit++; 
        done = true;
      }
      else if (j < 0 || (j == 0 && (word1 & 0x1) == 0)) {
        if (j1 > 0) {

          
          b = b.shiftLeft(1);
          j1 = b.compareTo(s);
          if (j1 > 0)
          {
            digit++; } 
        } 
        done = true;
      } else if (j1 > 0) {
        digit++;
        done = true;
      } 
      
      buffer.append(BASEDIGIT(digit));
    } while (!done);
    
    return buffer.toString();
  }





































  
  static int word0(double d) {
    long dBits = Double.doubleToLongBits(d);
    return (int)(dBits >> 32L);
  }

  
  static double setWord0(double d, int i) {
    long dBits = Double.doubleToLongBits(d);
    dBits = i << 32L | dBits & 0xFFFFFFFFL;
    return Double.longBitsToDouble(dBits);
  }

  
  static int word1(double d) {
    long dBits = Double.doubleToLongBits(d);
    return (int)dBits;
  }



  
  static BigInteger pow5mult(BigInteger b, int k) {
    return b.multiply(BigInteger.valueOf(5L).pow(k));
  }

  
  static boolean roundOff(StringBuilder buf) {
    int i = buf.length();
    while (i != 0) {
      i--;
      char c = buf.charAt(i);
      if (c != '9') {
        buf.setCharAt(i, (char)(c + 1));
        buf.setLength(i + 1);
        return false;
      } 
    } 
    buf.setLength(0);
    return true;
  }














































  
  static int JS_dtoa(double d, int mode, boolean biasUp, int ndigits, boolean[] sign, StringBuilder buf) {
    int b2, b5, s2, s5;
    char dig;
    double d2;
    boolean denorm;
    int[] be = new int[1];
    int[] bbits = new int[1];


    
    if ((word0(d) & Integer.MIN_VALUE) != 0) {
      
      sign[0] = true;
      
      d = setWord0(d, word0(d) & Integer.MAX_VALUE);
    } else {
      
      sign[0] = false;
    } 
    if ((word0(d) & 0x7FF00000) == 2146435072) {
      
      buf.append((word1(d) == 0 && (word0(d) & 0xFFFFF) == 0) ? "Infinity" : "NaN");
      return 9999;
    } 
    if (d == 0.0D) {
      
      buf.setLength(0);
      buf.append('0');
      return 1;
    } 
    
    BigInteger b = d2b(d, be, bbits); int i;
    if ((i = word0(d) >>> 20 & 0x7FF) != 0) {
      d2 = setWord0(d, word0(d) & 0xFFFFF | 0x3FF00000);




















      
      i -= 1023;
      denorm = false;
    }
    else {
      
      i = bbits[0] + be[0] + 1074;
      long x = (i > 32) ? (word0(d) << 64 - i | (word1(d) >>> i - 32)) : (word1(d) << 32 - i);



      
      d2 = setWord0(x, word0(x) - 32505856);
      i -= 1075;
      denorm = true;
    } 
    
    double ds = (d2 - 1.5D) * 0.289529654602168D + 0.1760912590558D + i * 0.301029995663981D;
    int k = (int)ds;
    if (ds < 0.0D && ds != k)
      k--; 
    boolean k_check = true;
    if (k >= 0 && k <= 22) {
      if (d < tens[k])
        k--; 
      k_check = false;
    } 

    
    int j = bbits[0] - i - 1;
    
    if (j >= 0) {
      b2 = 0;
      s2 = j;
    } else {
      
      b2 = -j;
      s2 = 0;
    } 
    if (k >= 0) {
      b5 = 0;
      s5 = k;
      s2 += k;
    } else {
      
      b2 -= k;
      b5 = -k;
      s5 = 0;
    } 

    
    if (mode < 0 || mode > 9)
      mode = 0; 
    boolean try_quick = true;
    if (mode > 5) {
      mode -= 4;
      try_quick = false;
    } 
    boolean leftright = true;
    int ilim1 = 0, ilim = ilim1;
    switch (mode) {
      case 0:
      case 1:
        ilim = ilim1 = -1;
        i = 18;
        ndigits = 0;
        break;
      case 2:
        leftright = false;
      
      case 4:
        if (ndigits <= 0)
          ndigits = 1; 
        ilim = ilim1 = i = ndigits;
        break;
      case 3:
        leftright = false;
      
      case 5:
        i = ndigits + k + 1;
        ilim = i;
        ilim1 = i - 1;
        if (i <= 0) {
          i = 1;
        }
        break;
    } 

    
    boolean fast_failed = false;
    if (ilim >= 0 && ilim <= 14 && try_quick) {


      
      i = 0;
      d2 = d;
      int k0 = k;
      int ilim0 = ilim;
      int ieps = 2;
      
      if (k > 0) {
        ds = tens[k & 0xF];
        j = k >> 4;
        if ((j & 0x10) != 0) {
          
          j &= 0xF;
          d /= bigtens[4];
          ieps++;
        } 
        for (; j != 0; j >>= 1, i++) {
          if ((j & 0x1) != 0) {
            ieps++;
            ds *= bigtens[i];
          } 
        }  d /= ds;
      } else {
        int j1; if ((j1 = -k) != 0) {
          d *= tens[j1 & 0xF];
          for (j = j1 >> 4; j != 0; j >>= 1, i++) {
            if ((j & 0x1) != 0) {
              ieps++;
              d *= bigtens[i];
            } 
          } 
        } 
      }  if (k_check && d < 1.0D && ilim > 0) {
        if (ilim1 <= 0) {
          fast_failed = true;
        } else {
          ilim = ilim1;
          k--;
          d *= 10.0D;
          ieps++;
        } 
      }


      
      double eps = ieps * d + 7.0D;
      eps = setWord0(eps, word0(eps) - 54525952);
      if (ilim == 0) {
        BigInteger bigInteger1 = null, bigInteger2 = bigInteger1;
        d -= 5.0D;
        if (d > eps) {
          buf.append('1');
          k++;
          return k + 1;
        } 
        if (d < -eps) {
          buf.setLength(0);
          buf.append('0');
          return 1;
        } 
        fast_failed = true;
      } 
      if (!fast_failed) {
        fast_failed = true;
        if (leftright) {


          
          eps = 0.5D / tens[ilim - 1] - eps;
          i = 0; while (true) {
            long L = (long)d;
            d -= L;
            buf.append((char)(int)(48L + L));
            if (d < eps) {
              return k + 1;
            }
            if (1.0D - d < eps) {
              char lastCh;
              
              while (true) {
                lastCh = buf.charAt(buf.length() - 1);
                buf.setLength(buf.length() - 1);
                if (lastCh != '9')
                  break;  if (buf.length() == 0) {
                  k++;
                  lastCh = '0';
                  break;
                } 
              } 
              buf.append((char)(lastCh + 1));
              return k + 1;
            } 
            if (++i >= ilim)
              break; 
            eps *= 10.0D;
            d *= 10.0D;
          }
        
        } else {
          
          eps *= tens[ilim - 1];
          for (i = 1;; i++, d *= 10.0D) {
            long L = (long)d;
            d -= L;
            buf.append((char)(int)(48L + L));
            if (i == ilim) {
              if (d > 0.5D + eps) {
                char lastCh;
                
                while (true) {
                  lastCh = buf.charAt(buf.length() - 1);
                  buf.setLength(buf.length() - 1);
                  if (lastCh != '9')
                    break;  if (buf.length() == 0) {
                    k++;
                    lastCh = '0';
                    break;
                  } 
                } 
                buf.append((char)(lastCh + 1));
                return k + 1;
              } 
              
              if (d < 0.5D - eps) {
                stripTrailingZeroes(buf);

                
                return k + 1;
              } 
              break;
            } 
          } 
        } 
      } 
      if (fast_failed) {
        buf.setLength(0);
        d = d2;
        k = k0;
        ilim = ilim0;
      } 
    } 


    
    if (be[0] >= 0 && k <= 14) {
      
      ds = tens[k];
      if (ndigits < 0 && ilim <= 0) {
        BigInteger bigInteger1 = null, bigInteger2 = bigInteger1;
        if (ilim < 0 || d < 5.0D * ds || (!biasUp && d == 5.0D * ds)) {
          buf.setLength(0);
          buf.append('0');
          return 1;
        } 
        buf.append('1');
        k++;
        return k + 1;
      } 
      for (i = 1;; i++) {
        long L = (long)(d / ds);
        d -= L * ds;
        buf.append((char)(int)(48L + L));
        if (i == ilim) {
          d += d;
          if (d > ds || (d == ds && ((L & 0x1L) != 0L || biasUp))) {
            char lastCh;







            
            while (true) {
              lastCh = buf.charAt(buf.length() - 1);
              buf.setLength(buf.length() - 1);
              if (lastCh != '9')
                break;  if (buf.length() == 0) {
                k++;
                lastCh = '0';
                break;
              } 
            } 
            buf.append((char)(lastCh + 1));
          } 
          break;
        } 
        d *= 10.0D;
        if (d == 0.0D)
          break; 
      } 
      return k + 1;
    } 
    
    int m2 = b2;
    int m5 = b5;
    BigInteger mlo = null, mhi = mlo;
    if (leftright) {
      if (mode < 2) {
        i = denorm ? (be[0] + 1075) : (54 - bbits[0]);
      
      }
      else {
        
        j = ilim - 1;
        if (m5 >= j) {
          m5 -= j;
        } else {
          s5 += j -= m5;
          b5 += j;
          m5 = 0;
        } 
        if ((i = ilim) < 0) {
          m2 -= i;
          i = 0;
        } 
      } 
      
      b2 += i;
      s2 += i;
      mhi = BigInteger.valueOf(1L);
    } 



    
    if (m2 > 0 && s2 > 0) {
      i = (m2 < s2) ? m2 : s2;
      b2 -= i;
      m2 -= i;
      s2 -= i;
    } 

    
    if (b5 > 0) {
      if (leftright) {
        if (m5 > 0) {
          mhi = pow5mult(mhi, m5);
          BigInteger b1 = mhi.multiply(b);
          b = b1;
        } 
        if ((j = b5 - m5) != 0) {
          b = pow5mult(b, j);
        }
      } else {
        b = pow5mult(b, b5);
      } 
    }

    
    BigInteger S = BigInteger.valueOf(1L);
    if (s5 > 0) {
      S = pow5mult(S, s5);
    }


    
    boolean spec_case = false;
    if (mode < 2 && 
      word1(d) == 0 && (word0(d) & 0xFFFFF) == 0 && (word0(d) & 0x7FE00000) != 0) {



      
      b2++;
      s2++;
      spec_case = true;
    } 








    
    byte[] S_bytes = S.toByteArray();
    int S_hiWord = 0;
    for (int idx = 0; idx < 4; idx++) {
      S_hiWord <<= 8;
      if (idx < S_bytes.length)
        S_hiWord |= S_bytes[idx] & 0xFF; 
    } 
    if ((i = ((s5 != 0) ? (32 - hi0bits(S_hiWord)) : 1) + s2 & 0x1F) != 0) {
      i = 32 - i;
    }
    if (i > 4) {
      i -= 4;
      b2 += i;
      m2 += i;
      s2 += i;
    }
    else if (i < 4) {
      i += 28;
      b2 += i;
      m2 += i;
      s2 += i;
    } 
    
    if (b2 > 0)
      b = b.shiftLeft(b2); 
    if (s2 > 0) {
      S = S.shiftLeft(s2);
    }
    
    if (k_check && 
      b.compareTo(S) < 0) {
      k--;
      b = b.multiply(BigInteger.valueOf(10L));
      if (leftright)
        mhi = mhi.multiply(BigInteger.valueOf(10L)); 
      ilim = ilim1;
    } 


    
    if (ilim <= 0 && mode > 2) {

      
      if (ilim < 0 || (i = b.compareTo(S = S.multiply(BigInteger.valueOf(5L)))) < 0 || (i == 0 && !biasUp)) {






        
        buf.setLength(0);
        buf.append('0');
        return 1;
      } 

      
      buf.append('1');
      k++;
      return k + 1;
    } 
    if (leftright) {
      if (m2 > 0) {
        mhi = mhi.shiftLeft(m2);
      }



      
      mlo = mhi;
      if (spec_case) {
        mhi = mlo;
        mhi = mhi.shiftLeft(1);
      } 


      
      for (i = 1;; i++) {
        BigInteger[] divResult = b.divideAndRemainder(S);
        b = divResult[1];
        dig = (char)(divResult[0].intValue() + 48);


        
        j = b.compareTo(mlo);
        
        BigInteger delta = S.subtract(mhi);
        int j1 = (delta.signum() <= 0) ? 1 : b.compareTo(delta);
        
        if (j1 == 0 && mode == 0 && (word1(d) & 0x1) == 0) {
          if (dig == '9') {
            buf.append('9');
            if (roundOff(buf)) {
              k++;
              buf.append('1');
            } 
            return k + 1;
          } 
          
          if (j > 0)
            dig = (char)(dig + 1); 
          buf.append(dig);
          return k + 1;
        } 
        if (j < 0 || (j == 0 && mode == 0 && (word1(d) & 0x1) == 0)) {



          
          if (j1 > 0) {

            
            b = b.shiftLeft(1);
            j1 = b.compareTo(S);
            dig = (char)(dig + 1); if ((j1 > 0 || (j1 == 0 && ((dig & 0x1) == 1 || biasUp))) && dig == '9') {
              
              buf.append('9');
              if (roundOff(buf)) {
                k++;
                buf.append('1');
              } 
              return k + 1;
            } 
          } 
          
          buf.append(dig);
          return k + 1;
        } 
        if (j1 > 0) {
          if (dig == '9') {


            
            buf.append('9');
            if (roundOff(buf)) {
              k++;
              buf.append('1');
            } 
            return k + 1;
          } 
          buf.append((char)(dig + 1));
          return k + 1;
        } 
        buf.append(dig);
        if (i == ilim)
          break; 
        b = b.multiply(BigInteger.valueOf(10L));
        if (mlo == mhi) {
          mlo = mhi = mhi.multiply(BigInteger.valueOf(10L));
        } else {
          mlo = mlo.multiply(BigInteger.valueOf(10L));
          mhi = mhi.multiply(BigInteger.valueOf(10L));
        } 
      } 
    } else {
      
      for (i = 1;; i++) {
        
        BigInteger[] divResult = b.divideAndRemainder(S);
        b = divResult[1];
        dig = (char)(divResult[0].intValue() + 48);
        buf.append(dig);
        if (i >= ilim)
          break; 
        b = b.multiply(BigInteger.valueOf(10L));
      } 
    } 

    
    b = b.shiftLeft(1);
    j = b.compareTo(S);
    if (j > 0 || (j == 0 && ((dig & 0x1) == 1 || biasUp))) {







      
      if (roundOff(buf)) {
        k++;
        buf.append('1');
        return k + 1;
      } 
    } else {
      
      stripTrailingZeroes(buf);
    } 











    
    return k + 1;
  }




  
  private static void stripTrailingZeroes(StringBuilder buf) {
    int bl = buf.length();
    while (bl-- > 0 && buf.charAt(bl) == '0');

    
    buf.setLength(bl + 1);
  }

  
  private static final int[] dtoaModes = new int[] { 0, 0, 3, 2, 2 };








  
  static void JS_dtostr(StringBuilder buffer, int mode, int precision, double d) {
    boolean[] sign = new boolean[1];




    
    if (mode == 2 && (d >= 1.0E21D || d <= -1.0E21D)) {
      mode = 0;
    }
    int decPt = JS_dtoa(d, dtoaModes[mode], (mode >= 2), precision, sign, buffer);
    int nDigits = buffer.length();

    
    if (decPt != 9999) {
      boolean exponentialNotation = false;
      int minNDigits = 0;

      
      switch (mode) {
        case 0:
          if (decPt < -5 || decPt > 21) {
            exponentialNotation = true; break;
          } 
          minNDigits = decPt;
          break;
        
        case 2:
          if (precision >= 0) {
            minNDigits = decPt + precision; break;
          } 
          minNDigits = decPt;
          break;

        
        case 3:
          minNDigits = precision;
        
        case 1:
          exponentialNotation = true;
          break;

        
        case 4:
          minNDigits = precision;
          if (decPt < -5 || decPt > precision) {
            exponentialNotation = true;
          }
          break;
      } 
      
      if (nDigits < minNDigits) {
        int p = minNDigits;
        nDigits = minNDigits;
        do {
          buffer.append('0');
        } while (buffer.length() != p);
      } 
      
      if (exponentialNotation) {
        
        if (nDigits != 1) {
          buffer.insert(1, '.');
        }
        buffer.append('e');
        if (decPt - 1 >= 0)
          buffer.append('+'); 
        buffer.append(decPt - 1);
      }
      else if (decPt != nDigits) {

        
        if (decPt > 0) {
          
          buffer.insert(decPt, '.');
        } else {
          
          for (int i = 0; i < 1 - decPt; i++)
            buffer.insert(0, '0'); 
          buffer.insert(1, '.');
        } 
      } 
    } 

    
    if (sign[0] && (word0(d) != Integer.MIN_VALUE || word1(d) != 0) && ((word0(d) & 0x7FF00000) != 2146435072 || (word1(d) == 0 && (word0(d) & 0xFFFFF) == 0)))
    {

      
      buffer.insert(0, '-');
    }
  }
}
