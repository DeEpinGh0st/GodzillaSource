package org.bouncycastle.crypto.engines;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class GOST28147Engine implements BlockCipher {
  protected static final int BLOCK_SIZE = 8;
  
  private int[] workingKey = null;
  
  private boolean forEncryption;
  
  private byte[] S = Sbox_Default;
  
  private static byte[] Sbox_Default = new byte[] { 
      4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 
      1, 12, 7, 15, 5, 3, 14, 11, 4, 12, 
      6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 
      5, 9, 5, 8, 1, 13, 10, 3, 4, 2, 
      14, 15, 12, 7, 6, 0, 9, 11, 7, 13, 
      10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 
      11, 2, 5, 3, 6, 12, 7, 1, 5, 15, 
      13, 8, 4, 10, 9, 14, 0, 3, 11, 2, 
      4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 
      8, 5, 9, 12, 15, 14, 13, 11, 4, 1, 
      3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 
      2, 12, 1, 15, 13, 0, 5, 7, 10, 4, 
      9, 2, 3, 14, 6, 11, 8, 12 };
  
  private static byte[] ESbox_Test = new byte[] { 
      4, 2, 15, 5, 9, 1, 0, 8, 14, 3, 
      11, 12, 13, 7, 10, 6, 12, 9, 15, 14, 
      8, 1, 3, 10, 2, 7, 4, 13, 6, 0, 
      11, 5, 13, 8, 14, 12, 7, 3, 9, 10, 
      1, 5, 2, 4, 6, 15, 0, 11, 14, 9, 
      11, 2, 5, 15, 7, 1, 0, 13, 12, 6, 
      10, 4, 3, 8, 3, 14, 5, 9, 6, 8, 
      0, 13, 10, 11, 7, 12, 2, 1, 15, 4, 
      8, 15, 6, 11, 1, 9, 12, 5, 13, 3, 
      7, 10, 0, 14, 2, 4, 9, 11, 12, 0, 
      3, 6, 7, 5, 4, 8, 14, 15, 1, 10, 
      2, 13, 12, 6, 5, 2, 11, 0, 9, 13, 
      3, 14, 7, 10, 15, 4, 1, 8 };
  
  private static byte[] ESbox_A = new byte[] { 
      9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 
      14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 
      8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 
      13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 
      12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 
      10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 
      15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 
      15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 
      3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 
      9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 
      7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 
      11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 
      6, 2, 3, 9, 1, 7, 13, 4 };
  
  private static byte[] ESbox_B = new byte[] { 
      8, 4, 11, 1, 3, 5, 0, 9, 2, 14, 
      10, 12, 13, 6, 7, 15, 0, 1, 2, 10, 
      4, 13, 5, 12, 9, 7, 3, 15, 11, 8, 
      6, 14, 14, 12, 0, 10, 9, 2, 13, 11, 
      7, 5, 8, 15, 3, 6, 1, 4, 7, 5, 
      0, 13, 11, 6, 1, 2, 3, 10, 12, 15, 
      4, 14, 9, 8, 2, 7, 12, 15, 9, 5, 
      10, 11, 1, 4, 0, 13, 6, 8, 14, 3, 
      8, 3, 2, 6, 4, 13, 14, 11, 12, 1, 
      7, 15, 10, 0, 9, 5, 5, 2, 10, 11, 
      9, 1, 12, 3, 7, 4, 13, 0, 6, 15, 
      8, 14, 0, 4, 11, 14, 8, 3, 7, 1, 
      10, 2, 9, 6, 15, 13, 5, 12 };
  
  private static byte[] ESbox_C = new byte[] { 
      1, 11, 12, 2, 9, 13, 0, 15, 4, 5, 
      8, 14, 10, 7, 6, 3, 0, 1, 7, 13, 
      11, 4, 5, 2, 8, 14, 15, 12, 9, 10, 
      6, 3, 8, 2, 5, 0, 4, 9, 15, 10, 
      3, 7, 12, 13, 6, 14, 1, 11, 3, 6, 
      0, 1, 5, 13, 10, 8, 11, 2, 9, 7, 
      14, 15, 12, 4, 8, 13, 11, 0, 4, 5, 
      1, 2, 9, 3, 12, 14, 6, 15, 10, 7, 
      12, 9, 11, 1, 8, 14, 2, 4, 7, 3, 
      6, 5, 10, 0, 15, 13, 10, 9, 6, 8, 
      13, 14, 2, 0, 15, 3, 5, 11, 4, 1, 
      12, 7, 7, 4, 0, 5, 10, 2, 15, 14, 
      12, 6, 1, 11, 13, 9, 3, 8 };
  
  private static byte[] ESbox_D = new byte[] { 
      15, 12, 2, 10, 6, 4, 5, 0, 7, 9, 
      14, 13, 1, 11, 8, 3, 11, 6, 3, 4, 
      12, 15, 14, 2, 7, 13, 8, 0, 5, 10, 
      9, 1, 1, 12, 11, 0, 15, 14, 6, 5, 
      10, 13, 4, 8, 9, 3, 7, 2, 1, 5, 
      14, 12, 10, 7, 0, 13, 6, 2, 11, 4, 
      9, 3, 15, 8, 0, 12, 8, 9, 13, 2, 
      10, 11, 7, 3, 6, 5, 4, 14, 15, 1, 
      8, 0, 15, 3, 2, 5, 14, 11, 1, 10, 
      4, 7, 12, 9, 13, 6, 3, 0, 6, 15, 
      1, 14, 9, 2, 13, 8, 12, 4, 11, 10, 
      5, 7, 1, 10, 6, 8, 15, 11, 0, 4, 
      12, 3, 5, 9, 7, 13, 2, 14 };
  
  private static byte[] DSbox_Test = new byte[] { 
      4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 
      1, 12, 7, 15, 5, 3, 14, 11, 4, 12, 
      6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 
      5, 9, 5, 8, 1, 13, 10, 3, 4, 2, 
      14, 15, 12, 7, 6, 0, 9, 11, 7, 13, 
      10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 
      11, 2, 5, 3, 6, 12, 7, 1, 5, 15, 
      13, 8, 4, 10, 9, 14, 0, 3, 11, 2, 
      4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 
      8, 5, 9, 12, 15, 14, 13, 11, 4, 1, 
      3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 
      2, 12, 1, 15, 13, 0, 5, 7, 10, 4, 
      9, 2, 3, 14, 6, 11, 8, 12 };
  
  private static byte[] DSbox_A = new byte[] { 
      10, 4, 5, 6, 8, 1, 3, 7, 13, 12, 
      14, 0, 9, 2, 11, 15, 5, 15, 4, 0, 
      2, 13, 11, 9, 1, 7, 6, 3, 12, 14, 
      10, 8, 7, 15, 12, 14, 9, 4, 1, 0, 
      3, 11, 5, 2, 6, 10, 8, 13, 4, 10, 
      7, 12, 0, 15, 2, 8, 14, 1, 6, 5, 
      13, 11, 9, 3, 7, 6, 4, 11, 9, 12, 
      2, 10, 1, 8, 0, 14, 15, 13, 3, 5, 
      7, 6, 2, 4, 13, 9, 15, 0, 10, 1, 
      5, 11, 8, 14, 12, 3, 13, 14, 4, 1, 
      7, 0, 5, 10, 3, 12, 8, 15, 6, 2, 
      9, 11, 1, 3, 10, 9, 5, 11, 4, 15, 
      8, 6, 7, 14, 13, 0, 2, 12 };
  
  private static Hashtable sBoxes = new Hashtable<Object, Object>();
  
  private static void addSBox(String paramString, byte[] paramArrayOfbyte) {
    sBoxes.put(Strings.toUpperCase(paramString), paramArrayOfbyte);
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithSBox) {
      ParametersWithSBox parametersWithSBox = (ParametersWithSBox)paramCipherParameters;
      byte[] arrayOfByte = parametersWithSBox.getSBox();
      if (arrayOfByte.length != Sbox_Default.length)
        throw new IllegalArgumentException("invalid S-box passed to GOST28147 init"); 
      this.S = Arrays.clone(arrayOfByte);
      if (parametersWithSBox.getParameters() != null)
        this.workingKey = generateWorkingKey(paramBoolean, ((KeyParameter)parametersWithSBox.getParameters()).getKey()); 
    } else if (paramCipherParameters instanceof KeyParameter) {
      this.workingKey = generateWorkingKey(paramBoolean, ((KeyParameter)paramCipherParameters).getKey());
    } else if (paramCipherParameters != null) {
      throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + paramCipherParameters.getClass().getName());
    } 
  }
  
  public String getAlgorithmName() {
    return "GOST28147";
  }
  
  public int getBlockSize() {
    return 8;
  }
  
  public int processBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    if (this.workingKey == null)
      throw new IllegalStateException("GOST28147 engine not initialised"); 
    if (paramInt1 + 8 > paramArrayOfbyte1.length)
      throw new DataLengthException("input buffer too short"); 
    if (paramInt2 + 8 > paramArrayOfbyte2.length)
      throw new OutputLengthException("output buffer too short"); 
    GOST28147Func(this.workingKey, paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt2);
    return 8;
  }
  
  public void reset() {}
  
  private int[] generateWorkingKey(boolean paramBoolean, byte[] paramArrayOfbyte) {
    this.forEncryption = paramBoolean;
    if (paramArrayOfbyte.length != 32)
      throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!"); 
    int[] arrayOfInt = new int[8];
    for (byte b = 0; b != 8; b++)
      arrayOfInt[b] = bytesToint(paramArrayOfbyte, b * 4); 
    return arrayOfInt;
  }
  
  private int GOST28147_mainStep(int paramInt1, int paramInt2) {
    int i = paramInt2 + paramInt1;
    int j = this.S[0 + (i >> 0 & 0xF)] << 0;
    j += this.S[16 + (i >> 4 & 0xF)] << 4;
    j += this.S[32 + (i >> 8 & 0xF)] << 8;
    j += this.S[48 + (i >> 12 & 0xF)] << 12;
    j += this.S[64 + (i >> 16 & 0xF)] << 16;
    j += this.S[80 + (i >> 20 & 0xF)] << 20;
    j += this.S[96 + (i >> 24 & 0xF)] << 24;
    j += this.S[112 + (i >> 28 & 0xF)] << 28;
    return j << 11 | j >>> 21;
  }
  
  private void GOST28147Func(int[] paramArrayOfint, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    int i = bytesToint(paramArrayOfbyte1, paramInt1);
    int j = bytesToint(paramArrayOfbyte1, paramInt1 + 4);
    if (this.forEncryption) {
      byte b;
      for (b = 0; b < 3; b++) {
        for (byte b1 = 0; b1 < 8; b1++) {
          int k = i;
          i = j ^ GOST28147_mainStep(i, paramArrayOfint[b1]);
          j = k;
        } 
      } 
      for (b = 7; b > 0; b--) {
        int k = i;
        i = j ^ GOST28147_mainStep(i, paramArrayOfint[b]);
        j = k;
      } 
    } else {
      byte b;
      for (b = 0; b < 8; b++) {
        int k = i;
        i = j ^ GOST28147_mainStep(i, paramArrayOfint[b]);
        j = k;
      } 
      for (b = 0; b < 3; b++) {
        for (byte b1 = 7; b1 >= 0 && (b != 2 || b1 != 0); b1--) {
          int k = i;
          i = j ^ GOST28147_mainStep(i, paramArrayOfint[b1]);
          j = k;
        } 
      } 
    } 
    j ^= GOST28147_mainStep(i, paramArrayOfint[0]);
    intTobytes(i, paramArrayOfbyte2, paramInt2);
    intTobytes(j, paramArrayOfbyte2, paramInt2 + 4);
  }
  
  private int bytesToint(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt + 3] << 24 & 0xFF000000) + (paramArrayOfbyte[paramInt + 2] << 16 & 0xFF0000) + (paramArrayOfbyte[paramInt + 1] << 8 & 0xFF00) + (paramArrayOfbyte[paramInt] & 0xFF);
  }
  
  private void intTobytes(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2] = (byte)paramInt1;
  }
  
  public static byte[] getSBox(String paramString) {
    byte[] arrayOfByte = (byte[])sBoxes.get(Strings.toUpperCase(paramString));
    if (arrayOfByte == null)
      throw new IllegalArgumentException("Unknown S-Box - possible types: \"Default\", \"E-Test\", \"E-A\", \"E-B\", \"E-C\", \"E-D\", \"D-Test\", \"D-A\"."); 
    return Arrays.clone(arrayOfByte);
  }
  
  public static String getSBoxName(byte[] paramArrayOfbyte) {
    Enumeration<String> enumeration = sBoxes.keys();
    while (enumeration.hasMoreElements()) {
      String str = enumeration.nextElement();
      byte[] arrayOfByte = (byte[])sBoxes.get(str);
      if (Arrays.areEqual(arrayOfByte, paramArrayOfbyte))
        return str; 
    } 
    throw new IllegalArgumentException("SBOX provided did not map to a known one");
  }
  
  static {
    addSBox("Default", Sbox_Default);
    addSBox("E-TEST", ESbox_Test);
    addSBox("E-A", ESbox_A);
    addSBox("E-B", ESbox_B);
    addSBox("E-C", ESbox_C);
    addSBox("E-D", ESbox_D);
    addSBox("D-TEST", DSbox_Test);
    addSBox("D-A", DSbox_A);
  }
}
