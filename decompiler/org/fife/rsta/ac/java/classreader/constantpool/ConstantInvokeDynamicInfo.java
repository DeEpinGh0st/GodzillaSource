package org.fife.rsta.ac.java.classreader.constantpool;
























public class ConstantInvokeDynamicInfo
  extends ConstantPoolInfo
{
  private int bootstrapMethodAttrIndex;
  private int nameAndTypeIndex;
  
  public ConstantInvokeDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    super(18);
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  
  public int getBootstrapMethodAttrIndex() {
    return this.bootstrapMethodAttrIndex;
  }

  
  public int getNameAndTypeIndex() {
    return this.nameAndTypeIndex;
  }







  
  public String toString() {
    return "[ConstantInvokeDynamicInfo: bootstrapMethodAttrIndex=" + 
      getBootstrapMethodAttrIndex() + "; nameAndTypeIndex=" + 
      getNameAndTypeIndex() + "]";
  }
}
