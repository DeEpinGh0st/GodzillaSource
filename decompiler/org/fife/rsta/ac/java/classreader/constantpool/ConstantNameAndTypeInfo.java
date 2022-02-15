package org.fife.rsta.ac.java.classreader.constantpool;
























public class ConstantNameAndTypeInfo
  extends ConstantPoolInfo
{
  private int nameIndex;
  private int descriptorIndex;
  
  public ConstantNameAndTypeInfo(int nameIndex, int descriptorIndex) {
    super(12);
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
  }

  
  public int getDescriptorIndex() {
    return this.descriptorIndex;
  }

  
  public int getNameIndex() {
    return this.nameIndex;
  }







  
  public String toString() {
    return "[ConstantNameAndTypeInfo: descriptorIndex=" + 
      getDescriptorIndex() + "; nameIndex=" + 
      getNameIndex() + "]";
  }
}
