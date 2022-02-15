package org.fife.rsta.ac.java.classreader.constantpool;



























class ConstantInterfaceMethodrefInfo
  extends ConstantPoolInfo
{
  private int classIndex;
  private int nameAndTypeIndex;
  
  public ConstantInterfaceMethodrefInfo(int classIndex, int nameAndTypeIndex) {
    super(11);
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
    return "[ConstantInterfaceMethodrefInfo: classIndex=" + 
      getClassIndex() + "; nameAndTypeIndex=" + 
      getNameAndTypeIndex() + "]";
  }
}
