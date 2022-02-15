package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
























public class NestHostAttribute
  extends AttributeInfo
{
  public static final String tag = "NestHost";
  
  NestHostAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  private NestHostAttribute(ConstPool cp, int hostIndex) {
    super(cp, "NestHost", new byte[2]);
    ByteArray.write16bit(hostIndex, get(), 0);
  }









  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    int hostIndex = ByteArray.readU16bit(get(), 0);
    int newHostIndex = getConstPool().copy(hostIndex, newCp, classnames);
    return new NestHostAttribute(newCp, newHostIndex);
  }





  
  public int hostClassIndex() {
    return ByteArray.readU16bit(this.info, 0);
  }
}
