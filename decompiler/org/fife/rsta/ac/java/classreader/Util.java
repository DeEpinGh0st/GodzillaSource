package org.fife.rsta.ac.java.classreader;

import java.io.DataInputStream;
import java.io.IOException;

































public class Util
  implements AccessFlags
{
  public static boolean isDefault(int accessFlags) {
    int access = 7;
    return ((accessFlags & access) == 0);
  }









  
  public static boolean isPrivate(int accessFlags) {
    return ((accessFlags & 0x2) > 0);
  }









  
  public static boolean isProtected(int accessFlags) {
    return ((accessFlags & 0x4) > 0);
  }









  
  public static boolean isPublic(int accessFlags) {
    return ((accessFlags & 0x1) > 0);
  }









  
  public static void skipBytes(DataInputStream in, int count) throws IOException {
    int skipped = 0;
    while (skipped < count)
      skipped += in.skipBytes(count - skipped); 
  }
}
