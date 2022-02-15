package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;























public class ConstantAttribute
  extends AttributeInfo
{
  public static final String tag = "ConstantValue";
  
  ConstantAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }







  
  public ConstantAttribute(ConstPool cp, int index) {
    super(cp, "ConstantValue");
    byte[] bvalue = new byte[2];
    bvalue[0] = (byte)(index >>> 8);
    bvalue[1] = (byte)index;
    set(bvalue);
  }



  
  public int getConstantValue() {
    return ByteArray.readU16bit(get(), 0);
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    int index = getConstPool().copy(getConstantValue(), newCp, classnames);
    
    return new ConstantAttribute(newCp, index);
  }
}
