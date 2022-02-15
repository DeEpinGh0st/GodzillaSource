package org.fife.rsta.ac.java.classreader.constantpool;
























public class ConstantLongInfo
  extends ConstantPoolInfo
{
  private int highBytes;
  private int lowBytes;
  
  public ConstantLongInfo(int highBytes, int lowBytes) {
    super(5);
    this.highBytes = highBytes;
    this.lowBytes = lowBytes;
  }

  
  public int getHighBytes() {
    return this.highBytes;
  }

  
  public long getLongValue() {
    return (this.highBytes << 32L) + this.lowBytes;
  }

  
  public int getLowBytes() {
    return this.lowBytes;
  }







  
  public String toString() {
    return "[ConstantLongInfo: value=" + 
      getLongValue() + "]";
  }
}
