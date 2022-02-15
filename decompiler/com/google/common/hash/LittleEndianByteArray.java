package com.google.common.hash;

import com.google.common.primitives.Longs;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;



























final class LittleEndianByteArray
{
  private static final LittleEndianBytes byteArray;
  
  static long load64(byte[] input, int offset) {
    assert input.length >= offset + 8;
    
    return byteArray.getLongLittleEndian(input, offset);
  }










  
  static long load64Safely(byte[] input, int offset, int length) {
    long result = 0L;



    
    int limit = Math.min(length, 8);
    for (int i = 0; i < limit; i++)
    {
      result |= (input[offset + i] & 0xFFL) << i * 8;
    }
    return result;
  }








  
  static void store64(byte[] sink, int offset, long value) {
    assert offset >= 0 && offset + 8 <= sink.length;
    
    byteArray.putLongLittleEndian(sink, offset, value);
  }








  
  static int load32(byte[] source, int offset) {
    return source[offset] & 0xFF | (source[offset + 1] & 0xFF) << 8 | (source[offset + 2] & 0xFF) << 16 | (source[offset + 3] & 0xFF) << 24;
  }








  
  static boolean usingUnsafe() {
    return byteArray instanceof UnsafeByteArray;
  }



  
  private static interface LittleEndianBytes
  {
    long getLongLittleEndian(byte[] param1ArrayOfbyte, int param1Int);



    
    void putLongLittleEndian(byte[] param1ArrayOfbyte, int param1Int, long param1Long);
  }



  
  private enum UnsafeByteArray
    implements LittleEndianBytes
  {
    UNSAFE_LITTLE_ENDIAN
    {
      public long getLongLittleEndian(byte[] array, int offset) {
        return UnsafeByteArray.theUnsafe.getLong(array, offset + UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET);
      }

      
      public void putLongLittleEndian(byte[] array, int offset, long value) {
        UnsafeByteArray.theUnsafe.putLong(array, offset + UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET, value);
      }
    },
    UNSAFE_BIG_ENDIAN
    {
      public long getLongLittleEndian(byte[] array, int offset) {
        long bigEndian = UnsafeByteArray.theUnsafe.getLong(array, offset + UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET);
        
        return Long.reverseBytes(bigEndian);
      }


      
      public void putLongLittleEndian(byte[] array, int offset, long value) {
        long littleEndianValue = Long.reverseBytes(value);
        UnsafeByteArray.theUnsafe.putLong(array, offset + UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET, littleEndianValue);
      }
    };








































    
    private static final Unsafe theUnsafe = getUnsafe();
    private static final int BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class); private static Unsafe getUnsafe() { try { return Unsafe.getUnsafe(); } catch (SecurityException securityException) { try { return AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                public Unsafe run() throws Exception { Class<Unsafe> k = Unsafe.class; for (Field f : k.getDeclaredFields()) { f.setAccessible(true); Object x = f.get((Object)null); if (k.isInstance(x))
                      return k.cast(x);  }  throw new NoSuchFieldError("the Unsafe"); }
              }); } catch (PrivilegedActionException e) { throw new RuntimeException("Could not initialize intrinsics", e.getCause()); }  }  } static { if (theUnsafe.arrayIndexScale(byte[].class) != 1)
        throw new AssertionError();  }
  
  }
  
  private enum JavaLittleEndianBytes
    implements LittleEndianBytes
  {
    INSTANCE
    {
      public long getLongLittleEndian(byte[] source, int offset) {
        return Longs.fromBytes(source[offset + 7], source[offset + 6], source[offset + 5], source[offset + 4], source[offset + 3], source[offset + 2], source[offset + 1], source[offset]);
      }









      
      public void putLongLittleEndian(byte[] sink, int offset, long value) {
        long mask = 255L;
        for (int i = 0; i < 8; mask <<= 8L, i++) {
          sink[offset + i] = (byte)(int)((value & mask) >> i * 8);
        }
      }
    };
  }
  
  static {
    LittleEndianBytes theGetter = JavaLittleEndianBytes.INSTANCE;










    
    try {
      String arch = System.getProperty("os.arch");
      if ("amd64".equals(arch))
      {
        theGetter = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? UnsafeByteArray.UNSAFE_LITTLE_ENDIAN : UnsafeByteArray.UNSAFE_BIG_ENDIAN;
      
      }
    }
    catch (Throwable throwable) {}

    
    byteArray = theGetter;
  }
}
