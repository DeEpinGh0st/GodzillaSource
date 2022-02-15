package org.fife.rsta.ac.java.classreader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.classreader.attributes.AttributeInfo;
import org.fife.rsta.ac.java.classreader.attributes.ConstantValue;





































public class FieldInfo
  extends MemberInfo
{
  private int nameIndex;
  private int descriptorIndex;
  private List<AttributeInfo> attributes;
  public static final String CONSTANT_VALUE = "ConstantValue";
  
  public FieldInfo(ClassFile cf, int accessFlags, int nameIndex, int descriptorIndex) {
    super(cf, accessFlags);
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
    this.attributes = new ArrayList<>(1);
  }






  
  public void addAttribute(AttributeInfo info) {
    this.attributes.add(info);
  }







  
  public AttributeInfo getAttribute(int index) {
    return this.attributes.get(index);
  }






  
  public int getAttributeCount() {
    return this.attributes.size();
  }

  
  public String getConstantValueAsString() {
    ConstantValue cv = getConstantValueAttributeInfo();
    return (cv == null) ? null : cv.getConstantValueAsString();
  }







  
  private ConstantValue getConstantValueAttributeInfo() {
    for (int i = 0; i < getAttributeCount(); i++) {
      AttributeInfo ai = this.attributes.get(i);
      if (ai instanceof ConstantValue) {
        return (ConstantValue)ai;
      }
    } 
    return null;
  }







  
  public String getDescriptor() {
    return this.cf.getUtf8ValueFromConstantPool(this.descriptorIndex);
  }





  
  public String getName() {
    return this.cf.getUtf8ValueFromConstantPool(this.nameIndex);
  }







  
  public int getNameIndex() {
    return this.nameIndex;
  }






  
  public String getTypeString(boolean qualified) {
    String clazz;
    StringBuilder sb = new StringBuilder();
    
    String descriptor = getDescriptor();
    int braceCount = descriptor.lastIndexOf('[') + 1;
    
    switch (descriptor.charAt(braceCount)) {

      
      case 'B':
        sb.append("byte");
        break;
      case 'C':
        sb.append("char");
        break;
      case 'D':
        sb.append("double");
        break;
      case 'F':
        sb.append("float");
        break;
      case 'I':
        sb.append("int");
        break;
      case 'J':
        sb.append("long");
        break;
      case 'S':
        sb.append("short");
        break;
      case 'Z':
        sb.append("boolean");
        break;

      
      case 'L':
        clazz = descriptor.substring(braceCount + 1, descriptor
            .length() - 1);
        if (qualified) {
          clazz = clazz.replace('/', '.');
        } else {
          
          clazz = clazz.substring(clazz.lastIndexOf('/') + 1);
        } 
        sb.append(clazz);
        break;

      
      default:
        sb.append("UNSUPPORTED_TYPE_").append(descriptor);
        break;
    } 

    
    for (int i = 0; i < braceCount; i++) {
      sb.append("[]");
    }
    
    return sb.toString();
  }


  
  public boolean isConstant() {
    return (getConstantValueAttributeInfo() != null);
  }













  
  public static FieldInfo read(ClassFile cf, DataInputStream in) throws IOException {
    FieldInfo info = new FieldInfo(cf, in.readUnsignedShort(), in.readUnsignedShort(), in.readUnsignedShort());
    int attrCount = in.readUnsignedShort();
    for (int i = 0; i < attrCount; i++) {
      AttributeInfo ai = info.readAttribute(in);
      if (ai != null) {
        info.addAttribute(ai);
      }
    } 
    return info;
  }











  
  private AttributeInfo readAttribute(DataInputStream in) throws IOException {
    AttributeInfo ai;
    int attributeNameIndex = in.readUnsignedShort();
    int attributeLength = in.readInt();
    
    String attrName = this.cf.getUtf8ValueFromConstantPool(attributeNameIndex);
    
    if ("ConstantValue".equals(attrName)) {
      int constantValueIndex = in.readUnsignedShort();
      ConstantValue cv = new ConstantValue(this.cf, constantValueIndex);
      ConstantValue constantValue1 = cv;
    
    }
    else {
      
      ai = readAttribute(in, attrName, attributeLength);
    } 
    
    return ai;
  }
}
