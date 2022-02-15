package util;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Mac;

public class functions {
  private static final char[] toBase64 = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };


  
  private static final char[] toBase64URL = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };

  
  private static final double TOOLSKIT_WIDTH = 1920.0D;

  
  private static final double TOOLSKIT_HEIGHT = 1080.0D;
  
  private static double CURRENT_WIDTH = 1920.0D;
  private static double CURRENT_HEIGHT = 1080.0D;
  
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  static {
    double _CURRENT_WIDTH = (Toolkit.getDefaultToolkit().getScreenSize()).width;
    double _CURRENT_HEIGHT = (Toolkit.getDefaultToolkit().getScreenSize()).height;
    if (_CURRENT_HEIGHT > 1080.0D && _CURRENT_WIDTH > 1920.0D) {
      CURRENT_WIDTH = _CURRENT_WIDTH;
      CURRENT_HEIGHT = _CURRENT_HEIGHT;
    } 
  }


  
  public static String getNetworSpeedk(long size) {
    if (size < 1024L) {
      return String.valueOf(size) + "B";
    }
    size /= 1024L;



    
    if (size < 1024L) {
      return String.valueOf(size) + "KB";
    }
    size /= 1024L;
    
    if (size < 1024L) {

      
      size *= 100L;
      return String.valueOf(size / 100L) + "." + 
        String.valueOf(size % 100L) + "MB";
    } 
    
    size = size * 100L / 1024L;
    return String.valueOf(size / 100L) + "." + 
      String.valueOf(size % 100L) + "GB";
  }

  
  public static void concatMap(Map<String, List<String>> receiveMap, Map<String, List<String>> map) {
    Iterator<String> iterator = map.keySet().iterator();
    String key = null;
    while (iterator.hasNext()) {
      key = iterator.next();
      receiveMap.put(key, map.get(key));
    } 
  }
  public static boolean isMatch(String s, String p, boolean us) {
    if (us) {
      return isMatch(s, p);
    }
    return isMatch(s.toLowerCase(), p.toLowerCase());
  }


  
  public static String SHA(byte[] data, String strType) {
    String strResult = null;

    
    if (data != null && data.length > 0) {
      
      try {


        
        MessageDigest messageDigest = MessageDigest.getInstance(strType);
        
        messageDigest.update(data);
        
        byte[] byteBuffer = messageDigest.digest();

        
        StringBuffer strHexString = new StringBuffer();
        
        for (int i = 0; i < byteBuffer.length; i++) {
          
          String hex = Integer.toHexString(0xFF & byteBuffer[i]);
          if (hex.length() == 1)
          {
            strHexString.append('0');
          }
          strHexString.append(hex);
        } 
        
        strResult = strHexString.toString();
      }
      catch (NoSuchAlgorithmException e) {
        
        e.printStackTrace();
      } 
    }
    
    return strResult;
  }
  
  public static boolean isMatch(String s, String p) {
    int i = 0;
    int j = 0;
    int starIndex = -1;
    int iIndex = -1;
    while (i < s.length()) {
      if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
        i++;
        j++; continue;
      }  if (j < p.length() && p.charAt(j) == '*') {
        starIndex = j;
        iIndex = i;
        j++; continue;
      }  if (starIndex != -1) {
        j = starIndex + 1;
        i = iIndex + 1;
        iIndex++; continue;
      } 
      return false;
    } 
    
    while (j < p.length() && p.charAt(j) == '*') {
      j++;
    }
    return (j == p.length());
  }
  public static void setWindowSize(Window window, int width, int height) {
    window.setSize((int)(width / 1920.0D * CURRENT_WIDTH), (int)(height / 1080.0D * CURRENT_HEIGHT));
  }
  
  public static byte[] HMACSHA256(byte[] data, byte[] key) throws Exception {
    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
    sha256_HMAC.init(secret_key);
    byte[] array = sha256_HMAC.doFinal(data);
    return array;
  }
  public static void fireActionEventByJComboBox(JComboBox comboBox) {
    try {
      comboBox.setSelectedIndex(0);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  public static String readCString(ByteBuffer buff) {
    StringBuilder stringBuilder = new StringBuilder();
    byte c;
    while ((c = buff.get()) != 0) {
      stringBuilder.append((char)c);
    }
    return stringBuilder.toString();
  }
  
  public static byte[] ipToByteArray(String paramString) {
    String[] array2 = paramString.split("\\.");
    byte[] array = new byte[4];
    for (int i = 0; i < array2.length; i++) {
      array[i] = (byte)Integer.parseInt(array2[i]);
    }
    return array;
  }
  public static boolean isContainChinese(String str) {
    Pattern p = Pattern.compile("[一-龥]");
    Matcher m = p.matcher(str);
    if (m.find()) {
      return true;
    }
    return false;
  }
  public static byte[] shortToByteArray(short s) {
    byte[] targets = new byte[2];
    for (int i = 0; i < 2; i++) {
      int offset = (targets.length - 1 - i) * 8;
      targets[i] = (byte)(s >>> offset & 0xFF);
    } 
    return targets;
  }
  public static int random(int a, int b) {
    int temp = 0;
    if (b < 1 || a > b) {
      return 0;
    }
    if (a == b) {
      return a;
    }
    
    try {
      if (a > b) {
        temp = (new Random()).nextInt(a - b);
        return temp + b;
      } 
      temp = (new Random()).nextInt(b - a);
      return temp + a;
    }
    catch (Exception e) {
      Log.error(e);
      
      return temp + a;
    } 
  }
  public static String endTrim(String value) {
    int i = value.length();
    byte b = 0;
    char[] arrayOfChar = value.toCharArray();
    while (b < i && arrayOfChar[i - 1] <= ' ') {
      i--;
    }
    return (b > 0 || i < arrayOfChar.length) ? value.substring(b, i) : value;
  }
  public static String startTrim(String value) {
    int i = value.length();
    byte b = 0;
    char[] arrayOfChar = value.toCharArray();
    while (b < i && arrayOfChar[b] <= ' ') {
      b = (byte)(b + 1);
    }
    return (b > 0 || i < arrayOfChar.length) ? value.substring(b, i) : value;
  }
  public static byte[] intToBytes(int value) {
    byte[] src = new byte[4];
    src[0] = (byte)(value & 0xFF);
    src[1] = (byte)(value >> 8 & 0xFF);
    src[2] = (byte)(value >> 16 & 0xFF);
    src[3] = (byte)(value >> 24 & 0xFF);
    return src;
  }
  public static String getJarFileByClass(Class cs) {
    String fileString = null;
    
    if (cs != null) {
      String tmpString = cs.getProtectionDomain().getCodeSource().getLocation().getFile();
      if (tmpString.endsWith(".jar")) {
        try {
          fileString = URLDecoder.decode(tmpString, "utf-8");
        } catch (UnsupportedEncodingException e) {
          Log.error(e);
          fileString = URLDecoder.decode(tmpString);
        } 
      }
    } 
    return fileString;
  }
  public static String byteArrayToHexPrefix(byte[] bytes, String prefix) {
    String strHex = "";
    StringBuilder sb = new StringBuilder();
    for (int n = 0; n < bytes.length; n++) {
      strHex = Integer.toHexString(bytes[n] & 0xFF);
      sb.append(prefix);
      sb.append((strHex.length() == 1) ? ("0" + strHex) : strHex);
    } 
    return sb.toString().trim();
  }
  public static String byteArrayToHex(byte[] bytes) {
    return byteArrayToHexPrefix(bytes, "");
  }
  
  public static byte[] hexToByte(String hex) {
    int m = 0, n = 0;
    int byteLen = hex.length() / 2;
    byte[] ret = new byte[byteLen];
    for (int i = 0; i < byteLen; i++) {
      m = i * 2 + 1;
      n = m + 1;
      int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n)).intValue();
      ret[i] = Byte.valueOf((byte)intVal).byteValue();
    } 
    return ret;
  }
  
  public static boolean isGzipStream(byte[] data) {
    if (data != null && data.length >= 2) {
      int ss = data[0] & 0xFF | (data[1] & 0xFF) << 8;
      return (ss == 35615);
    } 
    return false;
  }

  
  public static Class loadClass(ClassLoader loader, String className) {
    try {
      return loader.loadClass(className);
    } catch (Exception e) {
      return null;
    } 
  }
  
  public static boolean appendFile(File file, byte[] content) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
      fileOutputStream.write(content);
      return true;
    } catch (Throwable e) {
      e.printStackTrace();
      
      return false;
    } 
  }
  public static String readFileBottomLine(File file, int number) {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      ArrayList<String> arrayList = new ArrayList();
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        arrayList.add(line);
      }
      
      if (arrayList.size() > number) {
        arrayList.subList(arrayList.size() - 1 - number, arrayList.size()).forEach(v -> {
              stringBuilder.append(v);
              
              stringBuilder.append('\n');
            });
      } else {
        arrayList.forEach(v -> {
              stringBuilder.append(v);
              stringBuilder.append('\n');
            });
      } 
    } catch (Exception exception) {}

    
    return stringBuilder.toString();
  }

  
  public static Object concatArrays(Object array1, int array1_Start, int array1_End, Object array2, int array2_Start, int array2_End) {
    if (array1.getClass().isArray() && array2.getClass().isArray()) {
      if (array1_Start >= 0 && array1_Start >= 0 && array2_End >= 0 && array2_Start >= 0) {
        int array1len = (array1_Start != array1_End) ? (array1_End - array1_Start + 1) : 0;
        int array2len = (array2_Start != array2_End) ? (array2_End - array2_Start + 1) : 0;
        int maxLen = array1len + array2len;
        byte[] data = new byte[maxLen];
        System.arraycopy(array1, array1_Start, data, 0, array1len);
        System.arraycopy(array2, array2_Start, data, array1len, array2len);
        
        return data;
      } 
      return null;
    } 
    
    return null;
  }
  
  public static boolean delFiles(File file) {
    boolean result = false;
    try {
      if (file.isDirectory()) {
        File[] childrenFiles = file.listFiles();
        for (File childFile : childrenFiles) {
          result = delFiles(childFile);
          if (!result) {
            return result;
          }
        } 
      } 
      result = file.delete();
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return result;
  }
  public static void addShutdownHook(final Class<?> cls, final Object object) {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
          {
            public void run()
            {
              try {
                cls.getMethod("Tclose", null).invoke(object, (Object[])null);
              } catch (Exception e) {
                
                e.printStackTrace();
              } 
            }
          }));
  }

  
  public static short bytesToShort(byte[] bytes) {
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
  }
  
  public static int stringToint(String intString) {
    return stringToint(intString, 0);
  }
  public static int stringToint(String intString, int defaultValue) {
    try {
      return Integer.parseInt(intString.trim());
    } catch (Exception e) {
      return defaultValue;
    } 
  }
  public static Long stringToLong(String intString, long defaultValue) {
    try {
      return Long.valueOf(Long.parseLong(intString.trim()));
    } catch (Exception e) {
      return Long.valueOf(defaultValue);
    } 
  }
  
  public static byte[] readInputStream(InputStream inputStream) {
    byte[] temp = new byte[5120];
    int readOneNum = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      while ((readOneNum = inputStream.read(temp)) != -1) {
        bos.write(temp, 0, readOneNum);
      }
    } catch (Exception e) {
      Log.error(e);
    } 
    return bos.toByteArray();
  }
  
  public static HashMap<String, String> matcherTwoChild(String data, String regex) {
    String rexString = regex;
    Pattern pattern = Pattern.compile(rexString);
    Matcher m = pattern.matcher(data);
    HashMap<String, String> hashMap = new HashMap<>();
    while (m.find()) {
      try {
        String v1 = m.group(1);
        String v2 = m.group(2);
        hashMap.put(v1, v2);
      } catch (Exception e) {
        Log.error(e);
      } 
    } 
    return hashMap;
  }

  
  public static short[] toShortArray(byte[] src) {
    int count = src.length >> 1;
    short[] dest = new short[count];
    for (int i = 0; i < count; i++) {
      dest[i] = (short)(src[i * 2] << 8 | src[2 * i + 1] & 0xFF);
    }
    return dest;
  }
  
  public static byte[] stringToByteArray(String data, String encodng) {
    try {
      return data.getBytes(encodng);
    } catch (Exception e) {
      return data.getBytes();
    } 
  }
  
  public static String formatDir(String dirString) {
    if (dirString != null && dirString.length() > 0) {
      dirString = dirString.trim();
      dirString = dirString.replaceAll("\\\\+", "/").replaceAll("/+", "/").trim();
      if (!dirString.substring(dirString.length() - 1).equals("/")) {
        dirString = dirString + "/";
      }
      return dirString;
    } 
    return "";
  }


  
  public static boolean filePutContent(String file, byte[] data) {
    return filePutContent(new File(file), data);
  }
  
  public static boolean filePutContent(File file, byte[] data) {
    boolean state = false;
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      outputStream.write(data);
      outputStream.flush();
      outputStream.close();
      state = true;
    } catch (Exception e) {
      Log.error(e);
      state = false;
    } 
    return state;
  }
  public static String getRandomString(int length) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    sb.append(str.charAt(random.nextInt(52)));
    str = str + "0123456789";
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(62);
      sb.append(str.charAt(number));
    } 
    return sb.toString();
  }
  public static String concatCookie(String oldCookie, String newCookie) {
    oldCookie = oldCookie + ";";
    newCookie = newCookie + ";";
    StringBuffer cookieBuffer = new StringBuffer();
    Map<String, String> cookieMap = new HashMap<>();
    String[] tmpA = oldCookie.split(";");
    int i;
    for (i = 0; i < tmpA.length; i++) {
      String[] temB = tmpA[i].split("=");
      cookieMap.put(temB[0], temB[1]);
    } 
    tmpA = newCookie.split(";");
    for (i = 0; i < tmpA.length; i++) {
      String[] temB = tmpA[i].split("=");
      cookieMap.put(temB[0], temB[1]);
    } 
    Iterator<String> iterator = cookieMap.keySet().iterator();
    
    while (iterator.hasNext()) {
      String keyString = iterator.next();
      cookieBuffer.append(keyString);
      cookieBuffer.append("=");
      cookieBuffer.append(cookieMap.get(keyString));
      cookieBuffer.append(";");
    } 
    return cookieBuffer.toString();
  }
  
  public static Method getMethodByClass(Class cs, String methodName, Class... parameters) {
    Method method = null;
    while (cs != null) {
      try {
        method = cs.getDeclaredMethod(methodName, parameters);
        method.setAccessible(true);
        cs = null;
      } catch (Exception e) {
        cs = cs.getSuperclass();
      } 
    } 
    return method;
  }
  public static Object getFieldValue(Object obj, String fieldName) throws Exception {
    Field f = null;
    if (obj instanceof Field) {
      f = (Field)obj;
    } else {
      Method method = null;
      Class<?> cs = obj.getClass();
      while (cs != null) {
        try {
          f = cs.getDeclaredField(fieldName);
          cs = null;
        } catch (Exception e) {
          cs = cs.getSuperclass();
        } 
      } 
    } 
    f.setAccessible(true);
    return f.get(obj);
  }
  public static Object invoke(Object obj, String methodName, Object... parameters) {
    try {
      ArrayList<Class<?>> classes = new ArrayList();
      if (parameters != null) {
        for (int i = 0; i < parameters.length; i++) {
          Object o1 = parameters[i];
          if (o1 != null) {
            classes.add(o1.getClass());
          } else {
            classes.add(null);
          } 
        } 
      }
      Method method = getMethodByClass(obj.getClass(), methodName, (Class[])classes.<Class<?>[]>toArray((Class<?>[][])new Class[0]));
      
      return method.invoke(obj, parameters);
    } catch (Exception exception) {
      
      return null;
    } 
  }
  public static String md5(String s) {
    return byteArrayToHex(md5(s.getBytes()));
  }
  public static byte[] readInputStreamAutoClose(InputStream inputStream) {
    byte[] ret = new byte[0];
    try {
      ret = readInputStream(inputStream);
      inputStream.close();
    } catch (IOException e) {
      Log.error(e);
      throw new RuntimeException(e);
    } 
    
    return ret;
  }
  public static byte[] md5(byte[] data) {
    byte[] ret = null;
    
    try {
      MessageDigest m = MessageDigest.getInstance("MD5");
      m.update(data, 0, data.length);
      ret = m.digest();
    } catch (NoSuchAlgorithmException e) {
      Log.error(e);
    } 
    return ret;
  }
  public static String getCurrentTime() {
    return DATE_FORMAT.format(new Date());
  }
  public static byte[] base64Encode(byte[] src) {
    int off = 0;
    int end = src.length;
    byte[] dst = new byte[4 * (src.length + 2) / 3];
    int linemax = -1;
    boolean doPadding = true;
    char[] base64 = toBase64;
    int sp = off;
    int slen = (end - off) / 3 * 3;
    int sl = off + slen;
    if (linemax > 0 && slen > linemax / 4 * 3) {
      slen = linemax / 4 * 3;
    }
    int dp = 0;
    while (sp < sl) {
      int sl0 = Math.min(sp + slen, sl);
      for (int sp0 = sp, dp0 = dp; sp0 < sl0; ) {
        int bits = (src[sp0++] & 0xFF) << 16 | (src[sp0++] & 0xFF) << 8 | src[sp0++] & 0xFF;
        dst[dp0++] = (byte)base64[bits >>> 18 & 0x3F];
        dst[dp0++] = (byte)base64[bits >>> 12 & 0x3F];
        dst[dp0++] = (byte)base64[bits >>> 6 & 0x3F];
        dst[dp0++] = (byte)base64[bits & 0x3F];
      } 
      int dlen = (sl0 - sp) / 3 * 4;
      dp += dlen;
      sp = sl0;
    } 
    if (sp < end) {
      int b0 = src[sp++] & 0xFF;
      dst[dp++] = (byte)base64[b0 >> 2];
      if (sp == end) {
        dst[dp++] = (byte)base64[b0 << 4 & 0x3F];
        if (doPadding) {
          dst[dp++] = 61;
          dst[dp++] = 61;
        } 
      } else {
        int b1 = src[sp++] & 0xFF;
        dst[dp++] = (byte)base64[b0 << 4 & 0x3F | b1 >> 4];
        dst[dp++] = (byte)base64[b1 << 2 & 0x3F];
        if (doPadding) {
          dst[dp++] = 61;
        }
      } 
    } 
    return dst;
  }
  
  public static String base64EncodeToString(byte[] bytes) {
    return new String(base64Encode(bytes));
  }
  public static String base64DecodeToString(String base64Str) {
    return new String(base64Decode(base64Str));
  }
  
  public static byte[] base64Decode(String base64Str) {
    if (base64Str == null || base64Str.isEmpty()) {
      return new byte[0];
    }
    base64Str = base64Str.replace("\r", "").replace("\n", "").replace("\\/", "/").replace("\\\\", "\\");
    byte[] src = base64Str.getBytes();
    if (src.length == 0) {
      return src;
    }
    int sp = 0;
    int sl = src.length;
    int paddings = 0;
    int len = sl - sp;
    if (src[sl - 1] == 61) {
      paddings++;
      if (src[sl - 2] == 61) {
        paddings++;
      }
    } 
    if (paddings == 0 && (len & 0x3) != 0) {
      paddings = 4 - (len & 0x3);
    }
    byte[] dst = new byte[3 * (len + 3) / 4 - paddings];
    int[] base64 = new int[256];
    Arrays.fill(base64, -1);
    for (int i = 0; i < toBase64.length; i++) {
      base64[toBase64[i]] = i;
    }
    base64[61] = -2;
    int dp = 0;
    int bits = 0;
    int shiftto = 18;
    while (sp < sl) {
      int b = src[sp++] & 0xFF;
      if ((b = base64[b]) < 0 && 
        b == -2) {
        if ((shiftto == 6 && (sp == sl || src[sp++] != 61)) || shiftto == 18) {
          throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
        }
        
        break;
      } 
      bits |= b << shiftto;
      shiftto -= 6;
      if (shiftto < 0) {
        dst[dp++] = (byte)(bits >> 16);
        dst[dp++] = (byte)(bits >> 8);
        dst[dp++] = (byte)bits;
        shiftto = 18;
        bits = 0;
      } 
    } 
    
    if (shiftto == 6) {
      dst[dp++] = (byte)(bits >> 16);
    } else if (shiftto == 0) {
      dst[dp++] = (byte)(bits >> 16);
      dst[dp++] = (byte)(bits >> 8);
    } else if (shiftto == 12) {
      
      throw new IllegalArgumentException("Last unit does not have enough valid bits");
    } 
    if (dp != dst.length) {
      byte[] arrayOfByte = new byte[dp];
      System.arraycopy(dst, 0, arrayOfByte, 0, Math.min(dst.length, dp));
      dst = arrayOfByte;
    } 
    return dst;
  }
  
  public static String subMiddleStr(String data, String leftStr, String rightStr) {
    int leftIndex = data.indexOf(leftStr);
    leftIndex += leftStr.length();
    int rightIndex = data.indexOf(rightStr, leftIndex);
    if (leftIndex != -1 && rightIndex != -1) {
      return data.substring(leftIndex, rightIndex);
    }
    return null;
  }



  
  public static byte[] getResourceAsByteArray(Class cl, String name) {
    InputStream inputStream = cl.getResourceAsStream(name);
    byte[] data = null;
    data = readInputStream(inputStream);
    try {
      inputStream.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    return data;
  }

  
  public static byte[] getResourceAsByteArray(Object o, String name) {
    return getResourceAsByteArray(o.getClass(), name);
  }
  
  public static boolean saveDataViewToCsv(Vector columnVector, Vector<Vector> dataRows, String saveFile) {
    boolean state = false;
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
      int columnNum = columnVector.size();
      byte cob = 44;
      byte newLine = 10;
      int rowNum = dataRows.size();

      
      StringBuilder builder = new StringBuilder(); int i;
      for (i = 0; i < columnNum - 1; i++) {
        Object object = columnVector.get(i);
        fileOutputStream.write(formatStringByCsv(object.toString()).getBytes());
        fileOutputStream.write(cob);
      } 
      Object valueObject = columnVector.get(columnNum - 1);
      fileOutputStream.write(formatStringByCsv(valueObject.toString()).getBytes());
      fileOutputStream.write(newLine);
      for (i = 0; i < rowNum; i++) {
        Vector row = dataRows.get(i);
        for (int j = 0; j < columnNum - 1; j++) {
          valueObject = row.get(j);
          fileOutputStream.write(formatStringByCsv(String.valueOf(valueObject)).getBytes());
          fileOutputStream.write(cob);
        } 
        valueObject = row.get(columnNum - 1);
        fileOutputStream.write(formatStringByCsv(String.valueOf(valueObject)).getBytes());
        fileOutputStream.write(newLine);
      } 
      fileOutputStream.close();
      state = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return state;
  }
  
  public static String stringToUnicode(String unicode) {
    char[] chars = unicode.toCharArray();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < chars.length; i++) {
      
      builder.append("\\u");
      String hx = Integer.toString(chars[i], 16);
      if (hx.length() < 4) {
        builder.append("0000".substring(hx.length())).append(hx);
      } else {
        builder.append(hx);
      } 
    } 
    return builder.toString();
  }
  
  public static String unicodeToString(String s) {
    char[] chars = s.toCharArray();
    StringBuilder stringBuilder = new StringBuilder();
    StringBuilder temBuilder = null;
    int index = 0;
    boolean isUn = false;
    char currentChar = '0';
    char nextChar = '0';
    char[] temChars = new char[4];
    
    String temStr = null;
    while (index < chars.length) {
      currentChar = chars[index];
      index++;
      if (currentChar == '\\') {
        temBuilder = new StringBuilder();
        temBuilder.append('\\');
        while (index + 1 < chars.length) {
          nextChar = chars[index];
          index++;
          if (nextChar == '\\') {
            index--;
            stringBuilder.append(temBuilder.toString());
            break;
          } 
          temBuilder.append(nextChar);
          if (nextChar == 'u') {
            isUn = true; continue;
          } 
          if (isUn) {
            if (index + 3 - 1 < chars.length) {
              temChars[0] = nextChar;
              temChars[1] = chars[index];
              index++;
              temChars[2] = chars[index];
              index++;
              temChars[3] = chars[index];
              index++;
              temStr = new String(temChars);
              temBuilder.append(temStr, 1, temChars.length);
              for (int i = 0; i < temChars.length; i++) {
                char fixChar = temChars[i];
                if ((fixChar >= '0' && fixChar <= '9') || (fixChar >= 'A' && fixChar <= 'F') || (fixChar >= 'a' && fixChar <= 'f')) {
                  isUn = true;
                } else {
                  isUn = false;
                  break;
                } 
              } 
              if (isUn) {
                stringBuilder.append((char)Integer.parseInt(new String(temChars), 16));
                isUn = false;
                continue;
              } 
              stringBuilder.append(temBuilder.toString());
              
              continue;
            } 
            isUn = false;
            stringBuilder.append(temBuilder.toString());
            
            continue;
          } 
          isUn = false;
          stringBuilder.append(temBuilder.toString());
        } 
        
        continue;
      } 
      stringBuilder.append(currentChar);
    } 
    
    return stringBuilder.toString();
  }
  
  public static boolean sleep(int time) {
    boolean state = false;
    try {
      Thread.sleep(time);
      state = true;
    } catch (InterruptedException e) {
      Log.error(e);
    } 
    return state;
  }
  
  public static String toString(Object object) {
    return (object == null) ? "null" : object.toString();
  }
  
  public static String getLastFileName(String file) {
    String[] fs = formatDir(file).split("/");
    return fs[fs.length - 1];
  }
  
  private static String formatStringByCsv(String string) {
    string = string.replace("\"", "\"\"");
    return "\"" + string + "\"";
  }

  
  public static int byteToInt2(byte[] b) {
    int mask = 255;
    int temp = 0;
    int n = 0;
    for (int i = 0; i < b.length; i++) {
      n <<= 8;
      temp = b[i] & mask;
      n |= temp;
    } 
    return n;
  }
  
  public static int bytesToInt(byte[] bytes) {
    int i = bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 24;
    
    return i;
  }

  
  public static byte[] gzipE(byte[] data) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      
      GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
      
      gzipOutputStream.write(data);
      
      gzipOutputStream.close();
      
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static byte[] gzipD(byte[] data) {
    if (data.length == 0) {
      return data;
    }
    try {
      ByteArrayInputStream tStream = new ByteArrayInputStream(data);
      GZIPInputStream inputStream = new GZIPInputStream(tStream, data.length);
      return readInputStream(inputStream);
    } catch (Exception e) {
      if (data.length < 200) {
        Log.error(new String(data));
      }
      throw new RuntimeException(e);
    } 
  }
  public static int randomInt(int max, int min) {
    return min + (int)(Math.random() * (max - min + 1));
  }
  public static void openBrowseUrl(String url) {
    if (Desktop.isDesktopSupported()) {
      
      try {
        URI uri = URI.create(url);
        
        Desktop dp = Desktop.getDesktop();
        
        if (dp.isSupported(Desktop.Action.BROWSE))
        {
          dp.browse(uri);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      } 
    }
  }
  
  public static String joinCmdArgs(String[] commands) {
    StringBuilder cmd = new StringBuilder();
    boolean flag = false;
    for (String s : commands) {
      if (flag) {
        cmd.append(' ');
      } else {
        flag = true;
      } 
      
      if (s.indexOf(' ') >= 0 || s.indexOf('\t') >= 0) {
        if (s.charAt(0) != '"') {
          cmd.append('"').append(s);
          
          if (s.endsWith("\\")) {
            cmd.append("\\");
          }
          cmd.append('"');
        } else {
          cmd.append(s);
        } 
      } else {
        cmd.append(s);
      } 
    } 
    
    return cmd.toString();
  }
  
  public static String[] SplitArgs(String input) {
    return SplitArgs(input, 2147483647, false);
  }
  
  public static String[] SplitArgs(String input, int maxParts, boolean removeAllEscapeSequences) {
    StringBuilder chars = new StringBuilder(input.trim());

    
    List<String> fragments = new ArrayList<>();
    
    int parts = 0;
    int nextFragmentStart = 0;
    boolean inBounds = false;
    
    for (int i = 0; i < chars.length(); i++) {
      
      char c = chars.charAt(i);
      if (c == '\\') {
        
        if (removeAllEscapeSequences || (i + 1 < chars.length() && isEscapeable(chars.charAt(i + 1))))
        {

          
          chars.deleteCharAt(i);

        
        }
      
      }
      else if (c == '"' && (!inBounds ? (i == nextFragmentStart) : (i + 1 == chars
        
        .length() || isSpace(chars.charAt(i + 1))))) {
        
        inBounds = !inBounds;
        chars.deleteCharAt(i);
        i--;

      
      }
      else if (!inBounds) {


        
        if (isSpace(c)) {
          
          AddFragment(fragments, chars, nextFragmentStart, i);
          nextFragmentStart = i + 1;

          
          if (++parts + 1 >= maxParts) {
            break;
          }
        } 
      } 
    } 
    if (nextFragmentStart < chars.length()) {
      AddFragment(fragments, chars, nextFragmentStart, -1);
    }
    
    return fragments.<String>toArray(new String[0]);
  }
  
  private static boolean isSpace(char c) {
    return (c == ' ' || c == '\t');
  }
  
  private static boolean isEscapeable(char c) {
    switch (c) {
    
    } 



    
    return false;
  }
  
  public static LinkedList<String> stringToIps(String str) {
    LinkedList<String> ips = new LinkedList<>();
    String[] strIps = str.split("\n");
    String[] array;
    for (int length = (array = strIps).length, i = 0; i < length; i++) {
      String stringa = array[i];
      String string = stringa.trim();
      if (isIPv4LiteralAddress(string)) {
        ips.add(string);
      }
      else if (string.lastIndexOf("-") != -1) {
        String[] iph = string.split("-");
        if (isIPv4LiteralAddress(iph[0])) {
          String x = iph[0];
          String[] ipx = x.split("\\.");
          for (Integer start = Integer.valueOf(Integer.parseInt(ipx[3])), end = Integer.valueOf(Integer.parseInt(iph[1])); start.intValue() <= end.intValue(); start = Integer.valueOf(start.intValue() + 1)) {
            String ip = String.valueOf(ipx[0]) + "." + ipx[1] + "." + ipx[2] + "." + start.toString();
            ips.add(ip);
          }
        
        } 
      } else if (string.lastIndexOf("/") != -1) {
        String[] iph = string.split("/");
        if (isIPv4LiteralAddress(iph[0])) {
          Integer mask = Integer.valueOf(Integer.parseInt(iph[1]));
          if (mask.intValue() <= 32 && mask.intValue() >= 1) {
            ips.addAll(maskToIps(iph[0], mask));
          }
        } else {
          
          try {
            String ip2 = InetAddress.getByName(iph[0]).getHostAddress();
            Integer mask2 = Integer.valueOf(Integer.parseInt(iph[1]));
            if (mask2.intValue() <= 32 && mask2.intValue() >= 1) {
              ips.addAll(maskToIps(ip2, mask2));
            }
          }
          catch (Exception e) {
            Log.error(e);
          }
        
        } 
      } else if (!string.equals("")) {
        ips.add(string);
      } 
    } 
    return ips;
  }
  
  public static LinkedList<String> maskToIps(String ip, Integer m) {
    LinkedList<String> i = new LinkedList<>();
    try {
      InetAddress inetAddress = InetAddress.getByName(ip);
      int address = inetAddress.hashCode();
      Integer n = Integer.valueOf(32 - m.intValue());
      int startIp = address & -1 << n.intValue();
      int endIp = address | -1 >>> m.intValue();
      startIp++;
      endIp--;
      while (startIp <= endIp) {
        byte[] startaddr = getAddress(startIp);
        InetAddress from = InetAddress.getByAddress(startaddr);
        String fromIp = from.getHostAddress();
        i.add(fromIp);
        startIp++;
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return i;
  }
  
  public static byte[] getAddress(int intIp) {
    int address = intIp;
    byte[] addr = { (byte)(address >>> 24 & 0xFF), (byte)(address >>> 16 & 0xFF), (byte)(address >>> 8 & 0xFF), (byte)(address & 0xFF) };
    return addr;
  }
  
  public static LinkedList<Integer> stringToPorts(String str) {
    String[] ports = str.split(",");
    HashSet<Integer> portset = new HashSet<>();
    String[] array;
    for (int length = (array = ports).length, i = 0; i < length; i++) {
      String stringa = array[i];
      String string = stringa.trim();
      if (string.lastIndexOf("-") != -1) {
        String[] strPorts = string.split("-");
        for (Integer startPort = Integer.valueOf(Integer.parseInt(strPorts[0])), endPort = Integer.valueOf(Integer.parseInt(strPorts[1])); startPort.intValue() <= endPort.intValue(); startPort = Integer.valueOf(startPort.intValue() + 1)) {
          if (startPort.intValue() >= 0 && startPort.intValue() <= 65535) {
            portset.add(startPort);
          }
        } 
      } else {
        
        try {
          Integer port = Integer.valueOf(Integer.parseInt(string));
          if (port.intValue() >= 0 && port.intValue() <= 65535) {
            portset.add(port);
          }
        }
        catch (Exception exception) {}
      } 
    } 
    LinkedList<Integer> portList = new LinkedList<>(portset);
    return portList;
  }
  
  public static byte[] textToNumericFormatV4(String src) {
    byte[] res = new byte[4];
    
    long tmpValue = 0L;
    int currByte = 0;
    boolean newOctet = true;
    
    int len = src.length();
    if (len == 0 || len > 15) {
      return null;
    }
    for (int i = 0; i < len; i++) {
      char c = src.charAt(i);
      if (c == '.') {
        if (newOctet || tmpValue < 0L || tmpValue > 255L || currByte == 3) {
          return null;
        }
        res[currByte++] = (byte)(int)(tmpValue & 0xFFL);
        tmpValue = 0L;
        newOctet = true;
      } else {
        int digit = Character.digit(c, 10);
        if (digit < 0) {
          return null;
        }
        tmpValue *= 10L;
        tmpValue += digit;
        newOctet = false;
      } 
    } 
    if (newOctet || tmpValue < 0L || tmpValue >= 1L << (4 - currByte) * 8) {
      return null;
    }
    switch (currByte) {
      case 0:
        res[0] = (byte)(int)(tmpValue >> 24L & 0xFFL);
      case 1:
        res[1] = (byte)(int)(tmpValue >> 16L & 0xFFL);
      case 2:
        res[2] = (byte)(int)(tmpValue >> 8L & 0xFFL);
      case 3:
        res[3] = (byte)(int)(tmpValue >> 0L & 0xFFL); break;
    } 
    return res;
  }
  public static boolean isIPv4LiteralAddress(String src) {
    return (textToNumericFormatV4(src) != null);
  }
  
  private static void AddFragment(List<String> fragments, StringBuilder chars, int start, int end) {
    if (end <= start && end >= 0) {
      return;
    }
    if (end < 0) {
      end = chars.length();
    }
    String fragment = chars.substring(start, end);
    fragments.add(fragment);
  }
  public static void dup2(InputStream inputStream, OutputStream outputStream) throws Exception {
    byte[] readData = new byte[5120];
    int readSize = -1;
    while ((readSize = inputStream.read(readData)) != -1) {
      outputStream.write(readData, 0, readSize);
      Thread.sleep(10L);
    } 
  }
  
  public static String printStackTrace(Throwable e) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(stream);
    e.printStackTrace(printStream);
    printStream.flush();
    printStream.close();
    return new String(stream.toByteArray());
  }
  public static File getCurrentJarFile() {
    String jarFileString = getJarFileByClass(ApplicationContext.class);
    if (jarFileString != null) {
      return new File(jarFileString);
    }
    return null;
  }
  
  public static byte[] httpReqest(String urlString, String method, HashMap<String, String> headers, byte[] data) {
    byte[] result = null;
    
    try {
      URL url = new URL(urlString);
      
      HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setDoOutput(!"GET".equals(method.toUpperCase()));
      httpConn.setConnectTimeout(3000);
      httpConn.setReadTimeout(3000);
      httpConn.setRequestMethod(method.toUpperCase());
      Http.addHttpHeader(httpConn, headers);
      if (httpConn.getDoOutput() && data != null) {
        httpConn.getOutputStream().write(data);
      }
      InputStream inputStream = httpConn.getInputStream();
      result = readInputStream(inputStream);
    } catch (Exception e) {
      Log.error(e);
    } 
    
    return result;
  }
}
