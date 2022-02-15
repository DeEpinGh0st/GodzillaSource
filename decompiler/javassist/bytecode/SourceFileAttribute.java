package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;























public class SourceFileAttribute
  extends AttributeInfo
{
  public static final String tag = "SourceFile";
  
  SourceFileAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }






  
  public SourceFileAttribute(ConstPool cp, String filename) {
    super(cp, "SourceFile");
    int index = cp.addUtf8Info(filename);
    byte[] bvalue = new byte[2];
    bvalue[0] = (byte)(index >>> 8);
    bvalue[1] = (byte)index;
    set(bvalue);
  }



  
  public String getFileName() {
    return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    return new SourceFileAttribute(newCp, getFileName());
  }
}
