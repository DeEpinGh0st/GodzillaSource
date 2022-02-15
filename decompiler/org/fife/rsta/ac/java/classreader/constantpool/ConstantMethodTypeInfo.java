package org.fife.rsta.ac.java.classreader.constantpool;





















public class ConstantMethodTypeInfo
  extends ConstantPoolInfo
{
  private int descriptorIndex;
  
  public ConstantMethodTypeInfo(int descriptorIndex) {
    super(16);
    this.descriptorIndex = descriptorIndex;
  }

  
  public int getDescriptorIndex() {
    return this.descriptorIndex;
  }







  
  public String toString() {
    return "[ConstantMethodTypeInfo: bootstrapMethodAttrIndex=" + 
      getDescriptorIndex() + "]";
  }
}
