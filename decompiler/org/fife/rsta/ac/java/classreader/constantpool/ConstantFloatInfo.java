package org.fife.rsta.ac.java.classreader.constantpool;























public class ConstantFloatInfo
  extends ConstantPoolInfo
{
  private int bytes;
  
  public ConstantFloatInfo(int bytes) {
    super(4);
    this.bytes = bytes;
  }

  
  public long getBytes() {
    return this.bytes;
  }






  
  public float getFloatValue() {
    return Float.intBitsToFloat(this.bytes);
  }







  
  public String toString() {
    return "[ConstantFloatInfo: value=" + 
      getFloatValue() + "]";
  }
}
