package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;























public class InnerClassesAttribute
  extends AttributeInfo
{
  public static final String tag = "InnerClasses";
  
  InnerClassesAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  private InnerClassesAttribute(ConstPool cp, byte[] info) {
    super(cp, "InnerClasses", info);
  }





  
  public InnerClassesAttribute(ConstPool cp) {
    super(cp, "InnerClasses", new byte[2]);
    ByteArray.write16bit(0, get(), 0);
  }


  
  public int tableLength() {
    return ByteArray.readU16bit(get(), 0);
  }


  
  public int innerClassIndex(int nth) {
    return ByteArray.readU16bit(get(), nth * 8 + 2);
  }








  
  public String innerClass(int nth) {
    int i = innerClassIndex(nth);
    if (i == 0)
      return null; 
    return this.constPool.getClassInfo(i);
  }




  
  public void setInnerClassIndex(int nth, int index) {
    ByteArray.write16bit(index, get(), nth * 8 + 2);
  }



  
  public int outerClassIndex(int nth) {
    return ByteArray.readU16bit(get(), nth * 8 + 4);
  }






  
  public String outerClass(int nth) {
    int i = outerClassIndex(nth);
    if (i == 0)
      return null; 
    return this.constPool.getClassInfo(i);
  }




  
  public void setOuterClassIndex(int nth, int index) {
    ByteArray.write16bit(index, get(), nth * 8 + 4);
  }



  
  public int innerNameIndex(int nth) {
    return ByteArray.readU16bit(get(), nth * 8 + 6);
  }






  
  public String innerName(int nth) {
    int i = innerNameIndex(nth);
    if (i == 0)
      return null; 
    return this.constPool.getUtf8Info(i);
  }




  
  public void setInnerNameIndex(int nth, int index) {
    ByteArray.write16bit(index, get(), nth * 8 + 6);
  }



  
  public int accessFlags(int nth) {
    return ByteArray.readU16bit(get(), nth * 8 + 8);
  }




  
  public void setAccessFlags(int nth, int flags) {
    ByteArray.write16bit(flags, get(), nth * 8 + 8);
  }







  
  public int find(String name) {
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      if (name.equals(innerClass(i)))
        return i; 
    } 
    return -1;
  }








  
  public void append(String inner, String outer, String name, int flags) {
    int i = this.constPool.addClassInfo(inner);
    int o = this.constPool.addClassInfo(outer);
    int n = this.constPool.addUtf8Info(name);
    append(i, o, n, flags);
  }








  
  public void append(int inner, int outer, int name, int flags) {
    byte[] data = get();
    int len = data.length;
    byte[] newData = new byte[len + 8];
    for (int i = 2; i < len; i++) {
      newData[i] = data[i];
    }
    int n = ByteArray.readU16bit(data, 0);
    ByteArray.write16bit(n + 1, newData, 0);
    
    ByteArray.write16bit(inner, newData, len);
    ByteArray.write16bit(outer, newData, len + 2);
    ByteArray.write16bit(name, newData, len + 4);
    ByteArray.write16bit(flags, newData, len + 6);
    
    set(newData);
  }










  
  public int remove(int nth) {
    byte[] data = get();
    int len = data.length;
    if (len < 10) {
      return 0;
    }
    int n = ByteArray.readU16bit(data, 0);
    int nthPos = 2 + nth * 8;
    if (n <= nth) {
      return n;
    }
    byte[] newData = new byte[len - 8];
    ByteArray.write16bit(n - 1, newData, 0);
    int i = 2, j = 2;
    while (i < len) {
      if (i == nthPos) {
        i += 8; continue;
      } 
      newData[j++] = data[i++];
    } 
    set(newData);
    return n - 1;
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    byte[] src = get();
    byte[] dest = new byte[src.length];
    ConstPool cp = getConstPool();
    InnerClassesAttribute attr = new InnerClassesAttribute(newCp, dest);
    int n = ByteArray.readU16bit(src, 0);
    ByteArray.write16bit(n, dest, 0);
    int j = 2;
    for (int i = 0; i < n; i++) {
      int innerClass = ByteArray.readU16bit(src, j);
      int outerClass = ByteArray.readU16bit(src, j + 2);
      int innerName = ByteArray.readU16bit(src, j + 4);
      int innerAccess = ByteArray.readU16bit(src, j + 6);
      
      if (innerClass != 0) {
        innerClass = cp.copy(innerClass, newCp, classnames);
      }
      ByteArray.write16bit(innerClass, dest, j);
      
      if (outerClass != 0) {
        outerClass = cp.copy(outerClass, newCp, classnames);
      }
      ByteArray.write16bit(outerClass, dest, j + 2);
      
      if (innerName != 0) {
        innerName = cp.copy(innerName, newCp, classnames);
      }
      ByteArray.write16bit(innerName, dest, j + 4);
      ByteArray.write16bit(innerAccess, dest, j + 6);
      j += 8;
    } 
    
    return attr;
  }
}
