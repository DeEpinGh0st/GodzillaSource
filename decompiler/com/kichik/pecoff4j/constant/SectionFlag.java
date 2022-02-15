package com.kichik.pecoff4j.constant;

public interface SectionFlag {
  public static final int IMAGE_SCN_RESERVED_1 = 0;
  
  public static final int IMAGE_SCN_RESERVED_2 = 1;
  
  public static final int IMAGE_SCN_RESERVED_3 = 2;
  
  public static final int IMAGE_SCN_RESERVED_4 = 4;
  
  public static final int IMAGE_SCN_TYPE_NO_PAD = 8;
  
  public static final int IMAGE_SCN_RESERVED_5 = 16;
  
  public static final int IMAGE_SCN_CNT_CODE = 32;
  
  public static final int IMAGE_SCN_CNT_INITIALIZED_DATA = 64;
  
  public static final int IMAGE_SCN_CNT_UNINITIALIZED_DATA = 128;
  
  public static final int IMAGE_SCN_LNK_OTHER = 256;
  
  public static final int IMAGE_SCN_LNK_INFO = 512;
  
  public static final int IMAGE_SCN_RESERVED_6 = 1024;
  
  public static final int IMAGE_SCN_LNK_REMOVE = 2048;
  
  public static final int IMAGE_SCN_LNK_COMDAT = 4096;
  
  public static final int IMAGE_SCN_GPREL = 32768;
  
  public static final int IMAGE_SCN_MEM_PURGEABLE = 131072;
  
  public static final int IMAGE_SCN_MEM_16BIT = 131072;
  
  public static final int IMAGE_SCN_MEM_LOCKED = 262144;
  
  public static final int IMAGE_SCN_MEM_PRELOAD = 524288;
  
  public static final int IMAGE_SCN_ALIGN_1BYTES = 1048576;
  
  public static final int IMAGE_SCN_ALIGN_2BYTES = 2097152;
  
  public static final int IMAGE_SCN_ALIGN_4BYTES = 3145728;
  
  public static final int IMAGE_SCN_ALIGN_8BYTES = 4194304;
  
  public static final int IMAGE_SCN_ALIGN_16BYTES = 5242880;
  
  public static final int IMAGE_SCN_ALIGN_32BYTES = 6291456;
  
  public static final int IMAGE_SCN_ALIGN_64BYTES = 7340032;
  
  public static final int IMAGE_SCN_ALIGN_128BYTES = 8388608;
  
  public static final int IMAGE_SCN_ALIGN_256BYTES = 9437184;
  
  public static final int IMAGE_SCN_ALIGN_512BYTES = 10485760;
  
  public static final int IMAGE_SCN_ALIGN_1024BYTES = 11534336;
  
  public static final int IMAGE_SCN_ALIGN_2048BYTES = 12582912;
  
  public static final int IMAGE_SCN_ALIGN_4096BYTES = 13631488;
  
  public static final int IMAGE_SCN_ALIGN_8192BYTES = 14680064;
  
  public static final int IMAGE_SCN_LNK_NRELOC_OVFL = 16777216;
  
  public static final int IMAGE_SCN_MEM_DISCARDABLE = 33554432;
  
  public static final int IMAGE_SCN_MEM_NOT_CACHED = 67108864;
  
  public static final int IMAGE_SCN_MEM_NOT_PAGED = 134217728;
  
  public static final int IMAGE_SCN_MEM_SHARED = 268435456;
  
  public static final int IMAGE_SCN_MEM_EXECUTE = 536870912;
  
  public static final int IMAGE_SCN_MEM_READ = 1073741824;
  
  public static final int IMAGE_SCN_MEM_WRITE = -2147483648;
}
