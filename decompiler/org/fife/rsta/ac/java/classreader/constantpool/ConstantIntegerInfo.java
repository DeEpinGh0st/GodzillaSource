package org.fife.rsta.ac.java.classreader.constantpool;





















public class ConstantIntegerInfo
  extends ConstantPoolInfo
{
  private long bytes;
  
  public ConstantIntegerInfo(long bytes) {
    super(8);
    this.bytes = bytes;
  }

  
  public long getBytes() {
    return this.bytes;
  }






  
  public int getIntValue() {
    return (int)this.bytes;
  }







  
  public String toString() {
    return "[ConstantIntegerInfo: bytes=" + 
      getBytes() + "]";
  }
}
