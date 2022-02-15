package org.fife.rsta.ac.java.classreader.constantpool;

























public class ConstantFieldrefInfo
  extends ConstantPoolInfo
{
  private int classIndex;
  private int nameAndTypeIndex;
  
  public ConstantFieldrefInfo(int classIndex, int nameAndTypeIndex) {
    super(9);
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  
  public int getClassIndex() {
    return this.classIndex;
  }

  
  public int getNameAndTypeIndex() {
    return this.nameAndTypeIndex;
  }







  
  public String toString() {
    return "[ConstantFieldrefInfo: classIndex=" + 
      getClassIndex() + "; nameAndTypeIndex=" + 
      getNameAndTypeIndex() + "]";
  }
}
