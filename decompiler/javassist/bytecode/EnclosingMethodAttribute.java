package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;























public class EnclosingMethodAttribute
  extends AttributeInfo
{
  public static final String tag = "EnclosingMethod";
  
  EnclosingMethodAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }









  
  public EnclosingMethodAttribute(ConstPool cp, String className, String methodName, String methodDesc) {
    super(cp, "EnclosingMethod");
    int ci = cp.addClassInfo(className);
    int ni = cp.addNameAndTypeInfo(methodName, methodDesc);
    byte[] bvalue = new byte[4];
    bvalue[0] = (byte)(ci >>> 8);
    bvalue[1] = (byte)ci;
    bvalue[2] = (byte)(ni >>> 8);
    bvalue[3] = (byte)ni;
    set(bvalue);
  }







  
  public EnclosingMethodAttribute(ConstPool cp, String className) {
    super(cp, "EnclosingMethod");
    int ci = cp.addClassInfo(className);
    int ni = 0;
    byte[] bvalue = new byte[4];
    bvalue[0] = (byte)(ci >>> 8);
    bvalue[1] = (byte)ci;
    bvalue[2] = (byte)(ni >>> 8);
    bvalue[3] = (byte)ni;
    set(bvalue);
  }



  
  public int classIndex() {
    return ByteArray.readU16bit(get(), 0);
  }



  
  public int methodIndex() {
    return ByteArray.readU16bit(get(), 2);
  }



  
  public String className() {
    return getConstPool().getClassInfo(classIndex());
  }





  
  public String methodName() {
    ConstPool cp = getConstPool();
    int mi = methodIndex();
    if (mi == 0)
      return "<clinit>"; 
    int ni = cp.getNameAndTypeName(mi);
    return cp.getUtf8Info(ni);
  }



  
  public String methodDescriptor() {
    ConstPool cp = getConstPool();
    int mi = methodIndex();
    int ti = cp.getNameAndTypeDescriptor(mi);
    return cp.getUtf8Info(ti);
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    if (methodIndex() == 0)
      return new EnclosingMethodAttribute(newCp, className()); 
    return new EnclosingMethodAttribute(newCp, className(), 
        methodName(), methodDescriptor());
  }
}
