package org.fife.rsta.ac.java.classreader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.classreader.attributes.AttributeInfo;
import org.fife.rsta.ac.java.classreader.attributes.Code;
import org.fife.rsta.ac.java.classreader.attributes.Exceptions;
import org.fife.rsta.ac.java.classreader.attributes.Signature;



































































public class MethodInfo
  extends MemberInfo
  implements AccessFlags
{
  private int nameIndex;
  private int descriptorIndex;
  private Signature signatureAttr;
  private Code codeAttr;
  private List<AttributeInfo> attributes;
  private String[] paramTypes;
  private String returnType;
  private String nameAndParameters;
  private static final String SPECIAL_NAME_CONSTRUCTOR = "<init>";
  public static final String CODE = "Code";
  public static final String EXCEPTIONS = "Exceptions";
  
  public MethodInfo(ClassFile cf, int accessFlags, int nameIndex, int descriptorIndex) {
    super(cf, accessFlags);
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
    this.attributes = new ArrayList<>(1);
  }






  
  private void addAttribute(AttributeInfo info) {
    this.attributes.add(info);
  }


  
  private void appendParamDescriptors(StringBuilder sb) {
    String[] paramTypes = getParameterTypes();
    for (int i = 0; i < paramTypes.length; i++) {
      sb.append(paramTypes[i]).append(" param").append(i);
      if (i < paramTypes.length - 1) {
        sb.append(", ");
      }
    } 
  }













  
  void clearParamTypeInfo() {
    this.paramTypes = null;
    this.returnType = null;
  }












  
  private String[] createParamTypes() {
    String[] types = createParamTypesFromTypeSignature();
    if (types == null) {
      types = createParamTypesFromDescriptor(true);
    }
    return types;
  }










  
  private String[] createParamTypesFromDescriptor(boolean qualified) {
    String descriptor = getDescriptor();
    int rparen = descriptor.indexOf(')');
    String paramDescriptors = descriptor.substring(1, rparen);

    
    List<String> paramTypeList = new ArrayList<>();

    
    while (paramDescriptors.length() > 0) {
      String type, clazz, temp;


      
      int braceCount = -1;
      while (paramDescriptors.charAt(++braceCount) == '[');
      int pos = braceCount;
      
      switch (paramDescriptors.charAt(pos)) {

        
        case 'B':
          type = "byte";
          pos++;
          break;
        case 'C':
          type = "char";
          pos++;
          break;
        case 'D':
          type = "double";
          pos++;
          break;
        case 'F':
          type = "float";
          pos++;
          break;
        case 'I':
          type = "int";
          pos++;
          break;
        case 'J':
          type = "long";
          pos++;
          break;
        case 'S':
          type = "short";
          pos++;
          break;
        case 'Z':
          type = "boolean";
          pos++;
          break;

        
        case 'L':
          clazz = paramDescriptors.substring(pos + 1, paramDescriptors
              .indexOf(';'));
          type = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/') + 1);
          pos += clazz.length() + 2;
          break;

        
        default:
          temp = "INVALID_TYPE_" + paramDescriptors;
          type = temp;
          pos += paramDescriptors.length();
          break;
      } 

      
      for (int i = 0; i < braceCount; i++) {
        type = type + "[]";
      }
      paramTypeList.add(type);
      
      paramDescriptors = paramDescriptors.substring(pos);
    } 

    
    String[] types = new String[paramTypeList.size()];
    types = paramTypeList.<String>toArray(types);
    return types;
  }













  
  private String[] createParamTypesFromTypeSignature() {
    String[] params = null;
    
    if (this.signatureAttr != null) {
      
      List<String> paramTypes = this.signatureAttr.getMethodParamTypes(this, this.cf, false);
      if (paramTypes != null) {
        params = new String[paramTypes.size()];
        params = paramTypes.<String>toArray(params);
      } 
    } 
    
    return params;
  }








  
  public AttributeInfo getAttribute(int index) {
    return this.attributes.get(index);
  }






  
  public int getAttributeCount() {
    return this.attributes.size();
  }





  
  public String getDescriptor() {
    return this.cf.getUtf8ValueFromConstantPool(this.descriptorIndex);
  }





  
  public String getName() {
    String name = this.cf.getUtf8ValueFromConstantPool(this.nameIndex);
    if ("<init>".equals(name)) {
      name = this.cf.getClassName(false);
    }
    return name;
  }








  
  public String getNameAndParameters() {
    if (this.nameAndParameters == null) {
      
      StringBuilder sb = new StringBuilder(getName());
      
      sb.append('(');
      int paramCount = getParameterCount();
      for (int i = 0; i < paramCount; i++) {
        sb.append(getParameterType(i, false));
        if (i < paramCount - 1) {
          sb.append(", ");
        }
      } 
      sb.append(')');
      
      this.nameAndParameters = sb.toString();
    } 

    
    return this.nameAndParameters;
  }









  
  public int getParameterCount() {
    if (this.paramTypes == null) {
      this.paramTypes = createParamTypes();
    }
    return this.paramTypes.length;
  }









  
  public String getParameterName(int index) {
    if (index >= 0 && index < getParameterCount() && 
      this.codeAttr != null) {
      return this.codeAttr.getParameterName(index);
    }
    
    return null;
  }













  
  public String getParameterType(int index, boolean fullyQualified) {
    if (this.paramTypes == null) {
      this.paramTypes = createParamTypes();
    }
    String type = this.paramTypes[index];
    if (!fullyQualified) {
      int dot = type.lastIndexOf('.');
      if (dot > -1) {
        type = type.substring(dot + 1);
      }
    } 
    return type;
  }










  
  public String[] getParameterTypes() {
    if (this.paramTypes == null) {
      this.paramTypes = createParamTypes();
    }
    return (String[])this.paramTypes.clone();
  }





  
  public String getReturnTypeString(boolean fullyQualified) {
    if (this.returnType == null) {
      this.returnType = getReturnTypeStringFromTypeSignature(fullyQualified);
      if (this.returnType == null) {
        this.returnType = getReturnTypeStringFromDescriptor(fullyQualified);
      }
    } 
    if (!fullyQualified)
    {
      if (this.returnType != null && this.returnType.contains(".")) {
        return this.returnType.substring(this.returnType.lastIndexOf(".") + 1, this.returnType.length());
      }
    }
    return this.returnType;
  }
















  
  private String getReturnTypeStringFromDescriptor(boolean qualified) {
    String clazz, descriptor = getDescriptor();
    int rparen = descriptor.indexOf(')');
    descriptor = descriptor.substring(rparen + 1);
    StringBuilder sb = new StringBuilder();
    
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
      case 'V':
        sb.append("void");
        break;

      
      case 'L':
        clazz = descriptor.substring(braceCount + 1, descriptor.length() - 1);
        clazz = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/') + 1);
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










  
  private String getReturnTypeStringFromTypeSignature(boolean qualified) {
    String retType = null;
    if (this.signatureAttr != null) {
      retType = this.signatureAttr.getMethodReturnType(this, this.cf, qualified);
    }
    
    return retType;
  }









  
  public String getSignature() {
    StringBuilder sb = new StringBuilder();

    
    if (!isConstructor()) {
      sb.append(getReturnTypeString(false));
      sb.append(' ');
    } 

    
    sb.append(getName());
    sb.append('(');
    appendParamDescriptors(sb);
    sb.append(')');

    
    for (AttributeInfo ai : this.attributes) {
      if (ai instanceof Exceptions) {
        sb.append(" throws ");
        Exceptions ex = (Exceptions)ai;
        for (int j = 0; j < ex.getExceptionCount(); j++) {
          sb.append(ex.getException(j));
          if (j < ex.getExceptionCount() - 1) {
            sb.append(", ");
          }
        } 
      } 
    } 
    
    return sb.toString();
  }







  
  public boolean isAbstract() {
    return ((getAccessFlags() & 0x400) > 0);
  }






  
  public boolean isConstructor() {
    String name = this.cf.getUtf8ValueFromConstantPool(this.nameIndex);
    return "<init>".equals(name);
  }






  
  public boolean isNative() {
    return ((getAccessFlags() & 0x100) > 0);
  }







  
  public boolean isStatic() {
    return ((getAccessFlags() & 0x8) > 0);
  }










  
  public static MethodInfo read(ClassFile cf, DataInputStream in) throws IOException {
    int accessFlags = in.readUnsignedShort();
    int nameIndex = in.readUnsignedShort();
    int descriptorIndex = in.readUnsignedShort();
    MethodInfo mi = new MethodInfo(cf, accessFlags, nameIndex, descriptorIndex);
    
    int attrCount = in.readUnsignedShort();
    for (int j = 0; j < attrCount; j++) {
      AttributeInfo ai = mi.readAttribute(in);
      if (ai instanceof Signature) {
        mi.signatureAttr = (Signature)ai;
      }
      else if (ai instanceof Code) {
        mi.codeAttr = (Code)ai;
      }
      else if (ai != null) {
        mi.addAttribute(ai);
      } 
    } 
    return mi;
  }











  
  private AttributeInfo readAttribute(DataInputStream in) throws IOException {
    AttributeInfo ai;
    int attributeNameIndex = in.readUnsignedShort();
    int attributeLength = in.readInt();
    
    String attrName = this.cf.getUtf8ValueFromConstantPool(attributeNameIndex);
    
    if ("Code".equals(attrName)) {
      Code code = Code.read(this, in);
    
    }
    else if ("Exceptions".equals(attrName)) {
      int exceptionCount = in.readUnsignedShort();
      int[] exceptionIndexTable = null;
      if (exceptionCount > 0) {
        exceptionIndexTable = new int[exceptionCount];
        for (int i = 0; i < exceptionCount; i++) {
          exceptionIndexTable[i] = in.readUnsignedShort();
        }
      } 
      Exceptions e = new Exceptions(this, exceptionIndexTable);
      Exceptions exceptions1 = e;

    
    }
    else {

      
      ai = readAttribute(in, attrName, attributeLength);
    } 



    
    return ai;
  }
}
