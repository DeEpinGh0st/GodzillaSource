package javassist.bytecode;




























public class AccessFlag
{
  public static final int PUBLIC = 1;
  public static final int PRIVATE = 2;
  public static final int PROTECTED = 4;
  public static final int STATIC = 8;
  public static final int FINAL = 16;
  public static final int SYNCHRONIZED = 32;
  public static final int VOLATILE = 64;
  public static final int BRIDGE = 64;
  public static final int TRANSIENT = 128;
  public static final int VARARGS = 128;
  public static final int NATIVE = 256;
  public static final int INTERFACE = 512;
  public static final int ABSTRACT = 1024;
  public static final int STRICT = 2048;
  public static final int SYNTHETIC = 4096;
  public static final int ANNOTATION = 8192;
  public static final int ENUM = 16384;
  public static final int MANDATED = 32768;
  public static final int SUPER = 32;
  public static final int MODULE = 32768;
  
  public static int setPublic(int accflags) {
    return accflags & 0xFFFFFFF9 | 0x1;
  }




  
  public static int setProtected(int accflags) {
    return accflags & 0xFFFFFFFC | 0x4;
  }




  
  public static int setPrivate(int accflags) {
    return accflags & 0xFFFFFFFA | 0x2;
  }



  
  public static int setPackage(int accflags) {
    return accflags & 0xFFFFFFF8;
  }



  
  public static boolean isPublic(int accflags) {
    return ((accflags & 0x1) != 0);
  }



  
  public static boolean isProtected(int accflags) {
    return ((accflags & 0x4) != 0);
  }



  
  public static boolean isPrivate(int accflags) {
    return ((accflags & 0x2) != 0);
  }




  
  public static boolean isPackage(int accflags) {
    return ((accflags & 0x7) == 0);
  }



  
  public static int clear(int accflags, int clearBit) {
    return accflags & (clearBit ^ 0xFFFFFFFF);
  }






  
  public static int of(int modifier) {
    return modifier;
  }






  
  public static int toModifier(int accflags) {
    return accflags;
  }
}
