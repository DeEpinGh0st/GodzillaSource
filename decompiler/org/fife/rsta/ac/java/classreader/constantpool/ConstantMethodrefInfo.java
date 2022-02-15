package org.fife.rsta.ac.java.classreader.constantpool;

























public class ConstantMethodrefInfo
  extends ConstantPoolInfo
{
  private int classIndex;
  private int nameAndTypeIndex;
  
  public ConstantMethodrefInfo(int classIndex, int nameAndTypeIndex) {
    super(10);
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
    return "[ConstantMethodrefInfo: classIndex=" + 
      getClassIndex() + "; nameAndTypeIndex=" + 
      getNameAndTypeIndex() + "]";
  }
}
