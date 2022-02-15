package org.fife.rsta.ac.java.classreader.attributes;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.ExceptionTableEntry;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.classreader.Util;








































































public class Code
  extends AttributeInfo
{
  private MethodInfo mi;
  private int maxStack;
  private int maxLocals;
  private int codeLength;
  private ExceptionTableEntry[] exceptionTable;
  private String[] paramNames;
  private List<AttributeInfo> attributes;
  private static final String LINE_NUMBER_TABLE = "LineNumberTable";
  private static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";
  private static final String LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable";
  private static final String STACK_MAP_TABLE = "StackMapTable";
  
  public Code(MethodInfo mi) {
    super(mi.getClassFile());
    this.mi = mi;
  }

















  
  public int getCodeLength() {
    return this.codeLength;
  }












  
  public int getMaxLocals() {
    return this.maxLocals;
  }





  
  public int getMaxStack() {
    return this.maxStack;
  }






  
  public MethodInfo getMethodInfo() {
    return this.mi;
  }









  
  public String getParameterName(int index) {
    return (this.paramNames == null) ? null : this.paramNames[index];
  }











  
  public static Code read(MethodInfo mi, DataInputStream in) throws IOException {
    Code code = new Code(mi);
    code.maxStack = in.readUnsignedShort();
    code.maxLocals = in.readUnsignedShort();
    code.codeLength = in.readInt();
    Util.skipBytes(in, code.codeLength);
    
    int exceptionTableLength = in.readUnsignedShort();
    if (exceptionTableLength > 0) {
      code.exceptionTable = new ExceptionTableEntry[exceptionTableLength];
      for (int i = 0; i < exceptionTableLength; i++) {
        ExceptionTableEntry ete = ExceptionTableEntry.read(mi
            .getClassFile(), in);
        code.exceptionTable[i] = ete;
      } 
    } 
    
    int attrCount = in.readUnsignedShort();
    if (attrCount > 0) {
      code.attributes = new ArrayList<>(1);
      for (int i = 0; i < attrCount; i++) {
        AttributeInfo ai = code.readAttribute(in);
        if (ai != null) {
          code.attributes.add(ai);
        }
      } 
    } 
    
    return code;
  }











  
  private AttributeInfo readAttribute(DataInputStream in) throws IOException {
    AttributeInfo ai = null;
    ClassFile cf = this.mi.getClassFile();
    
    int attributeNameIndex = in.readUnsignedShort();
    int attributeLength = in.readInt();
    
    String attrName = cf.getUtf8ValueFromConstantPool(attributeNameIndex);

    
    if ("LineNumberTable".equals(attrName)) {

      
      Util.skipBytes(in, attributeLength);



    
    }
    else if ("LocalVariableTable".equals(attrName)) {






      
      int paramCount = this.mi.getParameterCount();
      this.paramNames = new String[paramCount];
      boolean isStatic = this.mi.isStatic();
      
      int localVariableTableLength = in.readUnsignedShort();
      for (int i = 0; i < localVariableTableLength; i++)
      {
        in.readUnsignedShort();
        in.readUnsignedShort();
        int nameIndex = in.readUnsignedShort();
        in.readUnsignedShort();


        
        int index = in.readUnsignedShort();
        int adjustedIndex = isStatic ? index : (index - 1);
        
        if (adjustedIndex >= 0 && adjustedIndex < this.paramNames.length) {
          String name = cf.getUtf8ValueFromConstantPool(nameIndex);


          
          this.paramNames[adjustedIndex] = name;
        
        }

      
      }
    
    }
    else if ("LocalVariableTypeTable".equals(attrName)) {
      Util.skipBytes(in, attributeLength);

    
    }
    else if ("StackMapTable".equals(attrName)) {
      Util.skipBytes(in, attributeLength);
    }
    else {
      
      System.out.println("Unsupported Code attribute: " + attrName);
      ai = AttributeInfo.readUnsupportedAttribute(cf, in, attrName, attributeLength);
    } 

    
    return ai;
  }
}
