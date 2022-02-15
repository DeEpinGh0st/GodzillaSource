package org.fife.rsta.ac.java.classreader.constantpool;
























public class ConstantDoubleInfo
  extends ConstantPoolInfo
{
  private int highBytes;
  private int lowBytes;
  
  public ConstantDoubleInfo(int highBytes, int lowBytes) {
    super(6);
    this.highBytes = highBytes;
    this.lowBytes = lowBytes;
  }






  
  public double getDoubleValue() {
    long bits = (this.highBytes << 32L) + this.lowBytes;
    return Double.longBitsToDouble(bits);
  }

  
  public int getHighBytes() {
    return this.highBytes;
  }

  
  public int getLowBytes() {
    return this.lowBytes;
  }







  
  public String toString() {
    return "[ConstantDoubleInfo: value=" + 
      getDoubleValue() + "]";
  }
}
