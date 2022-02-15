package org.bouncycastle.crypto.generators;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenBSDBCrypt {
  private static final byte[] encodingTable = new byte[] { 
      46, 47, 65, 66, 67, 68, 69, 70, 71, 72, 
      73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 
      83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 
      99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 
      109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 
      119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 
      54, 55, 56, 57 };
  
  private static final byte[] decodingTable = new byte[128];
  
  private static final String defaultVersion = "2y";
  
  private static final Set<String> allowedVersions = new HashSet<String>();
  
  private static String createBcryptString(String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    if (!allowedVersions.contains(paramString))
      throw new IllegalArgumentException("Version " + paramString + " is not accepted by this implementation."); 
    StringBuffer stringBuffer = new StringBuffer(60);
    stringBuffer.append('$');
    stringBuffer.append(paramString);
    stringBuffer.append('$');
    stringBuffer.append((paramInt < 10) ? ("0" + paramInt) : Integer.toString(paramInt));
    stringBuffer.append('$');
    stringBuffer.append(encodeData(paramArrayOfbyte2));
    byte[] arrayOfByte = BCrypt.generate(paramArrayOfbyte1, paramArrayOfbyte2, paramInt);
    stringBuffer.append(encodeData(arrayOfByte));
    return stringBuffer.toString();
  }
  
  public static String generate(char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt) {
    return generate("2y", paramArrayOfchar, paramArrayOfbyte, paramInt);
  }
  
  public static String generate(String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt) {
    if (!allowedVersions.contains(paramString))
      throw new IllegalArgumentException("Version " + paramString + " is not accepted by this implementation."); 
    if (paramArrayOfchar == null)
      throw new IllegalArgumentException("Password required."); 
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("Salt required."); 
    if (paramArrayOfbyte.length != 16)
      throw new DataLengthException("16 byte salt required: " + paramArrayOfbyte.length); 
    if (paramInt < 4 || paramInt > 31)
      throw new IllegalArgumentException("Invalid cost factor."); 
    byte[] arrayOfByte1 = Strings.toUTF8ByteArray(paramArrayOfchar);
    byte[] arrayOfByte2 = new byte[(arrayOfByte1.length >= 72) ? 72 : (arrayOfByte1.length + 1)];
    if (arrayOfByte2.length > arrayOfByte1.length) {
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    } else {
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte2.length);
    } 
    Arrays.fill(arrayOfByte1, (byte)0);
    String str = createBcryptString(paramString, arrayOfByte2, paramArrayOfbyte, paramInt);
    Arrays.fill(arrayOfByte2, (byte)0);
    return str;
  }
  
  public static boolean checkPassword(String paramString, char[] paramArrayOfchar) {
    if (paramString.length() != 60)
      throw new DataLengthException("Bcrypt String length: " + paramString.length() + ", 60 required."); 
    if (paramString.charAt(0) != '$' || paramString.charAt(3) != '$' || paramString.charAt(6) != '$')
      throw new IllegalArgumentException("Invalid Bcrypt String format."); 
    String str1 = paramString.substring(1, 3);
    if (!allowedVersions.contains(str1))
      throw new IllegalArgumentException("Bcrypt version '" + paramString.substring(1, 3) + "' is not supported by this implementation"); 
    int i = 0;
    try {
      i = Integer.parseInt(paramString.substring(4, 6));
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("Invalid cost factor: " + paramString.substring(4, 6));
    } 
    if (i < 4 || i > 31)
      throw new IllegalArgumentException("Invalid cost factor: " + i + ", 4 < cost < 31 expected."); 
    if (paramArrayOfchar == null)
      throw new IllegalArgumentException("Missing password."); 
    byte[] arrayOfByte = decodeSaltString(paramString.substring(paramString.lastIndexOf('$') + 1, paramString.length() - 31));
    String str2 = generate(str1, paramArrayOfchar, arrayOfByte, i);
    return paramString.equals(str2);
  }
  
  private static String encodeData(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 24 && paramArrayOfbyte.length != 16)
      throw new DataLengthException("Invalid length: " + paramArrayOfbyte.length + ", 24 for key or 16 for salt expected"); 
    boolean bool = false;
    if (paramArrayOfbyte.length == 16) {
      bool = true;
      byte[] arrayOfByte = new byte[18];
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramArrayOfbyte.length);
      paramArrayOfbyte = arrayOfByte;
    } else {
      paramArrayOfbyte[paramArrayOfbyte.length - 1] = 0;
    } 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int i = paramArrayOfbyte.length;
    for (byte b = 0; b < i; b += 3) {
      int j = paramArrayOfbyte[b] & 0xFF;
      int k = paramArrayOfbyte[b + 1] & 0xFF;
      int m = paramArrayOfbyte[b + 2] & 0xFF;
      byteArrayOutputStream.write(encodingTable[j >>> 2 & 0x3F]);
      byteArrayOutputStream.write(encodingTable[(j << 4 | k >>> 4) & 0x3F]);
      byteArrayOutputStream.write(encodingTable[(k << 2 | m >>> 6) & 0x3F]);
      byteArrayOutputStream.write(encodingTable[m & 0x3F]);
    } 
    String str = Strings.fromByteArray(byteArrayOutputStream.toByteArray());
    return (bool == true) ? str.substring(0, 22) : str.substring(0, str.length() - 1);
  }
  
  private static byte[] decodeSaltString(String paramString) {
    char[] arrayOfChar1 = paramString.toCharArray();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16);
    if (arrayOfChar1.length != 22)
      throw new DataLengthException("Invalid base64 salt length: " + arrayOfChar1.length + " , 22 required."); 
    for (byte b1 = 0; b1 < arrayOfChar1.length; b1++) {
      char c = arrayOfChar1[b1];
      if (c > 'z' || c < '.' || (c > '9' && c < 'A'))
        throw new IllegalArgumentException("Salt string contains invalid character: " + c); 
    } 
    char[] arrayOfChar2 = new char[24];
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, arrayOfChar1.length);
    arrayOfChar1 = arrayOfChar2;
    int i = arrayOfChar1.length;
    for (byte b2 = 0; b2 < i; b2 += 4) {
      byte b3 = decodingTable[arrayOfChar1[b2]];
      byte b4 = decodingTable[arrayOfChar1[b2 + 1]];
      byte b5 = decodingTable[arrayOfChar1[b2 + 2]];
      byte b6 = decodingTable[arrayOfChar1[b2 + 3]];
      byteArrayOutputStream.write(b3 << 2 | b4 >> 4);
      byteArrayOutputStream.write(b4 << 4 | b5 >> 2);
      byteArrayOutputStream.write(b5 << 6 | b6);
    } 
    null = byteArrayOutputStream.toByteArray();
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(null, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  static {
    allowedVersions.add("2a");
    allowedVersions.add("2y");
    allowedVersions.add("2b");
    byte b;
    for (b = 0; b < decodingTable.length; b++)
      decodingTable[b] = -1; 
    for (b = 0; b < encodingTable.length; b++)
      decodingTable[encodingTable[b]] = (byte)b; 
  }
}
