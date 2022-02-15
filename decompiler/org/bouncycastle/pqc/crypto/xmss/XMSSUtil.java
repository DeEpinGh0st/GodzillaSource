package org.bouncycastle.pqc.crypto.xmss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class XMSSUtil {
  public static int log2(int paramInt) {
    byte b;
    for (b = 0; (paramInt >>= 1) != 0; b++);
    return b;
  }
  
  public static byte[] toBytesBigEndian(long paramLong, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    for (int i = paramInt - 1; i >= 0; i--) {
      arrayOfByte[i] = (byte)(int)paramLong;
      paramLong >>>= 8L;
    } 
    return arrayOfByte;
  }
  
  public static void longToBigEndian(long paramLong, byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("in == null"); 
    if (paramArrayOfbyte.length - paramInt < 8)
      throw new IllegalArgumentException("not enough space in array"); 
    paramArrayOfbyte[paramInt] = (byte)(int)(paramLong >> 56L & 0xFFL);
    paramArrayOfbyte[paramInt + 1] = (byte)(int)(paramLong >> 48L & 0xFFL);
    paramArrayOfbyte[paramInt + 2] = (byte)(int)(paramLong >> 40L & 0xFFL);
    paramArrayOfbyte[paramInt + 3] = (byte)(int)(paramLong >> 32L & 0xFFL);
    paramArrayOfbyte[paramInt + 4] = (byte)(int)(paramLong >> 24L & 0xFFL);
    paramArrayOfbyte[paramInt + 5] = (byte)(int)(paramLong >> 16L & 0xFFL);
    paramArrayOfbyte[paramInt + 6] = (byte)(int)(paramLong >> 8L & 0xFFL);
    paramArrayOfbyte[paramInt + 7] = (byte)(int)(paramLong & 0xFFL);
  }
  
  public static long bytesToXBigEndian(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("in == null"); 
    long l = 0L;
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
      l = l << 8L | (paramArrayOfbyte[i] & 0xFF); 
    return l;
  }
  
  public static byte[] cloneArray(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("in == null"); 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[b]; 
    return arrayOfByte;
  }
  
  public static byte[][] cloneArray(byte[][] paramArrayOfbyte) {
    if (hasNullPointer(paramArrayOfbyte))
      throw new NullPointerException("in has null pointers"); 
    byte[][] arrayOfByte = new byte[paramArrayOfbyte.length][];
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      arrayOfByte[b] = new byte[(paramArrayOfbyte[b]).length];
      for (byte b1 = 0; b1 < (paramArrayOfbyte[b]).length; b1++)
        arrayOfByte[b][b1] = paramArrayOfbyte[b][b1]; 
    } 
    return arrayOfByte;
  }
  
  public static boolean areEqual(byte[][] paramArrayOfbyte1, byte[][] paramArrayOfbyte2) {
    if (hasNullPointer(paramArrayOfbyte1) || hasNullPointer(paramArrayOfbyte2))
      throw new NullPointerException("a or b == null"); 
    for (byte b = 0; b < paramArrayOfbyte1.length; b++) {
      if (!Arrays.areEqual(paramArrayOfbyte1[b], paramArrayOfbyte2[b]))
        return false; 
    } 
    return true;
  }
  
  public static void dumpByteArray(byte[][] paramArrayOfbyte) {
    if (hasNullPointer(paramArrayOfbyte))
      throw new NullPointerException("x has null pointers"); 
    for (byte b = 0; b < paramArrayOfbyte.length; b++)
      System.out.println(Hex.toHexString(paramArrayOfbyte[b])); 
  }
  
  public static boolean hasNullPointer(byte[][] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return true; 
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      if (paramArrayOfbyte[b] == null)
        return true; 
    } 
    return false;
  }
  
  public static void copyBytesAtOffset(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    if (paramArrayOfbyte1 == null)
      throw new NullPointerException("dst == null"); 
    if (paramArrayOfbyte2 == null)
      throw new NullPointerException("src == null"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("offset hast to be >= 0"); 
    if (paramArrayOfbyte2.length + paramInt > paramArrayOfbyte1.length)
      throw new IllegalArgumentException("src length + offset must not be greater than size of destination"); 
    for (byte b = 0; b < paramArrayOfbyte2.length; b++)
      paramArrayOfbyte1[paramInt + b] = paramArrayOfbyte2[b]; 
  }
  
  public static byte[] extractBytesAtOffset(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("src == null"); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("offset hast to be >= 0"); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("length hast to be >= 0"); 
    if (paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new IllegalArgumentException("offset + length must not be greater then size of source array"); 
    byte[] arrayOfByte = new byte[paramInt2];
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = paramArrayOfbyte[paramInt1 + b]; 
    return arrayOfByte;
  }
  
  public static boolean isIndexValid(int paramInt, long paramLong) {
    if (paramLong < 0L)
      throw new IllegalStateException("index must not be negative"); 
    return (paramLong < 1L << paramInt);
  }
  
  public static int getDigestSize(Digest paramDigest) {
    if (paramDigest == null)
      throw new NullPointerException("digest == null"); 
    String str = paramDigest.getAlgorithmName();
    return str.equals("SHAKE128") ? 32 : (str.equals("SHAKE256") ? 64 : paramDigest.getDigestSize());
  }
  
  public static long getTreeIndex(long paramLong, int paramInt) {
    return paramLong >> paramInt;
  }
  
  public static int getLeafIndex(long paramLong, int paramInt) {
    return (int)(paramLong & (1L << paramInt) - 1L);
  }
  
  public static byte[] serialize(Object paramObject) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(paramObject);
    objectOutputStream.flush();
    return byteArrayOutputStream.toByteArray();
  }
  
  public static Object deserialize(byte[] paramArrayOfbyte) throws IOException, ClassNotFoundException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    return objectInputStream.readObject();
  }
  
  public static int calculateTau(int paramInt1, int paramInt2) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      if ((paramInt1 >> b2 & 0x1) == 0) {
        b1 = b2;
        break;
      } 
    } 
    return b1;
  }
  
  public static boolean isNewBDSInitNeeded(long paramLong, int paramInt1, int paramInt2) {
    return (paramLong == 0L) ? false : ((paramLong % (long)Math.pow((1 << paramInt1), (paramInt2 + 1)) == 0L));
  }
  
  public static boolean isNewAuthenticationPathNeeded(long paramLong, int paramInt1, int paramInt2) {
    return (paramLong == 0L) ? false : (((paramLong + 1L) % (long)Math.pow((1 << paramInt1), paramInt2) == 0L));
  }
}
