package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;




























public class LocalVariableAttribute
  extends AttributeInfo
{
  public static final String tag = "LocalVariableTable";
  public static final String typeTag = "LocalVariableTypeTable";
  
  public LocalVariableAttribute(ConstPool cp) {
    super(cp, "LocalVariableTable", new byte[2]);
    ByteArray.write16bit(0, this.info, 0);
  }











  
  @Deprecated
  public LocalVariableAttribute(ConstPool cp, String name) {
    super(cp, name, new byte[2]);
    ByteArray.write16bit(0, this.info, 0);
  }


  
  LocalVariableAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  LocalVariableAttribute(ConstPool cp, String name, byte[] i) {
    super(cp, name, i);
  }










  
  public void addEntry(int startPc, int length, int nameIndex, int descriptorIndex, int index) {
    int size = this.info.length;
    byte[] newInfo = new byte[size + 10];
    ByteArray.write16bit(tableLength() + 1, newInfo, 0);
    for (int i = 2; i < size; i++) {
      newInfo[i] = this.info[i];
    }
    ByteArray.write16bit(startPc, newInfo, size);
    ByteArray.write16bit(length, newInfo, size + 2);
    ByteArray.write16bit(nameIndex, newInfo, size + 4);
    ByteArray.write16bit(descriptorIndex, newInfo, size + 6);
    ByteArray.write16bit(index, newInfo, size + 8);
    this.info = newInfo;
  }

  
  void renameClass(String oldname, String newname) {
    ConstPool cp = getConstPool();
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      int pos = i * 10 + 2;
      int index = ByteArray.readU16bit(this.info, pos + 6);
      if (index != 0) {
        String desc = cp.getUtf8Info(index);
        desc = renameEntry(desc, oldname, newname);
        ByteArray.write16bit(cp.addUtf8Info(desc), this.info, pos + 6);
      } 
    } 
  }
  
  String renameEntry(String desc, String oldname, String newname) {
    return Descriptor.rename(desc, oldname, newname);
  }

  
  void renameClass(Map<String, String> classnames) {
    ConstPool cp = getConstPool();
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      int pos = i * 10 + 2;
      int index = ByteArray.readU16bit(this.info, pos + 6);
      if (index != 0) {
        String desc = cp.getUtf8Info(index);
        desc = renameEntry(desc, classnames);
        ByteArray.write16bit(cp.addUtf8Info(desc), this.info, pos + 6);
      } 
    } 
  }
  
  String renameEntry(String desc, Map<String, String> classnames) {
    return Descriptor.rename(desc, classnames);
  }







  
  public void shiftIndex(int lessThan, int delta) {
    int size = this.info.length;
    for (int i = 2; i < size; i += 10) {
      int org = ByteArray.readU16bit(this.info, i + 8);
      if (org >= lessThan) {
        ByteArray.write16bit(org + delta, this.info, i + 8);
      }
    } 
  }



  
  public int tableLength() {
    return ByteArray.readU16bit(this.info, 0);
  }







  
  public int startPc(int i) {
    return ByteArray.readU16bit(this.info, i * 10 + 2);
  }







  
  public int codeLength(int i) {
    return ByteArray.readU16bit(this.info, i * 10 + 4);
  }



  
  void shiftPc(int where, int gapLength, boolean exclusive) {
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      int pos = i * 10 + 2;
      int pc = ByteArray.readU16bit(this.info, pos);
      int len = ByteArray.readU16bit(this.info, pos + 2);


      
      if (pc > where || (exclusive && pc == where && pc != 0)) {
        ByteArray.write16bit(pc + gapLength, this.info, pos);
      } else if (pc + len > where || (exclusive && pc + len == where)) {
        ByteArray.write16bit(len + gapLength, this.info, pos + 2);
      } 
    } 
  }





  
  public int nameIndex(int i) {
    return ByteArray.readU16bit(this.info, i * 10 + 6);
  }






  
  public String variableName(int i) {
    return getConstPool().getUtf8Info(nameIndex(i));
  }







  
  public String variableNameByIndex(int index) {
    for (int i = 0; i < tableLength(); i++) {
      if (index(i) == index) {
        return variableName(i);
      }
    } 
    throw new ArrayIndexOutOfBoundsException();
  }












  
  public int descriptorIndex(int i) {
    return ByteArray.readU16bit(this.info, i * 10 + 8);
  }










  
  public int signatureIndex(int i) {
    return descriptorIndex(i);
  }










  
  public String descriptor(int i) {
    return getConstPool().getUtf8Info(descriptorIndex(i));
  }













  
  public String signature(int i) {
    return descriptor(i);
  }






  
  public int index(int i) {
    return ByteArray.readU16bit(this.info, i * 10 + 10);
  }







  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    byte[] src = get();
    byte[] dest = new byte[src.length];
    ConstPool cp = getConstPool();
    LocalVariableAttribute attr = makeThisAttr(newCp, dest);
    int n = ByteArray.readU16bit(src, 0);
    ByteArray.write16bit(n, dest, 0);
    int j = 2;
    for (int i = 0; i < n; i++) {
      int start = ByteArray.readU16bit(src, j);
      int len = ByteArray.readU16bit(src, j + 2);
      int name = ByteArray.readU16bit(src, j + 4);
      int type = ByteArray.readU16bit(src, j + 6);
      int index = ByteArray.readU16bit(src, j + 8);
      
      ByteArray.write16bit(start, dest, j);
      ByteArray.write16bit(len, dest, j + 2);
      if (name != 0) {
        name = cp.copy(name, newCp, null);
      }
      ByteArray.write16bit(name, dest, j + 4);
      
      if (type != 0) {
        String sig = cp.getUtf8Info(type);
        sig = Descriptor.rename(sig, classnames);
        type = newCp.addUtf8Info(sig);
      } 
      
      ByteArray.write16bit(type, dest, j + 6);
      ByteArray.write16bit(index, dest, j + 8);
      j += 10;
    } 
    
    return attr;
  }

  
  LocalVariableAttribute makeThisAttr(ConstPool cp, byte[] dest) {
    return new LocalVariableAttribute(cp, "LocalVariableTable", dest);
  }
}
