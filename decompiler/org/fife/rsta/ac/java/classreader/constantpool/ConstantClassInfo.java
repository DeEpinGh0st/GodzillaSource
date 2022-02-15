package org.fife.rsta.ac.java.classreader.constantpool;































public class ConstantClassInfo
  extends ConstantPoolInfo
{
  private int nameIndex;
  
  public ConstantClassInfo(int nameIndex) {
    super(7);
    this.nameIndex = nameIndex;
  }








  
  public int getNameIndex() {
    return this.nameIndex;
  }







  
  public String toString() {
    return "[ConstantClassInfo: nameIndex=" + 
      getNameIndex() + "]";
  }
}
