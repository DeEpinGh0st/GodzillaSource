package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.util.Random;

public class GF2Polynomial {
  private int len;
  
  private int blocks;
  
  private int[] value;
  
  private static Random rand = new Random();
  
  private static final boolean[] parity = new boolean[] { 
      false, true, true, false, true, false, false, true, true, false, 
      false, true, false, true, true, false, true, false, false, true, 
      false, true, true, false, false, true, true, false, true, false, 
      false, true, true, false, false, true, false, true, true, false, 
      false, true, true, false, true, false, false, true, false, true, 
      true, false, true, false, false, true, true, false, false, true, 
      false, true, true, false, true, false, false, true, false, true, 
      true, false, false, true, true, false, true, false, false, true, 
      false, true, true, false, true, false, false, true, true, false, 
      false, true, false, true, true, false, false, true, true, false, 
      true, false, false, true, true, false, false, true, false, true, 
      true, false, true, false, false, true, false, true, true, false, 
      false, true, true, false, true, false, false, true, true, false, 
      false, true, false, true, true, false, false, true, true, false, 
      true, false, false, true, false, true, true, false, true, false, 
      false, true, true, false, false, true, false, true, true, false, 
      false, true, true, false, true, false, false, true, true, false, 
      false, true, false, true, true, false, true, false, false, true, 
      false, true, true, false, false, true, true, false, true, false, 
      false, true, false, true, true, false, true, false, false, true, 
      true, false, false, true, false, true, true, false, true, false, 
      false, true, false, true, true, false, false, true, true, false, 
      true, false, false, true, true, false, false, true, false, true, 
      true, false, false, true, true, false, true, false, false, true, 
      false, true, true, false, true, false, false, true, true, false, 
      false, true, false, true, true, false };
  
  private static final short[] squaringTable = new short[] { 
      0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 
      68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 
      272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 
      340, 341, 1024, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 
      1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 
      1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 
      1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 
      4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 
      4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 
      4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 
      5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 
      5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 
      5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 
      16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 
      16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 
      16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 
      17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 
      17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 
      17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 
      17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 
      20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 
      20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 
      20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 
      21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 
      21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 
      21828, 21829, 21840, 21841, 21844, 21845 };
  
  private static final int[] bitMask = new int[] { 
      1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 
      1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 
      1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 
      1073741824, Integer.MIN_VALUE, 0 };
  
  private static final int[] reverseRightMask = new int[] { 
      0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 
      1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 
      1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 
      1073741823, Integer.MAX_VALUE, -1 };
  
  public GF2Polynomial(int paramInt) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
  }
  
  public GF2Polynomial(int paramInt, Random paramRandom) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
    randomize(paramRandom);
  }
  
  public GF2Polynomial(int paramInt, String paramString) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
    if (paramString.equalsIgnoreCase("ZERO")) {
      assignZero();
    } else if (paramString.equalsIgnoreCase("ONE")) {
      assignOne();
    } else if (paramString.equalsIgnoreCase("RANDOM")) {
      randomize();
    } else if (paramString.equalsIgnoreCase("X")) {
      assignX();
    } else if (paramString.equalsIgnoreCase("ALL")) {
      assignAll();
    } else {
      throw new IllegalArgumentException("Error: GF2Polynomial was called using " + paramString + " as value!");
    } 
  }
  
  public GF2Polynomial(int paramInt, int[] paramArrayOfint) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
    int j = Math.min(this.blocks, paramArrayOfint.length);
    System.arraycopy(paramArrayOfint, 0, this.value, 0, j);
    zeroUnusedBits();
  }
  
  public GF2Polynomial(int paramInt, byte[] paramArrayOfbyte) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
    int m = Math.min((paramArrayOfbyte.length - 1 >> 2) + 1, this.blocks);
    int j;
    for (j = 0; j < m - 1; j++) {
      int n = paramArrayOfbyte.length - (j << 2) - 1;
      this.value[j] = paramArrayOfbyte[n] & 0xFF;
      this.value[j] = this.value[j] | paramArrayOfbyte[n - 1] << 8 & 0xFF00;
      this.value[j] = this.value[j] | paramArrayOfbyte[n - 2] << 16 & 0xFF0000;
      this.value[j] = this.value[j] | paramArrayOfbyte[n - 3] << 24 & 0xFF000000;
    } 
    j = m - 1;
    int k = paramArrayOfbyte.length - (j << 2) - 1;
    this.value[j] = paramArrayOfbyte[k] & 0xFF;
    if (k > 0)
      this.value[j] = this.value[j] | paramArrayOfbyte[k - 1] << 8 & 0xFF00; 
    if (k > 1)
      this.value[j] = this.value[j] | paramArrayOfbyte[k - 2] << 16 & 0xFF0000; 
    if (k > 2)
      this.value[j] = this.value[j] | paramArrayOfbyte[k - 3] << 24 & 0xFF000000; 
    zeroUnusedBits();
    reduceN();
  }
  
  public GF2Polynomial(int paramInt, BigInteger paramBigInteger) {
    int i = paramInt;
    if (i < 1)
      i = 1; 
    this.blocks = (i - 1 >> 5) + 1;
    this.value = new int[this.blocks];
    this.len = i;
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      arrayOfByte = arrayOfByte1;
    } 
    int j = arrayOfByte.length & 0x3;
    int k = (arrayOfByte.length - 1 >> 2) + 1;
    byte b;
    for (b = 0; b < j; b++)
      this.value[k - 1] = this.value[k - 1] | (arrayOfByte[b] & 0xFF) << j - 1 - b << 3; 
    int m = 0;
    for (b = 0; b <= arrayOfByte.length - 4 >> 2; b++) {
      m = arrayOfByte.length - 1 - (b << 2);
      this.value[b] = arrayOfByte[m] & 0xFF;
      this.value[b] = this.value[b] | arrayOfByte[m - 1] << 8 & 0xFF00;
      this.value[b] = this.value[b] | arrayOfByte[m - 2] << 16 & 0xFF0000;
      this.value[b] = this.value[b] | arrayOfByte[m - 3] << 24 & 0xFF000000;
    } 
    if ((this.len & 0x1F) != 0)
      this.value[this.blocks - 1] = this.value[this.blocks - 1] & reverseRightMask[this.len & 0x1F]; 
    reduceN();
  }
  
  public GF2Polynomial(GF2Polynomial paramGF2Polynomial) {
    this.len = paramGF2Polynomial.len;
    this.blocks = paramGF2Polynomial.blocks;
    this.value = IntUtils.clone(paramGF2Polynomial.value);
  }
  
  public Object clone() {
    return new GF2Polynomial(this);
  }
  
  public int getLength() {
    return this.len;
  }
  
  public int[] toIntegerArray() {
    int[] arrayOfInt = new int[this.blocks];
    System.arraycopy(this.value, 0, arrayOfInt, 0, this.blocks);
    return arrayOfInt;
  }
  
  public String toString(int paramInt) {
    char[] arrayOfChar = { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f' };
    String[] arrayOfString = { 
        "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", 
        "1010", "1011", "1100", "1101", "1110", "1111" };
    String str = new String();
    if (paramInt == 16) {
      for (int i = this.blocks - 1; i >= 0; i--) {
        str = str + arrayOfChar[this.value[i] >>> 28 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 24 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 20 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 16 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 12 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 8 & 0xF];
        str = str + arrayOfChar[this.value[i] >>> 4 & 0xF];
        str = str + arrayOfChar[this.value[i] & 0xF];
        str = str + " ";
      } 
    } else {
      for (int i = this.blocks - 1; i >= 0; i--) {
        str = str + arrayOfString[this.value[i] >>> 28 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 24 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 20 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 16 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 12 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 8 & 0xF];
        str = str + arrayOfString[this.value[i] >>> 4 & 0xF];
        str = str + arrayOfString[this.value[i] & 0xF];
        str = str + " ";
      } 
    } 
    return str;
  }
  
  public byte[] toByteArray() {
    int i = (this.len - 1 >> 3) + 1;
    int j = i & 0x3;
    byte[] arrayOfByte = new byte[i];
    byte b;
    for (b = 0; b < i >> 2; b++) {
      int k = i - (b << 2) - 1;
      arrayOfByte[k] = (byte)(this.value[b] & 0xFF);
      arrayOfByte[k - 1] = (byte)((this.value[b] & 0xFF00) >>> 8);
      arrayOfByte[k - 2] = (byte)((this.value[b] & 0xFF0000) >>> 16);
      arrayOfByte[k - 3] = (byte)((this.value[b] & 0xFF000000) >>> 24);
    } 
    for (b = 0; b < j; b++) {
      int k = j - b - 1 << 3;
      arrayOfByte[b] = (byte)((this.value[this.blocks - 1] & 255 << k) >>> k);
    } 
    return arrayOfByte;
  }
  
  public BigInteger toFlexiBigInt() {
    return (this.len == 0 || isZero()) ? new BigInteger(0, new byte[0]) : new BigInteger(1, toByteArray());
  }
  
  public void assignOne() {
    for (byte b = 1; b < this.blocks; b++)
      this.value[b] = 0; 
    this.value[0] = 1;
  }
  
  public void assignX() {
    for (byte b = 1; b < this.blocks; b++)
      this.value[b] = 0; 
    this.value[0] = 2;
  }
  
  public void assignAll() {
    for (byte b = 0; b < this.blocks; b++)
      this.value[b] = -1; 
    zeroUnusedBits();
  }
  
  public void assignZero() {
    for (byte b = 0; b < this.blocks; b++)
      this.value[b] = 0; 
  }
  
  public void randomize() {
    for (byte b = 0; b < this.blocks; b++)
      this.value[b] = rand.nextInt(); 
    zeroUnusedBits();
  }
  
  public void randomize(Random paramRandom) {
    for (byte b = 0; b < this.blocks; b++)
      this.value[b] = paramRandom.nextInt(); 
    zeroUnusedBits();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2Polynomial))
      return false; 
    GF2Polynomial gF2Polynomial = (GF2Polynomial)paramObject;
    if (this.len != gF2Polynomial.len)
      return false; 
    for (byte b = 0; b < this.blocks; b++) {
      if (this.value[b] != gF2Polynomial.value[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    return this.len + this.value.hashCode();
  }
  
  public boolean isZero() {
    if (this.len == 0)
      return true; 
    for (byte b = 0; b < this.blocks; b++) {
      if (this.value[b] != 0)
        return false; 
    } 
    return true;
  }
  
  public boolean isOne() {
    for (byte b = 1; b < this.blocks; b++) {
      if (this.value[b] != 0)
        return false; 
    } 
    return !(this.value[0] != 1);
  }
  
  public void addToThis(GF2Polynomial paramGF2Polynomial) {
    expandN(paramGF2Polynomial.len);
    xorThisBy(paramGF2Polynomial);
  }
  
  public GF2Polynomial add(GF2Polynomial paramGF2Polynomial) {
    return xor(paramGF2Polynomial);
  }
  
  public void subtractFromThis(GF2Polynomial paramGF2Polynomial) {
    expandN(paramGF2Polynomial.len);
    xorThisBy(paramGF2Polynomial);
  }
  
  public GF2Polynomial subtract(GF2Polynomial paramGF2Polynomial) {
    return xor(paramGF2Polynomial);
  }
  
  public void increaseThis() {
    xorBit(0);
  }
  
  public GF2Polynomial increase() {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(this);
    gF2Polynomial.increaseThis();
    return gF2Polynomial;
  }
  
  public GF2Polynomial multiplyClassic(GF2Polynomial paramGF2Polynomial) {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(Math.max(this.len, paramGF2Polynomial.len) << 1);
    GF2Polynomial[] arrayOfGF2Polynomial = new GF2Polynomial[32];
    arrayOfGF2Polynomial[0] = new GF2Polynomial(this);
    byte b;
    for (b = 1; b <= 31; b++)
      arrayOfGF2Polynomial[b] = arrayOfGF2Polynomial[b - 1].shiftLeft(); 
    for (b = 0; b < paramGF2Polynomial.blocks; b++) {
      byte b1;
      for (b1 = 0; b1 <= 31; b1++) {
        if ((paramGF2Polynomial.value[b] & bitMask[b1]) != 0)
          gF2Polynomial.xorThisBy(arrayOfGF2Polynomial[b1]); 
      } 
      for (b1 = 0; b1 <= 31; b1++)
        arrayOfGF2Polynomial[b1].shiftBlocksLeft(); 
    } 
    return gF2Polynomial;
  }
  
  public GF2Polynomial multiply(GF2Polynomial paramGF2Polynomial) {
    int i = Math.max(this.len, paramGF2Polynomial.len);
    expandN(i);
    paramGF2Polynomial.expandN(i);
    return karaMult(paramGF2Polynomial);
  }
  
  private GF2Polynomial karaMult(GF2Polynomial paramGF2Polynomial) {
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.len << 1);
    if (this.len <= 32) {
      gF2Polynomial1.value = mult32(this.value[0], paramGF2Polynomial.value[0]);
      return gF2Polynomial1;
    } 
    if (this.len <= 64) {
      gF2Polynomial1.value = mult64(this.value, paramGF2Polynomial.value);
      return gF2Polynomial1;
    } 
    if (this.len <= 128) {
      gF2Polynomial1.value = mult128(this.value, paramGF2Polynomial.value);
      return gF2Polynomial1;
    } 
    if (this.len <= 256) {
      gF2Polynomial1.value = mult256(this.value, paramGF2Polynomial.value);
      return gF2Polynomial1;
    } 
    if (this.len <= 512) {
      gF2Polynomial1.value = mult512(this.value, paramGF2Polynomial.value);
      return gF2Polynomial1;
    } 
    int i = IntegerFunctions.floorLog(this.len - 1);
    i = bitMask[i];
    GF2Polynomial gF2Polynomial2 = lower((i - 1 >> 5) + 1);
    GF2Polynomial gF2Polynomial3 = upper((i - 1 >> 5) + 1);
    GF2Polynomial gF2Polynomial4 = paramGF2Polynomial.lower((i - 1 >> 5) + 1);
    GF2Polynomial gF2Polynomial5 = paramGF2Polynomial.upper((i - 1 >> 5) + 1);
    GF2Polynomial gF2Polynomial6 = gF2Polynomial3.karaMult(gF2Polynomial5);
    GF2Polynomial gF2Polynomial7 = gF2Polynomial2.karaMult(gF2Polynomial4);
    gF2Polynomial2.addToThis(gF2Polynomial3);
    gF2Polynomial4.addToThis(gF2Polynomial5);
    GF2Polynomial gF2Polynomial8 = gF2Polynomial2.karaMult(gF2Polynomial4);
    gF2Polynomial1.shiftLeftAddThis(gF2Polynomial6, i << 1);
    gF2Polynomial1.shiftLeftAddThis(gF2Polynomial6, i);
    gF2Polynomial1.shiftLeftAddThis(gF2Polynomial8, i);
    gF2Polynomial1.shiftLeftAddThis(gF2Polynomial7, i);
    gF2Polynomial1.addToThis(gF2Polynomial7);
    return gF2Polynomial1;
  }
  
  private static int[] mult512(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1 = new int[32];
    int[] arrayOfInt2 = new int[8];
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt2, 0, Math.min(8, paramArrayOfint1.length));
    int[] arrayOfInt3 = new int[8];
    if (paramArrayOfint1.length > 8)
      System.arraycopy(paramArrayOfint1, 8, arrayOfInt3, 0, Math.min(8, paramArrayOfint1.length - 8)); 
    int[] arrayOfInt4 = new int[8];
    System.arraycopy(paramArrayOfint2, 0, arrayOfInt4, 0, Math.min(8, paramArrayOfint2.length));
    int[] arrayOfInt5 = new int[8];
    if (paramArrayOfint2.length > 8)
      System.arraycopy(paramArrayOfint2, 8, arrayOfInt5, 0, Math.min(8, paramArrayOfint2.length - 8)); 
    int[] arrayOfInt6 = mult256(arrayOfInt3, arrayOfInt5);
    arrayOfInt1[31] = arrayOfInt1[31] ^ arrayOfInt6[15];
    arrayOfInt1[30] = arrayOfInt1[30] ^ arrayOfInt6[14];
    arrayOfInt1[29] = arrayOfInt1[29] ^ arrayOfInt6[13];
    arrayOfInt1[28] = arrayOfInt1[28] ^ arrayOfInt6[12];
    arrayOfInt1[27] = arrayOfInt1[27] ^ arrayOfInt6[11];
    arrayOfInt1[26] = arrayOfInt1[26] ^ arrayOfInt6[10];
    arrayOfInt1[25] = arrayOfInt1[25] ^ arrayOfInt6[9];
    arrayOfInt1[24] = arrayOfInt1[24] ^ arrayOfInt6[8];
    arrayOfInt1[23] = arrayOfInt1[23] ^ arrayOfInt6[7] ^ arrayOfInt6[15];
    arrayOfInt1[22] = arrayOfInt1[22] ^ arrayOfInt6[6] ^ arrayOfInt6[14];
    arrayOfInt1[21] = arrayOfInt1[21] ^ arrayOfInt6[5] ^ arrayOfInt6[13];
    arrayOfInt1[20] = arrayOfInt1[20] ^ arrayOfInt6[4] ^ arrayOfInt6[12];
    arrayOfInt1[19] = arrayOfInt1[19] ^ arrayOfInt6[3] ^ arrayOfInt6[11];
    arrayOfInt1[18] = arrayOfInt1[18] ^ arrayOfInt6[2] ^ arrayOfInt6[10];
    arrayOfInt1[17] = arrayOfInt1[17] ^ arrayOfInt6[1] ^ arrayOfInt6[9];
    arrayOfInt1[16] = arrayOfInt1[16] ^ arrayOfInt6[0] ^ arrayOfInt6[8];
    arrayOfInt1[15] = arrayOfInt1[15] ^ arrayOfInt6[7];
    arrayOfInt1[14] = arrayOfInt1[14] ^ arrayOfInt6[6];
    arrayOfInt1[13] = arrayOfInt1[13] ^ arrayOfInt6[5];
    arrayOfInt1[12] = arrayOfInt1[12] ^ arrayOfInt6[4];
    arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt6[3];
    arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt6[2];
    arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt6[1];
    arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt6[0];
    arrayOfInt3[0] = arrayOfInt3[0] ^ arrayOfInt2[0];
    arrayOfInt3[1] = arrayOfInt3[1] ^ arrayOfInt2[1];
    arrayOfInt3[2] = arrayOfInt3[2] ^ arrayOfInt2[2];
    arrayOfInt3[3] = arrayOfInt3[3] ^ arrayOfInt2[3];
    arrayOfInt3[4] = arrayOfInt3[4] ^ arrayOfInt2[4];
    arrayOfInt3[5] = arrayOfInt3[5] ^ arrayOfInt2[5];
    arrayOfInt3[6] = arrayOfInt3[6] ^ arrayOfInt2[6];
    arrayOfInt3[7] = arrayOfInt3[7] ^ arrayOfInt2[7];
    arrayOfInt5[0] = arrayOfInt5[0] ^ arrayOfInt4[0];
    arrayOfInt5[1] = arrayOfInt5[1] ^ arrayOfInt4[1];
    arrayOfInt5[2] = arrayOfInt5[2] ^ arrayOfInt4[2];
    arrayOfInt5[3] = arrayOfInt5[3] ^ arrayOfInt4[3];
    arrayOfInt5[4] = arrayOfInt5[4] ^ arrayOfInt4[4];
    arrayOfInt5[5] = arrayOfInt5[5] ^ arrayOfInt4[5];
    arrayOfInt5[6] = arrayOfInt5[6] ^ arrayOfInt4[6];
    arrayOfInt5[7] = arrayOfInt5[7] ^ arrayOfInt4[7];
    int[] arrayOfInt7 = mult256(arrayOfInt3, arrayOfInt5);
    arrayOfInt1[23] = arrayOfInt1[23] ^ arrayOfInt7[15];
    arrayOfInt1[22] = arrayOfInt1[22] ^ arrayOfInt7[14];
    arrayOfInt1[21] = arrayOfInt1[21] ^ arrayOfInt7[13];
    arrayOfInt1[20] = arrayOfInt1[20] ^ arrayOfInt7[12];
    arrayOfInt1[19] = arrayOfInt1[19] ^ arrayOfInt7[11];
    arrayOfInt1[18] = arrayOfInt1[18] ^ arrayOfInt7[10];
    arrayOfInt1[17] = arrayOfInt1[17] ^ arrayOfInt7[9];
    arrayOfInt1[16] = arrayOfInt1[16] ^ arrayOfInt7[8];
    arrayOfInt1[15] = arrayOfInt1[15] ^ arrayOfInt7[7];
    arrayOfInt1[14] = arrayOfInt1[14] ^ arrayOfInt7[6];
    arrayOfInt1[13] = arrayOfInt1[13] ^ arrayOfInt7[5];
    arrayOfInt1[12] = arrayOfInt1[12] ^ arrayOfInt7[4];
    arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt7[3];
    arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt7[2];
    arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt7[1];
    arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt7[0];
    int[] arrayOfInt8 = mult256(arrayOfInt2, arrayOfInt4);
    arrayOfInt1[23] = arrayOfInt1[23] ^ arrayOfInt8[15];
    arrayOfInt1[22] = arrayOfInt1[22] ^ arrayOfInt8[14];
    arrayOfInt1[21] = arrayOfInt1[21] ^ arrayOfInt8[13];
    arrayOfInt1[20] = arrayOfInt1[20] ^ arrayOfInt8[12];
    arrayOfInt1[19] = arrayOfInt1[19] ^ arrayOfInt8[11];
    arrayOfInt1[18] = arrayOfInt1[18] ^ arrayOfInt8[10];
    arrayOfInt1[17] = arrayOfInt1[17] ^ arrayOfInt8[9];
    arrayOfInt1[16] = arrayOfInt1[16] ^ arrayOfInt8[8];
    arrayOfInt1[15] = arrayOfInt1[15] ^ arrayOfInt8[7] ^ arrayOfInt8[15];
    arrayOfInt1[14] = arrayOfInt1[14] ^ arrayOfInt8[6] ^ arrayOfInt8[14];
    arrayOfInt1[13] = arrayOfInt1[13] ^ arrayOfInt8[5] ^ arrayOfInt8[13];
    arrayOfInt1[12] = arrayOfInt1[12] ^ arrayOfInt8[4] ^ arrayOfInt8[12];
    arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt8[3] ^ arrayOfInt8[11];
    arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt8[2] ^ arrayOfInt8[10];
    arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt8[1] ^ arrayOfInt8[9];
    arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt8[0] ^ arrayOfInt8[8];
    arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt8[7];
    arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt8[6];
    arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt8[5];
    arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt8[4];
    arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt8[3];
    arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt8[2];
    arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt8[1];
    arrayOfInt1[0] = arrayOfInt1[0] ^ arrayOfInt8[0];
    return arrayOfInt1;
  }
  
  private static int[] mult256(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1 = new int[16];
    int[] arrayOfInt2 = new int[4];
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt2, 0, Math.min(4, paramArrayOfint1.length));
    int[] arrayOfInt3 = new int[4];
    if (paramArrayOfint1.length > 4)
      System.arraycopy(paramArrayOfint1, 4, arrayOfInt3, 0, Math.min(4, paramArrayOfint1.length - 4)); 
    int[] arrayOfInt4 = new int[4];
    System.arraycopy(paramArrayOfint2, 0, arrayOfInt4, 0, Math.min(4, paramArrayOfint2.length));
    int[] arrayOfInt5 = new int[4];
    if (paramArrayOfint2.length > 4)
      System.arraycopy(paramArrayOfint2, 4, arrayOfInt5, 0, Math.min(4, paramArrayOfint2.length - 4)); 
    if (arrayOfInt3[3] == 0 && arrayOfInt3[2] == 0 && arrayOfInt5[3] == 0 && arrayOfInt5[2] == 0) {
      if (arrayOfInt3[1] == 0 && arrayOfInt5[1] == 0) {
        if (arrayOfInt3[0] != 0 || arrayOfInt5[0] != 0) {
          int[] arrayOfInt = mult32(arrayOfInt3[0], arrayOfInt5[0]);
          arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt[1];
          arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt[0];
          arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[1];
          arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[0];
        } 
      } else {
        int[] arrayOfInt = mult64(arrayOfInt3, arrayOfInt5);
        arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt[3];
        arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt[2];
        arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt[1];
        arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt[0];
        arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt[3];
        arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt[2];
        arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[1];
        arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[0];
      } 
    } else {
      int[] arrayOfInt = mult128(arrayOfInt3, arrayOfInt5);
      arrayOfInt1[15] = arrayOfInt1[15] ^ arrayOfInt[7];
      arrayOfInt1[14] = arrayOfInt1[14] ^ arrayOfInt[6];
      arrayOfInt1[13] = arrayOfInt1[13] ^ arrayOfInt[5];
      arrayOfInt1[12] = arrayOfInt1[12] ^ arrayOfInt[4];
      arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt[3] ^ arrayOfInt[7];
      arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt[2] ^ arrayOfInt[6];
      arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt[1] ^ arrayOfInt[5];
      arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt[0] ^ arrayOfInt[4];
      arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt[3];
      arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt[2];
      arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[1];
      arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[0];
    } 
    arrayOfInt3[0] = arrayOfInt3[0] ^ arrayOfInt2[0];
    arrayOfInt3[1] = arrayOfInt3[1] ^ arrayOfInt2[1];
    arrayOfInt3[2] = arrayOfInt3[2] ^ arrayOfInt2[2];
    arrayOfInt3[3] = arrayOfInt3[3] ^ arrayOfInt2[3];
    arrayOfInt5[0] = arrayOfInt5[0] ^ arrayOfInt4[0];
    arrayOfInt5[1] = arrayOfInt5[1] ^ arrayOfInt4[1];
    arrayOfInt5[2] = arrayOfInt5[2] ^ arrayOfInt4[2];
    arrayOfInt5[3] = arrayOfInt5[3] ^ arrayOfInt4[3];
    int[] arrayOfInt6 = mult128(arrayOfInt3, arrayOfInt5);
    arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt6[7];
    arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt6[6];
    arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt6[5];
    arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt6[4];
    arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt6[3];
    arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt6[2];
    arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt6[1];
    arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt6[0];
    int[] arrayOfInt7 = mult128(arrayOfInt2, arrayOfInt4);
    arrayOfInt1[11] = arrayOfInt1[11] ^ arrayOfInt7[7];
    arrayOfInt1[10] = arrayOfInt1[10] ^ arrayOfInt7[6];
    arrayOfInt1[9] = arrayOfInt1[9] ^ arrayOfInt7[5];
    arrayOfInt1[8] = arrayOfInt1[8] ^ arrayOfInt7[4];
    arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt7[3] ^ arrayOfInt7[7];
    arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt7[2] ^ arrayOfInt7[6];
    arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt7[1] ^ arrayOfInt7[5];
    arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt7[0] ^ arrayOfInt7[4];
    arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt7[3];
    arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt7[2];
    arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt7[1];
    arrayOfInt1[0] = arrayOfInt1[0] ^ arrayOfInt7[0];
    return arrayOfInt1;
  }
  
  private static int[] mult128(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1 = new int[8];
    int[] arrayOfInt2 = new int[2];
    System.arraycopy(paramArrayOfint1, 0, arrayOfInt2, 0, Math.min(2, paramArrayOfint1.length));
    int[] arrayOfInt3 = new int[2];
    if (paramArrayOfint1.length > 2)
      System.arraycopy(paramArrayOfint1, 2, arrayOfInt3, 0, Math.min(2, paramArrayOfint1.length - 2)); 
    int[] arrayOfInt4 = new int[2];
    System.arraycopy(paramArrayOfint2, 0, arrayOfInt4, 0, Math.min(2, paramArrayOfint2.length));
    int[] arrayOfInt5 = new int[2];
    if (paramArrayOfint2.length > 2)
      System.arraycopy(paramArrayOfint2, 2, arrayOfInt5, 0, Math.min(2, paramArrayOfint2.length - 2)); 
    if (arrayOfInt3[1] == 0 && arrayOfInt5[1] == 0) {
      if (arrayOfInt3[0] != 0 || arrayOfInt5[0] != 0) {
        int[] arrayOfInt = mult32(arrayOfInt3[0], arrayOfInt5[0]);
        arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[1];
        arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[0];
        arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
        arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0];
      } 
    } else {
      int[] arrayOfInt = mult64(arrayOfInt3, arrayOfInt5);
      arrayOfInt1[7] = arrayOfInt1[7] ^ arrayOfInt[3];
      arrayOfInt1[6] = arrayOfInt1[6] ^ arrayOfInt[2];
      arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[1] ^ arrayOfInt[3];
      arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[0] ^ arrayOfInt[2];
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0];
    } 
    arrayOfInt3[0] = arrayOfInt3[0] ^ arrayOfInt2[0];
    arrayOfInt3[1] = arrayOfInt3[1] ^ arrayOfInt2[1];
    arrayOfInt5[0] = arrayOfInt5[0] ^ arrayOfInt4[0];
    arrayOfInt5[1] = arrayOfInt5[1] ^ arrayOfInt4[1];
    if (arrayOfInt3[1] == 0 && arrayOfInt5[1] == 0) {
      int[] arrayOfInt = mult32(arrayOfInt3[0], arrayOfInt5[0]);
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0];
    } else {
      int[] arrayOfInt = mult64(arrayOfInt3, arrayOfInt5);
      arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[3];
      arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[2];
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0];
    } 
    if (arrayOfInt2[1] == 0 && arrayOfInt4[1] == 0) {
      int[] arrayOfInt = mult32(arrayOfInt2[0], arrayOfInt4[0]);
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0];
      arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt[1];
      arrayOfInt1[0] = arrayOfInt1[0] ^ arrayOfInt[0];
    } else {
      int[] arrayOfInt = mult64(arrayOfInt2, arrayOfInt4);
      arrayOfInt1[5] = arrayOfInt1[5] ^ arrayOfInt[3];
      arrayOfInt1[4] = arrayOfInt1[4] ^ arrayOfInt[2];
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1] ^ arrayOfInt[3];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0] ^ arrayOfInt[2];
      arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt[1];
      arrayOfInt1[0] = arrayOfInt1[0] ^ arrayOfInt[0];
    } 
    return arrayOfInt1;
  }
  
  private static int[] mult64(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    int[] arrayOfInt1 = new int[4];
    int i = paramArrayOfint1[0];
    int j = 0;
    if (paramArrayOfint1.length > 1)
      j = paramArrayOfint1[1]; 
    int k = paramArrayOfint2[0];
    int m = 0;
    if (paramArrayOfint2.length > 1)
      m = paramArrayOfint2[1]; 
    if (j != 0 || m != 0) {
      int[] arrayOfInt = mult32(j, m);
      arrayOfInt1[3] = arrayOfInt1[3] ^ arrayOfInt[1];
      arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt[0] ^ arrayOfInt[1];
      arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt[0];
    } 
    int[] arrayOfInt2 = mult32(i ^ j, k ^ m);
    arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt2[1];
    arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt2[0];
    int[] arrayOfInt3 = mult32(i, k);
    arrayOfInt1[2] = arrayOfInt1[2] ^ arrayOfInt3[1];
    arrayOfInt1[1] = arrayOfInt1[1] ^ arrayOfInt3[0] ^ arrayOfInt3[1];
    arrayOfInt1[0] = arrayOfInt1[0] ^ arrayOfInt3[0];
    return arrayOfInt1;
  }
  
  private static int[] mult32(int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[2];
    if (paramInt1 == 0 || paramInt2 == 0)
      return arrayOfInt; 
    long l1 = paramInt2;
    l1 &= 0xFFFFFFFFL;
    long l2 = 0L;
    for (byte b = 1; b <= 32; b++) {
      if ((paramInt1 & bitMask[b - 1]) != 0)
        l2 ^= l1; 
      l1 <<= 1L;
    } 
    arrayOfInt[1] = (int)(l2 >>> 32L);
    arrayOfInt[0] = (int)(l2 & 0xFFFFFFFFL);
    return arrayOfInt;
  }
  
  private GF2Polynomial upper(int paramInt) {
    int i = Math.min(paramInt, this.blocks - paramInt);
    GF2Polynomial gF2Polynomial = new GF2Polynomial(i << 5);
    if (this.blocks >= paramInt)
      System.arraycopy(this.value, paramInt, gF2Polynomial.value, 0, i); 
    return gF2Polynomial;
  }
  
  private GF2Polynomial lower(int paramInt) {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(paramInt << 5);
    System.arraycopy(this.value, 0, gF2Polynomial.value, 0, Math.min(paramInt, this.blocks));
    return gF2Polynomial;
  }
  
  public GF2Polynomial remainder(GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this);
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(paramGF2Polynomial);
    if (gF2Polynomial2.isZero())
      throw new RuntimeException(); 
    gF2Polynomial1.reduceN();
    gF2Polynomial2.reduceN();
    if (gF2Polynomial1.len < gF2Polynomial2.len)
      return gF2Polynomial1; 
    int i;
    for (i = gF2Polynomial1.len - gF2Polynomial2.len; i >= 0; i = gF2Polynomial1.len - gF2Polynomial2.len) {
      GF2Polynomial gF2Polynomial = gF2Polynomial2.shiftLeft(i);
      gF2Polynomial1.subtractFromThis(gF2Polynomial);
      gF2Polynomial1.reduceN();
    } 
    return gF2Polynomial1;
  }
  
  public GF2Polynomial quotient(GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.len);
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this);
    GF2Polynomial gF2Polynomial3 = new GF2Polynomial(paramGF2Polynomial);
    if (gF2Polynomial3.isZero())
      throw new RuntimeException(); 
    gF2Polynomial2.reduceN();
    gF2Polynomial3.reduceN();
    if (gF2Polynomial2.len < gF2Polynomial3.len)
      return new GF2Polynomial(0); 
    int i = gF2Polynomial2.len - gF2Polynomial3.len;
    gF2Polynomial1.expandN(i + 1);
    while (i >= 0) {
      GF2Polynomial gF2Polynomial = gF2Polynomial3.shiftLeft(i);
      gF2Polynomial2.subtractFromThis(gF2Polynomial);
      gF2Polynomial2.reduceN();
      gF2Polynomial1.xorBit(i);
      i = gF2Polynomial2.len - gF2Polynomial3.len;
    } 
    return gF2Polynomial1;
  }
  
  public GF2Polynomial[] divide(GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    GF2Polynomial[] arrayOfGF2Polynomial = new GF2Polynomial[2];
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this.len);
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this);
    GF2Polynomial gF2Polynomial3 = new GF2Polynomial(paramGF2Polynomial);
    if (gF2Polynomial3.isZero())
      throw new RuntimeException(); 
    gF2Polynomial2.reduceN();
    gF2Polynomial3.reduceN();
    if (gF2Polynomial2.len < gF2Polynomial3.len) {
      arrayOfGF2Polynomial[0] = new GF2Polynomial(0);
      arrayOfGF2Polynomial[1] = gF2Polynomial2;
      return arrayOfGF2Polynomial;
    } 
    int i = gF2Polynomial2.len - gF2Polynomial3.len;
    gF2Polynomial1.expandN(i + 1);
    while (i >= 0) {
      GF2Polynomial gF2Polynomial = gF2Polynomial3.shiftLeft(i);
      gF2Polynomial2.subtractFromThis(gF2Polynomial);
      gF2Polynomial2.reduceN();
      gF2Polynomial1.xorBit(i);
      i = gF2Polynomial2.len - gF2Polynomial3.len;
    } 
    arrayOfGF2Polynomial[0] = gF2Polynomial1;
    arrayOfGF2Polynomial[1] = gF2Polynomial2;
    return arrayOfGF2Polynomial;
  }
  
  public GF2Polynomial gcd(GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    if (isZero() && paramGF2Polynomial.isZero())
      throw new ArithmeticException("Both operands of gcd equal zero."); 
    if (isZero())
      return new GF2Polynomial(paramGF2Polynomial); 
    if (paramGF2Polynomial.isZero())
      return new GF2Polynomial(this); 
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this);
    for (GF2Polynomial gF2Polynomial2 = new GF2Polynomial(paramGF2Polynomial); !gF2Polynomial2.isZero(); gF2Polynomial2 = gF2Polynomial) {
      GF2Polynomial gF2Polynomial = gF2Polynomial1.remainder(gF2Polynomial2);
      gF2Polynomial1 = gF2Polynomial2;
    } 
    return gF2Polynomial1;
  }
  
  public boolean isIrreducible() {
    if (isZero())
      return false; 
    GF2Polynomial gF2Polynomial1 = new GF2Polynomial(this);
    gF2Polynomial1.reduceN();
    int i = gF2Polynomial1.len - 1;
    GF2Polynomial gF2Polynomial2 = new GF2Polynomial(gF2Polynomial1.len, "X");
    for (byte b = 1; b <= i >> 1; b++) {
      gF2Polynomial2.squareThisPreCalc();
      gF2Polynomial2 = gF2Polynomial2.remainder(gF2Polynomial1);
      GF2Polynomial gF2Polynomial = gF2Polynomial2.add(new GF2Polynomial(32, "X"));
      if (!gF2Polynomial.isZero()) {
        GF2Polynomial gF2Polynomial3 = gF2Polynomial1.gcd(gF2Polynomial);
        if (!gF2Polynomial3.isOne())
          return false; 
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  void reduceTrinomial(int paramInt1, int paramInt2) {
    int j = paramInt1 >>> 5;
    int m = 32 - (paramInt1 & 0x1F);
    int k = paramInt1 - paramInt2 >>> 5;
    int n = 32 - (paramInt1 - paramInt2 & 0x1F);
    int i1 = (paramInt1 << 1) - 2 >>> 5;
    int i2 = j;
    for (int i = i1; i > i2; i--) {
      long l1 = this.value[i] & 0xFFFFFFFFL;
      this.value[i - j - 1] = this.value[i - j - 1] ^ (int)(l1 << m);
      this.value[i - j] = (int)(this.value[i - j] ^ l1 >>> 32 - m);
      this.value[i - k - 1] = this.value[i - k - 1] ^ (int)(l1 << n);
      this.value[i - k] = (int)(this.value[i - k] ^ l1 >>> 32 - n);
      this.value[i] = 0;
    } 
    long l = this.value[i2] & 0xFFFFFFFFL & 4294967295L << (paramInt1 & 0x1F);
    this.value[0] = (int)(this.value[0] ^ l >>> 32 - m);
    if (i2 - k - 1 >= 0)
      this.value[i2 - k - 1] = this.value[i2 - k - 1] ^ (int)(l << n); 
    this.value[i2 - k] = (int)(this.value[i2 - k] ^ l >>> 32 - n);
    this.value[i2] = this.value[i2] & reverseRightMask[paramInt1 & 0x1F];
    this.blocks = (paramInt1 - 1 >>> 5) + 1;
    this.len = paramInt1;
  }
  
  void reducePentanomial(int paramInt, int[] paramArrayOfint) {
    int j = paramInt >>> 5;
    int i1 = 32 - (paramInt & 0x1F);
    int k = paramInt - paramArrayOfint[0] >>> 5;
    int i2 = 32 - (paramInt - paramArrayOfint[0] & 0x1F);
    int m = paramInt - paramArrayOfint[1] >>> 5;
    int i3 = 32 - (paramInt - paramArrayOfint[1] & 0x1F);
    int n = paramInt - paramArrayOfint[2] >>> 5;
    int i4 = 32 - (paramInt - paramArrayOfint[2] & 0x1F);
    int i5 = (paramInt << 1) - 2 >>> 5;
    int i6 = j;
    for (int i = i5; i > i6; i--) {
      long l1 = this.value[i] & 0xFFFFFFFFL;
      this.value[i - j - 1] = this.value[i - j - 1] ^ (int)(l1 << i1);
      this.value[i - j] = (int)(this.value[i - j] ^ l1 >>> 32 - i1);
      this.value[i - k - 1] = this.value[i - k - 1] ^ (int)(l1 << i2);
      this.value[i - k] = (int)(this.value[i - k] ^ l1 >>> 32 - i2);
      this.value[i - m - 1] = this.value[i - m - 1] ^ (int)(l1 << i3);
      this.value[i - m] = (int)(this.value[i - m] ^ l1 >>> 32 - i3);
      this.value[i - n - 1] = this.value[i - n - 1] ^ (int)(l1 << i4);
      this.value[i - n] = (int)(this.value[i - n] ^ l1 >>> 32 - i4);
      this.value[i] = 0;
    } 
    long l = this.value[i6] & 0xFFFFFFFFL & 4294967295L << (paramInt & 0x1F);
    this.value[0] = (int)(this.value[0] ^ l >>> 32 - i1);
    if (i6 - k - 1 >= 0)
      this.value[i6 - k - 1] = this.value[i6 - k - 1] ^ (int)(l << i2); 
    this.value[i6 - k] = (int)(this.value[i6 - k] ^ l >>> 32 - i2);
    if (i6 - m - 1 >= 0)
      this.value[i6 - m - 1] = this.value[i6 - m - 1] ^ (int)(l << i3); 
    this.value[i6 - m] = (int)(this.value[i6 - m] ^ l >>> 32 - i3);
    if (i6 - n - 1 >= 0)
      this.value[i6 - n - 1] = this.value[i6 - n - 1] ^ (int)(l << i4); 
    this.value[i6 - n] = (int)(this.value[i6 - n] ^ l >>> 32 - i4);
    this.value[i6] = this.value[i6] & reverseRightMask[paramInt & 0x1F];
    this.blocks = (paramInt - 1 >>> 5) + 1;
    this.len = paramInt;
  }
  
  public void reduceN() {
    int i;
    for (i = this.blocks - 1; this.value[i] == 0 && i > 0; i--);
    int j = this.value[i];
    byte b;
    for (b = 0; j != 0; b++)
      j >>>= 1; 
    this.len = (i << 5) + b;
    this.blocks = i + 1;
  }
  
  public void expandN(int paramInt) {
    if (this.len >= paramInt)
      return; 
    this.len = paramInt;
    int i = (paramInt - 1 >>> 5) + 1;
    if (this.blocks >= i)
      return; 
    if (this.value.length >= i) {
      for (int j = this.blocks; j < i; j++)
        this.value[j] = 0; 
      this.blocks = i;
      return;
    } 
    int[] arrayOfInt = new int[i];
    System.arraycopy(this.value, 0, arrayOfInt, 0, this.blocks);
    this.blocks = i;
    this.value = null;
    this.value = arrayOfInt;
  }
  
  public void squareThisBitwise() {
    if (isZero())
      return; 
    int[] arrayOfInt = new int[this.blocks << 1];
    for (int i = this.blocks - 1; i >= 0; i--) {
      int j = this.value[i];
      int k = 1;
      for (byte b = 0; b < 16; b++) {
        if ((j & 0x1) != 0)
          arrayOfInt[i << 1] = arrayOfInt[i << 1] | k; 
        if ((j & 0x10000) != 0)
          arrayOfInt[(i << 1) + 1] = arrayOfInt[(i << 1) + 1] | k; 
        k <<= 2;
        j >>>= 1;
      } 
    } 
    this.value = null;
    this.value = arrayOfInt;
    this.blocks = arrayOfInt.length;
    this.len = (this.len << 1) - 1;
  }
  
  public void squareThisPreCalc() {
    if (isZero())
      return; 
    if (this.value.length >= this.blocks << 1) {
      for (int i = this.blocks - 1; i >= 0; i--) {
        this.value[(i << 1) + 1] = squaringTable[(this.value[i] & 0xFF0000) >>> 16] | squaringTable[(this.value[i] & 0xFF000000) >>> 24] << 16;
        this.value[i << 1] = squaringTable[this.value[i] & 0xFF] | squaringTable[(this.value[i] & 0xFF00) >>> 8] << 16;
      } 
      this.blocks <<= 1;
      this.len = (this.len << 1) - 1;
    } else {
      int[] arrayOfInt = new int[this.blocks << 1];
      for (byte b = 0; b < this.blocks; b++) {
        arrayOfInt[b << 1] = squaringTable[this.value[b] & 0xFF] | squaringTable[(this.value[b] & 0xFF00) >>> 8] << 16;
        arrayOfInt[(b << 1) + 1] = squaringTable[(this.value[b] & 0xFF0000) >>> 16] | squaringTable[(this.value[b] & 0xFF000000) >>> 24] << 16;
      } 
      this.value = null;
      this.value = arrayOfInt;
      this.blocks <<= 1;
      this.len = (this.len << 1) - 1;
    } 
  }
  
  public boolean vectorMult(GF2Polynomial paramGF2Polynomial) throws RuntimeException {
    boolean bool = false;
    if (this.len != paramGF2Polynomial.len)
      throw new RuntimeException(); 
    for (byte b = 0; b < this.blocks; b++) {
      int i = this.value[b] & paramGF2Polynomial.value[b];
      bool ^= parity[i & 0xFF];
      bool ^= parity[i >>> 8 & 0xFF];
      bool ^= parity[i >>> 16 & 0xFF];
      bool ^= parity[i >>> 24 & 0xFF];
    } 
    return bool;
  }
  
  public GF2Polynomial xor(GF2Polynomial paramGF2Polynomial) {
    GF2Polynomial gF2Polynomial;
    int i = Math.min(this.blocks, paramGF2Polynomial.blocks);
    if (this.len >= paramGF2Polynomial.len) {
      gF2Polynomial = new GF2Polynomial(this);
      for (byte b = 0; b < i; b++)
        gF2Polynomial.value[b] = gF2Polynomial.value[b] ^ paramGF2Polynomial.value[b]; 
    } else {
      gF2Polynomial = new GF2Polynomial(paramGF2Polynomial);
      for (byte b = 0; b < i; b++)
        gF2Polynomial.value[b] = gF2Polynomial.value[b] ^ this.value[b]; 
    } 
    gF2Polynomial.zeroUnusedBits();
    return gF2Polynomial;
  }
  
  public void xorThisBy(GF2Polynomial paramGF2Polynomial) {
    for (byte b = 0; b < Math.min(this.blocks, paramGF2Polynomial.blocks); b++)
      this.value[b] = this.value[b] ^ paramGF2Polynomial.value[b]; 
    zeroUnusedBits();
  }
  
  private void zeroUnusedBits() {
    if ((this.len & 0x1F) != 0)
      this.value[this.blocks - 1] = this.value[this.blocks - 1] & reverseRightMask[this.len & 0x1F]; 
  }
  
  public void setBit(int paramInt) throws RuntimeException {
    if (paramInt < 0 || paramInt > this.len - 1)
      throw new RuntimeException(); 
    this.value[paramInt >>> 5] = this.value[paramInt >>> 5] | bitMask[paramInt & 0x1F];
  }
  
  public int getBit(int paramInt) {
    if (paramInt < 0)
      throw new RuntimeException(); 
    return (paramInt > this.len - 1) ? 0 : (((this.value[paramInt >>> 5] & bitMask[paramInt & 0x1F]) != 0) ? 1 : 0);
  }
  
  public void resetBit(int paramInt) throws RuntimeException {
    if (paramInt < 0)
      throw new RuntimeException(); 
    if (paramInt > this.len - 1)
      return; 
    this.value[paramInt >>> 5] = this.value[paramInt >>> 5] & (bitMask[paramInt & 0x1F] ^ 0xFFFFFFFF);
  }
  
  public void xorBit(int paramInt) throws RuntimeException {
    if (paramInt < 0 || paramInt > this.len - 1)
      throw new RuntimeException(); 
    this.value[paramInt >>> 5] = this.value[paramInt >>> 5] ^ bitMask[paramInt & 0x1F];
  }
  
  public boolean testBit(int paramInt) {
    if (paramInt < 0)
      throw new RuntimeException(); 
    return (paramInt > this.len - 1) ? false : (((this.value[paramInt >>> 5] & bitMask[paramInt & 0x1F]) != 0));
  }
  
  public GF2Polynomial shiftLeft() {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(this.len + 1, this.value);
    for (int i = gF2Polynomial.blocks - 1; i >= 1; i--) {
      gF2Polynomial.value[i] = gF2Polynomial.value[i] << 1;
      gF2Polynomial.value[i] = gF2Polynomial.value[i] | gF2Polynomial.value[i - 1] >>> 31;
    } 
    gF2Polynomial.value[0] = gF2Polynomial.value[0] << 1;
    return gF2Polynomial;
  }
  
  public void shiftLeftThis() {
    if ((this.len & 0x1F) == 0) {
      this.len++;
      this.blocks++;
      if (this.blocks > this.value.length) {
        int[] arrayOfInt = new int[this.blocks];
        System.arraycopy(this.value, 0, arrayOfInt, 0, this.value.length);
        this.value = null;
        this.value = arrayOfInt;
      } 
      for (int i = this.blocks - 1; i >= 1; i--) {
        this.value[i] = this.value[i] | this.value[i - 1] >>> 31;
        this.value[i - 1] = this.value[i - 1] << 1;
      } 
    } else {
      this.len++;
      for (int i = this.blocks - 1; i >= 1; i--) {
        this.value[i] = this.value[i] << 1;
        this.value[i] = this.value[i] | this.value[i - 1] >>> 31;
      } 
      this.value[0] = this.value[0] << 1;
    } 
  }
  
  public GF2Polynomial shiftLeft(int paramInt) {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(this.len + paramInt, this.value);
    if (paramInt >= 32)
      gF2Polynomial.doShiftBlocksLeft(paramInt >>> 5); 
    int i = paramInt & 0x1F;
    if (i != 0) {
      for (int j = gF2Polynomial.blocks - 1; j >= 1; j--) {
        gF2Polynomial.value[j] = gF2Polynomial.value[j] << i;
        gF2Polynomial.value[j] = gF2Polynomial.value[j] | gF2Polynomial.value[j - 1] >>> 32 - i;
      } 
      gF2Polynomial.value[0] = gF2Polynomial.value[0] << i;
    } 
    return gF2Polynomial;
  }
  
  public void shiftLeftAddThis(GF2Polynomial paramGF2Polynomial, int paramInt) {
    if (paramInt == 0) {
      addToThis(paramGF2Polynomial);
      return;
    } 
    expandN(paramGF2Polynomial.len + paramInt);
    int j = paramInt >>> 5;
    for (int i = paramGF2Polynomial.blocks - 1; i >= 0; i--) {
      if (i + j + 1 < this.blocks && (paramInt & 0x1F) != 0)
        this.value[i + j + 1] = this.value[i + j + 1] ^ paramGF2Polynomial.value[i] >>> 32 - (paramInt & 0x1F); 
      this.value[i + j] = this.value[i + j] ^ paramGF2Polynomial.value[i] << (paramInt & 0x1F);
    } 
  }
  
  void shiftBlocksLeft() {
    this.blocks++;
    this.len += 32;
    if (this.blocks <= this.value.length) {
      for (int i = this.blocks - 1; i >= 1; i--)
        this.value[i] = this.value[i - 1]; 
      this.value[0] = 0;
    } else {
      int[] arrayOfInt = new int[this.blocks];
      System.arraycopy(this.value, 0, arrayOfInt, 1, this.blocks - 1);
      this.value = null;
      this.value = arrayOfInt;
    } 
  }
  
  private void doShiftBlocksLeft(int paramInt) {
    if (this.blocks <= this.value.length) {
      int i;
      for (i = this.blocks - 1; i >= paramInt; i--)
        this.value[i] = this.value[i - paramInt]; 
      for (i = 0; i < paramInt; i++)
        this.value[i] = 0; 
    } else {
      int[] arrayOfInt = new int[this.blocks];
      System.arraycopy(this.value, 0, arrayOfInt, paramInt, this.blocks - paramInt);
      this.value = null;
      this.value = arrayOfInt;
    } 
  }
  
  public GF2Polynomial shiftRight() {
    GF2Polynomial gF2Polynomial = new GF2Polynomial(this.len - 1);
    System.arraycopy(this.value, 0, gF2Polynomial.value, 0, gF2Polynomial.blocks);
    for (byte b = 0; b <= gF2Polynomial.blocks - 2; b++) {
      gF2Polynomial.value[b] = gF2Polynomial.value[b] >>> 1;
      gF2Polynomial.value[b] = gF2Polynomial.value[b] | gF2Polynomial.value[b + 1] << 31;
    } 
    gF2Polynomial.value[gF2Polynomial.blocks - 1] = gF2Polynomial.value[gF2Polynomial.blocks - 1] >>> 1;
    if (gF2Polynomial.blocks < this.blocks)
      gF2Polynomial.value[gF2Polynomial.blocks - 1] = gF2Polynomial.value[gF2Polynomial.blocks - 1] | this.value[gF2Polynomial.blocks] << 31; 
    return gF2Polynomial;
  }
  
  public void shiftRightThis() {
    this.len--;
    this.blocks = (this.len - 1 >>> 5) + 1;
    for (byte b = 0; b <= this.blocks - 2; b++) {
      this.value[b] = this.value[b] >>> 1;
      this.value[b] = this.value[b] | this.value[b + 1] << 31;
    } 
    this.value[this.blocks - 1] = this.value[this.blocks - 1] >>> 1;
    if ((this.len & 0x1F) == 0)
      this.value[this.blocks - 1] = this.value[this.blocks - 1] | this.value[this.blocks] << 31; 
  }
}
