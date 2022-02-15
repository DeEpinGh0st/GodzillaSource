package org.fife.rsta.ac.java.classreader.constantpool;

























public abstract class ConstantPoolInfo
  implements ConstantTypes
{
  private int tag;
  
  public ConstantPoolInfo(int tag) {
    this.tag = tag;
  }






  
  public int getTag() {
    return this.tag;
  }
}
